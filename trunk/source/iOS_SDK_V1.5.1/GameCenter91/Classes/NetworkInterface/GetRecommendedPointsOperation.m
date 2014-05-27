//
//  GetRecommendedPointOperation.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "GetRecommendedPointsOperation.h"
#import "ProjectInfo.h"

@implementation GetRecommendedPointsOperation

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 3;
        self.protocolMethod = @selector(operation:getRecommendedPointDidFinish:projectList:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    //客户端平台是iPhone
    NSString *platform = [NSString stringWithFormat:@"%d", 1];
    NSString *platformName = @"iPhone";
    NSString *firmwareVersion = [[UIDevice currentDevice] systemVersion];
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:platform forKey:@"Platform"];
    [dict setValue:platformName forKey:@"PlatformName"];
    [dict setValue:firmwareVersion forKey:@"FirmwareVersion"];
    
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.projectList), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    self.projectList = [ProjectInfo listFromDictionary:paramDict];
}

@end
