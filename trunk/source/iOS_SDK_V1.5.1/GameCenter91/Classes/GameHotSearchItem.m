//
//  GameHotSearchItem.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-12.
//
//

#import "GameHotSearchItem.h"
#define FONT_LABELTEXT 14
#define RGB(r,g,b)          [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:1.0]

@interface GameHotSearchItem()

@property (nonatomic, retain)UIButton *touchBtn;
@property (nonatomic, assign)CGSize viewSize_self;

@end
@implementation GameHotSearchItem

- (void)dealloc
{
    self.bgImageView = nil;
    self.gameIconImgView = nil;
    self.hotSearchLabel = nil;
    self.gameTitleLabel = nil;
    self.touchBtn = nil;
    [super dealloc];
}
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.backgroundColor = RGB(0xdc, 0xde, 0xdf);
        self.viewSize_self = self.frame.size;
        [self addCustomSubView];
    }
    return self;
}

- (void)adjustTitleFrame
{
    CGSize totalSize = [self.gameTitleLabel.text sizeWithFont:self.gameTitleLabel.font constrainedToSize:CGSizeMake(self.gameTitleLabel.frame.size.width, 1000)];
    if (totalSize.height < CGRectGetHeight(self.gameTitleLabel.frame)) {
        CGRect rc = self.gameTitleLabel.frame;
        rc.size.height = totalSize.height;
        self.gameTitleLabel.frame = rc;
    }
}

- (void)addCustomSubView
{
    //add backgroundView
    self.bgImageView = [[[UIImageView alloc] initWithFrame:self.bounds] autorelease];
    [self addSubview:self.bgImageView];
    //add gameTitleLabel
    self.gameTitleLabel = [[[UILabel alloc] initWithFrame:[self getTitleLabelFrame]] autorelease];
    self.gameTitleLabel.numberOfLines = 0;
    self.gameTitleLabel.textAlignment = UITextAlignmentLeft;
    self.gameTitleLabel.backgroundColor = [UIColor clearColor];
    self.gameTitleLabel.textColor = RGB(0x66, 0x66, 0x66);
    self.gameTitleLabel.font = [UIFont systemFontOfSize:FONT_LABELTEXT];
    
    //add gameIconImgView
    self.gameIconImgView = [[[UIImageView alloc] initWithFrame:[self getIconImgVieFrame]] autorelease];
    
    [self addSubview:self.gameIconImgView];
    [self addSubview: self.gameTitleLabel];
    //add hotSearchLabel
    self.hotSearchLabel = [[[UILabel alloc] initWithFrame:[self getHotSearchLabelFrame]] autorelease];
    self.hotSearchLabel.numberOfLines = 0;
    self.hotSearchLabel.textAlignment = UITextAlignmentCenter;
    self.hotSearchLabel.backgroundColor = [UIColor clearColor];
    self.hotSearchLabel.textColor = RGB(0x66, 0x66, 0x66);
    self.hotSearchLabel.font = [UIFont systemFontOfSize:FONT_LABELTEXT];
    [self addSubview:self.hotSearchLabel];
    
    self.touchBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.touchBtn.frame = [self getTouchBtnFrame];
    self.touchBtn.backgroundColor = [UIColor clearColor];
    [self.touchBtn addTarget:self.parentCtrl action:@selector(btnItemPress:) forControlEvents:UIControlEventTouchUpInside];
    
    [self addSubview:self.touchBtn];
}
- (CGRect)getTitleLabelFrame
{
    CGFloat width = self.viewSize_self.width;
    CGFloat height = self.viewSize_self.height / 2;
    return CGRectMake(3, 3, width, height);
}
- (CGRect)getHotSearchLabelFrame
{
    CGFloat width = self.viewSize_self.width;
    CGFloat height = self.viewSize_self.height / 2;
    CGFloat originY = height / 2;
    return CGRectMake(0, originY, width, height);
    
}
- (CGRect)getIconImgVieFrame
{
    CGFloat height = self.viewSize_self.height / 3;
    CGFloat width = height;
    CGFloat originX = self.viewSize_self.width - width - width / 4;
    CGFloat originY = self.viewSize_self.height - height - height / 4;
    return CGRectMake(originX, originY, width, height);
}
- (CGRect)getTouchBtnFrame
{
    return CGRectMake(0, 0, CGRectGetWidth(self.frame), CGRectGetHeight(self.frame));
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
