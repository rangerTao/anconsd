//
//  UnCompletedTaskItemCell.h
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-12.
//  Copyright 2012 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface UnCompletedTaskItemCell : UITableViewCell {
    IBOutlet UILabel *titleLabel;
    IBOutlet UILabel *detailLabel;
    IBOutlet UILabel *scoreLabel;
    IBOutlet UIButton *operateButton;
}

- (void)setInfo:(NSString *)title detail:(NSString *)detail score:(NSInteger)score;
- (void)setButton:(NSInteger)taskId title:(NSString *)title status:(BOOL)status target:(id)target action:(SEL)action;
+ (CGFloat)cellHeight;
@end
