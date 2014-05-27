//
//  NdComPlatformAPIActionNumber.h
//  NdComPlatformFoundation
//
//  Created by BeiQi on 13-6-7.
//  Copyright (c) 2013年 NdCP. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NdComPlatformAPIError.h"

#define ND_COM_PLATFORM_ERROR_PAY_PASSWORD_ERROR				-3001				/**< 支付密码错误 */
#define ND_COM_PLATFORM_ERROR_PAY_ACCOUNT_NOT_ACTIVED			-3002				/**< 该账号未在商城开户 */
#define ND_COM_PLATFORM_ERROR_PAY_PASSWORD_NOT_SET				-3003				/**< 支付密码未设置 */

#define ND_COM_PLATFORM_ERROR_PAY_PASSWORD_NOT_VERIFY			-4001				/**< 支付密码未验证 */
#define ND_COM_PLATFORM_ERROR_BALANCE_NOT_ENOUGH				-4002				/**< 余额不足，无法支付 */
#define ND_COM_PLATFORM_ERROR_ORDER_SERIAL_DUPLICATE			-4003				/**< 订单号重复 */
#define ND_COM_PLATFORM_ERROR_ORDER_SERIAL_SUBMITTED			-4004				/**< 订单已提交 */

#define ND_COM_PLATFORM_ERROR_PAY_ORDER_NOT_EXIST				-19032				/**< 无此订单 */
#define ND_COM_PLATFORM_ERROR_PAY_REQUEST_TIMEOUT				-23002				/**< 支付超时，请稍候重试 */

#define ND_COM_PLATFORM_ERROR_PAGE_REQUIRED_NOT_VALID			-5001				/**< 页码超过范围 */

#define ND_COM_PLATFORM_ERROR_NEW_PASSWORD_INVALID				-301				/**< 新密码格式非法，密码不能为空，长度为6－12个字符，由字母和数字组成，大小写敏感 */
#define ND_COM_PLATFORM_ERROR_OLD_PASSWORD_ERROR				-303				/**< 原密码错误 */
