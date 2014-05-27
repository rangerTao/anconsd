//
//  GetAppList.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/15/13.
//
//

#import "GetAppListOperation.h"
#import "GcPagination.h"
#import "AppDescriptionInfo.h"

@implementation GetAppListOperation

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 102;
        self.protocolMethod = @selector(operation:getAppListDidFinish:appList:page:isLastPage:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    
    NSString *catagoryId = [NSString stringWithFormat:@"%d", self.catagoryId];
    NSString *sortType = [NSString stringWithFormat:@"%d", self.sortType];

    NSString *platformName = @"iPhone";
    NSString *firmversion = [[UIDevice currentDevice] systemVersion];
    NSString *pageIndex = [NSString stringWithFormat:@"%d", self.requestPage.pageIndex];
    NSString *pageSize = [NSString stringWithFormat:@"%d", self.requestPage.pageSize];
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:catagoryId forKey:@"CatagoryId"];
    [dict setValue:sortType forKey:@"SortType"];
    [dict setValue:platformName forKey:@"PlatformName"];
    [dict setValue:firmversion forKey:@"FirmwareVersion"];
    [dict setValue:pageIndex forKey:@"PageIndex"];
    [dict setValue:pageSize forKey:@"PageSize"];
    
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM_3(self.appList, self.requestPage, self.isLastPage), nil];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    NSArray *arr = [paramDict objectForKey:@"AppList"];
    int count = [arr count];
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        NSDictionary *dict = [arr objectAtIndex:i];
        AppDescriptionInfo *item = [AppDescriptionInfo itemFromDictionary:dict];
        if (item)
            [result addObject:item];
    }
    if ([result count] > 0) {
        self.appList = [NSArray arrayWithArray:result];
    }

    self.isLastPage = [[paramDict objectForKey:@"IsLastPage"] intValue];
}


@end
