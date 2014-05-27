//
//  HotSearchItem.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-15.
//
//

#import <Foundation/Foundation.h>
typedef enum _HOT_SEARCH_TARGET_TYPE//推荐对象类型
{
    HOT_SEARCH_GAME = 1,//热搜游戏
    HOT_SEARCH_WORD,//搜索热词
    
}HOT_SEARCH_TARGET_TYPE;

typedef enum _BACKGROUND_TYPE//背景类型
{
    NO_TYPE = 0,//后台未配置
    IMAGE_TYPE,//图片
    COLOR_TYPE,//色块
    
}BACKGROUND_TYPE;

@interface HotSearchItem : NSObject
@property (nonatomic, assign) int targetType;
@property (nonatomic, retain) NSString *targetAction;
@property (nonatomic, retain) NSString *iconUrl;
@property (nonatomic, retain) NSString *showName;
@property (nonatomic, assign) int backGroundType;
@property (nonatomic, retain) NSString *backGround;
+ (HotSearchItem *)itemFromDictionary:(NSDictionary *)dict;

@end
