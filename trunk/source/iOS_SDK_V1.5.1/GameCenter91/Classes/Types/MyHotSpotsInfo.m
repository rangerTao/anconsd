//
//  MyHotSpotsInfo.m
//  GameCenter91
//
//  Created by Li.Binbin on 11/4/13.
//
//

#import "MyHotSpotsInfo.h"

@implementation MyHotSpotsInfo

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (MyHotSpotsInfo *)itemFromDictionary:(NSDictionary *)dict
{
    MyHotSpotsInfo *info = [[MyHotSpotsInfo new] autorelease];
    info.hotType = [[dict objectForKey:@"HotType"] intValue];
    info.imageUrl = [dict objectForKey:@"ImageUrl"];
    info.title = [dict objectForKey:@"Title"];
    info.content = [dict objectForKey:@"Content"];
    info.showTime = [dict objectForKey:@"ShowTime"];
    info.giftNumber = [[dict objectForKey:@"GiftNumber"] intValue];
    info.tagName = [dict objectForKey:@"TagName"];
    info.targetAction = [dict objectForKey:@"TargetAction"];
    info.targetActionUrl = [dict objectForKey:@"TargetActionUrl"];
    return info;
}

+ (NSArray *)listFromDictionary:(NSDictionary *)dict
{
    NSArray *arr = [dict objectForKey:@"HotList"];
    int count = [arr count];
    NSMutableArray *items = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        MyHotSpotsInfo *obj = [MyHotSpotsInfo itemFromDictionary:[arr objectAtIndex:i]];
        if (obj)
            [items addObject:obj];
    }
    
    if ([items count] != 0)
    {
        return [NSArray arrayWithArray:items];
    }
    else
    {
        return nil;
    }
}

@end
