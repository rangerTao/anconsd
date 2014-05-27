//
//  NdComPlatform+Center.h
//  NdComPlatform_Center
//
//  Created by xujianye on 12-5-24.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NdComPlatformBase.h"

extern NSString * const kNdCPLeavePlatformNotification;			/**< 离开平台界面时，会发送该通知 */

@interface NdComPlatform(Center)




#pragma mark -
typedef	enum  _NDCP_SETTING_FLAG {
	NDCP_SETTING_DEFAULT  =  0,         /**< 进入平台中心界面	*/
	NDCP_SETTING_PERSONAL_INFO,         /**< 进入“个人信息管理”界面	*/
	NDCP_SETTING_ACCOUNT,               /**< 进入“安全中心”界面	*/
	NDCP_SETTING_RECORD_TRADE,          /**< 进入“91豆充值消费记录”界面	*/
	NDCP_SETTING_RECHARGE_RECORD,		/**< 进入“91豆充值记录”界面	*/
	NDCP_SETTING_CONSUME_RECORD,		/**< 进入“91豆消费记录”界面	*/
}	NDCP_SETTING_FLAG;

/**
 @brief 进入平台中心
 @param nFlag 参见NDCP_SETTING_FLAG值。
 */
- (int)NdEnterPlatform:(int) nFlag;

@end
