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
//  CRMURLRequest.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/26/12.
//

#define ACCEPT                      @"Accept"
#define AUTHORIZATION               @"Authorization"
#define CONTENT_LENGTH              @"Content-Length"
#define CONTENT_TYPE                @"Content-Type"
#define APPLICATION_FORM_URLENCODED @"application/x-www-form-urlencoded"

#import "CRMURLRequest.h"

@implementation CRMURLRequest

- (void)setAccept:(NSString *)accept
{
    [self setValue:accept forHTTPHeaderField:ACCEPT];
}

- (void)setAuthorization:(NSString *)authorization
{
    [self setValue:authorization forHTTPHeaderField:AUTHORIZATION];
}

- (void)setContentLength:(NSUInteger)contentLength
{
    [self setValue:[NSString stringWithFormat:@"%ui", contentLength] forHTTPHeaderField:CONTENT_LENGTH];
}

- (void)setContentType:(NSString *)contentType
{
    [self setValue:contentType forHTTPHeaderField:CONTENT_TYPE];
}

@end
