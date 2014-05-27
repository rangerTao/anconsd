//
//  myGameButtonsCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 11/11/13.
//
//

#import <UIKit/UIKit.h>

@class MyGameInfo;

@interface MyGameButtonsCell : UITableViewCell

@property (nonatomic, assign) id fatherController;

- (void)refreshCellWithMyGameInfo:(MyGameInfo *)myGame withIndex:(NSInteger)index;

@end
