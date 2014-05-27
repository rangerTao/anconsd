//
//  GetAppCatagoryList.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/15/13.
//
//

#import "GetAppCatagoryListOperation.h"
#import "GameCatagoryInfo.h"
@implementation GetAppCatagoryListOperation

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 101;
        self.protocolMethod = @selector(operation:getAppCatagoryListDidFinish:appCatagoryList:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    NSString *platformName = @"iPhone";
    NSString *firmversion = [[UIDevice currentDevice] systemVersion];
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:platformName forKey:@"PlatformName"];
    [dict setValue:firmversion forKey:@"FirmwareVersion"];
    
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.appCatagoryList), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    NSArray *arr = [paramDict objectForKey:@"AppCatagoryList"];
    int count = [arr count];
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        NSDictionary *dict = [arr objectAtIndex:i];
        GameCatagoryInfo *item = [GameCatagoryInfo catagoryInfoFromDictionary:dict];
        if (item)
            [result addObject:item];
    }
    if ([result count] > 0) {
        self.appCatagoryList = [NSArray arrayWithArray:result];
    }

}


@end
