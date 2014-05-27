    //
//  ActivityCommonCtrl.m
//  GameCenter91
//
//  Created by  hiyo on 12-8-27.
//  Copyright 2012 Nd. All rights reserved.
//

#import "ActivityCommonCtrl.h"
#import "UITableViewCell+Addition.h"
#import "UIViewController+Extent.h"
#import "UIImageView+WebCache.h"
#import "NSDate+Utilities.h"
#import "MBProgressHUD.h"
#import "ActivityDetailCtrl.h"

#import <NdComPlatform/NdComPlatformAPIResponse.h>
#import <NdComPlatform/NdCPNotifications.h>
#import <NdComPlatform/NdComPlatform.h>
#import "GameCenterOperation.h"
#import "ActivityInfo.h"
#import "GiftItem.h"
#import "AdsInfoCache.h"
#import "UserData.h"
#import "DatabaseUtility.h"
#import "UIButton+WebCache.h"
#import "GameDetailController.h"
#import "CommUtility.h"
#import "TabContainerController.h"
#import "Notifications.h"
#import "GameDetailWebCtrl.h"
#import "GameDetailController.h"
#import "ActivityNewServersNoticeCell.h"
#import "ActivityNewServersNoticeTitleCell.h"
#import "DownloadButtonBar.h"
#import "GcPagination.h"
#import "CustomPageControlView.h"

#import "NSArray+Extent.h"
#import "Colors.h"
#import "AssigningControlBackgroundView.h"
#import "ColorfulImage.h"
#import "ReportCenter.h"

#define BANNER_MARGIN           5.0f
#define BANNER_WIDTH_DEFAULT    320.0f-BANNER_MARGIN*2
#define BANNER_HEIGHT_DEFAULT   85.0f

#define GATABLE_PAGE_SIZE       (self.act_type==ACT_NEW_SERVERS_NOTICE)?999:10

#define Base_AD_Btn_Tag     100

#pragma mark -
@interface ActivityCommonCtrl() <UIScrollViewDelegate>
@property (nonatomic, assign) BOOL bAppear;
@property (nonatomic, retain) NSString *errorTitle;
@property (nonatomic, retain) NSString *errorContent;
@property (nonatomic, retain) NSArray *adInfos;
@property (nonatomic, retain) UIScrollView *adsScrollView;

@property (nonatomic, retain) NSTimer *entertainingDiversionsTimer;
@property (nonatomic, assign) id oneself;
@property (nonatomic, assign) NSInteger currentEntertainingDiversionsPage;
@property (nonatomic, retain) CustomPageControlView *adsPageControl;

@property (nonatomic, retain) UIView *notLoginedPromptView;

- (void)initOtherProperties;
- (void)addAdBannerToTable:(UITableView *)table;
- (void)adAction:(id)sender;
- (void)showCell:(ActivityTableViewCell *)cell withMyGiftInfo:(GiftItem *)info;
- (void)showCell:(ActivityTableViewCell *)cell withActivityInfo:(ActivityInfo *)info;
- (void)showNewServersNoticeCell:(ActivityNewServersNoticeCell *)cell withActivityInfo:(ActivityInfo *)info;
- (NSString *)activityCtrlTitle:(id)data;
- (void)addLoginRelatedObserve;
@end


//#pragma mark -
@implementation ActivityCommonCtrl 
@synthesize act_table, act_type;
@synthesize bHaveHeader, bNeedShowIcon;
@synthesize identifier;
@synthesize tableStyle;
@synthesize bAppear, errorTitle, errorContent, adInfos;

- (void)dealloc {
	self.act_table = nil;
    self.errorTitle = nil;
    self.errorContent = nil;
    self.adInfos = nil;
    self.adsScrollView = nil;
    self.adsPageControl = nil;
    self.notLoginedPromptView = nil;
    [[RequestorAssistant sharedInstance] cancelAllOperationOfRequestor:self];
    
    [super dealloc];
}

- (id)init
{
	self = [super init];
	if (self) {
		self.act_table = nil;
		self.act_type = ACT_OTHER;
        self.identifier = nil;//默认为空，表示所有游戏
        
		self.bHaveHeader = NO;
        self.bNeedShowIcon = YES;
        self.tableStyle = UITableViewStylePlain;
        
        self.bAppear = NO;
        self.errorTitle = nil;
        self.errorContent = nil;
        self.adInfos = nil;
        
        self.oneself = self;
	}
	return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    CGFloat otherHeight = (self.parentContainerController == nil) ? 0 : [TabContainerController defaultSegmentHeight];
    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:!(self.act_type == ACT_OTHER) otherExcludeHeight:otherHeight];

	self.act_table = [[[GcPageTable alloc] initWithFrame:CGRectMake(0, 0, 320.0, height) style:self.tableStyle] autorelease];
    self.act_table.userInteractionEnabled = YES;
	act_table.strMoreTip = @"更多记录⋯⋯";
    switch (self.act_type) {
        case ACT_MY_GIFTS:
            act_table.strNoDataTip = @"亲，您还没有领取礼包哦。91游戏中心提供宇宙最强大的游戏礼包。赶紧去领一个吧。";
            [self addLoginRelatedObserve];
			break;
		case ACT_GAME_GIFTS:
            //act_table.strNoDataTip = @"当前没有游戏礼包哦～";
            [self addLoginRelatedObserve];
			break;
		case ACT_ACTIVITY_NOTICE:
            //act_table.strNoDataTip = @"当前没有活动公告哦～";
			break;
		default:
            [self addLoginRelatedObserve];
			break;
    }
	act_table.rowHeight = 80;
	act_table.pageTableDelegate = self;
	act_table.customCellCache = [ActivityTableViewCell loadFromNib];
//	[act_table setNdPageTableTransparent];
	[act_table setPageSize:GATABLE_PAGE_SIZE pageCountOnce:1];
	[act_table enableEgoRefreshTableHeaderView];
    if (self.act_type == ACT_NEW_SERVERS_NOTICE) {
        act_table.titleCellCache = [ActivityNewServersNoticeTitleCell loadFromNib];
        act_table.titleRowHeight = [ActivityNewServersNoticeTitleCell cellHeight];
    }
    
//    if (bHaveHeader) {
//        [self addAdBannerToTable:act_table];
//    }

	[self.view addSubview:act_table];
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self addNotLoginPromptViewToTable];
    
    [self assignViewFrame];
    
    if (bHaveHeader) {
        [self addAdBannerToTable:act_table];
        
        [self startEntertainingDiversionsTimer];
    }
    
    [self.act_table reloadData];

    //show error
    self.bAppear = YES;
    if (self.errorTitle || self.errorContent) {
        [MBProgressHUD showHintHUD:errorTitle message:errorContent hideAfter:DEFAULT_TIP_LAST_TIME];
        self.errorTitle = nil;
        self.errorContent = nil;
    }
    
    switch (self.act_type) {
            //        case ACT_OTHER:
            //            [NdAnalytics event:ANALYTICS_EVENT_3005 label:[NSString stringWithFormat:@"%d", self.appId]];
            //            break;
        case ACT_MY_GIFTS:
            break;
        case ACT_GAME_GIFTS:
            break;
        case ACT_ACTIVITY_NOTICE:
            break;
        case ACT_NEW_SERVERS_NOTICE:
            break;
        default:
            break;
    }
    
    //自动隐藏标题栏，以免两排菜单显得不协调
    if (self.act_type == ACTIVITY_TYPE_NEW_SERVERS_NOTICE) {
//        [self performSelector:@selector(hideNewServersNoticeTableTitle:) withObject:act_table afterDelay:3.0f];
    }
    
    if (self.act_type == ACT_MY_GIFTS) {
        if (![[NdComPlatform defaultPlatform] isLogined]) {
            self.notLoginedPromptView.hidden = NO;
            self.act_table.hidden = YES;
        } else {
            self.notLoginedPromptView.hidden = YES;
            self.act_table.hidden = NO;
        }
    }
}

- (void)assignViewFrame
{
    CGFloat otherHeight = (self.parentContainerController == nil) ? 0 : [TabContainerController defaultSegmentHeight];
    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:!(self.act_type == ACT_OTHER) otherExcludeHeight:otherHeight];
    
    self.act_table.frame = CGRectMake(0, 0, 320.0, height);
}

- (void)viewDidDisappear:(BOOL)animated
{
    if (self.act_type != ACT_OTHER) {
//        self.act_table.tableHeaderView = nil;
    }
    self.bAppear = NO;
    
    [self stopEntertainingDiversionsTimer];
}


/*
 // Override to allow orientations other than the default portrait orientation.
 - (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
 // Return YES for supported orientations
 return (interfaceOrientation == UIInterfaceOrientationPortrait);
 }
 */

- (void)updateGameActivityBannerWithUrl:(NSString *)urlstr
{
    if ([urlstr length] > 0) {
        float bannerWidth = BANNER_WIDTH_DEFAULT;
        float bannerHeight = BANNER_HEIGHT_DEFAULT;
        UIImageView *imgView = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_top_half.png"]] autorelease];
        imgView.frame = CGRectMake(0, 0, 320.0, bannerHeight + BANNER_MARGIN*2);
        UIImageView *innerView = [[[UIImageView alloc] init] autorelease];
        [innerView setImageWithURL:[NSURL URLWithString:urlstr] placeholderImage:[UIImage imageNamed:@"ad_banner_default.jpg"]];
        innerView.frame = CGRectMake(BANNER_MARGIN, BANNER_MARGIN, bannerWidth, bannerHeight);
        [imgView addSubview:innerView];
        
        act_table.tableHeaderView = imgView;
    }
    else {
        act_table.tableHeaderView = nil;
    }
}

#pragma mark -

- (void)startEntertainingDiversionsTimer
{
    if (self.entertainingDiversionsTimer == nil) {
        self.entertainingDiversionsTimer = [NSTimer scheduledTimerWithTimeInterval:5.0f
                                                                            target:self.oneself
                                                                          selector:@selector(showEntertainingDiversions)
                                                                          userInfo:nil
                                                                           repeats:YES];
    }
}

- (void)showEntertainingDiversions
{
    NSInteger totalAdsCount = MIN([self.adInfos count], 5);
    if (self.currentEntertainingDiversionsPage == (totalAdsCount - 1)) {
        self.currentEntertainingDiversionsPage = 0;
    } else {
        self.currentEntertainingDiversionsPage++;
    }
    [self.adsScrollView setContentOffset:CGPointMake(320 * self.currentEntertainingDiversionsPage, 0) animated:YES];
}

- (void)stopEntertainingDiversionsTimer
{
    if (self.entertainingDiversionsTimer != nil) {
        [self.entertainingDiversionsTimer invalidate];
        self.entertainingDiversionsTimer = nil;
    }
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    
    [self stopEntertainingDiversionsTimer];
    [self startEntertainingDiversionsTimer];
    
    CGFloat pageWidth = self.adsScrollView.frame.size.width;
    float fractionalPage = self.adsScrollView.contentOffset.x / pageWidth;
    NSInteger page = lround(fractionalPage);
    self.currentEntertainingDiversionsPage = page;
    [self.adsPageControl resetCustomStarsViewWithCurrentPageNumber:self.currentEntertainingDiversionsPage];
}

#pragma mark -
- (void)setAct_type:(int)type
{
	act_type = type;
	
	[self initOtherProperties];
}

- (void)initOtherProperties
{
	switch (self.act_type) {
        case ACT_MY_GIFTS:
            self.title = @"我的礼包";
			break;
		case ACT_GAME_GIFTS:
            self.title = @"游戏礼包";
            self.bHaveHeader = YES;
			break;
		case ACT_ACTIVITY_NOTICE:
            self.title = @"活动公告";
            self.bHaveHeader = YES;
			break;
        case ACT_NEW_SERVERS_NOTICE:
            self.title = @"新服预告";
			break;
		default:
			break;
    }
}

- (void)addAdBannerToTable:(UITableView *)table
{
    int positon = self.act_type+3;

    float bannerWidth = BANNER_WIDTH_DEFAULT;
    float bannerHeight = 0.0f;
    float adHeight = BANNER_HEIGHT_DEFAULT;
        
    //ad
    self.adInfos = [DatabaseUtility cachedAdsForPosition:positon];
    NSInteger totalAdsCount = MIN([self.adInfos count], 5);
    if ([self.adInfos count] != 0) {
        bannerHeight = BANNER_HEIGHT_DEFAULT + BANNER_MARGIN*2;
        self.adsScrollView = [[[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, bannerHeight)] autorelease];
        self.adsScrollView.contentSize = CGSizeMake(320 * totalAdsCount, bannerHeight);
        self.adsScrollView.pagingEnabled = YES;
        self.adsScrollView.delegate = self;
        
        for (NSInteger index = 0; index < totalAdsCount; index++) {
            AdsBriefInfo *info = [self.adInfos objectAtIndex:index];
            if (info != nil) {
                UIImageView *imgView = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_top_half.png"]] autorelease];
                imgView.frame = CGRectMake(320 * index, 0, 320.0, bannerHeight);
                imgView.userInteractionEnabled = YES;                
                
                UIImageView *btnView = [[[UIImageView alloc] initWithFrame:CGRectMake(BANNER_MARGIN, BANNER_MARGIN, bannerWidth, adHeight)] autorelease];
                
                [btnView setImageWithURL:[NSURL URLWithString:info.imageUrl] placeholderImage:[UIImage imageNamed:@"ad_banner_default.jpg"]];
                [imgView addSubview:btnView];
                
                UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
                btn.tag = index + Base_AD_Btn_Tag;
                btn.frame = CGRectMake(320/2-bannerWidth/2, BANNER_MARGIN, 320.0, adHeight);
                [btn addTarget:self action:@selector(adAction:) forControlEvents:UIControlEventTouchUpInside];
                [imgView addSubview:btn];
                
                switch (info.actionType) {
                    case ADS_TARGET_NONE:
                        btn.userInteractionEnabled = NO;
                        break;
                    case ADS_TARGET_LINK:
                        btn.userInteractionEnabled = YES;
                        break;
                    case ADS_TARGET_GAME:
                        btn.userInteractionEnabled = YES;
                        break;
                    default:
                        break;
                }
                [self.adsScrollView addSubview:imgView];
            }
        }
    }
    
    self.adsScrollView.showsHorizontalScrollIndicator = NO;
    table.tableHeaderView = self.adsScrollView;
    self.currentEntertainingDiversionsPage = -1;
    
    [self addAdsPageControl];
    
    [self showEntertainingDiversions];
}

- (void)addAdsPageControl
{
    [self.adsPageControl removeFromSuperview];
    
    float adHeight = BANNER_HEIGHT_DEFAULT;
    NSInteger totalAdsCount = MIN([self.adInfos count], 5);
    if (totalAdsCount <= 0) {
        return;
    }
    self.adsPageControl = [CustomPageControlView customPageControlViewWithTotalNumber:totalAdsCount];
    self.adsPageControl.frame = CGRectMake(0, adHeight - 20, 320.0, 20);
    [self.adsPageControl resetCustomStarsViewWithCurrentPageNumber:0];
    [self.act_table addSubview:self.adsPageControl];
}

- (void)adAction:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    int index = btn.tag - Base_AD_Btn_Tag;
    if (index >= [self.adInfos count]) {
        return;
    }
    AdsBriefInfo *info = [self.adInfos valueAtIndex:index];
    switch (info.actionType) {
        case ADS_TARGET_LINK:
        {
			GameDetailWebCtrl *ctrl = [[[GameDetailWebCtrl alloc] init] autorelease];
            ctrl.hidesBottomBarWhenPushed = YES;
            [self.parentContainerController.navigationController pushViewController:ctrl animated:YES];
            [ctrl loadWebView:info.actionParam];
		}
            break;
        case ADS_TARGET_GAME:
        {
            GameDetailController  *ctrl = [GameDetailController gameDetailWithIdentifier:info.actionParam gameName:nil];
            ctrl.hidesBottomBarWhenPushed = YES;
            [self.parentContainerController.navigationController pushViewController:ctrl animated:YES];
            
            //统计
            if (self.act_type == ACT_GAME_GIFTS) {
                [ReportCenter report:ANALYTICS_EVENT_15056 label:info.actionParam downloadFromNum:ANALYTICS_EVENT_15098];
            }
            else if (self.act_type == ACT_ACTIVITY_NOTICE) {
                [ReportCenter report:ANALYTICS_EVENT_15058 label:info.actionParam downloadFromNum:ANALYTICS_EVENT_15100];
            }
        }
            break;
        default:
            break;
    }
}

- (void)showCell:(ActivityTableViewCell *)cell withMyGiftInfo:(GiftItem *)info
{
    [cell setCellInfo:self.act_type withMyGiftInfo:info];
}

- (void)showCell:(ActivityTableViewCell *)cell withActivityInfo:(ActivityInfo *)info
{
    [cell setCellInfo:self.act_type withActivityInfo:info];
}

- (void)showNewServersNoticeCell:(ActivityNewServersNoticeCell *)cell withActivityInfo:(ActivityInfo *)info
{
    [cell setCellInfo:self.act_type withActivityInfo:info];
}

- (NSString *)activityCtrlTitle:(id)data
{
    NSString *ctrlTitle = nil;
    int type = 0;
    if ([data respondsToSelector:NSSelectorFromString(@"activityType")] == NO) {
        type = ACT_MY_GIFTS;
    }
    else {
        type = ((ActivityInfo *)data).activityType;
    }
    switch (type) {
        case ACT_MY_GIFTS:
			ctrlTitle = @"礼包";
			break;
		case ACTIVITY_TYPE_GAME_GIFT:
			ctrlTitle = @"礼包";
			break;
		case ACTIVITY_TYPE_ACTIVITY_NOTICE:
			ctrlTitle = ([[(ActivityInfo *)data title] length] > 0) ? [(ActivityInfo *)data title] : @"活动详情";
			break;
        case ACTIVITY_TYPE_PRIZE_NOTICE:
            ctrlTitle = @"获奖公告";
            break;
        case ACTIVITY_TYPE_NEW_SERVERS_NOTICE:
            ctrlTitle = @"新服预告";
            break;
		default:
			break;
    }
    return ctrlTitle;
}

- (void)addLoginRelatedObserve
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(loginFinished:) name:kNdCPLoginNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(loginFinished:) name:kNdCPSessionInvalidNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(getCodeSuccess:) name:kGC91GetCodeSuccessNotification object:nil];
}

- (void)getCodeSuccess:(NSNotification *)aNotify
{
    [self.act_table clearDataAndReload];
}

- (void)loginFinished:(NSNotification *)aNotification
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    [self.act_table clearDataAndReload];
    
    if (self.act_type == ACT_MY_GIFTS) {
        if (![[NdComPlatform defaultPlatform] isLogined]) {
            self.notLoginedPromptView.hidden = NO;
            self.act_table.hidden = YES;
        } else {
            self.notLoginedPromptView.hidden = YES;
            self.act_table.hidden = NO;
        }
    }
}

- (void)delayCustomDidDownloadWhenError
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    [act_table didDownloadPage:0 
                    totalCount:0 
                     dataArray:nil
                       success:NO];
}

- (void)hideNewServersNoticeTableTitle:(UITableView*)tableView {
    int rowNumber = [tableView numberOfRowsInSection:0];
    if (rowNumber <= 0) {
        return;
    }
    
    if (tableView.contentOffset.y < [ActivityNewServersNoticeTitleCell cellHeight]) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:1 inSection:0];
        [tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionTop animated:YES];
    }
}

#pragma mark -
- (void)GcPageTable:(GcPageTable*)table customCell:(UITableViewCell*)cell customData:(id)data
{
    if (act_type == ACT_MY_GIFTS) {
        ActivityTableViewCell* aCell = (ActivityTableViewCell*)cell;
        [self showCell:aCell withMyGiftInfo:data];
    }
    else if (self.act_type == ACTIVITY_TYPE_NEW_SERVERS_NOTICE) {
        ActivityNewServersNoticeCell *aCell = (ActivityNewServersNoticeCell*)cell;
        [self showNewServersNoticeCell:aCell withActivityInfo:data];
    }
    else {
        ActivityTableViewCell* aCell = (ActivityTableViewCell*)cell;
        [self showCell:aCell withActivityInfo:data];
        
        //游戏活动页不需要显示icon，因为所有icon都一样
        if (!self.bNeedShowIcon) {
            [aCell cellAdjustForNoneIconStyle];
        }
    }
    
    [AssigningControlBackgroundView assignCellSelectedBackgroundView:cell];
}

- (CGFloat)	GcPageTable:(GcPageTable*)table heightForCustomCell:(UITableViewCell*)cellCache customData:(id)data
{
    if (self.act_type == ACTIVITY_TYPE_NEW_SERVERS_NOTICE) {
        return [ActivityNewServersNoticeCell cellHeight];
    }
    else {
        return [ActivityTableViewCell cellHeight];
    }
}

- (int)GcPageTable:(GcPageTable*)table downloadPageIndex:(NSInteger)pageIdx  pageSize:(NSInteger)pageSize
{
	GcPagination* pg = [[GcPagination new] autorelease];
	pg.pageIndex = pageIdx + 1;
	pg.pageSize = pageSize;
	NSNumber *ret = [NSNumber numberWithInt:-1];
    [MBProgressHUD showHUDAddedTo:self.view animated:YES]; 
    if (self.act_type == ACT_MY_GIFTS) {
        if ([[NdComPlatform defaultPlatform] isLogined]) {
            ret = [RequestorAssistant requestMyActivityGiftList:pg delegate:self];
        }
    }
    else {
        ret = [RequestorAssistant requestGetActivityList:self.identifier type:self.act_type page:pg keyword:nil delegate:self];
    }
    if ([ret intValue] < 0) {
        [self performSelector:@selector(delayCustomDidDownloadWhenError) withObject:nil afterDelay:0.5];
    }
  
	return [ret intValue];
}

- (UITableViewCell*)GcPageTable:(GcPageTable*)table cellCopyFromCacheCell:(UITableViewCell*)cellCache
{
	ActivityTableViewCell* cellNew = [ActivityTableViewCell loadFromNib];
	return cellNew;
}

- (UITableViewCell*)GcPageTable:(GcPageTable*)table cellCopyByCustomData:(id)data
{
    if (act_type == ACTIVITY_TYPE_NEW_SERVERS_NOTICE) {
        return [ActivityNewServersNoticeCell loadFromNib];
    }
    else {
        return [ActivityTableViewCell loadFromNib];
    }
}

- (NSString*)GcPageTable:(GcPageTable*)table cellIdentifierBycustomData:(id)data
{
    if (act_type == ACTIVITY_TYPE_NEW_SERVERS_NOTICE) {
        return [ActivityNewServersNoticeCell cellReuseIdentifier];
    }
    else {
        return [ActivityTableViewCell cellReuseIdentifier];
    }
}

- (void) GcPageTable:(GcPageTable*)table didSelectRowWithData:(id)data 
{
	//跳转到活动详情
	ActivityDetailCtrl  *ctrl = [[ActivityDetailCtrl alloc] init];
    ctrl.customTitle = [self activityCtrlTitle:data];
    ctrl.appIdentifier = [data identifier];
    ctrl.contentUrl = [data contentUrl];
    ctrl.activityId = [data activityID];
	ctrl.hidesBottomBarWhenPushed = YES;
    if (self.parentContainerController) {
        [self.parentContainerController.navigationController pushViewController:ctrl animated:YES];
    }else
    {
        [self.navigationController pushViewController:ctrl animated:YES];
    }
    [self.act_table deselectRowAtIndexPath:self.act_table.indexPathForSelectedRow animated:YES];
	[ctrl release];
    
    if (self.act_type == ACTIVITY_TYPE_NEW_SERVERS_NOTICE) {
    }
    
    //统计
    if (self.act_type == ACT_GAME_GIFTS) {
        [ReportCenter report:ANALYTICS_EVENT_15055 label:[data identifier] downloadFromNum:ANALYTICS_EVENT_15097];
    }
    else if (self.act_type == ACT_ACTIVITY_NOTICE) {
        [ReportCenter report:ANALYTICS_EVENT_15057 label:[data identifier] downloadFromNum:ANALYTICS_EVENT_15099];
    }
    else if (self.act_type == ACT_NEW_SERVERS_NOTICE) {
        [ReportCenter report:ANALYTICS_EVENT_15059 label:[data identifier] downloadFromNum:ANALYTICS_EVENT_15101];
    }
}

#pragma mark GetMyActivityGiftListProtocol
- (void)operation:(GameCenterOperation *)operation getMyActivityGiftListDidFinish:(NSError *)error giftList:(NSArray *)giftList page:(GcPagination *)page totalCount:(int)totalCount
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    [act_table didDownloadPage:page.pageIndex-1 
                    totalCount:totalCount 
                     dataArray:giftList
                       success:operation.errorMessage == nil];
    if (error) {
        if (self.bAppear) {
            [MBProgressHUD showHintHUD:@"获取我的礼包列表失败" message:[error localizedDescription] hideAfter:DEFAULT_TIP_LAST_TIME];
        }
        else {
            self.errorTitle = @"获取我的礼包列表失败";
            self.errorContent = [error localizedDescription];
        }
    }
}

#pragma mark GetActivityListProtocol
- (void)operation:(GameCenterOperation *)operation getActivityListDidFinish:(NSError *)error activityList:(NSArray *)activityList page:(GcPagination *)page totalCount:(int)totalCount
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
        
    //开服提醒要过滤掉appName为空
    NSMutableArray *recordArray = nil;
    if (self.act_type == ACTIVITY_TYPE_NEW_SERVERS_NOTICE && error == nil) {
        recordArray = [NSMutableArray array];
        for (ActivityInfo *info in activityList) {
            if ([info.appName length] > 0) {
                [recordArray addObject:info];
            }
        }
    }
    else {
        recordArray = [NSMutableArray arrayWithArray:activityList];
    }
    
    [act_table didDownloadPage:page.pageIndex-1 
                    totalCount:totalCount 
                     dataArray:recordArray
                       success:operation.errorMessage == nil];
    if (error) {
        if (self.bAppear) {
            [MBProgressHUD showHintHUD:@"获取活动列表失败" message:[error localizedDescription] hideAfter:DEFAULT_TIP_LAST_TIME];
        }
        else {
            self.errorTitle = @"获取活动列表失败";
            self.errorContent = [error localizedDescription];
        }
    }
    
//    //更新游戏详情活动页的badge
//    if (self.act_type == ACT_OTHER && totalCount >= 0) {
//        GameDetailController *parentCtrl = (GameDetailController *)self.parentContainerController;
//        [parentCtrl.seg setBadgeNum:totalCount atIndex:1 bAutoHideWhenZero:NO];
//    }
}

- (void)addNotLoginPromptViewToTable
{
    if (self.notLoginedPromptView != nil) {
        return;
    }
    
//    CGFloat otherHeight = (self.parentContainerController == nil) ? 0 : [TabContainerController defaultSegmentHeight];
//    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:!(self.act_type == ACT_OTHER) otherExcludeHeight:otherHeight];
    
    //    UIImageView *icon = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"errorIcon.png"]] autorelease];
    ////    icon.frame = CGRectMake(80, 40, 160, 147);
    //    icon.center = CGPointMake(CGRectGetWidth(self.notLoginedPromptView.frame)/2, CGRectGetHeight(self.notLoginedPromptView.frame)*0.382);
    //    [self.notLoginedPromptView addSubview:icon];
    //
    //    UILabel *label = [[[UILabel alloc] initWithFrame:CGRectMake(0, 187, 320, 60)] autorelease];
    //
    //
    ////    CGRect rc = CGRectMake(0, 0, CGRectGetWidth(self.notLoginedPromptView.frame), fHeight);
    ////    label.numberOfLines = 0;
    ////    label.text = @"亲，查看礼包需要先登录哟。";
    ////    label.textColor = [UIColor darkGrayColor];
    ////    label.backgroundColor = [UIColor clearColor];
    ////
    ////    float margin = 20;
    ////    float labelWidth = CGRectGetWidth(self.notLoginedPromptView.frame) - margin*2;
    ////    CGSize textSize = [label.text sizeWithFont:label.font constrainedToSize:CGSizeMake(labelWidth, 300)];
    ////    float originWithoutImageY = CGRectGetHeight(self.notLoginedPromptView.frame)*0.382;
    ////    rc.origin.x = CGRectGetWidth(self.notLoginedPromptView.frame)/2 - textSize.width/2;
    ////    rc.origin.y = (icon.image ? originWithoutImageY+icon.image.size.height/2 : originWithoutImageY);
    ////    rc.size.width = textSize.width;
    ////    rc.size.height = textSize.height;
    ////    label.frame = rc;
    //
    ////    label.backgroundColor = [UIColor clearColor];
    ////    label.textColor = [UIColor darkGrayColor];
    ////    label.numberOfLines = 0;
    ////    label.font = [UIFont systemFontOfSize:16];
    //
    ////    label.textAlignment = UITextAlignmentCenter;
    //    [self.notLoginedPromptView addSubview:label];
    
    self.notLoginedPromptView = [[UIView alloc] init];
//    self.notLoginedPromptView.frame = CGRectMake(0, 0, 320, 420);
    self.notLoginedPromptView.frame = self.act_table.frame;
    CGRect frame = self.notLoginedPromptView.frame;
    self.notLoginedPromptView.backgroundColor = [UIColor clearColor];
    self.notLoginedPromptView.userInteractionEnabled = YES;
    
    CGFloat fHeight = frame.size.height;
    
    UIImage *image = [UIImage imageNamed:@"errorIcon.png"];
    UIImageView *imageView = [[[UIImageView alloc] initWithImage:image] autorelease];
    imageView.center = CGPointMake(CGRectGetWidth(frame)/2, CGRectGetHeight(frame)*0.382);
    [self.notLoginedPromptView addSubview:imageView];
    
    NSString *text = @"亲，查看礼包需要先登录哟。";
    UILabel *label = [[UILabel new] autorelease];
    label.numberOfLines = 0;
    label.text = text;
    label.textColor = [UIColor darkGrayColor];
    label.backgroundColor = [UIColor clearColor];
    
    CGRect lableFrame = CGRectMake(0, 0, CGRectGetWidth(self.act_table.frame), fHeight);
    
    float margin = 20;
    float labelWidth = CGRectGetWidth(frame) - margin*2;
    CGSize textSize = [text sizeWithFont:label.font constrainedToSize:CGSizeMake(labelWidth, 300)];
    float originWithoutImageY = CGRectGetHeight(frame)*0.382;
    lableFrame.origin.x = CGRectGetWidth(frame)/2 - textSize.width/2;
    lableFrame.origin.y = (image ? originWithoutImageY+image.size.height/2 : originWithoutImageY);
    lableFrame.size.width = textSize.width;
    lableFrame.size.height = textSize.height;
    label.frame = lableFrame;
    
    [self.notLoginedPromptView addSubview:label];
    
    self.view.userInteractionEnabled = YES;
    UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    CGFloat offsety = lableFrame.origin.y + CGRectGetHeight(lableFrame) + 10;
    button.frame = CGRectMake(110, offsety, 100, 30);
    [button setTitle:@"登录" forState:UIControlStateNormal];
    [button setTitleColor:[CommUtility colorWithHexRGB:@"1788C6"] forState:UIControlStateNormal];
    button.titleLabel.font = [UIFont boldSystemFontOfSize:16];
    [button addTarget:self action:@selector(userLogin:) forControlEvents:UIControlEventTouchUpInside];
    UIImage *buttonSelectedPromptImage = [ColorfulImage imageWithColor:[CommUtility colorWithHexRGB:CELL_SELECTED_COLOR]];
    buttonSelectedPromptImage = [buttonSelectedPromptImage stretchableImageWithLeftCapWidth:buttonSelectedPromptImage.size.width/2 topCapHeight:buttonSelectedPromptImage.size.height/2];

    [button setBackgroundImage:buttonSelectedPromptImage forState:UIControlStateSelected];
    [button setBackgroundImage:buttonSelectedPromptImage forState:UIControlStateHighlighted];
    [self.notLoginedPromptView addSubview:button];
    
    [self.view addSubview:self.notLoginedPromptView];
    
    self.notLoginedPromptView.hidden = YES;
}

- (void)userLogin:(id)sender
{
    [[NdComPlatform defaultPlatform] NdLogin:0];
}

@end
