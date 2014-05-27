//
//  AdsInfoCache.h
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-10.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum _ADS_TARGET{
    ADS_TARGET_NONE = 0,    //只作显示
    ADS_TARGET_LINK,        //链接地址
    ADS_TARGET_GAME,        //游戏专区
}ADS_TYPE;

typedef enum _ADS_POSITION{
    ADS_POS_GAME_ALL = 1,   //全部游戏
    ADS_POS_GAME_LOCAL,     //单机游戏
    ADS_POS_GAME_NETWORK,   //网络游戏
    ADS_POS_GAME_GIFTS,     //游戏礼包
    ADS_POS_ACTIVITY_NOTICE,  //活动公告
}ADS_POSITION;

@interface AdsBriefInfo : NSObject
@property (nonatomic, retain) NSString *imageUrl;
@property (nonatomic, retain) NSString *actionParam;
@property (nonatomic, assign) int actionType;
@property (nonatomic, assign) int areaGroup;
@property (nonatomic, assign) CGSize areaSize;

+ (AdsBriefInfo *)objectFromDictionary:(NSDictionary *)dict;
@end

@interface AdsBriefInfoList : NSObject
@property (nonatomic, retain) NSArray *adsList;
@property (nonatomic, retain) NSString *lastModifyDate;
+ (AdsBriefInfoList *)listFromDictionary:(NSDictionary *)dict;
@end