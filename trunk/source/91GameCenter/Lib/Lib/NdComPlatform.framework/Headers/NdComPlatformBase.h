//
//  NdComPlatform.h
//  NdComPlatform_SNS
//
//  Created by Sie Kensou on 10-9-15.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NdComPlatformAPIError.h"
#import "NdComPlatformAPIResponse+UserInfo.h"
#import "NdComPlatformAPIError+UserInfo.h"
#import "NdComPlatformAPIError+ChannelId.h"

@interface NdComPlatform : NSObject {
	NSDictionary		*m_stepDict;
	id					m_updateDelegate;
	NdMyUserInfo		*m_myUserInfo;
	UIInterfaceOrientation	m_orientation;
	BOOL				m_shouldAutoRotate;
	BOOL				m_showLoadingWhenAutoLogin;
	BOOL				m_isPayDebugMode;
	BOOL				m_isUpdateDebugMode;
	UIViewController	*m_unRotateBaseCtrl;
}

@property (nonatomic, assign) BOOL showLoadingWhenAutoLogin;					/**< 如果自动登录的时候不希望出现loading界面，请将该参数设置为NO */

/**
 @brief 获取NdComPlatform的实例对象
 */
+ (NdComPlatform *)defaultPlatform;

/**
 @brief 获取NdComPlatform的版本号
 */
+ (NSString *)version;

/**
 @brief 获取屏幕截图
 */
+ (UIImage *)NdGetScreenShot;

/**
 @brief 获取某视图的局域截图
 @param view 要截图的UIView
 @param captureRect 要截取的区域
 */
+ (UIImage *)NdGetViewCapture:(UIView *)view captureRect:(CGRect)captureRect;

/**
 @brief 设定调试模式
 @param nFlag 预留，默认为0
 */
- (void)NdSetDebugMode:(int)nFlag;




#pragma mark -
#pragma mark (Deprecated)

@property (nonatomic, readonly, assign) UIInterfaceOrientation orientation;		/**< 当前平台的UI方向 */

/**
 @brief 设定平台为横屏或者竖屏
 @note  !!!: 该API已经失效，SDK 以StatusBar方向为准。
 */
- (void)NdSetScreenOrientation:(UIInterfaceOrientation)orientation;

/**
 @brief 设置是否自动旋转。
 @note !!!: 该API已经失效，SDK会自适应当前StatusBar方向（iPhone平台不支持横屏与竖屏之间的动态转换）。
 */
- (void)NdSetAutoRotation:(BOOL)isAutoRotate;




#pragma mark -
#pragma mark SDK接入时需要的验证信息

/**
 @brief 设置应用Id
 @param appId 应用程序id，需要向用户中心申请，合法的id大于0
 */
- (BOOL)setAppId:(int)appId;

/**
 @brief 获取appId，需要预先设置
 */
- (int)appId;

/**
 @brief 设置应用密钥
 @param appKey 第三方应用程序密钥，appKey未系统分配给第三方的应用程序密钥，第三方需要向平台提供方申请，并设置到平台上
 */
- (BOOL)setAppKey:(NSString *)appKey;

/**
 @brief 获取应用软件名称，需要登录后才能获取
 */
- (NSString *)appName;




#pragma mark -
#pragma mark Version Update

/**
 @brief 检查自身版本更新
 @param nFlag 标识（按位标识）默认为0，检测更新
 @param delegate 回调对象，回调接口参见 NdComPlatformUIProtocol
 @result 错误码
 */
- (int)NdAppVersionUpdate:(int)nFlag delegate:(id)delegate;




#pragma mark -
#pragma mark Feedback 

/**
 @brief 用户反馈
 @result 错误码
 */
- (int)NdUserFeedBack;




#pragma mark -
#pragma mark Local Notification 

/**
 @brief 设置本地通知
 @param fireDate notification的倒计时时间，单位秒
 @param alertBody notification的消息内容
 @result 设置结果
 */
- (BOOL)NdSetLocalNotification:(NSTimeInterval)fireDate alertBody:(NSString*)alertBody;

/**
 @brief 取消所有已设置的本地通知
 */
- (void)NdCancelAllLocalNotification;




#pragma mark -
#pragma mark ChannelId

/**
 @brief 上传渠道id，需要先设置appId，无须登录
 @param delegate 回调对象
 @result 错误码
 */
- (int)NdUploadChannelId:(id)delegate;




@end




#pragma mark -
#pragma mark -

@protocol NdComPlatformUIProtocol


typedef	enum  _ND_APP_UPDATE_RESULT {
	ND_APP_UPDATE_NO_NEW_VERSION = 0,				/**< 没有新版本 */
	ND_APP_UPDATE_FORCE_UPDATE_CANCEL_BY_USER = 2,	/**< 用户取消下载强制更新 */
	ND_APP_UPDATE_NORMAL_UPDATE_CANCEL_BY_USER = 3,	/**< 用户取消下载普通更新 */
	ND_APP_UPDATE_NEW_VERSION_DOWNLOAD_FAIL = 4,	/**< 下载新版本失败 */
	ND_APP_UPDATE_CHECK_NEW_VERSION_FAIL = 7,		/**< 检测新版本失败 */
}	ND_APP_UPDATE_RESULT;
/**
 @brief NdAppVersionUpdate的回调函数
 @param updateResult 检测更新结果
 */
- (void)appVersionUpdateDidFinish:(ND_APP_UPDATE_RESULT)updateResult;


/**
 @brief NdUploadChannelId的回调
 @param error 错误码，如果error为0，则代表API执行成功，否则失败。错误码涵义请查看NdComPlatformError文件 
 */
- (void)uploadChannelIdDidFinish:(int)error;


@end

