//
//  FlexibleTextView.m
//  GameCenter91
//
//  Created by hiyo on 13-2-1.
//  Copyright (c) 2013年 __MyCompanyName__. All rights reserved.
//

#import "FlexibleTextView.h"

#define TAG_TEXT            198273
#define TAG_LINE            198274                
#define TAG_JIANTOU         198275
#define TAG_SPLIT           198276
#define STR_ZHANGKAI        @"   展开"
#define STR_SHOUQI          @"   收起"
#define DESCRIPLABEL_WIDTH  295

#define RGB(r,g,b)          [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:1.0]

@interface FlexibleTextView()
@property (nonatomic, assign) int totalLines;
@end

@implementation FlexibleTextView
@synthesize delegate;
- (void)dealloc
{
    self.tapGest = nil;
    self.expendView = nil;
    [super dealloc];
}

+ (FlexibleTextView *)flexibleTextViewWithText:(NSString *)str originXY:(CGPoint)originXY delegate:(id)del
{
    FlexibleTextView *topV = [[FlexibleTextView alloc] init];
    topV.delegate = del;
    
    UILabel *desc = [[[UILabel alloc] init] autorelease];
    desc.tag = TAG_TEXT;
    desc.text = str;
    desc.font = [UIFont systemFontOfSize:14];
    desc.backgroundColor = [UIColor clearColor];
    desc.userInteractionEnabled = YES;
    
    
    //点击显示简介区域 展开
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer new] autorelease];
    [gesture addTarget:topV action:@selector(tapLabelArea:)];
    [desc addGestureRecognizer:gesture];
    
    
    CGSize totoalSize = [desc.text sizeWithFont:desc.font constrainedToSize:CGSizeMake(DESCRIPLABEL_WIDTH, 10000)];
    CGSize oneLineSize = [@" " sizeWithFont:desc.font];
    topV.totalLines = totoalSize.height/oneLineSize.height;
    float introHeight = 0.0;
    if (topV.totalLines <= 4) {
        desc.numberOfLines = topV.totalLines;
        desc.frame = CGRectMake(5, 5, DESCRIPLABEL_WIDTH, totoalSize.height);
        [topV addSubview:desc];
        
        introHeight = 5 + totoalSize.height + 8;
    }
    else {
        desc.numberOfLines = 4;
        desc.frame = CGRectMake(5, 5, DESCRIPLABEL_WIDTH, oneLineSize.height * 4);
        [topV addSubview:desc];
         topV.expendFlag = NO;
        topV.expendView = [[[UIView alloc] initWithFrame:CGRectMake(130,  5 + oneLineSize.height * 4 + 5 , 170, 40)] autorelease];
        topV.expendView.backgroundColor = [UIColor clearColor];
        UIImageView *imgView = [[[UIImageView alloc] initWithFrame:CGRectMake(140, 10, 15, 9)] autorelease];
        imgView.image = [UIImage imageNamed:@"dropDown_Arrow"];
        imgView.tag = TAG_JIANTOU;
        [topV.expendView addSubview:imgView];
        topV.tapGest = [[UITapGestureRecognizer new] autorelease];
        [topV.tapGest addTarget:topV action:@selector(btnPress:)];
        [topV.expendView addGestureRecognizer:topV.tapGest];
        [topV addSubview:topV.expendView];
        

        
        
        introHeight = 5 + oneLineSize.height * 4 + 5 + 25 + 5;
    }
    topV.frame = CGRectMake(originXY.x, originXY.y, DESCRIPLABEL_WIDTH, introHeight);
    return [topV autorelease];
}

- (void)tapLabelArea:(UITapGestureRecognizer *)gesture
{
    if (!self.expendFlag) {
        [self btnPress:gesture];
    }
}

- (void)btnPress:(UITapGestureRecognizer *)gesture
{
    UILabel *label = (UILabel *)[self viewWithTag:TAG_TEXT];
    FlexibleTextView *topV = (FlexibleTextView *)[label superview];
    float offset = 0.0;
    
    UIView *view = self.expendView;
    UIImageView *imgView = (UIImageView *)[view viewWithTag:TAG_JIANTOU];
    self.expendFlag = !self.expendFlag;
    if (self.expendFlag) {
        offset = (topV.totalLines-4.0)/4*CGRectGetHeight(label.bounds);
        label.numberOfLines = topV.totalLines;
        imgView.image = [UIImage imageNamed:@"upArrow"];
    }
    else {
        offset = (4.0-topV.totalLines)/topV.totalLines*CGRectGetHeight(label.bounds);
        label.numberOfLines = 4;
        imgView.image = [UIImage imageNamed:@"dropDown_Arrow"];
    }
    
    CGRect rect = topV.frame;
    rect.size.height += offset;
    topV.frame = rect;
    rect = label.frame;
    rect.size.height += offset;
    label.frame = rect;
    view.frame = CGRectOffset(view.frame, 0, offset);
    
    
    if ([self.delegate respondsToSelector:@selector(flexibleTextViewFrameChanged:)]) {
        [self.delegate performSelector:@selector(flexibleTextViewFrameChanged:) withObject:[NSNumber numberWithFloat:offset]];
    }
}

@end
