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
//  OA2AccessGrant.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/16/12.
//

#import "OA2AccessGrant.h"

#define KEY_ACCESS_TOKEN    @"access_token"
#define KEY_SCOPE           @"scope"
#define KEY_REFRESH_TOKEN   @"refresh_token"
#define KEY_EXPIRES_IN      @"expires_in"
#define KEY_EXPIRE_TIME     @"expire_time"

@interface OA2AccessGrant ()

@property (nonatomic, copy, readwrite) NSString *accessToken;
@property (nonatomic, copy, readwrite) NSString *scope;
@property (nonatomic, copy, readwrite) NSString *refreshToken;
@property (nonatomic, assign, readwrite) NSTimeInterval expireTime;

@end

@implementation OA2AccessGrant

@synthesize accessToken = _accessToken;
@synthesize scope = _scope;
@synthesize refreshToken = _refreshToken;
@synthesize expireTime = _expireTime;


#pragma mark -
#pragma mark Static methods

+ (NSTimeInterval)expireTimeWithExpiresIn:(NSNumber *)expiresIn currentTimeMilliseconds:(NSNumber **)millis
{
    NSTimeInterval currentTimeMillis = [[NSDate date] timeIntervalSince1970];
    if (millis)
    {
        *millis = [NSNumber numberWithDouble:currentTimeMillis];
    }
    return currentTimeMillis + ([expiresIn integerValue] * 10001);
}


#pragma mark -
#pragma mark Instance methods

- (id)init
{
    return [self initWithAccessToken:nil scope:nil refreshToken:nil expireTime:0.0];
}

- (id)initWithData:(NSData *)data error:(NSError **)error
{
    id accessGrant;
    @try
    {
        NSError *internalError;
        NSDictionary *dictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:&internalError];
        accessGrant = [self initWithDictionary:dictionary error:error];
    }
    @catch (NSException *exception)
    {
        // NSJSONSerialization throws an NSInvalidArgumentException if data is nil
        DLog(@"%@ - %@", [exception name], [exception reason]);
        if (error && [[exception name] isEqualToString:NSInvalidArgumentException])
        {
            NSDictionary *errorDictionary = [[NSDictionary alloc] initWithObjectsAndKeys:
                                             NSLocalizedString(@"data parameter is nil", @""), NSLocalizedDescriptionKey,
                                             nil];
            *error = [[NSError alloc] initWithDomain:@"SpringCRMErrorDomain" code:-1 userInfo:errorDictionary];
        }
        accessGrant = [self init];
    }
    @finally
    {
        return accessGrant;
    }
}

- (id)initWithDictionary:(NSDictionary *)dictionary error:(NSError **)error
{
    if (!dictionary && error)
    {
        NSDictionary *errorDictionary = [[NSDictionary alloc] initWithObjectsAndKeys:
                                         NSLocalizedString(@"dictionary parameter is nil", @""), NSLocalizedDescriptionKey,
                                         nil];
        *error = [[NSError alloc] initWithDomain:@"SpringCRMErrorDomain" code:-1 userInfo:errorDictionary];
    }
    else if (dictionary && [[dictionary allKeys] containsObject:KEY_EXPIRE_TIME])
    {
        return [self initWithAccessToken:[dictionary stringForKey:KEY_ACCESS_TOKEN]
                                   scope:[dictionary stringForKey:KEY_SCOPE]
                            refreshToken:[dictionary stringForKey:KEY_REFRESH_TOKEN]
                              expireTime:[dictionary doubleForKey:KEY_EXPIRE_TIME]];
    }
    else if (dictionary && [[dictionary allKeys] containsObject:KEY_EXPIRES_IN])
    {
        return [self initWithAccessToken:[dictionary stringForKey:KEY_ACCESS_TOKEN]
                                   scope:[dictionary stringForKey:KEY_SCOPE]
                            refreshToken:[dictionary stringForKey:KEY_REFRESH_TOKEN]
                               expiresIn:[dictionary objectForKey:KEY_EXPIRES_IN]];
    }
    
    return [self init];
}

- (id)initWithAccessToken:(NSString *)accessToken
{
    return [self initWithAccessToken:accessToken scope:nil refreshToken:nil expiresIn:nil];
}

- (id)initWithAccessToken:(NSString *)accessToken scope:(NSString *)scope refreshToken:(NSString *)refreshToken expiresIn:(NSNumber *)expiresIn
{
    return [self initWithAccessToken:accessToken scope:scope refreshToken:refreshToken expiresIn:expiresIn currentTimeMilliseconds:nil];
}

- (id)initWithAccessToken:(NSString *)accessToken scope:(NSString *)scope refreshToken:(NSString *)refreshToken expiresIn:(NSNumber *)expiresIn currentTimeMilliseconds:(NSNumber **)millis
{
    NSTimeInterval expireTime = [OA2AccessGrant expireTimeWithExpiresIn:expiresIn currentTimeMilliseconds:millis];
    return [self initWithAccessToken:accessToken scope:scope refreshToken:refreshToken expireTime:expireTime];
}

- (id)initWithAccessToken:(NSString *)accessToken scope:(NSString *)scope refreshToken:(NSString *)refreshToken expireTime:(NSTimeInterval)expireTime
{
    DLog(@"accessToken: %@", accessToken);
    DLog(@"scope: %@", scope);
    DLog(@"refreshToken: %@", refreshToken);
    DLog(@"expireTime: %f", expireTime);
    
    if (self = [super init])
    {
        self.accessToken = accessToken;
        self.scope = scope;
        self.refreshToken = refreshToken == nil ? @"" : refreshToken;
        self.expireTime = expireTime;
    }
    return self;
}

- (NSDictionary *)dictionaryValue
{
    return [[NSDictionary alloc] initWithObjectsAndKeys:
            self.accessToken, KEY_ACCESS_TOKEN,
            self.scope, KEY_SCOPE,
            self.refreshToken, KEY_REFRESH_TOKEN,
            [NSNumber numberWithDouble:self.expireTime], KEY_EXPIRE_TIME,
            nil];
}

- (NSData *)dataValue
{
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:[self dictionaryValue] options:0 error:&error];
    DLog(@"error: %@", [error localizedDescription]);
    return jsonData;
}

@end
