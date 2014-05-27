//
//  gamesDownloadedEditingCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/24/13.
//
//

#import <UIKit/UIKit.h>

@class MyGameInfo;

@interface EditingMyGamesCell : UITableViewCell

@property (nonatomic, assign) id fatherView;
@property (nonatomic, assign) id fatherViewController;

- (void)refreshEditingMyGamesCell:(NSMutableArray *)appList withIndexPath:(NSIndexPath *)indexPath;

@end
