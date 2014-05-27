//
//  BadgeView.m
//  GameCenter91
//
//  Created by  hiyo on 12-8-31.
//  Copyright 2012 Nd. All rights reserved.
//

#import "BadgeView.h"

#define MARGIN_HOR	4.0f
#define MARGIN_VER	2.0f
#define TIP_TAG     100
#define IMAGE_TAG   200

#define kBackgroundImage @"number.png"

@implementation BadgeView


+ (BadgeView *)addToView:(UIView *)view Tag:(int)nTag position:(CGPoint)pos num:(int)num
{
	BadgeView *badge = [[[BadgeView alloc] init] autorelease];
	badge.tag = nTag;
	
	UILabel *tip = [[[UILabel alloc] init] autorelease];
    tip.tag = TIP_TAG;
	tip.font = [UIFont systemFontOfSize:13];
	tip.backgroundColor = [UIColor clearColor];
	tip.textColor = [UIColor whiteColor];
	tip.textAlignment = UITextAlignmentCenter;
	tip.text = [NSString stringWithFormat:@"%d", num];
	CGSize size =  [tip.text sizeWithFont:tip.font constrainedToSize:CGSizeMake(100, 20)];
    size.width = (size.width <16) ? 16 : size.width;
	tip.frame = CGRectMake(MARGIN_HOR, MARGIN_VER, size.width, size.height);
	
	UIImage *img = [UIImage imageNamed:kBackgroundImage];
	UIImageView *imgView = [[[UIImageView alloc] initWithImage:
							 [img stretchableImageWithLeftCapWidth:img.size.width/2 topCapHeight:img.size.height/2]] autorelease];
    imgView.tag = IMAGE_TAG;
	imgView.frame = CGRectMake(0, 0, size.width+MARGIN_HOR*2, size.height+MARGIN_VER*2);
		
	//float oriX = pos.x -imgView.frame.size.width - MARGIN_HOR;
    float oriX = pos.x - 20;
	float oriY = pos.y + MARGIN_VER;
	badge.frame = CGRectMake(oriX, oriY, imgView.frame.size.width, imgView.frame.size.height);
	
	[badge addSubview:imgView];
	[badge addSubview:tip];
	[view addSubview:badge];
    
    return badge;
}

- (void)setBadgeNum:(NSInteger)num
{
    UIImageView *imgView = (UIImageView *)[self viewWithTag:IMAGE_TAG];
    UILabel *tip = (UILabel *)[self viewWithTag:TIP_TAG];
    tip.text = [NSString stringWithFormat:@"%d", num];
    
    //resize
    CGSize size =  [tip.text sizeWithFont:tip.font constrainedToSize:CGSizeMake(100, 20)];
    size.width = (size.width <16) ? 16 : size.width;
	tip.frame = CGRectMake(MARGIN_HOR, MARGIN_VER, size.width, size.height);
    imgView.frame = CGRectMake(0, 0, size.width+MARGIN_HOR*2, size.height+MARGIN_VER*2);
    CGRect rect = self.frame;
    rect.size = CGSizeMake(imgView.frame.size.width, imgView.frame.size.height);
    self.frame = rect;
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event
{
	return nil;
}

@end
