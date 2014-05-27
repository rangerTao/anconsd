//
//  SectionHeadCell.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-30.
//
//

#import "BaseBorderlessCell.h"

@interface SectionHeadCell : BaseBorderlessCell
@property (nonatomic, retain) UIView *lineView;
//更多
- (void)addMoreRightButtonWithTarget:(id)target action:(SEL)action forControlEvents:(UIControlEvents)controlEvents;
//展开
- (void)addexpendRightButtonWithTarget:(id)target action:(SEL)action forControlEvents:(UIControlEvents)controlEvents;
- (void)removeExpendRightButton;

@end
