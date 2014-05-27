//
//  GetAppDetailViewList.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "GameCenterOperation.h"
@class AppDetailViewInfo;
@interface GetAppDetailInfoOperation : GameCenterOperation
@property (nonatomic, assign) NSString *identifer;

@property (nonatomic, retain) AppDetailViewInfo *detailViewInfo;
@end
