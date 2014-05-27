//
//  DownloadedItemCell.m
//  testCell
//
//  Created by Sun pinqun on 12-8-20.
//  Copyright net dragon 2012. All rights reserved.
//

#import "DownloadedItemCell.h"
#import "SoftItem.h"
#import "UIImageView+WebCache.h"
#import "UserData.h"
#import "SoftManagementCenter.h"
#import "MBProgressHUD.h"

@implementation DownloadedItemCell

@synthesize appImage;
@synthesize appName;
@synthesize rightButton;


-(void)reset
{
    [super reset];
    
	self.appImage.image = nil;
	self.appName.text = @"test";
}

-(void) awakeFromNib
{
    [super awakeFromNib];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:NO];
}

- (void)dealloc {	
    [super dealloc];
}

#pragma mark -
- (void)setRightButtonAction:(id)target action:(SEL)action {
	if (self.rightButton) {
		[self.rightButton removeTarget:target action:NULL forControlEvents:UIControlEventTouchUpInside];
		[self.rightButton addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
	}
}

- (void)updateButtonTitle:(SoftItem *)item
{
    NSString *title = @"";
    NSString *expandRightTitle = @"";
    if ([[SoftManagementCenter sharedInstance] isAnInstalledSoftItem:item])
    {
        title = @"开始玩";
        expandRightTitle = @"卸载";
    }
    else
    {
        title = @"安装中";
        expandRightTitle = @"删除";
    }
    [self.rightButton setTitle:title forState:UIControlStateNormal];
    [self.expandRightButton setTitle:expandRightTitle forState:UIControlStateNormal];
}

- (void)setSoftInfo:(SoftItem *)item
{
    [self.appImage setImageWithURL:item.iconUrl placeholderImage:item.defaultIcon];
    self.appName.text = item.softName;
    self.gameName = item.softName;
    self.appIdentifier = item.identifier;
    
    [self updateButtonTitle:item];
    [self setRightButtonAction:self action:@selector(rightButtonPressed:)];
}

- (void)rightButtonPressed:(UIButton *)btn
{
    NSString *appIdentifier = self.appIdentifier;
    SoftItem *item = [[SoftManagementCenter sharedInstance] softItemForIdentifier:appIdentifier];
    if ([[SoftManagementCenter sharedInstance] isAnInstalledSoftItem:item])
    {
        [[SoftManagementCenter sharedInstance] open:item];
    }
    else if (item.downloadStatus != KS_FINISHED)
    {
        NSLog(@"Not an finished item %@ %@", item.identifier, item.softName);
        return;        
    }
    else
    {
        if (item.installStatus != INSTALL_DEFAULT_STATE) {
            return;
        }
//        [MBProgressHUD showBlockHUD:YES];
        NSLog(@"!!!!!!!!!!!!!! appidentifier = %@ !!!!!!!!!!!", appIdentifier);
        [self performSelector:@selector(installItem:) withObject:item afterDelay:0.01];
    }
}

- (void)installItem:(SoftItem *)item
{
    [[SoftManagementCenter sharedInstance] doInstallWithLoading:item];
}
@end
