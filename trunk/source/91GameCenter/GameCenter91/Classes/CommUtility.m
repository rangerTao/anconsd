//
//  CommUtility.m
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-29.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import "CommUtility.h"
#import "DashboardController.h"
#import "MBProgressHUD.h"
#import "NSDate+Utilities.h"
#import <CoreTelephony/CTTelephonyNetworkInfo.h>
#import <CoreTelephony/CTCarrier.h>
#import <NdComPlatform/NDComPlatform.h>
#import "UserData.h"
#import "UIViewController+Extent.h"
#import "UIBarButtonItem+Extent.h"
#import "ActivityInfo.h"
#import "ActivityDetailCtrl.h"
#import "GameDetailController.h"
#import "GameTopicController.h"
#import "MIUtility.h"
#import "ActivityCommonCtrl.h"
#import "GameDetailWebCtrl.h"
#import "NdCPReachability.h"
#import "CustomNavController.h"
#import "UINavigationController+Extent.h"
#import <Log/NDLogger.h>

#define	 FILENAME_CHANNEL_CFG	@"NdChannelId.plist"
#define	 KEY_CHANNEL_ID			@"chl"
#define	 DEFAULT_CHANNEL_ID		@"0"

@interface UIApplication(OpenApp)
- (BOOL)launchApplicationWithIdentifier:(NSString *)identifier suspended:(BOOL)suspended;
@end

@implementation CommUtility

+ (NSString *)getDocumentPath
{
	NSArray *documentArr = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	assert([documentArr count] > 0);
    return [documentArr objectAtIndex:0];
}

+ (BOOL)openApp:(NSString *)identifier
{
    return [[UIApplication sharedApplication] launchApplicationWithIdentifier:identifier suspended:NO];
}

+ (NSString *)readableFileSize:(long)size
{
    if (size <= 0)
        size = 0;
    
	if (size < 1024)
		return [NSString stringWithFormat:@"%ld %@", size, @"B"];
	
	if (size < 1024 * 1024)
		return [NSString stringWithFormat:@"%.0lf %@", size / 1024.0, @"K"];
	
	if (size < 1024 * 1024 * 1024)
		return [NSString stringWithFormat:@"%.2lf %@", size / 1024.0 / 1024.0, @"M"];
	
	return [NSString stringWithFormat:@"%.2lf %@", size / 1024.0 / 1024.0 / 1024.0, @"G"];
}
+ (NSString *)readableDownloadNumber:(int)num
{
    if (num <= 0)
        return [NSString stringWithFormat:@"0"];
    if (num < 10000)
        return [NSString stringWithFormat:@"%d",num];
    if (num < 10000 * 10)
        return [NSString stringWithFormat:@"%.1lf %@", num / 10000.0, @"万"];
    if (num >= 10000 *10)
        return [NSString stringWithFormat:@"%.0lf %@" ,num / 10000.0, @"万"];
    return nil;
}
+ (NSString *)dateFromLastModified:(NSString *)lastModified
{
    NSString *retStr;
    int len = [lastModified length];
    NSString *date = [lastModified substringToIndex:len>10 ? 10: len];
//    NSString *time = [lastModified substringFromIndex:len>11 ? 11: len];
//    NSDate *today = [NSDate date];
//    //如果是今天则返回：time，如果不是则返回：date
//    if ([[[today description] substringToIndex:10] isEqualToString:[lastModified substringToIndex:len>10 ? 10: len]]) {
//        retStr = [NSString stringWithFormat:@"今天%@", time];
//    }
//    else {
        retStr = date;
//    }
    return retStr;
}

+ (BOOL)isSamedayDay1:(NSDate *)date1 date2:(NSDate *)date2
{
    NSString *str1 = [self stringFromDate:date1];
    str1 = [str1 substringToIndex:([str1 length]>10 ? 10 : [str1 length])];
    NSString *str2 = [self stringFromDate:date2];;
    str2 = [str2 substringToIndex:([str2 length]>10 ? 10 : [str2 length])];
    
    return [str1 isEqualToString:str2];
}

//取url转化为dict
+ (NSDictionary *)dictionaryFromUrlQueryComponents:(NSString *)query
{
    if(!query||[query length]==0) return nil;
	NSArray *array = [query componentsSeparatedByString:@"&"];
	NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
	for (int i = 0; i < [array count]; i++)
	{
		NSString *subString = [array objectAtIndex:i];
		NSArray *subArr = [subString componentsSeparatedByString:@"="];
		if ([subArr count] >= 2)
		{
			NSString *key = [subArr objectAtIndex:0];
			NSString *value = [subArr objectAtIndex:1];
			if ([subArr count] > 2)
			{
				value = [subString substringFromIndex:[key length] + 1];
			}
			if (key && value)
			{
				[dict setObject:value forKey:key];
			}
		}
	}
	return [dict autorelease];
}

+ (NSDate *)dateFromString:(NSString *)dateString
{
    NSDateFormatter *format = [[NSDateFormatter alloc] init];
    [format setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSDate *destDate = [format dateFromString:dateString];
    [format release];
    return destDate;
}

+ (NSString *)stringFromDate:(NSDate *)date
{
    NSDateFormatter *format = [[NSDateFormatter alloc] init];
    [format setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSString *destDateString = [format stringFromDate:date];
    [format release];
    return destDateString;
}

+ (BOOL)isTabbarHide
{
    BOOL isHide = NO;
    NSArray *windows = [UIApplication sharedApplication].windows;
	if ([windows count] > 0)
	{
        UIWindow * window =  (UIWindow *)[windows objectAtIndex:0];
        DashboardController *ctrl = (DashboardController *)window.rootViewController;
        isHide = ctrl.customBar.hidden;
	}
    return isHide;
}

+ (NSString *)currentPlatform
{
    BOOL isIPad = [[[UIDevice currentDevice] model] hasPrefix:@"iPad"];
	BOOL isAppSupportIPad = NO;
#if defined(__IPHONE_3_2) && (__IPHONE_3_2 <= __IPHONE_OS_VERSION_MAX_ALLOWED)
	if ([[UIDevice currentDevice] respondsToSelector:@selector(userInterfaceIdiom)]) {
		isAppSupportIPad = (UIUserInterfaceIdiomPad == [UIDevice currentDevice].userInterfaceIdiom) ? YES : NO;
	}
#endif
    if (isIPad && !isAppSupportIPad) {
        return @"iPhone";
    }
    return [[UIDevice currentDevice] model];
}

+ (CGFloat)viewHeightWithStatusBar:(BOOL)isStatusBarShown navBar:(BOOL)hasNavBar tabBar:(BOOL)hasTabBar otherExcludeHeight:(CGFloat)excludeHeight
{
    CGRect rt = [UIScreen mainScreen].bounds;
    CGFloat height = rt.size.height;
    
    int tabHeight = 49;
    int navHeight = UIInterfaceOrientationIsPortrait([UIApplication sharedApplication].statusBarOrientation) ? 44 : 33;
    int statusHeight = 20;
    
    if (isStatusBarShown)
        height -= statusHeight;
    if (hasNavBar)
        height -= navHeight;
    if (hasTabBar)
        height -= tabHeight;
    height -= excludeHeight;
    return height;
}

+ (void)clearGroupTableBgColor:(UITableView *)table
{
    [self setGroupTable:table withBgColor:[UIColor clearColor]];
}

+ (void)setGroupTableDefaultBgColor:(UITableView *)table
{
    [self setGroupTable:table withBgColor:[self defaultBgColor]];
}

+ (void)setGroupTable:(UITableView *)table withBgColor:(UIColor *)color
{
    UIView *view = [[[UIView alloc] initWithFrame:table.bounds] autorelease];
    view.backgroundColor = color;
    table.backgroundView = view;
    table.backgroundColor = [UIColor clearColor];
}

+ (UIColor *)defaultBgColor
{
    return [UIColor colorWithRed:0.961 green:0.961 blue:0.961 alpha:1];
}

+ (UIColor *)colorWithHexRGB:(NSString *)rgbStr
{
    if ([rgbStr length] < 6) {
        return nil;
    }
//    NSString *aStr = [argbStr substringWithRange:NSMakeRange(0, 2)];
    NSString *rStr = [rgbStr substringWithRange:NSMakeRange(0, 2)];
    NSString *gStr = [rgbStr substringWithRange:NSMakeRange(2, 2)];
    NSString *bStr = [rgbStr substringWithRange:NSMakeRange(4, 2)];
    unsigned int rColor,gColor,bColor;
//    [[NSScanner scannerWithString:aStr] scanHexInt:&aColor];
    [[NSScanner scannerWithString:rStr] scanHexInt:&rColor];
    [[NSScanner scannerWithString:gStr] scanHexInt:&gColor];
    [[NSScanner scannerWithString:bStr] scanHexInt:&bColor];
    
    return [UIColor colorWithRed:rColor/255.0 green:gColor/255.0 blue:bColor/255.0 alpha:1.0];
}

+ (void)autoLayoutLabelsInLine:(NSArray *)labels
{
    CGSize textSize;
    CGRect rc;
    CGPoint ori;
    for (int i = 0; i < [labels count]; i++) {
        id obj = [labels objectAtIndex:i];
        if ([obj isKindOfClass:[UILabel class]]) {
            UILabel *lbl = (UILabel *)obj;
            
            textSize = [lbl.text sizeWithFont:lbl.font constrainedToSize:CGSizeMake(300, 300)];
            if (i == 0) {
                ori = lbl.frame.origin;
            }
                        
            rc = lbl.frame;
            rc.origin = ori;
            rc.size.width = textSize.width;
            lbl.frame = rc;
            
            float margin = 5.0;
            ori = CGPointMake(CGRectGetMaxX(rc) + margin, ori.y);
        }
    }
}

#pragma mark -
#pragma mark UILocalNotification
+ (NSArray *)localNotificationByAppIdentifier:(NSString *)appIdentifier activityid:(NSString *)activityId
{
    NSString *notiIdentifier = [NSString stringWithFormat:@"%@_%@", appIdentifier, activityId];
    NSMutableArray *arrNoti = [NSMutableArray arrayWithCapacity:2];
#ifdef __IPHONE_4_0
    UIApplication *app = [UIApplication sharedApplication];
    for (UILocalNotification *noti in app.scheduledLocalNotifications) {
        if ([[noti.userInfo objectForKey:@"identifier"] isEqualToString:notiIdentifier]) {
            [arrNoti addObject:noti];
        }
    }
#endif
    if ([arrNoti count] > 0) {
        return [NSArray arrayWithArray:arrNoti];
    }
    else {
        return nil;
    }
}

+ (BOOL)cancelLocalNotificationWithAppIdentifier:(NSString *)appIdentifier activityid:(NSString *)activityId
{
    NSArray *arrNoti = [self localNotificationByAppIdentifier:appIdentifier activityid:activityId];
    if (arrNoti != nil) {
        for (UILocalNotification *noti in arrNoti) {
            [[UIApplication sharedApplication] cancelLocalNotification:noti];
        }
        return YES;
    }
    return NO;
}

+ (void)cancelLocalNotification:(UILocalNotification *)noti
{
    if (noti != nil) {
        [[UIApplication sharedApplication] cancelLocalNotification:noti];
    }
}

+ (BOOL)setNewServersLocalNotification:(NSDate *)openDate title:(NSString *)title appIdentifier:(NSString *)appIdentifier activityid:(NSString *)activityId appName:(NSString *)appName
{
    NSArray *arrNoti = [self localNotificationByAppIdentifier:appIdentifier activityid:activityId];
    if (arrNoti != nil) {
        return YES;
    }
    
#ifdef __IPHONE_4_0
    //系统会将alertBody相同的提醒合并，所以alertBody里面要加游戏名加以区分
    NSString *notiIdentifier = [NSString stringWithFormat:@"%@_%@", appIdentifier, activityId];
    NSMutableArray *arrAlertBody = [NSMutableArray arrayWithCapacity:2];
    NSMutableArray *arrFireDate = [NSMutableArray arrayWithCapacity:2];
    
    NSInteger minutesBeforeOpenDate = [[NSDate date] minutesBeforeDate:openDate];
    if (minutesBeforeOpenDate <= 0) {
        return NO;
    }
    else if (minutesBeforeOpenDate >= 30){
        [arrAlertBody addObject:[NSString stringWithFormat:@"30分钟开服提醒:您关注的游戏%@，将于30分钟后开启新服！赶快热身一下，准备投入战斗吧。", appName]];
        [arrAlertBody addObject:[NSString stringWithFormat:@"开服提醒：您关注的游戏%@，刚刚已开启了新服！出发的号角已经响起，快来一起踏上新征途吧。", appName]];
        [arrFireDate addObject:[openDate dateBySubtractingMinutes:30]];
        [arrFireDate addObject:openDate];
    }
    else {
        [arrAlertBody addObject:[NSString stringWithFormat:@"开服提醒：您关注的游戏%@，刚刚已开启了新服！出发的号角已经响起，快来一起踏上新征途吧。", appName]];
        [arrFireDate addObject:openDate];
    }
    
    for (int i=0; i<[arrFireDate count]; i++) {
        UILocalNotification *notification = [[UILocalNotification alloc] init];
        notification.fireDate = [arrFireDate objectAtIndex:i];
        notification.timeZone = [NSTimeZone defaultTimeZone];
        notification.soundName = UILocalNotificationDefaultSoundName;
        notification.repeatInterval = 0;
        notification.alertBody = [arrAlertBody objectAtIndex:i];
        NSDictionary *dic = [NSDictionary dictionaryWithObjectsAndKeys:appIdentifier, KEY_LOCAL_NOTIFICATION_APPID, title, @"title", notiIdentifier, @"identifier", nil];
        notification.userInfo = dic;
        [[UIApplication sharedApplication] scheduleLocalNotification:notification];
        [notification release];
    }
    
#endif
    
	return YES;
}

#pragma mark -
+ (NSString *)packRecommendIconsStr:(NSArray *)array
{
    if (array == nil) {
        return nil;
    }
    
    NSString *strIcons = nil;
    int count = [array count];
    NSMutableArray *arrIcons = [NSMutableArray arrayWithCapacity:count];
    for (int i=0;i<count; i++) {
        NSDictionary *dictionary = [array objectAtIndex:i];
        NSString *bgColor = [dictionary objectForKey:KEY_RI_BGCOLOR];
        NSString *fontColor = [dictionary objectForKey:KEY_RI_FONTCOLOR];
        NSString *name = [dictionary objectForKey:KEY_RI_NAME];
        
        [arrIcons addObject:[NSString stringWithFormat:@"%@$%@$%@", bgColor, fontColor, name]];
    }
    if ([arrIcons count] > 0) {
        strIcons = [arrIcons componentsJoinedByString:@","];
    }
    
    return strIcons;
}

+ (NSArray *)unPackRecommendIconsStr:(NSString *)iconsStr
{
    NSArray *arrIcons = [iconsStr componentsSeparatedByString:@","];
    int count = [arrIcons count];
    if (count <= 0) 
        return nil;
    
    NSMutableArray *result = [NSMutableArray arrayWithCapacity:count];
    for (int i=0; i<count; i++) {
        NSString *strValue = [arrIcons objectAtIndex:i];
        NSArray *arrValue = [strValue componentsSeparatedByString:@"$"];
        if ([arrValue count] == 3) {
            NSDictionary *dict = [NSDictionary dictionaryWithObjects:arrValue forKeys:[NSArray arrayWithObjects:KEY_RI_BGCOLOR,KEY_RI_FONTCOLOR, KEY_RI_NAME, nil]];
            [result addObject:dict];
        }
    }
    
    if ([result count] > 0) {
        return [NSArray arrayWithArray:result];
    }
    else {
        return nil;
    }
}

+ (UITableViewCell *)defaultTitleCellInSection:(id)target action:(SEL)action;
{
    UITableViewCell *cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:@"sectionTitle"] autorelease];
    
    UIImage *backgrdImage = [UIImage imageNamed:@"huodong_h2_bg.png"];
    UIImageView *backgrdView = [[[UIImageView alloc] initWithFrame:cell.frame] autorelease];
    backgrdView.image = backgrdImage;
    cell.backgroundView = backgrdView;
    
    cell.accessoryType = UITableViewCellAccessoryNone;
	cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.detailTextLabel.text = nil;
    
    cell.textLabel.backgroundColor = [UIColor clearColor];
    cell.textLabel.textColor = [UIColor colorWithRed:0x14/255.0 green:0x7f/255.0 blue:0xb5/255.0 alpha:1];
    cell.textLabel.font = [UIFont boldSystemFontOfSize:16];
    
    if (target!= nil && action != NULL) {
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        btn.frame = CGRectMake(0, 0, 54, 24);
        btn.tag = 1000;
        [btn setTitle:@"更多+" forState:UIControlStateNormal];
        [btn setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
        [btn addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
        btn.titleLabel.font = [UIFont boldSystemFontOfSize:13];
        cell.accessoryView = btn;
    }
    else {
        cell.accessoryView = nil;
    }
    
    return cell;
}

+ (int)currentCarrier
{    
    int ret = 0;
    CTTelephonyNetworkInfo *info = [[[CTTelephonyNetworkInfo alloc] init] autorelease];
    CTCarrier *carrier = [info subscriberCellularProvider];
    if (carrier == nil) {
        return ret;
    }
    NSString *code = [carrier mobileNetworkCode];
    if (code == nil) {
        return ret;
    }
    switch ([code intValue]) {
        case 0:
        case 2:
        case 7:
        case 20:
            ret = CARRIER_MOBILE;
            break;
        case 1:
        case 6:
            ret = CARRIER_UNICOM;
            break;
        case 3:
        case 5:
            ret = CARRIER_TELECOM;
            break;
        default:
            break;
    }
    return ret;
}

+ (BOOL)showWithCarrier:(int)carrier
{
    return (carrier==CARRIER_ALL)||(carrier&[self currentCarrier]);
}

+ (void)showBarItemForCallBack:(UIViewController *)controller
{
    BOOL canOpenURL = NO;
    NSString *strCallbackUrl = [UserData sharedInstance].thirdPartyUrlScheme;
    
    if ([strCallbackUrl length] > 0) {
        NSRange range = [strCallbackUrl rangeOfString:@"://"];
        if (range.location == NSNotFound) {
            strCallbackUrl = [strCallbackUrl stringByAppendingFormat:@"://"];
        }
        
        if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:strCallbackUrl]]) {
            canOpenURL = YES;
        }
    }

    //
    if (canOpenURL) {
        id delegate = [UIApplication sharedApplication].delegate;
        UIBarButtonItem *exchangeRecordsBtnItem = [UIBarButtonItem rightItemWithCustomStyle:@"回游戏" target:delegate action:@selector(launchedCallBackUrlScheme)];
        controller.navigationItem.leftBarButtonItem = exchangeRecordsBtnItem;
    }
    else {
        controller.navigationItem.leftBarButtonItem = nil;
    }
}

#pragma mark -
//跳转到活动详情
+ (void)pushActivityDetailCtrl:(NSString *)appIdentifier activityId:(int)activityId activityUrl:(NSString *)activityUrl activityTitle:(NSString *)activityTitle navigationController:(UINavigationController *)navigationController
{    
    ActivityDetailCtrl *ctrl = [[[ActivityDetailCtrl alloc] init] autorelease];
    ctrl.customTitle = ([activityTitle length] > 0) ? activityTitle : @"活动详情";
    ctrl.appIdentifier = appIdentifier;
    ctrl.contentUrl = activityUrl;
    ctrl.activityId = activityId;
    ctrl.hidesBottomBarWhenPushed = YES;
    [navigationController pushViewController:ctrl animated:YES];
}

//跳转到游戏详情
+ (void)pushGameDetailController:(NSString *)appIdentifier gameName:(NSString *)gameName navigationController:(UINavigationController *)navigationController
{   
    GameDetailController *ctrl = [GameDetailController gameDetailWithIdentifier:appIdentifier gameName:gameName];
    ctrl.hidesBottomBarWhenPushed = YES;
    [navigationController pushViewController:ctrl animated:YES];
}

+ (void)pushMoreGameDetailController:(NSString *)appIdentifier gameName:(NSString *)gameName navigationController:(UINavigationController *)navigationController moreDetail:(NSString *)more
{
    GameDetailController *ctrl = [GameDetailController gameDetailWithIdentifier:appIdentifier gameName:gameName];
    ctrl.hidesBottomBarWhenPushed = YES;
    [navigationController pushViewController:ctrl animated:YES];
    if (more != nil) {
        [ctrl performSelector:@selector(gotoGameMoredetail:) withObject:more afterDelay:1.5];
//        [ctrl gotoGameMoredetail:more];
    }
}

//跳转到游戏详情活动页
+ (void)pushGameDetailActivity:(NSString *)appIdentifier navigationController:(UINavigationController *)navigationController
{
    ActivityCommonCtrl *ctrl = [[[ActivityCommonCtrl alloc] init] autorelease];
    ctrl.bNeedShowIcon = NO;
    ctrl.identifier = appIdentifier;
    ctrl.tableStyle = UITableViewStylePlain;
    [navigationController pushViewController:ctrl animated:YES];
}
//跳转到游戏详情攻略页
+ (void)pushGameDetailStrategy:(NSString *)url navigationController:(UINavigationController *)navigationController
{
    GameDetailWebCtrl *webViewCtr = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:url];
    webViewCtr.customTitle = @"攻略";
    [navigationController pushViewController:webViewCtr animated:YES];
}
//跳转到游戏详情论坛页
+ (void)pushGameDetailForum:(NSString *)url navigationController:(UINavigationController *)navigationController
{
    GameDetailWebCtrl *webViewCtr = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:url];
    webViewCtr.customTitle = @"论坛";
    [navigationController pushViewController:webViewCtr animated:YES];
}

//跳转到游戏专题
+ (void)pushGameTopicController:(int)topicId navigationController:(UINavigationController *)navigationController
{
    GameTopicController *ctrl = [[[GameTopicController alloc] init] autorelease];
    ctrl.topicId = topicId;
    ctrl.hidesBottomBarWhenPushed = YES;
    [navigationController pushViewController:ctrl animated:YES];
}

//由SDK智能升级跳转而来
+ (void)presentGameDetailController:(NSString *)appIdentifier viewController:(UIViewController *)viewController
{
    GameDetailController *detail = [GameDetailController gameDetailForSDKUpgradeWithIdentifier:appIdentifier gameName:nil];
    CustomNavController *navCtrl = [[[CustomNavController alloc] initWithRootViewController:detail] autorelease];
    [navCtrl customizeNavigationBar];
    [viewController presentModalViewController:navCtrl animated:YES];
}

#pragma mark -
//获取所有已游戏信息
+ (NSDictionary *)allInstalledAppInfo
{
    NSDictionary *allAppsInfo = nil;
    NSDictionary *cacheDict = nil;
#if TARGET_IPHONE_SIMULATOR
    cacheDict = [MIUtility dicForAllIntalledAppInfo];
    allAppsInfo = cacheDict;
#else
    NSString *cacheFileName = @"com.apple.mobile.installation.plist";
    NSString *relativeCachePath = [[@"Library" stringByAppendingPathComponent: @"Caches"] stringByAppendingPathComponent: cacheFileName];
    NSString *path = [@"/var/mobile" stringByAppendingPathComponent: relativeCachePath];
    NSLog(@"%@", path);
    
    BOOL isDir = NO;

    
    if ([[NSFileManager defaultManager] fileExistsAtPath: path isDirectory: &isDir] && !isDir) {
        cacheDict = [NSDictionary dictionaryWithContentsOfFile: path];
    }
    if (!cacheDict) {
        return nil;
    }
    allAppsInfo = [cacheDict objectForKey: @"User"];

#endif
    return allAppsInfo;
}

//返回所有应用的基本信息数组
+ (NSArray *)allInstalledAppInfoList
{
#if TARGET_IPHONE_SIMULATOR
    NSArray *arr = [MIUtility allInstalledAppInfo];
#else
    NSDictionary *allInstallAppInfo = [CommUtility allInstalledAppInfo];
    NSArray *arr = [allInstallAppInfo allValues];
    //某些固件无法读取“com.apple.mobile.installation.plist”
    if (!arr) {
        arr = [MIUtility allInstalledAppInfo];
    }
#endif
    return arr;
}
//返回所有应用的identifier数组
+ (NSArray *)allInstalledAppIdentifierArr
{
    NSMutableArray *retArr = [NSMutableArray array];
    NSArray *arr = [self allInstalledAppInfoList];
    for (NSDictionary *dict in arr)
    {
        NSString *idenitifier = [dict objectForKey:@"CFBundleIdentifier"];
        [retArr addObject:idenitifier];
    }
    return retArr;
}
//根据identifier获取游戏version
+ (NSString *)getInstallAppVerByIdentifier:(NSString *)appIdentifier
{
    NSString *ver = nil;
    
    NSArray *arr = [self allInstalledAppInfoList];
    for (NSDictionary *dict in arr)
    {
        NSString *idenitifier = [dict objectForKey:@"CFBundleIdentifier"];
        if ([appIdentifier isEqualToString:idenitifier]) {
            ver = [dict objectForKey:@"CFBundleVersion"];
            break;
        }
    }
    
    return ver;
}
//根据identifier获取游戏shortVersion
+ (NSString *)getInstallAppShortVerByIdentifier:(NSString *)appIdentifier
{
    NSString *ver = nil;
    
    NSArray *arr = [self allInstalledAppInfoList];
    for (NSDictionary *dict in arr)
    {
        NSString *idenitifier = [dict objectForKey:@"CFBundleIdentifier"];
        if ([appIdentifier isEqualToString:idenitifier]) {
            ver = [dict objectForKey:@"CFBundleShortVersionString"];
            break;
        }
    }
    
    return ver;
}

//获取游戏安装目录
+ (NSString *)getInstallPathByCacheFile:(NSString *)appIdentifier
{
    NSDictionary *allAppsInfo = [self allInstalledAppInfo];
    NSDictionary *appInfo = [allAppsInfo objectForKey:appIdentifier];
    NSString *appPath = [appInfo objectForKey:@"Path"];
    NSLog(@"%@", appPath);
    
    return appPath;
}

+ (NSString *)getInstallPathByMILib:(NSString *)appIdentifier
{
    NSString *appPath = nil;
    
    NSArray *arr = [MIUtility allInstalledAppInfo];
    for (NSDictionary *dict in arr)
    {
        NSString *idenitifier = [dict objectForKey:@"CFBundleIdentifier"];
        if ([appIdentifier isEqualToString:idenitifier]) {
            appPath = [dict objectForKey:@"Path"];
        }
    }
    
    return appPath;
}

//将渠道id文件写入安装的游戏
+ (BOOL)copyChannelFileToInstalledApp:(NSString *)appIdentifier
{
    NSLog(@"start copy channelId File To Installed Application....");
	NSString* gameCenterDir = [[NSBundle mainBundle] resourcePath];
	NSString* filePathInGameCenter = [gameCenterDir stringByAppendingPathComponent:FILENAME_CHANNEL_CFG];
	BOOL isFileExist = [[NSFileManager defaultManager] fileExistsAtPath:filePathInGameCenter];
    if (!isFileExist) {
        NSLog(@"%@ not exist in gamecenter", FILENAME_CHANNEL_CFG);
        return NO;
    }
    
    NSString *appInstallPath = [self getInstallPathByCacheFile:appIdentifier];
    //某些固件无法读取“com.apple.mobile.installation.plist”
    if (!appInstallPath) {
        appInstallPath = [self getInstallPathByMILib:appIdentifier];
    }
    
    if (!appInstallPath) {
        NSLog(@"can not find applicaton install path");
        return NO;
    }
    
    NSError *err = nil;
    NSString* filePathInAPP = [appInstallPath stringByAppendingPathComponent:FILENAME_CHANNEL_CFG];
    [[NSFileManager defaultManager] removeItemAtPath:filePathInAPP error:NULL];
    BOOL bCopy = [[NSFileManager defaultManager] copyItemAtPath:filePathInGameCenter toPath:filePathInAPP error:&err];
    if (bCopy)
        NSLog(@"copy channelId File To Installed Application succeed!");
    else
        NSLog(@"copy channelId File To Installed Application fail!");
    
    return bCopy;
}

+ (NSString *)getChannelIdFromCfg
{
    static NSString* s_strRet = nil;//DEFAULT_CHANNEL_ID;
	if (s_strRet != nil) {
		return s_strRet;
	}
	
	s_strRet = DEFAULT_CHANNEL_ID;
	
	NSString* strAppDir = [[NSBundle mainBundle] resourcePath];
	NSString* strFileInApp = [strAppDir stringByAppendingPathComponent:FILENAME_CHANNEL_CFG];
	
	NSString* strDocDir = [self getDocumentPath];
	NSString* strFileInDoc = [strDocDir stringByAppendingPathComponent:FILENAME_CHANNEL_CFG];
	
	
	BOOL isFileExistInApp = [[NSFileManager defaultManager] fileExistsAtPath:strFileInApp];
	BOOL isFileExistInDoc = [[NSFileManager defaultManager] fileExistsAtPath:strFileInDoc];
	NSDictionary* dicChannel = nil;
	
	NSError* err = nil;
	BOOL bCopy = NO;
	if (isFileExistInApp)
	{
		dicChannel = [NSDictionary dictionaryWithContentsOfFile:strFileInApp];
		
		if (!isFileExistInDoc) {
			bCopy = [[NSFileManager defaultManager] copyItemAtPath:strFileInApp toPath:strFileInDoc error:&err];
		}
		else {
			//compare file time
			NSDictionary* dicFilePropertyInApp = [[NSFileManager defaultManager] attributesOfItemAtPath:strFileInApp error:&err];
			NSDictionary* dicFilePropertyInDoc = [[NSFileManager defaultManager] attributesOfItemAtPath:strFileInDoc error:&err];
			NSDate* dateOfFileInApp = [dicFilePropertyInApp objectForKey:NSFileModificationDate];
			NSDate* dateOfFileInDoc = [dicFilePropertyInDoc objectForKey:NSFileModificationDate];
			if (![dateOfFileInApp isEqualToDate:dateOfFileInDoc]) {
				[[NSFileManager defaultManager] removeItemAtPath:strFileInDoc error:NULL];
				bCopy = [[NSFileManager defaultManager] copyItemAtPath:strFileInApp toPath:strFileInDoc error:&err];
			}
		}
	}
	else if (isFileExistInDoc) {
		dicChannel = [NSDictionary dictionaryWithContentsOfFile:strFileInDoc];
	}
	
	NSString* strId = [dicChannel objectForKey:KEY_CHANNEL_ID];
	if ([strId length] > 0) {
		s_strRet = [strId copy];
	}
	
	NSString* strLog = isFileExistInApp ? strFileInApp : (isFileExistInDoc ? strFileInDoc : @"");
	NDLOG(@"getChannelIdFromCfgFile--------------------\n %@ \n channelId = %@\n", strLog, s_strRet);
	NDLOG(@"copy error %@ \n bRet = %d", [err description], bCopy);
	return s_strRet;
}

//是否使用wifi
+ (BOOL)isWifiNetWork
{
    BOOL isWifi = ([[NdCPReachability sharedReachability] localWiFiConnectionStatus] == NdCP_ReachableViaWiFiNetwork);
    return isWifi;
}

//网络是否可用
+ (BOOL)isNetWorkReachable
{
    BOOL isReachable = ([[NdCPReachability sharedReachability] internetConnectionStatus] != NdCP_NotReachable);
    return isReachable;
}
//是否是ios7
+ (BOOL)isIOS7
{
    return ([[UIDevice currentDevice] systemVersion].floatValue >= 7);

}

@end
