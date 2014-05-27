//
//  myGamesCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import <UIKit/UIKit.h>

@interface MyGamesCell : UITableViewCell 

@property (assign, nonatomic) id fatherViewController;
@property (nonatomic, assign) int selectedButtonIndex;

- (void)refreshMyGamesCell:(NSMutableArray *)myGames selectedButtonIndex:(int)selectedButtonIndex;

@end
