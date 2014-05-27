//
//  UserData.h
//  GameCenter91
//
//  Created by kensou on 12-9-20.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SynthesizeSingleton.h"

#define  NDCP_CHECK_LOGIN_ERROR_AND_SHOULD_ALERT(showAlert) \
{ \
    if (![[NdComPlatform defaultPlatform] isLogined]) { \
        [[UserData sharedInstance] showNotNormalUserHint:(showAlert)]; \
        return; \
    } \
} \

@class AdsBriefInfoList;
@class HomePageInfo;
@class UserBasicInfomation;

@interface UserData : NSObject
SYNTHESIZE_SINGLETON_FOR_CLASS_HEADER(UserData)
@property (nonatomic, retain) AdsBriefInfoList *adsList;

@property (nonatomic, readonly) NSString *lastAdsListModifyDate;

@property (nonatomic, retain) UserBasicInfomation *userInfo;

@property (nonatomic, retain) NSString *deviceToken;

@property (nonatomic, retain) NSString *thirdPartyUrlScheme;

//home page related
@property (nonatomic, retain) HomePageInfo *homePageInfo;
@property (nonatomic, retain) NSArray *allAppIdsList;
@property (nonatomic, retain) NSArray *myGameIdsList;         //我的关注游戏identifier列表
@property (nonatomic, retain) NSArray *newAppIdsList;

+ (void)prepare;

//广告
- (void)recordAdsList:(AdsBriefInfoList *)aAdsList;


- (void)showNotNormalUserHint:(NSString *)hint;

//home page related
- (void)reloadMyGameRelatedCache;                   //我的游戏相关缓存
- (void)updateAppsListCache;                        //差分得出新增和已删除应用,删除缓存中已删除的应用
- (void)recordHomePageInfo:(HomePageInfo *)info;    //首页
- (void)recordFilteredGames:(NSArray *)gamesArr;    //游戏过滤(新增游戏统一都在游戏过滤回调中处理)
- (void)removeGamesByIds:(NSArray *)removedGameIds; //删除游戏
- (void)quitGameEditPageWithGameIdsList:(NSArray *)gameIdsList gameList:(NSArray *)gameList;  //退出我的游戏编辑，传回新的游戏关注列表，以及新的全部游戏列表

@end
