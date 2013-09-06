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
//  CRMBaseController.h
//  SpringCRM
//
//  Created by Roy Clarkson on 9/16/10.
//

#import <Foundation/Foundation.h>
#import "CRMAuthorizedRequest.h"
#import "OA2AccessGrant.h"
#import "CRMConnectionSettings.h"

@interface CRMBaseController : NSObject

void ProcessError(NSString* action, NSError* error);
- (void)requestDidNotSucceedWithDefaultMessage:(NSString *)message response:(NSURLResponse *)response;
- (void)requestDidFailWithError:(NSError *)error;

@end
