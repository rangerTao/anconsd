//
//  UnCompletedTaskItemCell.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-12.
//  Copyright 2012 net dragon. All rights reserved.
//

#import "UnCompletedTaskItemCell.h"


@implementation UnCompletedTaskItemCell

-(void)reset
{
	titleLabel.text = @"";
	detailLabel.text = @"";
    scoreLabel.text = @"0";
    operateButton.enabled = YES;
    self.selectionStyle = UITableViewCellSelectionStyleNone;
}

-(void) awakeFromNib
{
	[self reset];
}


- (void)setSelected:(BOOL)selected animated:(BOOL)animated {

    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}


- (void)dealloc {
    
    [super dealloc];
}


- (void)setInfo:(NSString *)title detail:(NSString *)detail score:(NSInteger)score {
    titleLabel.text = title;
	detailLabel.text = detail;
    scoreLabel.text = [NSString stringWithFormat:@"%d", score];
}

- (void)setButton:(NSInteger)taskId title:(NSString *)title status:(BOOL)status target:(id)target action:(SEL)action {
    if (operateButton) {
        [operateButton setTitle:title forState:UIControlStateNormal];
        operateButton.tag = taskId;
        operateButton.enabled = status;
		[operateButton removeTarget:target action:NULL forControlEvents:UIControlEventTouchUpInside];
		[operateButton addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
	}
}

+ (CGFloat)cellHeight {
    return 60.0f;
}

@end
