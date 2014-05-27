//
//  GetActivityListOperation.h
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-12.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "GameCenterOperation.h"
@class GcPagination;

@interface GetActivityListOperation : GameCenterOperation

@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, assign) int activityType;
@property (nonatomic, retain) GcPagination *requestPage;
@property (nonatomic, retain) NSString *keyword;

@property (nonatomic, retain) NSArray *activityList;
@property (nonatomic, assign) int totalCount;
@end
