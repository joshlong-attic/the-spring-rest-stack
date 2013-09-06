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
//  CRMSignInViewController.m
//  SpringCRM
//
//  Created by Roy Clarkson on 7/12/12.
//

#import "CRMSignInViewController.h"
#import "OA2SignInRequestParameters.h"
#import "CRMURLPostRequest.h"
#import "OA2AccessGrant.h"
#import "CRMActivityAlertView.h"
#import "CRMAuthController.h"

@interface CRMSignInViewController ()
{
    BOOL keyboardIsDisplaying;
}

@property (nonatomic, strong) CRMActivityAlertView *activityView;

@end

@implementation CRMSignInViewController

@synthesize activityView;
@synthesize signInForm;
@synthesize usernameCell;
@synthesize passwordCell;
@synthesize usernameTextField;
@synthesize passwordTextField;

- (IBAction)actionCancel:(id)sender
{
    [self hideKeyboard];
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (IBAction)actionSubmit:(id)sender
{
    [self signInWithUserName:usernameTextField.text andPassword:passwordTextField.text];
}

- (void)signInWithUserName:(NSString *)username andPassword:(NSString *)password
{
    NSString *usernameValue = [username stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    NSString *passwordValue = [password stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    
    if(username == nil || [usernameValue isEqualToString:@""] ||
       password == nil || [passwordValue isEqualToString:@""])
    {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:nil
                                                            message:@"Your email or password was entered incorrectly."
                                                           delegate:self
                                                  cancelButtonTitle:@"OK"
                                                  otherButtonTitles:nil];
        [alertView show];
        DLog(@"empty username or password");
        return;
    }
    
    activityView = [[CRMActivityAlertView alloc] initWithActivityMessage:@"Signing in..."];
	[activityView startAnimating];
    [[CRMAuthController sharedInstance] sendRequestToSignIn:usernameValue password:passwordValue delegate:self];
}

- (void)hideKeyboard
{
    if (passwordTextField.isFirstResponder)
    {
        [passwordTextField resignFirstResponder];
    }
    else if (usernameTextField.isFirstResponder)
    {
        [usernameTextField resignFirstResponder];
    }
    
    keyboardIsDisplaying = NO;
}

- (NSTimeInterval)keyboardAnimationDurationForNotification:(NSNotification*)notification
{
    NSDictionary* info = [notification userInfo];
    NSValue* value = [info objectForKey:UIKeyboardAnimationDurationUserInfoKey];
    NSTimeInterval duration = 0;
    [value getValue:&duration];
    return duration;
}

- (void)keyboardWillShow:(NSNotification *)notification
{
    DLog(@"");
    if (keyboardIsDisplaying)
    {
        return;
    }

    CGRect viewFrame = self.view.frame;
    viewFrame.origin.y -= 120;
    
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:[self keyboardAnimationDurationForNotification:notification]];
    [self.view setFrame:viewFrame];
    [UIView commitAnimations];
    
    keyboardIsDisplaying = YES;
}

- (void)keyboardWillHide:(NSNotification *)notification
{
    DLog(@"");

    CGRect viewFrame = self.view.frame;
    viewFrame.origin.y += 120;
    
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:[self keyboardAnimationDurationForNotification:notification]];
    [self.view setFrame:viewFrame];
    [UIView commitAnimations];
    
    keyboardIsDisplaying = NO;
}


#pragma mark -
#pragma mark CRMAuthControllerDelegate methods

- (void)authRequestDidSucceed
{
    [activityView stopAnimating];
    self.activityView = nil;
    [(SpringCRMAppDelegate *)[[UIApplication sharedApplication] delegate] showMainNavigationViewController];
}

- (void)authRequestDidFailWithError:(NSError *)error
{
    [activityView stopAnimating];
    self.activityView = nil;
}


#pragma mark -
#pragma mark UITextFieldDelegate methods

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField.tag == 0)
    {
        [passwordTextField becomeFirstResponder];
    }
    else if (textField.tag ==1)
    {
        [passwordTextField resignFirstResponder];
        [self signInWithUserName:usernameTextField.text andPassword:passwordTextField.text];
    }
    
    return NO;
}


#pragma mark -
#pragma mark UITableViewDelegate methods

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	[tableView deselectRowAtIndexPath:indexPath animated:NO];
}


#pragma mark -
#pragma mark UITableViewDataSource methods

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath 
{
    if (indexPath.row == 0) 
    {
        return usernameCell;
    }
    else if (indexPath.row == 1) 
    {
        return passwordCell;
    }
    
    // static table content will not reach here
    return nil;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // fixed size table to include rows for email and password fields
    return 2;
}


#pragma mark -
#pragma mark UIViewController methods

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.title = @"Authenticate";
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:self.view.window];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:self.view.window];
    keyboardIsDisplaying = NO;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

    self.usernameTextField.text = nil;
    self.passwordTextField.text = nil;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:UIKeyboardWillShowNotification
                                                  object:nil];

    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:UIKeyboardWillHideNotification
                                                  object:nil];

    self.activityView = nil;
    self.signInForm = nil;
    self.usernameCell = nil;
    self.passwordCell = nil;
    self.usernameTextField = nil;
    self.passwordTextField = nil;
}

@end
