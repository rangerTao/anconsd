//
//  NdComPlatform+SSO.h
//  NdComPlatformUI
//
//  Created by BeiQi56 on 13-7-22.
//  Copyright (c) 2013年 NdCP. All rights reserved.
//

#import "NdComPlatformBase.h"
#import "NdComPlatformBase.h"

@interface NdComPlatform(SSO)

/**
	@brief 处理SSO登录回调通知，在AppDelegate中的 @selector(application: openURL: sourceApplication: annotation:) 和@selector(application: handleOpenURL:) 方法中调用该方法，传入openUrl参数。
	@param url AppDelegate中的openUrl
	@returns 返回SSO处理URL的结果
 */
- (BOOL)ssoHandleOpenURL:(NSURL*)url;

@end
