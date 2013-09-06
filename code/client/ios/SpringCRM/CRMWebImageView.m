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
//  CRMWebImageView.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/12/10.
//

#import "CRMWebImageView.h"


@implementation CRMWebImageView

@synthesize imageUrl;

- (id)initWithURL:(NSURL *)url
{
	if ((self = [super initWithImage:nil]))
	{
		self.imageUrl = url;
		[self startImageDownload];
	}
	
	return self;
}

- (void)startImageDownload
{
	DLog(@"%@", imageUrl);
	
	// cancel any current downloads to prevent memory leaks
	[self cancelImageDownload];
		
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:imageUrl];
    _urlConnection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
	
	if (_urlConnection)
	{
		_receivedData = [NSMutableData data];
	}
}

- (void)cancelImageDownload
{
	if (_urlConnection)
	{
		[_urlConnection cancel];
		_urlConnection = nil;
	}
	
	if (_receivedData)
	{
		_receivedData = nil;
	}
}


#pragma mark -
#pragma mark NSURLConnectionDelegate methods

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    [_receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [_receivedData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
	_receivedData = nil;
	_urlConnection = nil;
	
	DLog(@"Connection failed! Error - %@ %@",
		 [error localizedDescription],
		 [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
	_urlConnection = nil;
	
	DLog(@"Succeeded! Received %d bytes of data", [_receivedData length]);
		
    UIImage *downloadedImage = [[UIImage alloc] initWithData:_receivedData];
	_receivedData = nil;
	self.image = downloadedImage;
}

@end
