//
//  DashboardController.m
//  testCell
//
//  Created by Sun pinqun on 12-8-27.
//  Copyright 2012 net dragon. All rights reserved.
//

#import "DashboardController.h"
#import "BadgeView.h"
#import "UINavigationController+Extent.h"
#import "UIViewController+Extent.h"
#import "UIBarButtonItem+Extent.h"
#import "GameCenter91AppDelegate.h"

#import "ActivityMainController.h"
#import "GameHallController.h"
#import "MyGameController.h"
#import "CustomNavController.h"
#import "HomePage.h"
#import "ReportCenter.h"

#define TAG_CUSTOMBAR	300
#define TAG_BADGE		400
#define CHECK_INDEX(index) do { \
	if (index < 0 || index >= [buttons count]) { \
		NSLog(@"index out of range (0 - %d):index = %d", [buttons count], index); \
		return;	\
	} \
	}while(0)

@interface DashboardController()

- (void)hideRealTabBar;
- (void)createCustomTabBar;
- (void)selectedTab:(UIButton *)button;
- (void)slideTabBg:(UIButton *)btn animated:(BOOL)animated;

@end

@implementation DashboardController

@synthesize currentSelectedIndex;
@synthesize buttons;
@synthesize customBar;


- (id)initWithViewControllers:(NSArray *)arrController {
    self = [super init];
    if (self) {
        self.viewControllers = arrController;
        self.selectedIndex = 0;
//        [self hideRealTabBar];
        [self createCustomTabBar];
    }
    return self;
}

+ (DashboardController *)dashBoardController
{
    HomePage *ctr0 = [[[HomePage alloc] initWithStyle:UITableViewStyleGrouped] autorelease];
    UINavigationController *navCtrl0 = [[[CustomNavController alloc] initWithRootViewController:ctr0] autorelease];
    [navCtrl0 customizeNavigationBar];
    
    GameHallController *ctr1 = [GameHallController gameHall];
    UINavigationController *navCtrl1 = [[[CustomNavController alloc] initWithRootViewController:ctr1] autorelease];
    [navCtrl1 customizeNavigationBar];
    ctr1.currentPage = 1;
    
    ActivityMainController *ctr2 = [ActivityMainController activityMainController];
    UINavigationController *navCtrl2 = [[[CustomNavController alloc] initWithRootViewController:ctr2] autorelease];
    [navCtrl2 customizeNavigationBar];
    ctr2.currentPage = 1;
    
    MyGameController *myGameController = [MyGameController gameController];
    UINavigationController *navCtrl3 = [[[CustomNavController alloc] initWithRootViewController:myGameController] autorelease];
    [navCtrl3 customizeNavigationBar];
    
    return (DashboardController *)[[[DashboardController alloc] initWithViewControllers:[NSArray arrayWithObjects:navCtrl0, navCtrl1, navCtrl2, navCtrl3, nil]] autorelease];
}


- (void) dealloc{
    [slideBg release];
    [buttons release];
    [customBar release];
    [super dealloc];
}

- (void)setCurrentSelectedIndex:(int)index;
{
    [self jumpToIndex:index];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
	return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
    if (self.tabBar.hidden == YES)
    {
        self.customBar.hidden = YES;
    }
    else
    {
        self.customBar.hidden = NO;
        [self.view bringSubviewToFront:self.customBar];
    }
}

- (void)removeBadgeAtIndex:(int)index
{
	CHECK_INDEX(index);
	
	UIView *badge = [customBar viewWithTag:TAG_BADGE + index];
	[badge removeFromSuperview];
}

- (void)setBadgeNum:(int)num atIndex:(int)index
{
	CHECK_INDEX(index);
	
	[self removeBadgeAtIndex:index];
	
	CGSize segSize = customBar.frame.size;
	CGPoint pt = CGPointMake(segSize.width/[buttons count]*(index+1), 0);
	[BadgeView addToView:customBar Tag:TAG_BADGE + index position:pt num:num];
}

#pragma mark -
- (void)hideRealTabBar{
    self.tabBar.hidden = YES;
    [self.tabBar addObserver:self forKeyPath:@"hidden" options:NSKeyValueObservingOptionNew | NSKeyValueObservingOptionOld context:NULL];
}

- (void)createCustomTabBar{
    customBar = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.tabBar.frame.size.width, self.tabBar.frame.size.height)];
    customBar.tag = TAG_CUSTOMBAR;
	customBar.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin;
    [self.tabBar addSubview:customBar];
    
	
    UIImageView *imgBackground = [[[UIImageView alloc] initWithImage: [UIImage imageNamed:@"bg_main"]] autorelease];
    imgBackground.frame = CGRectMake(0, 0, self.tabBar.frame.size.width, self.tabBar.frame.size.height);
	imgBackground.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin;
    [customBar addSubview:imgBackground];
    
    UIImageView *imgLineTop = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"line_top"]] autorelease];
    CGFloat imgLineHeight = imgLineTop.image.size.height;
    imgLineTop.frame = CGRectMake(0, 0, self.tabBar.frame.size.width, imgLineTop.image.size.height);
	imgLineTop.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin;
    [customBar addSubview:imgLineTop];
    
    int viewCount = self.viewControllers.count > 5 ? 5 : self.viewControllers.count;
    double btnWidth = self.tabBar.frame.size.width / viewCount;
    double btnHeight = self.tabBar.frame.size.height;
    
    //添加蒙板
    slideBg = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"menu_mask"]];
    slideBg.frame = CGRectMake(0, 0+imgLineHeight, btnWidth, btnHeight);
    slideBg.alpha = 0.8;
	slideBg.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin;
	[customBar addSubview:slideBg];
    
    //创建按钮
    self.buttons = [NSMutableArray arrayWithCapacity:viewCount];
    for (int i = 0; i < viewCount; i++) {
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
		btn.frame = CGRectMake(i*btnWidth, 0+imgLineHeight, btnWidth, btnHeight);
		btn.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin;
        [btn setImage:[UIImage imageNamed:[NSString stringWithFormat:@"menu_%d", i]] forState:UIControlStateNormal];
        [btn setImage:[UIImage imageNamed:[NSString stringWithFormat:@"menu_%d", i]] forState:UIControlStateHighlighted];
        [btn setImage:[UIImage imageNamed:[NSString stringWithFormat:@"menu_on_%d", i]] forState:UIControlStateSelected];
        [btn addTarget:self action:@selector(selectedTab:) forControlEvents:UIControlEventTouchUpInside];
        btn.tag = i;
        [self.buttons addObject:btn];
		[customBar addSubview:btn];
    }
    
    UIButton *firstButton = [self.buttons objectAtIndex:self.selectedIndex];
    [self selectedTab:firstButton];
    firstButton.selected = YES;
}

- (void)selectedTab:(UIButton *)button animated:(BOOL)animated
{
    UINavigationController *selectedCtr = [self.viewControllers objectAtIndex:self.currentSelectedIndex];
    if (self.currentSelectedIndex == button.tag) {
        [selectedCtr popToRootViewControllerAnimated:YES];
        return;
    }
    
    BOOL should = YES;
    if (self.delegate && [self.delegate respondsToSelector:@selector(tabBarController:shouldSelectViewController:)])
    {
        UIViewController *toBeSelected = [self.viewControllers objectAtIndex:button.tag];
        should = [self.delegate tabBarController:self shouldSelectViewController:toBeSelected];
        if (should == NO)
            return;
    }
    
    UIButton *lastButton = [self.buttons objectAtIndex:self.currentSelectedIndex];
    lastButton.selected = NO;
    
    currentSelectedIndex = button.tag;
    self.selectedIndex = self.currentSelectedIndex;
    [self slideTabBg:button animated:animated];
}

- (void)selectedTab:(UIButton *)button
{
    switch (button.tag) {
        case HOME_PAGE:
            [ReportCenter report:ANALYTICS_EVENT_15001];
            break;
        case GAME_PAGE:
            [ReportCenter report:ANALYTICS_EVENT_15002];
            break;
        case ACTIVITIES_PAGE:
            [ReportCenter report:ANALYTICS_EVENT_15003];
            break;
        case MANAGEMENT:
            [ReportCenter report:ANALYTICS_EVENT_15004];
            break;
            
        default:
            break;
    }
    [self selectedTab:button animated:YES];
}

- (void)slideTabBg:(UIButton *)btn animated:(BOOL)animated
{
    if (animated)
    {
        [UIView beginAnimations:nil context:nil];  
        [UIView setAnimationDuration:0.20];  
        [UIView setAnimationDelegate:self];
    }
    slideBg.frame = CGRectMake(btn.frame.origin.x, btn.frame.origin.y, btn.frame.size.width, btn.frame.size.height);
    if (animated)
    {
        [UIView commitAnimations];
    }
    btn.selected = YES;
}

- (void)slideTabBg:(UIButton *)btn{
    [self slideTabBg:btn animated:YES];
}

- (void)jumpToIndex:(NSInteger)index {
    index = (index < 0) ? 0 : ((index > self.viewControllers.count) ? self.viewControllers.count : index);
    UIButton *button = [self.buttons objectAtIndex:index];
    [self selectedTab:button animated:NO];
}

@end
