//
//  RecommendationDialyCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "RecommendationDialyCell.h"
#import "DayRecommendInfo.h"
#import "UIImageView+WebCache.h"

@interface RecommendationDialyCell ()

@property (retain, nonatomic) IBOutlet UIImageView *recommendationTodayIcon;
@property (retain, nonatomic) IBOutlet UILabel *recommendationTodayName;
@property (retain, nonatomic) IBOutlet UILabel *recommendationTodayReview;

@end

@implementation RecommendationDialyCell

- (void)dealloc {
    self.recommendationTodayIcon = nil;
    self.recommendationTodayName = nil;
    self.recommendationTodayReview = nil;
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

- (void)refreshRecommendationToday:(DayRecommendInfo *)recommendationToday
{
    [self.recommendationTodayIcon setImageWithURL:[NSURL URLWithString:recommendationToday.imageUrl] placeholderImage:[UIImage imageNamed:@"nd_gc_icon_default_day_recommend_deluxe.jpg"]];
    
    self.recommendationTodayName.text = recommendationToday.title;
    self.recommendationTodayReview.text = recommendationToday.summary;
}
@end
