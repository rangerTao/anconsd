//
//  GetMyHotSpotsOperation.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "GetMyHotSpotsOperation.h"
#import "MyHotSpotsInfo.h"

@implementation GetMyHotSpotsOperation

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 5;
        self.protocolMethod = @selector(operation:getMyHotSpotsDidFinish:hotList:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    NSString *platformName = @"iPhone";
    NSString *firmversion = [[UIDevice currentDevice] systemVersion];
    
    NSString *myGameIdentifiers = nil;
    if (self.myGameIdentifiers == nil) {
        myGameIdentifiers = @"";
    } else {
        myGameIdentifiers = [self.myGameIdentifiers componentsJoinedByString:@","];
    }
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:platformName forKey:@"PlatformName"];
    [dict setValue:firmversion forKey:@"FirmwareVersion"];
    [dict setValue:myGameIdentifiers forKey:@"MyGameIdentifiers"];
    
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.hotList), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    self.hotList = [MyHotSpotsInfo listFromDictionary:paramDict];
}


@end
