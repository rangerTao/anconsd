//
//  MyGameActivityCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 11/10/13.
//
//

#import <UIKit/UIKit.h>

@class MyGameInfo;

@interface MyGameActivityCell : UITableViewCell

- (void)refreshCellWithMyGameInfo:(MyGameInfo *)myGame indexRow:(NSInteger)row;

@end
