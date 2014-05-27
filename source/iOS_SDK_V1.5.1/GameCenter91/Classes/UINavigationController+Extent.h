//
//  UIViewController+Extent.h
//  Untitled
//
//  Created by Sie Kensou on 11-12-21.
//  Copyright 2011 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

#define CUSTOM_BACKGOURD_TAG    65432

@interface UINavigationBar(CustomizeBar)
+ (void)switchMethods;
+ (void)switchMethod:(SEL)origSEL newMethod:(SEL)newSEL;
+ (void)switchMethodForLeftBackItem;
- (void)clInsertSubview:(UIView *)view atIndex:(NSInteger)index;
- (void)clSendSubviewToBack:(UIView *)view;
@end

@interface UINavigationController(CustomizeBar)
+ (void)prepareCustomizeNavigationBar;
- (void)customizeNavigationBar;
- (void)setupBackgroundImage:(NSString *)imageName;
@end
