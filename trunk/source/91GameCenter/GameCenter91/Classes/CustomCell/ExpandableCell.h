//
//  ExpandableCell.h
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-5.
//  Copyright 2012 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface ExpandableCell : UITableViewCell {

}

@property (nonatomic, readonly) IBOutlet UIView         *expandView;
@property (nonatomic, readonly) IBOutlet UIButton       *expandLeftButton;
@property (nonatomic, readonly) IBOutlet UIButton       *expandRightButton;

@property (nonatomic, assign) id expandLeftButtonTarget;
@property (nonatomic, assign) SEL expandLeftButtonAction;
@property (nonatomic, assign) id expandRightButtonTarget;
@property (nonatomic, assign) SEL expandRightButtonAction;

@property (nonatomic, retain) NSString *appIdentifier;
@property (nonatomic, retain) NSString *gameName;//传给详情页用到的参数

- (void)reset;
- (void)setExpand:(BOOL)expand showRoundCorner:(BOOL)showRoundCorner;

- (void)setExpandLeftButtonAction:(id)target action:(SEL)action;
- (void)setExpandRightButtonAction:(id)target action:(SEL)action;


+ (CGFloat)normalCellHeight;
+ (CGFloat)expandedCellHeight;



@end
