//
//  CompletedTaskItemCell.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-12.
//  Copyright 2012 net dragon. All rights reserved.
//

#import "CompletedTaskItemCell.h"


@implementation CompletedTaskItemCell

-(void)reset
{
	titleLabel.text = @"";
	detailLabel.text = @"";
    scoreLabel.text = @"0";
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


+ (CGFloat)cellHeight {
    return 60.0f;
}

@end
