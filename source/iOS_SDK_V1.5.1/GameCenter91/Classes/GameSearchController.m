    //
//  GameSearchController.m
//  GameCenter91
//
//  Created by  hiyo on 12-9-6.
//  Copyright 2012 Nd. All rights reserved.
//

#import "GameSearchController.h"
#import "GameTableViewCell.h"
#import "GameDetailController.h"
#import "UITableViewCell+Addition.h"
#import "UIViewController+Extent.h"
#import <NdComPlatform/NdComPlatformAPIResponse.h>
#import "MBProgressHUD.h"

#import "UserData.h"
#import "AppDescriptionInfo.h"

#import "SoftItem.h"
#import "SoftManagementCenter.h"
#import "UserData.h"
#import "Notifications.h"
#import "CommUtility.h"
#import "ProgessButton.h"
#import "GameHotSearchDisPlayView.h"
#import "RequestorAssistant.h"
#import "MIUtility.h"
#define HEIGHT_RECOMMEND    230.0f
#define HEIGHT_CHANGEBTN    30.0f
#define HEIGHT_SPACE        10.0f


#define GATABLE_PAGE_SIZE       10
#define RGB(r,g,b)          [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:1.0]

@interface GameSearchController()<GetGameSearchResultProtocol>
@property (nonatomic, retain) GameSearchBar *searchBar;
@property (nonatomic, assign) int delay_PageIdx;
@property (nonatomic, retain) NSMutableArray *delay_appList;
@property (nonatomic, retain) GameHotSearchDisPlayView *hotSearchDisplayView;

@end

@implementation GameSearchController
@synthesize table_result;
@synthesize delay_PageIdx, delay_appList;
@synthesize searchBar;
@synthesize hotSearchDisplayView;

- (id)init
{
	self = [super init];
	if (self) {
        self.customTitle = @"游戏搜索";
        
        self.table_result = nil;
        self.delay_appList = [NSMutableArray arrayWithCapacity:1];
	}
	return self;
}



- (void)viewDidLoad {
    [super viewDidLoad];
    
	self.hidesBottomBarWhenPushed = YES;
	self.searchBar = [GameSearchBar searchBar];
    searchBar.searchDelgate = self;
    float height_search = CGRectGetHeight(searchBar.frame);
    
    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:NO otherExcludeHeight:height_search];
	self.table_result = [[[GcPageTable alloc] initWithFrame:CGRectMake(0, height_search, 320.0, height) style:UITableViewStylePlain] autorelease];
	table_result.strMoreTip = @"更多记录⋯⋯";
	table_result.strNoDataTip = @"亲，没有搜到您要找的游戏呢。换一个关键字再试一试？支持拼音首字母查询的哦。";
	table_result.rowHeight = 80;
	table_result.pageTableDelegate = self;
	table_result.customCellCache = [GameTableViewCell loadFromNib];
	[table_result setNdPageTableTransparent];
	[table_result setPageSize:GATABLE_PAGE_SIZE pageCountOnce:1];
	[table_result enableEgoRefreshTableHeaderView];
    table_result.hidden = YES;
    
    self.hotSearchDisplayView = [[[GameHotSearchDisPlayView alloc] initWithFrame:CGRectMake(0, height_search, 320, height)]autorelease];
    self.hotSearchDisplayView.parentCtrl = self;
    [self.hotSearchDisplayView addSubViews];
    UITapGestureRecognizer *tapGr = [[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(viewTapped:)] autorelease];
    tapGr.cancelsTouchesInView = NO;
    [self.hotSearchDisplayView addGestureRecognizer:tapGr];
    [self.view addSubview:table_result];
    [self.view addSubview:self.hotSearchDisplayView];
    [self.view addSubview:searchBar];
    
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadQueueChanged:) name:kGC91DownloadQueueChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadPercentChanged:) name:kGC91DownloadPercentChangeNotification object:nil];
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
    
}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    if (table_result.hidden == NO && self.hotSearchDisplayView.hidden == YES) {
        [self.table_result reloadData];
    }
    else if (table_result.hidden == YES && self.hotSearchDisplayView.hidden == NO) {
        [self.hotSearchDisplayView updateContentView];
    }
    [searchBar hideKeyboard];
}


- (void)dealloc {
    self.hotSearchDisplayView = nil;
    self.table_result = nil;
    self.delay_appList = nil;
    [super dealloc];
}

#pragma mark -
- (void)hideKeyboard
{
	[searchBar hideKeyboard];
}
-(void)viewTapped:(UITapGestureRecognizer*)tapGr
{
    [self hideKeyboard];
}

- (void)downloadQueueChanged:(NSNotification *)aNotify
{
    BOOL bNeedReload = NO;
    SoftItem *item = (SoftItem *)[[aNotify userInfo] objectForKey:@"ITEM"];
    if (item) {
        for (UITableViewCell *cell in self.table_result.visibleCells) {
            if ([cell isKindOfClass:[GameTableViewCell class]]) {
                GameTableViewCell *aCell = (GameTableViewCell *)cell;
                if ([aCell.gameStateButton.identifier isEqualToString:item.identifier]) {
                    bNeedReload = YES;
                    break;
                }
            }
        }
    }
    
    if (bNeedReload && table_result.hidden == NO && self.hotSearchDisplayView.hidden == YES) {
        [self.table_result reloadData];
    }
}

- (void)downloadPercentChanged:(NSNotification *)aNotify
{
    if (table_result.hidden == NO) {
        SoftItem *item = (SoftItem *)[aNotify object];
        if (item) {
            for (UITableViewCell *cell in self.table_result.visibleCells) {
                if ([cell isKindOfClass:[GameTableViewCell class]]) {
                    GameTableViewCell *aCell = (GameTableViewCell *)cell;
                    if ([aCell.gameStateButton.identifier isEqualToString:item.identifier]) {
                        [aCell updateBtnState:item];
                    }
                }
            }
        }
    }
}

#pragma mark GameSearchBar Delegate
- (void)doSearchWithResult:(NSArray *)arr
{
    table_result.hidden = NO;
    self.hotSearchDisplayView.hidden = YES;
    
    [self.delay_appList removeAllObjects];
    [self.delay_appList addObjectsFromArray:arr];
    [table_result clearDataAndReload];
}

- (void)doSearchWithKeyword:(NSString *)aKeyword
{
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    self.searchBar.m_textField.text = aKeyword;
    self.searchBar.reset_btn.hidden = NO;
    NSNumber *ret = [RequestorAssistant requestGameSearchResultList:aKeyword delegate:self];
    if (ret < 0) {
        
    }
}

- (void)doDelete
{
    table_result.hidden = YES;
    if (!self.hotSearchDisplayView.bZeroHotList) {
        self.hotSearchDisplayView.hidden = NO;
    }
    [self.delay_appList removeAllObjects];
}

- (void)delayToDisplay
{
    NSRange range = NSMakeRange(delay_PageIdx*GATABLE_PAGE_SIZE, MIN(GATABLE_PAGE_SIZE, [delay_appList count]-delay_PageIdx*GATABLE_PAGE_SIZE));
    NSArray *subArr = [delay_appList subarrayWithRange:range];
    [table_result didDownloadPage:self.delay_PageIdx
                       totalCount:[delay_appList count]
                        dataArray:subArr
                          success:YES];
}

#pragma mark NdPageTableDelegate
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
    AppDescriptionInfo *info = (AppDescriptionInfo *)data;
    if ([info.labelIcons length] > 0) {
        return 96+5;
    }
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
    [self.table_result deselectRowAtIndexPath:self.table_result.indexPathForSelectedRow animated:YES];
}

#pragma mark - GetGameSearchResultProtocol
- (void)operation:(CommonOperation *)operation getGameSearchResultDidFinish:(NSError *)error resultList:(NSArray *)searchResultList
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    if (error == nil) {
        [self doSearchWithResult:searchResultList];

    }
}

@end
