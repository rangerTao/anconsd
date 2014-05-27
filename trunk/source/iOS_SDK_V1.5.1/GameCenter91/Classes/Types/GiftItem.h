//
//  GiftItem.h
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-12.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//



@interface GiftItem : NSObject
@property (nonatomic, assign) int activityID;
@property (nonatomic, retain) NSString *title;
@property (nonatomic, retain) NSString *summary;
@property (nonatomic, retain) NSString *contentUrl;
@property (nonatomic, retain) NSString *exchangeNo;
@property (nonatomic, retain) NSString *appIconUrl;
@property (nonatomic, retain) NSString *identifier;

+ (GiftItem *)itemFromDictionary:(NSDictionary *)dict;
@end
