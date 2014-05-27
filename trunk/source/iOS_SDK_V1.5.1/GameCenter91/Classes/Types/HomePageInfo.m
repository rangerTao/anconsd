//
//  HomePageInfo.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/28/13.
//
//

#import "HomePageInfo.h"
#import "HotInfo.h"
#import "MyGameInfo.h"
#import "AppInfo.h"
#import "DayRecommendInfo.h"

@implementation HomePageInfo

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (HomePageInfo *)itemFromDictionary:(NSDictionary *)dict
{
    HomePageInfo *info = [[HomePageInfo new] autorelease];
    info.hotList = [HotInfo listFromDictionary:dict];
    info.myGames = [MyGameInfo listFromDictionary:dict];
    info.appList = [AppInfo listFromDictionary:dict];
    info.dayRecommendList = [DayRecommendInfo listFromDictionary:dict];
    return info;
}

+ (NSDictionary *)serializedDictionaryFromItem:(HomePageInfo *)item
{
    if (item == nil) {
        return nil;
    }
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:[HotInfo serializedFromArr:item.hotList] forKey:@"HotList"];
    [dict setValue:[MyGameInfo serializedFromArr:item.myGames] forKey:@"MyGames"];
    [dict setValue:[AppInfo serializedFromArr:item.appList] forKey:@"AppList"];
    [dict setValue:[DayRecommendInfo serializedFromArr:item.dayRecommendList] forKey:@"DayRecommendList"];
    return dict;
}

@end
