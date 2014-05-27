//
//  SoftInstallItemQueue.m
//  GameCenter91
//
//  Created by hiyo on 12-11-13.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "SoftInstallItemQueue.h"

#define KSDQ_LOG    NSLog

@interface SoftInstallItemQueue()
@property (nonatomic, retain) NSMutableDictionary *installingQueue;
@property (nonatomic, assign) int concurrentCount;

- (void)schedule;
- (void)stopAllItems;
- (void)startItem:(id<KSDownloadItem>)item force:(BOOL)force;
@end

@implementation SoftInstallItemQueue
@synthesize maxConcurrent;
@synthesize installingQueue;
@synthesize concurrentCount;

- (id)init {
    self = [super init];
    if (self) {
        self.installingQueue = [NSMutableDictionary dictionary];
        self.concurrentCount = 0;
        self.maxConcurrent = -1;
    }
    return self;
}

- (void)dealloc {
    [self stopAllItems];
    self.installingQueue = nil;
    [super dealloc];
}

- (id<KSDownloadItem>)itemWithPrimaryKey:(NSString *)primaryKey
{
    if ([primaryKey length] == 0)
        return nil;
    return [self.installingQueue objectForKey:primaryKey];
}

- (void)setupQueue:(NSArray *)itemArray
{
    for (id<KSDownloadItem> item in itemArray)
    {
        NSString *primaryKey = [item primaryKey];
        if ([primaryKey length] == 0)
            continue;
        
        if ([item installStatus] == INSTALL_INITIALIZING || [item installStatus] == INSTALL_INSTALLING)
            [item setInstallStatus:INSTALL_QUEUING];
        [self.installingQueue setObject:item forKey:primaryKey];
    }
}

- (BOOL)addItemToQueue:(id<KSDownloadItem>)item;                          
{
    BOOL bStartImmediately = YES;
    NSArray *all = [self queue];    
    for (id<KSDownloadItem> item in all) {
        if ([item installStatus] == INSTALL_INSTALLING)
        {
            bStartImmediately = NO;
            break;
        }
    }
    return [self addItemToQueue:item startImmediately:bStartImmediately];
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
    id<KSDownloadItem> exist = [self.installingQueue objectForKey:primaryKey];
    if (exist)
    {
        BOOL shouldReplace = [self shouldReplaceExistInstallItem:exist withNewItem:item];        
        if (shouldReplace == NO)
        {
            return NO;              
        }
        else
        {
            [self removeItem:exist removeFile:YES];
        }
    }
    
    [item setInstallStatus:INSTALL_QUEUING];
    [self.installingQueue setObject:item forKey:primaryKey];
	[self itemDidAddToQueue:item];
	
    if (startImmediately == YES)
    {
        [self startItem:item];
    }
    
    //    [self schedule];
    return YES;
}

- (void)finishInitializeTaskForInstallItem:(id<KSDownloadItem>)item
{
    [self finishInitializeTaskForInstallItem:item withErorr:nil];
}

- (void)finishInitializeTaskForInstallItem:(id<KSDownloadItem>)item withErorr:(NSError *)error
{
    if (error)
    {
        [item setInstallStatus:INSTALL_STOPPED];
        [self installItem:item didFinishWithError:error];
    }
    else
    {
        [self itemDidFinishInitialize:item];        
        [self startItem:item];
    }
}

- (void)stopAllItems
{
    for (NSString *key in self.installingQueue) {
        id<KSDownloadItem> item = [self.installingQueue objectForKey:key];
        [self stopItem:item];
    }
}

- (void)stopItem:(id<KSDownloadItem>)item
{
    NSString *primaryKey = [item primaryKey];
    if ([self.installingQueue objectForKey:primaryKey] == nil)
        return;
    
    int status = [item installStatus];
    if (status == INSTALL_INSTALLING || status == INSTALL_INITIALIZING)
    {
        [self stopInstallForItem:item];
    }
    
    if (status != INSTALL_FINISHED)
    {
        [item setInstallStatus:INSTALL_STOPPED];        
    }
}

- (void)removeItem:(id<KSDownloadItem>)item removeFile:(BOOL)removeFile
{
    [self stopItem:item];
    if (removeFile)
    {
        NSString *file = [[item savePath] stringByAppendingPathComponent:[item fileName]];
        if (file)
        {
            [[NSFileManager defaultManager] removeItemAtPath:file error:nil];
        }
    }
    [self.installingQueue removeObjectForKey:[item primaryKey]];
}

- (void)removeItem:(id<KSDownloadItem>)item
{
    [self removeItem:item removeFile:NO];
}

- (NSArray *)queue
{
    NSArray *all = [self.installingQueue allValues];
    NSSortDescriptor *descriptor = [NSSortDescriptor sortDescriptorWithKey:@"timeStamp" ascending:NO];
    all = [all sortedArrayUsingDescriptors:[NSArray arrayWithObject:descriptor]];
    return all;
}

- (void)schedule
{
    NSArray *all = [self queue];    
    for (id<KSDownloadItem> item in all) {
        if ([item installStatus] == INSTALL_QUEUING)
        {
            [self startItem:item];
            break;
        }
    }
}

- (void)startItem:(id<KSDownloadItem>)item
{
    [self startItem:item force:YES];
}

- (void)startItem:(id<KSDownloadItem>)item force:(BOOL)force
{
    int status = [item installStatus];
    BOOL canRun = (self.maxConcurrent <= 0) ? YES : (self.concurrentCount < self.maxConcurrent);
    if (status == INSTALL_INITIALIZING)
        canRun = YES;
    
    if (canRun == NO && force == NO)    //the queue has been full, not allowed to run
        return;
    
    if (canRun == NO)       //the queue has been full, we have to find one to stop
    {
        id<KSDownloadItem> itemToStop = [self findTheFirstItemToStop];
        if (itemToStop)
        {
            [self stopItem:itemToStop];
            self.concurrentCount--;
        }
    }
    
    if (status != INSTALL_INITIALIZING)
    {
        self.concurrentCount++;
    }
    [item setInstallStatus:INSTALL_INSTALLING];
    [self startInstallForItem:item];
}

#pragma mark methods for subclass

- (BOOL)shouldReplaceExistInstallItem:(id<KSDownloadItem>)existItem withNewItem:(id<KSDownloadItem>)aNewItem
{
    return NO;
}

- (void)startInitializeTaskForInstallItem:(id<KSDownloadItem>)item
{
    //    [self performSelector:@selector(finishInitializeTaskForDownloadItem:) withObject:item afterDelay:0.01]; 
    [self finishInitializeTaskForInstallItem:item];
}

- (void)startInstallForItem:(id<KSDownloadItem>)item
{
    NSAssert(1, @"subclass should implement startInstallForItem method!!");
}

- (void)stopInstallForItem:(id<KSDownloadItem>)item
{
    NSAssert(1, @"subclass should implement stopInstallForItem method!!");
}

- (void)installItem:(id<KSDownloadItem>)item didFinishWithError:(NSError *)error
{
    if (error == nil)
    {
        [item setInstallStatus:INSTALL_FINISHED];
        [self itemDidInstallSuccess:item];
        [self removeItem:item];
    }
    else
    {
		[item setInstallStatus:INSTALL_DEFAULT_STATE];
		[self itemDidInstallFail:item];
        [self.installingQueue removeObjectForKey:[item primaryKey]];
    }
    [self schedule];
}

- (id<KSDownloadItem>)findTheFirstItemToStop
{
    id<KSDownloadItem> itemToStop = nil;
    NSArray *all = [self queue];
    for (id<KSDownloadItem> item in all) {
        if ([item installStatus] == INSTALL_INSTALLING)
        {
            itemToStop = item;
        }
    }
    return itemToStop;
}

- (void)itemDidAddToQueue:(id<KSDownloadItem>)item
{
    
}

- (void)itemDidFinishInitialize:(id<KSDownloadItem>)item
{
    
}

- (void)itemDidInstallSuccess:(id<KSDownloadItem>)item
{
    
}

- (void)itemDidInstallFail:(id<KSDownloadItem>)item
{
    
}

@end
