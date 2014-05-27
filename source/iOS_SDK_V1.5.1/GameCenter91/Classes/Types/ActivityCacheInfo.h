//
//  ActivityCacheInfo.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import <Foundation/Foundation.h>

//游戏详情页中的活动
@interface ActivityItem : NSObject
@property (nonatomic, assign) int activityId;
@property (nonatomic, assign) int isRecommend;
@property (nonatomic, retain) NSString *titleTagColor;
@property (nonatomic, retain) NSString *title;
@property (nonatomic, assign) int activityType;
@property (nonatomic, retain) NSString *contentUrl;

+ (ActivityItem *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)itemArrayFromDicArray:(NSArray *)dicArry;
@end


