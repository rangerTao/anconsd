//
//  GameTableViewCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/14/13.
//
//

#import <UIKit/UIKit.h>
@class ProgessButton;
@class SoftItem, AppDescriptionInfo,AppDetailViewInfo;
@class CustomStarsView;

@interface GameTableViewCell : UITableViewCell

@property (retain, nonatomic) IBOutlet ProgessButton *gameStateButton;

- (void)updateCellWithAppInfo:(AppDescriptionInfo *)appInfo;
- (void)updateBtnState:(SoftItem *)item;
- (void)cellAdjustForGroupStyle;
- (void)adjustForDetailViewCtrWithInfo:(AppDetailViewInfo *)detailInfo;

+ (CGFloat)cellHeight;

@end
