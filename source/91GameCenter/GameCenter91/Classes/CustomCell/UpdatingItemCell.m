//
//  UpdatingItemCell.m
//  testCell
//
//  Created by Sun pinqun on 12-8-20.
//  Copyright net dragon 2012. All rights reserved.
//

#import "UpdatingItemCell.h"
#import "SoftItem.h"
#import "UIImageView+WebCache.h"
#import "SoftManagementCenter.h"
#import "CommUtility.h"

@implementation UpdatingItemCell

@synthesize appImage;
@synthesize appName;
@synthesize rightButton;
@synthesize progress;
@synthesize updatePercent;
@synthesize appIdentifier;


-(void)reset
{
	appImage.image = nil;
	appName.text = @"";
    
    //adapt progressView in iOS7
    CGRect rc = progress.frame;
    rc.size.height = 9;
    progress.frame = rc;
}

-(void) awakeFromNib
{
    [super awakeFromNib];
    
	[self reset];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
}

- (void)dealloc {
    self.appIdentifier = nil;
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
            title = @"升级中";
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
    
    progress.progress = [item downloadPercent];

    updatePercent.text = [NSString stringWithFormat:@"%d%%(%@/%@)",
                            (item.totalLen!=0 ? (int)(1.0*item.downloadedLen/item.totalLen*100) : 0),
                            [CommUtility readableFileSize: item.downloadedLen],
                            [CommUtility readableFileSize: item.totalLen]];

    //智能升级
    if (item.increUpateInfo != nil && !item.increUpateInfo.smartUpdateFailed) { 
        [self.rightButton setBackgroundImage:[UIImage imageNamed:@"update_increase_normal.png"] forState:UIControlStateNormal];
        [self.rightButton setBackgroundImage:[UIImage imageNamed:@"update_increase_sel.png"] forState:UIControlStateSelected];
        [self.rightButton setTitle:@"智能升级" forState:UIControlStateNormal];
    }
    else {  
        [self.rightButton setBackgroundImage:[UIImage imageNamed:@"btn_blue.png"] forState:UIControlStateNormal];
        [self.rightButton setBackgroundImage:[UIImage imageNamed:@"btn_blue_down.png"] forState:UIControlStateSelected];
        [self.rightButton setTitle:@"升级" forState:UIControlStateNormal];
    }
    
    [self updateButtonTitle:item];
    self.appIdentifier = item.identifier;
    [self setRightButtonAction:self action:@selector(buttonPressed:)];
}

- (void)buttonPressed:(UIButton *)btn
{
    NSString *identifier = self.appIdentifier;
    SoftItem *item = [[SoftManagementCenter sharedInstance] softItemForIdentifier:identifier];
    switch (item.downloadStatus) {
        case KS_DOWNLOADING:
        case KS_INITIALIZING:
            [[SoftManagementCenter sharedInstance] stopTask:identifier];
            break;
        default:
            [[SoftManagementCenter sharedInstance] updateTask:identifier];
            break;
    }
    [self updateButtonTitle:item];
}

@end
