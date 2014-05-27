//
//  GameCenter91AppDelegate.m
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-1.
//  Copyright NetDragon WebSoft Inc. 2012. All rights reserved.
//

#import "GameCenter91AppDelegate.h"
#import <NdComPlatform/NdComPlatform.h>
#import <NdComPlatform/NdCPNotifications.h>
#import <NDUtility/NDStringUtility.h>
#import <NdAnalytics/NdAnalyticsAPIResponse.h>
#import <NdAnalytics/NdAnalytics.h>
#import "GameCenterMacros.h"

#import "DashboardController.h"
#import "UINavigationController+Extent.h"
#import "UIViewController+Extent.h"
#import "UIBarButtonItem+Extent.h"
#import "NSArray+Extent.h"

#import "SoftManagementCenter.h"
#import "RequestorAssistant.h"
#import "OptionProtocols.h"
#import "MBProgressHUD.h"
#import "UserData.h"
#import "DatabaseUtility.h"

#import "GameHallController.h"
#import "ActivityMainController.h"
#import "MyGameController.h"
#import "UIAlertView+Blocks.h"
#import "RIButtonItem.h"
#import "CommUtility.h"
#import "CustomAlertView.h"
#import "CustomNavController.h"
#import "ActivityInfo.h"
#import "Notifications.h"
#import "HomePage.h"
#import "HomePageInfo.h"
#import "ReportCenter.h"
#import "GameDetailController.h"
#import "NdAppUpdateHelper+HOOK.h"

#define IPHONE5 ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(640, 1136), [[UIScreen mainScreen] currentMode].size) : NO)  

#define Key_MSType          @"a"
#define Key_AppId           @"b"
#define Key_ActivityId      @"c"
#define Key_ActivityTitle   @"d"


@interface GameCenter91AppDelegate()<GetAppLastedVersionProtocol,
                                    GetHomePageProtocol, GetAdvertisementListProtocol,
                                    GetGamesFilteredProtocol>
@property (nonatomic, assign) BOOL isAppResumingFromBackgroud;
@property (nonatomic, retain) NSDate *activeDate;

@property (nonatomic, retain) NSDictionary *launchOpitionsDic;

@property (nonatomic, unsafe_unretained)UIBackgroundTaskIdentifier backgroundTaskIdentifier;

//用来标记程序是否已完全初始化，以便在从后台唤醒时判断是否重刷游戏列表接口。因为在程序未初始化时也可能前台后台切换。
@property (nonatomic, assign) BOOL isAppFinishedAllLoad;

- (void)initSDK;
- (void)initNotificationWathers;
- (void)checkBasicInfo;
- (void)checkBasicInfoFinish;
- (void)gotoGameDetail:(NSString *)appIdentifier;
- (void)gotoGameTopic:(NSString *)topicidStr;
- (void)showLocalNotification:(NSDictionary *)userInfo;
- (void)showPushNotification:(NSDictionary *)userInfo;
- (void)handleOpenURL:(NSDictionary *)urlInfo;

@end

@implementation GameCenter91AppDelegate

@synthesize window;
@synthesize defaultImageView;
@synthesize isAppResumingFromBackgroud;
@synthesize activeDate;

@synthesize backgroundTaskIdentifier;
@synthesize isAppFinishedAllLoad;
@synthesize launchOpitionsDic;
#pragma mark -
#pragma mark Application lifecycle

- (void)initSDK
{
    int appId = APP_ID;
    NSString *appKey = APP_KEY;
    
    [[NdComPlatform defaultPlatform] setAppId:appId];
    [[NdComPlatform defaultPlatform] setAppKey:appKey];
    [NdComPlatform defaultPlatform].showLoadingWhenAutoLogin = NO;
    
    NdAnalyticsSettings *settings = [[[NdAnalyticsSettings alloc] init] autorelease];
	settings.appId = [NSString stringWithFormat:@"%d", appId];
    settings.appKey = appKey;
	[NdAnalytics initialize:settings];
    
}

- (void)initNotificationWathers
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(loginFinished:) name:kNdCPLoginNotification object:nil];
}

- (void)login
{
    //仅尝试自动登陆，失败不进入登录界面，仅发送登陆结果消息
    [[NdComPlatform defaultPlatform] NdLoginInBackground:0];
}

- (void)doWorkAfterLoad
{
    self.isAppFinishedAllLoad = YES;
    
    NSDictionary *launchOptions = self.launchOpitionsDic;
    //程序由本地推送消息启动
    UILocalNotification *noti = [launchOptions objectForKey:UIApplicationLaunchOptionsLocalNotificationKey];
    if (noti) {
        [self showLocalNotification:[noti userInfo]];
        
        [CommUtility cancelLocalNotification:noti];
    }
    
    //程序由远程推送消息启动
    NSDictionary* pushNotificationKey = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
    if (pushNotificationKey) {
        [self showPushNotification:pushNotificationKey];
    }
    
    //程序由openUrl启动
    NSURL *url = [launchOptions objectForKey:UIApplicationLaunchOptionsURLKey];
    if (url) {
        NSDictionary *urlInfo = [NSDictionary dictionaryWithObject:url forKey:@"URL"];
        [self handleOpenURL:urlInfo];
    }
    
    self.launchOpitionsDic = nil;
}

- (void)startToWork
{
    //下载队列的准备工作
    [[SoftManagementCenter sharedInstance] updateLocalInstalledAppsAfterStartup:[UserData sharedInstance].homePageInfo.appList];
    //检查可更新的app
    [RequestorAssistant requestAppLatestVersion:[[SoftManagementCenter sharedInstance] installed91SDKSoft] delegate:self];
    //获取广告信息
    [RequestorAssistant requestAdsList:[UserData sharedInstance].lastAdsListModifyDate delegate:self];
}

- (void)checkBasicInfo
{
    UserData *user = [UserData sharedInstance];
    // 调用1接口获取首页数据
    NSArray *arr = ([user.homePageInfo.appList count]>0) ? nil : [CommUtility allInstalledAppIdentifierArr];
    NSNumber *res = [RequestorAssistant requestHomePage:arr myGameIdentifiers:user.myGameIdsList delegate:self];
    if ([res intValue] >= 0)
    {
        if (user.homePageInfo == NO)
        {
            NSLog(@"未找到首页缓存");
            [MBProgressHUD showBlockHUD:@"正在初始化" message:nil];
        }
    }
    else
    {
        [MBProgressHUD showHintHUD:@"初始化失败" message:nil hideAfter:10];
        [self startToWork];
    }
    [self performSelector:@selector(checkBasicInfoFinish) withObject:nil afterDelay:0.01];
}

- (void)checkBasicInfoFinish
{
    [self.defaultImageView removeFromSuperview];
    self.defaultImageView = nil;
    
    [[UIApplication sharedApplication] setStatusBarHidden:NO];
    
    DashboardController *rootCtr = [DashboardController dashBoardController];
    //2013/11/14 打开后进入首页
    rootCtr.currentSelectedIndex = HOME_PAGE;
    rootCtr.delegate = self;
    
    window.rootViewController = rootCtr;
    window.backgroundColor = [CommUtility defaultBgColor];
    
    //1.5版本暂时不开启引导
//    if (![[NSUserDefaults standardUserDefaults] boolForKey:@"firstLaunch"]) {
    if (NO) {
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"firstLaunch"];
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    else {
        [self doWorkAfterLoad];
        [self login];
    }
}

- (BOOL)tabBarController:(UITabBarController *)tabBarController shouldSelectViewController:(UIViewController *)viewController
{
    return YES;
}

- (void)showNotLoginHint
{
    [[UserData sharedInstance] showNotNormalUserHint:@"亲，没有身份不能进入个人中心的哦。快用91帐号登录吧！"];
}

- (void)hookSDKOnVersionCheck
{
    //产品经理要求在软件更新回来的时候知道网络是否可用，但是SDK的更新接口又没有返回这个信息
    //所以HOOK SDK中的软件更新回调接口，检查原始的错误信息，判断是否网络不可用
    [NdAppUpdateHelper hookForGameCenter];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{  
    NSLog(@"game start");
        
    self.defaultImageView = [[[UIImageView alloc] initWithFrame:self.window.bounds] autorelease];
    if (IPHONE5) {
        self.defaultImageView.image = [UIImage imageNamed:@"Default-568h@2x.png"];
    }
    else {
        self.defaultImageView.image = [UIImage imageNamed:@"Default.png"];
    }
    
    [self.window addSubview:self.defaultImageView];
    [window makeKeyAndVisible];

    [self initSDK];
    [self initNotificationWathers];
    //初始化网络请求管理队列
    [RequestorAssistant prepare];
    //初始化数据库
    [DatabaseUtility prepare];
    [UserData prepare];
    [[SoftManagementCenter sharedInstance] prepareToWorkWithLocalInstalledApps:[UserData sharedInstance].homePageInfo.appList];
    //初始化自定义导航条
    [UINavigationController prepareCustomizeNavigationBar];
    
	[[UIApplication sharedApplication] registerForRemoteNotificationTypes:UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert];
        
#if TARGET_IPHONE_SIMULATOR
    [self checkBasicInfo];
#else
    [self hookSDKOnVersionCheck];
    [[NdComPlatform defaultPlatform] NdAppVersionUpdate:0 delegate:self];
#endif
    application.idleTimerDisabled = YES;
    self.isAppResumingFromBackgroud = YES;
    self.isAppFinishedAllLoad = NO;
    self.activeDate = [NSDate date];
    
    self.launchOpitionsDic = [NSDictionary dictionaryWithDictionary:launchOptions];
    //如果由openUrl启动，返回NO，这样application:handleOpenURL:就不会被调用
    NSURL *url = [launchOptions objectForKey:UIApplicationLaunchOptionsURLKey];
    if (url == nil) {
        return YES;
    }
    else {
        return NO;
    }
}

- (void)appVersionUpdateDidFinish:(ND_APP_UPDATE_RESULT)updateResult
{
    NSLog(@"update result %d", updateResult);
    [self checkBasicInfo];
}

- (void)quitGameCenter:(NSString *)hint
{
    //hint is not used yet
    [[UIApplication sharedApplication] performSelector:@selector(suspend)];
    exit(0);    
}

- (void)loginFinished:(NSNotification *)aNotification
{
    NSDictionary *dict = [aNotification userInfo];
	BOOL success = [[dict objectForKey:@"result"] boolValue];
	int error = [[dict objectForKey:@"error"] intValue];

	if (success)
	{
        [NdAnalytics setUid:[[NdComPlatform defaultPlatform] loginUin]];
        
        //博远账号注册成功后也会有弹窗，跟“欢迎回来”弹窗冲突
        if (![[dict objectForKey:@"isRegistered"] boolValue]) {
            NdComPlatform *defaultPlatform = [NdComPlatform defaultPlatform];
            if ([defaultPlatform respondsToSelector:@selector(showWelcomeWindow)])
            {
                [defaultPlatform performSelector:@selector(showWelcomeWindow)];
            }
        }
	}
	else
	{
        switch (error) {
            case ND_COM_PLATFORM_ERROR_USER_CANCEL:
            case ND_COM_PLATFORM_ERROR_NETWORK_FAIL:
            case ND_COM_PLATFORM_ERROR_NETWORK_ERROR:
                break;
            default:
                break;
        }
	}
}

- (void)applicationWillResignActive:(UIApplication *)application {
    /*
     Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
     Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
     */
}

- (BOOL)isMultitaskingSupported {
    BOOL result = NO;
    if ([[UIDevice currentDevice] respondsToSelector:@selector(isMultitaskingSupported)]) {
        result = [[UIDevice currentDevice] isMultitaskingSupported];
    }
    return result;
}

- (void)endBackgroundTask {
    dispatch_queue_t mainQueue = dispatch_get_main_queue();
    dispatch_async(mainQueue, ^{
        if (self != nil) {
//            [self.myTimer invalidate];
            [[UIApplication sharedApplication] endBackgroundTask:self.backgroundTaskIdentifier];
            self.backgroundTaskIdentifier = UIBackgroundTaskInvalid;
        }
    });
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    /*
     Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
     If your application supports background execution, called instead of applicationWillTerminate: when the user quits.
     */
    //[[SoftManagementCenter sharedInstance] saveWork];
    int count = [[SoftManagementCenter sharedInstance] updatableCount];
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:count];
    
    self.isAppResumingFromBackgroud = NO;
    
    
    if ([self isMultitaskingSupported] == NO) {
        return;
    }
    self.backgroundTaskIdentifier = [application beginBackgroundTaskWithExpirationHandler:^{
        [self endBackgroundTask];
    }];
    
    //没有第三方urlscheme时要清掉按钮
    DashboardController *dash = ((DashboardController *)(self.window.rootViewController));
    UINavigationController *navCtr = (UINavigationController *)dash.selectedViewController;
    UIViewController *ctr = [navCtr topViewController];
    if ([[UserData sharedInstance].thirdPartyUrlScheme length] <= 0) {
        ctr.navigationItem.leftBarButtonItem = nil;
    }
}


- (void)applicationWillEnterForeground:(UIApplication *)application {
    self.isAppResumingFromBackgroud = YES;
    self.activeDate = [NSDate date];
    
    if (self.backgroundTaskIdentifier != UIBackgroundTaskInvalid) {
        [self endBackgroundTask];
    }
}


- (void)applicationDidBecomeActive:(UIApplication *)application {
    /*
     Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
     */
    
    //从urlscheme启动时，如果游戏中心已在运行，当前标签的viewWillAppear并不会被调用，所有要在这个地方设置按钮
    DashboardController *dash = ((DashboardController *)(self.window.rootViewController));
    if (dash.currentSelectedIndex == GAME_PAGE || dash.currentSelectedIndex == ACTIVITIES_PAGE || dash.currentSelectedIndex == MANAGEMENT) {
        UINavigationController *navCtr = (UINavigationController *)dash.selectedViewController;
        UIViewController *ctr = [navCtr.viewControllers objectAtIndex:0];
        [CommUtility showBarItemForCallBack:ctr];
    }
    
    //程序可能长期在后台，此间服务端可能添加游戏，所以客户端恢复启动时要刷这个接口
    if (self.isAppFinishedAllLoad) {
//        UserData *user = [UserData sharedInstance];
        //TODO: 是否从后台回来要刷1接口
        
//        [RequestorAssistant requestGetAppAndAdsList:user.lastAppListModifyDate adsLastModified:user.lastAdsListModifyDate activityLastModified:user.lastActivityListModifyDate delegate:self];
        
        //获取广告信息
        [RequestorAssistant requestAdsList:[UserData sharedInstance].lastAdsListModifyDate delegate:self];
        
        //回到前台时候viewWillAppear不会自己调用，用此消息刷新如首页最近在玩等界面
        [[NSNotificationCenter defaultCenter] postNotificationName:kGC91ApplicationDidBecomeActive object:nil];
    }
}


- (void)applicationWillTerminate:(UIApplication *)application {
    /*
     Called when the application is about to terminate.
     See also applicationDidEnterBackground:.
     */
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    NSLog(@"device token %@", deviceToken);
    NSString *hexString = [NDStringUtility getHexStringFromBytes:[deviceToken bytes] length:[deviceToken length]];
    NSLog(@"hex device token %@", hexString);
    [UserData sharedInstance].deviceToken = hexString;
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
    NSLog(@"fail to register device token %@", error);
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
    NSLog(@"did recevie remote notify %@", userInfo);
    if (self.isAppResumingFromBackgroud) {
        //小于2秒内，认为从后台点击通知
        if (abs([self.activeDate timeIntervalSinceNow]) < 2) {
            [self showPushNotification:userInfo];
        }
        else {
            NSString *appIdentifier = [userInfo objectForKey:Key_AppId];
            int activityId = [[userInfo objectForKey:Key_ActivityId] intValue];
            NSString *activityUrl = [NSString stringWithFormat:@"%@%d/", PUSH_ACTIVIT_URL, activityId];
            NSString *activityTitle = [userInfo objectForKey:Key_ActivityTitle];
            
            NSLog(@"%@\n%d\n%@\n%@", appIdentifier, activityId, activityUrl, activityTitle);
            
            //正在运行中
            int MSType = [[userInfo objectForKey:Key_MSType] intValue];
            NSString *alertContent = [[userInfo objectForKey:@"aps"] objectForKey:@"alert"];
            
            RIButtonItem *cancelItem = [RIButtonItem itemWithLabel:@"关闭"];
            UIAlertView *alterView = [[UIAlertView alloc] initWithTitle:@"91游戏中心" message:alertContent cancelButtonItem:cancelItem otherButtonItems:nil];
            
            if (MSType == 1) {
                RIButtonItem *openItem = [RIButtonItem itemWithLabel:@"查看"];
                openItem.action = ^{
                    [self gotoActivityDetail:appIdentifier activityid:activityId activityUrl:activityUrl activityName:activityTitle currentPage:2];
                };
                [alterView addButtonItem:openItem];
            }
            
            [alterView show];
            [alterView release];
        }
    }
    //[self performSelector:@selector(removeNotificationOnceAccepted)];
}

- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
    NSLog(@"did recevie local notify %@", notification);
    if (self.isAppResumingFromBackgroud) {
        //小于2秒内，认为从后台点击本地通知
        if (abs([self.activeDate timeIntervalSinceNow]) < 2) {
            [self showLocalNotification:[notification userInfo]];
        }
        else {
            //正在运行中
            RIButtonItem *cancelItem = [RIButtonItem itemWithLabel:@"关闭"];
            RIButtonItem *openItem = [RIButtonItem itemWithLabel:@"查看"];
            openItem.action = ^{[self gotoGameDetail:[notification.userInfo objectForKey:KEY_LOCAL_NOTIFICATION_APPID]];};
            
            UIAlertView *alterview = [[UIAlertView alloc] initWithTitle:notification.alertBody message:[notification.userInfo objectForKey:@"title"] cancelButtonItem:nil otherButtonItems:cancelItem, openItem, nil];
            [alterview show];
            [alterview release];
        }
    }
    //[self performSelector:@selector(removeNotificationOnceAccepted)];
    [CommUtility cancelLocalNotification:notification];
}

//用opneUrl启动
- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url
{
    NSLog(@"handleOpenURL***************************************************************************"); 
    if(!url) { 
        return NO;
    }else{
        NSDictionary *urlInfo = [NSDictionary dictionaryWithObject:url forKey:@"URL"];
        [self handleOpenURL:urlInfo];
    }

    return YES;
}

- (void)launchedCallBackUrlScheme
{
    NSString *strCallbackUrl = [UserData sharedInstance].thirdPartyUrlScheme;
    NSRange range = [strCallbackUrl rangeOfString:@"://"];
    if (range.location == NSNotFound) {
        strCallbackUrl = [strCallbackUrl stringByAppendingFormat:@"://"];
    }
    
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:strCallbackUrl]];
    [UserData sharedInstance].thirdPartyUrlScheme = nil;
}

- (void)handleOpenURL:(NSDictionary *)urlInfo
{
    NSURL *url = [urlInfo objectForKey:@"URL"];
    
    NSLog(@"Scheme: %@", [url scheme]); 
    NSLog(@"Host: %@", [url host]); 
    NSLog(@"Port: %@", [url port]);     
    NSLog(@"Path: %@", [url path]);     
    NSLog(@"Relative path: %@", [url relativePath]);
    NSLog(@"Path components as array: %@", [url pathComponents]);        
    NSLog(@"Parameter string: %@", [url parameterString]);   
    NSLog(@"Query: %@", [url query]);       
    NSLog(@"Fragment: %@", [url fragment]);
    
    NSString* queryString=[url query];
    NSString* fragmentString=[[url fragment] lowercaseString];
    NSDictionary *queryComponents = [CommUtility dictionaryFromUrlQueryComponents:[queryString lowercaseString]];
    
    NSString *callbackUrlScheme = [queryComponents objectForKey:@"callback"];
    [UserData sharedInstance].thirdPartyUrlScheme = callbackUrlScheme;

    NSString *strAction = [queryComponents objectForKey:@"do"];
    if (strAction) {
        if ([strAction isEqualToString:@"gametab"]) {//显示游戏tab
            [self getGameHallController:1];
        }
        else if ([strAction isEqualToString:@"activitytab"]) {//显示活动tab
            [self getActivityMainController:1];
        }
        else if ([strAction isEqualToString:@"managertab"]) {//显示管理tab
            [self getMyGameController:0];
        }
        else if ([strAction isEqualToString:@"detail"]) {//跳转.根据detail的值跳到不同类型页面
            
            NSString *targetType = [queryComponents objectForKey:@"targettype"];
            NSString *targetAction = [queryComponents objectForKey:@"targetaction"];
            NSString *targetTitle = [queryComponents objectForKey:@"targettitle"];
            NSString *targetActionUrl = [queryComponents objectForKey:@"targetactionurl"];
            
            if ([targetType isEqualToString:@"activitydetail"]) {
                //跳转到活动详情
                [self dealSkipActivity:targetAction targetUrl:targetActionUrl titile:targetTitle currentPage:2];
            }
            else if ([targetType isEqualToString:@"giftdetail"]) {
                //跳转到礼包详情
                [self dealSkipActivity:targetAction targetUrl:targetActionUrl titile:@"礼包" currentPage:1];
            }
            else if ([targetType isEqualToString:@"kaifudetail"]){
                //跳转到开服详情
                [self dealSkipActivity:targetAction targetUrl:targetActionUrl titile:@"新服预告" currentPage:3];
            }
            else if ([targetType isEqualToString:@"gamedetail"]) {
                //跳转到游戏详情
                if ([targetAction length] <= 0) return;
                NSString *moreDetail = fragmentString;
                int appid = [targetAction intValue];
                
                [self dealSkipGameDetail:appid moreDetail:moreDetail];
            }
            else if ([targetType isEqualToString:@"sdkgrade"]){
                //由SDK升级跳转而来 //字段待定
                if ([targetAction length] <= 0) return;
                int appid = [targetAction intValue];
                
                [self dealSkipGameUpdate:appid];
            }
            else if ([targetType isEqualToString:@"gametopic"]) {
                //跳转到游戏专题
                if ([targetAction length] <= 0) return;
                [self gotoGameTopic:targetAction];
            }
            
        }
    }
}

//游戏过滤
- (void)doGameFilter
{
    NSNumber *res = [RequestorAssistant requestGamesFilteredList:[UserData sharedInstance].newAppIdsList delegate:self];
    if ([res intValue] < 0)
    {
        [self startToWork];
    }
}

#pragma mark - call back
//首页
- (void)operation:(GameCenterOperation *)operation getHomePageDidFinish:(NSError *)error homePageInfo:(HomePageInfo *)homePageInfo
{
    if (error == nil) {
        if ([UserData sharedInstance].homePageInfo == NO) {
            [MBProgressHUD hideBlockHUD:NO];
        }
        //更新首页信息到缓存
        [[UserData sharedInstance] recordHomePageInfo:homePageInfo];
    }
    //调用游戏过滤接口
    [self doGameFilter];
}

//广告
- (void)operation:(GameCenterOperation *)operation getAdsListDidFinish:(NSError *)error adsList:(AdsBriefInfoList *)adsList
{
    if (error == nil)
    {
        [[UserData sharedInstance] recordAdsList:adsList];
    }
}

//游戏过滤回调
- (void)operation:(GameCenterOperation *)operation getGamesFilteredListDidFinish:(NSError *)error appList:(NSArray *)appList
{
    UserData *userData = [UserData sharedInstance];
    if (error == nil) {
        //保存新增游戏
        [userData recordFilteredGames:appList];
    }
    
    [self startToWork];
}
//获取游戏最新版本回调
- (void)operation:(GameCenterOperation *)operation getAppLastedVersionDidFinish:(NSError *)error appList:(NSArray *)appList
{
    if (error == nil)
    {
        [[SoftManagementCenter sharedInstance] updateUpdatableInfo:appList];
    }
}

#pragma mark -
#pragma mark Memory management

- (void)applicationDidReceiveMemoryWarning:(UIApplication *)application {
    /*
     Free up as much memory as possible by purging cached data objects that can be recreated (or reloaded from disk) later.
     */
}


- (void)dealloc {
    [window release];
    self.defaultImageView = nil;
    self.activeDate = nil;
    self.launchOpitionsDic = nil;
    [super dealloc];
}

#pragma mark -

- (void)gotoGameDetail:(NSString *)appIdentifier
{
    //打开游戏专区
    DashboardController *dash = ((DashboardController *)(self.window.rootViewController));
    if (dash.currentSelectedIndex != GAME_PAGE) {
        dash.currentSelectedIndex = GAME_PAGE;
    }
    UINavigationController *navCtr = (UINavigationController *)dash.selectedViewController;
    [CommUtility pushGameDetailController:appIdentifier gameName:nil navigationController:navCtr ];
}

- (void)dealSkipGameDetail:(int)appId moreDetail:(NSString *)moreDetail
{
    NSNumber *res = [RequestorAssistant requestAppIdentifier:appId complete:^(NSString *identifier, NSString *strategyUrl, NSString *forumUrl) {
        [MBProgressHUD hideBlockHUD:NO];
        //统计
        [ReportCenter report:ANALYTICS_EVENT_15012 label:identifier downloadFromNum:ANALYTICS_EVENT_15102];
        
        DashboardController *dash = ((DashboardController *)(self.window.rootViewController));
        if (dash.currentSelectedIndex != GAME_PAGE) {
            dash.currentSelectedIndex = GAME_PAGE;
        }
        UINavigationController *navCtr = (UINavigationController *)dash.selectedViewController;
        [CommUtility pushMoreGameDetailController:identifier gameName:nil navigationController:navCtr moreDetail:moreDetail];
    }];
    if ([res intValue] >= 0)
    {
        [MBProgressHUD showBlockHUD:@"正在获取游戏信息" message:nil];
    }
}

- (void)dealSkipGameUpdate:(int)appId
{
    NSNumber *res = [RequestorAssistant requestAppIdentifier:appId complete:^(NSString *identifier, NSString *strategyUrl, NSString *forumUrl) {
        [MBProgressHUD hideBlockHUD:NO];
        //统计
        [ReportCenter report:ANALYTICS_EVENT_15012 label:identifier downloadFromNum:ANALYTICS_EVENT_15102];
        
        DashboardController *dash = ((DashboardController *)(self.window.rootViewController));
        UINavigationController *navCtr = (UINavigationController *)dash.selectedViewController;
        //[navCtr popToRootViewControllerAnimated:NO];
        [CommUtility presentGameDetailController:identifier viewController:[navCtr topViewController]];
    }];
    if ([res intValue] >= 0)
    {
        [MBProgressHUD showBlockHUD:@"正在获取游戏信息" message:nil];
    }
}

- (void)dealSkipActivity:(NSString *)targetAction targetUrl:(NSString *)targetActionUrl titile:(NSString *)targetTitle currentPage:(int)pageIdx
{
    if (!targetAction || !targetTitle || !targetActionUrl) {
        return;
    }
    
    NSArray *arr = [targetAction componentsSeparatedByString:@","];
    int appId = [[arr valueAtIndex:0] intValue];
    int activityId = [[arr valueAtIndex:1] intValue];
    
    NSNumber *res = [RequestorAssistant requestAppIdentifier:appId complete:^(NSString *identifier, NSString *strategyUrl, NSString *forumUrl) {
        [MBProgressHUD hideBlockHUD:NO];
        //统计
        [ReportCenter report:ANALYTICS_EVENT_15012 label:identifier downloadFromNum:ANALYTICS_EVENT_15102];
        
        [self gotoActivityDetail:identifier activityid:activityId activityUrl:targetActionUrl activityName:targetTitle currentPage:pageIdx];
    }];
    if ([res intValue] >= 0)
    {
        [MBProgressHUD showBlockHUD:YES];
    }
}

- (void)gotoActivityDetail:(NSString *)aAppidentifier activityid:(int)activityid activityUrl:(NSString *)activityUrl activityName:(NSString *)activityname currentPage:(int)pageIdx
{
    //打开对应de活动专区(pageIdx：0-3)
    DashboardController *dash = ((DashboardController *)(self.window.rootViewController));
    if (dash.currentSelectedIndex != ACTIVITIES_PAGE) {
        dash.currentSelectedIndex = ACTIVITIES_PAGE;
    }
    UINavigationController *navCtr = (UINavigationController *)dash.selectedViewController;
    [navCtr popToRootViewControllerAnimated:NO];
    ActivityMainController *activityCtr = (ActivityMainController *)[navCtr topViewController];
    activityCtr.currentPage = pageIdx;
    
    [CommUtility pushActivityDetailCtrl:aAppidentifier activityId:activityid activityUrl:activityUrl activityTitle:[activityname stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding] navigationController:navCtr];
}

- (void)gotoGameTopic:(NSString *)topicidStr
{
    //跳转到游戏专题
    int topicId = [topicidStr intValue];
    DashboardController *dash = ((DashboardController *)(self.window.rootViewController));
    if (dash.currentSelectedIndex != GAME_PAGE) {
        dash.currentSelectedIndex = GAME_PAGE;
    }
    UINavigationController *navCtr = (UINavigationController *)dash.selectedViewController;
    
    [CommUtility pushGameTopicController:topicId navigationController:navCtr];
}

- (DashboardController *)getDashboardController:(int)index
{
    if (index <0 || index >4)
        return nil;
    DashboardController *dash = ((DashboardController *)(self.window.rootViewController));
    if (dash.currentSelectedIndex != index) {
        dash.currentSelectedIndex = index;
    }
    return dash;
}

- (GameHallController *)getGameHallController:(int)index
{
    if (index <0 || index >2)
        return nil;
    DashboardController *dash = [self getDashboardController:1];
    UINavigationController *navCtr = (UINavigationController *)dash.selectedViewController;
    [navCtr popToRootViewControllerAnimated:NO];
    GameHallController *hallCtr = (GameHallController *)[navCtr topViewController];
    hallCtr.currentPage = index;
    return hallCtr;
}

- (ActivityMainController *)getActivityMainController:(int)index
{
    if (index <0 || index >3)
        return nil;
    DashboardController *dash = [self getDashboardController:2];
    UINavigationController *navCtr = (UINavigationController *)dash.selectedViewController;
    [navCtr popToRootViewControllerAnimated:NO];
    ActivityMainController *activityCtr = (ActivityMainController *)[navCtr topViewController];
    activityCtr.currentPage = index;
    return activityCtr;
}

- (MyGameController *)getMyGameController:(int)index
{
    if (index <0 || index >2)
        return nil;
    DashboardController *dash = [self getDashboardController:3];
    UINavigationController *navCtr = (UINavigationController *)dash.selectedViewController;
    [navCtr popToRootViewControllerAnimated:NO];
    MyGameController *myGameCtr = (MyGameController *)[navCtr topViewController];
    myGameCtr.currentPage = index;
    return myGameCtr;
}

- (void)showLocalNotification:(NSDictionary *)userInfo
{
    [self gotoGameDetail:[userInfo objectForKey:KEY_LOCAL_NOTIFICATION_APPID]];
}

- (void)showPushNotification:(NSDictionary *)userInfo
{
    NSString *appIdentifier = [userInfo objectForKey:Key_AppId];
    int activityId = [[userInfo objectForKey:Key_ActivityId] intValue];
    NSString *activityUrl = [NSString stringWithFormat:@"%@%d/", PUSH_ACTIVIT_URL, activityId];
    NSString *activityTitle = [userInfo objectForKey:Key_ActivityTitle];

    NSLog(@"%@\n%d\n%@\n%@", appIdentifier, activityId, activityUrl, activityTitle);
    
    int MSType = [[userInfo objectForKey:Key_MSType] intValue];
    if (MSType == 1) {
        [self gotoActivityDetail:appIdentifier activityid:activityId activityUrl:activityUrl activityName:activityTitle currentPage:2];
    }
}

- (void)removeNotificationOnceAccepted
{
    NSLog(@"remove notification once it is accepted");
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:1];
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
    [[UIApplication sharedApplication] cancelAllLocalNotifications];
}

@end
