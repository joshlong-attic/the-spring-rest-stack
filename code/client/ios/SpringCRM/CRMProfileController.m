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
//  CRMProfileController.m
//  SpringCRM
//
//  Created by Roy Clarkson on 9/7/10.
//

#import "CRMProfileController.h"
#import "CRMCoreDataManager.h"
#import "Profile.h"

@implementation CRMProfileController


#pragma mark -
#pragma mark Static methods

// Use this class method to obtain the shared instance of the class.
+ (id)sharedInstance
{
    static id _sharedInstance = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        _sharedInstance = [[self alloc] init];
    });
    return _sharedInstance;
}


#pragma mark -
#pragma mark Instance methods

- (Profile *)fetchProfile
{
    NSManagedObjectContext *context = [[CRMCoreDataManager sharedInstance] managedObjectContext];
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Profile" inManagedObjectContext:context];
    [fetchRequest setEntity:entity];
    NSError *error;
    NSArray *fetchedObjects = [context executeFetchRequest:fetchRequest error:&error];
    Profile *profile = nil;
    if (!error && fetchedObjects.count > 0)
    {
        profile = [fetchedObjects objectAtIndex:0];
    }
    return profile;
}

- (void)sendRequestForProfileWithDelegate:(id<CRMProfileControllerDelegate>)delegate
{
    NSURL *url = [CRMConnectionSettings urlWithFormat:@"/user"];
    NSMutableURLRequest *request = [[CRMAuthorizedRequest alloc] initWithURL:url];
	[request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
	DLog(@"%@", request);
	
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
    [NSURLConnection sendAsynchronousRequest:request
                                       queue:[NSOperationQueue mainQueue]
                           completionHandler:^(NSURLResponse *response, NSData *data, NSError *error)
     {
         NSInteger statusCode = [(NSHTTPURLResponse *)response statusCode];
         [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
         if (statusCode == 200 && data.length > 0 && error == nil)
         {
             DLog(@"%@", [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]);
             NSError *error;
             NSDictionary *dictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
             if (!error)
             {
                 DLog(@"%@", dictionary);
                 [self deleteProfile];
                 [self storeProfileWithJson:dictionary];
                 Profile *profile = [self fetchProfile];
                 [delegate fetchProfileDidFinishWithResults:profile];
             }
             else
             {
                 [delegate fetchProfileDidFailWithError:error];
             }
             
         }
         else if (error)
         {
             [self requestDidFailWithError:error];
             [delegate fetchProfileDidFailWithError:error];
         }
         else if (statusCode != 200)
         {
             [self requestDidNotSucceedWithDefaultMessage:@"A problem occurred while retrieving the profile data." response:response];
         }
     }];
}

- (void)storeProfileWithJson:(NSDictionary *)dictionary
{
    DLog(@"");
    NSManagedObjectContext *context = [[CRMCoreDataManager sharedInstance] managedObjectContext];
    Profile *profile = [NSEntityDescription
                        insertNewObjectForEntityForName:@"Profile"
                        inManagedObjectContext:context];
    profile.accountId = [dictionary objectForKey:@"id"];
    profile.firstName = [dictionary stringByReplacingPercentEscapesForKey:@"firstName" usingEncoding:NSUTF8StringEncoding];
    profile.lastName = [dictionary stringByReplacingPercentEscapesForKey:@"lastName" usingEncoding:NSUTF8StringEncoding];
    profile.hasImage = [dictionary objectForKey:@"profilePhotoImported"];
    NSArray *links = [dictionary objectForKey:@"links"];
    
    [links enumerateObjectsUsingBlock:^(NSDictionary *d, NSUInteger idx, BOOL *stop) {
        NSString *url = [d objectForKey:@"rel"];
        NSString *href = [d objectForKey:@"href"];
        if ([url isEqualToString:@"photo"] && [profile.hasImage boolValue] == YES) {
            profile.imageUrl = href;
            profile.imageMediaType = [dictionary stringByReplacingPercentEscapesForKey:@"profilePhotoMediaType"
                                                                         usingEncoding:NSUTF8StringEncoding];
        }
        else if ([url isEqualToString:@"customers"]) {
            profile.customersUrl = href;
        }
    }];
    
    NSError *error;
    [context save:&error];
    if (error)
    {
        DLog(@"%@", [error localizedDescription]);
    }
}

- (void)deleteProfile
{
    DLog(@"");
    NSManagedObjectContext *context = [[CRMCoreDataManager sharedInstance] managedObjectContext];
    Profile *profile = [self fetchProfile];
    if (profile)
    {
        [context deleteObject:profile];
        NSError *error;
        [context save:&error];
        if (error)
        {
            DLog(@"%@", [error localizedDescription]);
        }
    }
}

@end
