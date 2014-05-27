//
//  GetGameFilteredOperation.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/24/13.
//
//

#import "GameCenterOperation.h"

@interface GetGamesFilteredListOperation : GameCenterOperation

@property (nonatomic, retain) NSArray *identifers;

@property (nonatomic, retain) NSArray *appList;

@end
