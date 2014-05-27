//
//  NormalDayRecommendCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 11/8/13.
//
//

#import <UIKit/UIKit.h>

@class DayRecommendInfo;

@interface NormalDayRecommendCell : UITableViewCell

- (void)refreshCellWithDayRecommendInfo:(DayRecommendInfo *)dayRecommendInfo;

@end
