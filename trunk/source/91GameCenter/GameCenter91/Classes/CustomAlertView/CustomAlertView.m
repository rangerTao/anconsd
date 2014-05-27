//
//  CustomAlertView.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-10-18.
//  Copyright 2012 net dragon. All rights reserved.
//

#define kAlertViewBounce         20
#define kAlertViewBorder         10
#define kAlertButtonHeight       44

#define kAlertViewTitleFont             [UIFont boldSystemFontOfSize:18]
#define kAlertViewTitleTextColor        [UIColor colorWithRed:0x2a/255.0 green:0x7d/255.0 blue:0xb7/255.0 alpha:1]
#define kAlertViewTitleShadowColor      [UIColor colorWithRed:0x2a/255.0 green:0x7d/255.0 blue:0xb7/255.0 alpha:0.4]
#define kAlertViewTitleShadowOffset     CGSizeMake(0, -1)

#define kAlertViewMessageFont           [UIFont systemFontOfSize:16]
#define kAlertViewMessageTextColor      [UIColor colorWithRed:0x8b/255.0 green:0x8b/255.0 blue:0x8b/255.0 alpha:1]
#define kAlertViewMessageShadowColor    [UIColor blackColor]
#define kAlertViewMessageShadowOffset   CGSizeMake(0, -1)

#define kAlertViewButtonFont            [UIFont boldSystemFontOfSize:18]
#define kAlertViewButtonTextColor       [UIColor whiteColor]
#define kAlertViewButtonShadowColor     [UIColor blackColor]
#define kAlertViewButtonShadowOffset    CGSizeMake(0, -1)

#define kAlertViewBackground                    @"bg_pop.png"
#define kAlertViewCloseButtonImage              @"btn_close.png"
#define kAlertViewDefaultButtonImage            @"btn_ok.png"
#define kAlertViewSelectedDefaultButtonImage    @"btn_ok_down.png"
#define kAlertViewCancelButtonImage             @"btn_cancel.png"
#define kAlertViewSelectedCancelButtonImage     @"btn_cancel_down.png"

#define kAlertViewBackgroundCapHeight   38

#import "CustomAlertView.h"

@interface CustomAlertView()
@property (nonatomic, assign) BOOL isShowCloseButton;
@end

@implementation CustomAlertView
@synthesize isShowCloseButton;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Close Button
        if (self.isShowCloseButton) {
            UIButton *closeButton = [[UIButton alloc]initWithFrame:CGRectMake(257,-5,25,25)];
            [closeButton setBackgroundImage:[UIImage imageNamed:kAlertViewCloseButtonImage] forState:UIControlStateNormal];
            closeButton.autoresizingMask = UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleBottomMargin;
            [closeButton addTarget:self action:@selector(dismiss) forControlEvents:UIControlEventTouchUpInside];
            [self addSubview:closeButton];
            [closeButton release];
        }
    }
    return self;
}
-(void)layoutSubviews{
    //IOS 4.2以下不做适配
    NSString *version = [[UIDevice currentDevice] systemVersion];
    if ([version compare:@"5.0"] == NSOrderedAscending)
        return;

    for (UIView *v in self.subviews) {
        if ([v isKindOfClass:[UIImageView class]]) {
            UIImageView *imageV = (UIImageView *)v;
            UIImage *image = [UIImage imageNamed:kAlertViewBackground];
            image = [image stretchableImageWithLeftCapWidth:0 topCapHeight:kAlertViewBackgroundCapHeight];
            [imageV setImage:image];
        }
        if ([v isKindOfClass:[UILabel class]]) {
            UILabel *label = (UILabel *)v;
            if ([label.text isEqualToString:self.title]) {
                label.font = kAlertViewTitleFont;
                label.numberOfLines = 0;
                label.lineBreakMode = UILineBreakModeWordWrap;
                label.textColor = kAlertViewTitleTextColor;
                label.backgroundColor = [UIColor clearColor];
                label.textAlignment = UITextAlignmentCenter;
                label.shadowColor = nil;
                //label.shadowOffset = kAlertViewTitleShadowOffset;
            }else{
                label.font = kAlertViewMessageFont;
                label.numberOfLines = 0;
                label.lineBreakMode = UILineBreakModeWordWrap;
                label.textColor = kAlertViewMessageTextColor;
                label.backgroundColor = [UIColor clearColor];
                label.textAlignment = UITextAlignmentCenter;
                label.shadowColor = nil;
                //label.shadowOffset = kAlertViewMessageShadowOffset;
            }
        }
        if ([v isKindOfClass:NSClassFromString(@"UIAlertButton")]) {
            UIImage *normalImage = nil;
            UIImage *highLightedImage = nil;
            if (v.tag == 1) {
                normalImage = [UIImage imageNamed:kAlertViewDefaultButtonImage];
                highLightedImage = [UIImage imageNamed:kAlertViewSelectedDefaultButtonImage];
            }else{
                normalImage = [UIImage imageNamed:kAlertViewCancelButtonImage];
                highLightedImage = [UIImage imageNamed:kAlertViewSelectedCancelButtonImage];
            }
            normalImage = [normalImage stretchableImageWithLeftCapWidth:(int)(normalImage.size.width+1)>>1 topCapHeight:0];
            highLightedImage = [highLightedImage stretchableImageWithLeftCapWidth:(int)(highLightedImage.size.width+1)>>1 topCapHeight:0];
            
            UIButton *button = (UIButton *)v;
            button.titleLabel.font = kAlertViewButtonFont;
            button.titleLabel.minimumFontSize = 10;
            button.titleLabel.textAlignment = UITextAlignmentCenter;
            button.titleLabel.shadowOffset = kAlertViewButtonShadowOffset;
            button.backgroundColor = [UIColor clearColor];
            [button setBackgroundImage:normalImage forState:UIControlStateNormal];
            [button setBackgroundImage:highLightedImage forState:UIControlStateHighlighted];
            [button setTitleColor:kAlertViewButtonTextColor forState:UIControlStateNormal];
            [button setTitleShadowColor:kAlertViewButtonShadowColor forState:UIControlStateNormal];
        }
        //for IOS 4
        if ([v isKindOfClass:NSClassFromString(@"UIThreePartButton")]) {
            UIImage *normalImage = nil;
            UIImage *highLightedImage = nil;
            if (v.tag == 1) {
                normalImage = [UIImage imageNamed:kAlertViewDefaultButtonImage];
                highLightedImage = [UIImage imageNamed:kAlertViewSelectedDefaultButtonImage];
            }else{
                normalImage = [UIImage imageNamed:kAlertViewCancelButtonImage];
                highLightedImage = [UIImage imageNamed:kAlertViewSelectedCancelButtonImage];
            }
            normalImage = [normalImage stretchableImageWithLeftCapWidth:(int)(normalImage.size.width+1)>>1 topCapHeight:0];
            highLightedImage = [highLightedImage stretchableImageWithLeftCapWidth:(int)(highLightedImage.size.width+1)>>1 topCapHeight:0];
            
            [v performSelector:@selector(setBackgroundImage:) withObject:normalImage];
            [v performSelector:@selector(setPressedBackgroundImage:) withObject:highLightedImage];
        }
    }
}

- (id)initWithCloseButton:(NSString *)title message:(NSString *)message delegate:(id)delegate firstButtonTitle:(NSString *)firstButtonTitle secondButtonTitles:(NSString *)secondButtonTitles
{
    self.isShowCloseButton = YES;
    return [super initWithTitle:title message:message delegate:delegate cancelButtonTitle:nil otherButtonTitles:firstButtonTitle, secondButtonTitles, nil];
}

- (void)dismiss {
    [self dismissWithClickedButtonIndex:0 animated:YES];
}

@end
