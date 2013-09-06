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
//  CRMProfileController.h
//  SpringCRM
//
//  Created by Roy Clarkson on 9/7/10.
//

#import "CRMBaseController.h"
#import "CRMProfileControllerDelegate.h"

@class Profile;

@interface CRMProfileController : CRMBaseController

+ (id)sharedInstance;

- (Profile *)fetchProfile;
- (void)sendRequestForProfileWithDelegate:(id<CRMProfileControllerDelegate>)delegate;
- (void)storeProfileWithJson:(NSDictionary *)dictionary;
- (void)deleteProfile;

@end