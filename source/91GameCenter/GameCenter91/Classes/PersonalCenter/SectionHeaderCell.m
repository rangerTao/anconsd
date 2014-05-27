//
//  SectionHeaderCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 11/12/13.
//
//

#import "SectionHeaderCell.h"
#import "AppInfo.h"

@interface SectionHeaderCell ()

@property (retain, nonatomic) IBOutlet UILabel *sectionHeaderTitle;
@property (retain, nonatomic) IBOutlet UIButton *sectionHeaderButton;
@property (retain, nonatomic) IBOutlet UIImageView *promptPoint;
@property (retain, nonatomic) IBOutlet UIView *colorLine;

@end

@implementation SectionHeaderCell

- (void)dealloc {
    self.appList = nil;
    self.sectionHeaderTitle = nil;
    self.sectionHeaderButton = nil;
    self.promptPoint = nil;
    self.colorLine = nil;
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

- (void)refreshCellWithSectionHeaderType:(SECTION_TYPE)sectionHeaderType
{
    if (sectionHeaderType == SECTION_MY_HOT_SPOT) {
        [self refreshMyHotSpotHeaderSection];
    }
    
    if (sectionHeaderType == SECTION_MY_GAMES) {
        [self refreshMyGamesSection];
            }
    
    if (sectionHeaderType == SECTION_DAY_RECOMMEND) {
        [self refreshDayRecommendSection];
    }
}

- (void)refreshMyHotSpotHeaderSection
{
    [self.sectionHeaderButton removeTarget:nil action:NULL forControlEvents:UIControlEventAllEvents];
    
    UIColor *promptColor = [UIColor colorWithRed:0xFF/255.0 green:0x63/255.0 blue:0x17/255.0 alpha:1.0];
    
    self.sectionHeaderTitle.text = @"我的热点";
    self.sectionHeaderTitle.textColor = promptColor;
    
    self.colorLine.backgroundColor = promptColor;
    
    self.sectionHeaderButton.hidden = NO;
    [self.sectionHeaderButton setTitle:@"更多>" forState:UIControlStateNormal];
    [self.sectionHeaderButton addTarget:self.fatherController action:@selector(showMoreMyHotSpots:) forControlEvents:UIControlEventTouchUpInside];
    
    self.promptPoint.image = nil;
}

- (void)refreshMyGamesSection
{
    [self.sectionHeaderButton removeTarget:nil action:NULL forControlEvents:UIControlEventAllEvents];
    
    UIColor *promptColor = [UIColor colorWithRed:0x14/255.0 green:0x7F/255.0 blue:0xB5/255.0 alpha:1.0];
    
    self.sectionHeaderTitle.text = @"我的游戏";
    self.sectionHeaderTitle.textColor = promptColor;
    
    self.colorLine.backgroundColor = promptColor;
    
    self.sectionHeaderButton.hidden = NO;
    
    [self.sectionHeaderButton setTitle:@"编辑>" forState:UIControlStateNormal];
    [self.sectionHeaderButton addTarget:self.fatherController action:@selector(editMyGames:) forControlEvents:UIControlEventTouchUpInside];
    
    if ([self isNewGameExist] == YES) {
        self.promptPoint.image = [UIImage imageNamed:@"reminder.png"];
    } else {
        self.promptPoint.image = nil;
    }
}

- (BOOL)isNewGameExist
{
    for (NSInteger index = 0; index < [self.appList count]; index++) {
        AppInfo *appInfo = [self.appList objectAtIndex:index];
        if (appInfo.bNewGame == YES) {
            return YES;
        }
    }
    return NO;
}

- (void)refreshDayRecommendSection
{
    [self.sectionHeaderButton removeTarget:nil action:NULL forControlEvents:UIControlEventAllEvents];
    
    UIColor *promptColor = [UIColor colorWithRed:0x5A/255.0 green:0x91/255.0 blue:0x03/255.0 alpha:1.0];
    self.sectionHeaderTitle.text = @"每日推荐";
    self.sectionHeaderTitle.textColor = promptColor;
    
    self.colorLine.backgroundColor = promptColor;
    
    self.sectionHeaderButton.hidden = YES;
    
    self.promptPoint.image = nil;
}

@end
