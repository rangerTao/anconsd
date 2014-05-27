//
//  KSDownloadQueue.h
//  KSDownloadQueue
//
//  Created by kensou on 12-10-5.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KSDownloadItem.h"

@interface KSDownloadQueue : NSObject
@property (nonatomic, assign) int maxConcurrent;                           //if set to <=0, it means no limit; default no limit; 

@property (nonatomic, readonly) NSArray *queue;                            // downloadingQueue

- (void)setupQueue:(NSArray *)itemArray;
- (void)setupQueue:(NSArray *)itemArray state:(KS_DOWNLOAD_STATUS)state;


- (BOOL)addItemToQueue:(id<KSDownloadItem>)item;                          //start immediately
- (BOOL)addItemToQueue:(id<KSDownloadItem>)item startImmediately:(BOOL)startImmediately;

- (void)schedule;

- (void)startItem:(id<KSDownloadItem>)item;
- (void)stopItem:(id<KSDownloadItem>)item;

- (void)removeItem:(id<KSDownloadItem>)item;
- (void)removeItem:(id<KSDownloadItem>)item removeFile:(BOOL)removeFile;

- (void)stopAllItems;

- (void)finishInitializeTaskForDownloadItem:(id<KSDownloadItem>)item;
- (void)finishInitializeTaskForDownloadItem:(id<KSDownloadItem>)item withErorr:(NSError *)error;

- (id<KSDownloadItem>)itemWithPrimaryKey:(NSString *)primaryKey;
#pragma mark -
#pragma mark below are methods for subclass

#pragma mark these methods are required for subclass!!
- (void)startDownloadForItem:(id<KSDownloadItem>)item;      //when override, remember change the status of item yourself
- (void)stopDownloadForItem:(id<KSDownloadItem>)item;       //when override, remember change the status of item yourself

- (void)downloadItem:(id<KSDownloadItem>)item didFinishWithError:(NSError *)error;  //if error == nil, download success, or fail
//- (void)downloadItem:(id<KSDownloadItem>)item totalLen:(long)totalLen downloaded:(long)downloaded;

#pragma mark these methods are optional for subclass
- (BOOL)shouldReplaceExistDownloadItem:(id<KSDownloadItem>)existItem withNewItem:(id<KSDownloadItem>)aNewItem;
- (void)startInitializeTaskForDownloadItem:(id<KSDownloadItem>)item;      //call DownloadQueue's finish when finished initialize

- (id<KSDownloadItem>)findTheFirstItemToStop;

- (void)itemDidAddToQueue:(id<KSDownloadItem>)item;
- (void)itemDidStartInitialize:(id<KSDownloadItem>)item;
- (void)itemDidFinishInitialize:(id<KSDownloadItem>)item;
- (void)itemDidStart:(id<KSDownloadItem>)item;
- (void)itemDidStop:(id<KSDownloadItem>)item;
- (void)itemDidDownloadSuccess:(id<KSDownloadItem>)item;
- (void)itemDidDownloadFail:(id<KSDownloadItem>)item error:(NSError *)error;

@end

