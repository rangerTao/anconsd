//
//  AdsInfoCache.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-10.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "AdsInfoCache.h"
#import "NSArray+Extent.h"

@implementation AdsBriefInfo
@synthesize imageUrl, actionType, actionParam, areaSize, areaGroup;

- (id)init {
    self = [super init];
    if (self) {
        self.areaSize = CGSizeZero;
    }
    return self;
}

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (AdsBriefInfo *)objectFromDictionary:(NSDictionary *)dict
{
    AdsBriefInfo *obj = [[AdsBriefInfo new] autorelease];
    obj.imageUrl = [dict objectForKey:@"ImageUrl"];
    obj.actionParam = [dict objectForKey:@"TargetAction"];
    obj.actionType = [[dict objectForKey:@"TargetType"] intValue];
    obj.areaGroup = [[dict objectForKey:@"AreaGroup"] intValue];
    NSString *sizeStr = [dict objectForKey:@"AreaSize"];
    NSArray *arr = [sizeStr componentsSeparatedByString:@"x"];
    obj.areaSize = CGSizeMake([[arr valueAtIndex:0] intValue], [[arr valueAtIndex:1] intValue]);
    return obj;
}
@end


@implementation AdsBriefInfoList
@synthesize adsList, lastModifyDate;

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (AdsBriefInfoList *)listFromDictionary:(NSDictionary *)dict
{
    AdsBriefInfoList *list = [[AdsBriefInfoList new] autorelease];
    list.lastModifyDate = [dict objectForKey:@"AdsLastModified"];
    NSArray *arr = [dict objectForKey:@"AdsList"];
    int count = [arr count];
    NSMutableArray *items = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        AdsBriefInfo *obj = [AdsBriefInfo objectFromDictionary:[arr objectAtIndex:i]];
        if (obj)
            [items addObject:obj];
    }
    if ([items count] != 0)
    {
        list.adsList = [NSArray arrayWithArray:items];
    }
    else
    {
        list.adsList = nil;
    }
    return list;    
}
@end