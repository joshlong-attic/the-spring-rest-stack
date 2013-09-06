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
//  SpringCRMAppDelegate.h
//  SpringCRM
//
//  Created by Roy Clarkson on 6/7/10.
//

#import <UIKit/UIKit.h>

@interface SpringCRMAppDelegate : NSObject <UIApplicationDelegate, UIAlertViewDelegate>

@property (nonatomic, strong) IBOutlet UIWindow *window;
@property (nonatomic, strong) IBOutlet UINavigationController *authNavigationController;
@property (nonatomic, strong) IBOutlet UINavigationController *mainNavigationController;

- (void)showAuthNavigationViewController;
- (void)showMainNavigationViewController;

@end

