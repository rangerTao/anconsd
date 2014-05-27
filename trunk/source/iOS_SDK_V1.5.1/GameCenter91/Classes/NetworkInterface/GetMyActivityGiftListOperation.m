//
//  GetMyActivityGiftListOperation.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-12.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "GetMyActivityGiftListOperation.h"
#import "OptionProtocols.h"
#import "GcPagination.h"
#import "GiftItem.h"

@implementation GetMyActivityGiftListOperation
@synthesize requestPage, totalCount, giftList;

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 202;
        self.protocolMethod = @selector(operation:getMyActivityGiftListDidFinish:giftList:page:totalCount:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    NSString *platFormName = [[UIDevice currentDevice] model];
    NSString *firmversion = [[UIDevice currentDevice] systemVersion];
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                          platFormName,@"PlatformName",
                          firmversion,@"FirmwareVersion",
                          [NSString stringWithFormat:@"%d", self.requestPage.pageSize], @"PageSize",
                          [NSString stringWithFormat:@"%d", self.requestPage.pageIndex], @"PageIndex", nil];
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM_3(self.giftList, self.requestPage, self.totalCount), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    NSArray *arr = [paramDict objectForKey:@"ActivityGiftList"];
    int count = [arr count];
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        GiftItem *item = [GiftItem itemFromDictionary:[arr objectAtIndex:i]];
        if (item)
            [result addObject:item];
    }
    self.giftList = [NSArray arrayWithArray:result];
    self.totalCount = [[paramDict objectForKey:@"TotalCount"] intValue];   
}
@end
