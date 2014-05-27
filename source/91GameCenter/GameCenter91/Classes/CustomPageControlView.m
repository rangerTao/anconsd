//
//  CustomPageControlView.m
//  GameCenter91
//
//  Created by Li.Binbin on 11/12/13.
//
//

#import "CustomPageControlView.h"

@interface CustomPageControlView ()

@property (nonatomic, assign) NSUInteger totalPageCount;

@end

@implementation CustomPageControlView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

+ (CustomPageControlView *)customPageControlViewWithTotalNumber:(NSInteger)totalNumber
{
    static CGFloat distance = 10;
    CustomPageControlView *customPageControlView = [[[CustomPageControlView alloc] initWithFrame:CGRectMake(0, 0, 320, 20)] autorelease];
    customPageControlView.backgroundColor = [UIColor clearColor];
    
    if (totalNumber%2 == 0) {
        for (NSInteger index = 0; index < totalNumber/2; index++) {
            UIImageView *pageViewLeft = [[[UIImageView alloc] initWithFrame:CGRectMake((320 - distance)/2 - 8 - (8 + distance) *index, 10, 8, 8)] autorelease];
            pageViewLeft.tag = totalNumber/2 - index - 1 + 100;
            pageViewLeft.backgroundColor = [UIColor clearColor];
            [customPageControlView addSubview:pageViewLeft];
            
            UIImageView *pageViewRight = [[[UIImageView alloc] initWithFrame:CGRectMake((320 + distance)/2 + (8 + distance) *index, 10, 8, 8)] autorelease];
            pageViewRight.tag = totalNumber/2 + index + 100;
            pageViewRight.backgroundColor = [UIColor clearColor];
            [customPageControlView addSubview:pageViewRight];
        }
    } else {
        for (NSInteger index = 0; index < totalNumber/2; index++) {
            UIImageView *pageViewLeft = [[[UIImageView alloc] initWithFrame:CGRectMake((320 - 8)/2 - (8 + distance) * (index + 1), 10, 8, 8)] autorelease];
            pageViewLeft.tag = totalNumber/2 - index - 1 + 100;
            pageViewLeft.backgroundColor = [UIColor clearColor];
            [customPageControlView addSubview:pageViewLeft];
            
            UIImageView *pageViewRight = [[[UIImageView alloc] initWithFrame:CGRectMake((320 + 8)/2 + + distance + (8 + distance) * index, 10, 8, 8)] autorelease];
            pageViewRight.tag = totalNumber/2 + index + 1 + 100;
            pageViewRight.backgroundColor = [UIColor clearColor];
            [customPageControlView addSubview:pageViewRight];
        }
        
        UIImageView *pageViewMiddle = [[[UIImageView alloc] initWithFrame:CGRectMake((320 - 8)/2, 10, 8, 8)] autorelease];
        pageViewMiddle.tag = totalNumber/2 + 100;
        pageViewMiddle.backgroundColor = [UIColor clearColor];
        [customPageControlView addSubview:pageViewMiddle];
    }
    
    customPageControlView.totalPageCount = totalNumber;
    return customPageControlView;
}

- (void)resetCustomStarsViewWithCurrentPageNumber:(NSInteger)currentPageNumber
{
    for (NSInteger index = 0; index < currentPageNumber; index++) {
        UIImageView *pageView = (UIImageView *)[self viewWithTag:index + 100];
        pageView.image = [UIImage imageNamed:@"no_current_page.png"];
    }
    
    UIImageView *pageView = (UIImageView *)[self viewWithTag:currentPageNumber + 100];
    pageView.image = [UIImage imageNamed:@"current_page.png"];
    
    for (NSInteger index = currentPageNumber + 1; index < self.totalPageCount; index++) {
        UIImageView *pageView = (UIImageView *)[self viewWithTag:index + 100];
        pageView.image = [UIImage imageNamed:@"no_current_page.png"];
    }
}

@end
