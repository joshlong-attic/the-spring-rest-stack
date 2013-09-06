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
//  OA2SignInRequestParameters.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/15/12.
//

#define KEY_GRANT_TYPE      @"grant_type"
#define KEY_USERNAME        @"username"
#define KEY_PASSWORD        @"password"
#define KEY_CLIENT_ID       @"client_id"
#define KEY_CLIENT_SECRET   @"client_secret"
#define KEY_SCOPE           @"scope"
#define VALUE_GRANT_TYPE    @"password"
#define VALUE_SCOPE         @"read,write"

#import "OA2SignInRequestParameters.h"
#import "CRMConnectionSettings.h"

@implementation OA2SignInRequestParameters

- (id)init
{
    return [self initWithUsername:@"" password:@""];
}

- (id)initWithUsername:(NSString *)username password:(NSString *)password
{
    self = [super initWithDictionary:[[NSDictionary alloc] initWithObjectsAndKeys:
                                      VALUE_GRANT_TYPE, KEY_GRANT_TYPE,
                                      username, KEY_USERNAME,
                                      password, KEY_PASSWORD,
                                      [CRMConnectionSettings clientId], KEY_CLIENT_ID,
                                      [CRMConnectionSettings clientSecret], KEY_CLIENT_SECRET,
                                      VALUE_SCOPE, KEY_SCOPE,
                                      nil]];
    return self;
}

@end
