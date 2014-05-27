//
//  FlexibleTextView.h
//  GameCenter91
//
//  Created by hiyo on 13-2-1.
//  Copyright (c) 2013年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FlexibleTextView : UIView
{
    id delegate;
}
@property (nonatomic, assign) id delegate;
@property (nonatomic, retain) UITapGestureRecognizer *tapGest;
@property (nonatomic, assign) BOOL expendFlag;
@property (nonatomic, retain) UIView *expendView;

+ (FlexibleTextView *)flexibleTextViewWithText:(NSString *)str originXY:(CGPoint)originXY delegate:(id)del; 
- (void)btnPress:(UITapGestureRecognizer *)gesture;
@end

@protocol FlexibleTextViewProtocol <NSObject>

@optional
- (void)flexibleTextViewFrameChanged:(NSNumber *)offset;

@end
