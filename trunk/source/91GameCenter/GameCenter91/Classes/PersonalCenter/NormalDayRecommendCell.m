//
//  NormalDayRecommendCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 11/8/13.
//
//

#import "NormalDayRecommendCell.h"
#import "DayRecommendInfo.h"
#import "UIImageView+WebCache.h"
#import "CommUtility.h"

@interface NormalDayRecommendCell ()

@property (retain, nonatomic) IBOutlet UIImageView *noramlDayRecommendIcon;
@property (retain, nonatomic) IBOutlet UILabel *normalDayRecommendName;
@property (retain, nonatomic) IBOutlet UILabel *normalDayRecommendDetail;
@property (retain, nonatomic) IBOutlet UILabel *normalDayRecommendActivityLable;
@property (retain, nonatomic) IBOutlet UIImageView *activityCornerMark;

@end

@implementation NormalDayRecommendCell

- (void)dealloc {
    self.noramlDayRecommendIcon = nil;
    self.normalDayRecommendName = nil;
    self.normalDayRecommendDetail = nil;
    self.normalDayRecommendActivityLable = nil;
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

- (void)refreshCellWithDayRecommendInfo:(DayRecommendInfo *)dayRecommendInfo
{
    [self.noramlDayRecommendIcon setImageWithURL:[NSURL URLWithString:dayRecommendInfo.appIconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
    
    self.normalDayRecommendName.text = dayRecommendInfo.title;
    
    self.normalDayRecommendDetail.text = dayRecommendInfo.summary;
    
    [self fetchActivitiesLabel:dayRecommendInfo];
}

- (void)fetchActivitiesLabel:(DayRecommendInfo *)dayRecommendInfo
{
    NSArray *activitiesArray = [CommUtility unPackRecommendIconsStr:dayRecommendInfo.labelIcons];
    
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
        self.normalDayRecommendActivityLable.text = nil;
        self.activityCornerMark.image = nil;
        self.normalDayRecommendActivityLable.backgroundColor = [UIColor clearColor];
    } else {
        self.normalDayRecommendActivityLable.text = name;
        self.normalDayRecommendActivityLable.textAlignment = UITextAlignmentRight;
        UIImage *image = [UIImage imageNamed:@"activity_cornerMark.png"];
        self.activityCornerMark.image= image;
        self.normalDayRecommendActivityLable.backgroundColor = [UIColor clearColor];
    }
}


@end
