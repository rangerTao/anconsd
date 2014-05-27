//
//  SoftInstallItemQueue.h
//  GameCenter91
//
//  Created by hiyo on 12-11-13.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KSDownloadItem.h"

@interface SoftInstallItemQueue : NSObject
@property (nonatomic, assign) int maxConcurrent;                           //if set to <=0, it means no limit; default no limit; 

@property (nonatomic, readonly) NSArray *queue;                            // installingQueue

- (void)setupQueue:(NSArray *)itemArray;

- (BOOL)addItemToQueue:(id<KSDownloadItem>)item;                          //start immediately
- (BOOL)addItemToQueue:(id<KSDownloadItem>)item startImmediately:(BOOL)startImmediately;

- (void)schedule;

- (void)startItem:(id<KSDownloadItem>)item;
- (void)stopItem:(id<KSDownloadItem>)item;

- (void)removeItem:(id<KSDownloadItem>)item;
- (void)removeItem:(id<KSDownloadItem>)item removeFile:(BOOL)removeFile;

- (void)stopAllItems;

- (void)finishInitializeTaskForInstallItem:(id<KSDownloadItem>)item;
- (void)finishInitializeTaskForInstallItem:(id<KSDownloadItem>)item withErorr:(NSError *)error;

- (id<KSDownloadItem>)itemWithPrimaryKey:(NSString *)primaryKey;
#pragma mark -
#pragma mark below are methods for subclass

#pragma mark these methods are required for subclass!!
- (void)startInstallForItem:(id<KSDownloadItem>)item;      //when override, remember change the status of item yourself
- (void)stopInstallForItem:(id<KSDownloadItem>)item;       //when override, remember change the status of item yourself

- (void)installItem:(id<KSDownloadItem>)item didFinishWithError:(NSError *)error;  //if error == nil, install success, or fail

#pragma mark these methods are optional for subclass
- (BOOL)shouldReplaceExistInstallItem:(id<KSDownloadItem>)existItem withNewItem:(id<KSDownloadItem>)aNewItem;
- (void)startInitializeTaskForInstallItem:(id<KSDownloadItem>)item;      //call InstallQueue's finish when finished initialize

- (id<KSDownloadItem>)findTheFirstItemToStop;

- (void)itemDidAddToQueue:(id<KSDownloadItem>)item;
- (void)itemDidFinishInitialize:(id<KSDownloadItem>)item;
- (void)itemDidInstallSuccess:(id<KSDownloadItem>)item;
- (void)itemDidInstallFail:(id<KSDownloadItem>)item;

@end
