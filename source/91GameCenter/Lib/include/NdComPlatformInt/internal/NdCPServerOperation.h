//
//  NdCPServerOperation.h
//  NdComPlatform
//
//  Created by Sie Kensou on 10-8-12.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NdCPServerCommunicater.h"
#import "NdComPlatformError.h"

@interface NdCPServerOperation : NSObject<NdCPServerCommunicaterProtocol> {
	NdCPServerCommunicater *_communicater;
	NSDictionary			*_userInfo;
	id						_operationDelegate;
	id						_requestDelegate;
	
	int						_actionNumber;
	
	BOOL					_isSynchronous;
	int						_error;
	BOOL					_bWithSessionId;
	BOOL					_bCheckResultParam;
    BOOL                    _bCheckResultCode;
}

@property (nonatomic, retain)	id operationDelegate;
@property (nonatomic, retain)	id requestDelegate;
@property (nonatomic, retain)	NSDictionary *userInfoDic;
@property (nonatomic, readonly) int actionNumber;
@property (nonatomic, readonly) int netId;
@property (nonatomic, assign)	BOOL beWithSessionId;	//默认为YES

- (int)operation;
- (int)sendRequest:(NSString *)url paramDict:(NSDictionary *)paramDict actionNumber:(int)actionNumber encrytType:(int)type;
- (void)fail:(NSError *)error;
- (void)success;
- (void)cancelOperation;
- (void)failOpertaionForSessionIdWithError:(NSError*)error;

- (void)errorRecevied:(int)errorCode paramDict:(NSDictionary *)paramDict;
- (void)resultCodeCheckFail:(NSDictionary *)paramDict;


+ (void)cancelAllSessionalOperationForLogout;
+ (void)cancelAllSessionalOperationForChangedUser;
+ (BOOL)setLoginedSessionId:(NSString*)sessionNew;

@end

@protocol NdCPServerOperationProtocol

- (void)serverOperationDidFinish:(NdCPServerOperation *)operation error:(NSError *)error;
- (void)serverOperationDidCancel:(NdCPServerOperation *)operation;

@end


@interface NdCPServerOperation(Synchronous)
- (int)synchronousOperation;
@end
