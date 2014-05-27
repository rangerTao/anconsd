//
//  GameProjectItem.m
//  GameCenter91
//
//  Created by Sun pinqun on 13-1-23.
//  Copyright (c) 2013年 net dragon. All rights reserved.
//

#import "GameProjectItem.h"

@implementation GameProjectItem
@synthesize gameProjectId, title, introduce, gameCount;

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

- (NSString *)description
{
    return [NSString stringWithFormat:@"\nGameProjectId:%d\nTitle:%@\nIntroduce:%@\nGameCount:%d\n", 
            self.gameProjectId,self.title,self.introduce,self.gameCount];
}

+ (GameProjectItem *)itemFromDictionary:(NSDictionary *)dict
{
    GameProjectItem *item = [[GameProjectItem new] autorelease];
    item.gameProjectId = [[dict objectForKey:@"GameProjectId"] intValue];
    item.title = [dict objectForKey:@"Title"];
    item.introduce = [dict objectForKey:@"Introduce"];
    item.gameCount = [[dict objectForKey:@"GameCount"] intValue];
    return item;
}

@end
