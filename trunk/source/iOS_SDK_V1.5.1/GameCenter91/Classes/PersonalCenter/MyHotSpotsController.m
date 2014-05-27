//
//  MyHotSpots.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/24/13.
//
//

#import "MyHotSpotsController.h"
#import "MyHotSpotCell.h"
#import "MyHotSpotsInfo.h"

#import "GameCenterOperation.h"
#import "MBProgressHUD.h"

#import "RequestorAssistant.h"

#import "UIViewController+Extent.h"

#import "ActivityDetailCtrl.h"

#import "UserData.h"

#import "NSArray+Extent.h"
#import "UITableViewCell+Addition.h"

#import "AssigningControlBackgroundView.h"
#import "GameDetailController.h"
#import "GameDetailWebCtrl.h"

#import "MyGameInfo.h"
#import "HomePageInfo.h"
#import "CommUtility.h"
@interface MyHotSpotsController () <GetMyHotSpotsProtocol>

@end

@implementation MyHotSpotsController

- (void)dealloc
{
    self.myHotSpotsList = nil;
    [super dealloc];
}

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
        self.myHotSpotsList = [NSMutableArray array];
        self.customTitle = @"我的热点";
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    UserData *user = [UserData sharedInstance];
    
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    //14-01-14 changed,我的热点更多 使用我的关注和未关注的软件标识符
    NSMutableArray *tmpArr = [NSMutableArray array];
    for (MyGameInfo *myGameInfo in user.homePageInfo.myGames) {
        [tmpArr addObject:myGameInfo.identifier];
    }
    NSNumber *ret = [RequestorAssistant requestMyHotSpots:tmpArr delegate:self];
//    NSNumber *ret = [RequestorAssistant requestMyHotSpots:user.myGameIdsList delegate:self];
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

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return [self.myHotSpotsList count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    MyHotSpotCell *myHotSpotCell = [MyHotSpotCell dequeOrCreateInTable:self.tableView];
    [myHotSpotCell refreshMyHotSpotCell:(MyHotSpotsInfo *)[self.myHotSpotsList valueAtIndex:indexPath.row]];
    myHotSpotCell.clipsToBounds = YES;
    [AssigningControlBackgroundView assignCellSelectedBackgroundView:myHotSpotCell];
    return myHotSpotCell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    MyHotSpotsInfo *myHotSpotsInfo = [self.myHotSpotsList valueAtIndex:indexPath.row];
    
    if (myHotSpotsInfo.hotType == MY_HOT_TYPE_APP_RECOMMEND) {
        
        if (myHotSpotsInfo.targetAction != nil) {
            GameDetailController *gameDetailController = [GameDetailController gameDetailWithIdentifier:myHotSpotsInfo.targetAction gameName:myHotSpotsInfo.title];
            gameDetailController.hidesBottomBarWhenPushed = YES;
            [self.navigationController pushViewController:gameDetailController animated:YES];
        }
    }
    else if (myHotSpotsInfo.hotType == MY_HOT_TYPE_STRATEGY) {
        if (myHotSpotsInfo.targetActionUrl != nil) {
            GameDetailWebCtrl *webViewCtr = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:myHotSpotsInfo.targetActionUrl];
            webViewCtr.customTitle = myHotSpotsInfo.title;
            webViewCtr.hidesBottomBarWhenPushed = YES;
            [self.navigationController pushViewController:webViewCtr animated:YES];
        }
    } else {
        
        NSArray *targetAction = [myHotSpotsInfo.targetAction componentsSeparatedByString:@","];
        
        if (myHotSpotsInfo.hotType == MY_HOT_TYPE_PLATFORM) {
            
            ActivityDetailCtrl *activityDetailCtrl = [[[ActivityDetailCtrl alloc] init] autorelease];
            activityDetailCtrl.customTitle = myHotSpotsInfo.title;
            activityDetailCtrl.contentUrl = myHotSpotsInfo.targetActionUrl;
            activityDetailCtrl.activityId = [[targetAction valueAtIndex:0] intValue];
            activityDetailCtrl.appIdentifier = nil;
            activityDetailCtrl.hidesBottomBarWhenPushed = YES;
            [self.navigationController pushViewController:activityDetailCtrl animated:YES];
            
        } else if ([targetAction count] == 2) {
            ActivityDetailCtrl *activityDetailCtrl = [[[ActivityDetailCtrl alloc] init] autorelease];
            activityDetailCtrl.customTitle = myHotSpotsInfo.title;
            
            activityDetailCtrl.contentUrl = myHotSpotsInfo.targetActionUrl;
            
            activityDetailCtrl.activityId = [[targetAction valueAtIndex:0] intValue];
            
            activityDetailCtrl.appIdentifier = [targetAction valueAtIndex:1];
            
            activityDetailCtrl.hidesBottomBarWhenPushed = YES;
            [self.navigationController pushViewController:activityDetailCtrl animated:YES];
        }
        
    }
    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 68;
}

#pragma mark -
#pragma mark GetMyHotSpotsProtocol
- (void)operation:(GameCenterOperation *)operation getMyHotSpotsDidFinish:(NSError *)error hotList:(NSArray *)hotList
{
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    if (error == nil) {
        self.myHotSpotsList = [NSMutableArray arrayWithArray:hotList];
        [self.tableView reloadData];
    }
}

@end
