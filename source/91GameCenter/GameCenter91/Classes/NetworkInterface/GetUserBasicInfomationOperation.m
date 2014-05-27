//
//  GetUserBasicInfomationOperation.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "GetUserBasicInfomationOperation.h"
#import "UserBasicInfomation.h"

@implementation GetUserBasicInfomationOperation

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 2;
        self.protocolMethod = @selector(operation:getUserBasicInfomationDidFinish:userBasicInfomationItem:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
//    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    return [self sendRequest:nil encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.userBasicInfomationItem), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    self.userBasicInfomationItem = [UserBasicInfomation itemFromDictionary:paramDict];
}

@end
