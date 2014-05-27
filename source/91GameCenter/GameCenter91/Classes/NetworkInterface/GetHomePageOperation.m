//
//  GetHomePageOperation.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "GetHomePageOperation.h"
#import "HomePageInfo.h"
#import "UserData.h"
#import "NdCPDeviceInfo.h"

@implementation GetHomePageOperation

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 1;
        self.protocolMethod = @selector(operation:getHomePageDidFinish:homePageInfo:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    NSString *platformName = @"iPhone";
    NSString *firmversion = [[UIDevice currentDevice] systemVersion];
    
    NSString *identifiers = nil;
    if (self.identifiers == nil) {
        identifiers = @"";
    } else {
        identifiers = [self.identifiers componentsJoinedByString:@","];
    }
    
    NSString *myGameIdentifiers = nil;
    if (self.myGameIdentifiers == nil) {
        myGameIdentifiers = @"";
    } else {
        myGameIdentifiers = [self.myGameIdentifiers componentsJoinedByString:@","];
    }

    NSString *deviceToken = [UserData sharedInstance].deviceToken;
    if (deviceToken == nil) {
        deviceToken = @"";
    }
    
    NSString *udid = [NdCPDeviceInfo uniqueDeviceID];
    if (udid == nil) {
        udid = @"";
    }
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:platformName forKey:@"PlatformName"];
    [dict setValue:firmversion forKey:@"FirmwareVersion"];
    [dict setValue:identifiers forKey:@"Identifiers"];
    [dict setValue:myGameIdentifiers forKey:@"MyGameIdentifiers"];
    [dict setValue:deviceToken forKey:@"DeviceToken"];
    [dict setValue:udid forKey:@"Udid"];
    
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_GZIP];
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.homePageInfo), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    self.homePageInfo = [HomePageInfo itemFromDictionary:paramDict];
}
@end
