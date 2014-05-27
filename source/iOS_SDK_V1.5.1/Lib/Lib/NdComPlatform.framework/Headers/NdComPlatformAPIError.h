//
//  NdComPlatformAPIActionNumber.h
//  NdComPlatformFoundation
//
//  Created by BeiQi on 13-6-7.
//  Copyright (c) 2013年 NdCP. All rights reserved.
//

#import <Foundation/Foundation.h>

#define ND_COM_PLATFORM_NO_ERROR								0					/**< 没有错误 */
#define ND_COM_PLATFORM_ERROR_UNKNOWN							-1					/**< 未知错误 */
#define ND_COM_PLATFORM_ERROR_NETWORK_FAIL						-2					/**< 网络连接错误 */
#define ND_COM_PLATFORM_ERROR_PACKAGE_INVALID					-3					/**< 数据包不全、丢失或无效*/
#define ND_COM_PLATFORM_ERROR_SESSIONID_INVALID					-4					/**< SessionId（用户的会话标识）无效 */
#define ND_COM_PLATFORM_ERROR_PARAM								-5					/**< 参数值错误或非法，请检查参数值是否有效 */
#define ND_COM_PLATFORM_ERROR_CLIENT_APP_ID_INVALID				-6					/**< 无效的应用ID接入 */
#define ND_COM_PLATFORM_ERROR_NETWORK_ERROR						-7					/**< 网络通信发生错误 */
#define ND_COM_PLATFORM_ERROR_APP_KEY_INVALID					-8					/**< 该用户未授权接入（AppKey无效）*/
#define ND_COM_PLATFORM_ERROR_NO_SIM							-9					/**< 未检测到SIM卡 */
#define ND_COM_PLATFORM_ERROR_SERVER_RETURN_ERROR				-10					/**< 服务器处理发生错误，请求无法完成 */
#define ND_COM_PLATFORM_ERROR_NOT_LOGINED						-11					/**< 未登录 */
#define ND_COM_PLATFORM_ERROR_USER_CANCEL						-12					/**< 用户取消 */
#define ND_COM_PLATFORM_ERROR_BUSINESS_SYSTEM_UNCHECKED			-13					/**< 业务系统未通过审核 */
#define ND_COM_PLATFORM_ERROR_SDK_VERSION_INVALID				-14					/**< SDK版本号无效 */
#define ND_COM_PLATFORM_ERROR_NOT_PERMITTED						-15					/**< 接口不允许调用（比如，游客权限不足) */

#define ND_COM_PLATFORM_ERROR_PARAM_INVALID						-109				/**< 参数无效 */
