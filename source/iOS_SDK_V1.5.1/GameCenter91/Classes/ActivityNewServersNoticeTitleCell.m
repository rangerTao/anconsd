//
//  ActivityNewServersNoticeTitleCell.m
//  GameCenter91
//
//  Created by Sun pinqun on 13-01-28.
//  Copyright (c) 2012年 net dragon. All rights reserved.
//

#import "ActivityNewServersNoticeTitleCell.h"

@implementation ActivityNewServersNoticeTitleCell

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
    return 35;
}

@end
