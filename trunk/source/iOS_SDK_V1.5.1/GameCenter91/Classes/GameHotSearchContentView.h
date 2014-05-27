//
//  GameHotSearchContentView.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-12.
//
//

#import <UIKit/UIKit.h>

@interface GameHotSearchContentView : UIView
@property (nonatomic, assign) UIViewController *parentCtrl;

- (void)updateWithAppRecommendList:(NSArray *)recommendList;
@end
