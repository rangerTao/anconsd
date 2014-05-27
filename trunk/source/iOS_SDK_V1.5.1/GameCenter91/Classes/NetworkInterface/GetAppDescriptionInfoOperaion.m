//
//  GetAppDescriptionInfoOperaion.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-24.
//
//

#import "GetAppDescriptionInfoOperaion.h"
#import "AppDescriptionInfo.h"
@implementation GetAppDescriptionInfoOperaion
- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 107;
        self.protocolMethod = @selector(operation:getAppDesciptionInfoDidFinish:appDesciptionInfo:);
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
    [dict setValue:self.softIdentifier forKey:@"Identifier"];
    
    
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}
- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.descriptionInfo), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    AppDescriptionInfo *descriptionInfo = [AppDescriptionInfo itemFromDictionary:paramDict];
    self.descriptionInfo = descriptionInfo;
}

@end
