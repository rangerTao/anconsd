//
//  UIViewController+Extent.m
//  Untitled
//
//  Created by Sie Kensou on 11-12-21.
//  Copyright 2011 NetDragon WebSoft Inc. All rights reserved.
//

#import "UINavigationController+Extent.h"
#import <QuartzCore/QuartzCore.h>
#import <objc/runtime.h>
#import <objc/message.h>
#import "UIImage+Extent.h"

#define CUSTOM_NAVBAR_TAG   'navb'

@implementation UINavigationController(CustomizeBar)

+ (UIImage *)customBgImage
{
    return [[UIImage imageNamed:@"top_bg.png"] stretchableImageWithCenterPoint];
}

+ (void)prepareCustomizeNavigationBar
{
#ifdef __IPHONE_5_0    
    if ([[UINavigationBar class]respondsToSelector:@selector(appearance)]) //IOS5
    {
        [UINavigationBar switchMethodForLeftBackItem];
    }
    else
#endif 
    {
        [UINavigationBar switchMethods];
    }
}

- (void)customizeNavigationBar
{    
#ifdef __IPHONE_5_0    
    if ([[UINavigationBar class]respondsToSelector:@selector(appearance)]) //IOS5
    {
        //do nothing, for we have do it in prepareCustomizeNavigationBar
        [self.navigationBar setBackgroundImage:[UINavigationController customBgImage] forBarMetrics:UIBarMetricsDefault];        
    }
    else
#endif        
    {
        UIImageView *imageView = (UIImageView *)[self.navigationBar viewWithTag:CUSTOM_BACKGOURD_TAG];
        if (imageView == nil)
        {
            imageView = [[UIImageView alloc] initWithImage:[UINavigationController customBgImage]];
            imageView.frame = self.navigationBar.bounds;
            [imageView setTag:CUSTOM_BACKGOURD_TAG];
            [self.navigationBar addSubview:imageView];
            [imageView release];
            [self.navigationBar sendSubviewToBack:imageView];
        }
    }    
    self.navigationBar.tag = CUSTOM_NAVBAR_TAG;
}

- (void)setupBackgroundImage:(NSString *)imageName
{
    UIImageView *bgView = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:imageName]]autorelease];
    CGRect rt = bgView.frame;
    rt.origin.y = 20;
    bgView.frame = rt;
    [self.view addSubview:bgView];
    [self.view sendSubviewToBack:bgView];
}

@end


@implementation UINavigationBar(CustomizeBar)

+ (void)switchMethods
{
    [self switchMethod:@selector(insertSubview:atIndex:) newMethod:@selector(clInsertSubview:atIndex:)];
    [self switchMethod:@selector(sendSubviewToBack:) newMethod:@selector(clSendSubviewToBack:)];
    [self switchMethodForLeftBackItem];
}

+ (void)switchMethodForLeftBackItem
{
    [self switchMethod:@selector(drawBackButtonBackgroundInRect:withStyle:pressed:) newMethod:@selector(clDrawBackButtonBackgroundInRect:withStyle:pressed:)];

    if ([[UINavigationBar class]respondsToSelector:@selector(appearance)] == NO) //IOS4
    {
        [self switchMethod:@selector(buttonItemTextColor) newMethod:@selector(clButtonItemTextColor)];
        [self switchMethod:@selector(buttonItemShadowColor) newMethod:@selector(clButtonItemShadowColor)];
    }
}

+ (void)switchMethod:(SEL)origSEL newMethod:(SEL)newSEL
{
    Class c = [self class];
    Method origMethod = class_getInstanceMethod(c, origSEL);
    Method newMethod = class_getInstanceMethod(c, newSEL);
    
    if (class_addMethod(c, origSEL, method_getImplementation(newMethod),
                        method_getTypeEncoding(newMethod)))
    {
        class_replaceMethod(c, newSEL, method_getImplementation(origMethod),
                            method_getTypeEncoding(origMethod));
    }
    else
    {
        method_exchangeImplementations(origMethod, newMethod);
    }
}

- (void)clInsertSubview:(UIView *)view atIndex:(NSInteger)index
{
    [self clInsertSubview:view atIndex:index];
    
    UIView *backgroundImageView = [self viewWithTag:CUSTOM_BACKGOURD_TAG];
    if (backgroundImageView != nil)
    {
        [self clSendSubviewToBack:backgroundImageView];
    }
}

- (void)clSendSubviewToBack:(UIView *)view
{
    [self clSendSubviewToBack:view];
    
    UIView *backgroundImageView = [self viewWithTag:CUSTOM_BACKGOURD_TAG];
    if (backgroundImageView != nil)
    {
        [self clSendSubviewToBack:backgroundImageView];
    }
}

- (UIColor *)clButtonItemTextColor
{
    if (self.tag == CUSTOM_NAVBAR_TAG)
        return [UIColor blackColor];
    return [self clButtonItemTextColor];
}

- (UIColor *)clButtonItemShadowColor
{
    if (self.tag == CUSTOM_NAVBAR_TAG)      
        return [UIColor whiteColor];
    return [self clButtonItemShadowColor];
}

- (void)clDrawBackButtonBackgroundInRect:(struct CGRect)rect withStyle:(int)style pressed:(BOOL)pressed
//- (void)drawBackButtonBackgroundInRect:(struct CGRect)rect withStyle:(int)style pressed:(BOOL)pressed
{
    if (self.tag == CUSTOM_NAVBAR_TAG)
    {
//        CGContextRef context = UIGraphicsGetCurrentContext();
//	    CGContextSaveGState( context );
//        
//        UIImage *img = pressed ? [UIImage imageNamed:@"btn_sys_back_down.png"] : [UIImage imageNamed:@"btn_sys_back.png"];
//        img = [img stretchableImageWithLeftCapWidth:30 topCapHeight:20];
//        [img drawInRect:rect];            
    }
    else
    {
        [self clDrawBackButtonBackgroundInRect:rect withStyle:style pressed:pressed];
    }
}

@end

