//
//  UILabel+Extent.m
//  GameCenter91
//
//  Created by Sun pinqun on 13-2-4.
//  Copyright (c) 2013年 net dragon. All rights reserved.
//

#import "UILabel+Extent.h"
#import <QuartzCore/QuartzCore.h>

@implementation UILabel (Extent)
- (void)contentFlicker
{
    //缩放动画
    CABasicAnimation *scaleAnim = [CABasicAnimation animationWithKeyPath:@"transform"];
    scaleAnim.fromValue = [NSValue valueWithCATransform3D:CATransform3DIdentity];
    scaleAnim.toValue = [NSValue valueWithCATransform3D:CATransform3DMakeScale(2.0, 2.0, 1.0)];
    scaleAnim.removedOnCompletion = YES;

     //透明动画
    CABasicAnimation *opacityAnim = [CABasicAnimation animationWithKeyPath:@"opacity"];
    opacityAnim.fromValue = [NSNumber numberWithFloat:1.0];
    opacityAnim.toValue = [NSNumber numberWithFloat:0.1];
    opacityAnim.removedOnCompletion = YES;
     
     //动画组
    CAAnimationGroup *animGroup = [CAAnimationGroup animation];
    animGroup.animations = [NSArray arrayWithObjects:scaleAnim, opacityAnim,nil];
    animGroup.autoreverses = YES;
    animGroup.duration = 0.3;
    animGroup.repeatCount = 2;
    animGroup.fillMode = kCAFillModeForwards;

    [self.layer addAnimation:animGroup forKey:nil];
}
@end
