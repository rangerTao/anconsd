//
//  ClassificationRecommendCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/12/13.
//
//

#import <UIKit/UIKit.h>

@class ProgessButton;
@class SoftItem;
@class AppDescriptionInfo;
@class CustomStarsView;

@interface CatagoryDetailCell : UITableViewCell

@property (retain, nonatomic) IBOutlet ProgessButton *gameStateButton;

- (void)updateBtnState:(SoftItem *)item;
- (void)updateCellWithAppInfo:(AppDescriptionInfo *)appInfo;
+ (CGFloat)cellHeight;

@end
