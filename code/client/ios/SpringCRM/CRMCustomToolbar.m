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
//  CRMCustomToolbar.m
//  SpringCRM
//
//  Created by Roy Clarkson on 10/5/10.
//

#import "CRMCustomToolbar.h"


@implementation CRMCustomToolbar


#pragma mark -
#pragma mark UIView methods

- (id)initWithFrame:(CGRect)frame 
{
    if ((self = [super initWithFrame:frame])) 
	{
        // Initialization code
    }
	
    return self;
}

- (void)drawRect:(CGRect)rect
{
	[super drawRect:rect];
	
	UIColor *color = [UIColor springLightGreenColor];
	CGContextRef context = UIGraphicsGetCurrentContext();
	CGContextSetFillColor(context, CGColorGetComponents(color.CGColor));
	CGContextFillRect(context, rect);
	self.tintColor = color;
	
	// warning, workaround ahead...
	// The tint is not being applied correctly to UIBarButtonItems
	// see http://www.openradar.me/8121374
	for (UIBarButtonItem *buttonItem in self.items)
	{		
		if ([buttonItem respondsToSelector:@selector(setTintColor:)]) 
		{
			[buttonItem performSelector:@selector(setTintColor:) withObject:color];
		}		
	}
}

@end
