//
//  HotInfo.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/23/13.
//
//

#import <Foundation/Foundation.h>

typedef enum _HOT_TYPE
{
    HOT_TYPE_PLATFORM = 0,          //平台活动
    HOT_TYPE_GIFT = 1,              //礼包
    HOT_TYPE_ACTIVITY = 2,          //活动
    HOT_TYPE_NOTICE = 3,            //公告
    HOT_TYPE_OPEN_SERVERS = 4,      //开服
    HOT_TYPE_APP_RECOMMEND = 11,    //App推荐
    HOT_TYPE_STRATEGY = 12,         //攻略
}HOT_TYPE;

@interface HotInfo : NSObject

@property (nonatomic, assign) int hotType;
@property (nonatomic, retain) NSString *imageUrl;
@property (nonatomic, retain) NSString *title;
@property (nonatomic, retain) NSString *content;
@property (nonatomic, retain) NSString *tagName;
@property (nonatomic, retain) NSString *titleTagColor;
@property (nonatomic, retain) NSString *targetAction;
@property (nonatomic, retain) NSString *targetActionUrl;

+ (HotInfo *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)listFromDictionary:(NSDictionary *)dict;

+ (NSArray *)serializedFromArr:(NSArray *)arr;

@end
