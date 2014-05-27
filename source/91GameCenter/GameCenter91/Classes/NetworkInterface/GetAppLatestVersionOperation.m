//
//  GetAppLastedVersion.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "GetAppLatestVersionOperation.h"
#import <NdComPlatform/NdComPlatformBase.h>
#import "SoftItem.h"
#import "SoftManagementCenter.h"
#import "CommUtility.h"

@implementation GetAppLatestVersionOperation
- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 104;
        self.protocolMethod = @selector(operation:getAppLastedVersionDidFinish:appList:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    
    NSString *platformName = @"iPhone";
    NSString *OsVersion = [[UIDevice currentDevice] systemVersion];
    NSString *platformVersion = [NdComPlatform version];
    NSArray *softs = self.installedApplist;
    int count = [softs count];
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        SoftItem *soft = [softs objectAtIndex:i];
//        soft.localVersion = @"1.9.0"; //测试用
//        soft.localShortVersion = @"1.9.0";
//        soft.version =  @"1.9.0";
//        soft.shortVersion =  @"1.9.0";
        NSString *identifier = soft.identifier;
        NSString *bundleVersion = soft.localShortVersion;
        NSString *appVersion = soft.localVersion;
        
        NSMutableDictionary *item = [NSMutableDictionary dictionary];
        [item setValue:identifier forKey:@"AppIdentifier"];
        [item setValue:appVersion forKey:@"AppVersion"];
        [item setValue:bundleVersion forKey:@"BundleVersion"];
        
        [arr addObject:item];
    }
    NSString *channel = [CommUtility getChannelIdFromCfg];
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:platformName forKey:@"PlatformName"];
    [dict setValue:OsVersion forKey:@"OsVersion"];
    [dict setValue:platformVersion forKey:@"PlatformVersion"];
    [dict setValue:arr forKey:@"AppList"];
    [dict setValue:channel forKey:@"ChannelId"];
    
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.appLastedVersionList), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    NSArray *arr = [paramDict objectForKey:@"AppList"];
    int count = [arr count];
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:count];
    for (NSDictionary *dict in arr){
        SoftItem *item = [[SoftItem new] autorelease];
        item.identifier = [dict objectForKey:@"AppIdentifier"];
        item.version = [dict objectForKey:@"AppVersion"];
        item.shortVersion = [dict objectForKey:@"BundleVersion"];
        item.updateUrl = [dict objectForKey:@"UpdateUrl"];
        item.totalLen = [[dict objectForKey:@"SoftSize"] longLongValue];
        item.f_id = [[dict objectForKey:@"SoftId"] intValue];
        item.softName = [dict objectForKey:@"AppName"];
        item.iconPath = [dict objectForKey:@"IconUrl"];
        item.increUpateInfo = [IncreUpdateInfo increUpdateInfoFromDictionary:dict];
        if ([item.identifier length] > 0)
        {
            [result addObject:item];
        }
    }
    if ([result count] > 0) {
        self.appLastedVersionList = [NSArray arrayWithArray:result];
    }
}

@end
