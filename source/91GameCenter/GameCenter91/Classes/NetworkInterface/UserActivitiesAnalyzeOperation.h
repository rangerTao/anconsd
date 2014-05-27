//
//  UserActivitiesAnalyzeOperation.h
//  GameCenter91
//
//  Created by Li.Binbin on 11/28/13.
//
//

#import "GameCenterOperation.h"

@interface UserActivitiesAnalyzeOperation : GameCenterOperation

@property (nonatomic, assign) int f_id;
@property (nonatomic, assign) int statType;
@property (nonatomic, retain) NSString *udid;

@end
