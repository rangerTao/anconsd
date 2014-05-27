//
//  GameTopicController.m
//  GameCenter91
//
//  Created by hiyo on 13-1-30.
//  Copyright (c) 2013年 __MyCompanyName__. All rights reserved.
//

#import "GameTopicController.h"
#import "GameTableViewCell.h"
#import "GameDetailController.h"
#import "AppDescriptionInfo.h"
#import "GameProjectItem.h"
#import "ProgessButton.h"
#import "Notifications.h"
#import "SoftItem.h"
#import "SoftManagementCenter.h"

#import "UIViewController+Extent.h"
#import "UITableViewCell+Addition.h"
#import "CommUtility.h"
#import "MBProgressHUD.h"
#import "RTLabel.h"
#import "ReportCenter.h"

#define GATABLE_PAGE_SIZE       10
#define RGB(r,g,b)          [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:1.0]

#define TAG_TEXT            198573
#define TAG_LINE            198574                
#define TAG_JIANTOU         198575
#define TAG_SPLIT           198576
#define STR_ZHANGKAI        @"   展开"
#define STR_SHOUQI          @"   收起"

@interface GameTopicController()
@property (nonatomic, retain) NSMutableArray *gamesArr;
@property (nonatomic, assign) int delay_PageIdx;
@property (nonatomic, retain) NSString *topicTitle; //统计用

- (void)addHeaderWithDetail:(GameProjectItem *)detail;
@end

@implementation GameTopicController
@synthesize topic_table, topicId;
@synthesize gamesArr, delay_PageIdx;

- (id)init
{
	self = [super init];
	if (self) {
        self.customTitle = @"游戏专题";
        
        self.topic_table = nil;
        self.gamesArr = nil;
        self.topicTitle = nil;
	}
	return self;
}

- (void)dealloc
{
    self.topic_table = nil;
    self.gamesArr = nil;
    self.topicTitle = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    [super dealloc];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //view
    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:NO otherExcludeHeight:0];
	self.topic_table = [[[GcPageTable alloc] initWithFrame:CGRectMake(0, 0, 320.0, height) style:UITableViewStylePlain] autorelease];
	topic_table.strMoreTip = @"更多记录⋯⋯";
	//game_table.strNoDataTip = @"没有游戏哦～";
	topic_table.rowHeight = 80;
	topic_table.pageTableDelegate = self;
	topic_table.customCellCache = [GameTableViewCell loadFromNib];
	[topic_table setNdPageTableTransparent];
	[topic_table setPageSize:GATABLE_PAGE_SIZE pageCountOnce:1];
//	[topic_table enableEgoRefreshTableHeaderView];
	
	[self.view addSubview:topic_table];    
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadQueueChanged:) name:kGC91DownloadQueueChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadPercentChanged:) name:kGC91DownloadPercentChangeNotification object:nil];
    
    //get detail
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];    
    NSNumber *ret = [RequestorAssistant requestGameProjectDetail:self.topicId delegate:self];
    if ([ret intValue] < 0) {
        [MBProgressHUD hideHUDForView:self.view animated:YES];
        [MBProgressHUD showHintHUD:@"错误" message:[NSString stringWithFormat:@"%d", [ret intValue]] hideAfter:DEFAULT_TIP_LAST_TIME];
    }
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

#pragma mark - others
- (void)addHeaderWithDetail:(GameProjectItem *)detail
{
    UIView *topV = [[[UIView alloc] init] autorelease];
    //title
    UILabel *title = [[[UILabel alloc] init] autorelease];
    title.frame = CGRectMake(5, 5, 200, 21);
    title.text = detail.title;
    title.textColor = [CommUtility colorWithHexRGB:@"333333"];
    title.backgroundColor = [UIColor clearColor];
    [topV addSubview:title];
    //count
    UILabel *count = [[[UILabel alloc] init] autorelease];
    count.frame = CGRectMake(200+5*3, 5, 100, 21);
    count.textAlignment = UITextAlignmentRight;
    count.text = [NSString stringWithFormat:@"%d款", detail.gameCount];
    count.textColor = [CommUtility colorWithHexRGB:@"666666"];
    count.backgroundColor = [UIColor clearColor];
    [topV addSubview:count];
    //introduce
    RTLabel *introduce = [[[RTLabel alloc] initWithFrame:CGRectMake(5,5+21+5,310,50)] autorelease];
    introduce.tag = TAG_TEXT;
    [introduce setText:detail.introduce];
    [introduce setParagraphReplacement:@""];
    introduce.textColor = [CommUtility colorWithHexRGB:@"666666"];
    CGSize iSize = [introduce optimumSize];
    
    UIImageView *lineImgView = [[[UIImageView alloc] init] autorelease];
    lineImgView.image = [UIImage imageNamed:@"line.png"];
    lineImgView.tag = TAG_LINE;
    
    UIImageView *splitImgView = [[[UIImageView alloc] init] autorelease];
    splitImgView.image = [UIImage imageNamed:@"bg_top_half.png"];
    splitImgView.tag = TAG_SPLIT;
    
    float introHeight = 0.0;
    if (iSize.height <= 100.0) {
        introduce.frame = CGRectMake(5, 5+21+10, 310, iSize.height);
        [topV addSubview:introduce];
        splitImgView.frame = CGRectMake(0, 5+21 + iSize.height + 10, 320, splitImgView.image.size.height);
        [topV addSubview:splitImgView];
        
        introHeight = 5+21 + 10 + iSize.height + splitImgView.image.size.height;
    }
    else {
        introduce.frame = CGRectMake(5, 10+21+5, 310, 100);
        [topV addSubview:introduce];
        lineImgView.frame = CGRectMake(0, 10+21 + 5 + 100 + 5, 320, 1);
        [topV addSubview:lineImgView];
        
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        btn.frame = CGRectMake(230, 10+21 + 5 + 100 + 5, 70, 25);
        [btn setTitle:STR_ZHANGKAI forState:UIControlStateNormal];
        [btn setTitleColor:RGB(20, 127, 181) forState:UIControlStateNormal];
        [btn setBackgroundImage:[UIImage imageNamed:@"xiankuang.png"] forState:UIControlStateNormal];
        btn.backgroundColor = RGB(245, 245, 245);
        [btn addTarget:self action:@selector(btnPress:) forControlEvents:UIControlEventTouchUpInside];
        [topV addSubview:btn];
        
        UIImageView *jiantouImgView = [[[UIImageView alloc] init] autorelease];
        jiantouImgView.frame = CGRectMake(230+10, 10+21 + 5 + 100 + 5 + 10, 9, 5);
        jiantouImgView.image = [UIImage imageNamed:@"jiantou_2.png"];
        jiantouImgView.tag = TAG_JIANTOU;
        [topV addSubview:jiantouImgView];
        
        introHeight = 10+21 + 5 + 100 + 5 + 25 + 5;
    }
    
    topV.bounds = CGRectMake(0, 0, 320, introHeight);
    topic_table.tableHeaderView = topV;
}

- (void)delayToDisplay
{
    NSRange range = NSMakeRange(delay_PageIdx*GATABLE_PAGE_SIZE, MIN(GATABLE_PAGE_SIZE, [gamesArr count]-delay_PageIdx*GATABLE_PAGE_SIZE));
    NSArray *subArr = [gamesArr subarrayWithRange:range];
    [topic_table didDownloadPage:self.delay_PageIdx 
                     totalCount:[gamesArr count]
                      dataArray:subArr
                        success:YES];
}

- (void)downloadQueueChanged:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[[aNotify userInfo] objectForKey:@"ITEM"];
    int state = [[[aNotify userInfo] objectForKey:@"STATE"] intValue];
    if (state == ITEM_INSTALLED || state == ITEM_UNINSTALLED) {
        //安装和卸载成功
        [self.topic_table clearDataAndReload];
        return;
    }
    BOOL bNeedReload = NO;
    
    if (item) {
        for (UITableViewCell *cell in self.topic_table.visibleCells) {
            if ([cell isKindOfClass:[GameTableViewCell class]]) {
                GameTableViewCell *aCell = (GameTableViewCell *)cell;
                if ([aCell.gameStateButton.identifier isEqualToString:item.identifier]) {
                    bNeedReload = YES;
                    break;
                }
            }
        }
    }
    if (bNeedReload) {
        [self.topic_table reloadData];
    }
}

- (void)downloadPercentChanged:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[aNotify object];
    if (item) {
        for (UITableViewCell *cell in self.topic_table.visibleCells) {
            if ([cell isKindOfClass:[GameTableViewCell class]]) {
                GameTableViewCell *aCell = (GameTableViewCell *)cell;
                if ([aCell.gameStateButton.identifier isEqualToString:item.identifier]) {
                    [aCell updateBtnState:item];
                }
            }
        }
    }
}

- (void)btnPress:(id)sender
{
    UIView *topV = topic_table.tableHeaderView;
    RTLabel *label = (RTLabel *)[topV viewWithTag:TAG_TEXT];
    [label setLineSpacing:3.0];
    UIImageView *lineImgView = (UIImageView *)[topV viewWithTag:TAG_LINE];
    UIImageView *jiantouImgView = (UIImageView *)[topV viewWithTag:TAG_JIANTOU];
    
    float offset = 0.0;
    UIButton *btn = (UIButton *)sender;
    if ([[btn titleForState:UIControlStateNormal] isEqualToString:STR_ZHANGKAI]) {
        offset = [label optimumSize].height - 100.0;
        [btn setTitle:STR_SHOUQI forState:UIControlStateNormal];
        jiantouImgView.image = [UIImage imageNamed:@"jiantou_1.png"];
    }
    else {
        offset = 100.0 - [label optimumSize].height;
        [btn setTitle:STR_ZHANGKAI forState:UIControlStateNormal];
        jiantouImgView.image = [UIImage imageNamed:@"jiantou_2.png"];
    }
    
    CGRect rect = topV.frame;
    rect.size.height += offset;
    topV.frame = rect;
    rect = label.frame;
    rect.size.height += offset;
    label.frame = rect;
    lineImgView.frame = CGRectOffset(lineImgView.frame, 0, offset);
    jiantouImgView.frame = CGRectOffset(jiantouImgView.frame, 0, offset);
    btn.frame = CGRectOffset(btn.frame, 0, offset); 
    
    topic_table.tableHeaderView = topV;
}

#pragma mark - NdPageTableDelegate
- (void)GcPageTable:(GcPageTable*)table customCell:(UITableViewCell*)cell customData:(id)data
{
	GameTableViewCell* gameCell = (GameTableViewCell*)cell;
	if(data != nil) {        
        //得到data
		AppDescriptionInfo *appInfo = (AppDescriptionInfo *)data;
		//设置cell文本和按钮的点击处理
		gameCell.textLabel.text = @"";
		gameCell.detailTextLabel.text = @"";
		
        [gameCell.gameStateButton reset];
        [gameCell updateCellWithAppInfo:appInfo];
	}
}

- (CGFloat)	GcPageTable:(GcPageTable*)table heightForCustomCell:(UITableViewCell*)cellCache  customData:(id)data
{
//    AppDescriptionInfo *info = (AppDescriptionInfo *)data;
//    if ([info.labelIcons length] > 0) {
//        return 96+5;
//    }
    return CGRectGetHeight(cellCache.frame);
}

- (int)GcPageTable:(GcPageTable*)table downloadPageIndex:(NSInteger)pageIdx  pageSize:(NSInteger)pageSize
{
    self.delay_PageIdx = pageIdx;
    [self performSelector:@selector(delayToDisplay) withObject:nil afterDelay:0.01];
    
    return YES;
}

- (UITableViewCell*)GcPageTable:(GcPageTable*)table cellCopyFromCacheCell:(UITableViewCell*)cellCache
{
	GameTableViewCell* cellNew = [GameTableViewCell loadFromNib];
	return cellNew;
}

- (void) GcPageTable:(GcPageTable*)table didSelectRowWithData:(id)data 
{
    //跳转到游戏详情
    AppDescriptionInfo *info = (AppDescriptionInfo *)data;
    [CommUtility pushGameDetailController:info.identifier gameName:info.appName navigationController:self.navigationController];
    [self.topic_table deselectRowAtIndexPath:self.topic_table.indexPathForSelectedRow animated:YES];
    
    //统计
    [ReportCenter report:ANALYTICS_EVENT_15054 label:self.topicTitle];
}

#pragma mark - GetGameProjectDetailProtocol
- (void)operation:(GameCenterOperation *)operation getGameProjectDetailDidFinish:(NSError *)error appList:(NSArray *)appList projectDetail:(GameProjectItem*)projectDetail
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    if (error == nil) {
        self.gamesArr = [NSMutableArray arrayWithCapacity:[appList count]];
        for (AppDescriptionInfo *info in appList) {
            [self.gamesArr addObject:info];
        }
        
        [self addHeaderWithDetail:projectDetail];
        [self.topic_table clearDataAndReload];
        
        self.topicTitle = projectDetail.title;
    }
}

@end
