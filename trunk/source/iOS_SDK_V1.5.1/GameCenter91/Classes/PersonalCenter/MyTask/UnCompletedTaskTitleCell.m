//
//  UnCompletedTaskTitleCell.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-7.
//  Copyright 2012 net dragon. All rights reserved.
//

#import "UnCompletedTaskTitleCell.h"


@implementation UnCompletedTaskTitleCell


-(void) awakeFromNib
{
	self.selectionStyle = UITableViewCellSelectionStyleNone;
}


- (void)setSelected:(BOOL)selected animated:(BOOL)animated {

    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}


- (void)dealloc {
    [super dealloc];
}


+ (CGFloat)cellHeight {
    return 44;
}

@end
