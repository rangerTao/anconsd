//
//  GetGameProjectListOperation.m
//  GameCenter91
//
//  Created by Sun pinqun on 13-1-23.
//  Copyright (c) 2013年 net dragon. All rights reserved.
//

#import "GetGameProjectListOperation.h"
#import "OptionProtocols.h"
#import "GameProjectItem.h"

@implementation GetGameProjectListOperation
@synthesize gameProjectList;

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 108;
        self.protocolMethod = @selector(operation:getGameProjectListDidFinish:gameProjectList:);
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    //NSString *platform = [CommUtility currentPlatform];
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"1", @"Platform", nil];
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    NSArray *arr = [paramDict objectForKey:@"GameProjectList"];
    int count = [arr count];
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        NSDictionary *dict = [arr objectAtIndex:i];
        GameProjectItem *item = [GameProjectItem itemFromDictionary:dict];
        if (item) 
            [result addObject:item];
    }
    
    if ([result count] > 0) {
        self.gameProjectList = [NSArray arrayWithArray:result];
    }
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.gameProjectList), nil];
}

@end
