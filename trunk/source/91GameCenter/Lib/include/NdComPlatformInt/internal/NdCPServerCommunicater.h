//
//  NdCPServerCommunicater.h
//  NdComPlatform
//
//  Created by Sie Kensou on 10-8-12.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NdCPServerCommunicatePackage.h"

extern NSString * const NSNDCommunicateErrorDomain;		/**< 如果服务器返回的数据包校验出错，则返回的errorCode的domain为NSNDCommunicateErrorDomain */

@interface NdCPServerCommunicater : NSObject {
	int				_actionNumber;
	long			_sendReference;
	id				_communicateDelegate;
	int				_tag;	
}

@property (nonatomic, assign) id communicateDelegate;
@property (nonatomic, readonly) int actionNumber;
@property (nonatomic, readonly) long sendRef;
@property (nonatomic, assign) int tag;

+ (NSString *)sessionId;
+ (BOOL)setSessionId:(NSString *)sessionId;
+ (BOOL)setLoginedSessionId:(NSString*)sessionId;

+ (NSData *)des3Key;
+ (BOOL)setDes3Key:(NSData *)des3Key;

+ (NSString *)rsaPublicKeyModulus;
+ (NSString *)rsaPublicKeyExponent;
+ (BOOL)setRSAPublicKeyModulus:(NSString *)modulusHexString;
+ (BOOL)setRSAPublicKeyExponent:(NSString *)exponentHexString;

+ (int)appId;
+ (BOOL)setAppId:(int)appId;

+ (NSString *)appKey;
+ (BOOL)setAppKey:(NSString *)appKey;

+ (void)clearMemory;

- (BOOL)sendDataToURL:(const NSString *)url paramDictionary:(NSDictionary *)paramDictionary actionNumber:(int)actionNumber encryptType:(ND_CP_HTTP_PACKAGE_ENCRYPTION)encryptType;
- (BOOL)sendDataToURL:(const NSString *)url data:(NSData *)data actionNumber:(int)actionNumber encryptType:(ND_CP_HTTP_PACKAGE_ENCRYPTION)encryptType;

- (void)cancel;
@end

@protocol NdCPServerCommunicaterProtocol

- (void)communicateDidFinish:(NdCPServerCommunicater *)communicater receivedData:(NdCPServerCommunicateReceivePackage *)receivedPackage;
- (void)communicateDidFail:(NdCPServerCommunicater *)communicater error:(NSError *)errorCode;
- (void)communicateDidCancel:(NdCPServerCommunicater *)communicater;

@end
