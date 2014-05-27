//
//  ActivityInfo.h
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-12.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

//页面类型
typedef enum _ACT_TYPE {
	ACT_OTHER = -1,
	ACT_MY_GIFTS = 0,			//我的礼包
	ACT_GAME_GIFTS = 1,			//游戏礼包	
	ACT_ACTIVITY_NOTICE = 2,	//活动公告
	ACT_NEW_SERVERS_NOTICE = 4, //新服预告
}ACT_TYPE;

typedef enum _ACTIVITY_TYPE
{
    ACTIVITY_TYPE_GAME_GIFT = 1,            //游戏礼包
    ACTIVITY_TYPE_ACTIVITY_NOTICE = 2,      //活动公告
    ACTIVITY_TYPE_PRIZE_NOTICE = 3,         //获奖公告
    ACTIVITY_TYPE_NEW_SERVERS_NOTICE = 4,   //开服预告
}ACTIVITY_TYPE;

typedef enum _ACTIVITY_SUB_TYPE
{
    ACTIVITY_SUB_TYPE_NORML = 0,
    ACTIVITY_SUB_TYPE_NEWSERVER = 1,
}_ACTIVITY_SUB_TYPE;

//活动接口返回的活动
@interface ActivityInfo : NSObject
@property (nonatomic, assign) int       activityID;
@property (nonatomic, retain) NSString *title;
@property (nonatomic, retain) NSString *summary;
//recommendIcons 字符串型如：“start/首发,pop/人气,pri/特权”，以英文逗号分开
@property (nonatomic, retain) NSString *recommendedIcons;
@property (nonatomic, retain) NSString *startTime;
@property (nonatomic, retain) NSString *endTime;
@property (nonatomic, assign) int       activityType;
@property (nonatomic, retain) NSString *contentUrl;
@property (nonatomic, assign) int       giftNumber;
@property (nonatomic, retain) NSString *belongServer;
@property (nonatomic, assign) int       exchanged;
@property (nonatomic, retain) NSString *exchangeNo;
@property (nonatomic, retain) NSString *openTime;
@property (nonatomic, retain) NSString *appIconUrl;
@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, retain) NSString *appName;

+ (ActivityInfo *)activityFromDictionary:(NSDictionary *)dict;
@end
