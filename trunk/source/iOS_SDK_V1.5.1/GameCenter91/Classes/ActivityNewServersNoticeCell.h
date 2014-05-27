//
//  ActivityTableViewCell.h
//  GameCenter91
//
//  Created by  hiyo on 12-8-27.
//  Copyright 2012 Nd. All rights reserved.
//

#import <UIKit/UIKit.h>

@class ActivityInfo;
@interface ActivityNewServersNoticeCell : UITableViewCell {
	UILabel *act_gameName;
    UILabel *act_openDate;
	UILabel *act_openTime;
    UILabel *act_serversHead;
	UILabel *act_serversName;
	UIButton *act_button;
	UIImageView *act_stamp;
}
@property(nonatomic, readonly) IBOutlet UILabel *act_gameName;
@property(nonatomic, readonly) IBOutlet UILabel *act_openDate;
@property(nonatomic, readonly) IBOutlet UILabel *act_openTime;
@property(nonatomic, readonly) IBOutlet UILabel *act_serversHead;
@property(nonatomic, readonly) IBOutlet UILabel *act_serversName;
@property(nonatomic, readonly) IBOutlet UIButton *act_button;
@property(nonatomic, readonly) IBOutlet UIImageView *act_stamp;

- (void)setCellInfo:(int)act_type withActivityInfo:(ActivityInfo *)info;

+ (NSString *)cellReuseIdentifier;
+ (float)cellHeight;
@end
