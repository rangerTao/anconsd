    //
//  ActivityMainController.m
//  GameCenter91
//
//  Created by  hiyo on 12-8-29.
//  Copyright 2012 Nd. All rights reserved.
//

#import "ActivityMainController.h"
#import "ActivityCommonCtrl.h"
#import "ActivitySearchCtrl.h"
#import "ActivityTableViewCell.h"
#import "UIViewController+Extent.h"
#import "ActivityInfo.h"
#import "CommUtility.h"

@interface ActivityMainController()
@property (nonatomic, retain) NSArray *ctrlsArr;
@end


@implementation ActivityMainController
@synthesize ctrlsArr;

+ (ActivityMainController *)activityMainController
{
    ActivityCommonCtrl *ctrl0 = [[[ActivityCommonCtrl alloc] init] autorelease];
    ActivityCommonCtrl *ctrl1 = [[[ActivityCommonCtrl alloc] init] autorelease];
    ActivityCommonCtrl *ctrl2 = [[[ActivityCommonCtrl alloc] init] autorelease];
    ActivityCommonCtrl *ctrl3 = [[[ActivityCommonCtrl alloc] init] autorelease];
    ctrl0.act_type = ACT_MY_GIFTS;
    ctrl1.act_type = ACT_GAME_GIFTS;
    ctrl2.act_type = ACT_ACTIVITY_NOTICE;
    ctrl3.act_type = ACT_NEW_SERVERS_NOTICE;
    
    NSArray *ctrls = [NSArray arrayWithObjects:ctrl0, ctrl1, ctrl2, ctrl3, nil];
    
    CGFloat height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:YES otherExcludeHeight:[self defaultSegmentHeight]];
    return (ActivityMainController *)[self controllerWithSubControllers:ctrls subviewHight:height];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.customTitle = @"活动";
    
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


/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)dealloc {
	self.ctrlsArr = nil;
    [super dealloc];
}

- (void)doSearch:(id)sender
{
    int type = -1;
    switch (currentPage) {
        case 0:
            type = -1;
            break;
        case 1:
            type = ACT_GAME_GIFTS;
            break;
        case 2:
            type = ACT_ACTIVITY_NOTICE;
            break;
        case 3:
            type = ACT_NEW_SERVERS_NOTICE;
            break;
        default:
            break;
    }
    ActivitySearchCtrl *ctrl = [[[ActivitySearchCtrl alloc] init] autorelease];
    ctrl.act_type = type;
    [self.navigationController pushViewController:ctrl animated:YES];
}

@end
