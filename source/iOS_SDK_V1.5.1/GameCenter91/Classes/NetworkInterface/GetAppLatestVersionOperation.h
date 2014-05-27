//
//  GetAppLastedVersion.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "GameCenterOperation.h"

@interface GetAppLatestVersionOperation : GameCenterOperation

@property (nonatomic, retain) NSArray *installedApplist;

@property (nonatomic, retain) NSArray *appLastedVersionList;

@end
