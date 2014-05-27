//
//  DashboardController.h
//  testCell
//
//  Created by Sun pinqun on 12-8-27.
//  Copyright 2012 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum _PAGE_TYPE {
	HOME_PAGE = 0x00,   //首页
	GAME_PAGE,          //游戏
	ACTIVITIES_PAGE,	//活动
    MANAGEMENT,			//管理
}PAGE_TYPE;

@interface DashboardController : UITabBarController {
    NSMutableArray *buttons;
    int currentSelectedIndex;
    UIImageView *slideBg;
}

@property (nonatomic,assign) int currentSelectedIndex;
@property (nonatomic,retain) NSMutableArray *buttons;
@property (nonatomic, retain) UIView *customBar;

+ (DashboardController *)dashBoardController;
- (void)setBadgeNum:(int)num atIndex:(int)index;
- (void)jumpToIndex:(NSInteger)index;

@end
