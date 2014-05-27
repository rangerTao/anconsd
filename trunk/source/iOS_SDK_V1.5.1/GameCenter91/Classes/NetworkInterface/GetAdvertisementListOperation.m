//
//  GetAdvertisementOperation.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "GetAdvertisementListOperation.h"
#import "AdsInfoCache.h"

@implementation GetAdvertisementListOperation

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 4;
        self.protocolMethod = @selector(operation:getAdsListDidFinish:adsList:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    NSString *adsLastModified = self.adsLastModified;
    //客户端平台是iPhone
    NSString *platform = [NSString stringWithFormat:@"%d", 1];
    NSString *firmversion = [[UIDevice currentDevice] systemVersion];
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:adsLastModified forKey:@"AdsLastModified"];
    [dict setValue:platform forKey:@"Platform"];
    [dict setValue:firmversion forKey:@"FirmwareVersion"];
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.adsList), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{    
    self.adsList = [AdsBriefInfoList listFromDictionary:paramDict];
}


@end
