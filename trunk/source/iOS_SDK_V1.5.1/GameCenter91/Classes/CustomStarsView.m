//
//  CustomStarsView.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/31/13.
//
//

#import "CustomStarsView.h"

@implementation CustomStarsView

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

+ (CustomStarsView *)customStarsViewWithNumber:(NSInteger)number
{
    CustomStarsView *customStarsView = [[[CustomStarsView alloc] initWithFrame:CGRectMake(0, 0, 45, 9)] autorelease];
    customStarsView.backgroundColor = [UIColor clearColor];
    for (NSInteger indexScored = 0; indexScored < number; indexScored++) {
        UIImageView *starScoredView = [[[UIImageView alloc] initWithFrame:CGRectMake(indexScored * 9, 0, 9, 9)] autorelease];
        starScoredView.tag = indexScored;
        starScoredView.image = [UIImage imageNamed:@"star_scored.png"];
        starScoredView.backgroundColor = [UIColor clearColor];
        [customStarsView addSubview:starScoredView];
    }
    for (NSInteger indexUnscored = number; indexUnscored < 5; indexUnscored++) {
        UIImageView *starUnscoredView = [[[UIImageView alloc] initWithFrame:CGRectMake(indexUnscored * 9, 0, 9, 9)] autorelease];
        starUnscoredView.tag = indexUnscored;
        starUnscoredView.image = [UIImage imageNamed:@"star_unScored.png"];
        starUnscoredView.backgroundColor = [UIColor clearColor];
        [customStarsView addSubview:starUnscoredView];
    }
    return customStarsView;
}

- (void)resetCustomStarsViewWithNumber:(NSInteger)number
{
    for (NSInteger indexScored = 0; indexScored < number; indexScored++) {
        UIImageView *starScoredView = (UIImageView *)[self viewWithTag:indexScored + 200];
        starScoredView.backgroundColor = [UIColor clearColor];
        if (starScoredView == nil) {
            starScoredView = [[[UIImageView alloc] initWithFrame:CGRectMake(indexScored * 9, 0, 9, 9)] autorelease];
            starScoredView.tag = indexScored + 200;
            [self addSubview:starScoredView];
        }
        starScoredView.image = [UIImage imageNamed:@"star_scored.png"];
    }
    for (NSInteger indexUnscored = number; indexUnscored < 5; indexUnscored++) {
        UIImageView *starUnscoredView = (UIImageView *)[self viewWithTag:indexUnscored + 200];
        starUnscoredView.backgroundColor = [UIColor clearColor];
        if (starUnscoredView == nil) {
            starUnscoredView = [[[UIImageView alloc] initWithFrame:CGRectMake(indexUnscored * 9, 0, 9, 9)] autorelease];
            starUnscoredView.tag = indexUnscored + 200;
            [self addSubview:starUnscoredView];
        }
        starUnscoredView.image = [UIImage imageNamed:@"star_unScored.png"];
    }
    self.backgroundColor = [UIColor clearColor];
}

@end
