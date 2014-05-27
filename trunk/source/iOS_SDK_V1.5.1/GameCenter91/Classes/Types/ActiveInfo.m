//
//  ActivieInfo.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/23/13.
//
//

#import "ActiveInfo.h"

@implementation ActiveInfo

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (ActiveInfo *)itemFromDictionary:(NSDictionary *)dict
{
    ActiveInfo *info = [[ActiveInfo new] autorelease];
    info.activityId = [[dict objectForKey:@"ActivityId"] intValue];
    info.identifier = [dict objectForKey:@"Identifier"];
    info.title = [dict objectForKey:@"Title"];
    info.activityType = [[dict objectForKey:@"ActivityType"] intValue];
    info.contentUrl = [dict objectForKey:@"ContentUrl"];
    return info;
}

+ (NSArray *)listFromDictionary:(NSDictionary *)dict
{
    NSArray *arr = [dict objectForKey:@"ActiveList"];
    int count = [arr count];
    NSMutableArray *items = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        ActiveInfo *obj = [ActiveInfo itemFromDictionary:[arr objectAtIndex:i]];
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
    for (ActiveInfo *info in arr) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setValue:[NSNumber numberWithInt:info.activityId] forKey:@"ActivityId"];
        [dic setValue:info.identifier forKey:@"Identifier"];
        [dic setValue:info.title forKey:@"Title"];
        [dic setValue:[NSNumber numberWithInt:info.activityType] forKey:@"ActivityType"];
        [dic setValue:info.contentUrl forKey:@"ContentUrl"];
                
        [arrRet addObject:dic];
    }
    return arrRet;
}

@end
