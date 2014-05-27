//
//  ProgessButton.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-11-29.
//  Copyright 2012 net dragon. All rights reserved.
//

#define kTrackButtonImage           @"btn_gray.png"
#define kProgressButtonImage        @"btn_play.png"
#define kNormalButtonImage          @"btn_blue.png"
#define kHighlightButtonImage       @"btn_blue_down.png"

#define kGreenProgressButtonImage   @"btn_green.png"
#define kGreenNormalButtonImage     @"btn_green.png"
#define kGreenHighlightButtonImage  @"update_increase_sel.png"

#import "ProgessButton.h"
#import "SoftItem.h"
#import "SoftManagementCenter.h"
#import "MIUtility.h"
#import "GameCenterAnalytics.h"
#import "ReportCenter.h"
#import "CommUtility.h"

@interface ProgessButton()
- (void)initilization;
- (void)showIconOrProgress:(SoftItem *)item;
- (void)setProgess:(float)fProgess bLoading:(BOOL)bLoading;
@end

@implementation ProgessButton
@synthesize bShowAppName, pose_type;
@synthesize identifier, f_id, softName, iconUrl;

- (id)initWithCoder:(NSCoder *)aCoder {
    if (self = [super initWithCoder:aCoder]) {
        [self initilization];
    }
    return self;
}

- (id)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        [self initilization];
    }
    return self;
}

- (void)initilization {
    
    progess = 0.0;
    self.identifier = nil;
    
    self.bShowAppName = NO;
    self.pose_type = ProgessButton_DISPLAY_POSE_DEF;
    self.showUpgrade = NO;//是否显示升级信息
    
    self.backgroundColor = [UIColor clearColor];
    CGSize size = self.frame.size;
    trackView = [[[UIImageView alloc] initWithFrame:CGRectMake(0, 0, size.width, size.height)] autorelease];
    UIImage *imgTrack = [UIImage imageNamed:kTrackButtonImage];
    trackView.image = [imgTrack stretchableImageWithLeftCapWidth:imgTrack.size.width/2 topCapHeight:imgTrack.size.height/2];
    trackView.userInteractionEnabled = NO;
    [self addSubview:trackView];
    
    UIView *progessViewBg = [[[UIView alloc] initWithFrame:CGRectMake(0, 0, size.width, size.height)] autorelease];
    progessViewBg.clipsToBounds = YES;
    progessViewBg.userInteractionEnabled = NO;
    [self addSubview:progessViewBg];
    
    progessView = [[[UIImageView alloc] init] autorelease];
    UIImage *imgProgress = [UIImage imageNamed:kProgressButtonImage];
    progessView.image = [imgProgress stretchableImageWithLeftCapWidth:imgProgress.size.width/2 topCapHeight:imgProgress.size.height/2];
    progessView.userInteractionEnabled = NO;
    [progessViewBg addSubview:progessView];
    
    percent = [[[UILabel alloc] initWithFrame:self.bounds] autorelease];
    percent.backgroundColor = [UIColor clearColor];
    percent.font = [UIFont systemFontOfSize:14];
    percent.textAlignment = UITextAlignmentCenter;
    [self addSubview:percent];
    
    self.normalButton = [UIButton buttonWithType:UIButtonTypeCustom];
    self.normalButton.frame = self.bounds;
    self.normalButton.titleLabel.font = [UIFont boldSystemFontOfSize:14];
    UIImage *imgNormalBtn = [UIImage imageNamed:kNormalButtonImage];
    UIImage *imgHighlightBtn = [UIImage imageNamed:kHighlightButtonImage];
    [self.normalButton setBackgroundImage:[imgNormalBtn stretchableImageWithLeftCapWidth:imgNormalBtn.size.width/2 topCapHeight:imgNormalBtn.size.height/2] forState:UIControlStateNormal];
    [self.normalButton setBackgroundImage:[imgHighlightBtn stretchableImageWithLeftCapWidth:imgHighlightBtn.size.width/2 topCapHeight:imgHighlightBtn.size.height/2] forState:UIControlStateHighlighted];
    [self.normalButton addTarget:self action:@selector(onButtonClick:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:self.normalButton];
}

- (void)dealloc
{
    self.identifier = nil;
    self.softName = nil;
    self.iconUrl = nil;
    self.normalButton = nil;
    [super dealloc];
}

#pragma makr -

- (void)setProgressButtonInfo:(NSString *)aIdentifier f_id:(int)aF_id softName:(NSString *)aSoftName iconUrl:(NSString *)aIconUrl
{
    self.identifier = aIdentifier;
    self.f_id = aF_id;
    self.softName = aSoftName;
    self.iconUrl = aIconUrl;
    
    [self updateProgessButtonState:aIdentifier];
}

- (void)setProgressButtonTitle:(NSString *)aIdentifier f_id:(int)aF_id softName:(NSString *)aSoftName iconUrl:(NSString *)aIconUrl
{
    self.identifier = aIdentifier;
    self.f_id = aF_id;
    self.softName = aSoftName;
    self.iconUrl = aIconUrl;
    [self updateProgessButtonTitle:aIdentifier];
    
}

- (void)updateProgessButtonTitle:(NSString *)aIdentifier
{
    [self bringSubviewToFront:self.normalButton];
    SoftItem *item = [[SoftManagementCenter sharedInstance] softItemForIdentifier:aIdentifier];
    SoftItem *upgradableItem = [[SoftManagementCenter sharedInstance] updatableSoftItemForIdentifier:aIdentifier];
    NSString *buttonTitle = nil;
    if (upgradableItem != nil && self.showUpgrade) {
        buttonTitle = @"升级";
        if ([[SoftManagementCenter sharedInstance] haveIncreInfoForSoftItem:upgradableItem]) {
            buttonTitle = [NSString stringWithFormat:@"智能升级(省%@)", [CommUtility readableFileSize:(upgradableItem.totalLen -  upgradableItem.increUpateInfo.increFileSize)]];
            UIImage *imgNormalBtn = [UIImage imageNamed:kGreenNormalButtonImage];
            UIImage *imgHighlightBtn = [UIImage imageNamed:kGreenHighlightButtonImage];
            [self.normalButton setBackgroundImage:[imgNormalBtn stretchableImageWithLeftCapWidth:imgNormalBtn.size.width/2 topCapHeight:imgNormalBtn.size.height/2] forState:UIControlStateNormal];
            [self.normalButton setBackgroundImage:[imgHighlightBtn stretchableImageWithLeftCapWidth:imgHighlightBtn.size.width/2 topCapHeight:imgHighlightBtn.size.height/2] forState:UIControlStateHighlighted];
            
            UIImage *imgProgress = [UIImage imageNamed:kGreenProgressButtonImage];
            progessView.image = [imgProgress stretchableImageWithLeftCapWidth:imgProgress.size.width/2 topCapHeight:imgProgress.size.height/2];
        }
        
        
    }else if([[SoftManagementCenter sharedInstance] isAnInstalledSoftItem:item]){
        buttonTitle = @"开始玩";
        UIImage *imgNormalBtn = [UIImage imageNamed:kNormalButtonImage];
        UIImage *imgHighlightBtn = [UIImage imageNamed:kHighlightButtonImage];
        [self.normalButton setBackgroundImage:[imgNormalBtn stretchableImageWithLeftCapWidth:imgNormalBtn.size.width/2 topCapHeight:imgNormalBtn.size.height/2] forState:UIControlStateNormal];
        [self.normalButton setBackgroundImage:[imgHighlightBtn stretchableImageWithLeftCapWidth:imgHighlightBtn.size.width/2 topCapHeight:imgHighlightBtn.size.height/2] forState:UIControlStateHighlighted];
        
    }else{
        buttonTitle = bShowAppName ? [NSString stringWithFormat:@"下载%@", item.softName] : @"下载";
    }
    [self.normalButton setTitle:buttonTitle forState:UIControlStateNormal];
    [self showButtonTitleOrProgress:aIdentifier];
}

- (void)updateProgessButtonState:(NSString *)aIdentifier
{
    self.identifier = aIdentifier;
    SoftItem *item = [[SoftManagementCenter sharedInstance] softItemForIdentifier:aIdentifier];
    NSString *buttonTitle = nil;
    if ([[SoftManagementCenter sharedInstance] isAnInstalledSoftItem:item])
    {
        buttonTitle = @"开始玩";
    }
    else {
        switch (item.downloadStatus) {
            case KS_QUEUING:
            case KS_STOPPED:
                buttonTitle = @"已暂停";
                break;
            case KS_DOWNLOADING:
            case KS_INITIALIZING:
                buttonTitle = @"下载中";
                break;
            case KS_FINISHED:
                buttonTitle = @"安装中";
                break;
            default:
            {
                buttonTitle = bShowAppName ? [NSString stringWithFormat:@"下载%@", item.softName] : @"下载";
          
            }
                
                break;
        }
    }


    [self showButtonTitleOrProgress:item.identifier]; //共用一个ProgessButton时刷新需要
    

    [self.normalButton setTitle:buttonTitle forState:UIControlStateNormal];
}

- (void)setNormalButtonInstalling
{
    [self.normalButton setTitle:@"安装中" forState:UIControlStateNormal];
}

- (void)showButtonTitleOrProgress:(NSString *)aIdentifier
{
    SoftItem *item = [[SoftManagementCenter sharedInstance] softItemForIdentifier:aIdentifier];
    SoftItem *updatableItem = [[SoftManagementCenter sharedInstance] updatableSoftItemForIdentifier:aIdentifier];
    if (![[SoftManagementCenter sharedInstance] isAnInstalledSoftItem:item]) {
        [self showIconOrProgress:item];
    }
    else if(updatableItem != nil && self.showUpgrade)
    {
        [self showIconOrProgress:item];
    }
    
}

- (void)showIconOrProgress:(SoftItem *)item
{
    BOOL isLoading = (item.downloadStatus == KS_DOWNLOADING || item.downloadStatus == KS_INITIALIZING);
    [self setProgess:[item downloadPercent] bLoading:isLoading];
    
    //进度条显示或者隐藏
//    if ([[SoftManagementCenter sharedInstance] isAnInstalledSoftItem:item]) {
//        [self bringSubviewToFront:normalButton]; //已安装的，除了可升级那边需要显示下载状态，其他显示开始玩
//    }
//    else if (item.downloadStatus == KS_DOWNLOADING || item.downloadStatus == KS_STOPPED ||
//        item.downloadStatus == KS_QUEUING || item.downloadStatus == KS_INITIALIZING) {
//        [self sendSubviewToBack:normalButton];
//    }
//    else {
//        [self bringSubviewToFront:normalButton];
    
//    }
    if ((item.downloadStatus == KS_DOWNLOADING || item.downloadStatus == KS_STOPPED ||
        item.downloadStatus == KS_QUEUING || item.downloadStatus == KS_INITIALIZING) ) {
        
        [self sendSubviewToBack:self.normalButton];
    }else
    {
        [self bringSubviewToFront:self.normalButton];
    }
}

- (void)setProgess:(float)fProgess bLoading:(BOOL)bLoading
{
    progess = fProgess;
    CGSize size = self.frame.size;
    progessView.frame = CGRectMake(size.width*progess-size.width, 0, size.width, size.height);
    
    percent.text = bLoading ? [NSString stringWithFormat:@"%d%%", (int)(fProgess*100)] : @"已暂停";
}

- (void)onButtonClick: (id)sender {
    
    if (self.tag == ProgessButton_DISPLAY_POSE_PERSON) {
        [ReportCenter report:ANALYTICS_EVENT_15032 label:self.identifier];
    }

    SoftItem *item = [[SoftManagementCenter sharedInstance] softItemForIdentifier:self.identifier];
    SoftItem *upgradableItem = [[SoftManagementCenter sharedInstance] updatableSoftItemForIdentifier:self.identifier];
    if (upgradableItem != nil && self.showUpgrade ) {
        switch (item.downloadStatus) {
            case KS_DOWNLOADING:
            case KS_INITIALIZING:
            case KS_QUEUING:
                [[SoftManagementCenter sharedInstance] stopTask:identifier];
                break;
            default:
            [[SoftManagementCenter sharedInstance] updateTask:identifier];
        }

    }else if ([[SoftManagementCenter sharedInstance] isAnInstalledSoftItem:item])
    {
        if (self.pose_type == ProgessButton_DISPLAY_POSE_PERSON) {
            //            [NdAnalytics event:ANALYTICS_EVENT_1030 label:[NSString stringWithFormat:@"%d", appId]];
        }
        [[SoftManagementCenter sharedInstance] open:item];
    }else
    {
        switch (item.downloadStatus) {
            case KS_DOWNLOADING:
            case KS_INITIALIZING:
            case KS_QUEUING:
                [[SoftManagementCenter sharedInstance] stopTask:self.identifier];
                break;
            case KS_FINISHED:
                if (item.installStatus == INSTALL_QUEUING || item.installStatus == INSTALL_INSTALLING) {
					return;
				}
				else {
					[[SoftManagementCenter sharedInstance] doInstallWithLoading:item];
				}
                break;
            default:
                [[SoftManagementCenter sharedInstance] startTask:self.identifier f_id:self.f_id softName:self.softName iconUrl:self.iconUrl];
                break;
        }

    }
//    if ([[SoftManagementCenter sharedInstance] isAnInstalledSoftItem:item])
//    {
//        if (self.pose_type == ProgessButton_DISPLAY_POSE_PERSON) {
////            [NdAnalytics event:ANALYTICS_EVENT_1030 label:[NSString stringWithFormat:@"%d", appId]];
//        }
//        [[SoftManagementCenter sharedInstance] open:item];
//    }
//    else {
//        switch (item.downloadStatus) {
//            case KS_DOWNLOADING:
//            case KS_INITIALIZING:
//            case KS_QUEUING:
//                [[SoftManagementCenter sharedInstance] stopTask:self.identifier];
//                break;
//            case KS_FINISHED:
//                if (item.installStatus == INSTALL_QUEUING || item.installStatus == INSTALL_INSTALLING) {
//					return;
//				}
//				else {
//					[[SoftManagementCenter sharedInstance] doInstallWithLoading:item];
//				}
//                break;
//            default:
//                [[SoftManagementCenter sharedInstance] startTask:self.identifier f_id:self.f_id softName:self.softName iconUrl:self.iconUrl];
//                break;
//        }
//    }
}

- (void)reset
{
    progess = 0.0;
//    self.identifier = nil;
    [self setProgess:0 bLoading:YES];
    [self bringSubviewToFront:self.normalButton];
}

//- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
//{
//    [self sendSubviewToBack:normalButton];
//}

- (void)resetNormalButtonWithFontSize:(UIFont *)font
{
    if (font == nil) {
        return;
    }
    self.normalButton.titleLabel.font = font;
}

- (void)resetNormalButtonWithTitleColor:(UIColor *)color
{
    if (color == nil) {
        return;
    }
    if ([self.normalButton.currentTitle isEqualToString:@"开始玩"]) {
        [self.normalButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    } else {
        [self.normalButton setTitleColor:color forState:UIControlStateNormal];
    }
    
}

- (void)resetNormalButtonWithBackgroundImage:(UIImage *)image
{
    if (image == nil) {
        return;
    }
    if ([self.normalButton.currentTitle isEqualToString:@"开始玩"]) {
        UIImage *deafaultImage = [UIImage imageNamed:kNormalButtonImage];
        [self.normalButton setBackgroundImage:[deafaultImage stretchableImageWithLeftCapWidth:deafaultImage.size.width/2 topCapHeight:deafaultImage.size.height/2] forState:UIControlStateNormal];
    } else {
        [self.normalButton setBackgroundImage:[image stretchableImageWithLeftCapWidth:image.size.width/2 topCapHeight:image.size.height/2] forState:UIControlStateNormal];
    }
}

- (void)resetPercentFontSize:(UIFont *)font
{
    percent.font = font;
}

@end
