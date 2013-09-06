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
//  NSDictionary+Helpers.h
//  SpringCRM
//
//  Created by Roy Clarkson on 6/28/10.
//

#import <Foundation/Foundation.h>

@interface NSDictionary (NSDictionary_Helpers)

- (NSString *)stringForKey:(id)aKey;
- (NSString *)stringByReplacingPercentEscapesForKey:(id)aKey usingEncoding:(NSStringEncoding)encoding;
- (NSNumber *)numberForKey:(id)aKey;
- (NSInteger)integerForKey:(id)aKey;
- (double)doubleForKey:(id)aKey;
- (BOOL)boolForKey:(id)aKey;
- (NSDate *)dateWithMillisecondsSince1970ForKey:(id)aKey;
//- (NSDate *)localDateWithMillisecondsSince1970ForKey:(id)aKey;
- (NSURL *)urlForKey:(id)aKey;

@end
