//
//  ClassificationRecommendCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/12/13.
//
//

#import "CatagoryDetailCell.h"
#import "UIImageView+WebCache.h"
#import "CommUtility.h"

#import "SoftManagementCenter.h"
#import "ProgessButton.h"
#import "SoftItem.h"
#import "AppDescriptionInfo.h"

#import "CustomStarsView.h"

#import "ColorfulImage.h"
#import "CommUtility.h"
#import "Colors.h"

#define GAME_SCORES_TAG 50

#define kButtonImageReseted       @"btn_gray.png"

@interface CatagoryDetailCell ()

@property (retain, nonatomic) IBOutlet UIImageView *gameIcon;
@property (retain, nonatomic) IBOutlet UILabel *gameName;
@property (retain, nonatomic) IBOutlet CustomStarsView *gameScores;
@property (retain, nonatomic) IBOutlet UILabel *fileSize;
@property (retain, nonatomic) IBOutlet UILabel *activityLable;
@property (retain, nonatomic) IBOutlet UIImageView *activityCornerMark;

@end

@implementation CatagoryDetailCell

- (void)dealloc {
    self.gameName = nil;
    self.gameIcon = nil;
    self.gameScores = nil;
    self.fileSize = nil;
    self.gameStateButton = nil;
    self.activityLable = nil;
    [_activityCornerMark release];
    [super dealloc];
}

- (void)reset
{
    [self.gameStateButton reset];
}

- (void)awakeFromNib
{
	[self reset];
}

- (void)updateBtnState:(SoftItem *)item
{
    [self.gameStateButton updateProgessButtonState:item.identifier];
}

- (void)updateCellWithAppInfo:(AppDescriptionInfo *)appInfo
{
    [self.gameIcon setImageWithURL:[NSURL URLWithString:appInfo.appIconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
	self.gameName.text = appInfo.appName;
    
    [self.gameScores resetCustomStarsViewWithNumber:appInfo.appScore];
    
    [self fetchActivitiesLabel:appInfo];
    
    self.fileSize.text = [NSString stringWithFormat:@"%@", [CommUtility readableFileSize:appInfo.fileSize]];
    
    [self.gameStateButton setProgressButtonInfo:appInfo.identifier f_id:appInfo.f_id softName:appInfo.appName iconUrl:appInfo.appIconUrl];
    
//    [self.gameDownloadButton resignFirstResponder];
//    [self.gameDownloadButton updateProgessButtonState:appInfo.identifier];
    
    UIImage *imageGameDownloadButton = [UIImage imageNamed:kButtonImageReseted];
    UIFont *butttonTitleFont = [UIFont systemFontOfSize:10.0];
    UIColor *buttonTitleColor = [CommUtility colorWithHexRGB:@"666666"];
    
    [self.gameStateButton resetNormalButtonWithFontSize:butttonTitleFont];
    [self.gameStateButton resetNormalButtonWithTitleColor:buttonTitleColor];
    [self.gameStateButton resetNormalButtonWithBackgroundImage:imageGameDownloadButton];
    [self.gameStateButton resetPercentFontSize:[UIFont systemFontOfSize:12.0]];
}

+ (CGFloat)cellHeight
{
    return 63.0f;
}

#pragma mark -

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
    if (name.length == 0) {
        self.activityLable.text = nil;
        self.activityCornerMark.image = nil;
        self.activityLable.backgroundColor = [UIColor clearColor];
    } else {
        self.activityLable.text = name;
        self.activityCornerMark.image = [UIImage imageNamed:@"activity_cornerMark.png"];
        self.activityLable.backgroundColor = [UIColor clearColor];
    }
}

@end