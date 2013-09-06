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
//  CRMUserSettings.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/25/10.
//

#import "CRMUserSettings.h"


#define resetAppOnStartPreference			@"reset_app_on_start_preference"
#define dataExpirationPreference			@"data_expiration_preference"
#define versionPreference					@"version_preference"


@implementation CRMUserSettings

+ (void)reset
{
	[[NSUserDefaults standardUserDefaults] removePersistentDomainForName:[[NSBundle mainBundle] bundleIdentifier]];
	[[NSUserDefaults standardUserDefaults] setObject:@(NO) forKey:resetAppOnStartPreference];
}

+ (NSInteger)dataExpiration
{
	NSString *s = [[NSUserDefaults standardUserDefaults] stringForKey:dataExpirationPreference];
	DLog(@"%@", s);
	
	if (s && ![s isEqual:[NSNull null]])
	{
		return [s intValue];
	}
	else 
	{
        // default to 1 hour
        return 3600;
	}
}

+ (BOOL)resetAppOnStart
{
	return [[NSUserDefaults standardUserDefaults] boolForKey:resetAppOnStartPreference];
}

+ (void)setAppVersion:(NSString *)appVersion
{	
	[[NSUserDefaults standardUserDefaults] setObject:appVersion forKey:versionPreference];	
}

@end
