
//
//  SectionHeadCell.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-30.
//
//

#import "SectionHeadCell.h"
#import "CommUtility.h"
#define RGB(r,g,b)          [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:1.0]
#define EXPENDLABELTAG 200
#define LINEVIEWTAG       201
#define EXPENDIMAGE 202
#define BACKVIEWTAG 302
#define ADAPT_X 11 //适配ios7

@implementation SectionHeadCell
- (void)dealloc
{
    self.lineView = nil;
    [super dealloc];
}

- (id)init
{
    self = [super init];
    if (self) {
        //调整cell的高度
        CGRect rc = self.frame;
        rc.size.height = 33;
        self.frame = rc;
        
        self.lineView = [[[UIView alloc] initWithFrame:CGRectMake(0, self.frame.size.height - 1, self.frame.size.width - 14, 1)] autorelease];
        self.lineView.backgroundColor = RGB(0x14, 0x7f, 0xb5);
        self.lineView.tag = LINEVIEWTAG;
        [self.contentView addSubview:self.lineView];
        self.textLabel.textColor = RGB(0x14, 0x7f, 0xb5);
        self.selectionStyle = UITableViewCellSelectionStyleNone;

        if ([CommUtility isIOS7]) {
            CGRect rc = self.lineView.frame;
            rc.origin.x += ADAPT_X;
            self.lineView.frame = rc;
        }
    }
    return self;
}
- (void)addMoreRightButtonWithTarget:(id)target action:(SEL)action forControlEvents:(UIControlEvents)controlEvents
{
    UILabel *labelShowMore = [[[UILabel alloc] initWithFrame:CGRectMake(145, 3, 150, 25)] autorelease];
    labelShowMore.userInteractionEnabled = YES;
    labelShowMore.textColor = [UIColor grayColor];
    labelShowMore.textAlignment = NSTextAlignmentRight;
    labelShowMore.backgroundColor = [UIColor clearColor];
    labelShowMore.text = @"更多>";
    labelShowMore.font = [UIFont systemFontOfSize:14.0];
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer new] autorelease];
    [gesture addTarget:target action:action];
    [labelShowMore addGestureRecognizer:gesture];

    [self.contentView addSubview:labelShowMore];

}
- (void)addexpendRightButtonWithTarget:(id)target action:(SEL)action forControlEvents:(UIControlEvents)controlEvents
{
    self.lineView.hidden = YES;
    
    UIView *view = [[[UIView alloc] initWithFrame:CGRectMake(140,  3, 155, 25)] autorelease];
    UIImageView *imgView = [[[UIImageView alloc] initWithFrame:CGRectMake(140, 8, CGRectGetWidth(view.frame) - 140, 9)] autorelease];
    imgView.image = [UIImage imageNamed:@"dropDown_Arrow"];
    imgView.tag = EXPENDIMAGE;
    [view addSubview:imgView];
    
    UILabel *expendLabel = [[[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMinX(imgView.frame) - 50, 0, 50, 25)] autorelease];
    expendLabel.textAlignment = NSTextAlignmentRight;
    expendLabel.tag = EXPENDLABELTAG;
    expendLabel.text = @"展开 ";
    expendLabel.textColor = [UIColor grayColor];
    expendLabel.font = [UIFont systemFontOfSize:14.0];
    [view addSubview:expendLabel];
    
    UITapGestureRecognizer *tapGes = [[[UITapGestureRecognizer alloc] initWithTarget:target action:action] autorelease];
    [view addGestureRecognizer:tapGes];
    
    view.tag = BACKVIEWTAG;
    [self.contentView addSubview:view];
    
}

- (void)removeExpendRightButton
{
    self.lineView.hidden = NO;
    UIView *view = [self.contentView viewWithTag:BACKVIEWTAG];
    [view removeFromSuperview];
}

@end
