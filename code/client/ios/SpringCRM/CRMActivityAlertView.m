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
//  CRMActivityAlertView.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/12/10.
//

#import "CRMActivityAlertView.h"


@interface CRMActivityAlertView()

@property (nonatomic, strong) UIActivityIndicatorView *activityIndicatorView;

@end


@implementation CRMActivityAlertView

@synthesize activityIndicatorView;

- (id)initWithActivityMessage:(NSString *)message
{
	return [self initWithTitle:nil message:message delegate:nil cancelButtonTitle:nil otherButtonTitles:nil];
}

- (id)initWithTitle:(NSString *)title 
			message:(NSString *)message 
		   delegate:(id)delegate 
  cancelButtonTitle:(NSString *)cancelButtonTitle 
  otherButtonTitles:(NSString *)otherButtonTitles, ...
{
	if ((self = [super initWithTitle:nil message:message delegate:nil cancelButtonTitle:nil otherButtonTitles:nil]))
	{
		self.activityIndicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
		
		CGRect frame = activityIndicatorView.frame;
		frame.origin.x = 140.0f - (frame.size.width / 2);

		if (message)
		{
			frame.origin.y = 50.0f;
		}
		else 
		{
			frame.origin.y = 15.0f;
		}

		activityIndicatorView.frame = frame;
		
		[self addSubview:activityIndicatorView];
	}
	
	return self;
}

- (void)startAnimating
{
	[activityIndicatorView startAnimating];
	[self show];
}

- (void)stopAnimating
{
	[self dismissWithClickedButtonIndex:0 animated:NO];
	[activityIndicatorView stopAnimating];
}

@end
