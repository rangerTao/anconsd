//
//  GetAppDetailViewList.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "GetAppDetailInfoOperation.h"
#import "AppDetailCacheInfo.h"
@implementation GetAppDetailInfoOperation
- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 106;
        self.protocolMethod = @selector(operation:getAppDetailViewInfoDidFinish:appDetailViewInfo:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    
//    NSString *platformName = [[UIDevice currentDevice] model];
    NSString *platformName = @"iPhone";
    NSString *firmwareVersion = [[UIDevice currentDevice] systemVersion];
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:platformName forKey:@"PlatformName"];
    [dict setValue:firmwareVersion forKey:@"FirmwareVersion"];
    [dict setValue:self.identifer forKey:@"Identifier"];
    
    
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}
- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.detailViewInfo), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    AppDetailViewInfo *detail = [AppDetailViewInfo itemFromDictionary:paramDict];
    self.detailViewInfo = detail;
}

@end
