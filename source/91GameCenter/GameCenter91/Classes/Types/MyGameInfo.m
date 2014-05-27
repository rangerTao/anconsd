//
//  MyGameInfo.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/23/13.
//
//

#import "MyGameInfo.h"
#import "ActiveInfo.h"

@implementation MyGameInfo

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (MyGameInfo *)itemFromDictionary:(NSDictionary *)dict
{
    MyGameInfo *info = [[MyGameInfo new] autorelease];
    info.f_id = [[dict objectForKey:@"f_id"] intValue];
    info.identifier = [dict objectForKey:@"Identifier"];
    info.appIconUrl = [dict objectForKey:@"AppIconUrl"];
    info.appName = [dict objectForKey:@"AppName"];
    info.suggestType = [[dict objectForKey:@"SuggestType"] intValue];
    info.strategyUrl = [dict objectForKey:@"StrategyUrl"];
    info.forumUrl = [dict objectForKey:@"ForumUrl"];
    info.activeList = [ActiveInfo listFromDictionary:dict];
    return info;
}

+ (NSArray *)listFromDictionary:(NSDictionary *)dict
{
    NSArray *arr = [dict objectForKey:@"MyGames"];
    int count = [arr count];
    NSMutableArray *items = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        MyGameInfo *obj = [MyGameInfo itemFromDictionary:[arr objectAtIndex:i]];
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

- (id)copyWithZone:(NSZone *)zone
{
    MyGameInfo *copy = [[MyGameInfo alloc] init];
    if (copy) {
        copy.f_id = self.f_id;
        copy.identifier = [[self.identifier copyWithZone:zone] autorelease];
        copy.appIconUrl = [[self.appIconUrl copyWithZone:zone] autorelease];
        copy.appName = [[self.appName copyWithZone:zone] autorelease];
        copy.suggestType = self.suggestType;
        copy.strategyUrl = [[self.strategyUrl copyWithZone:zone] autorelease];
        copy.forumUrl = [[self.forumUrl copyWithZone:zone] autorelease];
        copy.activeList = [[self.activeList copyWithZone:zone] autorelease];
    }
    return copy;
}

+ (NSArray *)serializedFromArr:(NSArray *)arr
{
    NSMutableArray *arrRet = [NSMutableArray arrayWithCapacity:[arr count]];
    for (MyGameInfo *info in arr) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setValue:[NSNumber numberWithInt:info.f_id] forKey:@"f_id"];
        [dic setValue:info.identifier forKey:@"Identifier"];
        [dic setValue:info.appIconUrl forKey:@"AppIconUrl"];
        [dic setValue:info.appName forKey:@"AppName"];
        [dic setValue:[NSNumber numberWithInt:info.suggestType] forKey:@"SuggestType"];
        [dic setValue:info.strategyUrl forKey:@"StrategyUrl"];
        [dic setValue:info.forumUrl forKey:@"ForumUrl"];
        [dic setValue:[ActiveInfo serializedFromArr:info.activeList] forKey:@"ActiveList"];
        
        [arrRet addObject:dic];
    }
    return arrRet;
}

@end
