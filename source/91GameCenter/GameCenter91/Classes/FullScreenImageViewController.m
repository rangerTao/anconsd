//
//  FullScreenImageViewController.m
//  GameCenter91
//
//  Created by kensou on 12-12-5.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "FullScreenImageViewController.h"
#import "UIImageView+WebCache.h"
#define PAGECONTROL_HEIGHT 44
@interface FullScreenImageViewController()<UIScrollViewDelegate>
@property (nonatomic, retain) UIPageControl *pageControl;
@end

@implementation FullScreenImageViewController
- (void)dealloc
{
    self.pageControl = nil;
    [super dealloc];
}
+ (void)show:(int)index ofImages:(NSArray *)images inController:(UIViewController *)parentCtr
{
    [[UIApplication sharedApplication] setStatusBarHidden:YES withAnimation:UIStatusBarAnimationNone];
    FullScreenImageViewController *ctr = [[FullScreenImageViewController new] autorelease];
    [parentCtr.navigationController presentModalViewController:ctr animated:YES];
    [ctr setupImageUrls:images];
    [ctr showIndex:index];    
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


// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView
{
    [super loadView];
    scroll = [[[UIScrollView alloc] initWithFrame:self.view.bounds] autorelease];
    scroll.pagingEnabled = YES;
    scroll.backgroundColor = [UIColor blackColor];
    scroll.showsHorizontalScrollIndicator = NO;
    [self.view addSubview:scroll]; 
    scroll.delegate = self;
    
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer new] autorelease];
    [gesture addTarget:self action:@selector(scrollViewTapped)];
    [self.view addGestureRecognizer:gesture];
    
    self.pageControl = [[[UIPageControl alloc] initWithFrame:CGRectMake(0, CGRectGetHeight(self.view.frame) - 44, 320, 44)] autorelease];
    [self.view addSubview:self.pageControl];
}



// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];    
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];

}

- (void)scrollViewTapped
{
    [[UIApplication sharedApplication] setStatusBarHidden:NO];
    [self dismissModalViewControllerAnimated:YES];
}

- (CGRect)rectForImageViewAtIndex:(int)index
{
    CGFloat width = self.view.bounds.size.width;
    CGFloat height = self.view.bounds.size.height;
    return CGRectMake(index * width, 0, width, height);
}


- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    int index = fabs(scrollView.contentOffset.x) / self.view.bounds.size.width;
    self.pageControl.currentPage = index;

}

- (void)setupImageUrls:(NSArray *)imageUrls
{
    for (int i = 0; i < [imageUrls count]; i++) {
        UIImageView *pic = [[UIImageView alloc] init];
        pic.tag = TAG_ROTATE_IMAGE_IF_WIDTH_BIGGER_THEN_HEIGHT;
        NSString *url = [imageUrls objectAtIndex:i];
        [pic setImageWithURL:[NSURL URLWithString:url] placeholderImage:[UIImage imageNamed:@"default_screen_shot.jpg"]];
        pic.frame = [self rectForImageViewAtIndex:i];
        [scroll addSubview:pic];
        [pic release];
    }
    
    CGFloat width = self.view.bounds.size.width;
    CGFloat height = self.view.bounds.size.height;    
    scroll.contentSize = CGSizeMake(width * [imageUrls count], height);
    self.pageControl.numberOfPages = [imageUrls count];
}

- (void)showIndex:(int)index
{
    CGRect rt = [self rectForImageViewAtIndex:index];
    [scroll scrollRectToVisible:rt animated:NO];
    self.pageControl.currentPage = index;
}


- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
#pragma mark - scrollview delegate


@end
