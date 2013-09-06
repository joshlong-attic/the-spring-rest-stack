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
//  CRMURLRequestParameters.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/16/12.
//

#import "CRMURLRequestParameters.h"
#import "NSString+Encoding.h"

@interface CRMURLRequestParameters ()

@property (nonatomic, strong) NSDictionary *parameterDictionary;

@end

@implementation CRMURLRequestParameters

@synthesize parameterDictionary = _parameterDictionary;

- (id)init
{
    return [self initWithDictionary:[[NSDictionary alloc] init]];
}

- (id)initWithDictionary:(NSDictionary *)dictionary
{
    if (self = [super init])
    {
        self.parameterDictionary = dictionary;
    }
    return self;
}

- (NSString *)encodedParameters
{
    NSMutableArray *array = [[NSMutableArray alloc] init];
    [self.parameterDictionary enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL *stop) {
        [array addObject:[NSString stringWithFormat:@"%@=%@", [key stringByURLEncoding], [obj stringByURLEncoding]]];
    }];
    return [array componentsJoinedByString:@"&"];
}

- (NSData *)encodedData
{
    return [[self encodedParameters] dataUsingEncoding:NSUTF8StringEncoding];
}

@end
