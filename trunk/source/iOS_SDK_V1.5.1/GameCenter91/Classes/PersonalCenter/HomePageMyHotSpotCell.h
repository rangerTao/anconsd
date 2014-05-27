//
//  HomePageMyHotSpotCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 11/12/13.
//
//

#import <UIKit/UIKit.h>

@class HotInfo;

@interface HomePageMyHotSpotCell : UITableViewCell

- (void)refreshCellWithHotInfo:(HotInfo *)hotInfo;

@end
