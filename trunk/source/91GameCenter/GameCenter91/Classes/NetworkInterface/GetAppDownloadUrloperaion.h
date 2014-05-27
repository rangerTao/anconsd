//
//  GetAppDownloadUrloperaion.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "GameCenterOperation.h"

@interface GetAppDownloadUrloperaion : GameCenterOperation
@property (nonatomic, retain) NSDictionary *dic; //value:f_id, key:identifier

@property (nonatomic, retain) NSArray *downloadAppList;
@end
