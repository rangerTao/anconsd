//
//  HotInfo.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/23/13.
//
//

#import "HotInfo.h"

@implementation HotInfo

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (HotInfo *)itemFromDictionary:(NSDictionary *)dict
{
    HotInfo *info = [[HotInfo new] autorelease];
    info.hotType = [[dict objectForKey:@"HotType"] intValue];
    info.imageUrl = [dict objectForKey:@"ImageUrl"];
    info.title = [dict objectForKey:@"Title"];
    info.content = [dict objectForKey:@"Content"];
    info.tagName = [dict objectForKey:@"TagName"];
    info.titleTagColor = [dict objectForKey:@"TitleTagColor"];
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
        HotInfo *obj = [HotInfo itemFromDictionary:[arr objectAtIndex:i]];
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

+ (NSArray *)serializedFromArr:(NSArray *)arr
{
    NSMutableArray *arrRet = [NSMutableArray arrayWithCapacity:[arr count]];
    for (HotInfo *info in arr) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setValue:[NSNumber numberWithInt:info.hotType] forKey:@"HotType"];
        [dic setValue:info.imageUrl forKey:@"ImageUrl"];
        [dic setValue:info.title forKey:@"Title"];
        [dic setValue:info.content forKey:@"Content"];
        [dic setValue:info.tagName forKey:@"TagName"];
        [dic setValue:info.titleTagColor forKey:@"TitleTagColor"];
        [dic setValue:info.targetAction forKey:@"TargetAction"];
        [dic setValue:info.targetActionUrl forKey:@"TargetActionUrl"];
        [arrRet addObject:dic];
    }
    return arrRet;
}

@end
