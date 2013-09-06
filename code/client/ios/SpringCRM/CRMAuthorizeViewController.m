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
//  CRMAuthorizeViewController.m
//  SpringCRM
//
//  Created by Roy Clarkson on 6/7/10.
//

#import "CRMAuthorizeViewController.h"
#import "CRMSignInViewController.h"

@implementation CRMAuthorizeViewController

@synthesize signInViewController;


#pragma mark -
#pragma mark Public Instance methods

- (IBAction)actionSignIn:(id)sender
{
    [self.navigationController pushViewController:signInViewController animated:YES];
}


#pragma mark -
#pragma mark UIViewController methods

- (void)viewDidLoad 
{
    [super viewDidLoad];
    DLog(@"");
    self.title = @"Spring CRM";
    
    self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"Home"
                                                                             style:UIBarButtonItemStyleBordered
                                                                            target:nil
                                                                            action:nil];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    DLog(@"");
    
//    [self.navigationController setNavigationBarHidden:YES];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    DLog(@"");
    
    self.signInViewController = nil;
}

@end
