    //
//  TabViewCtrl.m
//  GameCenter91
//
//  Created by  hiyo on 12-8-21.
//  Copyright 2012 Nd. All rights reserved.
//

#import "TabViewCtrl.h"
#import "CommUtility.h"

@implementation TabViewCtrl
@synthesize seg;
@synthesize cpsView;
@synthesize numberOfPages;
@synthesize currentPage;
@synthesize height_tab;
@synthesize height_ctrl;

#define HEIGHT_SEGMENT	35.0f
#define HEIGHT_CPS_VIEW	NSIntegerMax//480.0f-20.0-44.0-HEIGHT_SEGMENT-49.0


- (id)init
{
	self = [super init];
	if (self != nil) {
		self.seg = nil;
		self.cpsView = nil;
		self.numberOfPages = 0;
		self.currentPage = 0;
		self.height_tab = HEIGHT_SEGMENT;
		self.height_ctrl = HEIGHT_CPS_VIEW;
        
        [self addObserver:self forKeyPath:@"currentPage" options:NSKeyValueObservingOptionNew|NSKeyValueObservingOptionOld context:nil];
	}
	return self;
}

+ (id)tabViewCtrlWithTabStrings:(NSArray *)strArr cpsView:(CyclePageScrollView *)scrollView
{
	NSAssert([strArr count] == [scrollView.contentPageViews count], @"tabViewCtrl : param error!");
	TabViewCtrl *ctrl = [[self alloc] init];
	if (ctrl != nil) {
		ctrl.seg = [[[CustomSegment alloc] initWithItems:strArr delegate:ctrl] autorelease];
		ctrl.cpsView = scrollView;
	}
	return [ctrl autorelease];
}

+ (id)tabViewCtrlWithTabStrings:(NSArray *)strArr viewArr:(NSArray *)viewArr
{
	NSAssert([strArr count] == [viewArr count], @"tabViewCtrl : param error!");
	TabViewCtrl *ctrl = [[self alloc] init];
	if (ctrl != nil) {
		ctrl.seg = [[[CustomSegment alloc] initWithItems:strArr delegate:ctrl] autorelease];
		
		CyclePageScrollView *sv = [[[CyclePageScrollView alloc] init] autorelease];
		[sv setPageViews:viewArr];
		
		ctrl.cpsView = sv;
	}
	return [ctrl autorelease];
}

+ (CGFloat)defaultSegmentHeight
{
    return HEIGHT_SEGMENT;
}

- (void)viewDidLoad {
    [super viewDidLoad];

	self.seg.frame = CGRectMake(0, 0, self.view.frame.size.width, height_tab);
	self.seg.selectedSegmentIndex = self.currentPage;
	[self.view addSubview:self.seg];

	float cpsViewHeight = self.cpsView.frame.size.height;
	cpsViewHeight = cpsViewHeight > height_ctrl ? height_ctrl : cpsViewHeight;
	self.cpsView.frame = CGRectMake(0, height_tab, self.view.frame.size.width, cpsViewHeight);
	[self.view addSubview:self.cpsView];
	
	self.cpsView.customPageControl = self;
    self.cpsView.currentPage = self.currentPage;
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}


- (void)dealloc {
    [self removeObserver:self forKeyPath:@"currentPage"];
    
	self.seg = nil;
	self.cpsView = nil;
    
    [super dealloc];
}

#pragma mark -

- (void)segmentIndexChangedFromOld:(int)oldIndex ToNew:(int)newIndex
{
//	NSLog(@"old = %d, new = %d", oldIndex, newIndex);
	if (self.currentPage != newIndex) {
		self.cpsView.currentPage = newIndex;
		self.currentPage = newIndex;
//        [self indexDidChangeFrom:oldIndex to:newIndex];
	}
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
	if ([keyPath isEqual:@"currentPage"]) {
		int oldIndex = [[change objectForKey:NSKeyValueChangeOldKey] intValue];
		int newIndex = [[change objectForKey:NSKeyValueChangeNewKey] intValue];
//		NSLog(@"old index = %d, new index = %d", oldIndex, newIndex);
		if (oldIndex != newIndex) {
            self.cpsView.currentPage = newIndex;
			self.seg.selectedSegmentIndex = newIndex;
            [self indexDidChangeFrom:oldIndex to:newIndex];
		}
	}
}

- (void)indexDidChangeFrom:(int)previousIndex to:(int)currentIndex
{

}

- (void)setBadgeNumber:(int)num forIndex:(int)index bAutoHideWhenZero:(BOOL)bHide
{
    [self.seg setBadgeNum:num atIndex:index bAutoHideWhenZero:bHide];
}

//#pragma mark -
//#pragma mark autorotate
//- (void)resetSubViewFrame
//{
//	self.seg.frame = CGRectMake(0, 0, self.view.frame.size.width, HEIGHT_SEGMENT);
//	
//	float cpsViewHeight = self.cpsView.frame.size.height;
//	cpsViewHeight = cpsViewHeight > HEIGHT_CPS_VIEW ? HEIGHT_CPS_VIEW : cpsViewHeight;
//	self.cpsView.frame = CGRectMake(0, 40, self.view.frame.size.width, cpsViewHeight);
//}

//- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
//	return YES;
//}
//
//- (void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
//{	
//	BOOL bHiddenTabbar = NO;
//	BOOL bIsLandscape = UIInterfaceOrientationIsLandscape(toInterfaceOrientation);
//	CGFloat fTabbarHeight = (bHiddenTabbar) ? 0.0f : 50.0;
//	CGRect rc = [[UIScreen mainScreen] bounds];
//	if (bIsLandscape && rc.size.width < rc.size.height) {
//		CGFloat width = rc.size.width;
//		rc.size.width = rc.size.height;
//		rc.size.height = width - (33.0 + fTabbarHeight + 20.0);
//		rc.origin.y = 33.0;
//	}
//	else {
//		rc.size.height -= (44.0 + fTabbarHeight + 20.0);
//		rc.origin.y = 44.0;
//	}
//	self.view.frame = rc;
//	
//	[self resetSubViewFrame];
//}

@end
