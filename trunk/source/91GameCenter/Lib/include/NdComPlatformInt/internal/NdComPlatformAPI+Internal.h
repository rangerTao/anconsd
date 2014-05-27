/*
 *  NdComPlatformAPI+Internal.h
 *  NdComPlatform_SNS
 *
 *  Created by Sie Kensou on 10-10-11.
 *  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
 *
 */

#import "NdComPlatformAPI.h"
#import "NdCPServerOperation.h"
#import "SMSSender.h"

#define SMS_TAG_BIND_PHONE		88
#define SMS_TAG_RECHARGE		99
#define SMS_TAG_BIND_USER_PHONE 77
extern id g_checkSMSDelegate;
extern int g_smsTag;

@interface NdComPlatformAPI(Internal)

+ (NSString *)sdkVersion;

+ (BOOL)bindPhoneSmsSender:(SMSSender*)sender errorSendSMS:(NSString*)msg toPhoneNumber:(NSString*)phonenum;
+ (BOOL)bindPhoneSmsSender:(SMSSender*)sender receivedSMS:(NSString*)msg fromPhoneNumber:(NSString*)phonenum time:(NSString *)time;

+ (BOOL)rechargeSmsSender:(SMSSender*)sender errorSendSMS:(NSString*)msg toPhoneNumber:(NSString*)phonenum;
+ (BOOL)rechargeSmsSender:(SMSSender*)sender haveSentSMS:(NSString*)msg toPhoneNumber:(NSString*)phonenum;

+ (void)releaseFriendsWithUserNameCache;

//+ (void)addDelegateObject:(id)object netId:(int)netId;
//+ (void)removeDelegateObject:(int)netId;
//+ (BOOL)isDelegateValid:(id)delegate forId:(int)netId;
+ (NSString*)getChannelIdFromCfgFile;

+ (NSString*)findFriendRemarkWithUin:(NSString*)uin andNickName:(NSString*)nickName;

@end
