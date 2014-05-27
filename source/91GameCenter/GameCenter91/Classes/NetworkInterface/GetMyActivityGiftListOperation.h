//
//  GetMyActivityGiftListOperation.h
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-12.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "GameCenterOperation.h"
@class GcPagination;
@interface GetMyActivityGiftListOperation : GameCenterOperation
@property (nonatomic, retain) GcPagination *requestPage;

@property (nonatomic, retain) NSArray *giftList;
@property (nonatomic, assign) int totalCount;

@end
