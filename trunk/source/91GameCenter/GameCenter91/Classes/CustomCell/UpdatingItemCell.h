//
//  UpdatingItemCell.m
//  testCell
//
//  Created by Sun pinqun on 12-8-20.
//  Copyright net dragon 2012. All rights reserved.
//
#import "ExpandableCell.h"
@class SoftItem;

@interface UpdatingItemCell : ExpandableCell {

}

@property (nonatomic, readonly) IBOutlet UIImageView		*appImage;
@property (nonatomic, readonly) IBOutlet UILabel			*appName;
@property (nonatomic, readonly) IBOutlet UIProgressView     *progress;
@property (nonatomic, readonly) IBOutlet UIButton           *rightButton;
@property (nonatomic, readonly) IBOutlet UILabel			*updatePercent;

- (void)setRightButtonAction:(id)target action:(SEL)action;
- (void)setSoftItem:(SoftItem *)item;

@end
