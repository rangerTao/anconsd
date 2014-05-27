//
//  GetAppList.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/15/13.
//
//

#import "GameCenterOperation.h"
@class GcPagination;

@interface GetAppListOperation : GameCenterOperation

@property (nonatomic, assign) int catagoryId;
@property (nonatomic, assign) int sortType;
@property (nonatomic, retain) GcPagination *requestPage;

@property (nonatomic, retain) NSArray *appList;
@property (nonatomic, assign) int isLastPage;

@end
