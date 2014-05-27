//
//  CyclePageScrollView.m
//  Untitled
//
//  Created by Sie Kensou on 12-8-20.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import "CyclePageScrollView.h"

@interface CyclePageScrollView ()
@property (nonatomic, assign) BOOL bCouple;
@end

@implementation CyclePageScrollView
@synthesize bCouple;

- (id) initWithFrame:(CGRect)rect
{
    self = [super initWithFrame:rect];
    if (self != nil) {
        self.currentPage = 0;
    }
    return self;
}


- (void)dealloc {
    [super dealloc];
}

- (void)resetSubViewFrames
{
    int count = [self.contentPageViews count];    
    if (count <= 1)
        return;
    

    int offsetByX = (self.direction == ScrollHorizental) ? 1 : 0;
    int offsetByY = (self.direction == ScrollHorizental) ? 0 : 1;
        
    for (int i = 0; i < count; i++)
    {
        UIView *sub = [self.contentPageViews objectAtIndex:i];
        int index = (i - self.currentPage + 1 + count) % count;
        index = bCouple ? i : index;
        CGFloat x = index * self.pageSize.width * offsetByX + self.pageSize.width / 2;
        CGFloat y = index * self.pageSize.height * offsetByY + self.pageSize.height / 2;
        sub.center = CGPointMake(x, y);
    }

    [self scrollToPageIndex:(bCouple ? self.currentPage : 1) animated:NO];
}

- (void)setPageViews:(NSArray *)pageViews
{
    if ([pageViews count] == 2) {
        self.bCouple = YES;
    }
    [super setPageViews:pageViews];
    [self resetSubViewFrames];
}

- (void)setCurrentPage:(int)page
{
    [super setCurrentPage:page];
    [self resetSubViewFrames];
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

    int count = [self.contentPageViews count];
	self.currentPage = bCouple ? page : (self.currentPage + (page - 1) + count) % count;
    
    if (self.pageControl)
    {
        self.pageControl.currentPage = self.currentPage;
    }
    else if (self.customPageControl)
    {
        self.customPageControl.currentPage = self.currentPage;
    }

//    [self resetSubViewFrames];
}
@end
