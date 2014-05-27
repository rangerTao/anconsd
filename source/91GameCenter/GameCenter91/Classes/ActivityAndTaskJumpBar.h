//
//  ActivityAndTaskJumpBar.h
//  GameCenter91
//
//  Created by Sun pinqun on 12-12-12.
//
//

#import <UIKit/UIKit.h>

typedef enum {
    TYPE_TASK_TO_GAME = 1,
    TYPE_TASK_TO_ACTIVITY,
    TYPE_ACTIVITY_TO_GAME,
    TYPE_INCOMPATIBLE_GAME, //固件版本不支持此游戏
}JumpBarType;

@class AppDescriptionInfo;
@interface ActivityAndTaskJumpBar : UITableView <UITableViewDataSource, UITableViewDelegate>

@property (nonatomic, assign) NSInteger appId;

@property (nonatomic, assign) NSInteger targetId;//activityId
@property (nonatomic, assign) NSInteger targetAppId;
@property (nonatomic, retain) NSString *activityName;
@property (nonatomic, retain) NSString *activityDetail;
@property (nonatomic, retain) NSString *activityUrl;

@property (nonatomic, retain) AppDescriptionInfo *appInfo;

- (id)initWithView:(UIView *)view jumpType:(JumpBarType)type;
- (float)JumpBarHeight;
@end
