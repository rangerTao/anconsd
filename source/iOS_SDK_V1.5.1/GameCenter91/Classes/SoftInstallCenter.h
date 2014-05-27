//
//  SoftInstallCenter.h
//  GameCenter91
//
//  Created by hiyo on 12-11-13.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SoftInstallItemQueue.h"

@class SoftItem;
@protocol SoftInstallCenterDelegate;

@interface SoftInstallCenter :  SoftInstallItemQueue

@property (nonatomic, readonly) NSArray *softItemQueue;
@property (nonatomic, assign) id<SoftInstallCenterDelegate> delegate;

//- (void)setupDownloadQueue:(NSArray *)itemsToDownload;

- (void)start;


@end


@protocol SoftInstallCenterDelegate<NSObject>
@optional
- (void)installQueueDidAddItem:(SoftItem *)item;
- (void)installQueueDidFinishInitItem:(SoftItem *)item;
- (void)installQueueDidFinishItem:(SoftItem *)item;
- (void)installQueueDidFailItem:(SoftItem *)item;
@end