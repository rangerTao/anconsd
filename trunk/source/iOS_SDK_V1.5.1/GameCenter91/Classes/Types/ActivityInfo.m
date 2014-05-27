//
//  ActivityInfo.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-12.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "ActivityInfo.h"
#import "CommUtility.h"

@implementation ActivityInfo
@synthesize activityID;
@synthesize title;
@synthesize summary;
@synthesize recommendedIcons;
@synthesize startTime;
@synthesize endTime;
@synthesize activityType;
@synthesize contentUrl;
@synthesize giftNumber;
@synthesize belongServer;
@synthesize exchanged;
@synthesize exchangeNo;
@synthesize openTime;
@synthesize appIconUrl;
@synthesize identifier;
@synthesize appName;

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (ActivityInfo *)activityFromDictionary:(NSDictionary *)dict
{
    ActivityInfo *obj = [[ActivityInfo new] autorelease];
    
    obj.activityID = [[dict objectForKey:@"ActivityId"] intValue];
    obj.title = [dict objectForKey:@"Title"];
    obj.summary = [dict objectForKey:@"Summary"];
    obj.recommendedIcons = [CommUtility packRecommendIconsStr:[dict objectForKey:@"RecommendIcons"]];
    obj.startTime = [dict objectForKey:@"StartTime"];
    obj.endTime = [dict objectForKey:@"EndTime"];
    obj.activityType = [[dict objectForKey:@"ActivityType"] intValue];
    obj.contentUrl = [dict objectForKey:@"ContentUrl"];
    obj.giftNumber = [[dict objectForKey:@"GiftNumber"] intValue];
    obj.belongServer = [dict objectForKey:@"BelongServer"];
    obj.exchanged = [[dict objectForKey:@"Exchanged"] intValue];
    obj.exchangeNo = [dict objectForKey:@"ExchangeNo"];
    obj.openTime = [dict objectForKey:@"OpenTime"];
    obj.appIconUrl = [dict objectForKey:@"AppIconUrl"];
    obj.identifier = [dict objectForKey:@"Identifier"];
    obj.appName = [dict objectForKey:@"AppName"];
    
    return obj;
}
@end
