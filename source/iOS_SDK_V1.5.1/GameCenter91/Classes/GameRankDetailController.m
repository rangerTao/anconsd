//
//  GameRankDetailController.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/18/13.
//
//

#import "GameRankDetailController.h"
#import "CommUtility.h"
#import "TabContainerController.h"
#import "Notifications.h"
#import "GameTableViewCell.h"
#import "SoftManagementCenter.h"
#import "GameDetailController.h"
#import "SoftItem.h"
#import "AppDescriptionInfo.h"
#import "DatabaseUtility.h"
#import "UITableViewCell+Addition.h"
#import "UIViewController+Extent.h"
#import "UserData.h"
#import "GameCenterOperation.h"
#import "MBProgressHUD.h"
#import "GcPagination.h"
#import "GcPageTable.h"
#import "RequestorAssistant.h"
#import "ProgessButton.h"
#import "Colors.h"
#import "AssigningControlBackgroundView.h"
#import "ReportCenter.h"

#define GAME_RANK_DETAIL_TABLE_SINK_HEIGHT 30
#define GAME_RANK_DETAIL_TABLE_PAGE_SIZE 10

@interface GameRankDetailController () <GcPageTableDelegate, GetAppListProtocol>

@property (nonatomic, retain) NSMutableArray *gameRankDetailList;
@property (nonatomic,assign) BOOL isFirstLoading;
@property (nonatomic, assign) int delay_PageIdx;

@end

@implementation GameRankDetailController

- (void)dealloc {
	self.gameRankDetailTable = nil;
    self.gameRankDetailList = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kGC91DownloadQueueChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kGC91DownloadPercentChangeNotification object:nil];
    [super dealloc];
}

- (id)initWithType:(GAME_DETAIL_TYPE)type
{
	self = [super init];
	if (self) {
        self.gameRankDetailTable = nil;
		self.game_rank_type = type;
        self.gameRankDetailList = [NSMutableArray arrayWithCapacity:1];
        self.isFirstLoading = YES;
	}
	return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
	
	float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:YES otherExcludeHeight:[TabContainerController defaultSegmentHeight]+GAME_RANK_DETAIL_TABLE_SINK_HEIGHT];
    self.view.frame = CGRectMake(0, GAME_RANK_DETAIL_TABLE_SINK_HEIGHT, 320.0, height);
    
	self.gameRankDetailTable = [[[GcPageTable alloc] initWithFrame:CGRectMake(0, 0, 320.0, height) style:UITableViewStylePlain] autorelease];
    
    self.gameRankDetailTable.backgroundColor = [CommUtility colorWithHexRGB:BACKGROUND_COLOR];
    self.gameRankDetailTable.backgroundView = nil;
    
	self.gameRankDetailTable.rowHeight = 80;
	self.gameRankDetailTable.pageTableDelegate = self;
    
    self.gameRankDetailTable.customCellCache = [GameTableViewCell loadFromNib];
    
	[self.gameRankDetailTable setNdPageTableTransparent];
	[self.gameRankDetailTable setPageSize:GAME_RANK_DETAIL_TABLE_PAGE_SIZE pageCountOnce:1];
	[self.gameRankDetailTable enableEgoRefreshTableHeaderView];
    
    self.gameRankDetailTable.backgroundColor = [CommUtility colorWithHexRGB:BACKGROUND_COLOR];
    
    self.gameRankDetailTable.sectionIdxOfPageList = 0;
    
	[self.view addSubview:self.gameRankDetailTable];
    
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
    
    [self.gameRankDetailTable reloadData];
    
    switch (self.game_rank_type) {
		case GAME_DETAIL_NEW:
			break;
		default:
			break;
	}
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

#pragma mark -

- (void)downloadQueueChanged:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[[aNotify userInfo] objectForKey:@"ITEM"];
    int state = [[[aNotify userInfo] objectForKey:@"STATE"] intValue];
    if (state == ITEM_INSTALLED || state == ITEM_UNINSTALLED) {
        //安装和卸载成功
        [self.gameRankDetailTable clearDataAndReload];
        return;
    }
    BOOL bNeedReload = NO;
    
    if (item) {
        for (UITableViewCell *cell in self.gameRankDetailTable.visibleCells) {
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
        [self.gameRankDetailTable reloadData];
    }
}

- (void)downloadPercentChanged:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[aNotify object];
    if (item) {
        for (UITableViewCell *cell in self.gameRankDetailTable.visibleCells) {
            if ([cell isKindOfClass:[GameTableViewCell class]]) {
                GameTableViewCell *aCell = (GameTableViewCell *)cell;
                if ([aCell.gameStateButton.identifier isEqualToString:item.identifier]) {
                    [aCell updateBtnState:item];
                }
            }
        }
    }
}

#pragma mark -
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
    
    [AssigningControlBackgroundView assignCellSelectedBackgroundView:gameCell];
}

- (CGFloat)	GcPageTable:(GcPageTable*)table heightForCustomCell:(UITableViewCell*)cellCache  customData:(id)data
{
    return CGRectGetHeight(cellCache.frame);
}

- (int)GcPageTable:(GcPageTable*)table downloadPageIndex:(NSInteger)pageIdx  pageSize:(NSInteger)pageSize
{
    GcPagination* pg = [[[GcPagination alloc] init] autorelease];
	pg.pageIndex = pageIdx+1;
	pg.pageSize = 10;
    
    if (self.isFirstLoading == YES) {
        [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    }
    
    NSNumber *ret = [RequestorAssistant requestAppList:pg catagoryId:0 sortType:self.game_rank_type delegate:self];
    if ([ret intValue] < 0) {
        [MBProgressHUD hideHUDForView:self.view animated:YES];
        [MBProgressHUD showHintHUD:@"错误" message:[NSString stringWithFormat:@"%d", [ret intValue]] hideAfter:DEFAULT_TIP_LAST_TIME];
    }
    
    return [ret intValue];
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
	GameDetailController  *ctrl = [GameDetailController gameDetailWithIdentifier:info.identifier gameName:info.appName];
    ctrl.customTitle = [info.appName length] > 0 ? info.appName : @"游戏详情";
	ctrl.hidesBottomBarWhenPushed = YES;
    UIViewController *fatherController = self.fatherViewController;
    [fatherController.parentContainerController.navigationController pushViewController:ctrl animated:YES];
    [self.gameRankDetailTable deselectRowAtIndexPath:self.gameRankDetailTable.indexPathForSelectedRow animated:YES];
    
    //统计
    NSString *rankTitle = nil;
    switch (self.game_rank_type) {
        case GAME_DETAIL_NEW:
            rankTitle = @"最新";
            break;
        case GAME_DETAIL_HOT:
            rankTitle = @"最热";
            break;
        default:
            break;
    }
    if (rankTitle != nil) {
        [ReportCenter report:ANALYTICS_EVENT_15052 label:rankTitle];
    }
}

#pragma mark -
#pragma mark GetAppDetailListProtocol
- (void)operation:(GameCenterOperation *)operation getAppListDidFinish:(NSError *)error appList:(NSArray *)appList page:(GcPagination *)page isLastPage:(int)isLastPage
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    self.isFirstLoading = NO;
   
    int totalCount = 0;
    int tmpPage = page.pageIndex - 1;
    if (isLastPage == 1) {
        totalCount = tmpPage * GAME_RANK_DETAIL_TABLE_PAGE_SIZE;
        totalCount += [appList count];
    }
    else {
        if ([appList count] == GAME_RANK_DETAIL_TABLE_PAGE_SIZE) {
            totalCount = (tmpPage+1) * GAME_RANK_DETAIL_TABLE_PAGE_SIZE +2;
        }
        else {
            totalCount = tmpPage * GAME_RANK_DETAIL_TABLE_PAGE_SIZE;
            totalCount += [appList count];
        }
    }
    
    [self.gameRankDetailTable didDownloadPage:page.pageIndex-1
                                   totalCount:totalCount
                                    dataArray:appList
                                      success:YES];
}

@end
