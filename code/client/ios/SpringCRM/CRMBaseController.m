//
//  Copyright 2013 the original author or authors.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//
//
//  CRMBaseController.m
//  SpringCRM
//
//  Created by Roy Clarkson on 9/16/10.
//

#import "CRMBaseController.h"

@implementation CRMBaseController


#pragma mark -
#pragma mark Instance methods

void ProcessError(NSString* action, NSError* error)
{    
    if (!error)
    {
        return;
    }
    
    DLog(@"Failed to %@: %@", action, [error localizedDescription]);
    NSArray* detailedErrors = [[error userInfo] objectForKey:NSDetailedErrorsKey];
    if(detailedErrors && [detailedErrors count] > 0)
    {
        for(NSError* detailedError in detailedErrors)
        {
            DLog(@"DetailedError: %@", [detailedError userInfo]);
        }
    }
    else
    {
        DLog(@"%@", [error userInfo]);
    }
}

- (void)requestDidNotSucceedWithDefaultMessage:(NSString *)message response:(NSURLResponse *)response
{
	NSInteger statusCode = [(NSHTTPURLResponse *)response statusCode];
	DLog(@"HTTP Status Code: %d", statusCode);
	
    NSString *msg = message;
    id delegate = nil;
    NSString *otherButtonTitle = nil;
    switch (statusCode) {
        case 401:
            msg = @"Your access token is not valid. Please reauthorize the app.";
            delegate = [[UIApplication sharedApplication] delegate];
            break;
        default:
            break;
    }

    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                    message:msg
                                                   delegate:delegate
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:otherButtonTitle, nil];
    [alert show];
}

- (void)requestDidFailWithError:(NSError *)error
{	
	DLog(@"%d - %@", [error code], [error localizedDescription]);
    NSString *message = nil;
    id delegate = nil;
    NSString *otherButtonTitle = nil;
    switch ([error code]) {
        case NSURLErrorUserCancelledAuthentication:
            message = @"Your access token is not valid. Please reauthorize the app.";
            delegate = [[UIApplication sharedApplication] delegate];
            break;
        case NSURLErrorTimedOut:
            message = @"The network request timed out. Please try again in a few minutes.";
            break;
        case NSURLErrorCannotConnectToHost:
            message = @"The server is unavailable. Please try again in a few minutes.";
            break;
        case kCFURLErrorNotConnectedToInternet:
            message = @"No internet connection";
            break;
        default:
            message = @"The network connection is not available. Please try again in a few minutes.";
            break;
    }
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                    message:message
                                                   delegate:delegate
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:otherButtonTitle, nil];
    [alert show];
}

@end
