//
//  PageScrollView.h
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-1.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum _PAGE_DIRECTION {
    ScrollHorizental = 0,
    ScrollVertical,
}PageDirection;

@protocol PageControlProtocol;

@interface PageScrollView : UIView
{
    UIScrollView *scrollView;
    NSArray      *contentPageViews;
    UIPageControl *pageControl;
    id<PageControlProtocol> customPageControl;        
}

@property (nonatomic, assign) PageDirection direction;  //default ScrollHorizental

@property (nonatomic, assign, readonly) CGSize pageSize;          //be meaningful only when mode is PageBySize

@property (nonatomic, retain, readonly) NSArray *contentPageViews;
- (void)setPageViews:(NSArray *)pageViews;

@property (nonatomic, assign) int currentPage;
- (void)scrollToPageIndex:(int)index animated:(BOOL)animated;

@property (nonatomic, assign) UIPageControl *pageControl;                   //you can use eather pageControl or customPageContro, but only one is in use
@property (nonatomic, assign) id<PageControlProtocol> customPageControl;    //you can use eather pageControl or customPageContro, but only one is in use
@end

@protocol PageControlProtocol
@property(nonatomic) NSInteger numberOfPages;          // default is 0
@property(nonatomic) NSInteger currentPage;            // default is 0. value pinned to 0..numberOfPages-1
@end
