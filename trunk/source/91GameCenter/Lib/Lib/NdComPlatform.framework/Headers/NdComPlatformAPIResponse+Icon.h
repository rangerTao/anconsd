//
//  NdComPlatformAPIResponse+Icon.h
//  NdComPlatformFoundation
//
//  Created by BeiQi on 13-6-6.
//  Copyright (c) 2013年 NdCP. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, ND_PHOTO_SIZE_TYPE)  {
	ND_PHOTO_SIZE_TINY = 0,		/**< 16 * 16像素		*/
	ND_PHOTO_SIZE_SMALL,		/**< 48 * 48像素		*/
	ND_PHOTO_SIZE_MIDDLE,		/**< 120*120像素		*/
	ND_PHOTO_SIZE_BIG,			/**< 200*200像素		*/
};

extern NSString * const kNdCPUserPortraitDidChange;             /**< 用户修改了头像，会抛该notification */
