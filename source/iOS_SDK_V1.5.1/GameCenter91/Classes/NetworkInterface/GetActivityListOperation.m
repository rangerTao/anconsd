//
//  GetActivityListOperation.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-12.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "GetActivityListOperation.h"
#import "OptionProtocols.h"
#import "GcPagination.h"
#import "ActivityInfo.h"

@implementation GetActivityListOperation
@synthesize identifier, activityType, requestPage, keyword;
@synthesize activityList, totalCount;

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 201;
        self.protocolMethod = @selector(operation:getActivityListDidFinish:activityList:page:totalCount:);
        self.activityType = -1;
        self.beWithSessionId = NO;
        self.keyword = nil;
    }
    return self;
}

- (int)operation
{
    NSString *platFormName = [[UIDevice currentDevice] model];
    NSString *firmversion = [[UIDevice currentDevice] systemVersion];
    NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                 platFormName,@"PlatformName",
                                 firmversion,@"FirmwareVersion",
                                 [NSString stringWithFormat:@"%d", 1], @"Platform",
                                 [NSString stringWithFormat:@"%d", self.requestPage.pageSize], @"PageSize",
                                 [NSString stringWithFormat:@"%d", self.requestPage.pageIndex], @"PageIndex", nil];
    if (self.identifier != nil) {
        [dict setObject:self.identifier forKey:@"Identifier"];
    }
    if (self.activityType != -1) {
        [dict setObject:[NSString stringWithFormat:@"%d", self.activityType] forKey:@"ActivityType"];
    }
    if (self.keyword != nil) {
        [dict setObject:self.keyword forKey:@"Keyword"];
    }
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM_3(self.activityList, self.requestPage, self.totalCount), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    int total = [[paramDict objectForKey:@"TotalCount"] intValue];
    NSArray *arr = [paramDict objectForKey:@"ActivityList"];
    int count = [arr count];
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        ActivityInfo *obj = [ActivityInfo activityFromDictionary:[arr objectAtIndex:i]];
        if (obj)
            [result addObject:obj];
    }
    self.activityList = [NSArray arrayWithArray:result];
    self.totalCount = total;
}
@end
