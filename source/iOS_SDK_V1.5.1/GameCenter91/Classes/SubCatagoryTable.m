//
//  SubClassificationTable.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/12/13.
//
//

#import "SubCatagoryTable.h"
#import "CatagoryDetailCell.h"
#import "UITableViewCell+Addition.h"
#import "UIView+Addition.h"
#import "CommUtility.h"
#import "TabContainerController.h"
#import "SoftManagementCenter.h"
#import "Notifications.h"
#import "SoftItem.h"
#import "AppDescriptionInfo.h"
#import "UserData.h"
#import "GameDetailController.h"
#import "DatabaseUtility.h"
#import "UIViewController+Extent.h"
#import "MBProgressHUD.h"
#import <QuartzCore/QuartzCore.h>
#import "GameCenterOperation.h"
#import "GcPagination.h"
#import "RequestorAssistant.h"
#import "ProgessButton.h"
#import "LvPageTableView.h"
#import "Colors.h"
#import "CommUtility.h"
#import "AssigningControlBackgroundView.h"
#import "ReportCenter.h"

#define SUB_CATAGORY_TABLE_PAGE_SIZE       20

@interface SubCatagoryTable () <UITableViewDataSource, UITableViewDelegate, GetAppListProtocol, LvPageTableDelegate>

@property (nonatomic, assign) GAME_DETAIL_TYPE game_detail_type;
@property (nonatomic, assign) int catagoryId;
@property (nonatomic, assign) int sortType;

@property (nonatomic, retain) LvPageTableView *pgTableView;

@end

@implementation SubCatagoryTable

- (void)dealloc {
    self.pgTableView = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kGC91DownloadQueueChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kGC91DownloadPercentChangeNotification object:nil];
    [super dealloc];
}

- (id)initWithType:(GAME_DETAIL_TYPE)type andCatagoryId:(int)catagoryId
{
    self = [super init];
    if (self) {
        // Custom initialization
        self.game_detail_type = type;
        self.catagoryId = catagoryId;
        [self initOtherProperties];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
	// Do any additional setup after loading the view.
    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:NO otherExcludeHeight:[TabContainerController defaultSegmentHeight]];
    
    //multi column pageTableView
    self.pgTableView = [[[LvPageTableView alloc] initWithFrame:CGRectMake(0, 0, 320.0, height) style:UITableViewStylePlain] autorelease];
    self.pgTableView.selectedColor = [CommUtility colorWithHexRGB:CELL_SELECTED_COLOR];
    
    self.pgTableView.backgroundColor = [CommUtility colorWithHexRGB:BACKGROUND_COLOR];
    self.pgTableView.backgroundView = nil;

    self.pgTableView.pageSize = SUB_CATAGORY_TABLE_PAGE_SIZE;
    self.pgTableView.columnNum = 2;
    self.pgTableView.pageDelegate = self;
    [self.pgTableView launchRefreshing];
    [self.view addSubview:self.pgTableView];
	    
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
        
    switch (self.game_detail_type) {
        case GAME_DETAIL_NEW:
			break;
		case GAME_DETAIL_HOT:
			break;
		default:
			break;
	}
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)initOtherProperties
{
	switch (self.game_detail_type) {
		case GAME_DETAIL_NEW:
			self.title = @"最新";
            self.sortType = 1;
			break;
        case GAME_DETAIL_HOT:
            self.title = @"最热";
            self.sortType = 2;
            break;
		default:
			break;
	}

}

- (void)downloadQueueChanged:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[[aNotify userInfo] objectForKey:@"ITEM"];
    int state = [[[aNotify userInfo] objectForKey:@"STATE"] intValue];
    if (state == ITEM_INSTALLED || state == ITEM_UNINSTALLED) {
        //安装和卸载成功
        [self.pgTableView launchRefreshing];
        return;
    }
    BOOL bNeedReload = NO;
    
    if (item) {
        for (UITableViewCell *cell in self.pgTableView.visibleSubCells) {
            if ([cell isKindOfClass:[CatagoryDetailCell class]]) {
                CatagoryDetailCell *aCell = (CatagoryDetailCell *)cell;
                if ([aCell.gameStateButton.identifier isEqualToString:item.identifier]) {
                    bNeedReload = YES;
                    break;
                }
            }
        }
    }
    if (bNeedReload) {
        [self.pgTableView reloadData];
    }
}

- (void)downloadPercentChanged:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[aNotify object];
    if (item) {
        for (UITableViewCell *cell in self.pgTableView.visibleSubCells) {
            if ([cell isKindOfClass:[CatagoryDetailCell class]]) {
                CatagoryDetailCell *aCell = (CatagoryDetailCell *)cell;
                if ([aCell.gameStateButton.identifier isEqualToString:item.identifier]) {
                    [aCell updateBtnState:item];
                }
            }
        }
    }
}

#pragma mark - multi column pageTableView delegate
- (void)pageTable:(LvPageTableView *)pageTable downloadPageIndex:(NSInteger)pageIdx pageSize:(NSInteger)pageSize
{
    GcPagination* pg = [[[GcPagination alloc] init] autorelease];
	pg.pageIndex = pageIdx+1;
	pg.pageSize = SUB_CATAGORY_TABLE_PAGE_SIZE;
    NSNumber *ret = [RequestorAssistant requestAppList:pg catagoryId:self.catagoryId sortType:self.sortType delegate:self];
    if ([ret intValue] < 0) {
        [MBProgressHUD showHintHUD:@"错误" message:[NSString stringWithFormat:@"%d", [ret intValue]] hideAfter:DEFAULT_TIP_LAST_TIME];
    }
}
- (UITableViewCell *)pageTable:(LvPageTableView*)tableView cellForRowAtPoint:(CGPoint)point dataForCell:(id)data
{
    CatagoryDetailCell *cell = [CatagoryDetailCell loadFromNIB];
    
    //得到data
    AppDescriptionInfo *appInfo = (AppDescriptionInfo *)data;
    [cell.gameStateButton reset];
    [cell updateCellWithAppInfo:appInfo];
    
    return cell;
}
- (CGFloat)pageTable:(LvPageTableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [CatagoryDetailCell cellHeight];
}
- (void)pageTable:(LvPageTableView *)tableView didSelectRowAtPoint:(CGPoint)point dataForCell:(id)data
{
    //跳转到游戏详情
    AppDescriptionInfo *info = (AppDescriptionInfo *)data;
	GameDetailController  *ctrl = [GameDetailController gameDetailWithIdentifier:info.identifier gameName:info.appName];
	ctrl.hidesBottomBarWhenPushed = YES;
    [self.parentContainerController.navigationController pushViewController:ctrl animated:YES];
    
    //统计
    [ReportCenter report:ANALYTICS_EVENT_15051 label:self.customTitle];
}

#pragma mark -
#pragma mark GetCatagoryDetailAppListProtocol
- (void)operation:(GameCenterOperation *)operation getAppListDidFinish:(NSError *)error appList:(NSArray *)appList page:(GcPagination *)page isLastPage:(int)isLastPage
{
    int totalCount = 0;
    
    if (isLastPage == 1) {
        totalCount = (page.pageIndex-1) * SUB_CATAGORY_TABLE_PAGE_SIZE;
        totalCount += [appList count];
    }
    else {
        if ([appList count] == SUB_CATAGORY_TABLE_PAGE_SIZE) {
            totalCount = (page.pageIndex + 2) * SUB_CATAGORY_TABLE_PAGE_SIZE;
        }
        else {
            totalCount = (page.pageIndex-1) * SUB_CATAGORY_TABLE_PAGE_SIZE;
            totalCount += [appList count];
        }
    }
    
    [self.pgTableView didDownloadPage:page.pageIndex totalCount:totalCount pageArray:appList success:YES];
}

@end
