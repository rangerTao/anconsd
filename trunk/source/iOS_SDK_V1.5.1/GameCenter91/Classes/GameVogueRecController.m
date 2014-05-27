//
//  GameVogueRecController.m
//  GameCenter91
//
//  Created by hiyo on 13-1-23.
//  Copyright (c) 2013年 __MyCompanyName__. All rights reserved.
//

#import "GameVogueRecController.h"
#import "GameCenterMacros.h"
#import "ProjectInfo.h"
#import "ActivityDetailCtrl.h"
#import "ActivityInfo.h"
#import "GameDetailController.h"
#import <NdComPlatform/NdComPlatform.h>
#import "GameTopicController.h"
#import "ColorfulLabelView.h"
#import "UserData.h"

#import "MBProgressHUD.h"
#import "UIImageView+WebCache.h"
#import "CommUtility.h"
#import "TabContainerController.h"
#import "UIViewController+Extent.h"
#import "GameDetailWebCtrl.h"
#import "ReportCenter.h"

#define MAX_POS     14
#define MAIN_FONT_SIZE      16.0
#define SUB_FONT_SIZE       10.0
#define REMARK_FONT_SIZE    13.0

//#define MAR_L   6.0
#define MAR_L   2.0
#define MAR_M   4.0
#define MAR_S   2.0

#define W_S     155.0
#define H_S     89.0
#define W_L     312.0
#define H_L     151.0


@interface GameVogueRecController()<GetRecommendedPointProtocol>
@property (nonatomic, retain) NSMutableArray *vogueInfos;
@property (nonatomic, retain) NSMutableArray *vogueViews;
@property (nonatomic, assign) BOOL isFirstLoading;

- (CGRect)frameWithPosition:(int)position;
- (void)updateWithVogueInfos:(NSArray *)arr;
- (void)updatePictureWithVogueInfo:(ProjectInfo *)item;
- (void)updateTopicWithVogueInfo:(ProjectInfo *)item;
- (void)updateGameWithVogueInfo:(ProjectInfo *)item;
- (int)heightOfLabel:(UILabel *)label constrainedWidth:(float)width;
- (UILabel *)labelWithText:(NSString *)text lineNum:(int)lineNum fontSize:(float)fontSize bBold:(BOOL)bBold;
- (void)addBlockButton:(int)position;
@end

@implementation GameVogueRecController
@synthesize vogueInfos, vogueViews;

- (id)init
{
	self = [super init];
	if (self) {
        self.title = @"靓点推荐";
        
        self.vogueInfos = [NSMutableArray arrayWithCapacity:MAX_POS];
        self.vogueViews = [NSMutableArray arrayWithCapacity:MAX_POS];
        
        self.isFirstLoading = YES;
	}
	return self;
}

- (void)dealloc
{
    self.vogueInfos = nil;
    self.vogueViews = nil;
    
    [super dealloc];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:YES otherExcludeHeight:[TabContainerController defaultSegmentHeight]];
    UIScrollView *scrollView = [[[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320.0, height)] autorelease];
    scrollView.contentSize = CGSizeMake(320, H_L*2+H_S*7+MAR_M+MAR_L*5+MAR_S*3+MAR_M);
    [self.view addSubview:scrollView];
    
    for (int i = 0; i < MAX_POS; i++) {
        UIView *vogueView = [[[UIView alloc] init] autorelease];
        srand((unsigned)time(0));
        vogueView.backgroundColor = [UIColor colorWithRed:random()%100/100.0 green:random()%100/100.0 blue:random()%100/100.0 alpha:1];
        vogueView.frame = [self frameWithPosition:i];
        [scrollView addSubview:vogueView];
        
        [self.vogueViews addObject:vogueView];
        [self.vogueInfos addObject:[[[ProjectInfo alloc] init] autorelease]];
    }
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (void)viewWillAppear:(BOOL)animated
{
    NSNumber *res = [RequestorAssistant requestRecommendedPoint:self];
    if ([res intValue] >= 0) {
        if (self.isFirstLoading == YES) {
            [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        }
    }
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

#pragma mark -
- (CGRect)frameWithPosition:(int)position
{
    CGRect rc;
    int i = position;
    switch (i) {
        case 0:
        case 5:
            rc = CGRectMake(MAR_M, MAR_M+i/5*(H_L+H_S*2+MAR_L*2+MAR_S), W_L, H_L);
            break;
        case 10:
            rc = CGRectMake(MAR_M, MAR_M+i/5*(H_L+H_S*2+MAR_L*2+MAR_S), W_L, H_S);
            break;
        case 1:case 2:case 3:case 4:
            rc = CGRectMake(MAR_M+(i-1)%2*(W_S+MAR_S), MAR_M+(H_L+MAR_L)+(i-1)/2*(H_S+MAR_S), W_S, H_S);
            break;
        case 6:case 7:case 8:case 9:
            rc = CGRectMake(MAR_M+i%2*(W_S+MAR_S), MAR_M+(H_L*2+H_S*2+MAR_L*3+MAR_S)+(i-6)/2*(H_S+MAR_S), W_S, H_S);
            break;
        case 11:case 12:
            rc = CGRectMake(MAR_M, MAR_M+(H_L*2+H_S*5+MAR_L*5+MAR_S*2)+(i-11)*(H_S+MAR_S), W_S, H_S);
            break;
        case 13:
            rc = CGRectMake(MAR_M+(W_S+MAR_S), MAR_M+(H_L*2+H_S*5+MAR_L*5+MAR_S*2), W_S, H_S*2+MAR_S);
            break;
        default:
            break;
    }
    
    return rc;
}

- (void)updateWithVogueInfos:(NSArray *)arr
{
    int flag[MAX_POS];
    for (int i = 0; i < MAX_POS; i++) {
        flag[i] = NO;
    }
    
    for (int i = 0; i < [arr count]; i++) {
        ProjectInfo *item = [arr objectAtIndex:i];
        int index = item.position-1;
        if (index >= MAX_POS || index < 0) {
            continue;
        }
        //BrightSpotItem changed
        if (![item isEqual:[vogueInfos objectAtIndex:index]]) {
            //vogue type changed
//            if (item.projectType != [[vogueInfos objectAtIndex:item.position] projectType]) {
                UIView *vogue = [self.vogueViews objectAtIndex:index];
                for (UIView *subView in vogue.subviews) {
                    [subView removeFromSuperview];
                }
//            }
            //update view
            switch (item.projectType) {
                case VOGUE_PICTURE:
                    [self updatePictureWithVogueInfo:item];
                    break;
                case VOGUE_TOPIC:
                    [self updateTopicWithVogueInfo:item];
                    break;
                case VOGUE_GAME:
                    [self updateGameWithVogueInfo:item];
                    break;
                default:
                    break;
            }
            //background color                
            vogue.backgroundColor = [CommUtility colorWithHexRGB:item.bgColor];
            //save
            [self.vogueInfos replaceObjectAtIndex:index withObject:item];
        }
        flag[index] = YES;
    }
    
    //delete items no need to display
    for (int i = 0; i < MAX_POS; i++) {
        if (flag[i] == NO) {
            UIView *vogue = [self.vogueViews objectAtIndex:i];
            for (UIView *subView in vogue.subviews) {
                [subView removeFromSuperview];
            }
        }
    }
}

- (void)updatePictureWithVogueInfo:(ProjectInfo *)item
{
    int index = item.position-1;
    UIView *vogue = [self.vogueViews objectAtIndex:index];
    CGRect rc = vogue.bounds;
    //图片
    UIImageView *imgView = [[[UIImageView alloc] initWithFrame:rc] autorelease];
    NSString *defaultPicStr = nil;
    if (rc.size.width == 310 && rc.size.height == 150) {
        defaultPicStr = @"No1Position.jpg";
    }
    else if (rc.size.width == 150 && rc.size.height == 85) {
        defaultPicStr = @"No2Position.jpg";
    }
    else if (rc.size.width == 310 && rc.size.height == 85) {
        defaultPicStr = @"No11Position.jpg";
    }
    else if (rc.size.width == 150 && rc.size.height == 180) {
        defaultPicStr = @"No14Position.jpg";
    }
    [imgView setImageWithURL:[NSURL URLWithString:item.imageUrl] placeholderImage:[UIImage imageNamed:defaultPicStr]];
    [vogue addSubview:imgView];
    //备注
    if ([item.mainTitle length] > 0) {
        UIView *banner = [[[UIView alloc] initWithFrame:rc] autorelease];
        banner.frame = CGRectMake(0, CGRectGetHeight(rc)-20.0f, CGRectGetWidth(rc), 20.0f);
        banner.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.6];
        [vogue addSubview:banner];
        
        UILabel *label = [[[UILabel alloc] init] autorelease];
        label.frame = CGRectMake(5, 2, CGRectGetWidth(rc)-5*2, 20-2*2);
        label.font = [UIFont systemFontOfSize:REMARK_FONT_SIZE];
        label.textAlignment = UITextAlignmentCenter;
        label.textColor = [UIColor whiteColor];
        label.backgroundColor = [UIColor clearColor];
        label.text = item.mainTitle;
        [banner addSubview:label];
    }
    //链接
    if ([item.targetAction length] > 0) {
        [self addBlockButton:index];
    }
}

- (void)updateTopicWithVogueInfo:(ProjectInfo *)item
{
    int index = item.position-1;
    UIView *vogue = [self.vogueViews objectAtIndex:index];
    CGRect rc = vogue.bounds;
    float height = 0.0;
    //主标题
    UILabel *mainLabel = [self labelWithText:item.mainTitle lineNum:2 fontSize:MAIN_FONT_SIZE bBold:YES];
    height = [self heightOfLabel:mainLabel constrainedWidth:CGRectGetWidth(rc)-12*2];
    mainLabel.frame = CGRectMake(12, 10, CGRectGetWidth(rc)-12*2, height);
    [vogue addSubview:mainLabel];
    //副标题
    UILabel *subLabel = [self labelWithText:item.subTitle lineNum:1 fontSize:SUB_FONT_SIZE bBold:NO];
    height = [self heightOfLabel:subLabel constrainedWidth:100];
    subLabel.frame = CGRectMake(12, CGRectGetMaxY(mainLabel.frame)+4, 100, height);
    [vogue addSubview:subLabel];
    //标签
    if ([item.labelList length] > 0) {
        ColorfulLabelView *clView = [ColorfulLabelView colorfulLabelViewWithPackIconsStr:item.labelList bVogue:YES];
        clView.frame = CGRectMake(CGRectGetWidth(rc)-CGRectGetWidth(clView.frame)-4, CGRectGetHeight(rc)-CGRectGetHeight(clView.frame)-4, CGRectGetWidth(clView.frame), CGRectGetHeight(clView.frame));
        [vogue addSubview:clView];
    }
    //链接
    if ([item.targetAction length] > 0) {
        [self addBlockButton:index];
    }
}

- (void)updateGameWithVogueInfo:(ProjectInfo *)item
{
    int index = item.position-1;
    UIView *vogue = [self.vogueViews objectAtIndex:index];
    CGRect rc = vogue.bounds;
    //图标    
    UIImageView *icon = [[[UIImageView alloc] init] autorelease];
    icon.frame = CGRectMake(12, 10, 55, 55);
    [icon setImageWithURL:[NSURL URLWithString:item.imageUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
    [vogue addSubview:icon];
    //主标题
    if ([item.mainTitle length] > 0) {
        UILabel *mainLabel = [self labelWithText:item.mainTitle lineNum:2 fontSize:MAIN_FONT_SIZE bBold:YES];
        float height = [self heightOfLabel:mainLabel constrainedWidth:CGRectGetWidth(rc)-CGRectGetWidth(icon.frame)-12*3];
        mainLabel.frame = CGRectMake(CGRectGetMaxX(icon.frame)+12, 10, CGRectGetWidth(rc)-CGRectGetWidth(icon.frame)-12*3, height);
        [vogue addSubview:mainLabel];
    }
    //副标题
    if ([item.subTitle length] > 0) {
        UILabel *subLabel = [self labelWithText:item.subTitle lineNum:1 fontSize:SUB_FONT_SIZE bBold:NO];
        float height = [self heightOfLabel:subLabel constrainedWidth:100];
        subLabel.frame = CGRectMake(12, CGRectGetHeight(rc)-height-4, 100, height);
        [vogue addSubview:subLabel];
    }
    //标签
    if ([item.labelList length] > 0) {
        ColorfulLabelView *clView = [ColorfulLabelView colorfulLabelViewWithPackIconsStr:item.labelList bVogue:YES];
        clView.frame = CGRectMake(CGRectGetWidth(rc)-CGRectGetWidth(clView.frame)-4, CGRectGetHeight(rc)-CGRectGetHeight(clView.frame)-4, CGRectGetWidth(clView.frame), CGRectGetHeight(clView.frame));
        [vogue addSubview:clView];
    }
    //链接
    if ([item.targetAction length] > 0) {
        [self addBlockButton:index];
    }
}

- (int)heightOfLabel:(UILabel *)label constrainedWidth:(float)width
{
    CGSize totoalSize = [label.text sizeWithFont:label.font constrainedToSize:CGSizeMake(width, 1000)];
    CGSize oneLineSize = [@" " sizeWithFont:label.font];
    int totalLines = totoalSize.height/oneLineSize.height;
    return totalLines * oneLineSize.height;
}

- (UILabel *)labelWithText:(NSString *)text lineNum:(int)lineNum fontSize:(float)fontSize bBold:(BOOL)bBold
{
    UILabel *label = [[[UILabel alloc] init] autorelease];
    label.font = bBold ? [UIFont boldSystemFontOfSize:fontSize] : [UIFont systemFontOfSize:fontSize];
    label.textColor = [UIColor whiteColor];
    label.backgroundColor = [UIColor clearColor];
    label.text = text;
    label.numberOfLines = lineNum;
    return label;
}

- (void)addBlockButton:(int)position
{
    UIView *vogue = [self.vogueViews objectAtIndex:position];
    CGRect rc = vogue.bounds;
    
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.tag = position;
    btn.backgroundColor = [UIColor clearColor];
    btn.frame = rc;
    [btn addTarget:self action:@selector(blockPress:) forControlEvents:UIControlEventTouchUpInside];
    [vogue addSubview:btn];
}

- (void)blockPress:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    int position = btn.tag;
    if (position >= MAX_POS || position < 0) {
        return;
    }
    ProjectInfo *item = [self.vogueInfos objectAtIndex:position];
    switch (item.projectType) {
        case VOGUE_PICTURE:
            break;
        case VOGUE_TOPIC:
            break;
        case VOGUE_GAME:
            break;
        default:
            break;
    }
    switch (item.targetType) {
        case V_TARGET_ACTIVITY_DETAIL:
        {
            //跳转到活动详情
            NSArray *arr = [item.targetAction componentsSeparatedByString:@","];
            if ([arr count] == 2) {
                NSString *contentUrl = item.targetActionUrl;
                NSString *appIdentifier = [arr objectAtIndex:1];
                int activityID = [[arr objectAtIndex:0] intValue];
                NSString *title = item.mainTitle;
                
                [CommUtility pushActivityDetailCtrl:appIdentifier activityId:activityID activityUrl:contentUrl activityTitle:title navigationController:self.parentContainerController.navigationController];
                
                //统计
                int reportNum = ANALYTICS_EVENT_15036 + position;
                int fromNum = ANALYTICS_EVENT_15083 + position;
                [ReportCenter report:reportNum label:appIdentifier downloadFromNum:fromNum];
            }
        }
            break;
        case V_TARGET_GAME_DETAIL:
        {            
            //跳转到游戏详情
            NSString *identifier = item.targetAction;
            [CommUtility pushGameDetailController:identifier gameName:item.mainTitle navigationController:self.parentContainerController.navigationController];
            
            //统计
            int reportNum = ANALYTICS_EVENT_15036 + position;
            int fromNum = ANALYTICS_EVENT_15083 + position;
            [ReportCenter report:reportNum label:identifier downloadFromNum:fromNum];
        }
            break;
        case V_TARGET_GAME_TOPIC:
        {
            //跳转到游戏专题
            int topicId = [item.targetAction intValue];
            [CommUtility pushGameTopicController:topicId navigationController:self.parentContainerController.navigationController];
        }
            break;
        case V_TARGET_PROP_DETAIL:
        {
            //跳转到道具详情
        }
            break;
        case V_TARGET_WEB_DETAIL:
        {
            //跳转到网页链接
            GameDetailWebCtrl *vogueWebViewCtr = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:item.targetAction];
            vogueWebViewCtr.hidesBottomBarWhenPushed = YES;
            vogueWebViewCtr.customTitle = item.mainTitle;
            [self.parentContainerController.navigationController pushViewController:vogueWebViewCtr animated:YES];
        }
            break;
        default:
            break;
    }
}

#pragma mark -
- (void)operation:(GameCenterOperation *)operation getRecommendedPointDidFinish:(NSError *)error projectList:(NSArray *)projectList
{
    [MBProgressHUD hideHUDForView:self.view animated:NO];
    self.isFirstLoading = NO;
    if (error == nil) {
        [self updateWithVogueInfos:projectList];
    }
}

@end
