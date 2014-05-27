//
//  DownloadButtonBar.m
//  GameCenter91
//
//  Created by Sun pinqun on 13-1-25.
//  Copyright (c) 2013年 net dragon. All rights reserved.
//

#import "DownloadButtonBar.h"
#import "Notifications.h"
#import "ProgessButton.h"
#import "SoftItem.h"
#import "SoftManagementCenter.h"
#import "AppDetailCacheInfo.h"

#define kBarHeight             40.f
#define kButtonHeight          32.f

@interface DownloadButtonBar()
@property(nonatomic, assign) ProgessButton *downloadButton;
@property(nonatomic, assign) BOOL showUpgrade;//如果有智能升级信息则显示；
@end

@implementation DownloadButtonBar
@synthesize identifier, downloadButton;

- (id)initWithView:(UIView *)view
{
    CGRect frame = CGRectMake(0.f, view.frame.size.height - kBarHeight, view.frame.size.width, kBarHeight);
    self = [super initWithFrame:frame];
    if (self) {
        self.autoresizingMask = UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin;
        self.showUpgrade = NO;
        
        UIImage *img = [UIImage imageNamed:@"bg_top.png"];
        img = [img stretchableImageWithLeftCapWidth:img.size.width/2 topCapHeight:img.size.height/2];
        UIImageView *bgImgView = [[UIImageView alloc] initWithFrame:CGRectMake(0.f, 0.f, self.bounds.size.width, self.bounds.size.height)];
        bgImgView.image = img;
        [self addSubview:bgImgView];
        [bgImgView release];
        
        float border = kBarHeight - kButtonHeight;
        ProgessButton *button = [[ProgessButton alloc] initWithFrame:CGRectMake(border, border/2, self.bounds.size.width - border*2, kButtonHeight)];
        self.downloadButton = button;
        [self addSubview:button];
        [button release];
        
        //侦听下载按钮需要的消息
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadQueueChanged:) name:kGC91DownloadQueueChangeNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadPercentChanged:) name:kGC91DownloadPercentChangeNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadPercentChanged:) name:kGC91UpdatingPercentChangeNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadQueueChanged:) name:kGC91UpdateQueueChangeNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(itemIsInstalling:) name:kGC91InstallingNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(itemInstallFinished:) name:kGC91InstallFinishedNotification object:nil];


    }
    return self;
}

- (void)dealloc
{
    self.identifier = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super dealloc];
}

- (float)barHeight
{
    return kBarHeight; 
}

+ (DownloadButtonBar *)downloadBarWithAppIdentifier:(NSString *)aIdentifer
                                      appDetailInfo:(AppDetailViewInfo *)info
                                          superView:(UIView *)superView
                                          upperView:(UIView *)upperView
                                        showUpgreda:(BOOL)isSDkUpgrade
{
    DownloadButtonBar *bar = [self downloadBarWithAppIdentifier:aIdentifer appDetailInfo:info superView:superView upperView:upperView];
    bar.showUpgrade = isSDkUpgrade;
    bar.downloadButton.showUpgrade = isSDkUpgrade;
    [bar.downloadButton updateProgessButtonTitle:aIdentifer];
    return bar;
}


+ (DownloadButtonBar *)downloadBarWithAppIdentifier:(NSString *)aIdentifer appDetailInfo:(AppDetailViewInfo *)info superView:(UIView *)superView upperView:(UIView *)upperView
{
    if (aIdentifer == nil || superView == nil) {
        return nil;
    }

    DownloadButtonBar *bar = [[DownloadButtonBar alloc] initWithView:superView];
    bar.identifier = aIdentifer;
//    [bar.downloadButton setProgressButtonInfo:aIdentifer f_id:info.f_id softName:info.appName iconUrl:info.appIconUrl];
//    [bar.downloadButton updateProgessButtonState:aIdentifer];
    [bar.downloadButton setProgressButtonTitle:aIdentifer f_id:info.f_id softName:info.appName iconUrl:info.appIconUrl];
    
    if (upperView != nil) {
        CGRect rect = upperView.frame;
        rect.size.height -= [bar barHeight];
        upperView.frame = rect;
    }

    [superView addSubview:bar];
    [bar release];
    
    return bar;
}

#pragma mark -
- (void)downloadQueueChanged:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[[aNotify userInfo] objectForKey:@"ITEM"];
    if (item) {
        if ([item.identifier isEqualToString:self.identifier]) {
            [self.downloadButton showButtonTitleOrProgress:self.identifier];
        }
    }
}

- (void)downloadPercentChanged:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[aNotify object];
    if (item){
        if ([item.identifier isEqualToString:self.identifier]) {
            [self.downloadButton showButtonTitleOrProgress:self.identifier];
        }
    }
}

- (void)itemIsInstalling:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[aNotify object];
    if (item){
        if ([item.identifier isEqualToString:self.identifier]) {
            [self.downloadButton setNormalButtonInstalling];
        }
    }

}

- (void)itemInstallFinished:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[aNotify object];
    if (item){
        if ([item.identifier isEqualToString:self.identifier]) {
            [self.downloadButton updateProgessButtonTitle:self.identifier];
        }
    }
}

@end
