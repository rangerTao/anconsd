//
//  GameHotSearchDisPlayView.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-14.
//
//

#import <UIKit/UIKit.h>

@interface GameHotSearchDisPlayView : UIView
@property (nonatomic, assign) UIViewController *parentCtrl;
@property (nonatomic, assign) BOOL bZeroHotList;
- (void)addSubViews;
- (void)updateContentView;

@end
