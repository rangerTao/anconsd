//
//  GameTableViewCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/14/13.
//
//

#import "GameTableViewCell.h"
#import "UIImageView+WebCache.h"
#import "ColorfulLabelView.h"

#import "SoftItem.h"
#import "SoftManagementCenter.h"
#import "MBProgressHUD.h"
#import "CommUtility.h"
#import "ProgessButton.h"
#import "AppDescriptionInfo.h"
#import "AppDetailCacheInfo.h"
#import "CustomStarsView.h"
#import "ColorfulImage.h"

#define FINTIMAGE_WIDTH    12
#define FINTIMAGE_HEIGHT   12
#define TINTVIEWHEIGHT  28

#define kButtonImageReseted       @"btn_gray.png"

@interface GameTableViewCell()

@property (retain, nonatomic) IBOutlet UIImageView *gameIcon;
@property (retain, nonatomic) IBOutlet UILabel *gameName;
@property (retain, nonatomic) IBOutlet CustomStarsView *gameScores;
@property (retain, nonatomic) IBOutlet UILabel *gameDownloadNumber;
@property (retain, nonatomic) IBOutlet UILabel *gameVersionAndSize;
@property (retain, nonatomic) IBOutlet UILabel *activityLable;

@property (retain, nonatomic) IBOutlet UIView *lineView;
@property(nonatomic, assign) ColorfulLabelView *colorLabels;
@property (retain, nonatomic) IBOutlet UIImageView *activityCornerMark;

@end

@implementation GameTableViewCell

- (void)dealloc {
    [self clearAllProperty];
    self.lineView = nil;
    self.activityLable = nil;
    [_activityCornerMark release];
    [super dealloc];
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#pragma mark -
-(void)reset
{
    [self.gameStateButton reset];
}

-(void) awakeFromNib
{
    [super awakeFromNib];
	[self reset];
}

#pragma mark -
- (void)updateBtnState:(SoftItem *)item
{
    [self.gameStateButton updateProgessButtonState:item.identifier];
}

- (void)updateCellWithAppInfo:(AppDescriptionInfo *)appInfo
{

    [self.gameIcon setImageWithURL:[NSURL URLWithString:appInfo.appIconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
	self.gameName.text = appInfo.appName;
    
    [self fetchActivitiesLabel:appInfo];
    
    [self.gameScores resetCustomStarsViewWithNumber:appInfo.appScore];
    
    self.gameDownloadNumber.text = [NSString stringWithFormat:@"%@次下载", [CommUtility readableDownloadNumber: appInfo.downloadNumber ] ];
	self.gameVersionAndSize.text = [NSString stringWithFormat:@"版本%@ | %@", appInfo.appVersionName ,[CommUtility readableFileSize:appInfo.fileSize]];
    
    [self.gameStateButton setProgressButtonInfo:appInfo.identifier f_id:appInfo.f_id softName:appInfo.appName iconUrl:appInfo.appIconUrl];
    [self assignStateButton];
    
    UIImageView *bgImgView = [[[UIImageView alloc] init] autorelease];
    UIImage *bgSelectedImage = [ColorfulImage imageWithColor:[CommUtility colorWithHexRGB:@"b1daec"]];
    bgImgView.image = bgSelectedImage;
    self.selectedBackgroundView = bgImgView;
}

- (void)cellAdjustForGroupStyle
{
    for (UIView *view in self.contentView.subviews) {
        CGRect rect = view.frame;
        rect.origin.x -= 5;
        view.frame = rect;
    }
    
}
- (void)adjustForDetailViewCtrWithInfo:(AppDetailViewInfo *)detailInfo
{
    if (detailInfo) {
   
        self.gameName.hidden = NO;
        self.gameScores.hidden = NO;
        self.gameDownloadNumber.hidden = NO;
        self.gameVersionAndSize.hidden = NO;
    }else
    {
        self.gameName.hidden = YES;
        self.gameScores.hidden = YES;
        self.gameDownloadNumber.hidden = YES;
        self.gameVersionAndSize.hidden = YES;
    }
    self.frame = CGRectOffset(self.frame, 5, 0);
    [self.gameIcon setImageWithURL:[NSURL URLWithString:detailInfo.appIconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
    self.gameIcon.frame = CGRectOffset(self.gameIcon.frame, 2, 0);
    
	self.gameName.text = detailInfo.appName;
    self.gameName.font = [UIFont systemFontOfSize:14.0];
    self.gameName.textColor = [CommUtility colorWithHexRGB:@"333333"];
    
    [self.gameScores resetCustomStarsViewWithNumber:detailInfo.appScore];

    self.gameDownloadNumber.text = [NSString stringWithFormat:@"%@次下载", [CommUtility readableDownloadNumber: detailInfo.downloadNumber ]];
    self.gameDownloadNumber.font = [UIFont systemFontOfSize:11.0];
    self.gameDownloadNumber.textColor = [CommUtility colorWithHexRGB:@"666666"];
    self.gameDownloadNumber.frame = CGRectOffset(self.gameDownloadNumber.frame, -3, 0);
    
    NSString *language = (detailInfo.isChinese == 1)? @"中文":@"非中文";
	self.gameVersionAndSize.text = [NSString stringWithFormat:@"版本%@  | %@  | %@",detailInfo.appVersionName ,[CommUtility readableFileSize:detailInfo.fileSize],language];
    self.gameVersionAndSize.font = [UIFont systemFontOfSize:11.0];
    self.gameVersionAndSize.textColor = [CommUtility colorWithHexRGB:@"666666"];
    
    self.gameStateButton.hidden = YES;
    self.lineView.hidden = YES;
    if (detailInfo) {
        [self addTintViewForIsGreen:detailInfo.isGreen];
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

+ (CGFloat)cellHeight
{
    return 66.f;
}

#pragma mark - others
- (void)fetchActivitiesLabel:(AppDescriptionInfo *)appInfo
{
    NSArray *activitiesArray = [CommUtility unPackRecommendIconsStr:appInfo.labelIcons];
    
    NSMutableArray *activitiesNameArray = [NSMutableArray array];
    for (NSInteger index = 0; index < [activitiesArray count]; index++) {
        NSDictionary *dict = [activitiesArray objectAtIndex:index];
        [activitiesNameArray addObject:[dict objectForKey:KEY_RI_NAME]];
    }
    
    if ([activitiesNameArray count] > 0) {
        [self setActivityLablewithName:[activitiesNameArray objectAtIndex:0]];
    } else {
        [self setActivityLablewithName:@""];
    }
}

- (void)setActivityLablewithName:(NSString *)name
{
    if ([name isEqualToString:@""]) {
        self.activityLable.text = nil;
        self.activityLable.backgroundColor = [UIColor clearColor];
        self.activityCornerMark.image = nil;
    } else {
        self.activityLable.text = name;
        self.activityCornerMark.image = [UIImage imageNamed:@"activity_cornerMark.png"];
        self.activityLable.backgroundColor = [UIColor clearColor];
    }
}

- (void)assignStateButton
{
    UIImage *imageGameDownloadButton = [UIImage imageNamed:kButtonImageReseted];
    UIFont *butttonTitleFont = [UIFont boldSystemFontOfSize:12.0];
    UIColor *buttonTitleColor = [UIColor colorWithRed:0x66/255.0 green:0x65/255.0 blue:0x65/255.0 alpha:1.0];
    [self.gameStateButton resetNormalButtonWithFontSize:butttonTitleFont];
    [self.gameStateButton resetNormalButtonWithTitleColor:buttonTitleColor];
    [self.gameStateButton resetNormalButtonWithBackgroundImage:imageGameDownloadButton];
    
    [self.gameStateButton resetPercentFontSize:[UIFont systemFontOfSize:12.0]];
}

@end
