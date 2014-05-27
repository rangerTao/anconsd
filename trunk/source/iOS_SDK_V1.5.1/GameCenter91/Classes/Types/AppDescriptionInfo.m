//
//  AppDescriptionInfo.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-24.
//
//

#import "AppDescriptionInfo.h"
#import "CommUtility.h"

@implementation AppDescriptionInfo
- (void)dealloc
{
    [self clearAllProperty];
    [super dealloc];
}
+ (AppDescriptionInfo *)itemFromDictionary:(NSDictionary *)dict
{
    AppDescriptionInfo *item = [[AppDescriptionInfo new] autorelease];
    item.f_id = [[dict objectForKey:@"f_id"] intValue];
    item.identifier = [dict objectForKey:@"Identifier"];
    item.appIconUrl = [dict objectForKey:@"AppIconUrl"];
    item.appName = [dict objectForKey:@"AppName"];
    item.appScore = [[dict objectForKey:@"Appscore"] intValue];
    item.labelIcons = [CommUtility packRecommendIconsStr:[dict objectForKey:@"LabelIcons"]];
    item.downloadNumber = [[dict objectForKey:@"DownloadNumber"] intValue];
    item.fileSize = (long)[[dict objectForKey:@"FileSize"] longLongValue];
    item.appVersionName = [dict objectForKey:@"AppVersionName"];
    item.isChinese = [[dict objectForKey:@"IsChinese"] intValue];
    return item;
}

@end
