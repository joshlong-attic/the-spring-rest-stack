//
//  OA2SignInRequest.h
//  SpringCRM
//
//  Created by Roy Clarkson on 8/27/12.
//

#import "CRMURLPostRequest.h"
#import "OA2SignInRequestParameters.h"

@interface OA2SignInRequest : CRMURLPostRequest

- (id)initWithURL:(NSURL *)URL username:(NSString *)username password:(NSString *)password;
- (id)initWithURL:(NSURL *)URL signInParameters:(OA2SignInRequestParameters *)signInParameters;

@end
