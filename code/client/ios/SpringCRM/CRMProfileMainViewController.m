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
//  CRMProfileMainViewController.m
//  SpringCRM
//
//  Created by Roy Clarkson on 6/7/10.
//

#import "CRMProfileMainViewController.h"
#import "CRMProfileController.h"
#import "Profile.h"
#import "CRMCoreDataManager.h"
#import "CRMAuthController.h"

@interface CRMProfileMainViewController()

@property (nonatomic, strong) Profile *profile;

- (void)displayProfile;
- (void)signOut;

@end

@implementation CRMProfileMainViewController

@synthesize profile = _profile;
@synthesize labelDisplayName;
@synthesize profileImage;


#pragma mark -
#pragma mark Public Instance methods

- (IBAction)actionSignOut:(id)sender
{
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:nil
                                                        message:@"Would you like to sign out?"
                                                       delegate:self
                                              cancelButtonTitle:@"Cancel"
                                              otherButtonTitles:@"OK", nil];
    alertView.tag = 1;
    [alertView show];
}

- (IBAction)actionRefresh:(id)sender
{
    [[CRMProfileController sharedInstance] sendRequestForProfileWithDelegate:self];
}


#pragma mark -
#pragma mark Private Instance methods

- (void)displayProfile
{
    [self sendRequestForProfileImage];
	labelDisplayName.text = [NSString stringWithFormat:@"%@ %@", self.profile.firstName, self.profile.lastName];
}

- (void)signOut
{
    [CRMAuthController deleteAccessGrant];
    [[CRMCoreDataManager sharedInstance] deletePersistentStore];
	[(SpringCRMAppDelegate *)[[UIApplication sharedApplication] delegate] showAuthNavigationViewController];
}

- (void)sendRequestForProfileImage
{
    NSURL *url = [NSURL URLWithString:self.profile.imageUrl];
    NSMutableURLRequest *request = [[CRMAuthorizedRequest alloc] initWithURL:url];
	DLog(@"%@", request);
	
    [NSURLConnection sendAsynchronousRequest:request
                                       queue:[NSOperationQueue mainQueue]
                           completionHandler:^(NSURLResponse *response, NSData *data, NSError *error)
     {
         NSInteger statusCode = [(NSHTTPURLResponse *)response statusCode];
         DLog(@"Http Status: %d", statusCode);
         if (statusCode == 200 && data.length > 0 && error == nil)
         {
             UIImage *downloadedImage = [[UIImage alloc] initWithData:data];
             profileImage.image = downloadedImage;
             [profileImage setNeedsDisplay];
         }
         else {
             DLog(@"booo. something happened");
         }
     }];
}


#pragma mark -
#pragma mark ProfileControllerDelegate methods

- (void)fetchProfileDidFinishWithResults:(Profile *)profile;
{
    self.profile = profile;
    [self displayProfile];
}

- (void)fetchProfileDidFailWithError:(NSError *)error
{

}


#pragma mark -
#pragma mark UIAlertViewDelegate methods

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    if (alertView.tag == 1 && buttonIndex == 1)
    {
        [self signOut];
    }
}


#pragma mark -
#pragma mark UIViewController methods

- (void)viewDidLoad 
{
    [super viewDidLoad];
    DLog(@"");
    
    labelDisplayName.text = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    DLog(@"");
    
    [self.navigationController setNavigationBarHidden:YES];
    
    self.profile = [[CRMProfileController sharedInstance] fetchProfile];
    if (self.profile == nil)
    {
        [[CRMProfileController sharedInstance] sendRequestForProfileWithDelegate:self];
    }
    else
    {
        [self displayProfile];
    }
}

- (void)viewDidUnload 
{
    [super viewDidUnload];
    DLog(@"");
    
    self.profile = nil;
	self.labelDisplayName = nil;
}

@end
