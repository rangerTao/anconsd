//
//  GameRankDetailController.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/18/13.
//
//

#import "GameTypeDefining.h"

@class GcPageTable;

@interface GameRankDetailController : UIViewController 

@property (nonatomic, retain) GcPageTable *gameRankDetailTable;
@property (nonatomic, assign) GAME_DETAIL_TYPE game_rank_type;
@property (nonatomic, assign) id fatherViewController;

- (id)initWithType:(GAME_DETAIL_TYPE)type;

@end
