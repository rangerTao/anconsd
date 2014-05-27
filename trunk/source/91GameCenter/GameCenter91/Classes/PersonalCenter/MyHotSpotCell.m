//
//  MyHotSpotCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/25/13.
//
//

#import "MyHotSpotCell.h"
#import "MyHotSpotsInfo.h"
#import "UIImageView+WebCache.h"
#import "AssigningControlBackgroundView.h"
#import "NSDate+Utilities.h"
#import "CommUtility.h"

#define kActivityOverImage                @"end.png"
#define kServersStartImage                @"yikaifu.png"

@interface MyHotSpotCell ()

@property (retain, nonatomic) IBOutlet UIImageView *myHotSpotImage;
@property (retain, nonatomic) IBOutlet UILabel *myHotSpotTypeTitle;
@property (retain, nonatomic) IBOutlet UILabel *myHotSpotContent;
@property (retain, nonatomic) IBOutlet UILabel *myHotSpotTime;
@property (retain, nonatomic) IBOutlet UIImageView *myHotSpotOutOfDateImage;

@end

@implementation MyHotSpotCell

- (void)dealloc {
    self.myHotSpotImage = nil;
    self.myHotSpotTypeTitle = nil;
    self.myHotSpotContent = nil;
    self.myHotSpotTime = nil;
    [_myHotSpotOutOfDateImage release];
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

- (void)refreshMyHotSpotCell:(MyHotSpotsInfo *)myHotSpot
{
    if ([myHotSpot.imageUrl length] > 0) {
        [self.myHotSpotImage setImageWithURL:[NSURL URLWithString:myHotSpot.imageUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
    } else {
        self.myHotSpotImage.image = [UIImage imageNamed:@"spotDefault.png"];
    }

    if (myHotSpot.tagName.length > 0) {
        self.myHotSpotTypeTitle.text = [NSString stringWithFormat:@"%@ | %@", myHotSpot.tagName, myHotSpot.title];
    } else {
        self.myHotSpotTypeTitle.text = myHotSpot.title;
    }
    
    
    if ([myHotSpot.content isEqualToString:@""]) {
        self.myHotSpotContent.text = myHotSpot.title;
    } else {
        self.myHotSpotContent.text = myHotSpot.content;
    }
    
    NSString *timeText = [NSString string];
    if ([myHotSpot.showTime isEqualToString:@""]) {
        timeText = nil;
    } else {
        
        NSString *timeInfo = nil;
        if (myHotSpot.hotType == MY_HOT_TYPE_OPEN_SERVERS) {
            timeInfo = [myHotSpot.showTime substringToIndex:16];
        } else {
            timeInfo = [myHotSpot.showTime substringToIndex:10];
        }
        
        switch (myHotSpot.hotType) {
            case MY_HOT_TYPE_PLATFORM:
            case MY_HOT_TYPE_GIFT:
            case MY_HOT_TYPE_ACTIVITY:
                timeText = [NSString stringWithFormat:@"截止时间：%@",timeInfo];
                break;
            case MY_HOT_TYPE_OPEN_SERVERS:
                timeText = [NSString stringWithFormat:@"开服时间：%@",timeInfo];
                break;
            case MY_HOT_TYPE_APP_RECOMMEND:
            case MY_HOT_TYPE_NOTICE:
            case MY_HOT_TYPE_STRATEGY:
                timeText = [NSString stringWithFormat:@"发布时间：%@",timeInfo];
                break;
            default:
                break;
        }
    }
    
    self.myHotSpotTime.text = timeText;
    
    [self assignAdditionalInfo:myHotSpot];
}

- (void)assignAdditionalInfo:(MyHotSpotsInfo *)myHotSpot
{
    NSDate *endDate = ([myHotSpot.showTime length] == 0) ? [[NSDate date] dateByAddingDays:999]:[CommUtility dateFromString:myHotSpot.showTime];
    
    self.myHotSpotTypeTitle.font = [UIFont systemFontOfSize:16];
    switch (myHotSpot.hotType) {
        case MY_HOT_TYPE_PLATFORM:
        case MY_HOT_TYPE_GIFT:
        case MY_HOT_TYPE_ACTIVITY:
            if ([endDate isEarlierThanDate:[NSDate date]]) {
                self.myHotSpotOutOfDateImage.image = [UIImage imageNamed:kActivityOverImage];
            } else {
                self.myHotSpotOutOfDateImage.image = nil;
                
                if (myHotSpot.hotType == MY_HOT_TYPE_GIFT) {
                    NSString *giftNumberString = [NSString stringWithFormat:@"  剩余%d个",myHotSpot.giftNumber];
                    self.myHotSpotTime.text = [self.myHotSpotTime.text stringByAppendingString:giftNumberString];
                }
            }
            break;
        case MY_HOT_TYPE_NOTICE:
            self.myHotSpotOutOfDateImage.image = nil;
            break;
        case MY_HOT_TYPE_OPEN_SERVERS:
            if ([endDate isEarlierThanDate:[NSDate date]]) {
                self.myHotSpotOutOfDateImage.image = [UIImage imageNamed:kServersStartImage];
            } else {
                self.myHotSpotOutOfDateImage.image = nil;
            }
            break;
        case MY_HOT_TYPE_APP_RECOMMEND:
        case MY_HOT_TYPE_STRATEGY:
            self.myHotSpotOutOfDateImage.image = nil;
            break;
        default:
            self.myHotSpotOutOfDateImage.image = nil;
            break;
    }
}

@end
