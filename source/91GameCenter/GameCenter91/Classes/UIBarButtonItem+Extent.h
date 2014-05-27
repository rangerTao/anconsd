//
//  UIBarButtonItem+Extent.h
//  
//
//  Created by apple on 12-1-10.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIBarButtonItem(Extent)

+ (UIBarButtonItem *)leftItemWithCustomStyle:(NSString *)title target:(id)target action:(SEL)action;
+ (UIBarButtonItem *)rightItemWithCustomStyle:(NSString *)title target:(id)target action:(SEL)action;
+ (UIBarButtonItem *)unvisableItem;
@end
