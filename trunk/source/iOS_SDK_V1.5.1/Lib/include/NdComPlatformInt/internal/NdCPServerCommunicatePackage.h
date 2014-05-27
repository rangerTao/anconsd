//
//  NdCPServerCommunicatePackage.h
//  NdComPlatform
//
//  Created by Sie Kensou on 10-8-12.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//

#import <Foundation/Foundation.h>

/*!
 和服务器通讯的加密方式
 */
typedef enum _ND_CP_HTTP_PACKAGE_ENCRYPTION {
	ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE = 0,
	ND_CP_HTTP_PACKAGE_ENCRYPTION_GZIP = 1,		
	ND_CP_HTTP_PACKAGE_ENCRYPTION_3DES = 2,
	ND_CP_HTTP_PACKAGE_ENCRYPTION_GZIP_AND_3DES = 3,
	ND_CP_HTTP_PACKAGE_ENCRYPTION_RSA = 4,
	ND_CP_HTTP_PACKAGE_ENCRYPTION_NUM,
}ND_CP_HTTP_PACKAGE_ENCRYPTION;


#pragma mark -
#pragma mark 与服务器通信时发送的数据包
@interface NdCPServerCommunicateSendPackage : NSObject {
	unsigned char	_header[64];
	NSData			*_sendData;
	NSData			*_des3Key;
	NSString		*_rsaPublicKeyModulus;
	NSString		*_rsaPublicKeyExponent;	
}

@property (nonatomic, readonly) NSData *sendData;

- (void)setSessionId:(NSString *)session;		//32位sessionId
- (void)setEncryptionType:(ND_CP_HTTP_PACKAGE_ENCRYPTION)type;
- (ND_CP_HTTP_PACKAGE_ENCRYPTION)encryptionType;
- (void)setActionNumber:(int)act;
- (int)actionNumber;
- (BOOL)setDes3Key:(NSData *)keyData;
- (void)setAppId:(int)appId;
/*!
 设置rsa加密时使用的密钥和公钥
 @param modulusHexString 公钥模十六进制字符串
 @param exponentHexString 公钥幂十六进制字符串
 */
- (BOOL)setRSAPublicKey:(NSString *)modulusHexString exponent:(NSString *)exponentHexString;

/*!
 设置参数键值对
 @param dict 里头包含要传递的数据键值对，key和value都必须是NSString类型；自动生成对应的数据包体
 */
- (BOOL)setSendValuesAndKeysDictionary:(NSDictionary *)dict;

/*!
 设置数据包体数据
 */
- (BOOL)setBodyData:(NSData *)bodyData;

/*!
 生成整个数据包的NSData数据表示，包括数据包头和数据包体
 */
- (NSData *)createDataDescription;


@end


#pragma mark -
#pragma mark 与服务器通信时接受的数据包
@interface NdCPServerCommunicateReceivePackage : NSObject {
	unsigned char	_header[32];
	NSData			*_received;
	NSData			*_des3Key;	
	NSString		*_rsaPublicKeyModulus;
	NSString		*_rsaPublicKeyExponent;	
}

/*!
 设置收到的数据，并进行必要的解密或解压
 */
- (BOOL)setResponseData:(NSData *)responseData;
/*!
 收到的数据的加密方式
 */
- (ND_CP_HTTP_PACKAGE_ENCRYPTION)encryptionType;
/*!
 此次通讯的接口编号
 */
- (int)actionNumber;
/*!
 服务器返回的错误码
 */
- (int)errorCode;
/*!
 MD5校验值起始位置
 */
- (int)md5StartPos;
/*!
 数据报体中的MD5校验值
 */
- (NSString *)md5StringPart;

/*!
 设置解密用的3des密钥数据
 @param keyData 3des密钥数据
 */
- (BOOL)setDes3Key:(NSData *)keyData;

/*!
 设置rsa加密时使用的密钥和公钥
 @param modulusHexString 公钥模十六进制字符串
 @param exponentHexString 公钥幂十六进制字符串
 */
- (BOOL)setRSAPublicKey:(NSString *)modulusHexString exponent:(NSString *)exponentHexString;

/*!
 获取数具体中的键值对，所有的键值均为大写
 */
- (NSDictionary *)valuesAndKeysDictionary;

/*!
 收到的数据包体
 */
- (NSData *)bodyData;


@end
