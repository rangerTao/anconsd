//
//  UserData.m
//  GameCenter91
//
//  Created by kensou on 12-9-20.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "UserData.h"
#import "UserBasicInfomation.h"
#import "DatabaseUtility.h"
#import "AdsInfoCache.h"
#import "RIButtonItem.h"
#import "UIAlertView+Blocks.h"
#import <NdComPlatform/NdComPlatform.h>
#import "CustomAlertView.h"
#import "SoftManagementCenter.h"

#import "HomePageInfo.h"
#import "AppInfo.h"
#import "MyGameInfo.h"
#import "CommUtility.h"
#import "Notifications.h"


@implementation UserData
SYNTHESIZE_SINGLETON_FOR_CLASS(UserData)

@synthesize adsList;
@synthesize userInfo;
@synthesize deviceToken;
@synthesize thirdPartyUrlScheme;

+ (void)prepare
{
    [self sharedInstance].adsList = [DatabaseUtility cachedAdsList];
    [self sharedInstance].userInfo = [[UserBasicInfomation new] autorelease];
    [self sharedInstance].deviceToken = nil;
    [self sharedInstance].thirdPartyUrlScheme = nil;
    
    [[self sharedInstance] updateAppsListCache];
}

- (NSString *)lastAdsListModifyDate
{
    return self.adsList.lastModifyDate;
}

- (void)showNotNormalUserHint:(NSString *)hint
{
    NSString *title = @"";
    
    RIButtonItem *okItem = [RIButtonItem item];
    if (![[NdComPlatform defaultPlatform] isLogined])
    {
        title = [NSString stringWithFormat:@"%@", hint];
        okItem.label = @"马上登录";
        okItem.action = ^{[[NdComPlatform defaultPlatform] NdLogin:0];};
    }
    
    RIButtonItem *cancelItem = [RIButtonItem itemWithLabel:@"继续逛逛"];
    
    CustomAlertView *alert = [[CustomAlertView alloc] initWithTitle:title message:nil cancelButtonItem:cancelItem otherButtonItems:okItem, nil];
    [alert show];
    [alert release];

}

- (void)recordAdsList:(AdsBriefInfoList *)aAdsList
{
    [DatabaseUtility recordAdsList:aAdsList];
    self.adsList = [DatabaseUtility cachedAdsList];
}

//my games
- (void)reloadMyGameRelatedCache
{
    self.homePageInfo = [DatabaseUtility cachedHomePageInfo];
    self.allAppIdsList = [DatabaseUtility cachedAllAppIdsList];
    self.myGameIdsList = [DatabaseUtility cachedMyGameIdsList];
}

- (void)updateAppsListCache
{
    [self reloadMyGameRelatedCache];
    
    NSMutableArray *arrAdd = [NSMutableArray array];
    NSMutableArray *arrdelete = [NSMutableArray array];
    //差分得出新增和已删除应用
    NSArray *allInstalledArr = [CommUtility allInstalledAppIdentifierArr];
    for (NSString *identifier in allInstalledArr) {
        if ([self.allAppIdsList containsObject:identifier] == NO) {
            [arrAdd addObject:identifier];
        }
    }
    for (NSString *identifier in self.allAppIdsList) {
        if ([[CommUtility allInstalledAppIdentifierArr] containsObject:identifier] == NO) {
            [arrdelete addObject:identifier];
        }
    }
    //首次不进行6接口过滤，1接口已经过滤了
    self.newAppIdsList = ([self.homePageInfo.appList count] <= 0) ? nil : arrAdd;
    //删除缓存中已删除的应用
    [self removeGamesByIds:arrdelete];
}

- (void)recordHomePageInfo:(HomePageInfo *)info
{
    if ([self.homePageInfo.appList count] <= 0) {
        //首次启动保存当前的所有应用列表到DB
        self.allAppIdsList = [CommUtility allInstalledAppIdentifierArr];
        [DatabaseUtility recordAllAppIdsList:self.allAppIdsList];
        //首次启动需要保存游戏列表
        self.homePageInfo = info;
        
        //首次使用MyGames里面的作为关注
        NSMutableArray *tmpArr = [NSMutableArray array];
        for (MyGameInfo *myGameInfo in info.myGames) {
            if (myGameInfo.suggestType != 1) {
                [tmpArr addObject:myGameInfo.identifier];
            }
        }
        self.myGameIdsList = tmpArr;
        [DatabaseUtility recordMyGameIdsList:self.myGameIdsList];
        //初始化游戏列表的顺序
        [self autoSortGameList];
        //首次所有游戏加新游戏标记
        for (AppInfo *info in self.homePageInfo.appList) {
            info.bNewGame = YES;
        }
        //第一次要抛消息通知首页刷新
        [[NSNotificationCenter defaultCenter] postNotificationName:kGC91NeedRreshHomePage object:self userInfo:nil];
    }
    else {
        self.homePageInfo.hotList = info.hotList;
        self.homePageInfo.myGames = info.myGames;
        self.homePageInfo.dayRecommendList = info.dayRecommendList;
    }
    [DatabaseUtility recordHomePageInfo:self.homePageInfo];
}

- (void)recordFilteredGames:(NSArray *)gamesArr
{
    //保存当前的所有应用列表到DB
    self.allAppIdsList = [CommUtility allInstalledAppIdentifierArr];
    [DatabaseUtility recordAllAppIdsList:self.allAppIdsList];
    
    //加新游戏标记
    for (AppInfo *info in gamesArr) {
        info.bNewGame = YES;
    }
    //过滤已经有的（保险）
    NSMutableArray *safeGamesArr = [NSMutableArray arrayWithArray:gamesArr];
    NSMutableArray *removeArr = [NSMutableArray array];
    for (AppInfo *infoNew in gamesArr) {
        for (AppInfo *infoOld in self.homePageInfo.appList) {
            if ([infoNew.identifier isEqualToString:infoOld.identifier]) {
                [removeArr addObject:infoNew];
                break;
            }
        }
    }
    [safeGamesArr removeObjectsInArray:removeArr];
    
    if ([safeGamesArr count] <= 0) {
        return;
    }
    
    //保存新增游戏
    NSMutableArray *finalArr = [NSMutableArray arrayWithArray:safeGamesArr];
    [finalArr addObjectsFromArray:self.homePageInfo.appList];
    self.homePageInfo.appList = finalArr;

    //刷新我的关注游戏列表
    if ([self.myGameIdsList count] < 4) {
        [self supplyMyGameIdsWithNewGames:safeGamesArr];
    }
    [self autoSortGameList];
    //放入已关注的不加新游戏标记
    for (int i = 0; i < 4 && i < [self.homePageInfo.appList count]; i++) {
        AppInfo *info = [self.homePageInfo.appList objectAtIndex:i];
        if ([self.myGameIdsList containsObject:info.identifier]) {
            info.bNewGame = NO;
        }
    }
    //保存到数据库
    [DatabaseUtility recordHomePageInfo:self.homePageInfo];
    //通知首页进行刷新
    [[NSNotificationCenter defaultCenter] postNotificationName:kGC91NeedRreshHomePage object:self userInfo:nil];
}

- (void)removeGamesByIds:(NSArray *)removedGameIds
{
    BOOL bNeedNotify = NO;
    for (NSString *identifier in removedGameIds) {
        if ([self.myGameIdsList containsObject:identifier]) {
            bNeedNotify = YES;
            break;
        }
    }
    [DatabaseUtility removeCacheByDeletedIdentifiers:removedGameIds];
    [self reloadMyGameRelatedCache];
    if (bNeedNotify) {
        //通知首页进行刷新
        [[NSNotificationCenter defaultCenter] postNotificationName:kGC91NeedRreshHomePage object:self userInfo:nil];
    }
}

- (void)quitGameEditPageWithGameIdsList:(NSArray *)gameIdsList gameList:(NSArray *)gameList
{
    if (gameIdsList != nil) {
        //保存新游戏列表
        self.myGameIdsList = gameIdsList;
        [DatabaseUtility recordMyGameIdsList:self.myGameIdsList];
    }
    if (gameList != nil) {
        self.homePageInfo.appList = gameList;
        //清除标记
        [self removeNewGameLabel];
        [DatabaseUtility recordHomePageInfo:self.homePageInfo];
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:kGC91NeedRreshHomePage object:self userInfo:nil];
}

//清除新游戏标记
- (void)removeNewGameLabel
{
    for (AppInfo *info in self.homePageInfo.appList) {
        info.bNewGame = NO;
    }
}

//新增SDK游戏时，如果我的关注游戏不足四个时需要补充进去
- (void)supplyMyGameIdsWithNewGames:(NSArray *)gamesArr
{
    int myGameNum = [self.myGameIdsList count];
    if (myGameNum < 4) {
        NSMutableArray *tmpArr = [NSMutableArray arrayWithArray:self.myGameIdsList];
        for (int i = 0; myGameNum<4 && i<[gamesArr count]; i++) {
            AppInfo *info = [gamesArr objectAtIndex:i];
            if (info == nil || info.gameId <= 0) {
                //非SDK游戏不自动填充
                continue;
            }
            [tmpArr addObject:info.identifier];
            myGameNum++;
        }
        self.myGameIdsList = tmpArr;
        [DatabaseUtility recordMyGameIdsList:self.myGameIdsList];
    }
}

//自动调整游戏列表的顺序
- (void)autoSortGameList
{
    if ([self.homePageInfo.appList count] <= 0) {
        return;
    }
    NSMutableArray *finalArr = [NSMutableArray array];
    NSMutableArray *nonSdkGameArr = [NSMutableArray array];
    NSMutableArray *followGameArr = [NSMutableArray array];
    for (AppInfo *info in self.homePageInfo.appList) {
        if ([self.myGameIdsList containsObject:info.identifier]) {
            [followGameArr addObject:info];
        }
        else if (info.gameId <= 0) {
            [nonSdkGameArr addObject:info];
        }
        else {
            [finalArr addObject:info];
        }
    }
    //非SDK游戏放后面
    [finalArr addObjectsFromArray:nonSdkGameArr];
    //我的关注放最前面
    for (int i = [self.myGameIdsList count]-1; i >= 0; i--) {
        NSString *identifier = [self.myGameIdsList objectAtIndex:i];
        for (AppInfo *info in followGameArr) {
            if ([identifier isEqualToString:info.identifier]) {
                [finalArr insertObject:info atIndex:0];
                break;
            }
        }
    }
    self.homePageInfo.appList = finalArr;
}

@end
