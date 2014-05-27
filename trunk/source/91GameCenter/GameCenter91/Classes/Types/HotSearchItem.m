//
//  HotSearchItem.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-15.
//
//

#import "HotSearchItem.h"

@implementation HotSearchItem
- (void)dealloc
{
    [self clearAllProperty];
    [super dealloc];
}
+ (HotSearchItem *)itemFromDictionary:(NSDictionary *)dict
{
    HotSearchItem *item = [[HotSearchItem new] autorelease];
    item.targetType = [[dict objectForKey:@"TargetType"] intValue];
    item.targetAction = [dict objectForKey:@"TargetAction"];
    item.iconUrl = [dict objectForKey:@"IconUrl"];
    item.showName = [dict objectForKey:@"ShowName"];
    item.backGroundType = [[dict objectForKey:@"BackGroundType"] intValue];
    item.backGround = [dict objectForKey:@"BackGround"];
    return item;
}

@end
