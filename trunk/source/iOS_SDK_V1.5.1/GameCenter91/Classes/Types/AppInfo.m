//
//  appInfo.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/24/13.
//
//

#import "AppInfo.h"
#import "CommUtility.h"

@implementation AppInfo

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (AppInfo *)itemFromDictionary:(NSDictionary *)dict
{
    AppInfo *info = [[AppInfo new] autorelease];
    info.f_id = [[dict objectForKey:@"f_id"] intValue];
    info.identifier = [dict objectForKey:@"Identifier"];
    info.appIconUrl = [dict objectForKey:@"AppIconUrl"];
    info.appName = [dict objectForKey:@"AppName"];
    info.gameId = [[dict objectForKey:@"GameId"] intValue];
    info.labelIcons = [CommUtility packRecommendIconsStr:[dict objectForKey:@"LabelIcons"]];
    
    if ([dict objectForKey:@"bNewGame"] != nil) {
        info.bNewGame = [[dict objectForKey:@"bNewGame"] boolValue];
    }
    else {
        info.bNewGame = NO;
    }
    return info;
}

+ (NSArray *)listFromDictionary:(NSDictionary *)dict
{
    NSArray *arr = [dict objectForKey:@"AppList"];
    int count = [arr count];
    NSMutableArray *items = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        AppInfo *obj = [AppInfo itemFromDictionary:[arr objectAtIndex:i]];
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
    AppInfo *copy = [[AppInfo alloc] init];
    if (copy) {
        copy.f_id = self.f_id;
        copy.identifier = [[self.identifier copyWithZone:zone] autorelease];
        copy.appIconUrl = [[self.appIconUrl copyWithZone:zone] autorelease];
        copy.appName = [[self.appName copyWithZone:zone] autorelease];
        copy.gameId = self.gameId;
        copy.labelIcons = [[self.labelIcons copyWithZone:zone] autorelease];
        copy.bNewGame = self.bNewGame;
    }
    return copy;
}

+ (NSArray *)serializedFromArr:(NSArray *)arr
{
    NSMutableArray *arrRet = [NSMutableArray arrayWithCapacity:[arr count]];
    for (AppInfo *info in arr) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setValue:[NSNumber numberWithInt:info.f_id] forKey:@"f_id"];
        [dic setValue:info.identifier forKey:@"Identifier"];
        [dic setValue:info.appIconUrl forKey:@"AppIconUrl"];
        [dic setValue:info.appName forKey:@"AppName"];
        [dic setValue:[NSNumber numberWithInt:info.gameId] forKey:@"GameId"];
        [dic setValue:[CommUtility unPackRecommendIconsStr:info.labelIcons] forKey:@"LabelIcons"];
        
        [dic setValue:[NSNumber numberWithBool:info.bNewGame] forKey:@"bNewGame"];
        
        [arrRet addObject:dic];
    }
    return arrRet;
}

@end
