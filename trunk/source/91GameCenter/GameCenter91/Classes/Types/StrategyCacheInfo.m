//
//  StrategyCacheInfo.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "StrategyCacheInfo.h"

@implementation StrategyItem
- (void)dealloc
{
    [self clearAllProperty];
    [super dealloc];
}
+ (StrategyItem *)itemFromDictionary:(NSDictionary *)dict
{
    StrategyItem *item = [[StrategyItem new] autorelease];
    item.strategyName = [dict objectForKey:@"StrategyName"];
    item.strategyUrl = [dict objectForKey:@"StrategyUrl"];
    return item;
}
+ (NSArray *)itemArrayFromDicArray:(NSArray *)dicArry
{
    int count = [dicArry count];
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:count];
    for (NSDictionary *dict in dicArry) {
        StrategyItem *item = [StrategyItem itemFromDictionary:dict];
        if (item != nil) {
            [arr addObject:item];
        }
    }
    return arr;
    
}

@end
