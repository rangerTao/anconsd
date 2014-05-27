//
//  RequestorAssistant.h
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-7.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SynthesizeSingleton.h"
#import "OptionProtocols.h"

#import "GetAppIdentifierOperation.h"

typedef enum _USER_ANALYTICS_TYPE
{
    ANALYTICS_DOWNLOAD = 1,
    ANALYTICS_DOWNLOAD_SUC = 4,
    ANALYTICS_INSTALL_SUC = 5,
    ANALYTICS_INSTALL_FAIL = 6,
    ANALYTICS_DOWNLOAD_FAIL = 8,
}USER_ANALYTICS_TYPE;

@interface RequestorAssistant : NSObject
SYNTHESIZE_SINGLETON_FOR_CLASS_HEADER(RequestorAssistant)

//call this first before any other
+ (void)prepare;


#pragma mark - 基础接口
//首页接口
+ (NSNumber *)requestHomePage:(NSArray *)identifiers myGameIdentifiers:(NSArray *)myGameIdentifiers delegate:(id<GetHomePageProtocol>)delegate;
//用户基本信息获取接口
+ (NSNumber *)requestUserBasicInfomation:(id<GetUserBasicInfomationProtocol>)delegate;
//获取靓点推荐接口
+ (NSNumber *)requestRecommendedPoint:(id<GetRecommendedPointProtocol>)delegate;
//［缓存］获取广告列表
+ (NSNumber *)requestAdsList:(NSString *)adsLastModified delegate:(id<GetAdvertisementListProtocol>)delegate;
//我的热点列表
+ (NSNumber *)requestMyHotSpots:(NSArray *)myGameIdentifiers delegate:(id<GetMyHotSpotsProtocol>)delegate;
//游戏过滤接口
+ (NSNumber *)requestGamesFilteredList:(NSArray *)identifiers delegate:(id<GetGamesFilteredProtocol>)delegate;
//用户行为统计
+ (NSNumber *)requestUserActivitiesAnalyze:(NSInteger)f_id statType:(USER_ANALYTICS_TYPE)statType;

#pragma mark - 游戏接口
//获取应用类别列表
+ (NSNumber *)requestAppCatagoryList:(id<GetAppCatagoryListProtocol>)delegate;
//获取应用列表
+ (NSNumber *)requestAppList:(GcPagination *)page catagoryId:(int)catagoryId sortType:(int)sortType delegate:(id<GetAppListProtocol>)delegate;
//获取搜索推荐列表
+ (NSNumber *)requestHotSearchList:(id<GetHotSearchListProtocol>)delegate;
//获取游戏联想词
+ (NSNumber *)requestSoftSuggestionList:(NSString *)aKeyword delegate:(id<GetSoftSuggestionProtocol>)delegate;
//获取游戏搜索结果
+ (NSNumber *)requestGameSearchResultList:(NSString *)akeyword delegate:(id<GetGameSearchResultProtocol>)delegate;
//获取应用最新版本
+ (NSNumber *)requestAppLatestVersion:(NSArray *)applist delegate:(id<GetAppLastedVersionProtocol>)delegate;
//获取游戏下载地址
+ (NSNumber *)requestAppDownloadUrlList:(NSDictionary *)aDic delegate:(id<GetAppDownloadUrlProtocol>)delegate;
//获取游戏详情
+ (NSNumber *)requestAppDetailViewInfo:(NSString *)identifier delegate:(id<GetAppDetailViewInfoProtocol>)delegate;
//获取游戏简介
+ (NSNumber *)requestAppDesciptionInfo:(NSString *)aSoftIdentifier delegate:(id<GetAppDescriptionInfoProtocol>)delegate;
//获取游戏专题列表
+ (NSNumber *)requestGameProjectList:(id<GetGameProjectListProtocol>)delegate;
//获取游戏专题详情列表
+ (NSNumber *)requestGameProjectDetail:(int)gameProjectid delegate:(id<GetGameProjectDetailProtocol>)delegate;
//获取软件标识符
+ (NSNumber *)requestAppIdentifier:(int)aAppid savedArr:(NSArray *)aSavedArr delegate:(id<GetAppIdentifierProtocol>)delegate;
+ (NSNumber *)requestAppIdentifier:(int)aAppid complete:(IdentifierCallback)complete;


#pragma mark - 活动接口
//获取活动列表
+ (NSNumber *)requestGetActivityList:(NSString *)identifier type:(int)type page:(GcPagination *)page keyword:(NSString *)keyword delegate:(id<GetActivityListProtocol>)delegate;
//获取我的礼包的列表
+ (NSNumber *)requestMyActivityGiftList:(GcPagination *)page delegate:(id<GetMyActivityGiftListProtocol>)delegate;


#pragma mark -
#pragma cancel api 
- (void)cancelOperation:(NSNumber *)operationReference;
- (void)cancelAllOperationOfRequestor:(id)requestor;
@end
