//
//  UIView+Addition.m
//  GameCenter91
//
//  Created by  hiyo on 12-8-31.
//  Copyright 2012 Nd. All rights reserved.
//

#import "UIView+Addition.h"
#import <QuartzCore/QuartzCore.h>


@implementation UIView (Addition)

#define TAG_TIP		1000
#define MARGIN_HOR	20.0f
#define MARGIN_VER	10.0f

- (void)removeTip:(id)sender
{
	UIView *tip = [(UIView *)sender viewWithTag:TAG_TIP];
	[tip removeFromSuperview];
}

- (void)showTip:(NSString *)tip
{
	UIView *successView = [[[UIView alloc] initWithFrame:CGRectMake(0, 0, 120.0, 50.0)] autorelease];
	successView.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.5];
	BOOL bIsLandscape = UIInterfaceOrientationIsLandscape([[UIDevice currentDevice] orientation]);
	float width = bIsLandscape ? 480.0 : 320.0;
	float height = bIsLandscape ? 320.0 : 480.0;
	successView.layer.cornerRadius = 6.0;
	successView.tag = TAG_TIP;
	
	UILabel *label = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, width/2.0, 50.0)] autorelease];
	label.text = tip;
	label.textAlignment = UITextAlignmentCenter;
	label.textColor = [UIColor whiteColor];
	label.backgroundColor = [UIColor clearColor];
	label.numberOfLines = 0;
	CGSize labelSize = [tip sizeWithFont:label.font constrainedToSize:CGSizeMake(width/2.0, 300.0)];
	float finalWidth = MARGIN_HOR * 2 + labelSize.width;
	float finalHeight = MARGIN_VER * 2 + labelSize.height;
	label.center = CGPointMake(finalWidth/2, finalHeight/2);
	CGRect rc = successView.frame;
	rc.size.width = finalWidth;
	rc.size.height = finalHeight;
	successView.frame = rc;
	UIView *top = [[[[[UIApplication sharedApplication] windows] objectAtIndex:0] subviews] objectAtIndex:0];
	CGPoint pt = [self convertPoint:CGPointMake(width/2.0, height/2.0) fromView:top];
	successView.center = pt;
	
	[successView addSubview:label];
	[self addSubview:successView];
	
	[UIView beginAnimations:@"fadeTip" context:nil];
    [UIView setAnimationDuration:2.0f];
    successView.alpha = 0.0;	
    [UIView commitAnimations];
	
	[self performSelector:@selector(removeTip:) withObject:self afterDelay:2.0f];
}

+ (NSString*)nibName {
    return [self description];
}

+ (id)loadFromNIB {
    Class klass = [self class];
    NSString *nibName = [self nibName];
    NSArray* objects = [[NSBundle mainBundle] loadNibNamed:nibName owner:self options:nil];
	
    for (id object in objects) {
        if ([object isKindOfClass:klass]) {
            return object;
        }
    }
	
    [NSException raise:@"WrongNibFormat" format:@"Nib for '%@' must contain one UIView, and its class must be '%@'", nibName, NSStringFromClass(klass)];
	
    return nil;
}

+ (id)loadWithNibName:(NSString *)nibName
{
    NSArray* objects = [[NSBundle mainBundle] loadNibNamed:nibName owner:self options:nil];
	
    for (id object in objects) {
        if ([object isKindOfClass:NSClassFromString(@"UIView")]) {
            return object;
        }
    }
	
    [NSException raise:@"WrongNibFormat" format:@"Nib for '%@' must contain one UIView, and its class must be '%@'", nibName, NSClassFromString(@"UIView")];
    return nil;
}

@end