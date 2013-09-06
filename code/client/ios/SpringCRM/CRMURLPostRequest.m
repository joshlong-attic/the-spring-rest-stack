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
//  CRMURLPostRequest.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/16/12.
//

#import "CRMURLPostRequest.h"

@implementation CRMURLPostRequest

- (id)initWithURL:(NSURL *)URL parameters:(CRMURLRequestParameters *)parameters
{
    if (self = [super initWithURL:URL])
    {
        NSData *postData = [parameters encodedData];
        [self setHTTPMethod:@"POST"];
        [self setHTTPBody:postData];
        [self setContentType:@"application/x-www-form-urlencoded"];
        [self setContentLength:postData.length];
    }
    return self;
}

@end
