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
//  CRMAuthController.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/14/12.
//

#define KEYCHAIN_SERVICE_NAME    @"CRM"
#define KEYCHAIN_ACCOUNT_NAME    @"OAuth2"

#import "CRMAuthController.h"
#import "OA2AccessGrant.h"
#import "OA2SignInRequestParameters.h"
#import "CRMURLPostRequest.h"
#import "CRMKeychainManager.h"
#import "CRMConnectionSettings.h"

@implementation CRMAuthController


#pragma mark -
#pragma mark Static methods

// Use this class method to obtain the shared instance of the class.
+ (CRMAuthController *)sharedInstance
{
    static CRMAuthController *_sharedInstance = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        _sharedInstance = [[CRMAuthController alloc] init];
    });
    return _sharedInstance;
}

+ (BOOL)isAuthorized
{
    OA2AccessGrant *accessGrant = [self fetchAccessGrant];
    return (accessGrant != nil);
}

+ (BOOL)storeAccessGrant:(OA2AccessGrant *)accessGrant
{
	OSStatus status = [[CRMKeychainManager sharedInstance] storePassword:[accessGrant dataValue] service:KEYCHAIN_SERVICE_NAME account:KEYCHAIN_ACCOUNT_NAME];
	return (status == errSecSuccess);
}

+ (OA2AccessGrant *)fetchAccessGrant
{
    NSData *passwordData;
    OSStatus status = [[CRMKeychainManager sharedInstance] fetchPassword:&passwordData service:KEYCHAIN_SERVICE_NAME account:KEYCHAIN_ACCOUNT_NAME];
    if (status == errSecSuccess && passwordData)
    {
        NSError *error;
        OA2AccessGrant *accessGrant = [[OA2AccessGrant alloc] initWithData:passwordData error:&error];
        return accessGrant;
    }
    return nil;
}

+ (BOOL)deleteAccessGrant
{
	OSStatus status = [[CRMKeychainManager sharedInstance] deletePasswordWithService:KEYCHAIN_SERVICE_NAME account:KEYCHAIN_ACCOUNT_NAME];
	return (status == errSecSuccess);
}


#pragma mark -
#pragma mark Instance methods

- (void)sendRequestToSignIn:(NSString *)username password:(NSString *)password delegate:(id<CRMAuthControllerDelegate>)delegate
{
    OA2SignInRequestParameters *parameters = [[OA2SignInRequestParameters alloc] initWithUsername:username password:password];
    NSURL *url = [CRMConnectionSettings urlWithFormat:@"/oauth/token"];
    NSMutableURLRequest *request = [[CRMURLPostRequest alloc] initWithURL:url parameters:parameters];
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    DLog(@"%@", request);
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
    [NSURLConnection sendAsynchronousRequest:request
                                       queue:[NSOperationQueue mainQueue]
                           completionHandler:^(NSURLResponse *response, NSData *data, NSError *error)
     {
         [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];         
         NSInteger statusCode = [(NSHTTPURLResponse *)response statusCode];
         DLog(@"Http status: %d", statusCode);
         DLog(@"%@", [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]);
         if (statusCode == 200 && data.length > 0 && error == nil)
         {
             NSError *errorInternal;
             OA2AccessGrant *accessGrant = [[OA2AccessGrant alloc] initWithData:data error:&errorInternal];
             if (!errorInternal)
             {
                 [CRMAuthController storeAccessGrant:accessGrant];
             }
             else
             {
                 DLog(@"error parsing access grant: %@", [errorInternal localizedDescription]);
             }
             [delegate authRequestDidSucceed];
         }
         else if (error)
         {
             DLog(@"%d - %@", [error code], [error localizedDescription]);
             NSString *msg;
             switch ([error code]) {
                 case NSURLErrorUserCancelledAuthentication:
                     msg = @"Your username or password was entered incorrectly.";
                     break;
                 case NSURLErrorCannotConnectToHost:
                     msg = @"The server is unavailable. Please try again in a few minutes.";
                     break;
                 default:
                     msg = @"A problem occurred with the network connection. Please try again in a few minutes.";
                     break;
             }
             UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                             message:msg
                                                            delegate:nil
                                                   cancelButtonTitle:@"OK"
                                                   otherButtonTitles:nil];
             [alert show];
             [delegate authRequestDidFailWithError:error];
         }
         else if (statusCode != 200)
         {
             NSString *msg;
             switch (statusCode) {
                 default:
                     msg = @"You cannot be signed in.";
                     break;
             }
             UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:nil
                                                                 message:msg
                                                                delegate:nil
                                                       cancelButtonTitle:@"OK"
                                                       otherButtonTitles:nil];
             [alertView show];
             [delegate authRequestDidFailWithError:error];
         }
     }];
}

@end
