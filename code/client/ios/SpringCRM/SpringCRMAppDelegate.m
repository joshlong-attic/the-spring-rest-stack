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
//  SpringCRMAppDelegate.m
//  SpringCRM
//
//  Created by Roy Clarkson on 6/7/10.
//

#import "SpringCRMAppDelegate.h"
#import "CRMAuthController.h"
#import "CRMCoreDataManager.h"

@implementation SpringCRMAppDelegate

@synthesize window;
@synthesize authNavigationController;
@synthesize mainNavigationController;

- (void)showAuthNavigationViewController
{
	[mainNavigationController.view removeFromSuperview];
    [authNavigationController popToRootViewControllerAnimated:NO];
    [window addSubview:authNavigationController.view];
}

- (void)showMainNavigationViewController
{
    [authNavigationController.view removeFromSuperview];
    [mainNavigationController popToRootViewControllerAnimated:NO];
	[window addSubview:mainNavigationController.view];
}


#pragma mark -
#pragma mark UIAlertViewDelegate methods

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    // sign out
    [CRMAuthController deleteAccessGrant];
    [[CRMCoreDataManager sharedInstance] deletePersistentStore];
    [self showAuthNavigationViewController];
}


#pragma mark -
#pragma mark UITabBarControllerDelegate methods

- (void)tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController
{
	
}


#pragma mark -
#pragma mark UIApplicationDelegate methods

- (void)applicationDidFinishLaunching:(UIApplication *)application
{
	DLog(@"");
	[[NSUserDefaults standardUserDefaults] synchronize];
	if ([CRMUserSettings resetAppOnStart])
	{
		[CRMAuthController deleteAccessGrant];
        [[CRMCoreDataManager sharedInstance] deletePersistentStore];
		[CRMUserSettings reset];
		[self showAuthNavigationViewController];
	}
	else if ([CRMAuthController isAuthorized])
	{
		[self showMainNavigationViewController];
	}
	else
	{
        [self showAuthNavigationViewController];
	}
	
    [window makeKeyAndVisible];
	[CRMUserSettings setAppVersion:[CRMAppSettings appVersion]];
	return;
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
	DLog(@"");
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
	DLog(@"");
	[[NSUserDefaults standardUserDefaults] synchronize];
	if ([CRMUserSettings resetAppOnStart])
	{
		DLog(@"reset app");
		[CRMAuthController deleteAccessGrant];
        [[CRMCoreDataManager sharedInstance] deletePersistentStore];
		[CRMUserSettings reset];
		[CRMUserSettings setAppVersion:[CRMAppSettings appVersion]];
		[self showAuthNavigationViewController];
	}
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
	DLog(@"");
}

- (void)applicationWillResignActive:(UIApplication *)application
{
	DLog(@"");
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (void)applicationWillTerminate:(UIApplication *)application 
{    
	DLog(@"");
}

@end

