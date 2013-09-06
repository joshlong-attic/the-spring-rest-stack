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
//  CRMConnectionSettings.m
//  SpringCRM
//
//  Created by Roy Clarkson on 9/18/12.
//

#pragma mark -
#pragma mark LOCALHOST

#if LOCALHOST

#define OAUTH_CLIENT_ID                     @"ios-crm"
#define OAUTH_CLIENT_SECRET                 @"123456"
#define CRM_URL                             @"http://localhost:8080"

#pragma mark -
#pragma mark QA

#elif QA

#define OAUTH_CLIENT_ID                     @"ios-crm"
#define OAUTH_CLIENT_SECRET                 @"123456"
#define CRM_URL                             @"http://localhost:8080"

#pragma mark -
#pragma mark PRODUCTION

#elif PRODUCTION

#define OAUTH_CLIENT_ID                     @"ios-crm"
#define OAUTH_CLIENT_SECRET                 @"123456"
#define CRM_URL                             @"http://localhost:8080"

#endif

#pragma mark -

#import "CRMConnectionSettings.h"

@implementation CRMConnectionSettings


#pragma mark -
#pragma mark Static methods

+ (NSString *)clientId
{
    return OAUTH_CLIENT_ID;
}

+ (NSString *)clientSecret
{
    return OAUTH_CLIENT_SECRET;
}

+ (NSString *)url
{
    return CRM_URL;
}

+ (NSURL *)urlWithFormat:(NSString *)format, ... NS_FORMAT_FUNCTION(1,2)
{
    va_list arguments;
    va_start(arguments, format);
    NSString* s = [[NSString alloc] initWithFormat:format arguments:arguments];
    va_end(arguments);
    return [NSURL URLWithString:[NSString stringWithFormat:@"%@%@", CRM_URL, s]];
}

@end
