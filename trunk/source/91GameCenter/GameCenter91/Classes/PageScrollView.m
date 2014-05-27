//
//  PageScrollView.m
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-1.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import "PageScrollView.h"

@interface PageScrollView()<UIScrollViewDelegate>
@property (nonatomic, retain, readwrite) NSArray *contentPageViews;
@property (nonatomic, assign) CGSize pageSize;
//@property (nonatomic, assign, readwrite) int currentPage;
@end

@implementation PageScrollView
@synthesize direction;
@synthesize pageSize;
@synthesize contentPageViews;
@synthesize currentPage;

- (id) initWithFrame:(CGRect)rect
{
    self = [super initWithFrame:rect];
    if (self != nil) {
        self.backgroundColor = [UIColor clearColor];
        scrollView = [[[UIScrollView alloc] initWithFrame:self.bounds] autorelease];
        scrollView.backgroundColor = [UIColor clearColor];
        scrollView.delegate = self;
        scrollView.showsVerticalScrollIndicator = NO;
        scrollView.showsHorizontalScrollIndicator = NO;
        [self addSubview:scrollView];
        self.pageControl = nil;
        self.customPageControl = nil;
        
        self.direction = ScrollHorizental;
        self.pageSize = rect.size;
        self.currentPage = 0;
    }
    return self;
}

- (void) dealloc
{
    self.contentPageViews = nil;
    [super dealloc];
}

- (void)setPageControl:(UIPageControl *)control
{
    pageControl = control;
    customPageControl = nil;
}

- (UIPageControl *)pageControl
{
    return pageControl;
}

- (void)setCustomPageControl:(id <PageControlProtocol>)control
{
    customPageControl = control;
    pageControl = nil;
}

- (id<PageControlProtocol>)customPageControl
{
    return customPageControl;
}

- (void)scrollToPageIndex:(int)index animated:(BOOL)animated
{
    int count = [self.contentPageViews count];
    if (index >= 0 && index < count)
    {
        int offsetByX = (self.direction == ScrollHorizental) ? 1 : 0;
        int offsetByY = (self.direction == ScrollHorizental) ? 0 : 1;

        [scrollView scrollRectToVisible:
                    CGRectMake(self.pageSize.width * offsetByX * index, self.pageSize.height * offsetByY * index, self.pageSize.width, self.pageSize.height) 
                               animated:animated];
    }
}

- (void)setPageViews:(NSArray *)pageViews
{
    self.contentPageViews = pageViews;
    for (UIView *view in [scrollView subviews]) {
        [view removeFromSuperview];
    }
    
    int count = [pageViews count];
    int offsetByX = (self.direction == ScrollHorizental) ? 1 : 0;
    int offsetByY = (self.direction == ScrollHorizental) ? 0 : 1;
    
    scrollView.pagingEnabled = YES;        
    CGFloat pageWidth = self.pageSize.width;
    CGFloat pageHeight = self.pageSize.height;
    
    scrollView.contentSize = (self.direction == ScrollHorizental) ? CGSizeMake(pageWidth * count, pageHeight) : CGSizeMake(pageWidth, pageHeight * count);
    for (int i = 0; i < count; i++)
    {
        UIView *sub = [pageViews objectAtIndex:i];
        CGFloat subWidth = sub.frame.size.width;
        CGFloat subHeight = sub.frame.size.height;
        CGFloat subX = 0;
        CGFloat subY = 0;
        if (subWidth < pageWidth)
        {
            subX = (pageWidth - subWidth) / 2;
        }
        else
        {
            subWidth = pageWidth;
        }
        
        if (subHeight < pageHeight)
        {
            subY = (pageHeight - subHeight) / 2;
        }
        else
        {
            subHeight = pageHeight;
        }
        sub.frame = CGRectMake(subX + (pageWidth * offsetByX * i), subY + (pageHeight * offsetByY * i), subWidth, subHeight);
        [scrollView addSubview:sub];
    }
    if (self.pageControl)
    {
        self.pageControl.numberOfPages = count;
    }
    else if (self.customPageControl)
    {
        self.customPageControl.numberOfPages = count;
    }

}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scroll
{
    CGPoint pt = scroll.contentOffset;
    
    int page = 0;
    if (self.direction == ScrollHorizental)
    {
        CGFloat pageWidth = scroll.frame.size.width;        
        page = floor(pt.x / pageWidth);
    }
    else
    {
        CGFloat pageHeight = scroll.frame.size.height;        
        page = floor(pt.y / pageHeight);        
    }

    self.currentPage = page;
    if (self.pageControl)
    {
        self.pageControl.currentPage = page;
    }
    else if (self.customPageControl)
    {
        self.customPageControl.currentPage = page;
    }
}

@end
