//
//  GameDetailController.h
//  GameCenter91
//
//  Created by hiyo on 12-9-11.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TabContainerController.h"
#import "RequestorAssistant.h"


@interface GameDetailController : UIViewController


+ (GameDetailController *)gameDetailWithIdentifier:(NSString *)identifier gameName:(NSString *)gameName;

+ (GameDetailController *)gameDetailForSDKUpgradeWithIdentifier:(NSString *)identifier gameName:(NSString *)gameName;//从SDK智能升级跳转 进入详情页

- (void)showMoreHotSpot:(id)sender;
- (void)showMoreStrategy:(id)sender;
- (void)showMoreForum: (id)sender;

- (void)gotoGameMoredetail:(NSString *)more;

@end
