//
//  DayRecommendInfo.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/23/13.
//
//

#import "DayRecommendInfo.h"
#import "CommUtility.h"

@implementation DayRecommendInfo

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (DayRecommendInfo *)itemFromDictionary:(NSDictionary *)dict
{
    DayRecommendInfo *info = [[DayRecommendInfo new] autorelease];
    info.imageUrl = [dict objectForKey:@"ImageUrl"];
    info.title = [dict objectForKey:@"Title"];
    info.summary = [dict objectForKey:@"Summary"];
    info.f_id = [[dict objectForKey:@"f_id"] intValue];
    info.identifier = [dict objectForKey:@"Identifier"];
    info.appIconUrl = [dict objectForKey:@"AppIconUrl"];
    info.labelIcons = [CommUtility packRecommendIconsStr:[dict objectForKey:@"LabelIcons"]];
    return info;
}

+ (NSArray *)listFromDictionary:(NSDictionary *)dict
{
    NSArray *arr = [dict objectForKey:@"DayRecommendList"];
    int count = [arr count];
    NSMutableArray *items = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        DayRecommendInfo *obj = [DayRecommendInfo itemFromDictionary:[arr objectAtIndex:i]];
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
    for (DayRecommendInfo *info in arr) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setValue:info.imageUrl forKey:@"ImageUrl"];
        [dic setValue:info.title forKey:@"Title"];
        [dic setValue:info.summary forKey:@"Summary"];
        [dic setValue:[NSNumber numberWithInt:info.f_id] forKey:@"f_id"];
        [dic setValue:info.identifier forKey:@"Identifier"];
        [dic setValue:info.appIconUrl forKey:@"AppIconUrl"];
        [dic setValue:[CommUtility unPackRecommendIconsStr:info.labelIcons] forKey:@"LabelIcons"];
        
        [arrRet addObject:dic];
    }
    return arrRet;
}

@end
