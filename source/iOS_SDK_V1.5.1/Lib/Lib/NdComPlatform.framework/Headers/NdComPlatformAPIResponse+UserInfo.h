//
//  NdComPlatformAPIResponse.h
//  NdComPlatform_SNS
//
//  Created by Sie Kensou on 10-10-8.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//

#import <UIKit/UIKit.h>

extern NSString * const kNdCPUserInfoDidChange;                 /**< 用户修改了基础个人信息（昵称，真实姓名，地址，出生年月，性别），会抛此通知 */
extern NSString * const     kNdIsAllBasicInfoFilledOutKey;          /**< 用户基础个人信息是否都填写了，返回NSNumber对象 */
extern NSString * const kNdCPUserDidBindPhoneNotification;         /**< 用户首次绑定了手机号（手机号注册，首次绑定手机号），会抛此通知 */


/**
 @brief 用户详细信息
 */
@interface NdUserInfo : NSObject<NSCoding> 
{
	NSString	*uin;
	NSString	*nickName;
	int			bornYear;
	int			bornMonth;
	int			bornDay;
	int			sex;
	NSString	*province;
	NSString	*city;
	NSString	*trueName;
	NSString	*point;
	NSString	*emotion;
	NSString	*checkSum;
}

- (void)copyDataFromUserInfo:(NdUserInfo*)info;		/**< 浅复制 */
- (NSString*)provinceName;	/**< 通过province(ID)查询 获取省份名称 */
- (NSString*)cityName;		/**< 通过city(ID) 查询 获取城市名称 */
- (NSString*)provinceAndCityNameWithSplit:(NSString*)split;		/**< 把省份和城市名称合并，中间用分隔符。该方法的效率会比分别查询再自己合并来得高。 */
- (BOOL)isAllBasicInfomationFilledOut;
- (BOOL)isAllBasicInfomationSameWith:(NdUserInfo*)userInfo;
- (BOOL)isBirthdaySameWith:(NdUserInfo*)userInfo;
- (BOOL)isAddressSameWith:(NdUserInfo*)userInfo;

@property (nonatomic, retain) NSString *uin;		/**< 用户uin */
@property (nonatomic, retain) NSString *nickName;	/**< 昵称（1－20个字符，不可为空）*/
@property (nonatomic, assign) int bornYear;			/**< 出生年份，未知为空 */
@property (nonatomic, assign) int bornMonth;		/**< 出生月份，未知为空 */
@property (nonatomic, assign) int bornDay;			/**< 出生日，未知为空 */
@property (nonatomic, assign) int sex;				/**< 0＝未设置，1＝男，2＝女 */
@property (nonatomic, retain) NSString *province;	/**< 省份ID，未知为空 */
@property (nonatomic, retain) NSString *city;		/**< 城市ID，未知未空 */
@property (nonatomic, retain) NSString *trueName;	/**< 真实姓名（2－4个汉字），未知为空 */
@property (nonatomic, retain) NSString *point;		/**< 积分 */
@property (nonatomic, retain) NSString *emotion;	/**< 心情 */
@property (nonatomic, retain) NSString *checkSum;	/**< 好友头像的Md5值 */

@end




/**
 @brief 我的基础信息
 */
@interface NdMyBaseInfo : NSObject 
{
	NSString *uin;
	NSString *nickName;
	NSString *checkSum;
}

@property (nonatomic, retain) NSString *uin;			/**< 自己的uin */
@property (nonatomic, retain) NSString *nickName;		/**< 自己的昵称 */
@property (nonatomic, retain) NSString *checkSum;		/**< 自己的头像的checkSum */

@end




/**
 @brief 我的用户信息
 */
@interface NdMyUserInfo : NSObject 
{
	NdMyBaseInfo *baseInfo;
}

@property (nonatomic, retain) NdMyBaseInfo *baseInfo;	/**< 基础信息 */

@end




#pragma mark -
#pragma mark  ------------ Permission ------------


/**
 @brief 添加好友权限定义值
 */
typedef enum _ND_FRIEND_AUTHORIZE_TYPE 
{
	ND_FRIEND_AUTHORIZE_TYPE_READ = -1,					/**< 读取 */
	ND_FRIEND_AUTHORIZE_TYPE_NEED_AUTHORIZE,			/**< 需要验证才能添加 */
	ND_FRIEND_AUTHORIZE_TYPE_EVERYONE_CAN_ADD,			/**< 允许任何人添加 */
	ND_FRIEND_AUTHORIZE_TYPE_NO_ONE_CAN_ADD,			/**< 不允许任何人添加 */
} ND_FRIEND_AUTHORIZE_TYPE;

/**
 @brief 是否启用支付密码权限定义值
 */
typedef enum _ND_PAY_AUTHORIZE_TYPE
{
	ND_PAY_AUTHORIZE_TYPE_READ = -1,					/**< 读取*/
	ND_PAY_AUTHORIZE_TYPE_CLOSE,						/**< 关闭 */
	ND_PAY_AUTHORIZE_TYPE_OPEN,							/**< 启用 */
}ND_PAY_AUTHORIZE_TYPE;

/**
 @brief 是否已经设置帐号登录密码权限定义值
 */
typedef enum _ND_ACCOUNTS_AUTHORIZE_TYPE
{
	ND_ACCOUNTS_AUTHORIZE_TYPE_READ = -1,					/**< 读取*/
	ND_ACCOUNTS_AUTHORIZE_TYPE_CLOSE,						/**< 未设置 */
	ND_ACCOUNTS_AUTHORIZE_TYPE_OPEN,						/**< 已设置 */
}ND_ACCOUNTS_AUTHORIZE_TYPE;


/**
 @brief 用户的添加好友权限信息
 */
@interface NdAddFriendPermission : NSObject 
{
	ND_FRIEND_AUTHORIZE_TYPE		canAddFriend;
	NSString*						uin;
}

@property (nonatomic, assign) ND_FRIEND_AUTHORIZE_TYPE canAddFriend;			/**< uin对应的权限 */	
@property (nonatomic, retain) NSString*		uin;								/**< 用户uin, 为空时代表自己 */	

@end

/**
 @brief 是否启用支付密码权限信息
 */
@interface NdPayPasswordPermission : NSObject 
{
	ND_PAY_AUTHORIZE_TYPE			canPayPassword;
	NSString*						uin;
}

@property (nonatomic, assign) ND_PAY_AUTHORIZE_TYPE canPayPassword;				/**< uin对应的权限 */	
@property (nonatomic, retain) NSString*		uin;								/**< 用户uin, 为空时代表自己 */	

@end

/**
 @brief 是否已经设置帐号登录密码权限信息
 */
@interface NdPasswordPermission : NSObject 
{
	ND_ACCOUNTS_AUTHORIZE_TYPE		canPassword;
	NSString*						uin;
}

@property (nonatomic, assign) ND_ACCOUNTS_AUTHORIZE_TYPE canPassword;			/**< uin对应的权限 */	
@property (nonatomic, retain) NSString*		uin;								/**< 用户uin, 为空时代表自己 */	

@end


/**
 @brief 用户的权限信息
 */
@interface NdPermission : NSObject
{
	NdAddFriendPermission*	addFriendPermission;	
	NdPayPasswordPermission* payPasswordPermission;
	NdPasswordPermission* passwordPermission;
}

@property (nonatomic, retain) NdAddFriendPermission*	addFriendPermission;	/**< 添加好友权限 */
@property (nonatomic, retain) NdPayPasswordPermission*	payPasswordPermission;	/**< 是否启用支付密码权限 */ 
@property (nonatomic, retain) NdPasswordPermission*	passwordPermission;			/**< 设置帐号登录密码权限 */

@end




#pragma mark -
/**
 @brief 获取应用分享信息及其在资源中心的url地址
 */
@interface NdSharedMessageInfo : NSObject 
{
	NSString*	strAppInfo;
	NSString*	strAppUrl;
	NSString*	strTime;
}

@property (nonatomic, retain) NSString*	strAppInfo;	/**< 分享的信息 */
@property (nonatomic, retain) NSString*	strAppUrl;	/**< 应用在资源中心的页面地址 */
@property (nonatomic, retain) NSString*	strTime;	/**< 当前时间 */

@end


