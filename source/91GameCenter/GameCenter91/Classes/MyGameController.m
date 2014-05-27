//
//  MyGameController.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-3.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "MyGameController.h"
#import "UIViewController+Extent.h"
#import "GameDownloadController.h"
#import "AboutViewController.h"
#import "UpdateSoftController.h"
#import "CommUtility.h"
#import "NSArray+Extent.h"
#import "Notifications.h"
#import "UIBarButtonItem+Extent.h"

@implementation MyGameController

+ (MyGameController *)gameController
{
    GameDownloadController *ctr0 = [[GameDownloadController new] autorelease];
    UpdateSoftController *ctr1 = [[UpdateSoftController new] autorelease];
    
    int height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:YES otherExcludeHeight:[self defaultSegmentHeight]];
    return (MyGameController *)[self controllerWithSubControllers:[NSArray arrayWithObjects:ctr0, ctr1, nil] subviewHight:height];
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView
{
}
*/


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
    self.customTitle = @"我的游戏";
    self.view.backgroundColor = [CommUtility defaultBgColor];
    
    self.navigationItem.rightBarButtonItem = [UIBarButtonItem rightItemWithCustomStyle:@"关于" target:self action:@selector(goAbout:)];
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //从openurl开始时，添加回游戏按钮(只在游戏和活动页面添加)
    [CommUtility showBarItemForCallBack:self];
    
    UpdateSoftController *ctr = [self.subControllers valueAtIndex:1];
    [ctr refreshBadgeNumber];
    
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)goAbout:(id)sender
{
    AboutViewController *ctrl = [[AboutViewController new] autorelease];
    ctrl.hidesBottomBarWhenPushed = YES;
    [self.navigationController pushViewController:ctrl animated:YES];
}

@end
