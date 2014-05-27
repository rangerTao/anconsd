//
//  GetGameFilteredOperation.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/24/13.
//
//

#import "GetGamesFilteredListOperation.h"
#import "AppInfo.h"

@implementation GetGamesFilteredListOperation

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 6;
        self.protocolMethod = @selector(operation:getGamesFilteredListDidFinish:appList:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    NSString *platformName = @"iPhone";
    NSString *firmversion = [[UIDevice currentDevice] systemVersion];
    NSString *identifiers = ([self.identifers count] > 0) ? [self.identifers componentsJoinedByString:@","] : @"";
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:platformName forKey:@"PlatformName"];
    [dict setValue:firmversion forKey:@"FirmwareVersion"];
    [dict setValue:identifiers forKey:@"Identifiers"];
    
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_GZIP];
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.appList), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    self.appList = [AppInfo listFromDictionary:paramDict];
}


@end
