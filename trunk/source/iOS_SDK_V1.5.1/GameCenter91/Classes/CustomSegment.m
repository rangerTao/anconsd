//
//  CustomSegment.m
//  GameCenter91
//
//  Created by  hiyo on 12-8-22.
//  Copyright 2012 Nd. All rights reserved.
//

#import "CustomSegment.h"
#import "BadgeView.h"


@interface CustomSegment()

@property(nonatomic, assign)float cell_Width;
@property(nonatomic, assign)float cell_Offset;

@end


@implementation CustomSegment
@synthesize items;
@synthesize selectedSegmentIndex;
@synthesize customDelegate;
@synthesize cell_Width, cell_Offset;

#define TAG_BASE		100

#define TAG_ITEM		100
#define TAG_SELECTED	200
#define TAG_BACKGROUND	300
#define TAG_BADGE       400

#define RATIO_SELECTED_HEIGHT	0.9
#define MAX_CELL_WIDTH			150.0f

#define RGB(r,g,b)          [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:1.0]
#define RGBA(r, g, b, a)       [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:a]

#define CHECK_INDEX(index) do { \
		if (index < 0 || index >= [items count]) { \
			NSLog(@"index out of range (0 - %d):index = %d", [items count], index); \
			return;	\
		} \
		}while(0)


- (id)initWithItems:(NSArray *)arr delegate:(id)del 
{
	if ((self = [super init])) {
		self.items = [NSArray arrayWithArray:arr];
		self.customDelegate = del;
		
		UIImage *img = [UIImage imageNamed:@"bg_nav.png"];
		UIImageView *bgView = [[[UIImageView alloc] initWithImage:
							   [img stretchableImageWithLeftCapWidth:img.size.width/2 topCapHeight:img.size.height/2]] autorelease];
		bgView.tag = TAG_BACKGROUND;
		[self addSubview:bgView];
		UIImage *selectedImg = [UIImage imageNamed:@"bg_nav_on.png"];
		UIImageView *selectedImgView = [[[UIImageView alloc] initWithImage:
					[selectedImg stretchableImageWithLeftCapWidth:selectedImg.size.width/2 topCapHeight:selectedImg.size.height/2]] autorelease];
		selectedImgView.tag = TAG_SELECTED;
		selectedImgView.hidden = YES;
		[self addSubview:selectedImgView];
	
		for (int i = 0; i < [items count]; i++) {
			id obj = [arr objectAtIndex:i];
			UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
			btn.tag = TAG_ITEM + i;
			[btn addTarget:self action:@selector(btnTouched:) forControlEvents:UIControlEventTouchDown];
			[self addSubview:btn];
			if ([obj isKindOfClass:[NSString class]]) {		
				[btn setTitle:obj forState:UIControlStateNormal];
				
				btn.titleLabel.font = [UIFont boldSystemFontOfSize:16];
			}
			else if ([obj isKindOfClass:[UIImage class]]) {
				[btn setImage:obj forState:UIControlStateNormal];
				[btn setImage:obj forState:UIControlStateSelected];
			}
		}
		
		selectedSegmentIndex = -1;
        cell_Width = 0.0;
        cell_Offset = 0.0;
	}
	return self;
}

- (void)removeBadgeAtIndex:(int)index
{
	CHECK_INDEX(index);
	
	UIView *badge = [self viewWithTag:TAG_BADGE + index];
	[badge removeFromSuperview];
}

- (void)setBadgeNum:(int)num atIndex:(int)index bAutoHideWhenZero:(BOOL)bHide
{
	CHECK_INDEX(index);
    if (cell_Width == 0) {
        return;
    }
	
//    [self removeBadgeAtIndex:index];
    
	BadgeView *badge = (BadgeView*)[self viewWithTag:TAG_BADGE + index];
    if (badge == nil) {
        CGPoint pt = CGPointMake(cell_Width*(index+1)+cell_Offset, 0);
        badge = [BadgeView addToView:self Tag:TAG_BADGE + index position:pt num:num];
    }
    else {
        [badge setBadgeNum:num];
    }
    badge.hidden = (num <= 0 && bHide);
}

- (void)btnTouched:(id)sender
{
	UIButton *btn = (UIButton *)sender;
	int index = btn.tag % TAG_BASE;
	self.selectedSegmentIndex = index;	
}

- (void)updateSegmentBase
{
	CGSize segSize = self.frame.size;
	BOOL bMax = segSize.width / [items count] > MAX_CELL_WIDTH;
	self.cell_Width = (int)(bMax ? MAX_CELL_WIDTH : segSize.width / [items count]);
	self.cell_Offset = bMax ? (segSize.width-[items count]*cell_Width)/2.0 : 0.0f;
}

- (void)updateSegment
{
	[self updateSegmentBase];
	//调整选中图标的位置
	UIImageView *selectedImgView = (UIImageView *)[self viewWithTag:TAG_SELECTED];
	selectedImgView.hidden = NO;
	CGSize segSize = self.frame.size;
	float centerX = (selectedSegmentIndex+0.5) * cell_Width + cell_Offset;
	float centerY = segSize.height - RATIO_SELECTED_HEIGHT * segSize.height/2;
	selectedImgView.center = CGPointMake(centerX, centerY);	
	//更新标签颜色
	for (int i = 0; i < [items count]; i++) {
		UIButton *btn = (UIButton *)[self viewWithTag:TAG_ITEM + i];
		if (btn.tag - TAG_ITEM == selectedSegmentIndex) {
			UIColor *c = RGB(0x14, 0x7f, 0xb5);
			[btn setTitleColor:c forState:UIControlStateNormal];
            [btn setTitleShadowColor:RGBA(255, 255, 255, 0.75) forState:UIControlStateNormal];
            [btn.titleLabel setShadowOffset:CGSizeMake(cos(M_PI*60/180), sin(M_PI*60/180))];
		}
		else {
			UIColor *c = RGB(0xC4, 0xdd, 0xec);
			[btn setTitleColor:c forState:UIControlStateNormal];
            [btn setTitleShadowColor:RGBA(0, 0, 0, 0.4) forState:UIControlStateNormal];
            [btn.titleLabel setShadowOffset:CGSizeMake(0, -1)];            
		}
	}
	//去除选中项的badge
	//[self removeBadgeAtIndex:selectedSegmentIndex];
}

- (void)setSelectedSegmentIndex:(int)index
{
	CHECK_INDEX(index);
	
	int temp = selectedSegmentIndex;
	selectedSegmentIndex = index;
	if (temp != index) {
		[self updateSegment];
		id del = self.customDelegate;
		SEL sel = @selector(segmentIndexChangedFromOld:ToNew:);
		if ([del respondsToSelector:sel]) {
			//此处使用NSInvocation
			NSMethodSignature* sign = nil;
			if ([del respondsToSelector:@selector(methodSignatureForSelector:)]) {
				sign = [del methodSignatureForSelector:sel];
			}
			else {
				sign = [del instanceMethodSignatureForSelector:sel];
			}
			NSInvocation* invoc = [NSInvocation invocationWithMethodSignature:sign];
			[invoc setTarget:del];
			[invoc setSelector:sel];
			[invoc setArgument:&temp atIndex:2];
			[invoc setArgument:&selectedSegmentIndex atIndex:3];
			[invoc invoke];
		}
	}
}

- (void)layoutSubviews
{
	[super layoutSubviews];
	[self updateSegmentBase];
	CGSize segSize = self.frame.size;
	//背景图片位置和大小
	UIImageView *bgView = (UIImageView *)[self viewWithTag:TAG_BACKGROUND];
	bgView.bounds = self.bounds;
	bgView.frame = CGRectMake(0, 0, segSize.width, segSize.height);
	//调整选中图标的大小
	UIImageView *selectedImgView = (UIImageView *)[self viewWithTag:TAG_SELECTED];
	float imgViewWidth = cell_Width;
	float imgViewHeight = RATIO_SELECTED_HEIGHT * segSize.height;
	selectedImgView.frame = CGRectMake(selectedSegmentIndex * cell_Width + cell_Offset,
									   (1-RATIO_SELECTED_HEIGHT) * segSize.height, imgViewWidth, imgViewHeight);
	
	//调整按钮位置和大小
	for (int i = 0; i < [items count]; i++) {
		UIButton *btn = (UIButton *)[self viewWithTag:TAG_ITEM + i];
		btn.frame = CGRectMake(cell_Width * i + cell_Offset, 0, cell_Width, segSize.height);
	}
}

- (void)dealloc {
	self.items = nil;
	self.customDelegate = nil;
    [super dealloc];
}


@end
