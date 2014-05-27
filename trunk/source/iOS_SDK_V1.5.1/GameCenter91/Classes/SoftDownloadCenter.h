//
//  SoftDownloadCenter.h
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-28.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KSDownloadQueue.h"

@class SoftItem;
@protocol SoftDownloadCenterDelegate;

@interface SoftDownloadCenter : KSDownloadQueue

@property (nonatomic, readonly) NSArray *softItemQueue;
@property (nonatomic, assign) id<SoftDownloadCenterDelegate> delegate;

//- (void)setupDownloadQueue:(NSArray *)itemsToDownload;

- (void)start;


@end


@protocol SoftDownloadCenterDelegate<NSObject>
@optional
- (void)downloadQueueDidAddItem:(SoftItem *)item;
- (void)downloadQueueDidStartInitItem:(SoftItem *)item;
- (void)downloadQueueDidFinishInitItem:(SoftItem *)item;
- (void)downloadQueueDidStartItem:(SoftItem *)item;
- (void)downloadQueueDidStopItem:(SoftItem *)item;
- (void)downloadQueueDidFinishItem:(SoftItem *)item;
- (void)downloadQueueDidFailItem:(SoftItem *)item error:(NSError *)error;

- (void)item:(SoftItem *)item lengthDownloaded:(long long)downloaded total:(long long)total;
@end