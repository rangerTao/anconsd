//
//  CustomNavController.m
//  GameCenter91
//
//  Created by hiyo on 13-2-25.
//  Copyright (c) 2013年 __MyCompanyName__. All rights reserved.
//

#import "CustomNavController.h"

@implementation CustomNavController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (id)init
{
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(becomeActive) name:UIApplicationWillEnterForegroundNotification object:nil];
    }
    return self;
}

- (id)initWithRootViewController:(UIViewController *)rootViewController
{
    self = [super initWithRootViewController:rootViewController];
    if (self) {
#ifdef	__IPHONE_6_0
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(becomeActive) name:UIApplicationWillEnterForegroundNotification object:nil];
#endif
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationWillEnterForegroundNotification object:nil];
    
    [super dealloc];
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

- (UIBarButtonItem *)customLeftItem
{
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    UIImage *img = [UIImage imageNamed:@"return.png"];
    UIImage *imgSel = [UIImage imageNamed:@"return_hover.png"];
    btn.bounds = CGRectMake(0, 0, img.size.width, img.size.height);
    [btn setImage:img forState:UIControlStateNormal];
    [btn setImage:imgSel forState:UIControlStateSelected];
    [btn addTarget:self action:@selector(doReturn:) forControlEvents:UIControlEventTouchUpInside];
    
    return [[[UIBarButtonItem alloc] initWithCustomView:btn] autorelease];
}

- (void)doReturn:(id)sender
{
    [self popViewControllerAnimated:YES];
}

- (void)pushViewController:(UIViewController *)viewController animated:(BOOL)animated
{
    [super pushViewController:viewController animated:animated];
    
    if ([self.viewControllers count] == 1) {
        return;
    }
    
    viewController.navigationItem.leftBarButtonItem = [self customLeftItem];
}

- (void)becomeActive
{
    if ([self.viewControllers count] == 1) {
        return;
    }
    self.topViewController.navigationItem.leftBarButtonItem = [self customLeftItem];
}

@end
