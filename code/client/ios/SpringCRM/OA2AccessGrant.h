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
//  OA2AccessGrant.h
//  SpringCRM
//
//  Created by Roy Clarkson on 8/16/12.
//

#import <Foundation/Foundation.h>

@interface OA2AccessGrant : NSObject

@property (nonatomic, copy, readonly) NSString *accessToken;
@property (nonatomic, copy, readonly) NSString *scope;
@property (nonatomic, copy, readonly) NSString *refreshToken;
@property (nonatomic, assign, readonly) NSTimeInterval expireTime;

+ (NSTimeInterval)expireTimeWithExpiresIn:(NSNumber *)expiresIn currentTimeMilliseconds:(NSNumber **)millis;

- (id)initWithData:(NSData *)data error:(NSError **)error;
- (id)initWithDictionary:(NSDictionary *)dictionary error:(NSError **)error;
- (id)initWithAccessToken:(NSString *)accessToken;
- (id)initWithAccessToken:(NSString *)accessToken scope:(NSString *)scope refreshToken:(NSString *)refreshToken expiresIn:(NSNumber *)expiresIn;
- (id)initWithAccessToken:(NSString *)accessToken scope:(NSString *)scope refreshToken:(NSString *)refreshToken expiresIn:(NSNumber *)expiresIn currentTimeMilliseconds:(NSNumber **)millis;
- (id)initWithAccessToken:(NSString *)accessToken scope:(NSString *)scope refreshToken:(NSString *)refreshToken expireTime:(NSTimeInterval)expireTime;
- (NSDictionary *)dictionaryValue;
- (NSData *)dataValue;

@end
