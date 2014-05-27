//
//  CommUtility.h
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-29.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


#define KEY_RI_BGCOLOR      @"BGColor"
#define KEY_RI_FONTCOLOR    @"FontColor"
#define KEY_RI_NAME         @"Name"

#define CARRIER_ALL         0xFF
#define CARRIER_MOBILE      0x01
#define CARRIER_UNICOM      0x02
#define CARRIER_TELECOM     0x04

#define KEY_LOCAL_NOTIFICATION_APPID    @"appIdentifier"


@interface CommUtility : NSObject 
+ (NSString *)getDocumentPath;
+ (BOOL)openApp:(NSString *)identifier;
+ (NSString *)readableFileSize:(long)size;
+ (NSString *)readableDownloadNumber:(int)num;
+ (NSString *)dateFromLastModified:(NSString *)lastModified;
+ (BOOL)isSamedayDay1:(NSDate *)date1 date2:(NSDate *)date2;
+ (NSDictionary *)dictionaryFromUrlQueryComponents:(NSString *)query;

+ (NSDate *)dateFromString:(NSString *)dateString;
+ (NSString *)stringFromDate:(NSDate *)date;

+ (BOOL)isTabbarHide;
+ (NSString *)currentPlatform;

+ (CGFloat)viewHeightWithStatusBar:(BOOL)isStatusBarShown navBar:(BOOL)hasNavBar tabBar:(BOOL)hasTabBar otherExcludeHeight:(CGFloat)excludeHeight;

+ (void)clearGroupTableBgColor:(UITableView *)table;
+ (void)setGroupTableDefaultBgColor:(UITableView *)table;
+ (void)setGroupTable:(UITableView *)table withBgColor:(UIColor *)color;

+ (UIColor *)defaultBgColor;
+ (UIColor *)colorWithHexRGB:(NSString *)rgbStr;

+ (void)autoLayoutLabelsInLine:(NSArray *)labels;

+ (NSArray *)localNotificationByAppIdentifier:(NSString *)appIdentifier activityid:(NSString *)activityId;
+ (BOOL)setNewServersLocalNotification:(NSDate *)openDate title:(NSString *)title appIdentifier:(NSString *)appIdentifier activityid:(NSString *)activityId appName:(NSString *)appName;
+ (BOOL)cancelLocalNotificationWithAppIdentifier:(NSString *)appIdentifier activityid:(NSString *)activityId;
+ (void)cancelLocalNotification:(UILocalNotification *)noti;

//将接口下发的reCommendIcons值拼接成字符串
+ (NSString *)packRecommendIconsStr:(NSArray *)array;
+ (NSArray *)unPackRecommendIconsStr:(NSString *)iconsStr;

+ (UITableViewCell *)defaultTitleCellInSection:(id)target action:(SEL)action;

//判断运营商
+ (int)currentCarrier;
+ (BOOL)showWithCarrier:(int)carrier;

+ (void)showBarItemForCallBack:(UIViewController *)controller;

+ (void)pushActivityDetailCtrl:(NSString *)appIdentifier activityId:(int)activityId activityUrl:(NSString *)activityUrl activityTitle:(NSString *)activityTitle navigationController:(UINavigationController *)navigationController;
+ (void)pushGameDetailController:(NSString *)appIdentifier gameName:(NSString *)gameName navigationController:(UINavigationController *)navigationController;
+ (void)pushMoreGameDetailController:(NSString *)appIdentifier gameName:(NSString *)gameName navigationController:(UINavigationController *)navigationController moreDetail:(NSString *)more;

+ (void)pushGameDetailActivity:(NSString *)appIdentifier navigationController:(UINavigationController *)navigationController;
+ (void)pushGameDetailStrategy:(NSString *)url navigationController:(UINavigationController *)navigationController;
+ (void)pushGameDetailForum:(NSString *)url navigationController:(UINavigationController *)navigationController;
+ (void)pushGameTopicController:(int)topicId navigationController:(UINavigationController *)navigationController;

+ (void)presentGameDetailController:(NSString *)appIdentifier viewController:(UIViewController *)viewController;

+ (NSDictionary *)allInstalledAppInfo;
+ (NSArray *)allInstalledAppInfoList;
+ (NSArray *)allInstalledAppIdentifierArr;
+ (NSString *)getInstallAppVerByIdentifier:(NSString *)appIdentifier;
+ (NSString *)getInstallAppShortVerByIdentifier:(NSString *)appIdentifier;
+ (NSString *)getInstallPathByCacheFile:(NSString *)appIdentifier;
+ (NSString *)getInstallPathByMILib:(NSString *)appIdentifier;

+ (BOOL)copyChannelFileToInstalledApp:(NSString *)appIdentifier;
+ (NSString *)getChannelIdFromCfg;

//network
+ (BOOL)isWifiNetWork;      //是否使用wifi
+ (BOOL)isNetWorkReachable; //网络是否可用

//system version
+ (BOOL)isIOS7;     //是否是ios7
@end
