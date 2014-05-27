/*
 *  NdComPlatformActError.h
 *  NdComPlatform
 *
 *  Created by Sie Kensou on 10-8-12.
 *  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
 *
 */

#define ND_COM_PLATFORM_ACT_ERROR_SUCCESS															0					/**< 成功 */
#define ND_COM_PLATFORM_ACT_ERROR_PACKAGE_ERROR														1			
#define ND_COM_PLATFORM_ACT_ERROR_SESSION_ID_INVAILD												2
#define ND_COM_PLATFORM_ACT_ERROR_INVALID_ACTION_NUMBER												3
#define ND_COM_PLATFORM_ACT_ERROR_RSA_INVALID														4
#define ND_COM_PLATFORM_ACT_ERROR_INTERFACE_DEPRECATED												5
#define ND_COM_PLATFORM_ACT_ERROR_DECREPT_FAIL														6
#define ND_COM_PLATFORM_ACT_ERROR_PARAM_ERROR														7
#define ND_COM_PLATFORM_ACT_ERROR_PARAM_INVALID														8
#define ND_COM_PLATFORM_ACT_ERROR_ENCRYPT_FAIL														9
#define ND_COM_PLATFORM_ACT_ERROR_MD5_WRONG															10
#define ND_COM_PLATFORM_ACT_ERROR_INVALID_VERSION													11
#define ND_COM_PLATFORM_ACT_ERROR_INVALID_ENCRYPTION_TYPE											12
#define ND_COM_PLATFORM_ACT_ERROR_INVALID_APP_ID													13
#define ND_COM_PLATFORM_ACT_ERROR_INVALID_CLIENT_TYPE												14
#define ND_COM_PLATFORM_ACT_ERROR_INVALID_SCREEN_SIZE												15
#define ND_COM_PLATFORM_ACT_ERROR_BUSINESS_SYSTEM_UNCHECKED											16


#define ND_COM_PLATFORM_ACT_ERROR_DATA_TOO_LONG														996
#define ND_COM_PLATFORM_ACT_ERROR_UNKNOW_SERVER_ERROR												997
#define ND_COM_PLATFORM_ACT_ERROR_SERVER_MAINTAINING												998
#define ND_COM_PLATFORM_ACT_ERROR_SERVER_INSIDE_ERROR												999

#define ND_COM_PLATFORM_ACT_ERROR_ACT1_GET_ACCOUNTS_FAIL											1001				/**< 获取列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT1_RSA_KEY_INVALID												1002				/**< 公钥无效 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT1_3DES_KEY_INVALID												1003				/**< 3des密钥无效 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT1_3DES_KEY_WEAK												1004				/**< 3des密钥为弱密钥 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT1_NO_ACCOUNTS													1005				/**< 列表为空 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT1_INVALID_CLIENT												1006				/**< 该用户未授权接入 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT2_LOGIN_FAIL													2001				/**< 登录失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT2_ACCOUNT_NOT_EXIST_OR_NOT_IN_USE								2002				/**< 91通行证账号不存在或者停用 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT2_ACCOUNT_PASSWORD_ERROR										2003				/**< 91通行证账号密码错误 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT2_ACCOUNT_LOGIN_FAIL											2004				/**< 91通行证账号登录出错 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT2_TOO_MUCH_ACCOUNT_REGISTERED									2005				/**< 该手机号码已经注册太多的账号 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT2_AUTO_LOGIN_SIGN_INVALID										2006				/**< 自动登录凭据失效，请重新输入密码登录 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT3_REGISTER_FAIL												3001				/**< 注册失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT3_ACCOUNT_EXIST												3003				/**< 91通行证已被注册 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT3_VERIFY_ACCOUNT_ERROR											3004				/**< 验证91通行证账号出错 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT3_TOO_MUCH_ACCOUNT_REGISTERED									3005				/**< 该手机号已经注册太多的账号 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT3_ACCOUNT_HAS_REGISTERED										3006				/**< 91通行证账号已经注册用户 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT3_007															3007				/**< 用户昵称不合法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT3_008															3008				/**< 短信验证码无效 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT4_MODIFY_NICK_NAME_FAIL										4001				/**< 昵称修改失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT4_NICK_NAME_INVALID											4002				/**< 昵称不合法，没有修改 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT5_MODIFY_PASSWORD_FAIL											5001				/**< 91通行证账号密码修改失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT5_NEW_PASSWORD_INVALID											5002				/**< 新密码格式不合法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT5_OLD_PASSWORD_INVALID											5003				/**< 旧密码格式不合法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT5_OLD_PASSWORD_ERROR											5004				/**< 原密码错误 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT6_SET_PHONE_NUM_FAIL											6001				/**< 设定用户的手机号码失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT6_HAS_SET_PHONE_NUM											6002				/**< 该用户已经设定过了手机号码 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT7_FAIL															7001				/**< 获取预注册的91账号失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT7_SMS_SERVER_FAIL												7002				/**< 该手机号码已经注册了太多账号 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT7_TOO_MUCH_ACCOUNT												7003				/**< 该手机已经注册了太多账号 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT8_MODIFY_USER_INFO_FAIL										8001				/**< 修改用户信息失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT8_NICKNAME_INVALID												8002				/**< 昵称不合法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT8_TRUE_NAME_INVALID											8003				/**< 真实姓名不合法 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT9_MODIFY_USER_EMOTION_FAIL										9001				/**< 修改用户心情失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT9_EMOTION_TOO_LONG												9002				/**< 心情内容超过140 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT9_EMOTION_CONTENT_NOT_VALID									9003				/**< 心情内容不合法 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT10_SET_FAIL													10001				/**< 设置失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT10_READ_FAIL													10002				/**< 读取失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT10_UIN_NOT_FOUND												10003				/**< 用户不存在 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT11_GET_USER_INFO_FAIL											11001				/**< 获取失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT11_USER_NOT_FOUND												11002				/**< 该用户不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT11_PERMISSION_NOT_ENOUGH										11003				/**< 没有权限获取该用户资料 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT12_UPLOAD_FAIL													12001				/**< 上传照片失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT12_IMAGE_SIZE_NOT_ALLOWED										12002				/**< 发送的图片大小超过服务器允许大小 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT12_IMAGEDATA_INVALID											12003				/**< 发送的图片数据不合法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT12_UAPSERVER_ERROR												12004				/**< UAP服务器错误 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT13_DOWNLOAD_FAIL												13001				/**< 下载头像失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT13_USER_NOT_FOUND												13002				/**< 该用户不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT13_PHOTO_NOT_CHANGED											13003				/**< 头像没有变更 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT13_NO_PHOTO_SETED												13004				/**< 该用户无自定义头像 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT14_DOWNLOAD_FAIL												14001				/**< 下载应用图标失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT14_APP_NOT_FOUND												14002				/**< 该应用不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT14_ICON_NOT_CHANGED											14003				/**< 图标没有变更 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT14_NO_ICON_SETTED												14004				/**< 该应用没有自定义图标 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT15_FAIL														15001				/**< 获取应用最新版本号失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT15_NO_SUCH_APP													15002				/**< 该应用不存在 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT16_FAIL														16001				/**< 根据IMSI判断是否有号码失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT17_SET_PHONE_NUM_FAIL											17001				/**< 设定用户的手机号码失败*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT17_ALREADY_SET_PHONE_NUM										17002				/**< 该用户已经设定了手机号码*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT18_FAIL														18001				/**< 删除账号失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT19_FAIL														19001				/**< 获取应用在资源中心的页面地址失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT19_APP_NOT_EXIST												19002				/**< 该应用不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT19_003															19003				/**< INFO ID 无效 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT20_FAIL														20001				/**< 批量获取用户资料失败 */

//21接口废除
#define ND_COM_PLATFORM_ACT_ERROR_ACT21_FAIL														21001				/**< 使用第三方账号登录出错 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT21_HAS_REGISTERED_TOO_MUCH_ACCOUNT								21002				/**< 该手机或号码已经注册了太多个账号 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT21_3RD_PASSWORD_CANNOT_BE_CHECKED								21003				/**< 无法验证第三方账号密码 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT21_3RD_PASSWORD_ERROR											21004				/**< 第三方账号密码出错 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT21_006															21006				/**< 第三方账号绑定的91账号异常（停用等） */

#define ND_COM_PLATFORM_ACT_ERROR_ACT24_FAIL														24001				/**< 获取第三方好友列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT24_HAS_NO_FRIENDS												24002				/**< 该第三方账号没有好友 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT24_HAVE_NOT_BIND_3RD_ACCOUNT									24003				/**< 未绑定第三方账号 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT31_FAIL														31001				/**< 重新登录，验证第三方帐号授权失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT31_002															31002				/**< 第三方帐号不存在或停用 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT31_003															31003				/**< 当前登录的第三方帐号与绑定的第三方帐号不符 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT31_004															31004				/**< 交换第三方平台AccessToken失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT31_005															31005				/**< 交换第三方平台SessionKey失败 */

#pragma mark -
#pragma mark 91统一接口
#define ND_COM_PLATFORM_ACT_ERROR_ACT32_FAIL														32001				/**< 获取第三方登录配置信息失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT32_002															32002				/**< 指定第三方不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT32_003															32003				/**< 获取临时授权token失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT33_FAIL														33001				/**< 验证第三方帐号授权失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT33_002															33002				/**< 第三方帐号不存在或停用 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT33_003															33003				/**< 第三方帐号已经绑定其他91帐号 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT33_004															33004				/**< 交换第三方平台AccessToken失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT33_005															33005				/**< 交换第三方平台SessionKey失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT34_FAIL														34001				/**< 第三方帐号绑定默认91帐号失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT34_002															34002				/**< 获取第三方帐号信息失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT34_003															34003				/**< 获取默认91帐号失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT34_004															34004				/**< 第三方帐号已经绑定其他91帐号 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT35_FAIL														35001				/**< 解除第三方绑定失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT35_002															35002				/**< 不能解除当前登录的第三方帐号的绑定*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT36_FAIL														36001				/**< 绑定91账号失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT36_HAS_BIND_91ACCOUNT											36002				/**< 第三方账号已绑定91账号 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT36_91ACCOUNT_HAS_BEEN_REGISTERD								36003				/**< 91通行证账号已经被注册 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT36_91ACCOUNT_PASSWORD_ERROR									36004				/**< 91通行证账号密码错误 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT36_91ACCOUNT_NOT_EXIST											36005				/**< 91通行证账号不存在或者停用 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT36_91ACCOUNT_CHECK_FAIL										36006				/**< 验证91通行证账号出错 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT36_3RD_ACCOUNT_INFO_LOST										36007				/**< 第三方登录信息丢失 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT36_HAS_REGISTERED_TOO_MUCH_ACCOUNT								36008				/**< 已经注册了太多的账号 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT36_HAS_BIND_3RD_ACCOUNT										36009				/**< 91账号已经绑定指定类型的第三方账号 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT37_FAIL														37001				/**< 绑定第三方账号失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT37_HAS_BIND_3RD_ACCOUNT										37002				/**< 已经绑定指定类型的第三方账号 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT37_VERIFY_3RD_ACCOUNT_FAIL										37003				/**< 验证第三方账号失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT37_3RD_ACCOUNT_NOT_EXIST										37004				/**< 第三方账号不存在或停用 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT37_3RD_ACCOUNT_PASSWORD_ERROR									37005				/**< 第三方账号密码错 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT37_006															37006				/**< 第三方账号已经绑定其他91账号 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT38_FAIL														38001				/**< 获取用户绑定的第三方账号列表失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT39_FAIL														39001				/**< 获取第三方平台列表失败 */

#pragma mark -

#define ND_COM_PLATFORM_ACT_ERROR_ACT52_FAIL														52001				/**< 下载图标失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT52_002															52002				/**< 该图标不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT52_003															52003				/**< 图标没有变更 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT52_004															52004				/**< 无自定义图标 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT53_001															53001				/**< 应用不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT53_002															53002				/**< 获取模块列表失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT54_FAIL														54001				/**< 获取新应用数失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT55_FAIL														55001				/**< 获取失败*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT56_FAIL														56001				/**< 发送短信失败*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT56_002															56002				/**< 手机号码格式无效*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT56_003															56003				/**< 重复绑定，账号已经绑定手机号码*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT56_004															56004				/**< 手机号码已经绑定其他账号*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT56_005															56005				/**< 短信发送次数过多*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT57_FAIL														57001				/**< 发送短信失败*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT57_002															57002				/**< 手机号码格式无效*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT57_003															57003				/**< 手机号码未绑定*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT57_004															57004				/**< 手机号码与绑定手机号码不一致*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT57_005															57005				/**< 短信发送次数过多*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT58_FAIL														58001				/**< 发送短信失败*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT58_002															58002				/**< 手机号码格式无效*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT58_003															58003				/**< 账号不存在*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT58_004															58004				/**< 手机号码未绑定，无法重置密码*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT58_005															58005				/**< 手机号码与绑定手机号码不一致*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT58_006															58006				/**< 短信发送次数过多*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT59_FAIL														59001				/**< 绑定失败*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT59_002															59002				/**< 手机号码格式无效*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT59_003															59003				/**< 验证码错误*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT59_004															59004				/**< 验证码过期*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT59_005															59005				/**< 重复绑定，账号已经绑定手机号码*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT59_006															59006				/**< 手机号码前后不一致*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT59_007															59007				/**< 手机号码已经被绑定*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT60_FAIL														60001				/**< 解绑失败*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT60_002															60002				/**< 手机号码无效*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT60_003															60003				/**< 验证码错误*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT60_004															60004				/**< 验证码过期*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT60_005															60005				/**< 手机号码未绑定*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT60_006															60006				/**< 手机号码与绑定手机号码不一致*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT61_FAIL														61001				/**< 找回密码验证失败*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT61_002															61002				/**< 手机号码格式无效*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT61_003															61003				/**< 账号不存在*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT61_004															61004				/**< 验证码错误*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT61_005															61005				/**< 验证码过期*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT61_006															61006				/**< 账号未绑定任何手机号*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT61_007															61007				/**< 手机号码与绑定手机号码不一致*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT62_FAIL														62001				/**< 重置密码失败*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT62_002															62002				/**< 账号不存在*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT62_003															62003				/**< 没通过验证，需先调用act61*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT62_004															62004				/**< 新密码格式不合法*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT62_005															62005				/**< VIP用户无法使用手机找回密码*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT63_FAIL														63001				/**< 抽奖失败*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT63_002															63002				/**< 不符合抽奖条件*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT63_003															63003				/**< 已经抽过奖*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT96_001															96001				/**< 获取手机注册短信验证码失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT96_002															96002				/**< 该手机号已经被注册 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT96_003															96003				/**< 指定时间内不能重复发送短信验证码 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT97_001															97001				/**< 发送渠道id失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT97_002															97002				/**< 渠道id已发送过，不能重复发送 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT98_001															98001				/**< 验证用户的帐号密码失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT98_002															98002				/**< 帐号或密码错误 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT98_003															98003				/**< 帐号被停用 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT100_LOGOUT_FAIL												100001				/**< 注销失败*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT101_CHECK_VIRTUAL_PAY_PASSWORD_FAIL							101001				/**< 验证过程出错 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT101_VIRTUAL_PAY_PASSWORD_ERROR									101002				/**< 支付密码错误 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT101_ACCOUNT_NOT_ACTIVED										101003				/**< 商城未开户 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT101_PAY_PASSWORD_NOT_SET										101004				/**< 支付密码未设置 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT102_MODIFY_VIRTUAL_PAY_PASSWORD_FAIL							102001				/**< 密码修改失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT102_OLD_PASSWORD_INVALID										102002				/**< 旧密码格式不合法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT102_NEW_PASSWORD_INVALID										102003				/**< 新密码格式不合法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT102_OLD_PASSWORD_ERROR											102004				/**< 原密码格式错误 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT103_BALANCE_QUERY_FAIL											103001				/**< 查询请求失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT104_PAY_REQUEST_FAIL											104001				/**< 支付请求失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT104_PAY_PASSWORD_NOT_VERIFIED									104002				/**< 支付密码未验证 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT104_BALANCE_NOT_ENOUGH											104003				/**< 余额不足支付 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT104_ORDER_SERIAL_DUPLICATE										104004				/**< 订单号重复 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT105_PAY_RECORD_QUERY_FAIL										105001				/**< 查询失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT105_PAGE_OVERFLOW												105002				/**< 页号超过查询 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT106_FAIL														106001				/**< 发送代付请求失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT106_002														106002				/**< 代付人不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT106_003														106003				/**< 代付人不是你的好友 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT106_004														106004				/**< 订单号重复 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT107_FAIL														107001				/**< 获取代付订单信息失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT107_002														107002				/**< 没有该代付订单 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT107_003														107003				/**< 该代付订单的代付人不是当前用户 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT108_FAIL														108001				/**< 订单状态查询失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT108_002														108002				/**< 无此订单 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT201_RECHARGE_TYPE_QUEIRY_FAIL									201001				/**< 虚拟币充值方式查询失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT202_GET_PAY_HELP_FAIL											202001				/**< 获取充值说明失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT203_ACCOUNT_BIND_FAIL											203001				/**< 支付用91通行证绑定失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT203_ACCOUNT_NOT_EXISTS											203002				/**< 支付用91通行证不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT203_ACCOUNT_LOGIN_PASSWD_ERROR									203003				/**< 支付用91通行证登录密码错误 */ 
#define ND_COM_PLATFORM_ACT_ERROR_ACT203_ACCOUNT_PAY_PASSSWD_ERROR									203004				/**< 支付用91通行证支付密码错误 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT203_ACCOUNT_PAY_PASSWD_NOT_UPLOADED							203005				/**< 支付用91通行证支付密码已设置，但客户端未上传该参数 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT204_QUERY_BALANCE_FAIL											204001				/**< 查询余额请求失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT205_BALANCE_RECHARGE_FAIL										205001				/**< 余额充值请求失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT205_BALANCE_NOT_ENOUGH											205002				/**< 91账号余额不足 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT205_PAY_PASSWORD_ERROR											205003				/**< 91账号支付密码错误 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT206_SMS_RECHARGE_FAIL											206001				/**< 短信充值请求失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT206_SMS_CREATE_BOOK_FAIL										206002				/**< 短信充值订单创建失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT206_SMS_RECHARGE_FEE_ILLEGAL									206003				/**< 短信充值面额非法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT206_SMS_RECHARGE_ACCOUNT_ILLEGAL								206004				/**< 短信支付账号无效 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT206_SMS_HAVE_NOT_MOBILE_NUMBER									206005				/**< 没有电话号码 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT207_SZX_RECHARGE_FAIL											207001				/**< 充值请求失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT207_SZX_RECHARGE_FEE_ILLEGAL									207002				/**< 面额非法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT207_SZX_CARD_NUMBER_ILLEGAL									207003				/**< 卡号无效 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT207_SZX_CARD_PASSWORD_ILLEGAL									207004				/**< 卡密码无效 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT207_SZX_CARD_TYPE_NOT_SUPPORTED								207005				/**< 卡类型不支持 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT208_ZFB_FAIL													208001				/**< 支付宝充值请求失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT208_MONEY_NOT_VALID											208002				/**< 充值金额不合法 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT209_RECHARGE_RECORD_QUREY_FAIL									209001				/**< 查询失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT209_PAGE_NUMBER_OUT_OF_RANGE									209002				/**< 页面超出范围 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT301_FAIL														301001				/**< 联系人上传失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT301_UPLOAD_IGNORED												301002				/**< 相同通讯录已经上传，忽略此次上传 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT302_FAIL														302001				/**< 获取用户Id与通讯录号码对应关系失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT303_FAIL														303001				/**< 通过账号或昵称查找平台用户失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT303_USER_NAME_NULL												303002				/**< UserName不能为空 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT304_FAIL														304001				/**< 通过年龄性别地区查找用户失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT305_FAIL														305001				/**< 发送添加好友请求失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT305_HAS_BEEN_YOUR_FRIEND										305003				/**< 对方已经是你的好友 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT305_COMMENT_LENGTH_INVALID										305004				/**< 备注长度不合法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT305_MAX_FRIEND_NUM_ARRIVED										305005				/**< 超过好友个数上线，需要删除好友 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT306_FAIL														306001				/**< 查找好友列表中的用户失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT307_FAIL														307001				/**< 删除好友失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT308_FAIL														308001				/**< 获取制定好友的好友列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT308_NOT_ALLOWED_TO_SEE											308002				/**< 未授权查看 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT309_FAIL														309001				/**< 获取可能认识的人列表失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT310_FAIL														310001				/**< 获取所有好友的uin列表失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT311_FAIL														311001				/**< 批量邀请好友一起玩失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT311_APP_NOT_FOUND												311002				/**< 该应用不存在 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT312_FAIL														312001				/**< 批量添加好友失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT313_FAIL														313001				/**< 设置备注信息失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT313_NOT_YOUR_FRIEND											313002				/**< 不是你的好友 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT313_INVALID_REMARK												313003				/**< 非法的备注 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT314_FAIL														314001				/**< 获取备注信息失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT314_NOT_YOUR_FRIEND											314002				/**< 不是你的好友 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT315_FAIL														315001				/**< 获取所有好友的简单信息列表失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT331_FAIL														331001				/**< 查看好友应用列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT331_NOT_ALLOWED_TO_SEE											331002				/**< 未授权查看 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT332_FAIL														332001				/**< 查看应用的用户列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT332_APP_NOT_FOUND												332002				/**< 该应用不存在 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT333_FAIL														333001				/**< 查看应用的好友列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT333_APP_NOT_FOUND												333002				/**< 该应用不存在 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT334_FAIL														334001				/**< 查看应用的没玩该应用的我的好友列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT334_APP_NOT_FOUND												334002				/**< 该应用不存在 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT335_FAIL														335001				/**< 查看应用的好友列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT335_APP_NOT_FOUND												335002				/**< 该应用不存在 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT401_FAIL														401001				/**< 获取所有好友动态列表失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT402_FAIL														402001				/**< 获取一个好友动态消息列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT402_FRIEND_NOT_FOUND											402002				/**< 该好友不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT402_PERMISSION_NOT_ENOUGH										402003				/**< 没有权限获取该用户动态 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT403_FAIL														403001				/**< 获取一个好友动态消息列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT403_FRIEND_NOT_FOUNT											403002				/**< 该好友不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT403_APP_ID_INVALID												403003				/**< 应用Id不存在*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT403_TYPE_INVALID												403004				/**< 动态类型不合法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT403_PERMISSION_NOT_ENOUGH										403005				/**< 没有权限获取该用户动态 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT404_FAIL														404001				/**< 获取一个好友的一条动态失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT404_MSG_NOT_FOUND												404002				/**< 没有该条消息 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT404_PERMISSION_NOT_ENOUGH										404003				/**< 没有权限获取该用户动态 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT405_FAIL														405001				/**< 获取新消息数和新系统消息数失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT406_SEND_FAIL													406001				/**< 发送应用下载动态失败*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT406_APP_NOT_EXIST												406002				/**< 该应用不存在*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT407_SEND_FAIL													407001				/**< 动态模板发送失败*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT407_TEMPLATEID_INVALID											407002				/**< 模板id无效*/
#define ND_COM_PLATFORM_ACT_ERROR_ACT407_PARAMLIST_ERROR											407003				/**< 模板参数错误*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT408_FAIL														408001

#define ND_COM_PLATFORM_ACT_ERROR_ACT411_FAIL														411001				/**< 发送消息给好友失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT411_CONTENT_LENGTH_INVALID										411002				/**< 内容长度不合法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT411_CONTENT_INVALID											411003				/**< 内容不合法 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT411_RECEIVER_NOT_FOUND											411004				/**< 接收方不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT411_NOT_ALLOWED_TO_SEND_MSG									411005				/**< 发送者被禁止发消息 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT411_CAN_NOT_SEND_MSG_TO_SELF									411006				/**< 不能给自己发消息 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT411_CAN_NOT_SEND_MSG_TO_STRANGER								411007				/**< 不能给陌生人发消息 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT412_FAIL														412001				/**< 接受所有好友最新消息列表失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT413_FAIL														413001				/**< 接受一个好友消息列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT413_FRIEND_NOT_FOUND											413002				/**< 不存在该好友 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT413_PERMISSION_NOT_ENOUGH										413003				/**< 没有权限获取该用户消息 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT421_FAIL														421001				/**< 接受系统消息列表失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT422_FAIL														422001				/**< 接受一条系统消息失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT422_MSG_NOT_FOUND												422002				/**< 没有该消息 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT431_FAIL														431001				/**< 分享到第三方失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT431_NOT_BIND_3RD												431002				/**< 用户未绑定第三方账号 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT431_REPEAT_CONTENT												431003				/**< 分享内容重复 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT431_IMG_TOO_BIG												431004				/**< 发送的图片超过了服务器允许的大小 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT431_IMG_INVALID												431005				/**< 发送的图片数据不合法 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT432_FAIL														432001				/**< 发送私信失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT432_NOT_BIND_3RD												432002				/**< 用户没有绑定第三方账号 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT432_003														432003				/**< 不能发送重复内容 */


#define ND_COM_PLATFORM_ACT_ERROR_ACT451_FAIL														451001				/**< 响应自定义标签请求失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT451_CLIENT_TAG													451002				/**< 该标签为客户端标签 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT451_INVALID_COMMAND_TAG										451003				/**< 无效的标签指令 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT451_INVALID_TEXT_TAG											451004				/**< 无效的标签文本 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT451_CUSTOM_TAG_NOT_ENOUGH										451005				/**< 自定义标签参数不足 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT451_CUSTOM_TAG_INVALID											451006				/**< 自定义标签参数无效 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT461_FAIL														461001				/**< 获取应用服务商信息失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT462_FEEDBACK_FAIL												462001				/**< 用户向服务商反馈建议或bug失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT462_INVALID_ID													462002				/**< 反馈类型ID无效*/

#define ND_COM_PLATFORM_ACT_ERROR_ACT501_FAIL														501001				/**< 添加或更新用户排行榜数据失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT501_LEADER_BOARD_NOT_EXIST										501002				/**< 该排行榜不存在 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT502_FAIL														502001				/**< 获取应用排行榜列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT502_LEADER_BOARD_NOT_EXIST										502002				/**< 应用排行榜不存在 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT503_FAIL														503001				/**< 获取应用排行榜用户排行列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT503_LEADER_BOARD_NOT_EXIST										503002				/**< 应用排行榜不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT503_NO_FRIENDS_IN_THIS_BOARD									503003				/**< 该用户没有好友在玩 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT510_FAIL														510001				/**< 添加或更新用户成就的数据失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT510_ARCHIEVEMENT_NOT_EXIST										510002				/**< 成就不存在 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT511_FAIL														511001				/**< 获取用户成就列表失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT511_NO_ARCHIEVEMENTS											511002				/**< 该应用没有成就 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT512_FAIL														512001				/**< 图标下载失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT512_ICON_NOT_EXIST												512002				/**< 该图标不存在 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT512_ICON_NOT_CHANGED											512003				/**< 图标没有变更 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT512_NO_CUSTOM_ICON												512004				/**< 无自定义图标 */


#pragma mark  ------------ Virtual Goods ------------

#define ND_COM_PLATFORM_ACT_ERROR_ACT601_FAIL														601001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT601_APPID_INVALID												601002				/**< 应用id无效   */

#define ND_COM_PLATFORM_ACT_ERROR_ACT602_FAIL														602001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT602_APPID_INVALID												602002				/**< 应用id无效  */

#define ND_COM_PLATFORM_ACT_ERROR_ACT603_FAIL														603001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT603_APPID_INVALID												603002				/**< 应用id无效  */
#define ND_COM_PLATFORM_ACT_ERROR_ACT603_CATE_ID_INVALID											603003				/**< 商品类别无效 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT603_FEE_TYPE_INVALID											603004				/**< FeeType无效 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT604_FAIL														604001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT604_APPID_INVALID												604002				/**< 应用id无效  */

#define ND_COM_PLATFORM_ACT_ERROR_ACT605_FAIL														605001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT605_APPID_INVALID												605002				/**< 应用id无效  */

#define ND_COM_PLATFORM_ACT_ERROR_ACT606_FAIL														606001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT606_APPID_INVALID												606002				/**< 应用id无效  */

#define ND_COM_PLATFORM_ACT_ERROR_ACT607_FAIL														607001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT607_APPID_INVALID												607002				/**< 应用id无效  */

#define ND_COM_PLATFORM_ACT_ERROR_ACT608_FAIL														608001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT608_APPID_INVALID												608002				/**< 应用id无效  */

#define ND_COM_PLATFORM_ACT_ERROR_ACT609_FAIL														609001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT609_UseFlag_INVALID											609002				/**< 虚拟商品使用标志无效 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT610_FAIL														610001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT610_ID_INVALID													610002				/**< 商品ID无效 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT621_FAIL														621001				/**< 接口返回失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT622_FAIL														622001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT622_BALANCE_NOT_ENOUGH											622002				/**< 虚拟币余额不足 */


#define ND_COM_PLATFORM_ACT_ERROR_ACT801_FAIL														801001				/**< 使用兑换码失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT801_002														801002				/**< 兑换码无效 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT801_003														801003				/**< 兑换码已经被使用 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT801_004 														801004				/**< 虚拟商店物品不允许非91帐号兑换 */


#pragma mark ------------- Application Promotion ---------------
#define ND_COM_PLATFORM_ACT_ERROR_ACT1001_FAIL														1001001				/**< 接口返回失败 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT1002_FAIL														1002001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT1002_002														1002002				/**< 被点击的应用不存在或已经下架 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT1002_003														1002003				/**< 过度频繁的重复点击行为 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT1003_FAIL														1003001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT1003_002														1003002				/**< 被查询的应用不存在或已经下架 */

#define ND_COM_PLATFORM_ACT_ERROR_ACT1004_FAIL														1004001				/**< 接口返回失败 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT1004_002														1004002				/**< 被查询的应用不存在或已经下架 */
#define ND_COM_PLATFORM_ACT_ERROR_ACT1004_003														1004003				/**< 该应用并未处于待激活状态 */

