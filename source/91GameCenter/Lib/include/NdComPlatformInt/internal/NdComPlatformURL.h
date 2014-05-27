/*
 *  NdComPlatformURL.h
 *  NdComPlatform
 *
 *  Created by Sie Kensou on 10-8-12.
 *  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
 *
 */

#import <Foundation/Foundation.h>


/*!
 本文件定义了平台相关的url
 */

#pragma mark 接口地址
extern NSString  const *kNDComPlatformSnSCenterURL;						/**< CommPlatform 用户中心网址 */
extern NSString  const *kNDComPlatformDefaultBusinessCenterURL;			/**< 默认业务中心网址 */
extern NSString	 const *kNDComPlatformDefaultBusinessQueryURL;			/**< 默认支付查询网址 */
extern NSString	 const *kNDComPlatformCreditBusinessURL;				/**< 支付和余额的网址 */
extern NSString	 const *kNDComPlatformContactAndFriendURL;				/**< 3xx接口地址 */
extern NSString  const *kNDComPlatformUAPMessageURL;					/**< 4xx接口地址 */
extern NSString  const *kNDComPlatformArchievementLeaderBoardURL;		/**< 成就排行 */
extern NSString  const *kNDComPlatformVirtualGoodsURL;					/**< 虚拟商店 */
extern NSString	 const *kNDComPlatformCustomEventURL;					/**< 自定义事件 */
extern NSString  const *KNDComPlatformRedeemCodeURL;					/**< 兑换码 */
extern NSString  const *kNDComPlatformFindPasswordURL;					/**< 找回密码页面 */
extern NSString  const *kNDComPlatformPasswordProtectionURL;			/**< 密保管理页面 */
extern NSString  const *kNDComPlatformApplicationPromotionURL;			/**< 软件互推 */

#pragma mark 充值地址
extern NSString* NdComplatformUrlRechargeFor91Dou();					/**< (91豆充)值地址 */
extern NSString* NdComplatformUrlRechargeForCustomedCoin();				/**< (开发者自定义)虚拟币充值地址 */
extern NSString* NdComplatformUrlRechargeForVirtualGoodsCoin();			/**< (虚拟商店)虚拟币充值地址 */
extern NSString* NdComplatformUrlAppCenter();							/**< 应用中心 */
extern NSString* NdComplatformUrlAppCenterBBS();						/**< 应用中心论坛地址 */

extern NSString	 const *kNDComPlatformDynamicServiceURL;				/**< 动态接口列表请求地址 */


#pragma mark -
void initURL();

BOOL NDCP_is_DebugURL();


