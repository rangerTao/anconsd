//
//  myGamesDeafultCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/28/13.
//
//

#import <UIKit/UIKit.h>

@interface MyGamesDefaultCell : UITableViewCell

@property (nonatomic, assign) id fatherView;

- (void)refresMyGamesDefaultCell:(NSArray *)myGames isMyGameDownloadedExist:(BOOL)isMyGameDownloadedExist;

@end
