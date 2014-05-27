//
//  HomePageMyHotSpotCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 11/12/13.
//
//

#import "HomePageMyHotSpotCell.h"
#import "CommUtility.h"
#import "HotInfo.h"

@implementation HomePageMyHotSpotCell

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

- (void)refreshCellWithHotInfo:(HotInfo *)hotInfo
{
    self.imageView.image = nil;
    self.detailTextLabel.text = nil;
    
    if (hotInfo.titleTagColor == nil) {
        self.textLabel.textColor = [CommUtility colorWithHexRGB:@"333333"];
    } else {
        self.textLabel.textColor = [CommUtility colorWithHexRGB:hotInfo.titleTagColor];
        self.textLabel.backgroundColor = [UIColor clearColor];
    }
        
    if (hotInfo.tagName.length > 0) {
        self.textLabel.text = [NSString stringWithFormat:@"%@ | %@",hotInfo.tagName,hotInfo.title];
    } else {
        self.textLabel.text = hotInfo.title;
    }
    
    self.textLabel.font = [UIFont systemFontOfSize:16];
    
    UIImageView *view = [[[UIImageView alloc] initWithFrame:CGRectMake(281, 8, 19, 19)] autorelease];
    
    switch (hotInfo.hotType) {
        case HOT_TYPE_GIFT:
            view.image = [UIImage imageNamed:@"giftBag.png"];
            break;
        case HOT_TYPE_ACTIVITY:
            view.image = [UIImage imageNamed:@"activity.png"];
            break;
        case HOT_TYPE_NOTICE:
            view.image = [UIImage imageNamed:@"notice.png"];
            break;
        case HOT_TYPE_OPEN_SERVERS:
            view.image = [UIImage imageNamed:@"kaifu.png"];
            break;
        default:
            break;
    }
    self.accessoryView = view;
}

@end
