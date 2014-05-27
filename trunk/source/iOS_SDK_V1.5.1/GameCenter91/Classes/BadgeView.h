//
//  BadgeView.h
//  GameCenter91
//
//  Created by  hiyo on 12-8-31.
//  Copyright 2012 Nd. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface BadgeView : UIView {

}
/**
 @brief 添加提示泡
 @param pos	要添加的view右上角位置
 @param num 要显示的数字
 */
+ (BadgeView *)addToView:(UIView *)view Tag:(int)nTag position:(CGPoint)pos num:(int)num;

- (void)setBadgeNum:(NSInteger)num;

@end
