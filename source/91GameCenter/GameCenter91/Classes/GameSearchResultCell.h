//
//  GameSearchResultCell.h
//  GameCenter91
//
//  Created by hiyo on 12-9-25.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GameSearchResultCell : UITableViewCell
{
    UILabel	*result_title;
    UILabel	*result_num;
}
@property(nonatomic, readonly) IBOutlet UILabel *result_title;
@property(nonatomic, readonly) IBOutlet UILabel *result_num;


- (void)setTitle:(NSString *)title num:(int)num; 

@end
