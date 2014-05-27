//
//  SoftManagementCenter.h
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-21.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SynthesizeSingleton.h"

@class SoftItem;

typedef enum _QUEUE_STATE_CHANGE
{
    ITEM_ADDED = 0,
    ITEM_REMOVED,
    ITEM_START_INIT,
    ITEM_END_INIT,
    ITEM_START,
    ITEM_STOP,
    ITEM_FINISHED,
    ITEM_FAILED,
    ITEM_INSTALLED,
    ITEM_UNINSTALLED,
    ITEM_INSTALLED_FAIL,
}QUEUE_STATE_CHANGE;

@interface SoftManagementCenter : NSObject
@property (nonatomic, assign) int maxDownloadCount;

- (void)prepareToWorkWithLocalInstalledApps:(NSArray *)appList;
- (void)updateLocalInstalledAppsAfterStartup:(NSArray *)appList;
- (void)saveWork;

- (NSArray *)installed91SDKSoft;

- (void)updateUpdatableInfo:(NSArray *)updatable;
- (void)increUpdatableDic:(SoftItem *)item;//往updatableAppsDict塞softitem
- (void)increInstalledDic:(SoftItem *)item;//往installedAppsDict塞softitem

- (NSArray *)downloadedSoftItemList;
- (NSArray *)downloadingSoftItemList;
- (NSArray *)updatingSoftItemList;
- (NSArray *)updatableSoftItemList;

- (int)updatableCount;             //count used to represent updatable count on appIcon(bubble representation)

- (void)startTask:(NSString *)identifier f_id:(int)f_id softName:(NSString *)softName iconUrl:(NSString *)iconUrl;
- (void)startTask:(NSString *)identifier;
- (void)stopTask:(NSString *)identifier;
- (void)removeTask:(NSString *)identifier; 
- (void)updateTask:(NSString *)identifier;
- (void)cancelTask:(NSString *)identifier;

- (SoftItem *)softItemForIdentifier:(NSString *)identifier;
- (SoftItem *)updatableSoftItemForIdentifier: (NSString *)identifier;
- (BOOL)isAnInstalledSoftItem:(SoftItem *)item;
- (BOOL)isAnInstalledGame:(NSString *)identifier;
- (BOOL)isAnUpdableSoftItem:(SoftItem *)item;
- (BOOL)haveIncreInfoForSoftItem:(SoftItem *)item;

- (void)open:(SoftItem *)item;
- (BOOL)install:(NSString *)identifier;
- (void)doInstallWithLoading:(SoftItem *)item;
- (BOOL)uninstall:(SoftItem *)item;

SYNTHESIZE_SINGLETON_FOR_CLASS_HEADER(SoftManagementCenter)
@end
