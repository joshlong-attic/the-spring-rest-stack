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
//  NSDate+Helpers.m
//  SpringCRM
//
//  Created by Roy Clarkson on 9/2/12.
//

#import "NSDate+Helpers.h"

@implementation NSDate (NSDate_Helpers)

+ (id)dateWithMillisecondsSince1970:(double)milliseconds
{
    return [[NSDate alloc] initWithMillisecondsSince1970:milliseconds];
}

- (id)initWithMillisecondsSince1970:(double)milliseconds
{
    NSTimeInterval unixDate = (milliseconds * .001);
    return [[NSDate alloc] initWithTimeIntervalSince1970:unixDate];
}

@end
