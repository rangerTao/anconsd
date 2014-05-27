//
//  NdComPlatform+CustomLoginExtent.h
//  NdComPlatform_CustomLoginExtent
//
//  Created by xujianye on 12-5-24.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NdComPlatformBase.h"


@interface NdComPlatform(CustomLoginExtent)

/**
 @brief 登录平台,仅尝试自动登陆，失败不进入登录界面，仅发送登陆结果消息
 @param nFlag 标识（按位标识）预留，默认为0
 @result 错误码
 */
- (int)NdLoginInBackground:(int) nFlag;

/**
 @brief 登录平台,进入登录或者注册界面入口
 @param str3rdSessionId 第三方传入的sessionId
 @result 错误码
 */
- (int)NdLoginBySessionId:(NSString*)str3rdSessionId;


@end



