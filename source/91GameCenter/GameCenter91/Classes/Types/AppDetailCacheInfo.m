
//
//  AppDetailViewInfo.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "AppDetailCacheInfo.h"
#import "GuideCacheInfo.h"
#import "StrategyCacheInfo.h"
#import "ForumCacheInfo.h"
#import "ActivityCacheInfo.h"
@implementation AppDetailViewInfo
- (void)dealloc
{
    [self clearAllProperty];
    [super dealloc];
}
+ (AppDetailViewInfo *)itemFromDictionary:(NSDictionary *)dict
{
    AppDetailViewInfo *item = [[AppDetailViewInfo new] autorelease];
    item.f_id = [[dict objectForKey:@"f_id"] intValue];
    item.identifier = [dict objectForKey:@"Identifier"];
    item.appName = [dict objectForKey:@"AppName"];
    item.appIconUrl = [dict objectForKey:@"AppIconUrl"];
    item.appScore = [[dict objectForKey:@"Appscore"] intValue];
    item.isChinese = [[dict objectForKey:@"IsChinese"] intValue];
    item.isGreen = [[dict objectForKey:@"IsGreen"] intValue];
    item.downloadNumber = [[dict objectForKey:@"DownloadNumber"] intValue];
    item.fileSize = [[dict objectForKey:@"FileSize"] longLongValue];
    item.appVersionName = [dict objectForKey:@"AppVersionName"];
    item.appDescription = [dict objectForKey:@"AppDescription"];
    item.softImages = [dict objectForKey:@"SoftImages"];
    item.strategyUrl = [dict objectForKey:@"StrategyUrl"];
    item.forumUrl = [dict objectForKey:@"ForumUrl"];
    item.activityList = [ActivityItem itemArrayFromDicArray:[dict objectForKey:@"ActiveList"]];
    item.guideList = [GuideItem itemArrayFromDicArray:[dict objectForKey:@"GuideList"]];
    item.strategyList = [StrategyItem itemArrayFromDicArray:[dict objectForKey:@"StrategyList"]];
    item.forumList = [ForumItem itemArrayFromDicArray:[dict objectForKey:@"ForumList"]];
    return item;
}
@end
