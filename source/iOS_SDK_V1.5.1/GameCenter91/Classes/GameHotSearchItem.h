//
//  GameHotSearchItem.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-12.
//
//

#import <UIKit/UIKit.h>
typedef NS_ENUM(NSInteger, HotSearchItemStyle) {
    hotSearchGame,
    HotSearchKeyword
};

@interface GameHotSearchItem : UIView
@property (nonatomic, assign) UIViewController *parentCtrl;
@property (nonatomic, retain) UIImageView *bgImageView;
@property (nonatomic, retain)UILabel *gameTitleLabel;
@property (nonatomic, retain)UILabel *hotSearchLabel;
@property (nonatomic, retain)UIImageView *gameIconImgView;

- (void)adjustTitleFrame;
@end
