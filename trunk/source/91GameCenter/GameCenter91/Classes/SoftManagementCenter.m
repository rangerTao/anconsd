//
//  SoftManagementCenter.m
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-21.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import "SoftManagementCenter.h"
#import "SoftItem.h"
#import <Log/NDLogger.h>
#import "MIUtility.h"
#import <NetEngine/NDNetHttpTransfer.h>
#import <NdComPlatform/NdComPlatform.h>
#import "SoftDownloadCenter.h"
#import "SoftInstallCenter.h"
#import "Notifications.h"
#import "UserData.h"
#import "RequestorAssistant.h"
#import "NSArray+Extent.h"
#import "CommUtility.h"
#import "DatabaseUtility.h"
#import "RIButtonItem.h"
#import "UIAlertView+Blocks.h"
#import "MBProgressHUD.h"
#import "CustomAlertView.h"
#import "StatusBarNotification.h"
#import "AppInfo.h"
#import "SoftIncreUpdateModule.h"
#import "ReportCenter.h"
#import "WifiCheckManager.h"

#define SOFT_INFO_KEY               @"SoftInfo"
#define DOWNLOADING_KEY             @"Downloading"
#define DOWNLOADED_KEY              @"Downloaded"
#define UPDATABLE_KEY               @"Updatable"
#define INSTALLING_KEY              @"Installing"

#define SAVE_WORK_INTERVAL          10

@interface SoftManagementCenter()<SoftDownloadCenterDelegate,SoftInstallCenterDelegate, GetGamesFilteredProtocol>

@property (nonatomic, retain) NSUserDefaults *managerDefault;
@property (nonatomic, retain) SoftDownloadCenter *downloadCenter;
@property (nonatomic, retain) SoftInstallCenter *installCenter;

@property (nonatomic, retain) NSMutableDictionary *downloadingAppsDict;
@property (nonatomic, retain) NSMutableDictionary *downloadedAppsDict;
@property (nonatomic, retain) NSMutableDictionary *installingAppsDict;
@property (nonatomic, retain) NSMutableDictionary *installedAppsDict;
@property (nonatomic, retain) NSMutableDictionary *updatableAppsDict;


- (SoftItem *)itemFromSerializedDictionary:(NSDictionary *)dict;
- (NSDictionary *)serializedDictionaryFromItem:(SoftItem *)item;

- (NSDictionary *)serializedItemsInDictionary:(NSDictionary *)dict;

- (NSMutableDictionary *)deserializedDownloadingDict;
- (NSDictionary *)serializedDownloadingDict;

- (NSMutableDictionary *)deserializedDownloadedDict;
- (NSDictionary *)serializedDownloadedDict;

- (NSMutableDictionary *)deserializedUpdatableDict;
- (NSDictionary *)serializedUpdatableDict;

- (NSMutableDictionary *)deserializedInstallingDict;
- (NSDictionary *)serializedInstallingDict;

- (BOOL)item:(SoftItem *)itemA isNewerThan:(SoftItem *)itemB;
- (void)updateUpdatableInfo:(NSArray *)updatable needNotify:(BOOL)needNotify;

- (void)downloadQueueStatusDidChange:(SoftItem *)item state:(QUEUE_STATE_CHANGE)state;
- (void)updateQueueStatusDidChange:(SoftItem *)item state:(QUEUE_STATE_CHANGE)state;

- (void)askToInstall:(SoftItem *)item;
- (void)doInstallWithLoading:(SoftItem *)item;
@end


@implementation SoftManagementCenter

@synthesize maxDownloadCount;
@synthesize managerDefault;
@synthesize downloadCenter, installCenter;

@synthesize installedAppsDict, installingAppsDict, updatableAppsDict, downloadingAppsDict, downloadedAppsDict;

SYNTHESIZE_SINGLETON_FOR_CLASS(SoftManagementCenter)

- (id) init
{
    self = [super init];
    if (self != nil) {
        self.maxDownloadCount = -1;
        self.managerDefault = [[[NSUserDefaults alloc] initWithUser:NSStringFromClass([self class])] autorelease];        
        self.downloadCenter = [[SoftDownloadCenter new] autorelease];
        self.downloadCenter.maxConcurrent = self.maxDownloadCount;
        self.downloadCenter.delegate = self;
        self.installCenter = [[SoftInstallCenter new] autorelease];
        self.installCenter.delegate = self;
//        [NSTimer timerWithTimeInterval:SAVE_WORK_INTERVAL target:self selector:@selector(saveWork) userInfo:nil repeats:YES];
    }
    return self;
}

- (void) dealloc
{
    [self.downloadCenter stopAllItems];
    self.downloadCenter = nil;
    
    [self.installCenter stopAllItems];
    self.installCenter = nil;
        
    [self.managerDefault synchronize];
    self.managerDefault = nil;
    [super dealloc];
}

#pragma mark -
- (void)prepareToWorkWithLocalInstalledApps:(NSArray *)appList
{
    [self updateLocalInstalledWithApplist:appList];
    
    self.downloadingAppsDict = [self deserializedDownloadingDict];
    self.downloadedAppsDict = [self deserializedDownloadedDict];
    self.updatableAppsDict = [self deserializedUpdatableDict];
    self.installingAppsDict = [self deserializedInstallingDict];
    
    [self.downloadCenter setupQueue:[self.downloadingAppsDict allValues] state:KS_STOPPED];
    [self.installCenter setupQueue:[self.installingAppsDict allValues]];
    
    //增加检测到Wifi网络自动继续下载功能（启动）
    [self continueDownloadIfWifiOk];
}

- (void)updateLocalInstalledAppsAfterStartup:(NSArray *)appList
{
    [self updateLocalInstalledWithApplist:appList];
}

- (void)saveWork
{
    NSDictionary *allDownloading = [self serializedDownloadingDict];
    NSDictionary *allDownloaded = [self serializedDownloadedDict];
    NSDictionary *allInstalling = [self serializedInstallingDict];
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithCapacity:2];
    [dict setValue:allDownloading forKey:DOWNLOADING_KEY];
    [dict setValue:allDownloaded forKey:DOWNLOADED_KEY];
    [dict setValue:allInstalling forKey:INSTALLING_KEY];
    
    [self.managerDefault setValue:dict forKey:SOFT_INFO_KEY];
    [self.managerDefault synchronize];
}

- (void)updateLocalInstalledWithApplist:(NSArray *)appList
{
    self.installedAppsDict = [NSMutableDictionary dictionary];
    
    for (AppInfo *info in appList) {
        
        //版本信息本地找
        NSString *ver = [CommUtility getInstallAppVerByIdentifier:info.identifier];
        NSString *shortVer = [CommUtility getInstallAppShortVerByIdentifier:info.identifier];
        
        SoftItem *item = [[SoftItem new] autorelease];
        item.identifier = info.identifier;
        item.localVersion = ver;
        item.localShortVersion = shortVer;
        item.version = ver;
        item.localShortVersion = shortVer;
        item.iconPath = info.appIconUrl;
        item.softName = info.appName;
        item.downloadStatus = KS_FINISHED;
        item.f_id = info.f_id;
        
        [self.installedAppsDict setObject:item forKey:item.identifier];
    }
}

- (void)notifySmartUpdateFailed:(SoftItem *)item
{
    [[NSNotificationCenter defaultCenter] postNotificationName:kGC91SmartUpdateFailedNotification object:item userInfo:nil];
}
#pragma mark -

- (NSString *)getInstalledIconPathFromSystemInfo:(NSDictionary *)installedInfo
{
    NSString *iconPath = nil;
    NSArray *icons = [installedInfo objectForKey:@"CFBundleIconFiles"];
    if ([icons count] > 0)
    {
        NSString *icon = [icons objectAtIndex:0];
        NSString *path =  [[installedInfo objectForKey:@"Path"] stringByAppendingPathComponent:icon];
        if ([[NSFileManager defaultManager] fileExistsAtPath:path] == YES)
            iconPath = path;
    }
    return iconPath;
}

- (NSString *)getInstalledSoftNameFromSystemInfo:(NSDictionary *)installedInfo
{
    NSString *path = [installedInfo objectForKey:@"Path"];
    BOOL hasLocalTitle = NO;
    
    NSArray *chineseArray = [NSArray arrayWithObjects:@"zh_CN.lproj/InfoPlist.strings", @"zh-CN.lproj/InfoPlist.strings", @"zh-Hans.lproj/InfoPlist.strings", nil];
    
    NSString *chineseLocalPath = nil;
    for (NSString *subPath in chineseArray)
    {
        chineseLocalPath = [path stringByAppendingPathComponent:subPath];
        if ([[NSFileManager defaultManager] fileExistsAtPath:chineseLocalPath] == YES)
        {
            hasLocalTitle = YES;
            break;
        }
    }
            
    if (hasLocalTitle == YES)
    {
        NSDictionary *infoDict = [NSDictionary dictionaryWithContentsOfFile:chineseLocalPath];
        NSString *title = [infoDict objectForKey:@"CFBundleDisplayName"];
        if ([title length] != 0)
            return title;
    }        
    
    return [installedInfo objectForKey:@"CFBundleDisplayName"];
}

- (void)updateUpdatableInfo:(NSArray *)updatable needNotify:(BOOL)needNotify
{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    for (SoftItem *item in updatable) {
        [dict setObject:item forKey:item.identifier];
    }
    self.updatableAppsDict = dict;
    NSDictionary *info = [self serializedUpdatableDict];
    [self.managerDefault setObject:info forKey:UPDATABLE_KEY];
    
    if (needNotify)
    {
        [[NSNotificationCenter defaultCenter] postNotificationName:kGC91UpdateQueueChangeNotification object:self userInfo:nil];
    }
}

- (void)updateUpdatableInfo:(NSArray *)updatable 
{
    [self updateUpdatableInfo:updatable needNotify:YES];
}

- (void)increUpdatableDic:(SoftItem *)item
{
    SoftItem *soft = [self.updatableAppsDict objectForKey:item.identifier];
    if (soft == nil) {
        [self.updatableAppsDict setObject:item forKey:item.identifier];
//        NSDictionary *info = [self serializedUpdatableDict];
//        [self.managerDefault setObject:info forKey:UPDATABLE_KEY];
    }
}

- (void)increInstalledDic:(SoftItem *)item
{
    SoftItem *soft = [self.installedAppsDict objectForKey:item.identifier];
    if (soft == nil) {
        [self.installedAppsDict setObject:item forKey:item.identifier];
    }
}

- (NSArray *)downloadedSoftItemList
{
    NSMutableArray *downloadedNotInstalled = [NSMutableArray array];
    NSMutableArray *downloadedAndInstalled = [NSMutableArray array];
    NSMutableArray *otherInstalled = [NSMutableArray arrayWithArray:[self.installedAppsDict allValues]];
        
    NSDictionary *dict = [NSDictionary dictionaryWithDictionary:self.downloadedAppsDict];
    
    for (NSString *appIdString in dict)
    {
        SoftItem *item = [dict objectForKey:appIdString];
        if ([item downloadStatus] != KS_FINISHED)
            continue;
        
        SoftItem *exist = [self.installedAppsDict objectForKey:appIdString];
        if (exist != nil)
        {
            if ([self item:item isNewerThan:exist] && [item fileExist])
            {
                [downloadedNotInstalled addObject:item];
                [otherInstalled removeObject:exist];
            }
            else
            {
                [downloadedAndInstalled addObject:item];
                [otherInstalled removeObject:exist];
            }
        }
        else if ([item fileExist])
        {
            [downloadedNotInstalled addObject:item];
        }
        else
        {
            [self.downloadedAppsDict removeObjectForKey:appIdString];
        }
    }
    
    [downloadedNotInstalled sortWithKey:@"timeStamp" ascending:NO];
    [downloadedAndInstalled sortWithKey:@"timeStamp" ascending:NO];
    [otherInstalled sortWithKey:@"softName" ascending:YES];
    
    NSMutableArray *list = [NSMutableArray arrayWithCapacity:[downloadedNotInstalled count] + [downloadedAndInstalled count] + [otherInstalled count]];
    [list addObjectsFromArray:downloadedNotInstalled];
    [list addObjectsFromArray:downloadedAndInstalled];
    [list addObjectsFromArray:otherInstalled];
    return list;    
}

- (int)transverStringToVersionInt:(NSString *)str
{
    int len = [str length];
    if (len == 0)
        return 0;
    int i = 0;
    const char *p = [str UTF8String];
    for (i = 0; i < len; i++)
    {
        if (p[i] != 0)
            break;
    }
    
    if (i == len)
        return 0;
    return atoi(&p[i]);
}


- (int)version:(NSString *)versionA compareToVersion:(NSString *)versionB
{
    NSArray *arrA = [versionA componentsSeparatedByString:@"."];
    NSArray *arrB = [versionB componentsSeparatedByString:@"."];
    int count = MAX([arrA count], [arrB count]);
    for (int i = 0; i < count; i++)
    {
        NSString *verA = [arrA valueAtIndex:i];
        NSString *verB = [arrB valueAtIndex:i];
        
        int a = [self transverStringToVersionInt:verA];
        int b = [self transverStringToVersionInt:verB];
        if (a > b)
            return 1;
        if (a < b)
            return -1;
    }
    return 0;
}

- (BOOL)item:(SoftItem *)itemA isNewerThan:(SoftItem *)itemB
{
    int resultA = [self version:itemA.version compareToVersion:itemB.version];
    int resultB = [self version:itemA.shortVersion compareToVersion:itemB.shortVersion];
    if (resultA > 0 && resultB > 0)
        return YES;
    return NO;
}

- (NSArray *)downloadingSoftItemList
{
    NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithDictionary:self.downloadingAppsDict];
    for (NSString *key in self.downloadingAppsDict) {
        SoftItem *item = [self.downloadingAppsDict objectForKey:key];
        if (item.downloadStatus == KS_FINISHED)
        {
            [dict removeObjectForKey:key];
            continue;      
        }
                
        SoftItem *local = [self.installedAppsDict objectForKey:key];
        if (local != nil)
        {
            [dict removeObjectForKey:key];
        }
    }
    
    NSArray *arr = [dict allValues];
    return [arr sortedArrayWithKey:@"timeStamp" ascending:NO];
}

- (NSArray *)updatingSoftItemList
{
    NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithDictionary:self.downloadingAppsDict];
    for (NSString *key in self.downloadingAppsDict) {
        SoftItem *item = [self.downloadingAppsDict objectForKey:key];
        if (item.downloadStatus == KS_FINISHED)
        {
            [dict removeObjectForKey:key];
            continue;
        }
        
        SoftItem *local = [self.installedAppsDict objectForKey:key];
        if (local != nil)
        {
            BOOL isHigher = [self item:item isNewerThan:local];
            if (isHigher == NO)     //we are downloading a older verion than local, we'll remove it
            {
                [self.downloadCenter removeItem:item removeFile:YES];
                [dict removeObjectForKey:key];
            }
            else {
                item.localVersion = local.localVersion;
                item.localShortVersion = local.localShortVersion;
                
                item.softName = local.softName;
                item.iconPath = local.iconPath;
                
                [dict setValue:item forKey:key]; //local version
            }
        }
        else
        {
            [dict removeObjectForKey:key];        
        }
    }
    
    NSArray *arr = [dict allValues];
    return [arr sortedArrayWithKey:@"timeStamp" ascending:NO];
}

- (NSArray *)updatableSoftItemList
{
    NSMutableArray *installedArray = [NSMutableArray array];
    NSMutableArray *notInstaledArray = [NSMutableArray array];
    
    for (NSString *appIdString in self.installedAppsDict)
    {
        SoftItem *item = [self.installedAppsDict objectForKey:appIdString];
        
        SoftItem *downloaded = [self.downloadedAppsDict objectForKey:appIdString];
        SoftItem *server = [self.updatableAppsDict objectForKey:appIdString];
        SoftItem *downloading = [self.downloadingAppsDict objectForKey:appIdString];
        
        if (downloading)    //if it has an downloading item, means it's updating, so not an updatable,
            continue;
        
        BOOL isInstalled = NO;  //whether a item update package downloaded, but not installed
        SoftItem *updatable = nil;
        if (downloaded && downloaded.downloadStatus == KS_FINISHED)
        {
            BOOL fileExist = [downloaded fileExist];
            BOOL isHigher = [self item:downloaded isNewerThan:item];
            if (fileExist && isHigher)
            {
                updatable = downloaded;
                isInstalled = NO;
            }
        }
        
        if (updatable == nil)
        {        
            if (server && [self item:server isNewerThan:item])
            {
                updatable = server;
                isInstalled = YES;
            }
        }  
        
        if (updatable)
        {
            SoftItem *upItem = [[SoftItem new] autorelease];
            upItem.f_id = updatable.f_id;;
            upItem.identifier = updatable.identifier;
            upItem.url = updatable.url;
            upItem.totalLen = updatable.totalLen;
            upItem.version = updatable.version;
            upItem.shortVersion = updatable.shortVersion;            
            upItem.fileName = updatable.fileName;
            upItem.savePath = updatable.savePath;
            upItem.downloadStatus = updatable.downloadStatus;
            
            upItem.localVersion = item.localVersion;
            upItem.localShortVersion = item.localShortVersion;
            upItem.softName = item.softName;
            upItem.iconPath = item.iconPath;
            upItem.increUpateInfo = updatable.increUpateInfo;
            upItem.increInstallPackagePath = updatable.increInstallPackagePath;
            upItem.updateUrl = updatable.updateUrl;
            
            if (isInstalled)
            {
                [installedArray addObject:upItem];
            }
            else
            {
                [notInstaledArray addObject:upItem];
            }
        }
    }    
    
    NSMutableArray *array = [NSMutableArray arrayWithArray:notInstaledArray];
    [array addObjectsFromArray:installedArray];
    return array;
}

- (int)updatableCount
{
    int updatingCount = [[self updatingSoftItemList] count];
    NSArray *arr = [self updatableSoftItemList];
    int count = [arr count];
    
// this code is commented, because the product manager decide to count not installed package in to the bubble count
//    for (SoftItem *item in arr)
//    {
//        if ([item fileExist])       //it's an downloaded package but not installed, not count in the updatable bubble count(decided by chenlingjie)
//            count--;
//    }
    return updatingCount + count;
}

//自动继续下载
- (void)doAutoContinueDownload
{
    NSLog(@"WifiCheck: doAutoContinueDownload");
    for (SoftItem *item in [self.downloadingAppsDict allValues]) {
        if (item.isAutoContinueDownload == YES && item.downloadStatus == KS_STOPPED) {
            SoftItem *exist = [self.installedAppsDict objectForKey:item.identifier];
            if (exist) {
                [self updateTask:item.identifier];
            }
            else {
                [self startTask:item.identifier];
            }
        }
    }
}

//增加检测到Wifi网络自动继续下载功能
- (void)continueDownloadIfWifiOk
{
    [[WifiCheckManager sharedInstance] startCheckFor:self wifiOkSelStr:@"doAutoContinueDownload"];
}

//非Wifi下载/升级给提示
- (void)showHintWhenNoWifi:(NSString *)title okTitle:(NSString *)okTitle cancelTitle:(NSString *)cancelTitle okAction:(RISimpleAction)okAction cancelAction:(RISimpleAction)cancelAction
{    
    RIButtonItem *okItem = [RIButtonItem item];
    okItem.label = okTitle;
    okItem.action = okAction;
    
    RIButtonItem *cancelItem = [RIButtonItem item];
    cancelItem.label = cancelTitle;
    cancelItem.action = cancelAction;
    
    CustomAlertView *alert = [[CustomAlertView alloc] initWithTitle:title message:nil cancelButtonItem:nil otherButtonItems:okItem, cancelItem, nil];
    [alert show];
    [alert release];
}

#pragma mark -
- (void)open:(SoftItem *)item
{
    //统计
    if ([item.identifier length] > 0) {
        [ReportCenter report:ANALYTICS_EVENT_15011 label:item.identifier];
    }
    
    BOOL res = [CommUtility openApp:item.identifier];
    if (res == NO)
    {
        [MBProgressHUD showHintHUD:@"该游戏无法启动" message:@"请检查是否已经从游戏中卸载了该游戏" hideAfter:5];
        [self removeTask:item.identifier];
        return;
    }
}

- (void)realStartTask:(NSString *)identifier f_id:(int)f_id softName:(NSString *)softName iconUrl:(NSString *)iconUrl
{
    SoftItem *exist = [self.installedAppsDict objectForKey:identifier];
    if (exist)
        return;
    exist = [self.downloadingAppsDict objectForKey:identifier];
    if (exist)
    {
        if (exist.downloadStatus == KS_FINISHED)
            return;
        else
        {
            exist.isAutoContinueDownload = YES;
            [self.downloadCenter startItem:exist];
            return;
        }
    }
    
    SoftItem *item = [SoftItem itemWithAppIdentifier:identifier softName:softName];
    item.f_id = f_id;
    item.iconPath = iconUrl;
    
    //下载转化率统计
    if ([[ReportCenter sharedInstance].reportIdentifier isEqualToString:identifier]) {
        [ReportCenter report:[ReportCenter sharedInstance].reportNum label:identifier];
    }
    [ReportCenter report:ANALYTICS_EVENT_15005 label:identifier];
    [RequestorAssistant requestUserActivitiesAnalyze:f_id statType:ANALYTICS_DOWNLOAD];
    
    [self.downloadCenter addItemToQueue:item];
}

- (void)startTask:(NSString *)identifier f_id:(int)f_id softName:(NSString *)softName iconUrl:(NSString *)iconUrl
{
    //非Wifi状态
    if (![CommUtility isWifiNetWork] && [CommUtility isNetWorkReachable]) {
        [self showHintWhenNoWifi:@"检测到当前不是WiFi网络，下载将会消耗您的数据流量。\n\n是否继续下载？" okTitle:@"继续下载" cancelTitle:@"取消" okAction:^{
            [self realStartTask:identifier f_id:f_id softName:softName iconUrl:iconUrl];
        } cancelAction:^{
            ;
        }];
    }
    else {
        [self realStartTask:identifier f_id:f_id softName:softName iconUrl:iconUrl];
    }
}

- (void)startTask:(NSString *)identifier
{
    [self startTask:identifier f_id:0 softName:nil iconUrl:nil];
}

- (void)stopTask:(NSString *)identifier
{
    SoftItem *item = [self.downloadingAppsDict objectForKey:identifier];
    item.isAutoContinueDownload = NO;
    [self.downloadCenter stopItem:item];
}

- (void)removeTask:(NSString *)identifier
{
    [CommUtility getInstallPathByCacheFile:identifier];
    SoftItem *item = [self softItemForIdentifier:identifier];
    if (item == nil)
        return;
    
    if ([self isAnInstalledSoftItem:item])
    {
        [self uninstall:item];
    }
    else
    {
        SoftItem *itemToNotfiy = [[item retain] autorelease];
        [self.downloadCenter removeItem:item removeFile:YES];        
        if (item.downloadStatus == KS_FINISHED)
        {
            [self.downloadedAppsDict removeObjectForKey:item.identifier];
        }
        else
        {
            [self.downloadingAppsDict removeObjectForKey:item.identifier];
        }
        [self downloadQueueStatusDidChange:itemToNotfiy state:ITEM_REMOVED];
    }
}

- (void)realUpdateTask:(NSString *)identifier
{
    SoftItem *updatable = [self.updatableAppsDict objectForKey:identifier];
    SoftItem *downloading = [self.downloadingAppsDict objectForKey:identifier];
    SoftItem *exist = [self.installedAppsDict objectForKey:identifier];
    
    if (exist == nil)
        return;
    
    if (updatable == nil && downloading == nil)
        return;
    
    if (downloading)
    {
        downloading.isAutoContinueDownload = YES;
        downloading.localVersion = exist.localVersion;
        downloading.localShortVersion = exist.localShortVersion;
        [self.downloadCenter startItem:downloading];
        return;
    }
    
    SoftItem *item = [[SoftItem new] autorelease];
    item.identifier = updatable.identifier;
    item.version = updatable.version;
    item.shortVersion = updatable.shortVersion;
    item.updateUrl = updatable.updateUrl;
    item.totalLen = updatable.totalLen;
    item.f_id = updatable.f_id;
    item.softName = updatable.softName;
    item.iconPath = updatable.iconPath;
    item.increUpateInfo = updatable.increUpateInfo;
    item.localVersion = exist.localVersion;
    item.localShortVersion = exist.localShortVersion;
    [item generateSaveName];
    if (item.increUpateInfo == nil) {
        item.url = item.updateUrl;
    }
    
    //统计
    [ReportCenter report:ANALYTICS_EVENT_15006 label:identifier];
    [RequestorAssistant requestUserActivitiesAnalyze:exist.f_id statType:ANALYTICS_DOWNLOAD];
    
    [self.downloadCenter addItemToQueue:item];
}

- (void)updateTask:(NSString *)identifier
{
    //非Wifi状态
    if (![CommUtility isWifiNetWork] && [CommUtility isNetWorkReachable]) {
        [self showHintWhenNoWifi:@"检测到当前不是WiFi网络，升级将会消耗您的数据流量。\n\n是否继续升级？" okTitle:@"继续升级" cancelTitle:@"取消" okAction:^{
            [self realUpdateTask:identifier];
        } cancelAction:^{
            ;
        }];
    }
    else {
        [self realUpdateTask:identifier];
    }
}

- (void)cancelTask:(NSString *)identifier
{
    SoftItem *item = [self softItemForIdentifier:identifier];
    if (item == nil)
        return;
    
    SoftItem *itemToNotfiy = [[item retain] autorelease];
    [self.downloadCenter removeItem:item removeFile:YES];
    [self.downloadingAppsDict removeObjectForKey:item.identifier];
    [self updateQueueStatusDidChange:itemToNotfiy state:ITEM_INSTALLED];
}

- (SoftItem *)softItemForIdentifier:(NSString *)identifier
{
    NSString *str = identifier;
    SoftItem *item = [self.downloadingAppsDict objectForKey:str];
    if (item == nil)
        item = [self.downloadedAppsDict objectForKey:str];
    
    if (item == nil)
        item = [self.installedAppsDict objectForKey:str];
    return item;
}

- (SoftItem *)updatableSoftItemForIdentifier: (NSString *)identifier
{
    SoftItem *updatableItem =  [self.updatableAppsDict objectForKey:identifier];
//    SoftItem *item = [self.installedAppsDict objectForKey:identifier];
    if (updatableItem) {
        updatableItem.localVersion = [CommUtility getInstallAppVerByIdentifier:identifier]; //赋值本地版本号
        updatableItem.localShortVersion = [CommUtility getInstallAppShortVerByIdentifier:identifier];
        return updatableItem;
    }
    return nil;
}

- (void)downloadQueueStatusDidChange:(SoftItem *)item state:(QUEUE_STATE_CHANGE)state
{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:item forKey:@"ITEM"];
    [dict setValue:[NSNumber numberWithInt:state] forKey:@"STATE"];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:kGC91DownloadQueueChangeNotification object:self userInfo:dict];

    [self saveWork];
}

- (void)updateQueueStatusDidChange:(SoftItem *)item state:(QUEUE_STATE_CHANGE)state
{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:item forKey:@"ITEM"];
    [dict setValue:[NSNumber numberWithInt:state] forKey:@"STATE"];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:kGC91UpdateQueueChangeNotification object:self userInfo:dict];
    [self saveWork];    
}

- (void)item:(SoftItem *)item lengthDownloaded:(long long)downloaded total:(long long)total
{
    if (downloaded == 0)    //说明刚收到响应，记录文件总长
    {
        [self saveWork];
    }
    NSDictionary *dict = [NSDictionary dictionaryWithObject:item forKey:@"SoftItem"];
    NSString *notifyName = kGC91DownloadPercentChangeNotification;
    if ([self isAnInstalledSoftItem:item])
        notifyName = kGC91UpdatingPercentChangeNotification;
    
    [[NSNotificationCenter defaultCenter] postNotificationName:notifyName object:item userInfo:dict];
}

- (void)downloadQueueDidAddItem:(SoftItem *)item
{
    if (item)
    {
        [self.downloadingAppsDict setObject:item forKey:item.identifier];
        if ([self isAnInstalledSoftItem:item])
        {
            [self updateQueueStatusDidChange:item state:ITEM_ADDED];
        }
        else
        {
            [self downloadQueueStatusDidChange:item state:ITEM_ADDED];
        }
    }
}

- (void)downloadQueueDidFinishInitItem:(SoftItem *)item
{
    [self downloadQueueStatusDidChange:item state:ITEM_END_INIT];
}

- (void)downloadQueueDidStartInitItem:(SoftItem *)item
{
    [self downloadQueueStatusDidChange:item state:ITEM_START_INIT];
}

- (void)downloadQueueDidStartItem:(SoftItem *)item
{
    [self downloadQueueStatusDidChange:item state:ITEM_START];
}

- (void)downloadQueueDidStopItem:(SoftItem *)item
{
    [self downloadQueueStatusDidChange:item state:ITEM_STOP];
}

- (void)downloadQueueDidFinishItem:(SoftItem *)item
{
    BOOL bNeedAnalytics = NO;
    
    [self.downloadingAppsDict removeObjectForKey:item.identifier];
    [self.downloadedAppsDict setObject:item forKey:item.identifier];
    
    if ([self isAnInstalledSoftItem:item])
    {
        if (item.increUpateInfo != nil && item.increUpateInfo.isFilelistPackage) {//filelist下载完成
            BOOL compareSuccess = [SoftIncreUpdateModule isFilelistMatchedForSoft:item.identifier path:[CommUtility getInstallPathByCacheFile:item.identifier]];
            if (compareSuccess) {//文件清单匹配成功
                item.url = item.increUpateInfo.increPackageUrl;
                item.increUpateInfo.isIncrePackage = YES;
                item.increUpateInfo.isFilelistPackage = NO;
                [self.downloadedAppsDict removeObjectForKey:item.identifier];
                [self.downloadCenter removeItem:item removeFile:YES];
                [self.downloadCenter addItemToQueue:item];
            }else
            {//文件清单匹配失败
                item.increUpateInfo.smartUpdateFailed = YES;
                item.url = item.updateUrl;
                [SoftIncreUpdateModule deleteIncrementTmpFileForSoft:item.identifier];
                item.increUpateInfo.isFilelistPackage = NO;
                item.increUpateInfo.isIncrePackage = NO;
                [self.downloadedAppsDict removeObjectForKey:item.identifier];
                [self.downloadCenter removeItem:item removeFile:YES];
                [self.downloadCenter addItemToQueue:item];
                [self notifySmartUpdateFailed:item];

            }
           
            
        } else if (item.increUpateInfo != nil && item.increUpateInfo.isIncrePackage)
           {//增量包下载完成
            [[NSNotificationCenter defaultCenter] postNotificationName:kGC91InstallingNotification object:item userInfo:nil];
            item.increInstallPackagePath = [SoftIncreUpdateModule xdeltaIncrementPackageForSoft:item.identifier path:[CommUtility getInstallPathByCacheFile:item.identifier]];
            if (item.increInstallPackagePath == nil) { //差分失败
                item.increUpateInfo.smartUpdateFailed = YES;
                item.url = item.increUpateInfo.updateUrl;
                [SoftIncreUpdateModule deleteIncrementTmpFileForSoft:item.identifier];
                item.increUpateInfo.isFilelistPackage = NO;
                item.increUpateInfo.isIncrePackage = NO;
                [self.downloadedAppsDict removeObjectForKey:item.identifier];
//                [self updateQueueStatusDidChange:item state:ITEM_FINISHED];
                [self.downloadCenter removeItem:item removeFile:YES];
                [self.downloadCenter addItemToQueue:item];
                [self notifySmartUpdateFailed:item];
            }else
            {
                [self.downloadCenter removeItem:item];
                [self updateQueueStatusDidChange:item state:ITEM_FINISHED];
                [self install:item.identifier];

                bNeedAnalytics = YES;
            }

        } else
        {
            [self updateQueueStatusDidChange:item state:ITEM_FINISHED];
            [self install:item.identifier];

            bNeedAnalytics = YES;
        }

    }
    else
    {
        [self downloadQueueStatusDidChange:item state:ITEM_FINISHED];
        [self install:item.identifier];

        bNeedAnalytics = YES;
    }
    
    if (bNeedAnalytics) {
        //统计
        [RequestorAssistant requestUserActivitiesAnalyze:item.f_id statType:ANALYTICS_DOWNLOAD_SUC];
    }
}

- (void)downloadQueueDidFailItem:(SoftItem *)item error:(NSError *)error
{
    //统计
    [RequestorAssistant requestUserActivitiesAnalyze:item.f_id statType:ANALYTICS_DOWNLOAD_FAIL];
    
    if (item.increUpateInfo != nil && item.increUpateInfo.isFilelistPackage) {
        item.increUpateInfo.smartUpdateFailed = YES;
        item.url = item.increUpateInfo.updateUrl;
        [SoftIncreUpdateModule deleteIncrementTmpFileForSoft:item.identifier];
        item.increUpateInfo.isIncrePackage = NO;
        item.increUpateInfo.isFilelistPackage = NO;
        [self.downloadingAppsDict removeObjectForKey:item.identifier];
        [self.downloadCenter removeItem:item removeFile:YES];
        [self.downloadCenter addItemToQueue:item];
        [self notifySmartUpdateFailed:item];

    }else
    {
        if ([item.softName length] > 0) {
            NSString *massage = [NSString stringWithFormat:@"(%@)下载失败。请检查网络是否稳定、存储卡是否有足够空间。", item.softName];
            [StatusBarNotification notificationWithMessage:massage];
        }
        
        BOOL isNeedAutoContinue = NO;
        if ([[error domain] isEqual:NSURLErrorDomain]) {
            int errorCode = [error code];
            if (errorCode == NSURLErrorTimedOut || errorCode == NSURLErrorCannotConnectToHost || errorCode == NSURLErrorNetworkConnectionLost || errorCode == NSURLErrorNotConnectedToInternet) {
                isNeedAutoContinue = YES;
            }
        }
        
        item.isAutoContinueDownload = isNeedAutoContinue;
        
        if ([self isAnInstalledSoftItem:item])
        {
            [self updateQueueStatusDidChange:item state:ITEM_FAILED];
            
        }
        else
        {
            [self downloadQueueStatusDidChange:item state:ITEM_FAILED];
        }
        
        //增加检测到Wifi网络自动继续下载功能（网络原因导致的下载失败）
        if (isNeedAutoContinue) {
            [self continueDownloadIfWifiOk];
        }
    }

}

#pragma mark - install
- (BOOL)install:(NSString *)identifier
{

    SoftItem *exist = [self.installedAppsDict objectForKey:identifier];
//    if (exist)
//        return NO;
    exist = [self.installingAppsDict objectForKey:identifier];
    if (exist)
    {
        if (exist.installStatus == INSTALL_FINISHED)
            return NO;
        else
        {
            [self.installCenter startItem:exist];
            return YES;
        }
    }
    
    SoftItem *item = [self softItemForIdentifier:identifier];
    [item generateSaveName];
    [[NSNotificationCenter defaultCenter] postNotificationName:kGC91InstallingNotification object:item userInfo:nil];

    return [self.installCenter addItemToQueue:item];
}

- (BOOL)uninstall:(SoftItem *)item
{
    BOOL res = [MIUtility uninstall:item.identifier error:nil];
    if (res == YES)
    {
        [self.installedAppsDict removeObjectForKey:item.identifier];
        [self.downloadingAppsDict removeObjectForKey:item.identifier];
        [self.updatableAppsDict removeObjectForKey:item.identifier];
        [self.downloadedAppsDict removeObjectForKey:item.identifier];
        
        [self downloadQueueStatusDidChange:item state:ITEM_UNINSTALLED];
        [self updateQueueStatusDidChange:item state:ITEM_UNINSTALLED];
        
        //卸载成功，更新UserData里面的缓存
        [[UserData sharedInstance] removeGamesByIds:[NSArray arrayWithObject:item.identifier]];
    }
    return res;
}

- (void)askToInstall:(SoftItem *)item
{
    RIButtonItem *install = [RIButtonItem itemWithLabel:@"立即安装"];
    install.action = ^{
        [self doInstallWithLoading:item];
    };
    
    RIButtonItem *cancel = [RIButtonItem itemWithLabel:@"稍候再说"];
    
    NSString *title = [NSString stringWithFormat:@"%@已经下载完成，是否立即安装", [item softName]];
    CustomAlertView *alert = [[CustomAlertView alloc] initWithTitle:title message:nil cancelButtonItem:cancel otherButtonItems:install, nil];
    [alert show];
    [alert release];
}

- (void)doInstallWithLoading:(SoftItem *)item
{
    [self performSelector:@selector(installWithLoading:) withObject:item afterDelay:0.01];
}

- (void)installWithLoading:(SoftItem *)item
{
    [self install:item.identifier];
}

- (BOOL)isAnInstalledSoftItem:(SoftItem *)item
{
    return ([self.installedAppsDict objectForKey:item.identifier] == nil) ? NO: YES;
}
- (BOOL)isAnUpdableSoftItem:(SoftItem *)item
{
    return ([self.updatableAppsDict objectForKey:item.identifier] == nil) ? NO: YES;
}

- (BOOL)isAnInstalledGame:(NSString *)identifier
{
    return ([self.installedAppsDict objectForKey:identifier] == nil) ? NO: YES;

}
- (BOOL)haveIncreInfoForSoftItem:(SoftItem *)item //是否有增量升级信息
{
    return  (item.increUpateInfo == nil) ? NO: YES;
}
- (NSArray *)installed91SDKSoft
{
    return [self.installedAppsDict allValues];
}

- (void)installQueueDidAddItem:(SoftItem *)item
{
    if (item) {
        [self.installingAppsDict setObject:item forKey:item.identifier];
		if ([self isAnInstalledSoftItem:item])
        {
            [self updateQueueStatusDidChange:item state:ITEM_ADDED];
        }
        else
        {
            [self downloadQueueStatusDidChange:item state:ITEM_ADDED];
        }
    }
}

- (void)installQueueDidFinishInitItem:(SoftItem *)item
{
}

- (void)installQueueDidFinishItem:(SoftItem *)item
{
    if (item) {
        if ([[NSFileManager defaultManager] fileExistsAtPath:item.absoluteFilePath])
        {
            [[NSFileManager defaultManager] removeItemAtPath:item.absoluteFilePath error:nil];
        }
        item.localVersion = item.version;
        item.localShortVersion = item.shortVersion;
        item.fileName = @"installed";
        
        [self.installingAppsDict removeObjectForKey:item.identifier];
        [self.downloadedAppsDict removeObjectForKey:item.identifier];
        
        if ([self isAnInstalledSoftItem:item])
        {
            [self.installedAppsDict setObject:item forKey:item.identifier];
            
            [self.updatableAppsDict removeObjectForKey:item.identifier];
            [self updateQueueStatusDidChange:item state:ITEM_INSTALLED];
            [SoftIncreUpdateModule deleteIncrementTmpFileForSoft:item.identifier];
        }
        else 
        {
            [self.installedAppsDict setObject:item forKey:item.identifier];
            [self downloadQueueStatusDidChange:item state:ITEM_INSTALLED];
        }
        [[NSNotificationCenter defaultCenter] postNotificationName:kGC91InstallFinishedNotification object:item userInfo:nil];
        
        //统计
        [RequestorAssistant requestUserActivitiesAnalyze:item.f_id statType:ANALYTICS_INSTALL_SUC];
        
        //安装成功，调用6接口获取游戏标签等信息，再更新到UserData
        [RequestorAssistant requestGamesFilteredList:[NSArray arrayWithObject:item.identifier] delegate:self];
    }
}

- (void)installQueueDidFailItem:(SoftItem *)item
{
    [MBProgressHUD showHintHUD:@"安装失败" message:nil hideAfter:2];
    if (item) {
        item.downloadStatus = KS_DEFAULT_STATE;
        [self.installingAppsDict removeObjectForKey:item.identifier];
		[self.downloadedAppsDict removeObjectForKey:item.identifier];
        
        if ([self isAnInstalledSoftItem:item])
        {
            [self updateQueueStatusDidChange:item state:ITEM_INSTALLED_FAIL];
            [SoftIncreUpdateModule deleteIncrementTmpFileForSoft:item.identifier];

        }
        else
        {
            [self downloadQueueStatusDidChange:item state:ITEM_INSTALLED_FAIL];
        }
        
        //统计
        [RequestorAssistant requestUserActivitiesAnalyze:item.f_id statType:ANALYTICS_INSTALL_FAIL];
    }
}

#pragma mark - call back
- (void)operation:(GameCenterOperation *)operation getGamesFilteredListDidFinish:(NSError *)error appList:(NSArray *)appList
{
    if (error == nil) {
        [[UserData sharedInstance] recordFilteredGames:appList];
    }
}

#pragma mark -
#define F_ID_KEY                @"f_id"
#define IDENTIFIER_KEY          @"identifier"
#define APP_NAME_KEY            @"app_name"
#define VERSION_KEY             @"version"
#define SHORT_VERSION_KEY       @"short_version"
#define DOWNLOAD_URL_KEY        @"download_url"
#define STATUS_KEY              @"status"
#define STORAGE_PATH_KEY        @"storage_path"
#define TMP_STORAGE_PATH_KEY    @"tmp_storage_path"
#define FILE_NAME_KEY           @"file_name"
#define TIME_STAMP_KEY          @"time_stamp"
#define DOWNLOADED_LEN_KEY      @"downloaded_len"
#define FILE_LEN_KEY            @"file_len"
#define ICON_PATH_KEY            @"icon_path"
#define INCRE_INFO              @"incre_info"
#define UPDATE_URL              @"update_url"
#define INCRE_INSTALL_PATH      @"incre_install_path"

#define FULL_UPDATE_URL         @"full_update_url"
#define INCRE_PACKAGE_URL       @"incre_package_url"
#define INCRE_FILE_SIZE         @"incre_file_size"
#define FILE_LIST_URL           @"file_list_url"
#define SMART_UPT_FAILED        @"smart_upt_failed"
#define XDT_INSTALL_PATH        @"xdt_install_path"
#define IS_FILE_LIST            @"is_file_list"
#define IS_INCRE_PACKAGE        @"is_incre_package"

#define IS_AUTO_CONTINUE_DOWNLOAD @"isAutoContinueDownload"

- (SoftItem *)itemFromSerializedDictionary:(NSDictionary *)dict
{
    SoftItem *item = [[SoftItem new] autorelease];
    item.f_id = [[dict objectForKey:F_ID_KEY] intValue];
    item.identifier = [dict objectForKey:IDENTIFIER_KEY];
    item.version = [dict objectForKey:VERSION_KEY];
    item.shortVersion = [dict objectForKey:SHORT_VERSION_KEY];
    item.url = [dict objectForKey:DOWNLOAD_URL_KEY];
    item.downloadStatus = [[dict objectForKey:STATUS_KEY] intValue];
    item.savePath = [dict objectForKey:STORAGE_PATH_KEY];
    item.fileName = [dict objectForKey:FILE_NAME_KEY];
    [item generateSaveName];
    
    item.timeStamp = [[dict objectForKey:TIME_STAMP_KEY] longLongValue];
    //item.downloadedLen = [[dict objectForKey:DOWNLOADED_LEN_KEY] longValue];
    item.increUpateInfo = [self increInfoFromSerializedDictionary:[dict objectForKey:INCRE_INFO]];
    item.downloadedLen = [item localFileLength];
    item.totalLen = [[dict objectForKey:FILE_LEN_KEY] longLongValue];
    item.softName = [dict objectForKey:APP_NAME_KEY];
    item.iconPath = [dict objectForKey:ICON_PATH_KEY];
    item.increInstallPackagePath = [dict objectForKey:INCRE_INSTALL_PATH];
    item.updateUrl = [dict objectForKey:UPDATE_URL];
    NSNumber *tmp = [dict objectForKey:IS_AUTO_CONTINUE_DOWNLOAD];
    if (tmp != nil) {
        item.isAutoContinueDownload =  [tmp boolValue];
    }
    
    if ([item.identifier length] <= 0)
        return nil;
    return item;
}

- (IncreUpdateInfo *)increInfoFromSerializedDictionary:(NSDictionary *)dict
{
    if (!dict) {
        return nil;
    }
    IncreUpdateInfo *info = [[IncreUpdateInfo new] autorelease];
    info.updateUrl = [dict objectForKey:FULL_UPDATE_URL];
    info.increPackageUrl = [dict objectForKey:INCRE_PACKAGE_URL ];
    info.increFileSize = [[dict objectForKey:INCRE_FILE_SIZE] longLongValue];
    info.filelistPackageUrl = [dict objectForKey:FILE_LIST_URL];
    info.smartUpdateFailed = [[dict objectForKey:SMART_UPT_FAILED] boolValue];
    info.increInstallPackagePath = [dict objectForKey:XDT_INSTALL_PATH];
    info.isFilelistPackage = [[dict objectForKey:IS_FILE_LIST] boolValue];
    info.isIncrePackage = [[dict objectForKey:IS_INCRE_PACKAGE] boolValue];
    return info;
}

- (NSDictionary *)serializedDictionaryFromItem:(SoftItem *)item
{
    if ([item.identifier length] <= 0)
        return nil;
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:[NSNumber numberWithInt:item.f_id] forKey:F_ID_KEY];
    [dict setValue:item.identifier forKey:IDENTIFIER_KEY];
    [dict setValue:item.version forKey:VERSION_KEY];
    [dict setValue:item.shortVersion forKey:SHORT_VERSION_KEY];
    [dict setValue:item.url forKey:DOWNLOAD_URL_KEY];
    [dict setValue:[NSNumber numberWithInt:item.downloadStatus] forKey:STATUS_KEY];
    [dict setValue:item.savePath forKey:STORAGE_PATH_KEY];
    [dict setValue:item.fileName forKey:FILE_NAME_KEY];
    [dict setValue:[NSNumber numberWithLong:item.timeStamp] forKey:TIME_STAMP_KEY];
//    [dict setValue:[NSNumber numberWithLong:item.downloadedLen] forKey:DOWNLOADED_LEN_KEY];
    if (item.totalLen == 0) {
        item.totalLen = item.increUpateInfo.increFileSize;
    }
    [dict setValue:[NSNumber numberWithLong:item.totalLen] forKey:FILE_LEN_KEY];
    [dict setValue:item.softName forKey:APP_NAME_KEY];
    [dict setValue:item.iconPath forKey:ICON_PATH_KEY];
    [dict setValue:item.updateUrl forKey:UPDATE_URL];
    [dict  setValue:item.increInstallPackagePath forKey:INCRE_INSTALL_PATH];
    NSDictionary *increInfo = [self serializedInreInfoFrom:item.increUpateInfo];
    [dict setValue:increInfo forKey:INCRE_INFO];
    [dict setValue:[NSNumber numberWithBool:item.isAutoContinueDownload] forKey:IS_AUTO_CONTINUE_DOWNLOAD];
    return dict;
}

- (NSDictionary *)serializedInreInfoFrom:(IncreUpdateInfo *)increInfo
{
    if (!increInfo) {
        return nil;
    }
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:increInfo.updateUrl forKey:FULL_UPDATE_URL];
    [dict setValue:increInfo.increPackageUrl forKey:INCRE_PACKAGE_URL];
    [dict setValue:[NSNumber numberWithLong:increInfo.increFileSize] forKey:INCRE_FILE_SIZE];
    [dict setValue:increInfo.filelistPackageUrl forKey:FILE_LIST_URL];
    [dict setValue:[NSNumber numberWithBool:increInfo.smartUpdateFailed] forKey:SMART_UPT_FAILED];
    [dict setValue:increInfo.increInstallPackagePath forKey:XDT_INSTALL_PATH];
    [dict setValue:[NSNumber numberWithBool:increInfo.isFilelistPackage] forKey:IS_FILE_LIST];
    [dict setValue:[NSNumber numberWithBool:increInfo.isIncrePackage] forKey:IS_INCRE_PACKAGE];
    return dict;
}

- (NSDictionary *)serializedItemsInDictionary:(NSDictionary *)dict
{
    NSMutableDictionary *store = [NSMutableDictionary dictionary];
    for (NSString *key in dict)
    {
        SoftItem *item = [dict objectForKey:key];
        NSDictionary *dict = [self serializedDictionaryFromItem:item];
        [store setValue:dict forKey:item.identifier];
    } 
    return store;
}

- (NSMutableDictionary *)deserializedDownloadingDict
{
    NSDictionary *dict = [self.managerDefault objectForKey:SOFT_INFO_KEY];
    
    NSMutableDictionary *allDownloading = [NSMutableDictionary dictionary];
    NSDictionary *downloading = [dict objectForKey:DOWNLOADING_KEY];
    for (NSString *appIdString in downloading) {
        NSDictionary *info = [downloading objectForKey:appIdString];
        SoftItem *item = [self itemFromSerializedDictionary:info];
        if (item != nil)
        { 
            if (item.downloadStatus == KS_FINISHED)
                continue;
            
            SoftItem *exist = [self.installedAppsDict objectForKey:item.identifier];
            if (exist)
            {
                //if we found an downloading item is an old version than local, remove this downloading
                if ([self item:item isNewerThan:exist] == NO)
                {
                    [self.downloadCenter removeItem:item removeFile:YES];
                    continue;
                }
            }
            
            [allDownloading setObject:item forKey:appIdString];
        }
        
    }
    
    return allDownloading;
}

- (NSDictionary *)serializedDownloadingDict
{
    return [self serializedItemsInDictionary:self.downloadingAppsDict];
}

- (NSMutableDictionary *)deserializedDownloadedDict
{
    NSDictionary *dict = [self.managerDefault objectForKey:SOFT_INFO_KEY];
    
    NSMutableDictionary *allDownloaded = [NSMutableDictionary dictionary];
    NSDictionary *downloaded = [dict objectForKey:DOWNLOADED_KEY];
    for (NSString *appIdString in downloaded)
    {
        NSDictionary *info = [downloaded objectForKey:appIdString];
        SoftItem *item = [self itemFromSerializedDictionary:info];
        if (item != nil)
        { 
            if (item.downloadStatus != KS_FINISHED)
                continue;
            
            
            SoftItem *exist = [self.installedAppsDict objectForKey:item.identifier];
            if (exist)
            {
                //if we found an downloaded item(not installed) is an old version than local, remove the file                
                if ([item fileExist] && [self item:item isNewerThan:exist] == NO)
                {
                    [[NSFileManager defaultManager] removeItemAtPath:[item absoluteFilePath] error:nil];
                }
            }
            
            [allDownloaded setObject:item forKey:appIdString];            
        }
    }
    return allDownloaded;
}

- (NSDictionary *)serializedDownloadedDict
{
    return [self serializedItemsInDictionary:self.downloadedAppsDict];
}


- (NSMutableDictionary *)deserializedUpdatableDict
{
    NSDictionary *dict = [self.managerDefault objectForKey:UPDATABLE_KEY];
    NSMutableDictionary *all = [NSMutableDictionary dictionary];
    for (NSString *appIdString in dict) {
        NSDictionary *info = [dict objectForKey:appIdString];
        SoftItem *item = [self itemFromSerializedDictionary:info];
        if (item != nil)
        {
            [all setObject:item forKey:appIdString];
        }
    }
    return all;
}

- (NSDictionary *)serializedUpdatableDict
{
    return [self serializedItemsInDictionary:self.updatableAppsDict];
}

- (NSMutableDictionary *)deserializedInstallingDict
{
    NSDictionary *dict = [self.managerDefault objectForKey:SOFT_INFO_KEY];
    
    NSMutableDictionary *allInstalling = [NSMutableDictionary dictionary];
    NSDictionary *installing = [dict objectForKey:INSTALLING_KEY];
    for (NSString *appIdString in installing) {
        NSDictionary *info = [installing objectForKey:appIdString];
        SoftItem *item = [self itemFromSerializedDictionary:info];
        if (item != nil)
        { 
            if (item.downloadStatus == INSTALL_FINISHED)
                continue;
            
            [allInstalling setObject:item forKey:appIdString];
        }
    }
    
    return allInstalling;
}

- (NSDictionary *)serializedInstallingDict
{
    return [self serializedItemsInDictionary:self.installingAppsDict];
}

@end
