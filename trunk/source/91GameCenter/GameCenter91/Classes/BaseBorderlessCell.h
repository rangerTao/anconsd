//
//  NdBYBorderlessCell.h
//  NdComPlatformUI
//
//  Created by Sun pinqun on 13-7-16.
//  Copyright (c) 2013年 NdCP. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface BaseBorderlessCell : UITableViewCell
@property (nonatomic, retain) NSString * jumpUrl;
@property (nonatomic, retain) NSString * jumpTitle;
@property (nonatomic, assign) int activityId;//热点cell用

@property (nonatomic, assign) BOOL bNeedRedBorder;
@end
