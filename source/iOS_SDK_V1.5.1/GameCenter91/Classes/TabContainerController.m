//
//  TabContainerController.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-3.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "TabContainerController.h"
#import "CyclePageScrollView.h"
#import "UIViewController+Extent.h"

@implementation TabContainerController
@synthesize subControllers;

+ (TabContainerController *)controllerWithSubControllers:(NSArray *)subControllers subviewHight:(CGFloat)subviewHeight
{
    NSMutableArray *titles = [NSMutableArray arrayWithCapacity:[subControllers count]];
    for (UIViewController *ctr in subControllers)
    {
        NSString *title = ctr.title;
        if (title == nil)
            title = @"";
        [titles addObject:title];
    }
    
    NSMutableArray *pageViews = [NSMutableArray arrayWithCapacity:[subControllers count]];
    CyclePageScrollView *scrollView = [[[CyclePageScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, subviewHeight)] autorelease];
    scrollView.clipsToBounds = YES;
    for (UIViewController *ctr in subControllers)
    {
        ctr.view.frame = scrollView.bounds;
        [pageViews addObject:ctr.view];
    }
    [scrollView setPageViews:pageViews];
    
    TabContainerController *containerCtr = [self tabViewCtrlWithTabStrings:titles cpsView:scrollView];    
    containerCtr.subControllers = subControllers;
    for (UIViewController *ctr in containerCtr.subControllers) {
        ctr.parentContainerController = containerCtr;
    }
    return  containerCtr;
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)dealloc {
    for (UIViewController *ctr in self.subControllers) {
        ctr.parentContainerController = nil;
    }
    self.subControllers = nil;
    [super dealloc];
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

- (void)indexDidChangeFrom:(int)previousIndex to:(int)currentIndex
{
    UIViewController *previous = [self.subControllers objectAtIndex:previousIndex];
    UIViewController *current = [self.subControllers objectAtIndex:currentIndex];
    [current viewWillAppear:NO];
    [previous viewDidDisappear:NO];
}

- (void)setBadgeNumber:(int)num forSubController:(UIViewController *)controller bAutoHideWhenZero:(BOOL)bHide
{
    int index = [self.subControllers indexOfObject:controller];
    [self setBadgeNumber:num forIndex:index bAutoHideWhenZero:bHide];
}
#pragma mark - View lifecycle

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView
{
}
*/

/*
// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
}
*/

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
//    for (UIViewController *ctr in self.subControllers) {
//        [ctr viewWillAppear:animated];
//    }
    UIViewController *curController = [self.subControllers objectAtIndex:self.currentPage];
    [curController viewWillAppear:animated];
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

@end
