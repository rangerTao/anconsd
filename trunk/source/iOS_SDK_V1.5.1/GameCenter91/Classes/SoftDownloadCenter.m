//
//  SoftDownloadCenter.m
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-28.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import "SoftDownloadCenter.h"
#import <NetEngine/NDNetHttpTransfer.h>
#import "SoftItem.h"
#import "RequestorAssistant.h"
#import "GetAppDownloadUrloperaion.h"
#import "NSArray+Extent.h"
#import <LOG/NDLogger.h>
#import "AppDownLoadInfo.h"
#import "SoftIncreUpdateModule.h"
#import "CommUtility.h"
//NdComPlatformInt methods
extern NSError *errorWithCodeAndDesc(int errorCode, NSString *errorDesc);

@interface SoftDownloadCenter()<GetAppDownloadUrlProtocol>
@property (nonatomic, retain) NSMutableDictionary *idToHttpHandlers;
@property (nonatomic, retain) NSMutableDictionary *httpHandlerToSoftItems;
@end


@implementation SoftDownloadCenter
@synthesize idToHttpHandlers, httpHandlerToSoftItems;
@synthesize delegate;

- (id) init
{
    self = [super init];
    if (self != nil) {
        self.idToHttpHandlers = [NSMutableDictionary dictionary];
        self.httpHandlerToSoftItems = [NSMutableDictionary dictionary];
        self.delegate = nil;
        
    }
    return self;
}

- (void) dealloc
{
    self.delegate = nil;
    [self stopAllItems];
    self.idToHttpHandlers = nil;
    self.httpHandlerToSoftItems = nil;
    [super dealloc];
}

- (NSArray *)softItemQueue
{
    return [self queue];
}

//- (void)setupDownloadQueue:(NSArray *)itemsToDownload
//{
//    [self stopAllItems];
//    for (SoftItem *itm in itemsToDownload) {
//        [self addItemToQueue:itm startImmediately:NO];
//    }
//}

- (void)start
{
    [self schedule];
}

- (void)startInitializeTaskForDownloadItem:(id<KSDownloadItem>)item
{
    SoftItem *soft = (SoftItem *)item;
    NSString *f_id_str = [NSString stringWithFormat:@"%d", soft.f_id];
    NSDictionary *dic = [NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithInt:soft.f_id], soft.identifier, nil];
    
    NSNumber *num = [RequestorAssistant requestAppDownloadUrlList:dic delegate:self];
    if ([num intValue] < 0)
    {
        NSError *error = errorWithCodeAndDesc([num intValue], @"获取下载地址失败");    
        [self finishInitializeTaskForDownloadItem:item withErorr:error];
    }
    else
    {
        [self.idToHttpHandlers setValue:num forKey:f_id_str];
        [self.httpHandlerToSoftItems setObject:soft forKey:num];    
    }
}

- (void)operation:(GameCenterOperation *)operation getAppDownloadUrlListDidFinish:(NSError *)error downloadUrlList:(NSArray *)downloadAppList
{
    GetAppDownloadUrloperaion *requestOperation = (GetAppDownloadUrloperaion *)operation;
    NSArray *identifierArr = [requestOperation.dic allKeys];
    NSString *identifier = nil;
    int f_id = 0;
    if ([identifierArr count] > 0) {
        identifier = [identifierArr objectAtIndex:0];
        f_id = [[requestOperation.dic objectForKey:identifier] intValue];
    }
    
    SoftItem *item = (SoftItem *)[self itemWithPrimaryKey:identifier];
    if (error)
    {
        [self finishInitializeTaskForDownloadItem:item withErorr:error];
    }
    else
    {
        AppDownLoadInfo *info = [downloadAppList valueAtIndex:0];
        
        //remove record for initializing status
        NSString *f_id_str = [NSString stringWithFormat:@"%d", info.f_id];
        NSNumber *num = [self.idToHttpHandlers objectForKey:f_id_str];
        [self.httpHandlerToSoftItems removeObjectForKey:num];        
        [self.idToHttpHandlers removeObjectForKey:f_id_str];
        
        if (info && [info.downloadUrl length] != 0)
        {
            item.version = info.versionName;
            item.shortVersion = info.versionCode;
            item.identifier = info.identifier;
            item.url = info.downloadUrl;
            item.totalLen = info.packageSize;
            [item generateSaveName];
            [self finishInitializeTaskForDownloadItem:item withErorr:nil];
        }
        else
        {
            NSError *error = errorWithCodeAndDesc(-1, @"获取下载地址失败");
            [self finishInitializeTaskForDownloadItem:item withErorr:error];
        }
    }
}

- (void)startDownloadForItem:(id<KSDownloadItem>)item
{
    SoftItem *soft = (SoftItem *)item;
    NSString *fileName = [soft absoluteFilePath];

    //判断是否先下载文件清单 更新用
    if (soft.url == nil) {
        soft.url = soft.increUpateInfo.filelistPackageUrl;
        soft.increUpateInfo.isFilelistPackage = YES;
        fileName = [SoftIncreUpdateModule requiredFileListPackageNameForSoft:soft.identifier];
    }else if(soft.increUpateInfo != nil && soft.increUpateInfo.isIncrePackage)
    {
        fileName = [SoftIncreUpdateModule requiredIncrementPackageNameForSoft:soft.identifier];
    }
        
    NDNetHttpTransferIDRef ref = -1;
  
    ref = [[NDNetHttpTransfer sharedTransfer] getFile:[soft url] fileName:fileName allowOverWrite:YES delegate:self];
    if (ref >= 0)
    {
        [self.idToHttpHandlers setValue:[NSNumber numberWithLong:ref] forKey:soft.identifier];
        [self.httpHandlerToSoftItems setObject:soft forKey:[NSNumber numberWithLong:ref]];
    }    
    else
    {
        NSError *error = errorWithCodeAndDesc(ref, @"下载失败");
        [self finishInitializeTaskForDownloadItem:soft withErorr:error];
    }
}

- (void)stopDownloadForItem:(id<KSDownloadItem>)item
{
    if ([[item primaryKey] length] == 0)
        return;
    
    SoftItem *soft = (SoftItem *)item;
    NSNumber *num = [self.idToHttpHandlers objectForKey:soft.identifier];
    if (num)
    {
        if ([item downloadStatus] == KS_INITIALIZING)
        {
            [[RequestorAssistant sharedInstance] cancelOperation:num];
        }
        else
        {
            NDNetHttpTransferIDRef ref = [num longValue];
            [[NDNetHttpTransfer sharedTransfer] cancelConnection:ref];            
        }
        
        [self.httpHandlerToSoftItems removeObjectForKey:num];        
        [self.idToHttpHandlers removeObjectForKey:soft.identifier];
    }        
}


#pragma makr 下载回调
//- (void)transfer:(NDNetHttpTransferIDRef)connection didReceiveResponse:(NSURLResponse *)response
//{
//    NSNumber *num = [NSNumber numberWithLong:connection];
//    SoftItem *item = [self.httpHandlerToSoftItems objectForKey:num];
//    if (item)
//    {
//        long long len = [response expectedContentLength];        
//        if (self.delegate && [self.delegate respondsToSelector:@selector(item:lengthDownloaded:total:)])
//        {
//            [self.delegate item:item lengthDownloaded:0 total:len];
//        }
//    }        
//}

- (void)transfer:(NDNetHttpTransferIDRef)connection didReceiveData:(unsigned long)receivedLen expectedTotalLen:(unsigned long)expectedTotalLen
{
    NSNumber *num = [NSNumber numberWithLong:connection];
    SoftItem *item = [self.httpHandlerToSoftItems objectForKey:num];
    if (item)
    {
        item.downloadedLen = receivedLen;
        item.totalLen = expectedTotalLen;
        if (item.increUpateInfo != nil && item.increUpateInfo.isFilelistPackage) {
            item.totalLen = 0;
        }
        
        //如果是下载filelist不显示进度

        if (self.delegate && [self.delegate respondsToSelector:@selector(item:lengthDownloaded:total:)] && !item.increUpateInfo.isFilelistPackage)
        {
            [self.delegate item:item lengthDownloaded:receivedLen total:expectedTotalLen];
        }
    }    
    
}

- (void)transferDidFinishLoading:(NDNetHttpTransferIDRef)connection
{
    NSNumber *num = [NSNumber numberWithLong:connection];
    SoftItem *item = [self.httpHandlerToSoftItems objectForKey:num];
    if (item)
    {
        item.timeStamp = [[NSDate date] timeIntervalSince1970];
        [self.httpHandlerToSoftItems removeObjectForKey:num];
        [self.idToHttpHandlers removeObjectForKey:item.identifier];
        
        [self downloadItem:item didFinishWithError:nil];                
    }
}

- (void)transfer:(NDNetHttpTransferIDRef)connection didFailWithError:(NSError*)error
{
    NSNumber *num = [NSNumber numberWithLong:connection];
    SoftItem *item = [self.httpHandlerToSoftItems objectForKey:num];
    
    NDLOG(@"download item %@ fail, error %@", [item softName], error);
    [self.httpHandlerToSoftItems removeObjectForKey:num];
    if (item.identifier != nil) {
        [self.idToHttpHandlers removeObjectForKey:item.identifier];
    }
    
    [self downloadItem:item didFinishWithError:error];
    
    
}

- (void)itemDidAddToQueue:(id<KSDownloadItem>)item
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(downloadQueueDidAddItem:)])
        [self.delegate downloadQueueDidAddItem:item];
}

- (void)itemDidStartInitialize:(id<KSDownloadItem>)item
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(downloadQueueDidStartInitItem:)])
        [self.delegate downloadQueueDidStartInitItem:item];
}

- (void)itemDidFinishInitialize:(id<KSDownloadItem>)item
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(downloadQueueDidFinishInitItem:)])
        [self.delegate downloadQueueDidFinishInitItem:item];    
}

- (void)itemDidStart:(id<KSDownloadItem>)item
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(downloadQueueDidStartItem:)])
        [self.delegate downloadQueueDidStartItem:item];
}

- (void)itemDidStop:(id<KSDownloadItem>)item
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(downloadQueueDidStopItem:)])
        [self.delegate downloadQueueDidStopItem:item];
}

- (void)itemDidDownloadSuccess:(id<KSDownloadItem>)item
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(downloadQueueDidFinishItem:)])
        [self.delegate downloadQueueDidFinishItem:item];
}

- (void)itemDidDownloadFail:(id<KSDownloadItem>)item error:(NSError *)error
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(downloadQueueDidFailItem:error:)])
        [self.delegate downloadQueueDidFailItem:item error:error];
}

@end
