//
//  GameCenterOperation.h
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-7.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "NdCPServerOperation.h"
#import "OperationCommonProtocols.h"

@interface GameCenterOperation : NdCPServerOperation<OperationCommonProtocol>

@property (nonatomic, retain) NSNumber *referenceNumber;
@property (nonatomic, assign) int actionNumber;
@property (nonatomic, retain) NSError *errorMessage;

@property (nonatomic, assign) SEL protocolMethod;

- (NSString *)requestUrl;
- (int)transverToOutputError:(int)errorCode;

- (int)sendRequest:(NSDictionary *)paramDict encrytType:(int)type;
- (void)generateResponse:(NSDictionary *)paramDict;

- (NSInvocation *)callbackInvocationOnObject:(id)object;
- (void)callProtocolMethodOnObject:(id)object;

- (NSInvocation *)invocationOnTarget:(id)target action:(SEL)selector withArguments:(void *)object, ... NS_REQUIRES_NIL_TERMINATION;


@end

#define INVOC_PARAM(A)          ({ __typeof__(A) __a = (A); &__a;})
#define INVOC_PARAM_2(A, B)     INVOC_PARAM(A), INVOC_PARAM(B), nil
#define INVOC_PARAM_3(A, B, C)  INVOC_PARAM(A), INVOC_PARAM(B), INVOC_PARAM(C), nil
#define INVOC_PARAM_4(A, B, C, D)  INVOC_PARAM(A), INVOC_PARAM(B), INVOC_PARAM(C), INVOC_PARAM(D), nil