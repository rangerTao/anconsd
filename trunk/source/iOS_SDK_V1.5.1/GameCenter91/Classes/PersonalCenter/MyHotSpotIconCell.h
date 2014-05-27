//
//  MyHotSpotIconCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 11/8/13.
//
//

#import <UIKit/UIKit.h>

@class HotInfo;

@interface MyHotSpotIconCell : UITableViewCell

- (void)refreshCellWithHotInfo:(HotInfo *)myHotSpot;

@end
