//
//  UIBarButtonItem+Extent.m
//  
//
//  Created by apple on 12-1-10.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "UIBarButtonItem+Extent.h"

#define DEFAULT_IMAGE               @"btn_top.png"
#define DEFAULT_PRESSED_IMAGE       @"btn_top_hover.png"

#define DEFAULT_FONT_SIZE           15
#define DEFAULT_FONT_COLOR          [UIColor blackColor]

@implementation UIBarButtonItem(Extent)
+ (UIBarButtonItem *)itemWithCustomStyle:(NSString *)title image:(UIImage *)image pressedImage:(UIImage *)pressedImage target:(id)target action:(SEL)action
{    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn setTitle:title forState:UIControlStateNormal];
    [btn addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
    if (image)
    {
        image = [image stretchableImageWithLeftCapWidth:image.size.width / 2 topCapHeight:image.size.height / 2];
        [btn setBackgroundImage:image forState:UIControlStateNormal];
    }
    
    if (pressedImage)
    {
        pressedImage = [pressedImage stretchableImageWithLeftCapWidth:image.size.width / 2 topCapHeight:image.size.height / 2];
        [btn setBackgroundImage:pressedImage forState:UIControlStateSelected];
    }
//    btn.titleLabel.font = [UIFont boldSystemFontOfSize:DEFAULT_FONT_SIZE];
    btn.titleLabel.font = [UIFont systemFontOfSize:DEFAULT_FONT_SIZE];
//    [btn setTitleColor:DEFAULT_FONT_COLOR forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor colorWithRed:0x3f/255.0 green:0x66/255.0 blue:0x77/255.0 alpha:1.0] forState:UIControlStateNormal];
    
    
    int height = (int)image.size.height;
    CGFloat textWidth = [title sizeWithFont:btn.titleLabel.font constrainedToSize:CGSizeMake(200, height)].width + 10;
    int width = MAX(textWidth, image.size.width);
    
    width = (width % 2 == 0) ? width : width + 1;
    height = (height % 2 == 0) ? height : height + 1;    
    
    btn.frame = CGRectMake(0, 0, width, height);
    UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithCustomView:btn];
    return [item autorelease];
}

+ (UIBarButtonItem *)leftItemWithCustomStyle:(NSString *)title target:(id)target action:(SEL)action
{
    return [self itemWithCustomStyle:title image:[UIImage imageNamed:DEFAULT_IMAGE] pressedImage:[UIImage imageNamed:DEFAULT_PRESSED_IMAGE] target:target action:action];
}

+ (UIBarButtonItem *)rightItemWithCustomStyle:(NSString *)title target:(id)target action:(SEL)action
{
    return [self itemWithCustomStyle:title image:[UIImage imageNamed:DEFAULT_IMAGE] pressedImage:[UIImage imageNamed:DEFAULT_PRESSED_IMAGE] target:target action:action];
}

+ (UIBarButtonItem *)unvisableItem
{
    return [[[UIBarButtonItem alloc] initWithCustomView:[[UIView new] autorelease]]autorelease];
}
@end
