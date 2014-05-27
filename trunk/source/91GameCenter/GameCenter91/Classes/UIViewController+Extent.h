//
//  UIViewController+Extent.h
//  Untitled
//
//  Created by Sie Kensou on 11-12-21.
//  Copyright 2011 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UIViewController(TitleExtent)
@property (nonatomic, retain) NSString *customTitle;
- (void)setCustomTitle:(NSString *)customTitle withIcon:(UIImage *)image;
@end

@interface UIViewController(ContainerExtent)
@property (nonatomic, assign) UIViewController *parentContainerController;
- (void)setBadgeNum:(NSInteger)num controller:(UIViewController*)controller bAutoHideWhenZero:(BOOL)bHide;
@end