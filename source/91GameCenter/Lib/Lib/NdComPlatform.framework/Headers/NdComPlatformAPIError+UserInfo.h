//
//  NdComPlatformAPIActionNumber.h
//  NdComPlatformFoundation
//
//  Created by BeiQi on 13-6-7.
//  Copyright (c) 2013年 NdCP. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NdComPlatformAPIError.h"

#define ND_COM_PLATFORM_ERROR_USER_NOT_EXIST					-10011				/**< 该用户不存在 */
#define ND_COM_PLATFORM_ERROR_APP_NOT_EXIST						-2001				/**< 该应用不存在 */
#define ND_COM_PLATFORM_ERROR_PARAM_INVALID						-109				/**< 参数无效 */
#define ND_COM_PLATFORM_ERROR_PERMISSION_NOT_ENOUGH				-701				/**< 权限不足 */
#define ND_COM_PLATFORM_ERROR_USER_NOT_EXIST					-10011				/**< 该用户不存在 */
#define ND_COM_PLATFORM_ERROR_PERMISSION_NOT_ENOUGH				-701				/**< 权限不足 */
#define ND_COM_PLATFORM_ERROR_NEW_PASSWORD_INVALID				-301				/**< 新密码格式非法，密码不能为空，长度为6－12个字符，由字母和数字组成，大小写敏感 */
#define ND_COM_PLATFORM_ERROR_OLD_PASSWORD_INVALID				-302				/**< 旧密码格式非法，不能为空*/
#define ND_COM_PLATFORM_ERROR_OLD_PASSWORD_ERROR				-303				/**< 原密码错误 */
#define ND_COM_PLATFORM_ERROR_NICKNAME_INVALID					-201				/**< 昵称不合法，合法昵称由1－16个非空字符构成，请勿使用敏感词汇 */
#define ND_COM_PLATFORM_ERROR_TRUE_NAME_INVALID					-501				/**< 真实姓名不合法 */
#define ND_COM_PLATFORM_ERROR_ACCOUNT_NOT_EXIST					-103				/**< 91通行证账号不存在或者停用 */
#define ND_COM_PLATFORM_ERROR_VERIFY_ACCOUNT_FAIL				-108				/**< 账号验证失败 */
