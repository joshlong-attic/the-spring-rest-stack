//
//  OA2AuthorizedRequest.h
//  SpringCRM
//
//  Created by Roy Clarkson on 8/27/12.
//

#import "CRMURLRequest.h"

@interface OA2AuthorizedRequest : CRMURLRequest

- (id)initWithURL:(NSURL *)URL accessToken:(NSString *)accessToken;
- (NSString *)bearerTokenWithAccessToken:(NSString *)accessToken;

@end
