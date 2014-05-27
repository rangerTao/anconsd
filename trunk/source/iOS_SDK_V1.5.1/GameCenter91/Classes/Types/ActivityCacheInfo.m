//
//  ActivityCacheInfo.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "ActivityCacheInfo.h"

@implementation ActivityItem
- (void)dealloc
{
    [self clearAllProperty];
    [super dealloc];
}
+ (ActivityItem *)itemFromDictionary:(NSDictionary *)dict
{
    ActivityItem *item = [[ActivityItem new] autorelease];
    item.activityId = [[dict objectForKey:@"ActivityId"] intValue];
    item.isRecommend = [[dict objectForKey:@"IsRecommend"] intValue];
    item.titleTagColor = [dict objectForKey:@"TitleTagColor"];
    item.title = [dict objectForKey:@"Title"];
    item.activityType = [[dict objectForKey:@"ActivityType"] intValue];
    item.contentUrl = [dict objectForKey:@"ContentUrl"];
    return item;
}
+ (NSArray *)itemArrayFromDicArray:(NSArray *)dicArry
{
    int count = [dicArry count];
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:count];
    for (NSDictionary *dict in dicArry) {
        ActivityItem *item = [ActivityItem itemFromDictionary:dict];
        if (item != nil) {
            [arr addObject:item];
        }
    }
    return arr;
}
@end
