//
//  CommonOperation.h
//  GameCenter91
//
//  Created by  hiyo on 13-10-16.
//
//

#import <Foundation/Foundation.h>
#import "NdNetworkClient.h"
#import "OperationCommonProtocols.h"

#define ND_REQUESTOR_NO_ERROR       ND_NETWORK_CLIENT_NO_ERROR
#define ND_REQUESTOR_PARAM_ERROR    ND_NETWORK_CLIENT_PARAM_ERROR

@interface CommonOperation : NSObject<NdNetworkClientDelegate, OperationCommonProtocol>
@property (nonatomic, strong) id<NdNetworkClient> networkClient;
@property (nonatomic, strong) NSString *requestUrl;

@property (nonatomic, strong) NSDictionary *userInfo;

@property (nonatomic, strong) NSError *errorMessage;

@property (nonatomic, assign) SEL protocolMethod;

//@property (nonatomic, assign) SEL updateMethod;

@property (nonatomic, assign) BOOL usePost;
@property (nonatomic, assign) BOOL isJsonRespone;

@property (nonatomic, assign) id operationDelegate;
@property (nonatomic, strong) NSNumber *referenceNumber;

#pragma override methods below
- (Class<NdNetworkClient>)networkClientClass;   //which is used to create networkClient instance, this class should adopt NdNetworkClient Protocol

- (int)checkParamValidity;

- (NSError *)dealWithData:(NSData *)data;
- (NSInvocation *)callbackInvocationOnObject:(id)object;

//- (NSInvocation *)updateInvocationOnObject:(id)object;
#pragma override methods upside


- (NSInvocation *)invocationOnTarget:(id)target action:(SEL)selector withArguments:(void *)object, ... NS_REQUIRES_NIL_TERMINATION;
- (void)success;
- (void)fail:(NSError *)error;

- (int)operation;
- (void)cancelOperation;

- (NSDictionary *)paramDict;
- (NSData *)postData;
- (NSError *)checkResponseError:(int)statusCode responseHeader:(NSDictionary *)responseHeaderFields responseData:(NSData *)responseData;
- (void)generateResponse:(id)object;
@end

#define INVOC_PARAM(A)                  ({ __typeof__(A) __a = (A); &__a;})
#define INVOC_PARAM_1(A)                INVOC_PARAM(A), nil
#define INVOC_PARAM_2(A, B)             INVOC_PARAM(A), INVOC_PARAM(B), nil
#define INVOC_PARAM_3(A, B, C)          INVOC_PARAM(A), INVOC_PARAM(B), INVOC_PARAM(C), nil
#define INVOC_PARAM_4(A, B, C, D)       INVOC_PARAM(A), INVOC_PARAM(B), INVOC_PARAM(C), INVOC_PARAM(D), nil
