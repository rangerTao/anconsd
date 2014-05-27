//
//  GameHotSearchDisPlayView.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-14.
//
//

#import "GameHotSearchDisPlayView.h"
#import "GameHotSearchContentView.h"
#import <NdComPlatform/NdComPlatformAPIResponse.h>
#import "RequestorAssistant.h"
#import "OptionProtocols.h"

#define HEIGHT_LABEL        15.0f
#define HEIGHT_CONTENT      300.0f
#define HEIGHT_CHANGEBTN    30.0f
#define HEIGHT_SPACE        5.0f
#define WIDTH_CONTENT       300.0f
#define WIDTH_CHANGEBTN     180.0f
#define RGB(r,g,b)          [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:1.0]

@interface GameHotSearchDisPlayView()<GetHotSearchListProtocol>

@property (nonatomic, retain) UILabel *hotSearchLab;
@property (nonatomic, retain)GameHotSearchContentView *hotSearchContentView;
@property (nonatomic, retain) UIButton *changeBtn;

@end
@implementation GameHotSearchDisPlayView
- (void)dealloc
{
    self.hotSearchLab = nil;
    self.hotSearchContentView = nil;
    self.changeBtn = nil;
    [super dealloc];
}
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}
- (void)addSubViews
{
    self.hotSearchLab = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, 320.0, HEIGHT_LABEL)]autorelease];
    self.hotSearchLab.backgroundColor = [UIColor clearColor];

    
    self.hotSearchContentView = [[[GameHotSearchContentView alloc] initWithFrame:[self getContentViewFrame]] autorelease];
    self.hotSearchContentView.backgroundColor = [UIColor clearColor];
    self.hotSearchContentView.parentCtrl = self.parentCtrl;
    self.bZeroHotList = NO;
    
    [self addSubview:self.hotSearchLab];
    [self addSubview:self.hotSearchContentView];

    

}

- (void)updateContentView
{
    self.hidden = NO;
    NSNumber *ret = [RequestorAssistant requestHotSearchList:self];
    if (ret >= 0) {
        NSLog(@"have data");
    }

}
- (CGRect)getContentViewFrame
{
    CGFloat oringinX = 160.0 - WIDTH_CONTENT / 2;
    return CGRectMake(oringinX, HEIGHT_LABEL, WIDTH_CONTENT, HEIGHT_CONTENT);
}
 - (CGRect)getChangeBtnFrame
{
    CGFloat originX = 160.0 - WIDTH_CHANGEBTN / 2;
    CGFloat originY = HEIGHT_LABEL + HEIGHT_CONTENT + HEIGHT_SPACE;
    return CGRectMake(originX, originY, WIDTH_CHANGEBTN, HEIGHT_CHANGEBTN);
}
#pragma mark - GetHotSearchListProtocol
- (void)operation:(GameCenterOperation *)operation getHotSearchListDidFinish:(NSError *)error hotSearchList:(NSArray *)hotsearchlist
{
    if (error == nil && [hotsearchlist count] != 0) {
        [self.hotSearchContentView updateWithAppRecommendList:hotsearchlist];
    }else
    {
        self.bZeroHotList = YES;
        self.hidden = YES;
    }
}


@end
