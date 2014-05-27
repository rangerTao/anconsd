//
//  NdCPUserData.h
//  NdComPlatform
//
//  Created by Sie Kensou on 10-8-12.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//

#import <Foundation/Foundation.h>
#import	<UIKit/UIKit.h>

#define NdCP_CUSTOM_PASTEBOARD  @"NdCPCustomPasteBoard"
#define NdCP_CUSTOM_PASTEBOARD_CONTENT_TYPE     @"NdCPCustomType"

@interface NdCPUserData : NSObject {

}

+ (NSString *)RSAPublicKeyModulus;
+ (BOOL)updateRSAPublicKeyModulus:(NSString *)newRSAPublicKeyModulus;

+ (NSString *)RSAPublicKeyExponent;

+ (BOOL)isAutoLogin;
+ (BOOL)isRememberPassword;

+ (NSString *)lastLoginIMSI;
+ (NSString *)lastLoginAccount;
+ (NSString *)lastLoginPassword;

+ (NSString *)autoLoginIMSI;
+ (NSString *)autoLoginAccount;
+ (NSString *)autoLoginPassword;
+ (void)setAutoLogin:(BOOL)bAutoLogin;
+ (void)setAutoLoginAccount:(NSString *)account password:(NSString *)password autoLogin:(BOOL)autoLogin rememberPassword:(BOOL)rememberPassord;

+ (BOOL)isLoginIMSIChanged;

+ (BOOL)shouldSkipRechargeHelp;
+ (void)setShouldSkipRechargeHelp:(BOOL)bShouldSkip;

+ (int)cachedContactModifyTime;
+ (NSString *)cachedContactSendTime;
+ (NSString *)cachedContactHashValue;
+ (NSDictionary *)cachedContactDictionary;
+ (void)updateCachedInfo:(int)modifyTime hashValue:(NSString *)hashValue sendTime:(NSString *)sendTime;
+ (void)updateCachedContactDictionary:(NSDictionary *)cachedContactDictionary;

+ (NSString *)commonDataStorePath;
+ (NSString *)userDataStorePath;
+ (NSString *)cacheDownloadPath;

+ (void)clearOutDateDownloads;

+ (void)setLastLogin3rdAccount:(NSString *)account type:(int)type;
+ (NSString *)lastLogin3rdAccount:(int)type;

#pragma mark Multi Account Cache Methods
+ (NSArray *)cachedAccountList;
+ (void)cacheAccount:(NSString *)account password:(NSString *)password;
+ (void)removeCacheAccount:(NSString *)account;
+ (void)removeAllCacheAccount;
+ (NSString *)cachedPasswordForAccount:(NSString *)account;

#pragma mark Pasteboard Methods
+ (BOOL)canAutoLoginByPasteboard;
+ (BOOL)hasLocalUserData;
+ (NSDictionary *)dictionaryFromPasteboard:(NSString *)pasteboardName;
+ (void)setDictionary:(NSDictionary *)dict toPasteBoard:(NSString *)pasteboardName;
+ (NSString *)autoLoginAccountFromDict:(NSDictionary *)dict;
+ (NSString *)autoLoginPasswordFromDict:(NSDictionary *)dict;

#pragma mark Guest Login
+ (BOOL)isLastGuestLogin;
+ (NSString *)lastGuestUin;
+ (void)recordGuestUin:(NSString *)uin;
+ (void)removeGuestUinRecord;
@end
