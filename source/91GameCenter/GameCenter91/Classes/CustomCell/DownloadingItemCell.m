//
//  DownloadingItemCell.m
//  testCell
//
//  Created by Sun pinqun on 12-8-20.
//  Copyright net dragon 2012. All rights reserved.
//

#import "DownloadingItemCell.h"
#import "SoftItem.h"
#import "UIImageView+WebCache.h"
#import "SoftManagementCenter.h"
#import "CommUtility.h"

@implementation DownloadingItemCell

@synthesize appImage;
@synthesize appName;
@synthesize downloadPercent;
@synthesize rightButton;
@synthesize progress;

-(void)reset
{
    [super reset];
    
	appImage.image = nil;
	appName.text = @"";
	downloadPercent.text = @"";
    
    //adapt progressView in iOS7
    CGRect rc = progress.frame;
    rc.size.height = 9;
    progress.frame = rc;
}

-(void) awakeFromNib
{
    [super awakeFromNib];    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

- (void)dealloc {
    [super dealloc];
}

- (void)setRightButtonAction:(id)target action:(SEL)action {
	if (self.rightButton) {
		[self.rightButton removeTarget:target action:NULL forControlEvents:UIControlEventTouchUpInside];
		[self.rightButton addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
	}
}

- (void)updateButtonTitle:(SoftItem *)item
{
    NSString *title = @"";
    switch (item.downloadStatus) {
        case KS_DOWNLOADING:
        case KS_INITIALIZING:
            title = @"下载中";
            break;
        default:
            title = @"已暂停";
            break;
    }
    [self.rightButton setTitle:title forState:UIControlStateNormal];
}

- (void)setSoftItem:(SoftItem *)item
{
    [appImage setImageWithURL:item.iconUrl placeholderImage:[item defaultIcon]];
    appName.text = item.softName;
    self.gameName = item.softName;
    downloadPercent.text = [NSString stringWithFormat:@"%d%%(%@/%@)", 
                                (item.totalLen!=0 ? (int)(1.0*item.downloadedLen/item.totalLen*100) : 0),
                                [CommUtility readableFileSize: item.downloadedLen],
                                [CommUtility readableFileSize: item.totalLen]];
    progress.progress = [item downloadPercent];
    
    self.appIdentifier = item.identifier;
    [self updateButtonTitle:item];
    [self setRightButtonAction:self action:@selector(buttonPressed:)];
}

- (void)buttonPressed:(UIButton *)btn
{
    NSString *appIdentifier = self.appIdentifier;
    SoftItem *item = [[SoftManagementCenter sharedInstance] softItemForIdentifier:appIdentifier];
    switch (item.downloadStatus) {
        case KS_DOWNLOADING:
        case KS_INITIALIZING:
            [[SoftManagementCenter sharedInstance] stopTask:appIdentifier];
            break;
        default:
            [[SoftManagementCenter sharedInstance] startTask:appIdentifier];
            break;
    }
    [self updateButtonTitle:item];
}
@end
