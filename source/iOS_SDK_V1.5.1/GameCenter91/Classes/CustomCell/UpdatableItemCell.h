//
//  UpdatableItemCell.m
//  testCell
//
//  Created by Sun pinqun on 12-8-20.
//  Copyright net dragon 2012. All rights reserved.
//
#import "ExpandableCell.h"

@class SoftItem;
@class AppDetailViewInfo;
@interface UpdatableItemCell : UITableViewCell {

}

@property (nonatomic, readonly) IBOutlet UIImageView		*appImage;
@property (nonatomic, readonly) IBOutlet UILabel			*appName;
@property (nonatomic, readonly) IBOutlet UILabel			*currentVersion;
@property (nonatomic, readonly) IBOutlet UILabel			*direction;
@property (nonatomic, readonly) IBOutlet UILabel			*nextVersion;
@property (nonatomic, readonly) IBOutlet UILabel			*appSize;
@property (nonatomic, readonly) IBOutlet UIButton           *rightButton;
@property (nonatomic, readonly) IBOutlet UIView             *delLine;
@property (nonatomic, readonly) IBOutlet UILabel            *increSize;

@property (nonatomic, retain) NSString *appIdentifier;

- (void)setRightButtonAction:(id)target action:(SEL)action;

- (void)setSoftInfo:(SoftItem *)item;

- (void)adjustForDetailViewWithInfo:(AppDetailViewInfo *)info;
+ (CGFloat)cellHeight;
@end
