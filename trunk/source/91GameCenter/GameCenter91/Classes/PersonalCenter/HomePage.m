//
//  HomePage.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/21/13.
//
//

#import "HomePage.h"
#import "SectionHeaderCell.h"
#import "MyHotSpotIconCell.h"
#import "HomePageMyHotSpotCell.h"
#import "MyGamesCell.h"
#import "MyGamesDefaultCell.h"
#import "MyGameButtonsCell.h"
#import "MyGameActivityCell.h"
#import "RecommendationDialyCell.h"
#import "NormalDayRecommendCell.h"

#import "HotInfo.h"
#import "MyGameInfo.h"
#import "ActiveInfo.h"
#import "DayRecommendInfo.h"
#import "AppInfo.h"

#import "MyHotSpotsController.h"
#import "EditingMyGamesController.h"

#import "GameCenterOperation.h"
#import "MBProgressHUD.h"
#import "RequestorAssistant.h"

#import "HomePageInfo.h"

#import "ProgessButton.h"

#import "ActivityDetailCtrl.h"
#import "GameDetailController.h"
#import "GameDetailWebCtrl.h"

#import "UIViewController+Extent.h"

#import <NdComPlatform/NDComPlatform.h>
#import <NdComPlatform/NdCPNotifications.h>
#import "Notifications.h"
#import "UserBasicInfomation.h"
#import "NdCPIconManager.h"
#import "UserData.h"
#import "UIBarButtonItem+Extent.h"

#import "KSTableProxy.h"
#import "UITableViewCell+Addition.h"

#import "UIImageView+WebCache.h"
#import "UIImage+Extent.h"
#import "CommUtility.h"
#import "NSArray+Extent.h"
#import "HomePageSectionType.h"
#import "Colors.h"
#import "AssigningControlBackgroundView.h"
#import "SoftManagementCenter.h"
#import "GameCenterAnalytics.h"
#import "ReportCenter.h"

#define MAX_ACTIVITIES_ROW_COUNT 5
#define LAST_DAY_RECOMMEND_ROW 3
#define BUTTON_TAG_OFFSET 21

#define MAX_MY_GAME_CELL_COUNT 9
#define MIN_MY_GAME_CELL_COUNT 3

#define MY_TOP_SPOT  101
#define MY_REST_SPOT 102
#define DAY_TOP_RECOMMEND 111
#define DAY_REST_RECOMMEND  112

@interface HomePage () <GetHomePageProtocol, GetUserBasicInfomationProtocol, NdIconObserverDelegate>

@property (nonatomic, retain) KSTableProxy *tableProxy;

@property (nonatomic, retain) UIImageView *userIconBackground;
@property (nonatomic, retain) UIImageView *userIcon;
@property (nonatomic, retain) UIButton *userIconButton;
@property (nonatomic, retain) UILabel *userName;

@property (nonatomic, retain) NSMutableArray *sectionHeaderTitles;
@property (nonatomic, retain) NSMutableArray *hotList;
@property (nonatomic, retain) NSMutableArray *myGames;
@property (nonatomic, retain) NSMutableArray *dayRecommendList;
@property (nonatomic, retain) NSArray *myGameIdsList;

@property (assign, nonatomic) NSInteger myGameCellCount;
@property (assign, nonatomic) NSInteger selectedButtonIndex;

@end

@implementation HomePage

- (void)dealloc
{
    self.tableProxy = nil;
    self.userIconBackground = nil;
    self.userIcon = nil;
    self.userIconButton = nil;
    self.userName = nil;
    self.sectionHeaderTitles = nil;
    self.hotList = nil;
    self.myGames = nil;
    self.dayRecommendList = nil;
    self.myGameIdsList = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[NdCPIconManager singleton] removeUserIconObserver:[NSString stringWithFormat:@"%d",[UserData sharedInstance].userInfo.uin] photoType:[NdCPIconManager userIconTypeForServerChecksum] observer:self];
    [super dealloc];
}

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
        self.sectionHeaderTitles = [NSMutableArray array];
        self.hotList = [NSMutableArray array];
        self.myGames = [NSMutableArray array];
        self.dayRecommendList = [NSMutableArray array];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(loginFinished:) name:kNdCPLoginNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(loginFinished:) name:kNdCPSessionInvalidNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshHomePage:) name:kGC91NeedRreshHomePage object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(becomeActive) name:UIApplicationWillEnterForegroundNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshUserInfoView:) name:kNdCPUserInfoDidChange object:nil];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.view.backgroundColor = [CommUtility colorWithHexRGB:@"F5F5F5"];
    [self assignUserInfomationView];

    // Uncomment the following line to preserve selection between presentations.
    
    self.tableView.clipsToBounds = YES;
    self.tableView.separatorColor = [UIColor clearColor];
    
    if (![[NdComPlatform defaultPlatform] isLogined]) {
        self.navigationItem.rightBarButtonItem = [UIBarButtonItem rightItemWithCustomStyle:@"登录" target:self action:@selector(userLogin)];
    }
    
    [self.tableView setBackgroundColor:[CommUtility colorWithHexRGB:@"D1D3D5"]];
    self.tableView.backgroundView = nil;
    
    [self updateDataAndView];
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (void)viewWillAppear:(BOOL)animated
{
    [self.tableProxy reload];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - observer action

- (void)loginFinished:(NSNotification *)aNotification
{
    NSDictionary *dict = [aNotification userInfo];
	BOOL success = [[dict objectForKey:@"result"] boolValue];
    
	if (success) {
        [RequestorAssistant requestUserBasicInfomation:self];
	}
    else {
        self.navigationItem.rightBarButtonItem = [UIBarButtonItem rightItemWithCustomStyle:@"登录" target:self action:@selector(userLogin)];
        self.navigationItem.leftBarButtonItem = nil;
    }
    
    [self refreshHomePage:nil];
}

- (void)refreshHomePage:(NSNotification *)aNotification
{
    UserData *user = [UserData sharedInstance];
    NSArray *newMyGameIdsList = [NSArray arrayWithArray:user.myGameIdsList];
    
    if ([self.myGameIdsList isEqualToArray:newMyGameIdsList]) {
        [self updateDataAndView];
        
        return;
    }
    
    NSNumber *ret = [RequestorAssistant requestHomePage:nil myGameIdentifiers:user.myGameIdsList delegate:self];
    if ([ret intValue] < 0) {
        [MBProgressHUD hideHUDForView:self.view animated:YES];
        [MBProgressHUD showHintHUD:@"错误" message:[NSString stringWithFormat:@"%d", [ret intValue]] hideAfter:DEFAULT_TIP_LAST_TIME];
    }
    else {
        [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    }
    
    self.selectedButtonIndex = 0;
}

- (void)becomeActive
{
    if ([[NdComPlatform defaultPlatform] isLogined] && [UserData sharedInstance].userInfo != nil) {
        [self fetchUserInformationButtonItems:[UserData sharedInstance].userInfo];
    }
    else {
        self.navigationItem.rightBarButtonItem = [UIBarButtonItem rightItemWithCustomStyle:@"登录" target:self action:@selector(userLogin)];
    }
}

- (void)refreshUserInfoView:(NSNotification *)aNotification
{
    self.userName.text = [[NdComPlatform defaultPlatform] nickName];
}

#pragma mark Section Header Button Action

- (void)showMoreMyHotSpots:(id)sender
{
    MyHotSpotsController *myHotSpotsController = [[[MyHotSpotsController alloc] init] autorelease];
    myHotSpotsController.hidesBottomBarWhenPushed = YES;
    
    [ReportCenter report:ANALYTICS_EVENT_15025];
    
    [self.navigationController pushViewController:myHotSpotsController animated:YES];
}

- (void)editMyGames:(id)sender
{
    EditingMyGamesController *editingMyGames = [[[EditingMyGamesController alloc] initWithStyle:UITableViewStyleGrouped] autorelease];
    editingMyGames.hidesBottomBarWhenPushed = YES;
    
    UserData *user = [UserData sharedInstance];
    HomePageInfo *homePageInfo = user.homePageInfo;
    editingMyGames.appList = [NSArray arrayWithArray:homePageInfo.appList];
    
    [ReportCenter report:ANALYTICS_EVENT_15032];
    
    [self.navigationController pushViewController:editingMyGames animated:YES];
}

- (void)showMyGameActivities:(id)sender
{
    UIButton *button = (UIButton *)sender;
    MyGameInfo *myGame = [self.myGames valueAtIndex:button.tag - BUTTON_TAG_OFFSET];
    if ([myGame.activeList count] > MAX_ACTIVITIES_ROW_COUNT) {
        self.myGameCellCount = MAX_MY_GAME_CELL_COUNT;
    } else {
        self.myGameCellCount = MIN_MY_GAME_CELL_COUNT + [myGame.activeList count];
    }
    
    self.selectedButtonIndex = button.tag - BUTTON_TAG_OFFSET;
    
    [self.tableProxy reload];
}

#pragma mark - others

- (void)updateDataAndView
{
    [self fetchData];
    [self fetchHomePageTableViewSections];
    
    [self.tableProxy reload];
}

- (void)assignUserInfomationView
{
    self.userIconBackground = [[[UIImageView alloc] initWithFrame:CGRectMake(15, 3, 39, 39)] autorelease];
    self.userIconBackground.image = [UIImage imageNamed:@"headBaseBoard.png"];
    self.userIconBackground.userInteractionEnabled = YES;
    
    self.userIcon = [[[UIImageView alloc] initWithFrame:CGRectMake(3, 3, 33, 33)] autorelease];
    self.userIcon.userInteractionEnabled = YES;
    self.userIcon.clipsToBounds = YES;
    
    self.userIconButton = [UIButton buttonWithType: UIButtonTypeCustom];
    self.userIconButton.frame = CGRectMake(0, 0, 39, 39);
    [self.userIconButton addTarget:self action:@selector(showPersonCenter:) forControlEvents:UIControlEventTouchUpInside];
    
    [self.userIconBackground addSubview:self.userIcon];
    [self.userIconBackground addSubview:self.userIconButton];
}

- (void)userLogin
{
    //    [self performSelector:@selector(showUserLoginPromptText) withObject:self afterDelay:0.3];
    [self showUserLoginPromptText]; //delay maybe cause a situation : show logining after login finished
    
    [[NdComPlatform defaultPlatform] NdLogin:0];
}

- (void)showUserLoginPromptText
{
    self.navigationItem.rightBarButtonItem = [UIBarButtonItem rightItemWithCustomStyle:@"登录中" target:nil action:NULL];
}

- (NSMutableArray *)filteredRecommmends:(NSArray *)recommendsArr
{
    NSMutableArray *filteredRecommends = [NSMutableArray array];
    UserData *userData = [UserData sharedInstance];
    for (DayRecommendInfo *info in recommendsArr) {
        if (![[userData allAppIdsList] containsObject:info.identifier]) {
            [filteredRecommends addObject:info];
            if ([filteredRecommends count] == 3) {
                break;
            }
        }
    }
    
    if ([filteredRecommends count] < 3) {
        for (DayRecommendInfo *info in recommendsArr) {
            if ([[userData allAppIdsList] containsObject:info.identifier]) {
                [filteredRecommends addObject:info];
                if ([filteredRecommends count] == 3) {
                    break;
                }
            }
        }
    }
    
    return filteredRecommends;
}

- (void)reloadTableWithProxy:(KSTableProxy *)proxy withSections:(NSArray *)sections
{
    self.tableProxy = proxy;
    [self.tableProxy becomeProxyForTable:self.tableView withSections:sections];
}

- (void)showPersonCenter:(id)sender
{
    [[NdComPlatform defaultPlatform] NdEnterPlatform:0];
}

- (void)updateIcon:(UIImage*)image checkSum:(NSString*)checkSum iconType:(NSUInteger)iconType  errorCode:(NSError*)error
{
	if (image) {
        self.userIcon.image = image;
	}
}

- (UITableViewCell *)dequeOrCreateCellWithIdentifier:(NSString *)identifier style:(UITableViewCellStyle)style
{
    UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
    if (cell == nil)
    {
        cell = [[[UITableViewCell alloc] initWithStyle:style reuseIdentifier:identifier] autorelease];
    }
    return cell;
}

- (UITableViewCell *)dequeOrCreateCellWithIdentifier:(NSString *)identifier nibName:(NSString *)nibName
{
    UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:identifier];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:nibName owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects valueAtIndex:0];
    }
    return cell;
}

- (void)showActivityPageWithHotInfo:(HotInfo *)hotInfo sourceViewSignNumber:(NSInteger)sourceViewSignNumber
{
    NSInteger hotType = hotInfo.hotType;
    
    if (hotType >= HOT_TYPE_PLATFORM && hotType <= HOT_TYPE_OPEN_SERVERS ) {
        
        NSArray *targetAction = [hotInfo.targetAction componentsSeparatedByString:@","];
        
        if (hotType == HOT_TYPE_PLATFORM) {
            ActivityDetailCtrl *activityDetailCtrl = [[[ActivityDetailCtrl alloc] init] autorelease];
            activityDetailCtrl.customTitle = hotInfo.title;
            activityDetailCtrl.contentUrl = hotInfo.targetActionUrl;
            activityDetailCtrl.activityId = [[targetAction valueAtIndex:0] intValue];
            activityDetailCtrl.appIdentifier = nil;
            activityDetailCtrl.hidesBottomBarWhenPushed = YES;
            
            [self analysisForMyHotSpots:hotType appIdentifier:nil sourceViewSignNumber:sourceViewSignNumber];
            
            [self.navigationController pushViewController:activityDetailCtrl animated:YES];
        } else if ([targetAction count] > 1) {
            ActivityDetailCtrl *activityDetailCtrl = [[[ActivityDetailCtrl alloc] init] autorelease];
            activityDetailCtrl.customTitle = hotInfo.title;
            
            activityDetailCtrl.contentUrl = hotInfo.targetActionUrl;
            
            activityDetailCtrl.activityId = [[targetAction valueAtIndex:0] intValue];
            
            if (hotType == HOT_TYPE_PLATFORM) {
                activityDetailCtrl.appIdentifier = nil;
            } else {
                activityDetailCtrl.appIdentifier = [targetAction valueAtIndex:1];
            }
            
            activityDetailCtrl.hidesBottomBarWhenPushed = YES;
            
            [self analysisForMyHotSpots:hotType appIdentifier:activityDetailCtrl.appIdentifier sourceViewSignNumber:sourceViewSignNumber];
            
            [self.navigationController pushViewController:activityDetailCtrl animated:YES];
            
        }
        
    }
    if (hotType == HOT_TYPE_APP_RECOMMEND) {
        if (hotInfo.targetAction != nil) {
            GameDetailController *gameDetailController = [GameDetailController gameDetailWithIdentifier:hotInfo.targetAction gameName:hotInfo.title];
            gameDetailController.hidesBottomBarWhenPushed = YES;
            
            [self analysisForMyHotSpots:hotType appIdentifier:hotInfo.targetAction sourceViewSignNumber:sourceViewSignNumber];
            
            [self.navigationController pushViewController:gameDetailController animated:YES];
        }
    }
    
    if (hotType == HOT_TYPE_STRATEGY) {
        if (hotInfo.targetActionUrl != nil) {
            GameDetailWebCtrl *webViewCtr = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:hotInfo.targetActionUrl];
            webViewCtr.customTitle = hotInfo.title;
            webViewCtr.hidesBottomBarWhenPushed = YES;
            
            [self analysisForMyHotSpots:hotType appIdentifier:nil sourceViewSignNumber:sourceViewSignNumber];
            
            [self.navigationController pushViewController:webViewCtr animated:YES];
        }
    }
}

- (void)assignCellBackground:(UITableViewCell *)cell withImageName:(NSString *)imageName isBottomLineNeeded:(BOOL)isBottomLineNeeded
{
    UIImage *image = [UIImage imageNamed:imageName];
    image = [image stretchableImageWithLeftCapWidth:image.size.width/2 topCapHeight:image.size.height/2];
    
    UIImageView *imageView = [[[UIImageView alloc] initWithFrame:cell.bounds] autorelease];
    imageView.image = image;
    
    if (isBottomLineNeeded == YES) {
        UIView *lineView = [[[UIView alloc] initWithFrame:CGRectMake(0, cell.bounds.size.height - 1, cell.bounds.size.width, 1)] autorelease];
        lineView.backgroundColor = [CommUtility colorWithHexRGB:@"E0E0E0"];
        [imageView addSubview:lineView];
    }
    
    cell.backgroundView = imageView;
}

- (void)analysisForMyHotSpots:(NSInteger)hotType appIdentifier:(NSString *)appIdentifier sourceViewSignNumber:(NSInteger)sourceViewSignNumber
{
    if (sourceViewSignNumber == MY_TOP_SPOT) {
        switch (hotType) {
            case HOT_TYPE_PLATFORM:
                [ReportCenter report:ANALYTICS_EVENT_15018];
                break;
            case HOT_TYPE_GIFT:
                [ReportCenter report:ANALYTICS_EVENT_15015 label:appIdentifier downloadFromNum:ANALYTICS_EVENT_15074];
                break;
            case HOT_TYPE_ACTIVITY:
                [ReportCenter report:ANALYTICS_EVENT_15013 label:appIdentifier downloadFromNum:ANALYTICS_EVENT_15072];
                break;
            case HOT_TYPE_NOTICE:
                [ReportCenter report:ANALYTICS_EVENT_15014 label:appIdentifier downloadFromNum:ANALYTICS_EVENT_15073];
                break;
            case HOT_TYPE_OPEN_SERVERS:
                [ReportCenter report:ANALYTICS_EVENT_15016 label:appIdentifier downloadFromNum:ANALYTICS_EVENT_15075];
                break;
            case HOT_TYPE_APP_RECOMMEND:
                [ReportCenter report:ANALYTICS_EVENT_15017 label:appIdentifier downloadFromNum:ANALYTICS_EVENT_15076];
                break;
            case HOT_TYPE_STRATEGY:
                [ReportCenter report:ANALYTICS_EVENT_15018];
                break;
            default:
                break;
        }
    }
    
    if (sourceViewSignNumber == MY_REST_SPOT) {
        switch (hotType) {
            case HOT_TYPE_PLATFORM:
                [ReportCenter report:ANALYTICS_EVENT_15024];
                break;
            case HOT_TYPE_GIFT:
                [ReportCenter report:ANALYTICS_EVENT_15021 label:appIdentifier downloadFromNum:ANALYTICS_EVENT_15079];
                break;
            case HOT_TYPE_ACTIVITY:
                [ReportCenter report:ANALYTICS_EVENT_15019 label:appIdentifier downloadFromNum:ANALYTICS_EVENT_15077];
                break;
            case HOT_TYPE_NOTICE:
                [ReportCenter report:ANALYTICS_EVENT_15020 label:appIdentifier downloadFromNum:ANALYTICS_EVENT_15078];
                break;
            case HOT_TYPE_OPEN_SERVERS:
                [ReportCenter report:ANALYTICS_EVENT_15022 label:appIdentifier downloadFromNum:ANALYTICS_EVENT_15080];
                break;
            case HOT_TYPE_APP_RECOMMEND:
                [ReportCenter report:ANALYTICS_EVENT_15023 label:appIdentifier downloadFromNum:ANALYTICS_EVENT_15081];
                break;
            case HOT_TYPE_STRATEGY:
                [ReportCenter report:ANALYTICS_EVENT_15024];
                break;
            default:
                break;
        }
    }
}

#pragma mark fetch

- (void)fetchData
{
    UserData *user = [UserData sharedInstance];
    HomePageInfo *homePageInfo = user.homePageInfo;
    self.hotList = [NSMutableArray arrayWithArray:homePageInfo.hotList];
    self.myGames = [NSMutableArray arrayWithArray:homePageInfo.myGames];
    self.dayRecommendList = [NSMutableArray arrayWithArray:[self filteredRecommmends:homePageInfo.dayRecommendList]];
    self.myGameIdsList = [NSArray arrayWithArray:user.myGameIdsList];
}

- (void)fetchHomePageTableViewSections
{
    KSTableProxy *proxy = [[KSTableProxy new] autorelease];
    NSMutableArray *sections = [[NSMutableArray new] autorelease];
    
    if ([self.hotList count] != 0)
    {
        KSTableProxySection *myHotSpotsSection = [self myHotSpotsSection];
        [sections addObject:myHotSpotsSection];
    }
    
    if ([self.myGames count] != 0)
    {
        KSTableProxySection *myGamesSection = [self myGamesSection];
        [sections addObject:myGamesSection];
    }
    
    if ([self.dayRecommendList count] != 0)
    {
        KSTableProxySection *dayRecommendSection = [self dayRecommendSection];
        [sections addObject:dayRecommendSection];
    }
    
    [self reloadTableWithProxy:proxy withSections:sections];
}

- (void)fetchUserInformationButtonItems:(UserBasicInfomation *)userInfo
{
    UIImageView *leftBarItemImageView = [[[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 200, 44)] autorelease];
    leftBarItemImageView.userInteractionEnabled = YES;
    
    [self fetchUserIconInLeftBarItemImageView:leftBarItemImageView userInfo:userInfo];
    [self fetchUserNameInLeftBarItemImageView:leftBarItemImageView userInfo:userInfo];
    
    UIBarButtonItem *leftBarItem = [[[UIBarButtonItem alloc] initWithCustomView:leftBarItemImageView] autorelease];
    
    self.navigationItem.leftBarButtonItem = leftBarItem;
}

- (void)fetchUserIconInLeftBarItemImageView:(UIImageView *)leftBarItemImageView userInfo:(UserBasicInfomation *)userInfo
{
    
    self.userIcon.image = [[NdCPIconManager singleton] getUserIcon:[NSString stringWithFormat:@"%d", userInfo.uin] checkSum:userInfo.checkSum photoType:ND_PHOTO_SIZE_BIG observer:self];
    
    [leftBarItemImageView addSubview:self.userIconBackground];
}

- (void)fetchUserNameInLeftBarItemImageView:(UIImageView *)leftBarItemImageView userInfo:(UserBasicInfomation *)userInfo
{
    self.userName = [[[UILabel alloc] initWithFrame:CGRectMake(61, 0, 131, 41)] autorelease];
    self.userName.backgroundColor = [UIColor clearColor];
    self.userName.font = [UIFont systemFontOfSize:20];
    self.userName.text = userInfo.nickName;
    self.userName.textColor = [CommUtility colorWithHexRGB:@"4883A3"];
    
    [leftBarItemImageView addSubview:self.userName];
}

#pragma mark - delegate

- (void)operation:(GameCenterOperation *)operation getHomePageDidFinish:(NSError *)error homePageInfo:(HomePageInfo *)homePageInfo
{
    [MBProgressHUD hideHUDForView:self.view animated:NO];
    if (error == nil) {
        //更新首页信息到缓存
        [[UserData sharedInstance] recordHomePageInfo:homePageInfo];
        
        [self updateDataAndView];
    }
}

- (void)operation:(GameCenterOperation *)operation getUserBasicInfomationDidFinish:(NSError *)error userBasicInfomationItem:(UserBasicInfomation *)userBasicInfomation
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    if (error == nil) {
        [self fetchUserInformationButtonItems:userBasicInfomation];
        self.navigationItem.rightBarButtonItem = nil;
        [UserData sharedInstance].userInfo = userBasicInfomation;
    }
}

#pragma mark - Section Header Row

- (KSTableProxyRow *)sectionHeaderRow:(SECTION_TYPE)sectionHeaderType
{
    KSTableProxyRow *row = [[KSTableProxyRow new] autorelease];
    
    row.cell = ^(NSInteger section, NSInteger row){
        SectionHeaderCell *cell = [SectionHeaderCell dequeOrCreateInTable:self.tableView];
        
        UserData *user = [UserData sharedInstance];
        HomePageInfo *homePageInfo = user.homePageInfo;
        cell.appList = [NSArray arrayWithArray:homePageInfo.appList];
        [cell refreshCellWithSectionHeaderType:sectionHeaderType];
        
        [self assignCellBackground:cell withImageName:@"bordless_first_row.png" isBottomLineNeeded:NO];
        
        cell.backgroundColor = [UIColor clearColor];
        cell.clipsToBounds = YES;        
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        return cell;
    };
    
    row.cellHeight = ^(NSInteger section, NSInteger row){
        return 32.0f;
    };
    
    row.repeatCount = ^(){
        return (NSUInteger)1;
    };
    
    return row;
}

#pragma mark MyHotSpots Section

- (KSTableProxySection *)myHotSpotsSection
{
    KSTableProxySection *section = [[KSTableProxySection new] autorelease];
    
    section.headerHeight = ^(NSInteger section){
        return 5.0f;
    };
    
    section.footerHeight = ^(NSInteger section){
        return 2.0f;
    };

    NSMutableArray *rowArray = [[NSMutableArray new] autorelease];
    
    KSTableProxyRow *sectionHeaderRow = [self sectionHeaderRow:SECTION_MY_HOT_SPOT];
    [rowArray addObject:sectionHeaderRow];
    
    KSTableProxyRow *myHotSpotIconRow = [self myHotSpotIconRow];
    [rowArray addObject:myHotSpotIconRow];
    
    KSTableProxyRow *myHotSpotRow = [self myHotSoptRow];
    [rowArray addObject:myHotSpotRow];
    
    section.rows = rowArray;
    
    return section;
}

- (KSTableProxyRow *)myHotSpotIconRow
{
    KSTableProxyRow *row = [[KSTableProxyRow new] autorelease];
    
    NSInteger (^realIndexInHotList)(NSInteger) = ^(NSInteger row) {
        return row - 1;
    };
    
    row.cell = ^(NSInteger section, NSInteger row){
        MyHotSpotIconCell *cell = [MyHotSpotIconCell dequeOrCreateInTable:self.tableView];
        
        HotInfo * hotInfo =[self.hotList valueAtIndex:realIndexInHotList(row)];
        [cell refreshCellWithHotInfo:hotInfo];
        
        [self assignCellBackground:cell withImageName:@"bordless_middle_row.png" isBottomLineNeeded:YES];
        [AssigningControlBackgroundView assignCellSelectedBackgroundView:cell];

        return cell;
    };
    
    row.cellHeight = ^(NSInteger section, NSInteger row){
//        return 68.0f;
        return 80.0f;
    };
    
    row.repeatCount = ^(){
        HotInfo *hotInfo = [self.hotList valueAtIndex:0];
        if (hotInfo.imageUrl != nil) {
            return (NSUInteger)1;
        } else {
            return (NSUInteger)0;
        }
    };
    
    row.didSelected = ^(NSInteger section, NSInteger row){
        
        HotInfo *hotInfo = [self.hotList valueAtIndex:realIndexInHotList(row)];
        [self showActivityPageWithHotInfo:hotInfo sourceViewSignNumber:MY_TOP_SPOT];
    };
    
    return row;
}

- (KSTableProxyRow *)myHotSoptRow
{
    KSTableProxyRow *row = [[KSTableProxyRow new] autorelease];
    
    NSInteger (^realIndexInHotList)(NSInteger) = ^(NSInteger row) {
        return row - 1;
    };
    
    row.cell = ^(NSInteger section, NSInteger row){
        HomePageMyHotSpotCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"HomePageMyHotSpotRow"];
        if (cell == nil) {
            cell = [[HomePageMyHotSpotCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"HomePageMyHotSpotRow"];
        }
        
        HotInfo *hotInfo = (HotInfo *)[self.hotList valueAtIndex:realIndexInHotList(row)];
        [cell refreshCellWithHotInfo:hotInfo];
        
        CGRect frame =  cell.frame;
        frame.size.height = 33.0f;
        cell.frame = frame;
        
//        NSInteger hotSpotCount = 0;
//        HotInfo *firstHotInfo = [self.hotList valueAtIndex:0];
//        if (firstHotInfo.imageUrl.length > 0) {
//            hotSpotCount = 3;
//        } else {
//            hotSpotCount = 4;
//        }
        
        if (row == [self.hotList count]) {
            [self assignCellBackground:cell withImageName:@"bordless_last_row.png" isBottomLineNeeded:NO];
        } else {
            [self assignCellBackground:cell withImageName:@"bordless_middle_row.png" isBottomLineNeeded:YES];
        }
        
        [AssigningControlBackgroundView assignCellSelectedBackgroundView:cell];

        return cell;
    };
    
    row.cellHeight = ^(NSInteger section, NSInteger row){
        return 33.0f;
    };
    
    row.repeatCount = ^(){
        HotInfo *hotInfo = [self.hotList valueAtIndex:0];
        if (hotInfo.imageUrl != nil) {
            return (NSUInteger)([self.hotList count] - 1);
        } else {
            return (NSUInteger)([self.hotList count]);
        }
    };
    
    row.didSelected = ^(NSInteger section, NSInteger row){
        HotInfo *hotInfo = [self.hotList valueAtIndex:realIndexInHotList(row)];
        [self showActivityPageWithHotInfo:hotInfo sourceViewSignNumber:MY_REST_SPOT];
    };
    
    return row;
}

#pragma mark MyGames Section

- (KSTableProxySection *)myGamesSection
{
    KSTableProxySection *section = [[KSTableProxySection new] autorelease];
    
    section.headerHeight = ^(NSInteger section){
        return 3.0f;
    };
    
    section.footerHeight = ^(NSInteger section){
        return 2.0f;
    };
        
    NSMutableArray *rowArray = [[NSMutableArray new] autorelease];
    
    KSTableProxyRow *sectionHeaderRow = [self sectionHeaderRow:SECTION_MY_GAMES];
    [rowArray addObject:sectionHeaderRow];
    
    KSTableProxyRow *mySuggestedGamesRow = [self mySuggestedGamesRow];
    [rowArray addObject:mySuggestedGamesRow];
    
    KSTableProxyRow *myTopGamesRow = [self myTopGamesRow];
    [rowArray addObject:myTopGamesRow];
    
    KSTableProxyRow *myGameButtonsRow = [self myGameButtonsRow];
    [rowArray addObject:myGameButtonsRow];
    
    KSTableProxyRow *myGameActivitiesPromptRow = [self myGameActivitiesPromptRow];
    [rowArray addObject:myGameActivitiesPromptRow];
    
    KSTableProxyRow *myGameActivitiesRow = [self myGameActivitiesRow];
    [rowArray addObject:myGameActivitiesRow];
    
    KSTableProxyRow *moreMyGameActivitiesRow = [self moreMyGameActivitiesRow];
    [rowArray addObject:moreMyGameActivitiesRow];
    
    section.rows = rowArray;
    
    return section;
}

- (KSTableProxyRow *)mySuggestedGamesRow
{
    KSTableProxyRow *row = [[KSTableProxyRow new] autorelease];
    row.cell = ^(NSInteger section, NSInteger row){
        MyGamesDefaultCell *cell = [MyGamesDefaultCell dequeOrCreateInTable:self.tableView];
        
        cell.fatherView = self;
        
        BOOL isMyGameDownloadedExist = NO;
        UserData *user = [UserData sharedInstance];
        HomePageInfo *homePageInfo = user.homePageInfo;
        if ([homePageInfo.appList count] > 0) {
            isMyGameDownloadedExist = YES;
        }
        [cell refresMyGamesDefaultCell:self.myGames isMyGameDownloadedExist:isMyGameDownloadedExist];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.userInteractionEnabled = YES;
        
        [self assignCellBackground:cell withImageName:@"bordless_last_row.png" isBottomLineNeeded:NO];
        
        cell.clipsToBounds = YES;
        
        return cell;
    };
    
    row.cellHeight = ^(NSInteger section, NSInteger row){
        return 144.0f;
    };
    
    row.repeatCount = ^(){
        MyGameInfo *myGame = [self.myGames valueAtIndex:0];
        if (myGame.suggestType == 1) {
            return (NSUInteger)1;
        } else {
            return (NSUInteger)0;
        }
    };
    
    return row;
}

- (KSTableProxyRow *)myTopGamesRow
{
    KSTableProxyRow *row = [[KSTableProxyRow new] autorelease];
    row.cell = ^(NSInteger section, NSInteger row){
        MyGamesCell *cell = [MyGamesCell dequeOrCreateInTable:self.tableView];
        
        [cell refreshMyGamesCell:self.myGames selectedButtonIndex:self.selectedButtonIndex];
        
        cell.fatherViewController = self;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        cell.userInteractionEnabled = YES;
        
        [self assignCellBackground:cell withImageName:@"bordless_last_row.png" isBottomLineNeeded:NO];

        return cell;
    };
    
    row.cellHeight = ^(NSInteger section, NSInteger row){
        return 100.0f;
    };
    
    row.repeatCount = ^(){
        MyGameInfo *myGame = [self.myGames valueAtIndex:0];
        if (myGame.suggestType == 1) {
            return (NSUInteger)0;
        } else {
            return (NSUInteger)1;
        }
    };
    
    return row;
}

- (KSTableProxyRow *)myGameButtonsRow
{
    KSTableProxyRow *row = [[KSTableProxyRow new] autorelease];
    row.cell = ^(NSInteger section, NSInteger row){
        MyGameButtonsCell *cell = [MyGameButtonsCell dequeOrCreateInTable:self.tableView];
        cell.fatherController = self;
        
        MyGameInfo *myGame = [self.myGames valueAtIndex:self.selectedButtonIndex];
        [cell refreshCellWithMyGameInfo:myGame withIndex:self.selectedButtonIndex];
        
        if ([myGame.activeList count] == 0) {
            [self assignCellBackground:cell withImageName:@"activity_area_single_row.png" isBottomLineNeeded:NO];
        } else {
            [self assignCellBackground:cell withImageName:@"activity_area_first_row.png" isBottomLineNeeded:YES];
        }
        
        cell.backgroundColor = [UIColor whiteColor];
        cell.clipsToBounds = NO;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        return cell;
    };
    
    row.cellHeight = ^(NSInteger section, NSInteger row){
        return 43.0f;
    };
    
    row.repeatCount = ^(){
        MyGameInfo *myGame = [self.myGames valueAtIndex:self.selectedButtonIndex];
        if (myGame.suggestType == 1) {
            return (NSUInteger)0;
        } else {
            return (NSUInteger)1;
        }
    };
    return row;
}

- (KSTableProxyRow *)myGameActivitiesPromptRow
{
    KSTableProxyRow *row = [[KSTableProxyRow new] autorelease];
    row.cell = ^(NSInteger section, NSInteger row){
        UITableViewCell *cell = [self dequeOrCreateCellWithIdentifier:@"MyGameActivitiesPromptRow" style:UITableViewCellStyleDefault];
        
        cell.textLabel.text = @"礼包·活动·公告";
        cell.textLabel.font = [UIFont boldSystemFontOfSize:16];
        cell.textLabel.textAlignment = NSTextAlignmentLeft;
        cell.textLabel.textColor = [CommUtility colorWithHexRGB:@"FF6317"];
        
        CGRect frame =  cell.frame;
        frame.size.height = 33.0f;
        cell.frame = frame;
        
        [self assignCellBackground:cell withImageName:@"activity_area_middle_row.png" isBottomLineNeeded:YES];
        
        cell.backgroundColor = [UIColor clearColor];
        cell.imageView.image = nil;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        
        return cell;
    };
    
    row.cellHeight = ^(NSInteger section, NSInteger row){
        return 33.0f;
    };
    
    row.repeatCount = ^(){
        MyGameInfo *myGame0 = [self.myGames valueAtIndex:self.selectedButtonIndex];
        if (myGame0.suggestType == 1) {
            return (NSUInteger)0;
        }
        
        MyGameInfo *myGame = [self.myGames valueAtIndex:self.selectedButtonIndex];
        NSInteger activitiesConut = [myGame.activeList count];
        if (activitiesConut > 0) {
            return (NSUInteger)1;
        } else {
            return (NSUInteger)0;
        }
    };
    return row;
}

- (KSTableProxyRow *)myGameActivitiesRow
{
    KSTableProxyRow *row = [[KSTableProxyRow new] autorelease];
    
    NSInteger (^realIndexInActiveList)(NSInteger) = ^(NSInteger row) {
        return row - 4;
    };
    
    row.cell = ^(NSInteger section, NSInteger row){
        MyGameActivityCell *cell = [MyGameActivityCell dequeOrCreateInTable:self.tableView];
        
        MyGameInfo *myGame = [self.myGames valueAtIndex:self.selectedButtonIndex];
        
        [cell refreshCellWithMyGameInfo:myGame indexRow:row];
        
        [self assignCellBackground:cell withImageName:@"activity_area_middle_row.png" isBottomLineNeeded:YES];
        [AssigningControlBackgroundView assignCellSelectedBackgroundView:cell];
        
        return cell;
    };
    
    row.cellHeight = ^(NSInteger section, NSInteger row){
        return 33.0f;
    };
    
    row.repeatCount = ^(){
        MyGameInfo *myGame0 = [self.myGames valueAtIndex:self.selectedButtonIndex];
        if (myGame0.suggestType == 1) {
            return (NSUInteger)0;
        }
        
        MyGameInfo *myGame = [self.myGames valueAtIndex:self.selectedButtonIndex];
        NSInteger activitiesConut = [myGame.activeList count];
        if (activitiesConut > MAX_ACTIVITIES_ROW_COUNT) {
            return (NSUInteger)MAX_ACTIVITIES_ROW_COUNT;
        } else {
            return (NSUInteger)activitiesConut;
        }
    };
    
    row.didSelected = ^(NSInteger section, NSInteger row){
        MyGameInfo *myGame = [self.myGames valueAtIndex:self.selectedButtonIndex];
        ActiveInfo *activeInfo = [myGame.activeList valueAtIndex:realIndexInActiveList(row)];
        
        if (activeInfo.contentUrl != nil) {
            ActivityDetailCtrl *activityDetailCtrl = [[[ActivityDetailCtrl alloc] init] autorelease];
            activityDetailCtrl.customTitle = activeInfo.title;
            activityDetailCtrl.appIdentifier = activeInfo.identifier;
            activityDetailCtrl.contentUrl = activeInfo.contentUrl;
            activityDetailCtrl.activityId = activeInfo.activityId;
            activityDetailCtrl.hidesBottomBarWhenPushed = YES;
            
            [ReportCenter report:ANALYTICS_EVENT_15030 label:myGame.identifier];
            
            [self.navigationController pushViewController:activityDetailCtrl animated:YES];
        }
    };
    
    return row;
}

- (KSTableProxyRow *)moreMyGameActivitiesRow
{
    KSTableProxyRow *row = [[KSTableProxyRow new] autorelease];
    row.cell = ^(NSInteger section, NSInteger row){
        UITableViewCell *cell = [self dequeOrCreateCellWithIdentifier:@"MoreMyGameActivitiesRow" style:UITableViewCellStyleDefault];
        
        cell.textLabel.text = @"去专区查看更多>>";
        cell.textLabel.font = [UIFont boldSystemFontOfSize:20];
        cell.textLabel.textAlignment = NSTextAlignmentCenter;
        cell.textLabel.textColor = [CommUtility colorWithHexRGB:@"1788C6"];
        cell.textLabel.backgroundColor = [UIColor clearColor];

        [self assignCellBackground:cell withImageName:@"activity_area_last_row.png" isBottomLineNeeded:NO];
        
        [AssigningControlBackgroundView assignCellSelectedBackgroundView:cell];
        
        return cell;
    };
    
    row.cellHeight = ^(NSInteger section, NSInteger row){
        return 46.0f;
    };
    
    row.repeatCount = ^(){
        MyGameInfo *myGame0 = [self.myGames valueAtIndex:self.selectedButtonIndex];
        if (myGame0.suggestType == 1) {
            return (NSUInteger)0;
        }
        
        MyGameInfo *myGame = [self.myGames valueAtIndex:self.selectedButtonIndex];
        if ([myGame.activeList count] > 0) {
            return (NSUInteger)1;
        } else {
            return (NSUInteger)0;
        }
    };
    
    row.didSelected = ^(NSInteger section, NSInteger row){
        MyGameInfo *myGame = [self.myGames valueAtIndex:self.selectedButtonIndex];
        GameDetailController *ctrl = [GameDetailController gameDetailWithIdentifier:myGame.identifier gameName:myGame.appName];
        ctrl.hidesBottomBarWhenPushed = YES;
        
        [ReportCenter report:ANALYTICS_EVENT_15031 label:myGame.identifier];
        
        [self.navigationController pushViewController:ctrl animated:YES];
    };
    
    return row;
}

#pragma mark DayRecommend Section

- (KSTableProxySection *)dayRecommendSection
{
    KSTableProxySection *section = [[KSTableProxySection new] autorelease];
    
    section.headerHeight = ^(NSInteger section){
        return 3.0f;
    };
    
    section.footerHeight = ^(NSInteger section){
        return 5.0f;
    };
    
    NSMutableArray *rowArray = [[NSMutableArray new] autorelease];
    
    KSTableProxyRow *sectionHeaderRow = [self sectionHeaderRow:SECTION_DAY_RECOMMEND];
    [rowArray addObject:sectionHeaderRow];
    
    KSTableProxyRow *topDayRecommendRow = [self topDayRecommendRow];
    [rowArray addObject:topDayRecommendRow];
    
    KSTableProxyRow *normalDayRecommendRow = [self normalDayRecommendRow];
    [rowArray addObject:normalDayRecommendRow];
    
    section.rows = rowArray;
    
    return section;
}

- (KSTableProxyRow *)topDayRecommendRow
{
    KSTableProxyRow *row = [[KSTableProxyRow new] autorelease];
    
    NSInteger (^realIndexInDayRecommendList)(NSInteger) = ^(NSInteger row) {
        return row - 1;
    };
    
    row.cell = ^(NSInteger section, NSInteger row){

        RecommendationDialyCell *cell = [RecommendationDialyCell dequeOrCreateInTable:self.tableView];

        DayRecommendInfo *dayRecommendInfo = ((DayRecommendInfo *)[self.dayRecommendList valueAtIndex:realIndexInDayRecommendList(row)]);
        
        [cell refreshRecommendationToday:dayRecommendInfo];
        
        [self assignCellBackground:cell withImageName:@"bordless_middle_row.png" isBottomLineNeeded:YES];
        
        [AssigningControlBackgroundView assignCellSelectedBackgroundView:cell];
        
        cell.clipsToBounds = YES;
        return cell;
    };
    
    row.cellHeight = ^(NSInteger section, NSInteger row){
        return 82.0f;
    };
    
    row.repeatCount = ^(){
        return (NSUInteger)1;
    };
    
    row.didSelected = ^(NSInteger section, NSInteger row){
        DayRecommendInfo *dayRecommendInfo = [self.dayRecommendList valueAtIndex:realIndexInDayRecommendList(row)];
        GameDetailController *gameDetailController = [GameDetailController gameDetailWithIdentifier:dayRecommendInfo.identifier gameName:dayRecommendInfo.title];
        
        [ReportCenter report:ANALYTICS_EVENT_15034 label:dayRecommendInfo.identifier downloadFromNum:ANALYTICS_EVENT_15070];
        
        [self.navigationController pushViewController:gameDetailController animated:YES];
    };
    
    return row;
}

- (KSTableProxyRow *)normalDayRecommendRow
{
    KSTableProxyRow *row = [[KSTableProxyRow new] autorelease];
    
    NSInteger (^realIndexInDayRecommendList)(NSInteger) = ^(NSInteger row) {
        return row - 1;
    };
    
    row.cell = ^(NSInteger section, NSInteger row){
        NormalDayRecommendCell *cell = [NormalDayRecommendCell dequeOrCreateInTable:self.tableView];

        DayRecommendInfo *dayRecommendInfo = ((DayRecommendInfo *)[self.dayRecommendList valueAtIndex:realIndexInDayRecommendList(row)]);
        [cell refreshCellWithDayRecommendInfo:dayRecommendInfo];

        if (row == LAST_DAY_RECOMMEND_ROW) {
            [self assignCellBackground:cell withImageName:@"bordless_last_row.png" isBottomLineNeeded:NO];
        } else {
            [self assignCellBackground:cell withImageName:@"bordless_middle_row.png" isBottomLineNeeded:YES];
        }
        
        [AssigningControlBackgroundView assignCellSelectedBackgroundView:cell];

        return cell;
    };
    
    row.cellHeight = ^(NSInteger section, NSInteger row){
        return 73.0f;
    };
    
    row.repeatCount = ^(){
        return (NSUInteger)2;
    };
    
    row.didSelected = ^(NSInteger section, NSInteger row){
        DayRecommendInfo *dayRecommendInfo = [self.dayRecommendList valueAtIndex:row - 1];
        GameDetailController *gameDetailController = [GameDetailController gameDetailWithIdentifier:dayRecommendInfo.identifier gameName:dayRecommendInfo.title];
        
        [ReportCenter report:ANALYTICS_EVENT_15035 label:dayRecommendInfo.identifier downloadFromNum:ANALYTICS_EVENT_15071];
        
        [self.navigationController pushViewController:gameDetailController animated:YES];
    };
    
    return row;
}

@end