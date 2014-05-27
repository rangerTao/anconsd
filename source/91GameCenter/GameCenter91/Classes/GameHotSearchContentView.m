//
//  GameHotSearchContentView.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-12.
//
//

#import "GameHotSearchContentView.h"
#import "GameHotSearchItem.h"
#import "GameDetailController.h"
#import "UIImageView+WebCache.h"
#import "HotSearchItem.h"
#import "CommUtility.h"
#import "HotSearchItem.h"
#define SPACE_BETWEEN_ITEMS 2.0f
#define TAG_BASE            300

@interface GameHotSearchContentView()
@property (nonatomic, assign)CGFloat contentItemWith;
@property (nonatomic, assign)CGFloat contentItemHeight;
@property (nonatomic, retain) NSArray *recommendList;


@end
@implementation GameHotSearchContentView
- (void)dealloc
{
    self.recommendList = nil;
    [super dealloc];
}
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.recommendList = nil;
        [self calculateContentItemSize];
        [self addContentItem];
    }
    return self;
}
- (void)updateWithAppRecommendList:(NSArray *)recommendList
{
    self.recommendList = recommendList;
    for (int i= 0; i < [recommendList count]; i ++) {
        GameHotSearchItem *itemView = (GameHotSearchItem*)[self viewWithTag:TAG_BASE + i];
        itemView.hidden = NO;
        HotSearchItem *itemInfo = [recommendList objectAtIndex:i];
        
        //背景类型
        if (itemInfo.backGroundType != NO_TYPE) {
            itemView.gameTitleLabel.textColor = [UIColor whiteColor];
            itemView.hotSearchLabel.textColor = [UIColor whiteColor];
            itemView.bgImageView.hidden = NO;
        }else
        {
            [itemView.bgImageView setHidden:YES];
        }
        
        if (itemInfo.backGroundType == IMAGE_TYPE) {
            [itemView.bgImageView setImageWithURL:[NSURL URLWithString:itemInfo.backGround]];
            
        }
        
        if (itemInfo.backGroundType == COLOR_TYPE) {
            itemView.backgroundColor = [CommUtility colorWithHexRGB:itemInfo.backGround];
        }
        
        //按钮类型
        if (itemInfo.targetType == HOT_SEARCH_GAME) {
            itemView.gameTitleLabel.text = [NSString stringWithFormat:@"%@",itemInfo.showName];
            [itemView adjustTitleFrame];
            [itemView.gameIconImgView setImageWithURL:[NSURL URLWithString:itemInfo.iconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
            
            itemView.gameIconImgView.hidden = NO;
            itemView.gameTitleLabel.hidden = NO;
            itemView.hotSearchLabel.hidden = YES;
        }
        
        if (itemInfo.targetType == HOT_SEARCH_WORD) {
            itemView.hotSearchLabel.text = [NSString stringWithFormat:@"%@",itemInfo.showName];
            
            itemView.gameIconImgView.hidden = YES;
            itemView.gameTitleLabel.hidden = YES;
            itemView.hotSearchLabel.hidden = NO;
        }
        
        
    }
    
}

- (void)btnItemPress:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    UIView *view = [btn superview];
    int index = view.tag % TAG_BASE;
    if (index >= [self.recommendList count]) {
        return;
    }
    HotSearchItem *item = [self.recommendList objectAtIndex:index];
    if (item.targetType == HOT_SEARCH_GAME) {
        NSString *identifier = item.targetAction;
        [CommUtility pushGameDetailController:identifier gameName:item.showName navigationController:self.parentCtrl.navigationController];
    }else{
        //搜索
        if ([self.parentCtrl respondsToSelector:@selector(doSearchWithKeyword:)]) {
            [self.parentCtrl doSearchWithKeyword:item.showName];
        }
    }
    
}




- (void)calculateContentItemSize
{
    CGSize contentSize = self.frame.size;
    self.contentItemWith = (contentSize.width - SPACE_BETWEEN_ITEMS * 3) / 4;
    self.contentItemHeight = (contentSize.height - SPACE_BETWEEN_ITEMS *3) /4;
    
    
}
- (void)addContentItem
{
    CGFloat originX = 0;
    CGFloat originY = 0;
    for (int i = 0; i < 16; i++) {
        originX = (self.contentItemWith + SPACE_BETWEEN_ITEMS) * (i % 4);
        if (i == 4 || i == 8 || i == 12) {
            originY += self.contentItemHeight + SPACE_BETWEEN_ITEMS;
        }
        
        GameHotSearchItem *item = [[GameHotSearchItem alloc] initWithFrame:CGRectMake(originX, originY, self.contentItemWith, self.contentItemHeight)];
        item.tag = TAG_BASE + i;
        item.parentCtrl = self.parentCtrl;
        [self addSubview:item];
        [item release];
    }
}


@end
