//
//  KSDownloadQueue.m
//  KSDownloadQueue
//
//  Created by kensou on 12-10-5.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "KSDownloadQueue.h"
#import "CommUtility.h"
#import "SoftItem.h"
#import "SoftIncreUpdateModule.h"
#define KSDQ_LOG    NSLog

NSString * const kKSDownloadStatusChangeNotify = @"kKSDownloadStatusChangeNotify";

@interface KSDownloadQueue()
@property (nonatomic, retain) NSMutableDictionary *downloadingQueue;
@property (nonatomic, assign) int concurrentCount;

- (void)schedule;
- (void)stopAllItems;
- (void)startItem:(id<KSDownloadItem>)item force:(BOOL)force;
@end

@implementation KSDownloadQueue
@synthesize maxConcurrent;
@synthesize downloadingQueue;
@synthesize concurrentCount;

- (id)init {
    self = [super init];
    if (self) {
        self.downloadingQueue = [NSMutableDictionary dictionary];
        self.concurrentCount = 0;
        self.maxConcurrent = -1;
    }
    return self;
}

- (void)dealloc {
    [self stopAllItems];
    self.downloadingQueue = nil;
    [super dealloc];
}

- (id<KSDownloadItem>)itemWithPrimaryKey:(NSString *)primaryKey
{
    if ([primaryKey length] == 0)
        return nil;
    return [self.downloadingQueue objectForKey:primaryKey];
}

- (void)setupQueue:(NSArray *)itemArray
{
    for (id<KSDownloadItem> item in itemArray)
    {
        NSString *primaryKey = [item primaryKey];
        if ([primaryKey length] == 0)
            continue;
        
        if ([item downloadStatus] == KS_INITIALIZING || [item downloadStatus] == KS_DOWNLOADING)
            [item setDownloadStatus:KS_QUEUING];
        [self.downloadingQueue setObject:item forKey:primaryKey];
    }
}

- (void)setupQueue:(NSArray *)itemArray state:(KS_DOWNLOAD_STATUS)state
{
    [self setupQueue:itemArray];
    for (id<KSDownloadItem> item in itemArray)
    {
        [item setDownloadStatus:state];
    }
}

- (BOOL)addItemToQueue:(id<KSDownloadItem>)item;                          //start immediately
{
    return [self addItemToQueue:item startImmediately:YES];
}

- (BOOL)addItemToQueue:(id<KSDownloadItem>)item startImmediately:(BOOL)startImmediately
{
    NSString *primaryKey = [item primaryKey];
    if (item == nil || [primaryKey length] == 0)
    {
        KSDQ_LOG(@"can not add an item nil or nil primary key");
        return NO;        
    }
    
    [item setTimeStamp:(long)[[NSDate date] timeIntervalSince1970]];
    id<KSDownloadItem> exist = [self.downloadingQueue objectForKey:primaryKey];
    if (exist)
    {
        BOOL shouldReplace = [self shouldReplaceExistDownloadItem:exist withNewItem:item];        
        if (shouldReplace == NO)
        {
            return NO;              
        }
        else
        {
            [self removeItem:exist removeFile:YES];
        }
    }
    
    [item setDownloadStatus:KS_QUEUING];
    [self.downloadingQueue setObject:item forKey:primaryKey];
    
    if (startImmediately == YES)
    {
        [self startItem:item];
    }
    [self itemDidAddToQueue:item];
//    [self schedule];
    return YES;
}

- (void)finishInitializeTaskForDownloadItem:(id<KSDownloadItem>)item
{
    [self finishInitializeTaskForDownloadItem:item withErorr:nil];
}

- (void)finishInitializeTaskForDownloadItem:(id<KSDownloadItem>)item withErorr:(NSError *)error
{
    if (error)
    {
        [item setDownloadStatus:KS_STOPPED];
        [self downloadItem:item didFinishWithError:error];
    }
    else
    {
        [self itemDidFinishInitialize:item];        
        [self startItem:item];
    }
}

- (void)stopItem:(id<KSDownloadItem>)item shouldSchedule:(BOOL)shouldSchedule
{
    NSString *primaryKey = [item primaryKey];
    if ([self.downloadingQueue objectForKey:primaryKey] == nil)
        return;
    
    int status = [item downloadStatus];
    if (status == KS_DOWNLOADING || status == KS_INITIALIZING)
    {
        [self stopDownloadForItem:item];
    }
    
    if (status != KS_FINISHED)
    {
        [item setDownloadStatus:KS_STOPPED];        
    }
    [self itemDidStop:item];
    
    //正在下载，初始化，下载完成
    if (status != KS_QUEUING) {
        self.concurrentCount--;
    }

    if (shouldSchedule)
        [self schedule];
}

- (void)stopItem:(id<KSDownloadItem>)item
{
    [self stopItem:item shouldSchedule:YES];
}

- (void)stopAllItems
{
    for (NSString *key in self.downloadingQueue) {
        id<KSDownloadItem> item = [self.downloadingQueue objectForKey:key];
        [self stopItem:item shouldSchedule:NO];
    }
}

- (void)removeItem:(id<KSDownloadItem>)item removeFile:(BOOL)removeFile
{
    [self stopItem:item];
    if (removeFile)
    {
        NSString *file = [[[CommUtility getDocumentPath] stringByAppendingPathComponent:[item savePath]] stringByAppendingPathComponent:[item fileName]];
        SoftItem * soft = (SoftItem *)item;
        if (soft.increUpateInfo != nil && !soft.increUpateInfo.smartUpdateFailed) {
            file = [SoftIncreUpdateModule getIncrementPackagePathForSoft:soft.identifier];
        }
        if (file)
        {
            [[NSFileManager defaultManager] removeItemAtPath:file error:nil];
        }
    }
    [self.downloadingQueue removeObjectForKey:[item primaryKey]];
}

- (void)removeItem:(id<KSDownloadItem>)item
{
    [self removeItem:item removeFile:NO];
}

- (NSArray *)queue
{
    NSArray *all = [self.downloadingQueue allValues];
    NSSortDescriptor *descriptor = [NSSortDescriptor sortDescriptorWithKey:@"timeStamp" ascending:YES];
    all = [all sortedArrayUsingDescriptors:[NSArray arrayWithObject:descriptor]];
    return all;
}

- (void)schedule
{
    NSArray *all = [self queue];    
    for (id<KSDownloadItem> item in all) {
        if ([item downloadStatus] == KS_QUEUING)
        {
            BOOL canRun = (self.maxConcurrent <= 0) ? YES : (self.concurrentCount < self.maxConcurrent);
            if (canRun)
            {
                [self startItem:item];
            }
            else
            {
                break;
            }
        }
    }
}

- (void)startItem:(id<KSDownloadItem>)item
{
    [self startItem:item force:NO];
}

- (void)startItem:(id<KSDownloadItem>)item force:(BOOL)force
{
    int status = [item downloadStatus];
    BOOL canRun = (self.maxConcurrent <= 0) ? YES : (self.concurrentCount < self.maxConcurrent);
    if (status == KS_INITIALIZING)
        canRun = YES;
    else
        [item setDownloadStatus:KS_QUEUING];
    
    if (canRun == NO && force == NO)    //the queue has been full, not allowed to run
        return;
    
    if (canRun == NO)       //the queue has been full, we have to find one to stop
    {
        id<KSDownloadItem> itemToStop = [self findTheFirstItemToStop];
        if (itemToStop)
        {
            [self stopItem:itemToStop shouldSchedule:NO];
            [itemToStop setDownloadStatus:KS_QUEUING];  //and change it to queueing state
        }
    }
    
    SoftItem *soft = (SoftItem *)item;
    NSString *url = [soft url];
    
    if ([url length] == 0 && soft.increUpateInfo == nil)
    {
        [item setDownloadStatus:KS_INITIALIZING];
        self.concurrentCount++;
        [self startInitializeTaskForDownloadItem:item];
        [self itemDidStartInitialize:item];
    }
    else
    {
        if (status != KS_INITIALIZING)
        {
            self.concurrentCount++;
        }
        [item setDownloadStatus:KS_DOWNLOADING];
        [self startDownloadForItem:item];
        [self itemDidStart:item];
    }
}

#pragma mark methods for subclass

- (BOOL)shouldReplaceExistDownloadItem:(id<KSDownloadItem>)existItem withNewItem:(id<KSDownloadItem>)aNewItem
{
    return NO;
}

- (void)startInitializeTaskForDownloadItem:(id<KSDownloadItem>)item
{
//    [self performSelector:@selector(finishInitializeTaskForDownloadItem:) withObject:item afterDelay:0.01]; 
    [self finishInitializeTaskForDownloadItem:item];
}

- (void)startDownloadForItem:(id<KSDownloadItem>)item
{
    NSAssert(1, @"subclass should implement startDownloadForItem method!!");
}

- (void)stopDownloadForItem:(id<KSDownloadItem>)item
{
    NSAssert(1, @"subclass should implement stopDownloadForItem method!!");
}

- (void)downloadItem:(id<KSDownloadItem>)item didFinishWithError:(NSError *)error
{
    SoftItem *soft = (SoftItem *)item;
    if (error == nil)
    {
        [soft setDownloadStatus:KS_FINISHED];
        [self itemDidDownloadSuccess:soft];
        if (soft.increUpateInfo == nil) {
            [self removeItem:soft];
        }
    }
    else
    {
        [soft setDownloadStatus:KS_STOPPED];
        self.concurrentCount--;
        [self itemDidDownloadFail:soft error:error];
    }
    [self schedule];
}

- (id<KSDownloadItem>)findTheFirstItemToStop
{
    id<KSDownloadItem> itemToStop = nil;
    NSArray *all = [self queue];
    for (id<KSDownloadItem> item in all) {
        if ([item downloadStatus] == KS_DOWNLOADING)
        {
            itemToStop = item;
        }
    }
    return itemToStop;
}

- (void)itemDidAddToQueue:(id<KSDownloadItem>)item
{

}

- (void)itemDidStartInitialize:(id<KSDownloadItem>)item
{
    
}

- (void)itemDidFinishInitialize:(id<KSDownloadItem>)item
{

}

- (void)itemDidStart:(id<KSDownloadItem>)item
{

}

- (void)itemDidStop:(id<KSDownloadItem>)item
{

}

- (void)itemDidDownloadSuccess:(id<KSDownloadItem>)item
{

}

- (void)itemDidDownloadFail:(id<KSDownloadItem>)item error:(NSError *)error
{

}

@end
