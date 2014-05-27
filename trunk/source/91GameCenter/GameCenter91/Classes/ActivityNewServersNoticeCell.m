//
//  ActivityTableViewCell.m
//  GameCenter91
//
//  Created by sun pinqun on 13-01-25.
//  Copyright 2012 Nd. All rights reserved.
//

#define kServersStartImage   @"yikaifu.png"

#import "ActivityNewServersNoticeCell.h"
#import "ActivityInfo.h"
#import "UserData.h"
#import "CommUtility.h"
#import "NSDate+Utilities.h"
#import "UIAlertView+Blocks.h"
#import "RIButtonItem.h"
#import <NdComPlatform/NdComPlatform.h>
#import "GameCenterMacros.h"
#import "StatusBarNotification.h"
#import "ReportCenter.h"
#import "CustomAlertView.h"

#define kTomorrowTextColor   HEXCOLOR(0x147fb5ff)
#define kTodayTextColor      HEXCOLOR(0x147fb5ff)
#define kOutDateTextColor    HEXCOLOR(0xa1a1a1ff)
#define KTitleTextColor      HEXCOLOR(0x292929ff) 

#define Today 100
#define Tomorrow 101
#define OutDate 102
#define DateLater 103

@interface ActivityNewServersNoticeCell()
@property (nonatomic, assign) ActivityInfo *activityInfo;
- (void)resetLabelColorWithDateType:(NSInteger)dateType;
@end

@implementation ActivityNewServersNoticeCell
@synthesize act_gameName;
@synthesize act_openDate;
@synthesize act_openTime;
@synthesize act_serversHead;
@synthesize act_serversName;
@synthesize act_button;
@synthesize act_stamp;

@synthesize activityInfo;

-(void)reset
{
    self.act_stamp.hidden = YES;
    self.act_button.hidden = NO;
    [self.act_button removeTarget:self action:NULL forControlEvents:UIControlEventTouchUpInside];
}

-(void) awakeFromNib
{
    [self reset];
}

- (void)dealloc {
    [super dealloc];
}

#pragma mark-

- (void)resetLabelColorWithDateType:(NSInteger)dateType
{
    if (dateType == Today || dateType == Tomorrow) {
        self.act_gameName.textColor = [CommUtility colorWithHexRGB:@"333333"];
        self.act_openDate.textColor = [CommUtility colorWithHexRGB:@"333333"];
        self.act_openTime.textColor = [CommUtility colorWithHexRGB:@"333333"];
        self.act_serversHead.textColor = [CommUtility colorWithHexRGB:@"333333"];
        self.act_serversName.textColor = [CommUtility colorWithHexRGB:@"333333"];
        self.act_gameName.font = [UIFont boldSystemFontOfSize:16];
    } else if (dateType == OutDate) {
        self.act_gameName.textColor = [CommUtility colorWithHexRGB:@"666666"];
        self.act_openDate.textColor = [CommUtility colorWithHexRGB:@"666666"];
        self.act_openTime.textColor = [CommUtility colorWithHexRGB:@"666666"];
        self.act_serversHead.textColor = [CommUtility colorWithHexRGB:@"666666"];
        self.act_serversName.textColor = [CommUtility colorWithHexRGB:@"666666"];
        self.act_gameName.font = [UIFont systemFontOfSize:16];
    } else {
        self.act_gameName.textColor = [CommUtility colorWithHexRGB:@"333333"];
        self.act_openDate.textColor = [CommUtility colorWithHexRGB:@"333333"];
        self.act_openTime.textColor = [CommUtility colorWithHexRGB:@"333333"];
        self.act_serversHead.textColor = [CommUtility colorWithHexRGB:@"333333"];
        self.act_serversName.textColor = [CommUtility colorWithHexRGB:@"333333"];
        self.act_gameName.font = [UIFont systemFontOfSize:16];
    }
    
}


- (void)setButtonState
{
    NSString *strAppIdentifier = self.activityInfo.identifier;
    NSString *strActivityId = [NSString stringWithFormat:@"%d", self.activityInfo.activityID];
    NSArray *arrNoti = [CommUtility localNotificationByAppIdentifier:strAppIdentifier activityid:strActivityId];
    if (arrNoti == nil) {
        [self.act_button setTitle:@"开服提醒" forState:UIControlStateNormal];
        self.act_button.tag = 0;
    }
    else {
        [self.act_button setTitle:@"取消提醒" forState:UIControlStateNormal];
        self.act_button.tag = 1;
    }
}

- (void)setCellInfo:(int)act_type withActivityInfo:(ActivityInfo *)info
{
    [self reset];
    self.activityInfo = info;
    
    NSString *strAppName = @"";
    UIImage *imageStamp = nil;
    NSString *strOpenDate = @"";
    NSString *strOpenTime = @"";
    
    NSDate *openDate = [CommUtility dateFromString:info.openTime];
    //NSDate *openDate = [[NSDate date] dateByAddingDays:-1];
    if ([info.openTime length] > 0) {
        strOpenDate = [info.openTime substringWithRange:NSMakeRange(5, 5)];
        strOpenTime = [info.openTime substringWithRange:NSMakeRange(11, 5)];
    }
    
    strAppName = (act_type == -1) ? [NSString stringWithFormat:@"[开服] %@",info.appName] : info.appName;
    
    if ([openDate isEarlierThanDate:[NSDate date]]) {
        //已开服
        imageStamp = [UIImage imageNamed:kServersStartImage];
        self.act_button.hidden = YES;
        self.act_stamp.hidden = NO;
        [self resetLabelColorWithDateType:OutDate];
    } else if ([openDate isToday]) {
        //今天
        strOpenDate = @"今天";
        [self resetLabelColorWithDateType:Today];
    } else if ([openDate isTomorrow]) {
        //明天
        strOpenDate = @"明日";
        [self resetLabelColorWithDateType:Tomorrow];
    } else {
        //未开服
        [self resetLabelColorWithDateType:DateLater];
    }
        
    
//    //由于新服数太少，产品经理要求七天内的开服都要显蓝色
//    NSInteger dayIntervals = [[NSDate date] daysAfterDate:openDate];
//    if (dayIntervals >= 0 && dayIntervals <= 7) {
////        [self resetLabelColor:kTomorrowTextColor];
//        [self resetLabelColorWithDateType:Tomorrow];
//    }

    if (strAppName.length == 0) {
        [self resetCellWhenActGameNameNoExist];
    } else {
        self.act_gameName.text = strAppName;
        self.act_openDate.text = strOpenDate;
        self.act_openTime.text = strOpenTime;
        self.act_serversName.text = info.belongServer;
        self.act_stamp.image = imageStamp;
    }
    
    
    if ([self.act_button isHidden] == NO) {
        [self setButtonState];     
        [self.act_button addTarget:self action:@selector(buttonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
}

- (void)resetCellWhenActGameNameNoExist
{
    self.act_gameName.text = nil;
    self.act_openDate.text = nil;
    self.act_openTime.text = nil;
    self.act_serversHead.text = nil;
    self.act_serversName.text = nil;
    self.act_stamp.image = nil;
    
    self.act_button.hidden = YES;
    self.act_stamp.hidden = YES;
}

- (void)registerNotify{
    //设置开服提醒操作无需登录
//    if ([[NdComPlatform defaultPlatform] isLogined]) {
        NSString *appIdentifier = self.activityInfo.identifier;
        NSString *activityId = [NSString stringWithFormat:@"%d", self.activityInfo.activityID];
        NSDate *openDate = [CommUtility dateFromString:self.activityInfo.openTime];
        BOOL res = [CommUtility setNewServersLocalNotification:openDate title:self.activityInfo.title appIdentifier:appIdentifier activityid:activityId appName:self.activityInfo.appName];
        if (res) {
            [self setButtonState];
            
            //统计
            [ReportCenter report:ANALYTICS_EVENT_15061 label:appIdentifier];
        }
//    }
//    else {
//        NDCP_CHECK_LOGIN_ERROR_AND_SHOULD_ALERT(@"亲，你现在还未登录，设置开服提醒。马上用91账号登录吧！")
//    }
}

- (void)cancelNotify
{
    //取消开服提醒
    NSString *strAppIdentifier = self.activityInfo.identifier;
    NSString *activityId = [NSString stringWithFormat:@"%d", self.activityInfo.activityID];
    BOOL res = [CommUtility cancelLocalNotificationWithAppIdentifier:strAppIdentifier activityid:activityId];
    if (res) {
        [self setButtonState];
        
        //统计
        [ReportCenter report:ANALYTICS_EVENT_15062 label:strAppIdentifier];
    }
}

- (void)buttonPressed:(id)sender
{
    UIButton *button = (UIButton*)sender;
    
    if (button.tag == 0) {
        [self registerNotify];
        
        NSString *massage = [NSString stringWithFormat:@"设置开服提醒成功，在游戏开服当天，将给您发送通知。"];
        [StatusBarNotification notificationWithMessage:massage];
    } else {
        
        SEL selector = @selector(cancelNotify);
        NSString *title = @"您要撤销已启动的开服提醒？";
        
        RIButtonItem *cancelItem = [RIButtonItem itemWithLabel:@"取消"];
        RIButtonItem *openItem = [RIButtonItem itemWithLabel:@"确定"];
        openItem.action = ^{[self performSelector:selector];};
        
        CustomAlertView *alert = [[CustomAlertView alloc] initWithTitle:title message:nil cancelButtonItem:nil otherButtonItems:openItem, cancelItem, nil];
        [alert show];
        [alert release];
    }
}

+ (NSString *)cellReuseIdentifier
{
    return @"ActivityNewServersNoticeCell";
}

+ (float)cellHeight
{
    return 58;
}

@end
