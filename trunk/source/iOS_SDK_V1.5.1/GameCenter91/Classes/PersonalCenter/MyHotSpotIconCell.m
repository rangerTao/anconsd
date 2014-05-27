//
//  MyHotSpotIconCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 11/8/13.
//
//

#import "MyHotSpotIconCell.h"
#import "HotInfo.h"
#import "UIImageView+WebCache.h"
#import "MSLabel.h"

@interface MyHotSpotIconCell ()

@property (retain, nonatomic) IBOutlet UIImageView *myHotSpotIcon;
@property (retain, nonatomic) IBOutlet UILabel *myHotSpotTitle;
@property (retain, nonatomic) IBOutlet MSLabel *myHotSpotDetail;

@end

@implementation MyHotSpotIconCell

- (void)dealloc {
    self.myHotSpotIcon = nil;
    self.myHotSpotTitle = nil;
    self.myHotSpotDetail = nil;
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

- (void)refreshCellWithHotInfo:(HotInfo *)myHotSpot
{
    [self.myHotSpotIcon setImageWithURL:[NSURL URLWithString:myHotSpot.imageUrl] placeholderImage:[UIImage imageNamed:@"nd_gc_icon_default_hot_deluxe.jpg"]];

    if (myHotSpot.tagName.length > 0) {
        self.myHotSpotTitle.text = [NSString stringWithFormat:@"%@ | %@", myHotSpot.tagName, myHotSpot.title];
    } else {
        self.myHotSpotTitle.text = myHotSpot.title;
    }

    self.myHotSpotDetail.text = myHotSpot.content;
//    self.myHotSpotDetail.numberOfLines = 2;
    self.myHotSpotDetail.lineHeight = 18;
    self.myHotSpotDetail.verticalAlignment = MSLabelVerticalAlignmentTop;
}


@end
