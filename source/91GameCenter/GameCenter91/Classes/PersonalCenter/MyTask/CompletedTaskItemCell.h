//
//  CompletedTaskItemCell.h
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-12.
//  Copyright 2012 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface CompletedTaskItemCell : UITableViewCell {
    IBOutlet UILabel *titleLabel;
    IBOutlet UILabel *detailLabel;
    IBOutlet UILabel *scoreLabel;
}

- (void)setInfo:(NSString *)title detail:(NSString *)detail score:(NSInteger)score;

+ (CGFloat)cellHeight;
@end
