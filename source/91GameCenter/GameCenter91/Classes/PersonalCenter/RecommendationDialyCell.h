//
//  RecommendationDialyCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import <UIKit/UIKit.h>
@class DayRecommendInfo;

@interface RecommendationDialyCell : UITableViewCell

- (void)refreshRecommendationToday:(DayRecommendInfo *)recommendationToday;

@end
