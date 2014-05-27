//
//  NdCPLoginUserData.h
//  NdComPlatform_SNS
//
//  Created by Sie Kensou on 10-9-29.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "NdComPlatformAPIResponse.h"
#import "NdComPlatformAPIResponse+LeaderBoard.h"
#import "NdContacts.h"

@interface NdCPBaseLoginUser : NSObject
{
	NdUserInfo  *userInfo;
	NSString	*userName;
	NSString	*payUserName;
	NSString	*balance;
	NSString	*autoLoginSignContent;
	BOOL		needCheckPayPassword;
	BOOL		isDeviceOwner;
	
	int			loginedPlatformId;
	BOOL		hasSetPassword;
	NSString	*thirdPlatformUserName;
	
	NSMutableDictionary	*friendUinAndNameDict;
	NdContacts	*currentContacts;
	NSString	*lastUpLoadContactTime;
}

@property (nonatomic, retain) NSString  *uin;
@property (nonatomic, retain) NSString  *nickName;
@property (nonatomic, retain) NSString  *checkSum;
@property (nonatomic, retain) NdUserInfo*userInfo;
@property (nonatomic, retain) NSString  *userName;
@property (nonatomic, retain) NSString  *payUserName;
@property (nonatomic, retain) NSString	*balance;
@property (nonatomic, retain) NSString	*autoLoginSignContent;
@property (nonatomic, assign) BOOL		needCheckPayPassword;		/**< 支付时是否需要验证密码 */
@property (nonatomic, assign) BOOL		isDeviceOwner;				/**< 当前账号是否是机主 */

@property (nonatomic, assign) int		loginedPlatformId;			/**< 如果大于0，则认为使用了第三方登录，小于等于0，为使用91通行证登录 */
@property (nonatomic, assign) BOOL		hasSetPassword;				/**< 91账号是否设置了密码 */
@property (nonatomic, retain) NSString	*thirdPlatformUserName;		/**< 第三方登录用户名称，可能是用户Id或昵称 */

@property (nonatomic, retain) NdContacts *currentContacts;
@property (nonatomic, retain) NSString	 *lastUpLoadContactTime;


- (ND_LOGIN_STATE)getCurrentLoginState;
- (void)clearBaseLoginData;

- (void)updateFriendName:(NSDictionary *)phoneAndUinDict;
- (NSString *)getFriendNameInContact:(NSString *)uin;

@end


@interface NdCPLoginUserData : NdCPBaseLoginUser {

	NSString		*lastLoginUin;
	NSMutableArray	*availableCounts;

	NSString	*appName;
	NSString	*servicePhoneNum;
	NSString	*smsServerPhoneNum;
	BOOL		hasPhoneNum;
	int			serverYear;
	int			serverMonth;
	int			serverDay;
	
	NdModuleList*	moduleList;
	int			virtualShopMoneyType;
}


+ (NdCPLoginUserData *)currentUser;
+ (void)clearCurrentUserData;

- (void)clearUserData;


@property (nonatomic, retain) NSString			*lastLoginUin;	
- (BOOL)isTheSameLoginUser;

@property (nonatomic, retain) NSMutableArray	*availableCounts;
- (void)setAccountList:(NSArray*)accounts;
- (void)updateLoginedAccountToAvailableCounts;
- (NSMutableArray*)accountListWithoutCurrentLoginAccount;
- (NSMutableArray*)accountListWithCurrentLoginAccount;


@property (nonatomic, retain) NSString		*appName;			/**< 当前应用名称 */
@property (nonatomic, retain) NSString		*servicePhoneNum;	/**< 客服电话 */
@property (nonatomic, retain) NSString		*smsServerPhoneNum;	/**< 短信猫号码 */
@property (nonatomic, assign) BOOL			hasPhoneNum;		/**< 短信猫里是否有当前IMSI的手机号 */

- (void)setServerTime:(NSString *)serverTime;
@property (nonatomic, assign) int		serverYear;
@property (nonatomic, assign) int		serverMonth;
@property (nonatomic, assign) int		serverDay;


@property (nonatomic, retain) NdModuleList*		moduleList;		/**< 成就排行榜模块启用信息 */
- (BOOL)isEnabledAchievement;
- (BOOL)isEnabledLeaderBoard;


- (void)setVGMoneyType:(int)type;		/**< 虚拟商店的币种，91豆或虚拟币 */
- (BOOL)isVGMoneyType91Dou;
- (BOOL)isVGMoneyTypeUnknown;

@end
