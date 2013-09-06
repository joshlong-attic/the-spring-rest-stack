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
//  CRMCoreDataManager.m
//  SpringCRM
//
//  Created by Roy Clarkson on 8/31/12.
//

#import "CRMCoreDataManager.h"
#import "CRMAppSettings.h"

@implementation CRMCoreDataManager

@synthesize managedObjectContext = _managedObjectContext;
@synthesize managedObjectModel = _managedObjectModel;
@synthesize persistentStoreCoordinator = _persistentStoreCoordinator;


#pragma mark -
#pragma mark Static methods

// Use this class method to obtain the shared instance of the class.
+ (CRMCoreDataManager *)sharedInstance
{
    static CRMCoreDataManager *_sharedInstance = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        _sharedInstance = [[CRMCoreDataManager alloc] init];
    });
    return _sharedInstance;
}


#pragma mark -
#pragma mark Instance methods

// Returns the managed object context for the application.
// If the context doesn't already exist, it is created and bound to the persistent store coordinator for the application.
- (NSManagedObjectContext *)managedObjectContext
{
    if (_managedObjectContext == nil)
    {
        _managedObjectContext = [[NSManagedObjectContext alloc] init];
    }
    
    if (_managedObjectContext.persistentStoreCoordinator == nil)
    {
        NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
        if (coordinator != nil)
        {
            [_managedObjectContext setPersistentStoreCoordinator:coordinator];
        }
    }
    
    return _managedObjectContext;
}

// Returns the managed object model for the application.
// If the model doesn't already exist, it is created from the application's model.
- (NSManagedObjectModel *)managedObjectModel
{
    if (_managedObjectModel != nil)
    {
        return _managedObjectModel;
    }
    NSURL *modelURL = [[NSBundle mainBundle] URLForResource:@"SpringCRM" withExtension:@"momd"];
    _managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:modelURL];
    return _managedObjectModel;
}

// Returns the persistent store coordinator for the application.
// If the coordinator doesn't already exist, it is created and the application's store added to it.
- (NSPersistentStoreCoordinator *)persistentStoreCoordinator
{
    if (_persistentStoreCoordinator == nil)
    {
        NSPersistentStoreCoordinator *storeCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self managedObjectModel]];        
        _persistentStoreCoordinator = storeCoordinator;
        DLog(@"");
        NSError *error = nil;
        if (![_persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType
                                                       configuration:nil
                                                                 URL:[self storeURL]
                                                             options:nil
                                                               error:&error])
        {
            DLog(@"Unresolved error %@, %@", error, [error userInfo]);
        }
    }
 
    return _persistentStoreCoordinator;
}

//- (void)createPersistentStore
//{
//    NSError *error = nil;
//    NSPersistentStoreCoordinator *storeCoordinator = [self persistentStoreCoordinator];
//    if (![storeCoordinator addPersistentStoreWithType:NSSQLiteStoreType
//                                        configuration:nil
//                                                  URL:[self storeURL]
//                                              options:nil
//                                                error:&error])
//    {
//        DLog(@"Unresolved error %@, %@", error, [error userInfo]);
//    }
//}

- (void)deletePersistentStore
{
    NSError *error;
    NSPersistentStoreCoordinator *storeCoordinator = [self persistentStoreCoordinator];
    NSPersistentStore *store = [storeCoordinator persistentStoreForURL:[self storeURL]];
    [storeCoordinator removePersistentStore:store error:&error];
    if (![[NSFileManager defaultManager] removeItemAtURL:[self storeURL] error:&error])
    {
        DLog(@"Unresolved error %@, %@", error, [error userInfo]);
    }
    _managedObjectContext = nil;
    _persistentStoreCoordinator = nil;
}

- (NSURL *)storeURL
{
    return [[CRMAppSettings documentsDirectory] URLByAppendingPathComponent:@"SpringSCRM.sqlite"];
}

@end
