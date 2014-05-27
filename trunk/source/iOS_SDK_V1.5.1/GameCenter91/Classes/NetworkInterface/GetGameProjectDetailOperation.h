//
//  GetGameProjectDetailOperation.h
//  GameCenter91
//
//  Created by Sun pinqun on 13-1-23.
//  Copyright (c) 2013年 net dragon. All rights reserved.
//

#import "GameCenterOperation.h"

@class GameProjectItem;
@interface GetGameProjectDetailOperation : GameCenterOperation
@property (nonatomic, assign) int      gameProjectId;

@property (nonatomic, retain) NSArray *appList;
@property (nonatomic, retain) GameProjectItem *gameProjectDetail;

@end
