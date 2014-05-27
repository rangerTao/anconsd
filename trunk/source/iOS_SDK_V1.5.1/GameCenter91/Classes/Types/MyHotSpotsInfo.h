//
//  MyHotSpotsInfo.h
//  GameCenter91
//
//  Created by Li.Binbin on 11/4/13.
//
//

#import <Foundation/Foundation.h>

typedef enum _MY_HOT_TYPE
{
    MY_HOT_TYPE_PLATFORM = 0,          //平台活动
    MY_HOT_TYPE_GIFT = 1,              //礼包
    MY_HOT_TYPE_ACTIVITY = 2,          //活动
    MY_HOT_TYPE_NOTICE = 3,            //公告
    MY_HOT_TYPE_OPEN_SERVERS = 4,      //开服
    MY_HOT_TYPE_APP_RECOMMEND = 11,    //App推荐
    MY_HOT_TYPE_STRATEGY = 12,         //攻略
}MY_HOT_TYPE;

@interface MyHotSpotsInfo : NSObject

@property (nonatomic, assign) int hotType;
@property (nonatomic, retain) NSString *imageUrl;
@property (nonatomic, retain) NSString *title;
@property (nonatomic, retain) NSString *content;
@property (nonatomic, retain) NSString *showTime;
@property (nonatomic, assign) int giftNumber;
@property (nonatomic, retain) NSString *tagName;
@property (nonatomic, retain) NSString *targetAction;
@property (nonatomic, retain) NSString *targetActionUrl;

+ (MyHotSpotsInfo *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)listFromDictionary:(NSDictionary *)dict;

@end
