//
//  NdComPlatformAPIActionNumber.h
//  NdComPlatformFoundation
//
//  Created by BeiQi on 13-6-7.
//  Copyright (c) 2013年 NdCP. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NdComPlatformAPIError.h"

#define ND_COM_PLATFORM_ERROR_IMAGE_SIZE_TOO_LARGE				-801				/**< 发送的图片数据超过了服务器允许的大小 */
#define ND_COM_PLATFORM_ERROR_IMAGE_DATA_INVALID				-802				/**< 发送的图片数据内容不合法 */

#define ND_COM_PLATFORM_ERROR_PHOTO_NOT_CHANGED					-1001				/**< 头像没有变更 */
#define ND_COM_PLATFORM_ERROR_NO_CUSTOM_PHOTO					-1002				/**< 该用户没有自定义头像 */

#define ND_COM_PLATFORM_ERROR_APP_NOT_EXIST						-2001				/**< 该应用不存在 */
#define ND_COM_PLATFORM_ERROR_ICON_NOT_CHANGED					-2002				/**< 图标没有变更 */
#define ND_COM_PLATFORM_ERROR_NO_CUSTOM_ICON					-2003				/**< 无自定义图标 */
#define ND_COM_PLATFORM_ERROR_ICON_NOT_EXIST					-2004				/**< 该图标不存在 */

#define ND_COM_PLATFORM_ERROR_USER_NOT_EXIST					-10011				/**< 该用户不存在 */
