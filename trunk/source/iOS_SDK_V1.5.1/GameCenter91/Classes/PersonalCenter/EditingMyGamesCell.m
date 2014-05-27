//
//  gamesDownloadedEditingCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/24/13.
//
//

#import "EditingMyGamesCell.h"
#import "AppInfo.h"
#import "EditingMyGamesController.h"
#import "ActiveInfo.h"
#import "CommUtility.h"
#import "UIImageView+WebCache.h"
#import "NSArray+Extent.h"
#import "ColorfulLabelView.h"

#define TAG_OFFSET 1000


@interface EditingMyGamesCell ()

@property (retain, nonatomic) IBOutlet UIImageView *myGameIndex;
@property (retain, nonatomic) IBOutlet UIImageView *myGameIcon;
@property (retain, nonatomic) IBOutlet UIImageView *newIcon;
@property (retain, nonatomic) IBOutlet UILabel *myGameName;

@property (nonatomic, retain) NSMutableArray *activityLables;

@end

@implementation EditingMyGamesCell

- (void)dealloc {
    self.myGameIndex = nil;
    self.myGameIcon = nil;
    self.myGameName = nil;
    self.newIcon = nil;
    self.activityLables = nil;
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

- (void)refreshEditingMyGamesCell:(NSMutableArray *)appList withIndexPath:(NSIndexPath *)indexPath
{
    self.backgroundColor = [UIColor whiteColor];
    AppInfo *myGame = [appList valueAtIndexPath:indexPath];
    if (indexPath.section == 0) {
        if (indexPath.row >=0 && indexPath.row <= 3) {
            self.myGameIndex.image = [UIImage imageNamed:[NSString stringWithFormat:@"%d.png",indexPath.row + 1]];
        }
        else {
            self.myGameIndex.image = nil;
        }
    } else {
        self.myGameIndex.image = nil;
    }
    
    [self.myGameIcon setImageWithURL:[NSURL URLWithString:myGame.appIconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
    
    self.myGameName.text = myGame.appName;
    
    if (myGame.bNewGame) {
        self.newIcon.image = [UIImage imageNamed:@"left_down_cornerMark.png"];
    } else {
        self.newIcon.image = nil;
    }
    
    [self fetchActivitiesLabels:myGame.labelIcons];
}

- (void)assignColorsAndSizes
{
    self.myGameName.font = [UIFont systemFontOfSize:14.0];
    self.myGameName.textColor = [UIColor colorWithRed:0x33/255.0 green:0x33/255.0 blue:0x33/255.0 alpha:1.0];
}

- (void)fetchActivitiesLabels:(NSString *)labelIcons
{
    [self removeActivitiesLabels];
    self.activityLables = [NSMutableArray array];
    [self assignActivitiesLables:labelIcons];
}

- (void)removeActivitiesLabels
{
    for (NSInteger index = 0; index < [self.activityLables count]; index++) {
        ColorfulLabelView *label = (ColorfulLabelView *)[self viewWithTag:TAG_OFFSET + index];
        if (label != nil) {
            [label removeFromSuperview];
        }
    }
}

- (void)assignActivitiesLables:(NSString *)labelIcons
{
    
    if (labelIcons != nil) {
        NSArray *activitiesArray = [CommUtility unPackRecommendIconsStr:labelIcons];
        for (NSInteger index = 0; index < [activitiesArray count]; index++) {
            
            ColorfulLabelView *label = [self fetchActivityLabelWithDictionary:[activitiesArray valueAtIndex:index]];
            
            [self.activityLables addObject:label];
            
            label.tag = TAG_OFFSET + index;
            
            [self.contentView addSubview:label];
        }

    }
}

- (ColorfulLabelView *)fetchActivityLabelWithDictionary:(NSDictionary *)dict
{
    CGFloat x = 98;
    
    for (NSInteger index = 0; index < [self.activityLables count]; index++) {
        ColorfulLabelView *label = [self.activityLables objectAtIndex:index];
        
        x += label.frame.size.width + 2;
    }
    
    NSString *name = [dict objectForKey:KEY_RI_NAME];
    NSString *textColor = [dict objectForKey:KEY_RI_FONTCOLOR];
    NSString *bgColor = [dict objectForKey:KEY_RI_BGCOLOR];
    
    ColorfulLabelView *label = [ColorfulLabelView colorfulLabelView:name fontSize:12 bgColor:bgColor fontColor:textColor bVogue:NO];
    
    CGRect frame = label.frame;
    frame.origin.x = x;
    frame.origin.y = 33;
    label.frame = frame;
    
    return label;
}

@end
