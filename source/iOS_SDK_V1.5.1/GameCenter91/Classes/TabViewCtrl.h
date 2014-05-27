//
//  TabViewCtrl.h
//  GameCenter91
//
//  Created by  hiyo on 12-8-21.
//  Copyright 2012 Nd. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CyclePageScrollView.h"
#import "CustomSegment.h"


@class CustomSegment;
@interface TabViewCtrl : UIViewController <PageControlProtocol, CustomSegmentProtocol> {
	CustomSegment *seg;
	CyclePageScrollView *cpsView;
	
	NSInteger numberOfPages;
	NSInteger currentPage; 
	
	float	height_tab;
	float	height_ctrl;
}
@property (nonatomic, retain) CustomSegment *seg;
@property (nonatomic, retain) CyclePageScrollView *cpsView;

@property(nonatomic) NSInteger numberOfPages;
@property(nonatomic) NSInteger currentPage; 

@property(nonatomic, assign) float height_tab;
@property(nonatomic, assign) float height_ctrl;

+ (id)tabViewCtrlWithTabStrings:(NSArray *)strArr cpsView:(CyclePageScrollView *)scrollView;
+ (id)tabViewCtrlWithTabStrings:(NSArray *)strArr viewArr:(NSArray *)viewArr;

+ (CGFloat)defaultSegmentHeight;

- (void)indexDidChangeFrom:(int)previousIndex to:(int)currentIndex;

- (void)setBadgeNumber:(int)num forIndex:(int)index bAutoHideWhenZero:(BOOL)bHide;
@end
