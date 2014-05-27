    //
//  MyTaskController.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-11.
//  Copyright 2012 net dragon. All rights reserved.
//

#import "MyTaskController.h"
#import "UIViewController+Extent.h"
#import "CurrentTaskController.h"
#import "GetableTaskController.h"
#import "CompletedTaskController.h"
#import "CommUtility.h"
#import "GuideView.h"
#import "GameCenterAnalytics.h"

@implementation MyTaskController

+ (MyTaskController *)taskController
{
    CurrentTaskController   *ctr0 = [[CurrentTaskController new] autorelease];
    GetableTaskController   *ctr1 = [[GetableTaskController new] autorelease];
    CompletedTaskController *ctr2 = [[CompletedTaskController new] autorelease];

    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:[CommUtility isTabbarHide] otherExcludeHeight:[self defaultSegmentHeight]];
    return (MyTaskController *)[self controllerWithSubControllers:[NSArray arrayWithObjects:ctr0, ctr1, ctr2, nil] subviewHight:height];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.customTitle = @"我的任务";
    self.view.backgroundColor = [CommUtility defaultBgColor];
}

- (void)viewWillAppear:(BOOL)animated
{
    //显示引导视图
    if (![[NSUserDefaults standardUserDefaults] boolForKey:@"firstShowMyTask"]) { 
        NSLog(@"first show MyTask");
        
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"firstShowMyTask"];
        [[NSUserDefaults standardUserDefaults] synchronize];
        [GuideView show:GUIDE_VIEW_MYTASK animated:NO];
    }

    [NdAnalytics event:ANALYTICS_EVENT_0005];
}

// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
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
    [super dealloc];
}


@end
