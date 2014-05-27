//
//  GetGameProjectDetailOperation.m
//  GameCenter91
//
//  Created by Sun pinqun on 13-1-23.
//  Copyright (c) 2013年 net dragon. All rights reserved.
//

#import "GetGameProjectDetailOperation.h"
#import "OptionProtocols.h"
#import "GameProjectItem.h"
#import "AppDescriptionInfo.h"

@implementation GetGameProjectDetailOperation
@synthesize gameProjectId;
@synthesize appList, gameProjectDetail;

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 109;
        self.protocolMethod = @selector(operation:getGameProjectDetailDidFinish:appList:projectDetail:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                            @"iPhone",@"PlatformName",
                            [[UIDevice currentDevice] systemVersion], @"FirmwareVersion",
                            [NSString stringWithFormat:@"%d",self.gameProjectId], @"GameProjectId", nil];
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    NSArray *arr = [paramDict objectForKey:@"AppList"];
    int count = [arr count];
    NSMutableArray *muArr = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        NSDictionary *dict = [arr objectAtIndex:i];
        AppDescriptionInfo *item = [AppDescriptionInfo itemFromDictionary:dict];
        if (item != nil) {
            [muArr addObject:item];
        }
    }
    self.appList = [NSArray arrayWithArray:muArr];
    
    GameProjectItem *item = [[GameProjectItem new] autorelease];
    item.gameProjectId = self.gameProjectId;
    item.title = [paramDict objectForKey:@"Title"];
    item.introduce = [paramDict objectForKey:@"Introduce"];
    item.gameCount = [[paramDict objectForKey:@"GameCount"] intValue];
    self.gameProjectDetail = item;
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM_2(self.appList, self.gameProjectDetail), nil];
}

@end
