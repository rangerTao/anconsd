//
//  ActivityTableViewCell.h
//  GameCenter91
//
//  Created by  hiyo on 12-8-27.
//  Copyright 2012 Nd. All rights reserved.
//

#import <UIKit/UIKit.h>

@class GiftItem, ActivityInfo;
@interface ActivityTableViewCell : UITableViewCell {
    UIImageView *game_icon;
	UILabel *act_title;
	UILabel *act_detail;
	UILabel *act_append1;
	UILabel *act_append2;
	UIButton *act_button;
	UIImageView *act_stamp;
}
@property(nonatomic, readonly) IBOutlet UIImageView *game_icon;
@property(nonatomic, readonly) IBOutlet UILabel *act_title;
@property(nonatomic, readonly) IBOutlet UILabel *act_detail;
@property(nonatomic, readonly) IBOutlet UILabel *act_append1;
@property(nonatomic, readonly) IBOutlet UILabel *act_append2;
@property(nonatomic, readonly) IBOutlet UIButton *act_button;
@property(nonatomic, readonly) IBOutlet UIImageView *act_stamp;

- (void)setCellInfo:(int)act_type withMyGiftInfo:(GiftItem *)info;
- (void)setCellInfo:(int)act_type withActivityInfo:(ActivityInfo *)info;

- (void)cellAdjustForNoneIconStyle;

+ (NSString *)cellReuseIdentifier;
+ (float)cellHeight;
@end
