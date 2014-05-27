//
//  ActivityTableViewCell.m
//  GameCenter91
//
//  Created by  hiyo on 12-8-27.
//  Copyright 2012 Nd. All rights reserved.
//

#define kGiftsExchangedImage              @"yilingqu.png"
#define kActivityOverImage                @"end.png"
#define kServersStartImage                @"yikaifu.png"

#import "ActivityTableViewCell.h"
#import "UIImageView+WebCache.h"
#import "ActivityInfo.h"
#import "GiftItem.h"
#import "UserData.h"
#import "CommUtility.h"
#import "NSDate+Utilities.h"
#import "UIAlertView+Blocks.h"
#import "MBProgressHUD.h"

@implementation ActivityTableViewCell
@synthesize game_icon;
@synthesize act_title;
@synthesize act_detail;
@synthesize act_append1;
@synthesize act_append2;
@synthesize act_button;
@synthesize act_stamp;

-(void)reset
{
    self.act_button.hidden = YES;
    self.act_append2.hidden = YES;
    self.act_stamp.hidden = YES;
//    self.act_title.textColor = [UIColor colorWithRed:0x14/255.0 green:0x7f/255.0 blue:0xb5/255.0 alpha:1];;
    self.act_title.textColor = [UIColor colorWithRed:0x33/255.0 green:0x33/255.0 blue:0x33/255.0 alpha:1.0];
//    self.act_detail.textColor = [UIColor darkTextColor];
    self.act_detail.textColor = [UIColor colorWithRed:0x66/255.0 green:0x66/255.0 blue:0x66/255.0 alpha:1.0];
    [self.act_button removeTarget:self action:NULL forControlEvents:UIControlEventTouchUpInside];
}

-(void) awakeFromNib
{
    [self reset];
}

- (void)dealloc {
    [super dealloc];
}

- (void)setCellInfo:(int)act_type withMyGiftInfo:(GiftItem *)info
{
    [self reset];
    self.act_button.hidden = NO;
    self.act_append2.hidden = NO;
    
    NSString *actTitleLabel = (act_type == ACT_OTHER) ? @"[礼包] ": @"";
    NSString *iconUrl = info.appIconUrl;
    
    [self.act_button setTitle:@"复制激活码" forState:UIControlStateNormal];
    [self.act_button addTarget:self action:NSSelectorFromString(@"copyActivationCode:") forControlEvents:UIControlEventTouchUpInside];
    
	self.act_title.text = [actTitleLabel stringByAppendingString:info.title];
	self.act_detail.text = info.summary ;
	self.act_append1.text = @"激活码:";
	self.act_append2.text = info.exchangeNo;
    self.act_append2.textColor = [UIColor redColor];
    [self.game_icon setImageWithURL:[NSURL URLWithString:iconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
    
    //调整label长度，防止被button遮盖
    if (CGRectGetMaxX(act_detail.frame) >= CGRectGetMinX(act_button.frame)) {
        CGRect rc = act_detail.frame;
        rc.size.width = CGRectGetMinX(act_button.frame)-CGRectGetMinX(act_detail.frame);
        act_detail.frame = rc;
    }
}

- (void)setCellInfo:(int)act_type withActivityInfo:(ActivityInfo *)info
{
    [self reset];
    
    NSString *actTitleLabel = @"";
    NSString *detailText = @"";
    NSString *append1 = @"";
    NSString *append2 = @"";
    UIImage *imageStamp = nil;
    NSString *iconUrl = info.appIconUrl;
    BOOL bNeedTitleLabel = (act_type == ACT_OTHER);
    BOOL bNeedGrayText = NO;
    
    NSDate *endDate = ([info.endTime length] == 0) ? [[NSDate date] dateByAddingDays:999]:[CommUtility dateFromString:info.endTime];
    
    switch (info.activityType) {
        case ACTIVITY_TYPE_GAME_GIFT:
            actTitleLabel = bNeedTitleLabel ? @"礼包 | " : @"";
            detailText = info.belongServer;
            if (![detailText length]) {
                detailText = info.summary;
            }
            append1 = [NSString stringWithFormat:@"截止时间: %@  剩余%d个",
                       ([info.endTime length]==0 ? @"不限时" : [info.endTime substringWithRange:NSMakeRange(0, 10)]),
                       info.giftNumber];
            if (info.exchanged == 1) {
                imageStamp = [UIImage imageNamed:kGiftsExchangedImage];//已领取
            }            
            else if ([endDate isEarlierThanDate:[NSDate date]]) {
                imageStamp = [UIImage imageNamed:kActivityOverImage];//已过期
                bNeedGrayText = YES;
            }
            break;
        case ACTIVITY_TYPE_ACTIVITY_NOTICE:
            actTitleLabel = bNeedTitleLabel ? @"活动 | " : @"";
            detailText = info.summary;
            append1 = [NSString stringWithFormat:@"截止时间: %@", 
                       ([info.endTime length]==0 ? @"不限时" : [info.endTime substringWithRange:NSMakeRange(0, 10)])];
            //已过期
            if ([endDate isEarlierThanDate:[NSDate date]]) {
                imageStamp = [UIImage imageNamed:kActivityOverImage];
                bNeedGrayText = YES;
            }
            break;
        case ACTIVITY_TYPE_PRIZE_NOTICE:
            actTitleLabel = bNeedTitleLabel ? @"获奖 | " : @"";
            detailText = info.summary;
            append1 = [NSString stringWithFormat:@"发布时间: %@", 
                       ([info.startTime length]==0 ? @"不限时" : [info.startTime substringWithRange:NSMakeRange(0, 10)])];
            //已过期
            if ([endDate isEarlierThanDate:[NSDate date]]) {
                imageStamp = [UIImage imageNamed:kActivityOverImage];
                bNeedGrayText = YES;
            }
            break;
        case ACTIVITY_TYPE_NEW_SERVERS_NOTICE:
            actTitleLabel = bNeedTitleLabel ? @"新服 | " : @"";
            detailText = info.belongServer;
            if (![detailText length]) {
                detailText = info.summary;
            }
            append1 = [NSString stringWithFormat:@"开服时间: %@",([info.openTime length]==0 ? @"" :[info.openTime substringWithRange:NSMakeRange(0, 10)])];
            //已过期
            NSDate *openDate = [CommUtility dateFromString:info.openTime];
            if ([openDate isEarlierThanDate:[NSDate date]]) {
                imageStamp = [UIImage imageNamed:kServersStartImage];
            }
            break;
        default:
            break;
    }
    
    if (imageStamp != nil) {
        self.act_stamp.hidden = NO;
        self.act_stamp.image = imageStamp;
    }
    //已结束的公告，标题、简介全部显示为灰色
    if (bNeedGrayText) {
//        self.act_title.textColor = [UIColor grayColor];
        self.act_title.textColor = [UIColor colorWithRed:0x33/255.0 green:0x33/255.0 blue:0x33/255.0 alpha:1.0];
        self.act_title.font = [UIFont systemFontOfSize:16.0];
//        self.act_detail.textColor = [UIColor grayColor];
        self.act_detail.textColor = [UIColor colorWithRed:0x66/255.0 green:0x66/255.0 blue:0x66/255.0 alpha:1.0];
    }
    
	self.act_title.text = [actTitleLabel stringByAppendingString:info.title];
	self.act_detail.text = detailText;
	self.act_append1.text = append1;
	self.act_append2.text = append2;
    [self.game_icon setImageWithURL:[NSURL URLWithString:iconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
}

- (void)cellAdjustForNoneIconStyle
{
    game_icon.image = nil;
    game_icon.hidden = YES;
    for (UIView *view in self.contentView.subviews) {
        if ([view isKindOfClass:NSClassFromString(@"UIImageView")] || [view isKindOfClass:NSClassFromString(@"UIButton")])
            continue;
        
        CGRect rect = view.frame;
        rect.origin.x = game_icon.frame.origin.x;
        rect.size.width += game_icon.frame.size.width;
        view.frame = rect;
    }
}

+ (NSString *)cellReuseIdentifier
{
    return @"ActivityTableViewCell";
}

+ (float)cellHeight
{
    return 80;
}

#pragma mark action
- (void)copyActivationCode:(id)sender
{
	UIPasteboard *board = [UIPasteboard generalPasteboard];
	[board setString:self.act_append2.text];
	
	[MBProgressHUD showHintHUD:@"复制成功" message:nil hideAfter:DEFAULT_TIP_LAST_TIME];
}

@end
