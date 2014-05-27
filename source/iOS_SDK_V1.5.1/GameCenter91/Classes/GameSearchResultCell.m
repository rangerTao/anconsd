//
//  GameSearchResultCell.m
//  GameCenter91
//
//  Created by hiyo on 12-9-25.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "GameSearchResultCell.h"

@implementation GameSearchResultCell
@synthesize result_title;
@synthesize result_num;

- (void)setTitle:(NSString *)title num:(int)num
{
    result_title.text = title;
    result_num.text = [NSString stringWithFormat:@"%d人玩过", num];
}

@end
