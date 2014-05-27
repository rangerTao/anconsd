//
//  HotSpotInfo.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "HotSpotInfo.h"

@implementation HotSpotInfo

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (HotSpotInfo *)itemFromDictionary:(NSDictionary *)dict
{
    HotSpotInfo *info = [[HotSpotInfo new] autorelease];
    info.hotType = [[dict objectForKey:@"HotType"] intValue];
    info.imageUrl = [dict objectForKey:@"ImageUrl"];
    info.title = [dict objectForKey:@"Title"];
    info.content = [dict objectForKey:@"Content"];
    info.tagName = [dict objectForKey:@"TagName"];
    info.targetAction = [dict objectForKey:@"TargetAction"];
    info.targetActionUrl = [dict objectForKey:@"TargetactionUrl"];
    return info;
}

+ (NSArray *)listFromDictionary:(NSDictionary *)dict
{
    NSArray *arr = [dict objectForKey:@"HotList"];
    int count = [arr count];
    NSMutableArray *items = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        HotSpotInfo *obj = [HotSpotInfo itemFromDictionary:[arr objectAtIndex:i]];
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
