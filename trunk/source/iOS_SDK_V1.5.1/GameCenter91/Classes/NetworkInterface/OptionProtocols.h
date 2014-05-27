//
//  OptionProtocols.h
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-7.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

@class GameCenterOperation, CommonOperation;

@class GcPagination;


#pragma mark - 基础接口
//首页接口
@class HomePageInfo;
@protocol GetHomePageProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getHomePageDidFinish:(NSError *)error homePageInfo:(HomePageInfo *)homePageInfo;
@end

//用户基本信息获取接口
@class UserBasicInfomation;
@protocol GetUserBasicInfomationProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getUserBasicInfomationDidFinish:(NSError *)error userBasicInfomationItem:(UserBasicInfomation *)userBasicInfomation;
@end

//获取靓点推荐接口
@protocol GetRecommendedPointProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getRecommendedPointDidFinish:(NSError *)error projectList:(NSArray *)projectList;
@end

//［缓存］获取广告列表
@class AdsBriefInfoList;
@protocol GetAdvertisementListProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getAdsListDidFinish:(NSError *)error adsList:(AdsBriefInfoList *)adsList;
@end

//我的热点列表
@protocol GetMyHotSpotsProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getMyHotSpotsDidFinish:(NSError *)error hotList:(NSArray *)hotList;
@end

//游戏过滤回调
@protocol GetGamesFilteredProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getGamesFilteredListDidFinish:(NSError *)error appList:(NSArray *)appList;
@end

#pragma mark - 游戏接口
//获取应用类别回调
@protocol GetAppCatagoryListProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getAppCatagoryListDidFinish:(NSError *)error appCatagoryList:(NSArray *)appCatagoryList;
@end

//获取应用列表回调
@protocol GetAppListProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getAppListDidFinish:(NSError *)error appList:(NSArray *)appList page:(GcPagination *)page isLastPage:(int)isLastPage;
@end

//获取联想词回调
@protocol GetSoftSuggestionProtocol <NSObject>
@required
- (void)operation:(CommonOperation *)operation getSoftSuggestionDidFinish:(NSError *)error suggestionList:(NSArray *)suggestionList;
@end

//获取搜索结果回调
@protocol GetGameSearchResultProtocol <NSObject>
@required
- (void)operation:(CommonOperation *)operation getGameSearchResultDidFinish:(NSError *)error resultList:(NSArray *)searchResultList;

@end

//获取热搜列表回调
@protocol GetHotSearchListProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getHotSearchListDidFinish:(NSError *)error hotSearchList:(NSArray *)hotsearchlist;
@end

//获取应用最新版本回调
@protocol GetAppLastedVersionProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getAppLastedVersionDidFinish:(NSError *)error appList:(NSArray *)appList;
@end

//获取游戏下载地址回调
@protocol GetAppDownloadUrlProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getAppDownloadUrlListDidFinish:(NSError *)error downloadUrlList:(NSArray *)downloadAppList;
@end

//获取游戏详情回调
@class AppDetailViewInfo;
@protocol GetAppDetailViewInfoProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getAppDetailViewInfoDidFinish:(NSError *)error appDetailViewInfo:(AppDetailViewInfo *)detailViewInfo;
@end

//获取游戏简介回调
@class AppDescriptionInfo;
@protocol GetAppDescriptionInfoProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getAppDesciptionInfoDidFinish:(NSError *)error appDesciptionInfo:(AppDescriptionInfo *)descriptionInfo;
@end

//获取专题列表回调
@protocol GetGameProjectListProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getGameProjectListDidFinish:(NSError *)error gameProjectList:(NSArray *)gameProjectList;
@end

//获取专题详情列表回调
@class GameProjectItem;
@protocol GetGameProjectDetailProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getGameProjectDetailDidFinish:(NSError *)error appList:(NSArray *)appList projectDetail:(GameProjectItem*)projectDetail;
@end

//获取软件标识符回调
@protocol GetAppIdentifierProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getAppIdentifierDidFinish:(NSError *)error identifier:(NSString *)aIdentifier strategy:(NSString *)aStrategy forum:(NSString *)aForum savedArr:(NSArray *)savedArr;
@end


#pragma mark - 活动接口
//获取活动列表
@protocol GetActivityListProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getActivityListDidFinish:(NSError *)error activityList:(NSArray *)activityList page:(GcPagination *)page totalCount:(int)totalCount;
@end

//获取我的礼包的列表
@protocol GetMyActivityGiftListProtocol <NSObject>
@required
- (void)operation:(GameCenterOperation *)operation getMyActivityGiftListDidFinish:(NSError *)error giftList:(NSArray *)giftList page:(GcPagination *)page totalCount:(int)totalCount;
@end

