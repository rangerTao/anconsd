//
//  TabContainerController.h
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-3.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TabViewCtrl.h"

@interface TabContainerController : TabViewCtrl
@property (nonatomic, retain) NSArray *subControllers;
+ (TabContainerController *)controllerWithSubControllers:(NSArray *)subControllers subviewHight:(CGFloat)subviewHeight;

- (void)setBadgeNumber:(int)num forSubController:(UIViewController *)controller bAutoHideWhenZero:(BOOL)bHide;
@end
