//
//  AppDownLoadInfo.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "AppDownLoadInfo.h"

@implementation AppDownLoadInfo
- (void)dealloc
{
    [self clearAllProperty];
    [super dealloc];
}
+ (AppDownLoadInfo *)itemFromDictionary:(NSDictionary *)dict
{
    AppDownLoadInfo *item = [[AppDownLoadInfo new] autorelease];
    item.f_id = [[dict objectForKey:@"f_id"] intValue];
    item.versionCode = [dict objectForKey:@"VersionCode"];
    item.versionName = [dict objectForKey:@"VersionName"];
    item.identifier = [dict objectForKey:@"Identifier"];
    item.packageSize = [[dict objectForKey:@"PackageSize"] longLongValue];
    item.downloadUrl = [dict objectForKey:@"DownloadUrl"];
    return  item;
}
@end
