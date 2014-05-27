//
//  MyGameActivityCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 11/10/13.
//
//

#import "MyGameActivityCell.h"
#import "MyGameInfo.h"
#import "ActiveInfo.h"
#import "NSArray+Extent.h"
#import "ActivityInfo.h"

@interface MyGameActivityCell ()

@property (retain, nonatomic) IBOutlet UIImageView *activityIcon;
@property (retain, nonatomic) IBOutlet UILabel *activityTitle;

@end

@implementation MyGameActivityCell

- (void)dealloc {
    self.activityIcon = nil;
    self.activityTitle = nil;
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

- (void)refreshCellWithMyGameInfo:(MyGameInfo *)myGame indexRow:(NSInteger)row
{
    NSInteger realIndexRow = [self realIndexRowInActivities:row];
    ActiveInfo *ActiveInfo = [myGame.activeList valueAtIndex:realIndexRow];
    
    [self assignActivityIcon:ActiveInfo];
    [self assignActivityTitle:ActiveInfo];
}

- (void)assignActivityTitle:(ActiveInfo *)activeInfo
{
    self.activityTitle.text = activeInfo.title;
}

- (void)assignActivityIcon:(ActiveInfo *)activeInfo
{
    switch (activeInfo.activityType) {
        case ACTIVITY_TYPE_GAME_GIFT:
            self.activityIcon.image = [UIImage imageNamed:@"giftBag.png"];
            break;
        case ACTIVITY_TYPE_ACTIVITY_NOTICE:
            self.activityIcon.image = [UIImage imageNamed:@"activity.png"];
            break;
        case ACTIVITY_TYPE_PRIZE_NOTICE:
            self.activityIcon.image = [UIImage imageNamed:@"notice.png"];
            break;
        case ACTIVITY_TYPE_NEW_SERVERS_NOTICE:
            self.activityIcon.image = [UIImage imageNamed:@"kaifu.png"];
            break;
        default:
            break;
    }
}

- (NSInteger)realIndexRowInActivities:(NSInteger)row
{
    return row - 4;
}

@end