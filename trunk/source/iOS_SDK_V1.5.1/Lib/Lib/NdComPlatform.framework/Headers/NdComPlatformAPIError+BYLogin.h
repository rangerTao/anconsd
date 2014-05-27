//
//  NdComPlatformAPIActionNumber.h
//  NdComPlatformFoundation
//
//  Created by BeiQi on 13-6-7.
//  Copyright (c) 2013年 NdCP. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NdComPlatformAPIError.h"

#define ND_COM_PLATFORM_ERROR_PASSWORD_INVALID					-101				/**< 密码格式不合法，密码不能为空，长度为6－12个字符，由字母和数字组成，大小写敏感 */
#define ND_COM_PLATFORM_ERROR_ACCOUNT_NOT_EXIST					-103				/**< 91通行证账号不存在或者停用 */
#define ND_COM_PLATFORM_ERROR_ACCOUNT_PASSWORD_ERROR			-104				/**< 91通行证账号密码错误 */
#define ND_COM_PLATFORM_ERROR_ACCOUNT_INVALID					-100				/**< 账号格式不合法，合法账号为4－70个字符，仅允许小写字母及数字，支持邮箱注册 */

#define ND_COM_PLATFORM_ERROR_DEVICE_NEVER_LOGINED				-111				/**< 该设备没有登录过用户 */
#define ND_COM_PLATFORM_ERROR_DEVICE_CANNOT_AUTO_LOGIN			-112				/**< 该设备不能自动登录 */
#define ND_COM_PLATFORM_ERROR_AUTO_LOGIN_SIGN_INVALID			-114				/**< 自动登录凭据失效，请重新输入密码登录 */
#define ND_COM_PALTFORM_ERROR_BY_TOKEN_INVALID                  -123                /**< 博远注册令牌无效 */
#define ND_COM_PALTFORM_ERROR_BY_HAS_DEVICE_REG                 -124                /**< 已经用设备注册过博远的账号了 */
#define ND_COM_PALTFORM_ERROR_BY_NEED_DEVICE_LOGIN              -125                /**< 需要重新设备登录一下 */
#define ND_COM_PALTFORM_ERROR_BY_NEED_BIND_BY_ACCOUNT           -126                /**< 需要用户设置博远账号 */
#define ND_COM_PALTFORM_ERROR_BY_BIND_WITHOUT_LOGINED           -127                /**< 绑定博远账号失败，请先登录91账号 */
#define ND_COM_PALTFORM_ERROR_BY_NEED_LOGIN_WITH_BY_ACCOUNT     -128                /**< 请使用绑定的博远账号登录 */
#define ND_COM_PALTFORM_ERROR_BY_DISABLE_DEVICE_REG             -129                /**< 系统已经关闭设备注册 */

#define ND_COM_PLATFORM_ERROR_BD_INVALID_PHONE_NUM				-25001				/**< 手机号码格式无效 */
#define ND_COM_PLATFORM_ERROR_PHONE_NUM_BE_REGISTERED			-402				/**< 该手机号已经被注册 */
#define ND_COM_PLATFORM_ERROR_EMAIL_INVALID                     -140				/**< 邮箱格式不合法 */
#define ND_COM_PLATFORM_ERROR_EMAIL_EXISTED                     -141				/**< 邮箱已被注册 */

#define ND_COM_PLATFORM_ERROR_3RD_INFO_INVALID					-22001				/**< 第三方信息不存在 */
#define ND_COM_PLATFORM_ERROR_3RD_AUTH_FAILED					-28001				/**< 验证第三方账号授权失败 */
#define ND_COM_PLATFORM_ERROR_3RD_REAUTH_FAILDED				-28002				/**< 验证第三方绑定信息失败 */
#define ND_COM_PLATFORM_ERROR_91_ACCOUNT_EXCEPTION				-19046				/**< 第三方账号绑定的91账号异常（停用等） */
#define ND_COM_PLATFORM_ERROR_NO_NEED_BECOME_REGULAR			-26002				/**< 非游客登录状态 */
