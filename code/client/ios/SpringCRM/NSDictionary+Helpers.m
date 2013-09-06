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
//  NSDictionary+Helpers.m
//  SpringCRM
//
//  Created by Roy Clarkson on 6/28/10.
//

#import "NSDictionary+Helpers.h"

@implementation NSDictionary (NSDictionary_Helpers)

- (NSString *)stringForKey:(id)aKey 
{		
	NSString *s = nil;
	
	@try 
	{
		NSObject *o = [self objectForKey:aKey];
		
		if (o != nil && o != [NSNull null])
		{
			if ([o isKindOfClass:[NSString class]])
			{
				s = (NSString *)o;
			}			
			else if ([o isKindOfClass:[NSNumber class]])
			{
				s = [(NSNumber *)o stringValue];
			}
		}
	}
	@catch (NSException *e) 
	{
		DLog(@"Caught %@%@", [e name], [e reason]);
	}
	@finally 
	{ 
		return s;
	}
}

- (NSString *)stringByReplacingPercentEscapesForKey:(id)aKey usingEncoding:(NSStringEncoding)encoding
{		
	NSString *s = nil;
	
	@try 
	{
		NSObject *o = [self objectForKey:aKey];
		
		if (o != nil && o != [NSNull null])
		{
			s = [[NSString stringWithString:(NSString *)o] stringByReplacingPercentEscapesUsingEncoding:encoding];
		}
	}
	@catch (NSException *e) 
	{
		DLog(@"Caught %@%@", [e name], [e reason]);
	}
	@finally 
	{ 
		return s;
	}
}

- (NSNumber *)numberForKey:(id)aKey
{
	NSNumber *n = nil;
	
	@try
	{
		NSObject *o = [self objectForKey:aKey];
		
		if (o != nil && o != [NSNull null])
		{
            if ([o isKindOfClass:[NSNumber class]])
			{
				n = (NSNumber *)o;
			}
		}
	}
	@catch (NSException *e)
	{
		DLog(@"Caught %@%@", [e name], [e reason]);
	}
	@finally
	{
		return n;
	}
}

- (NSInteger)integerForKey:(id)aKey 
{	
	NSInteger i = 0;
	
	@try 
	{
		NSObject *o = [self objectForKey:aKey];
		
		if (o != nil && o != [NSNull null])
		{
			i = [(NSNumber *)o integerValue];
		}
	}
	@catch (NSException *e) 
	{
		DLog(@"Caught %@%@", [e name], [e reason]);
	}
	@finally 
	{ 
		return i;
	}
}

- (double)doubleForKey:(id)aKey 
{	
	double d = 0.0;
	
	@try 
	{
		NSObject *o = [self objectForKey:aKey];
		
		if (o != nil && o != [NSNull null])
		{
			d = [(NSNumber *)o doubleValue];
		}
	}
	@catch (NSException *e) 
	{
		DLog(@"Caught %@%@", [e name], [e reason]);
	}
	@finally 
	{ 
		return d;
	}
}

// any nonzero value will be interpreted as YES or true
- (BOOL)boolForKey:(id)aKey
{
	BOOL b = NO;
	
	@try 
	{
		NSObject *o = [self objectForKey:aKey];
		
		if (o != nil && o != [NSNull null])
		{
			b = [(NSNumber *)o boolValue];
		}
	}
	@catch (NSException * e) 
	{
		DLog(@"Caught %@%@", [e name], [e reason]);
	}
	@finally 
	{
		return b;
	}
}

- (NSDate *)dateWithMillisecondsSince1970ForKey:(id)aKey 
{	
	NSDate *date = nil;
	
	@try 
	{
		double milliseconds = [self doubleForKey:aKey];
		NSTimeInterval unixDate = (milliseconds * .001);
		date = [NSDate dateWithTimeIntervalSince1970:unixDate];
	}
	@catch (NSException *e) 
	{
		DLog(@"Caught %@%@", [e name], [e reason]);
	}
	@finally 
	{
		return date;
	}
}

- (NSURL *)urlForKey:(id)aKey
{
	NSURL *url = nil;
	
	@try 
	{
		url = [NSURL URLWithString:[self stringByReplacingPercentEscapesForKey:aKey usingEncoding:NSUTF8StringEncoding]];
	}
	@catch (NSException * e) 
	{
		DLog(@"Caught %@%@", [e name], [e reason]);
	}
	@finally 
	{
		return url;
	}
}


@end
