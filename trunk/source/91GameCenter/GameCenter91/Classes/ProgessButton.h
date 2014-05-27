//
//  ProgessButton.h
//  GameCenter91
//
//  Created by Sun pinqun on 12-11-29.
//  Copyright 2012 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>

//区分显示在哪个位置上的cell被点击了，用于用户行为统计
typedef enum _ProgessButton_DISPLAY_POSE {
	ProgessButton_DISPLAY_POSE_DEF = 0x00,
	ProgessButton_DISPLAY_POSE_PERSON = 200,             //个人中心页
}ProgessButton_DISPLAY_POSE;

@class SoftItem;
@interface ProgessButton : UIView {
    float progess;
    UILabel *percent;
    UIImageView *progessView;
    UIImageView *trackView;
    
    ProgessButton_DISPLAY_POSE pose_type;
}

@property(nonatomic, assign) BOOL bShowAppName;
@property(nonatomic, assign) ProgessButton_DISPLAY_POSE pose_type;
@property(nonatomic, retain) UIButton *normalButton;

@property(nonatomic, retain) NSString *identifier;
@property(nonatomic, assign) int f_id;
@property(nonatomic, retain) NSString *softName;
@property(nonatomic, retain) NSString *iconUrl;
@property(nonatomic, assign) BOOL showUpgrade;

- (void)setProgressButtonInfo:(NSString *)aIdentifier f_id:(int)aF_id softName:(NSString *)aSoftName iconUrl:(NSString *)aIconUrl;
- (void)updateProgessButtonState:(NSString *)aIdentifier;
- (void)reset;
- (void)setNormalButtonInstalling;
- (void)setProgressButtonTitle:(NSString *)aIdentifier f_id:(int)aF_id softName:(NSString *)aSoftName iconUrl:(NSString *)aIconUrl;
- (void)showButtonTitleOrProgress:(NSString *)aIdentifier;
- (void)updateProgessButtonTitle:(NSString *)aIdentifier;

- (void)resetNormalButtonWithFontSize:(UIFont *)font;
- (void)resetNormalButtonWithTitleColor:(UIColor *)color;
- (void)resetNormalButtonWithBackgroundImage:(UIImage *)image;

- (void)resetPercentFontSize:(UIFont *)font;

@end
