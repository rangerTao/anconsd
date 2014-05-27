//
//  UpdatableItemCell.m
//  testCell
//
//  Created by Sun pinqun on 12-8-20.
//  Copyright net dragon 2012. All rights reserved.
//

#import "UpdatableItemCell.h"
#import "SoftItem.h"
#import "UIImageView+WebCache.h"
#import "CommUtility.h"
#import "SoftManagementCenter.h"
#import "MBProgressHUD.h"
#import "AppDetailCacheInfo.h"
#define FINTIMAGE_WIDTH    12
#define FINTIMAGE_HEIGHT   12
#define TINTVIEWHEIGHT  28

@implementation UpdatableItemCell

@synthesize appImage;
@synthesize appName;
@synthesize currentVersion;
@synthesize direction;
@synthesize nextVersion;
@synthesize appSize;
@synthesize rightButton;
@synthesize appIdentifier;
@synthesize increSize;


-(void)reset
{
	appImage.image = nil;
	appName.text = @"";
}

-(void) awakeFromNib
{
	[self reset];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
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

- (CGFloat)widthForText:(NSString *)text maxWidth:(float)maxWidth
{
    CGSize size = [text sizeWithFont:[UIFont systemFontOfSize:14] constrainedToSize:CGSizeMake(maxWidth, 30) lineBreakMode:UILineBreakModeTailTruncation];
    return size.width;
}

- (void)setSoftInfo:(SoftItem *)item
{
    appName.text = item.softName;
    NSString *leftText = [NSString stringWithFormat:@"%@", item.localVersion];
    currentVersion.text = leftText;
    
    direction.text = @"-->";
    
    NSString *rightText = [NSString stringWithFormat:@"%@", item.version];
    nextVersion.text = rightText;
    
    NSString *sizeText = [NSString stringWithFormat:@"%@", [CommUtility readableFileSize:item.totalLen]];
    appSize.text = sizeText;
    
    [appImage setImageWithURL:item.iconUrl placeholderImage:item.defaultIcon];
    
    self.appIdentifier = item.identifier;
    [self setRightButtonAction:self action:@selector(buttonPressed:)];
    
//    NSString *title = [item fileExist] ? @"安装" : @"升级";
//    [self.rightButton setTitle:title forState:UIControlStateNormal];
    
    //智能升级
    if (item.increUpateInfo != nil && !item.increUpateInfo.smartUpdateFailed) {
        self.delLine.hidden = NO;
        self.increSize.hidden = NO;
        self.increSize.text = [NSString stringWithFormat:@"%@", [CommUtility readableFileSize:item.increUpateInfo.increFileSize]];
        
        [self.rightButton setBackgroundImage:[UIImage imageNamed:@"update_increase_normal.png"] forState:UIControlStateNormal];
        [self.rightButton setBackgroundImage:[UIImage imageNamed:@"update_increase_sel.png"] forState:UIControlStateSelected];
        [self.rightButton setTitle:@"智能升级" forState:UIControlStateNormal];
    }
    else {
        self.delLine.hidden = YES;
        self.increSize.hidden = YES;
        
        [self.rightButton setBackgroundImage:[UIImage imageNamed:@"btn_blue.png"] forState:UIControlStateNormal];
        [self.rightButton setBackgroundImage:[UIImage imageNamed:@"btn_blue_down.png"] forState:UIControlStateSelected];
        [self.rightButton setTitle:@"升级" forState:UIControlStateNormal];
    }
    
    if ([item fileExist]) {
        [self.rightButton setTitle:@"安装中" forState:UIControlStateNormal];
    }
    [CommUtility autoLayoutLabelsInLine:[NSArray arrayWithObjects:currentVersion, direction, nextVersion, nil]];
    [CommUtility autoLayoutLabelsInLine:[NSArray arrayWithObjects:appSize, increSize, nil]];
    CGRect rc = self.delLine.frame;
    rc.origin.x = CGRectGetMinX(self.appSize.frame);
    rc.size.width = CGRectGetWidth(self.appSize.frame);
    self.delLine.frame = rc;
}

- (void)buttonPressed:(UIButton *)btn
{
    NSString *identifier = self.appIdentifier;
    SoftItem *item = [[SoftManagementCenter sharedInstance] softItemForIdentifier:identifier];
    
    if (item.installStatus != INSTALL_DEFAULT_STATE) {
        return;
    }
    
    if ([item fileExist])
    {
        [MBProgressHUD showBlockHUD:YES];
        [self performSelector:@selector(installItem:) withObject:item afterDelay:0.01];        
    }
    else
    {
        [[SoftManagementCenter sharedInstance] updateTask:identifier];
    }
}

- (void)installItem:(SoftItem *)item
{
    BOOL res = [[SoftManagementCenter sharedInstance] install:item.identifier];
    [MBProgressHUD hideBlockHUD:YES];
    if (res == NO)
    {
        [MBProgressHUD showHintHUD:@"安装失败" message:nil hideAfter:DEFAULT_TIP_LAST_TIME];
    }
}


+ (CGFloat)cellHeight {
    return 70.0;
}

- (void)adjustForDetailViewWithInfo:(AppDetailViewInfo *)info
{
//    self.frame = CGRectOffset(self.frame, 5, 0);
    self.frame = CGRectMake(5, 0, 320, 66);
    self.rightButton.hidden = YES;

    if (info) {
        [self addTintViewForIsGreen:info.isGreen];
    }
}

- (void)addTintViewForIsGreen:(int)isGreen
{
    CGRect rc = self.frame;
    rc.size.height += TINTVIEWHEIGHT;
    UIView *tintView = [[[UIView alloc] initWithFrame:CGRectMake(0, self.frame.size.height, 320, TINTVIEWHEIGHT)] autorelease];
    self.frame = rc;
    tintView.backgroundColor = [CommUtility colorWithHexRGB:@"7fbd35"];
    [self addHintOnView:tintView isGreen:isGreen];
    
    [self addSubview:tintView];
}


- (void)addHintOnView:(UIView *)view isGreen:(int)isGreen
{
    //
    UIImageView *imageView = [[[UIImageView alloc] initWithFrame:CGRectMake(20, 14 - FINTIMAGE_HEIGHT / 2, FINTIMAGE_WIDTH, FINTIMAGE_HEIGHT)] autorelease];
    imageView.image = [UIImage imageNamed:@"check"];
    [view addSubview:imageView];
    
    CGRect rc = imageView.frame;
    rc.origin.x += rc.size.width;
    rc.origin.y = 6;
    rc.size.height = 16;
    rc.size.width = 16 *  3;
    UILabel *label = [[[UILabel alloc] initWithFrame:rc] autorelease];
    label.text = @"官方";
    label.font = [UIFont systemFontOfSize:12.0];
    label.textColor = [UIColor whiteColor];
    label.backgroundColor = [UIColor clearColor];
    [view addSubview:label];
    
    //
    rc.origin.x += rc.size.width;
    rc.origin.y = 8;
    rc.size.height = FINTIMAGE_HEIGHT;
    rc.size.width = FINTIMAGE_WIDTH;
    imageView = [[[UIImageView alloc] initWithFrame:rc] autorelease];
    imageView.image = [UIImage imageNamed:@"check"];
    [view addSubview:imageView];
    
    rc.origin.x += rc.size.width;
    rc.origin.y = 6;
    rc.size.height = 16;
    rc.size.width = 16 * 3 + 12;
    label = [[[UILabel alloc] initWithFrame:rc] autorelease];
    label.font = [UIFont systemFontOfSize:12.0];
    label.textColor = [UIColor whiteColor];
    label.backgroundColor = [UIColor clearColor];
    label.text = @"无病毒";
    [view addSubview:label];
    
    //
    
    rc.origin.x += rc.size.width;
    rc.origin.y = 8;
    rc.size.height = FINTIMAGE_HEIGHT;
    rc.size.width = FINTIMAGE_WIDTH;
    imageView = [[[UIImageView alloc] initWithFrame:rc] autorelease];
    imageView.image = [UIImage imageNamed:@"check"];
    [view addSubview:imageView];
    rc.origin.x += rc.size.width;
    rc.origin.y = 6;
    rc.size.height = 16;
    rc.size.width = 16 * 3;
    label = [[[UILabel alloc] initWithFrame:rc] autorelease];
    label.font = [UIFont systemFontOfSize:12.0];
    label.backgroundColor = [UIColor clearColor];
    label.text = @"无广告";
    label.textColor = [UIColor whiteColor];
    if (isGreen == 0 ) {
        imageView.image = [UIImage imageNamed:@"cross"];
        label.text = @"无广告";
        label.textColor = [UIColor redColor];
        //frame微调
        rc = imageView.frame;
        rc.origin.x += 2;
        rc.origin.y += 2;
        rc.size.width -= 3;
        rc.size.height -= 3;
        imageView.frame = rc;
    }
    
    [view addSubview:label];
}

@end
