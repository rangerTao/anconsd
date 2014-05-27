    //
//  GameHallController.m
//  GameCenter91
//
//  Created by  hiyo on 12-9-6.
//  Copyright 2012 Nd. All rights reserved.
//

#import "GameHallController.h"
#import "GameVogueRecController.h"
#import "GameSearchController.h"
#import "UIViewController+Extent.h"
#import "CommUtility.h"
#import "CatagoryViewController.h"
#import "GameRankController.h"
#import "ReportCenter.h"
#define INDEX_SEARCH_CONTROLLER 3

@implementation GameHallController

+ (GameHallController *)gameHall
{
    CatagoryViewController *ctr0 = [[[CatagoryViewController alloc] init] autorelease];
    GameVogueRecController *ctr1 = [[[GameVogueRecController alloc] init] autorelease];
    GameRankController *ctr2 = [GameRankController gameRankController];
    
    CGFloat height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:![CommUtility isTabbarHide] otherExcludeHeight:[self defaultSegmentHeight]];
    return (GameHallController *)[self controllerWithSubControllers:[NSArray arrayWithObjects:ctr0, ctr1, ctr2, nil] subviewHight:height];
}

- (void)viewDidLoad {
    [super viewDidLoad];
	
	self.customTitle = @"游戏大厅";
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    UIImage *img = [UIImage imageNamed:@"search.png"];
    UIImage *imgSel = [UIImage imageNamed:@"search_hover.png"];
    btn.bounds = CGRectMake(0, 0, img.size.width, img.size.height);
    [btn setImage:img forState:UIControlStateNormal];
    [btn setImage:imgSel forState:UIControlStateSelected];
    [btn addTarget:self action:@selector(doSearch:) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] initWithCustomView:btn] autorelease];
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //从openurl开始时，添加回游戏按钮
    [CommUtility showBarItemForCallBack:self];
}


// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

//#pragma mark delegate
//- (void)segmentIndexChangedFromOld:(int)oldIndex ToNew:(int)newIndex
//{
//	[super segmentIndexChangedFromOld:oldIndex ToNew:newIndex];
//	if (oldIndex == INDEX_SEARCH_CONTROLLER) {
//		GameSearchController *ctrl = (GameSearchController *)[self.subControllers objectAtIndex:INDEX_SEARCH_CONTROLLER];
//		[ctrl hideKeyboard];
//	}
//}

- (void)doSearch:(id)sender
{
    GameSearchController *ctrl = [[[GameSearchController alloc] init] autorelease];
    ctrl.hidesBottomBarWhenPushed = YES;
    [ReportCenter report:ANALYTICS_EVENT_15053];
    [self.navigationController pushViewController:ctrl animated:YES];
}

@end
