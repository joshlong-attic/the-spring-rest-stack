//
//  OA2SignInRequest.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/27/12.
//

#import "OA2SignInRequest.h"
#import "OA2SignInRequestParameters.h"

@implementation OA2SignInRequest

- (id)initWithURL:(NSURL *)URL username:(NSString *)username password:(NSString *)password
{
    OA2SignInRequestParameters *parameters = [[OA2SignInRequestParameters alloc] initWithUsername:username password:password];
    return [self initWithURL:URL signInParameters:parameters];
}

- (id)initWithURL:(NSURL *)URL signInParameters:(OA2SignInRequestParameters *)signInParameters
{
    if (self = [super initWithURL:URL parameters:signInParameters])
    {
        
    }
    return self;
}

@end
