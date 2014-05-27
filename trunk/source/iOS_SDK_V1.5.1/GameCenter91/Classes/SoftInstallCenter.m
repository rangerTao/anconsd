//
//  SoftInstallCenter.m
//  GameCenter91
//
//  Created by hiyo on 12-11-13.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "SoftInstallCenter.h"
#import "MIUtility.h"
#import "CommUtility.h"
#import "SoftItem.h"
#import <Log/NDLogger.h>

@implementation SoftInstallCenter
@synthesize delegate;

- (id) init
{
    self = [super init];
    if (self != nil) {
        self.delegate = nil;
    }
    return self;
}

- (void) dealloc
{
    self.delegate = nil;
    [self stopAllItems];
    [super dealloc];
}

- (NSArray *)softItemQueue
{
    return [self queue];
}

- (void)start
{
    [self schedule];
}

- (void)startInstallForItem:(id<KSDownloadItem>)item
{
    SoftItem *soft = (SoftItem *)item;
    NSString *filePath = soft.increInstallPackagePath;
    if (!filePath) {
        filePath = [soft absoluteFilePath];
    }

    NDLOG(@"start!!!!!!!!!!!!!");
    static dispatch_queue_t install_queue = NULL;
    if (!install_queue) {
        install_queue = dispatch_queue_create("NdAppPromot.installqueue", NULL);
    }
    dispatch_async(install_queue, ^{
        NDLOG(@"path = %@", filePath);
        BOOL res = [MIUtility install:filePath error:nil];
        dispatch_async(dispatch_get_main_queue(), ^{
            NDLOG(@"main_queue!!!!!!!!!!!!!");
            if (res)
            {    
                [self installItem:item didFinishWithError:nil];
                
                //将游戏中心中的渠道文件覆盖到安装的游戏里
                [CommUtility copyChannelFileToInstalledApp:soft.identifier];
            }
            else {
                [self installItem:item didFinishWithError:[NSError errorWithDomain:@"ssss" code:-1 userInfo:nil]];
            }
        });
        
    });
    NDLOG(@"end!!!!!!!!!!!!!");
}

- (void)stopInstallForItem:(id<KSDownloadItem>)item
{
    if ([[item primaryKey] length] == 0)
        return;
    
    SoftItem *soft = (SoftItem *)item;
    [self installItem:soft didFinishWithError:[NSError errorWithDomain:nil code:-1 userInfo:nil]]; 
}


#pragma makr 安装回调

- (void)itemDidAddToQueue:(id<KSDownloadItem>)item
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(installQueueDidAddItem:)])
        [self.delegate installQueueDidAddItem:item];
}

- (void)itemDidFinishInitialize:(id<KSDownloadItem>)item
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(installQueueDidFinishInitItem:)])
        [self.delegate installQueueDidFinishInitItem:item];    
}

- (void)itemDidInstallSuccess:(id<KSDownloadItem>)item
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(installQueueDidFinishItem:)])
        [self.delegate installQueueDidFinishItem:item];
}

- (void)itemDidInstallFail:(id<KSDownloadItem>)item
{
    if (self.delegate && [self.delegate respondsToSelector:@selector(installQueueDidFailItem:)])
        [self.delegate installQueueDidFailItem:item];
}

@end