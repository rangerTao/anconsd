//
//  GetAppDownloadUrloperaion.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "GetAppDownloadUrloperaion.h"
#import "AppDownLoadInfo.h"
#import "CommUtility.h"

@implementation GetAppDownloadUrloperaion
- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 105;
        self.protocolMethod = @selector(operation:getAppDownloadUrlListDidFinish:downloadUrlList:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    
    NSString *platformName = @"iPhone";
    NSString *firmwareVersion = [[UIDevice currentDevice] systemVersion];
    NSString *channel = [CommUtility getChannelIdFromCfg];
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:platformName forKey:@"PlatformName"];
    [dict setValue:firmwareVersion forKey:@"FirmwareVersion"];
    [dict setValue:channel forKey:@"ChannelId"];
    
    NSString *f_id_string = [[self.dic allValues] componentsJoinedByString:@","];
    if (f_id_string != nil) {
        [dict setValue:f_id_string forKey:@"f_id"];
    }
    
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}
- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.downloadAppList), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    NSArray *arr = [paramDict objectForKey:@"DownloadAppList"];
    int count = [arr count];
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        NSDictionary *dict = [arr objectAtIndex:i];
        AppDownLoadInfo *item = [AppDownLoadInfo itemFromDictionary:dict];
        if (item)
            [result addObject:item];
    }
    if ([result count] > 0) {
        self.downloadAppList = [NSArray arrayWithArray:result];
    }
    
}

@end
