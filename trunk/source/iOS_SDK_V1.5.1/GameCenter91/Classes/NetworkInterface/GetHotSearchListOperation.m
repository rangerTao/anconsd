//
//  GetHotSearchListOperation.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-15.
//
//

#import "GetHotSearchListOperation.h"
#import "OptionProtocols.h"
#import "HotSearchItem.h"
#import <NdComPlatform/NdComPlatformAPIResponse.h>

@implementation GetHotSearchListOperation
- (id)init
{
    self = [super init];
    if (self) {
        self.actionNumber = 103;
        self.protocolMethod = @selector(operation:getHotSearchListDidFinish:hotSearchList:);
        self.beWithSessionId = NO;
    }
    return self;
}
- (int)operation
{
    NSString *platformName = @"iPhone";
    NSString *firmware = [[UIDevice currentDevice] systemVersion];
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:platformName, @"PlatformName" ,firmware, @"FirmwareVersion", nil];
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];

}
- (void)generateResponse:(NSDictionary *)paramDict
{
    NSArray *arr = [paramDict objectForKey:@"RecommendList"];
    int count = [arr count];
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        NSDictionary *dict = [arr objectAtIndex:i];
        HotSearchItem *item = [HotSearchItem itemFromDictionary:dict];
        if (item)
            [result addObject:item];
    }
    if ([result count] > 0) {
        self.hotSearchList = [NSArray arrayWithArray:result];
    }
    

}
- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.hotSearchList), nil];

}
@end
