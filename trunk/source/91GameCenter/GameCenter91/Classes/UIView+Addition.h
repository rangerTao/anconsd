//
//  UIView+Addition.h
//  GameCenter91
//
//  Created by  hiyo on 12-8-31.
//  Copyright 2012 Nd. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface UIView (Addition)

/**
 @brief 在屏幕中心区域显示提示语，并自动消失
 @param tip	提示语
 */
- (void)showTip:(NSString *)tip;

+ (id)loadFromNIB;
+ (id)loadWithNibName:(NSString *)nibName;

@end
