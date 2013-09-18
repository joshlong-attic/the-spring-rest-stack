//
//  Copyright 2013 Roy Clarkson.
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
//  Profile.h
//  SpringCRM
//
//  Created by Roy Clarkson on 9/17/13.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface Profile : NSManagedObject

@property (nonatomic, retain) NSNumber * accountId;
@property (nonatomic, retain) NSString * customersUrl;
@property (nonatomic, retain) NSString * firstName;
@property (nonatomic, retain) NSString * imageMediaType;
@property (nonatomic, retain) NSString * imageUrl;
@property (nonatomic, retain) NSString * profilePhotoMediaType;
@property (nonatomic, retain) NSString * lastName;
@property (nonatomic, retain) NSNumber * hasImage;

@end
