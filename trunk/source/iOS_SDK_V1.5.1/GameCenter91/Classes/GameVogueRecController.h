//
//  GameVogueRecController.h
//  GameCenter91
//
//  Created by hiyo on 13-1-23.
//  Copyright (c) 2013年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RequestorAssistant.h"

typedef enum _VOGUE_TYPE {
	VOGUE_UNKNOWN = 0x00,
	VOGUE_PICTURE,		//图片类型	
	VOGUE_TOPIC,		//专题类型	
    VOGUE_GAME,			//游戏类型
}VOGUE_TYPE;

typedef enum _VOGUE_TARGET_TYPE {
	V_TARGET_UNKNOWN = 0x00,
	V_TARGET_ACTIVITY_DETAIL,   //活动详情
	V_TARGET_GAME_DETAIL,		//游戏详情	
    V_TARGET_GAME_TOPIC,		//游戏专题
    V_TARGET_PROP_DETAIL,		//道具详情
    V_TARGET_WEB_DETAIL,        //网页链接
}VOGUE_TARGET_TYPE;


@interface GameVogueRecController : UIViewController

@end
