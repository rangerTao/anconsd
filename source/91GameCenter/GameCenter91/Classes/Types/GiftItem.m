//
//  GiftItem.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-12.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "GiftItem.h"

@implementation GiftItem
@synthesize activityID, title, summary, contentUrl, exchangeNo, appIconUrl, identifier;

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (GiftItem *)itemFromDictionary:(NSDictionary *)dict
{
    GiftItem *item = [[GiftItem new] autorelease];
    item.activityID = [[dict objectForKey:@"ActivityId"] intValue];
    item.title = [dict objectForKey:@"Title"];
    item.summary = [dict objectForKey:@"Summary"];
    item.contentUrl = [dict objectForKey:@"ContentUrl"];
    item.exchangeNo = [dict objectForKey:@"ExchangeNo"];
    item.appIconUrl = [dict objectForKey:@"AppIconUrl"];
    item.identifier = [dict objectForKey:@"Identifier"];
    return item;
}
@end
