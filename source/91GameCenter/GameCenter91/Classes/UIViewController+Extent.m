//
//  UIViewController+Extent.m
//  Untitled
//
//  Created by Sie Kensou on 11-12-21.
//  Copyright 2011 NetDragon WebSoft Inc. All rights reserved.
//

#import "UIViewController+Extent.h"
#import <QuartzCore/QuartzCore.h>
#import <objc/runtime.h>
#import "TabContainerController.h"

#define DEFAULT_CUSTOM_TITLE_FRAME CGRectMake(90, 0, 140, 44)

#define DEFAULT_TITLE_COLOR     [UIColor colorWithRed:0x0b/255.0 green:0x71/255.0 blue:0xa3/255.0 alpha:1]
#define DEFAULT_TITLE_FONT      [UIFont boldSystemFontOfSize:20]
#define DEFAULT_SHADOW_OFFSET   CGSizeMake(1,1)
#define DEFAULT_SHADOW_COLOR    [UIColor colorWithRed:1 green:1 blue:1 alpha:1]

@implementation UIViewController(TitleExtent)


- (void)setCustomBackButtonItem:(NSString *)backTitle
{
    UIBarButtonItem *item = [[[UIBarButtonItem alloc] initWithTitle:backTitle style:UIBarButtonItemStylePlain target:nil action:nil] autorelease];    
#ifdef __IPHONE_5_0            
    if ([[UIBarButtonItem class]respondsToSelector:@selector(appearance)]) //IOS5
    {

        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:[UIColor blackColor], UITextAttributeTextColor, [UIColor whiteColor], UITextAttributeTextShadowColor, [UIFont boldSystemFontOfSize:15], UITextAttributeFont, nil];
        [item setTitleTextAttributes:dict forState:UIControlStateNormal];
//        [item setTitlePositionAdjustment:UIOffsetMake(0, -2) forBarMetrics:UIBarMetricsDefault];
        [item setBackButtonTitlePositionAdjustment:UIOffsetMake(2, -2) forBarMetrics:UIBarMetricsDefault];
    }
#endif  
    self.navigationItem.backBarButtonItem = item;    
}



- (void)setCustomTitle:(NSString *)customTitle
{    
    UILabel *titleLabel = [[[UILabel alloc] initWithFrame:DEFAULT_CUSTOM_TITLE_FRAME] autorelease];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.text = customTitle;
    titleLabel.textColor = DEFAULT_TITLE_COLOR;
    titleLabel.font = DEFAULT_TITLE_FONT;
    titleLabel.shadowColor = DEFAULT_SHADOW_COLOR;
    titleLabel.shadowOffset = DEFAULT_SHADOW_OFFSET;
    titleLabel.textAlignment = UITextAlignmentCenter;
    
    self.navigationItem.titleView = titleLabel;
    //[self setCustomBackButtonItem:customTitle];
}

- (void)setCustomTitle:(NSString *)customTitle withIcon:(UIImage *)image
{
    UIButton *titleButton = [[[UIButton alloc] initWithFrame:DEFAULT_CUSTOM_TITLE_FRAME] autorelease];
    [titleButton setTitle:customTitle forState:UIControlStateNormal];
    [titleButton setTitleColor:DEFAULT_TITLE_COLOR forState:UIControlStateNormal];
    titleButton.titleLabel.textAlignment = UITextAlignmentCenter;
    titleButton.titleLabel.font = DEFAULT_TITLE_FONT;
    titleButton.titleLabel.backgroundColor = [UIColor clearColor];
    titleButton.titleLabel.shadowOffset = DEFAULT_SHADOW_OFFSET;
    [titleButton setTitleShadowColor:DEFAULT_SHADOW_COLOR forState:UIControlStateNormal];
    titleButton.userInteractionEnabled = NO;
    if (image)
    {
        [titleButton setImage:image forState:UIControlStateNormal];
        titleButton.titleEdgeInsets = UIEdgeInsetsMake(0, 10, 0, 0);
    }
    
    self.navigationItem.titleView = titleButton;
    //[self setCustomBackButtonItem:customTitle];
}

- (NSString *)customTitle
{
    UIView *titleView = self.navigationItem.titleView;
    if ([titleView isKindOfClass:[UILabel class]])
    {
        return  ((UILabel *)self.navigationItem.titleView).text;
    }
    else if ([titleView isKindOfClass:[UIButton class]])
    {
        return  ((UIButton *)self.navigationItem.titleView).titleLabel.text;
    }
    return nil;
}

@end

#define CONTAINER_KEY "ParentContainer"

@implementation UIViewController(ContainerExtent)

- (void)setParentContainerController:(UIViewController *)parentContainerController
{
    objc_setAssociatedObject(self, CONTAINER_KEY, parentContainerController, OBJC_ASSOCIATION_ASSIGN);
}

- (UIViewController *)parentContainerController
{
    return objc_getAssociatedObject(self, CONTAINER_KEY);
}

- (void)setBadgeNum:(NSInteger)num controller:(UIViewController*)controller bAutoHideWhenZero:(BOOL)bHide
{
    if (self.parentContainerController == nil)
        return;
    
    TabContainerController *containerController = (TabContainerController *)self.parentContainerController;
    CustomSegment *seg = containerController.seg;
    NSInteger index = [containerController.subControllers indexOfObject:controller];
    [seg setBadgeNum:num atIndex:index bAutoHideWhenZero:bHide];
}
@end