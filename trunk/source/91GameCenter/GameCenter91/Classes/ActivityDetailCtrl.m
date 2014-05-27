    //
//  ActivityDetailCtrl.m
//  GameCenter91
//
//  Created by  hiyo on 12-9-4.
//  Copyright 2012 Nd. All rights reserved.
//

#import "ActivityDetailCtrl.h"
#import "GiftItem.h"
#import "ActivityInfo.h"
#import "CommUtility.h"
#import "MBProgressHUD.h"
#import <NdComPlatform/NdComPlatform.h>
#import <NdComPlatform/NdCPNotifications.h>
#import "CommUtility.h"
#import "Notifications.h"
#import "ActivityAndTaskJumpBar.h"
#import "GameDetailWebCtrl.h"
#import "AppDescriptionInfo.h"
#import "UserData.h"
#import "OptionProtocols.h"
#import "RequestorAssistant.h"
#import "ReportCenter.h"

@interface  ActivityDetailCtrl()<GetAppDescriptionInfoProtocol>
@property (nonatomic, retain) UIWebView *webView;
@property (nonatomic, retain) NSString *notLoginUrl;
@property (nonatomic, retain) AppDescriptionInfo *appDescInfo;

- (void)loadWebView:(NSString *)urlStr;
- (void)addJumpBar:(AppDescriptionInfo *)info;
@end


@implementation ActivityDetailCtrl
@synthesize contentUrl, activityId, appIdentifier;
@synthesize webView;
@synthesize notLoginUrl;

- (id)init
{
    self = [super init];
    if (self) {
        self.contentUrl = nil;
        self.appIdentifier = nil;
        self.webView = nil;
        self.notLoginUrl = nil;
        self.appDescInfo = nil;
    }
    return self;
}

- (void)dealloc {
    self.contentUrl = nil;
    self.appIdentifier = nil;
    self.webView = nil;
    self.notLoginUrl = nil;
    self.appDescInfo = nil;
    [super dealloc];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:NO otherExcludeHeight:0];
    self.webView = [[[UIWebView alloc] initWithFrame:CGRectMake(0, 0, 320.0, height)] autorelease];
    webView.delegate = self;
    webView.dataDetectorTypes = UIDataDetectorTypeLink;
    [self.view addSubview:webView];
    
    //获取游戏简介
//    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    if ([self.appIdentifier length] > 0) {
        NSNumber *ret = [RequestorAssistant requestAppDesciptionInfo:self.appIdentifier delegate:self];
        if ([ret intValue] < 0) {
            [MBProgressHUD hideHUDForView:self.view animated:YES];
        }
    }

    NSString *urlstr = self.contentUrl;
    NSString *sid = [[NdComPlatform defaultPlatform] sessionId];
    sid = sid ? sid : @"";
    
    NSRange range = [urlstr rangeOfString:@"?"];
    if (range.location != NSNotFound) {
        [self loadWebView:[urlstr stringByAppendingFormat:@"&SessionId=%@", sid]];
        self.notLoginUrl = [urlstr stringByAppendingString:@"&SessionId="];
    }else {
        [self loadWebView:[urlstr stringByAppendingFormat:@"?SessionId=%@", sid]];
        self.notLoginUrl = [urlstr stringByAppendingString:@"?SessionId="];
    }
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(loginFinished:) name:kNdCPLoginNotification object:nil];
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

#pragma mark -
#pragma mark Action
- (NSString*)decodeStringFromDictionary:(NSDictionary*)dictionary ByKey:(NSString*)key
{
	return [[dictionary objectForKey:key] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
}

- (void)RegisterNotify:(NSDictionary *)kVPairs
{ 
    if ([[NdComPlatform defaultPlatform] isLogined]) {
        //开服提醒
        NSString *title = [self decodeStringFromDictionary:kVPairs ByKey:@"Title"];
//        NSString *content = [self decodeStringFromDictionary:kVPairs ByKey:@"Content"];
        NSString *time = [self decodeStringFromDictionary:kVPairs ByKey:@"NotifyTime"];
        NSString *identifier = [self decodeStringFromDictionary:kVPairs ByKey:@"Identifier"];
        NSString *actId = [self decodeStringFromDictionary:kVPairs ByKey:@"ActivityId"];
        NSDate *openDate = [CommUtility dateFromString:time];
        //没简介则不提醒
        if (self.appDescInfo == nil) {
            return;
        }
        [CommUtility setNewServersLocalNotification:openDate title:title appIdentifier:identifier activityid:actId appName:self.appDescInfo.appName];
        
        //统计
        [ReportCenter report:ANALYTICS_EVENT_15061 label:identifier];
    }
    else {
        NDCP_CHECK_LOGIN_ERROR_AND_SHOULD_ALERT(@"亲，你现在还未登录，设置开服提醒。马上用91账号登录吧！")
    }
}

- (void)CancelNotify:(NSDictionary *)kVPairs
{
    //取消开服提醒
//    NSString *title = [self decodeStringFromDictionary:kVPairs ByKey:@"Title"];
//    NSString *content = [self decodeStringFromDictionary:kVPairs ByKey:@"Content"];
    //    NSString *time = [self decodeStringFromDictionary:kVPairs ByKey:@"NotifyTime"];
    NSString *identifier = [self decodeStringFromDictionary:kVPairs ByKey:@"Identifier"];
    NSString *actId = [self decodeStringFromDictionary:kVPairs ByKey:@"ActivityId"];
    [CommUtility cancelLocalNotificationWithAppIdentifier:identifier activityid:actId];
    
    //统计
    [ReportCenter report:ANALYTICS_EVENT_15062 label:identifier];
}

- (void)CopyText:(NSDictionary *)kVPairs
{
    //复制
    NSString *content = [self decodeStringFromDictionary:kVPairs ByKey:@"Content"];
    NSString *sucInfo = [self decodeStringFromDictionary:kVPairs ByKey:@"SuccessInfo"];
    NSString *failInfo = [self decodeStringFromDictionary:kVPairs ByKey:@"FailureInfo"];
    if ([sucInfo length] > 0) {
        UIPasteboard *board = [UIPasteboard generalPasteboard];
        [board setString:content];
        
        [MBProgressHUD showHintHUD:@"复制成功" message:nil hideAfter:DEFAULT_TIP_LAST_TIME];
    }
    else if ([failInfo length] > 0) {
        [MBProgressHUD showHintHUD:@"复制失败" message:nil hideAfter:DEFAULT_TIP_LAST_TIME];
    }
}

- (void)GetCodeSuccess:(NSDictionary *)kVPairs
{
    if ([[NdComPlatform defaultPlatform] isLogined]) {
        //抢包成功
        [[NSNotificationCenter defaultCenter] postNotificationName:kGC91GetCodeSuccessNotification object:kVPairs];
        
        //统计
        [ReportCenter report:ANALYTICS_EVENT_15060 label:self.appIdentifier];
    }
    else {
        NDCP_CHECK_LOGIN_ERROR_AND_SHOULD_ALERT(@"亲，未登录不能领取礼包哦。快登录91帐号，海量礼包等您领！")
    }
}

- (void)GetCodeNotLogin:(NSDictionary *)kVPairs
{
    if (![[NdComPlatform defaultPlatform] isLogined]) {
        NDCP_CHECK_LOGIN_ERROR_AND_SHOULD_ALERT(@"亲，未登录不能领取礼包哦。快登录91帐号，海量礼包等您领！！")
    }
}

- (void)loadWebView:(NSString *)urlStr
{
    NSMutableString *urlString = [NSMutableString stringWithString:urlStr];
    
    //如果是开服的详情页面，在详情的url后根据是否有设置开服提醒再加上&Notify=1 或 &Notify=0
    NSArray *arrNoti= [CommUtility localNotificationByAppIdentifier:self.appIdentifier activityid:[NSString stringWithFormat:@"%d", self.activityId]];
    if (arrNoti == nil) {
        [urlString appendString:@"&Notify=0"];
    }
    else {
        [urlString appendString:@"&Notify=1"];
    }

    NSURL *url = [NSURL URLWithString:urlString]; 
	NSMutableURLRequest *urlRequest = [[[NSMutableURLRequest alloc] initWithURL:url] autorelease]; 
	[webView loadRequest:urlRequest];
//	[activityIndicator startAnimating];
}

- (void)loginFinished:(NSNotification *)aNotification
{
    NSDictionary *dict = [aNotification userInfo];
	BOOL success = [[dict objectForKey:@"result"] boolValue];
    
    if (success) {
        NSString *sid = [[NdComPlatform defaultPlatform] sessionId];
        NSString *loginUrl = [notLoginUrl stringByAppendingString:sid];
        [webView stopLoading];
        [self loadWebView:loginUrl];
    }
}

#pragma mark - UIWebViewDelegate
- (BOOL)webView:(UIWebView*)webView shouldStartLoadWithRequest:(NSURLRequest*)request navigationType:(UIWebViewNavigationType)navigationType {	
    
    NSString *urlstr = [[request URL] absoluteString];
    //NSLog(@"URLString %@",urlstr);
    
    if (navigationType == UIWebViewNavigationTypeLinkClicked && ([[[request URL] scheme] isEqualToString:@"http"] || 
                                                                 [[[request URL] scheme] isEqualToString:@"https"])) {
        GameDetailWebCtrl *ctrl = [[[GameDetailWebCtrl alloc] init] autorelease];
        ctrl.hidesBottomBarWhenPushed = YES;
        [self.navigationController pushViewController:ctrl animated:YES];
        [ctrl loadWebView:urlstr];
        return NO;
    }
    else {
        NSRange range = [urlstr rangeOfString:@"about:blank?Do="];
        NSDictionary *kVPairs = [CommUtility dictionaryFromUrlQueryComponents:urlstr];
        if(range.location != NSNotFound) {
            NSString *action = [NSString stringWithFormat:@"%@:", [kVPairs valueForKey:@"about:blank?Do"]];
            SEL aSelector = NSSelectorFromString(action);
            if([self respondsToSelector:aSelector]) {
                [self performSelector:aSelector withObject:kVPairs];
                return NO;
            }
        }
    }
	
	return YES;
}

- (void)webViewDidStartLoad:(UIWebView *)webView {
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    [MBProgressHUD hideHUDForView:self.view animated:YES];
}

- (void)webView:(UIWebView *)webview didFailLoadWithError:(NSError *)error {
    [MBProgressHUD hideHUDForView:self.view animated:YES];    
    if ([error.domain isEqualToString:@"NSURLErrorDomain"] && error.code == NSURLErrorCancelled)
	{
		return;		//this error was cause by cancel a connection;
	}
    [MBProgressHUD showHintHUD:@"网络错误" message:[error localizedDescription] hideAfter:DEFAULT_TIP_LAST_TIME];
}

- (void)addJumpBar:(AppDescriptionInfo *)info
{
    int barType = (info == nil) ? TYPE_INCOMPATIBLE_GAME: TYPE_ACTIVITY_TO_GAME;
    
    ActivityAndTaskJumpBar *jumpBar = [[[ActivityAndTaskJumpBar alloc] initWithView:self.view jumpType:barType] autorelease];
    jumpBar.appInfo = info;

    CGRect rect = self.webView.frame;
    rect.size.height -= [jumpBar JumpBarHeight];
    self.webView.frame = rect;
    [self.view addSubview:jumpBar];
}

#pragma mark - 
- (void)operation:(GameCenterOperation *)operation getAppDesciptionInfoDidFinish:(NSError *)error appDesciptionInfo:(AppDescriptionInfo *)descriptionInfo
{
//    [MBProgressHUD hideHUDForView:self.view animated:YES];
    self.appDescInfo = descriptionInfo;
    //活动详情添加跳转条
    [self addJumpBar:descriptionInfo];
}

@end
