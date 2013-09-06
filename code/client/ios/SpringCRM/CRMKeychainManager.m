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
//  CRMKeychainManager.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/20/12.
//

#import "CRMKeychainManager.h"

@interface CRMKeychainManager ()

- (NSMutableDictionary *)queryKeychainWithService:(NSString *)service account:(NSString *)account;

@end

@implementation CRMKeychainManager

#pragma mark -
#pragma mark Static methods

// Use this class method to obtain the shared instance of the class.
+ (CRMKeychainManager *)sharedInstance
{
    static CRMKeychainManager *_sharedInstance = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        _sharedInstance = [[CRMKeychainManager alloc] init];
    });
    return _sharedInstance;
}


#pragma mark -
#pragma mark Helper methods

- (OSStatus)storePassword:(NSData *)passwordData service:(NSString *)service account:(NSString *)account;
{
    [self deletePasswordWithService:service account:account];
    NSMutableDictionary *query = [self queryKeychainWithService:service account:account];
    [query setValue:passwordData forKey:(__bridge id)kSecValueData];
    return SecItemAdd((__bridge CFDictionaryRef)query, NULL);
}

- (OSStatus)fetchPassword:(NSData **)passwordData service:(NSString *)service account:(NSString *)account;
{
	NSMutableDictionary *query = [self queryKeychainWithService:service account:account];
    [query setValue:(__bridge id)kCFBooleanTrue forKey:(__bridge id)(kSecReturnData)];
//    [query setValue:(__bridge id)kCFBooleanTrue forKey:(__bridge id)(kSecReturnPersistentRef)];
	
    CFDataRef result;
	OSStatus status = SecItemCopyMatching((__bridge CFDictionaryRef)query, (CFTypeRef *)&result);
    if (status == errSecSuccess && result)
    {
        *passwordData = (__bridge NSData*)result;
    }
    return status;
}

- (OSStatus)deletePasswordWithService:(NSString *)service account:(NSString *)account;
{
	NSDictionary *query = [self queryKeychainWithService:service account:account];
	return SecItemDelete((__bridge CFDictionaryRef)query);
}

- (NSMutableDictionary *)queryKeychainWithService:(NSString *)service account:(NSString *)account
{
    return [[NSMutableDictionary alloc] initWithObjectsAndKeys:
            (__bridge id)kSecClassGenericPassword, (__bridge id)kSecClass,
            service, (__bridge id)kSecAttrService,
            account, (__bridge id)kSecAttrAccount,
            nil];
}

@end
