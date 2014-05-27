//
//  NdCPAppInfoMng.h
//  NdComPlatform_SNS
//
//  Created by xujianye on 10-10-21.
//  Copyright 2010 NetDragon WebSoft Inc. All rights reserved.
//
/*
 * 存储结构：[NdCPAppInfoMng getCurrentAppInfo] 返回的dic键值对为
			 NDCP_APP_KEY_NAME			- nsstring
			 NDCP_APP_KEY_VER			- nsstring
			 NDCP_APP_KEY_VER_SHORT		- nsstring
			 NDCP_APP_KEY_ID			- nsstring

 *	singleton.dicAppInfoList 存储结构为：dic键值对为
		每个应用程序的identifier  --   [dictionary] 
									 { 
										NDCP_APP_KEY_NAME	- nsstring
										NDCP_APP_KEY_VER	- nsstring
									 }
 
 *
 */

#import <Foundation/Foundation.h>

@interface NdCPAppInfoMng : NSObject {
	NSString*		strPath;
	NSMutableDictionary*	dicAppInfoList;
}

@property (nonatomic, retain)	NSString*		strPath;		//可以更改存储路径
@property (nonatomic, retain, readonly)	NSMutableDictionary*	dicAppInfoList;

- (void)checkInCurrentAppInfo;
- (void)loadAppInfoFromFile;
- (void)saveAppInfoToFile;

+ (NdCPAppInfoMng*)singleton;
+ (void)releaseSingleton;

+ (NSMutableDictionary*)getCurrentAppInfo;
+ (NSString*)getIphonePlatform;				//客户端平台类型，iphone=1
+ (NSString*)getCurrentOsVersion;			//固件版本
+ (NSString*)getCurrentAppIdentifier;		//应用标识
// getBundleVersion 和 getAppVersion 如果任何一个取不到就取另一个，保持两个一致。
+ (NSString*)getBundleVersion;				//BundleVersion	(build)
+ (NSString*)getAppVersion;					//BundleShortVersionString (version)

@end
