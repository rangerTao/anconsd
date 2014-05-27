//
//  GameCenterOperation.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-7.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "GameCenterOperation.h"
#import <Log/NDLogger.h>
#import "NSInvocation+NdCPSimple.h"
#import "GameCenterMacros.h"

#pragma mark -
#pragma mark extern some method which are defined in NdComPlatformInt
//NdComPlatformInt methods
extern NSError *errorWithCodeAndDesc(int errorCode, NSString *errorDesc);
extern NSString *NDSTR(NSString *input);
//extern int convertError(int error);
//NdComPlatformInt methods

#pragma mark -
@interface NdCPServerOperation(Extend)
@property (nonatomic, retain) NdCPServerCommunicater*  communicater;

+ (void)addSessionalOperation:(NdCPServerOperation*)op;
+ (void)removeSessionalOperation:(NdCPServerOperation*)op;
- (void)fail:(NSError *)error  shouldPostError:(BOOL)should;
- (int)convertError:(int)error;
@end

@implementation GameCenterOperation
@synthesize referenceNumber;
@synthesize actionNumber;
@synthesize protocolMethod;
@synthesize errorMessage;

- (id)init {
    self = [super init];
    if (self) {
        self.errorMessage = nil;
        self.actionNumber = 0;
    }
    return self;
}

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

- (NSString *)requestUrl
{
    return [BASE_URL stringByAppendingFormat:@"%d", self.actionNumber];
}

- (int)transverToOutputError:(int)errorCode
{
    //小于1000错误使用和SDK一样的错误码转换
    if (errorCode < 1000)
        return [self convertError:errorCode];
    //大于1000的是游戏中的自己的接口，直接转换为负数
    return 0 - errorCode;
}

- (void)generateResponse:(NSDictionary *)paramDict
{
    //do nothing
}

- (NSInvocation *)invocationOnTarget:(id)target action:(SEL)selector withArguments:(void *)object, ...
{
    if (selector == NULL)
        return nil;
    
    NSMethodSignature* sign = nil;
	if ([target respondsToSelector:@selector(methodSignatureForSelector:)]) {
		sign = [target methodSignatureForSelector:selector];
	}
	else {
		sign = [target instanceMethodSignatureForSelector:selector];
	}
    
    if (sign == nil)
    {
        NDLOG(@"%@ does not implement callback %@", target, NSStringFromSelector(selector));
        return nil;
    }
    
    int basicArgCount = 4;//the 4 here ->target and selector takes two parameter, and operation, error take the other two
    
    int argCount = [sign numberOfArguments];
    if (argCount < basicArgCount)   
    {
        NDLOG(@"selector param less than %d %@", basicArgCount, NSStringFromSelector(selector));
        return nil;
    }
    
	NSInvocation* invoc = [NSInvocation invocationWithMethodSignature:sign];
	[invoc setTarget:target];
	[invoc setSelector:selector];
    
    int index = 2;  //the first two is target and selector
    [invoc setArgument:&self atIndex:index++];
    NSError *err = [self errorMessage];
    [invoc setArgument:&err atIndex:index++];
    
    argCount = argCount - basicArgCount;
    va_list arg_ptr;
    void *argValue = object;
    va_start(arg_ptr, object);
    while (argCount--)
    {
        [invoc setArgument:argValue atIndex:index++];                    
        argValue = va_arg(arg_ptr, void *);           
    };
    return invoc;
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    if (self.protocolMethod)
    {
        return [self invocationOnTarget:object action:self.protocolMethod withArguments: nil];
    }
    return nil;
}

- (void)callProtocolMethodOnObject:(id)object
{
    NSInvocation *invoc = [self callbackInvocationOnObject:object];
    [invoc invoke];
}

- (int)sendRequest:(NSDictionary *)paramDict encrytType:(int)type
{
	if (self.actionNumber <= 0)	{
		NDLOG(@"action number not set");
		return ND_COM_PLATFORM_ERROR_PARAM;
	}
    
    NSString *url = [self requestUrl];
	NDLOG(@"send gamecenter actionNumber:======= %d =======\n url = %@", self.actionNumber, url);
    
	if (self.beWithSessionId && [[NdCPServerCommunicater sessionId] length] <= 0) {
		NDLOG(@"sessionId invalid gamecenter actionNumber:%d \n", self.actionNumber);
		return ND_COM_PLATFORM_ERROR_SESSIONID_INVALID;
	}
    
	NdCPServerCommunicater* cmnTmp = [[NdCPServerCommunicater alloc] init];
	cmnTmp.communicateDelegate = self;
	self.communicater = cmnTmp;
	[cmnTmp release];
	
	BOOL res = [self.communicater sendDataToURL:url paramDictionary:paramDict actionNumber:self.actionNumber encryptType:type];
	if (res == NO)
	{
		NDLOG(@"send gamecenter comunciater fail");
		self.communicater = nil;
		return ND_COM_PLATFORM_ERROR_PARAM;
	}
    
    
	[[self class] addSessionalOperation:self];
    
	return ND_COM_PLATFORM_NO_ERROR;	
}

- (void)success
{
	NDLOG(@"gamecenter action %d success", self.actionNumber);
	
	id operationDelegate = self.operationDelegate;
	if (operationDelegate && [operationDelegate respondsToSelector:@selector(serverOperationDidFinish:error:)])
		[operationDelegate serverOperationDidFinish:self error:nil];
}

- (void)communicateDidFinish:(NdCPServerCommunicater *)communicater receivedData:(NdCPServerCommunicateReceivePackage *)receivedPackage
{
	[[self class] removeSessionalOperation:self];
    
	NSDictionary *paramDict = [receivedPackage valuesAndKeysDictionary];
	int errorCode = [receivedPackage errorCode];
	if (errorCode != 0)
	{
//		NDLOG(@"gamecenter action %d failed, error number = %d, ", _actionNumber, errorCode);
        [self errorRecevied:errorCode paramDict:paramDict];
        
		int outError = [self transverToOutputError:errorCode];
        NSError *error = errorWithCodeAndDesc(outError, [paramDict objectForKey:@"Result"]);
		[self fail:error];
	}
	else 
	{
//        if (_bCheckResultParam)
        {
            if (paramDict == nil)
            {
                NSError *error = errorWithCodeAndDesc(ND_COM_PLATFORM_ERROR_SERVER_RETURN_ERROR, NDSTR(@"ND_SERVER_ERROR"));
                [self fail:error];
                return;
            }
            else
            {
//                if (_bCheckResultCode)
                {
                    NSString *strCode = [paramDict objectForKey:@"ResultCode"];
                    NSString *strDesc = [paramDict objectForKey:@"Result"];
                    int nErrorCode = [strCode intValue];
                    nErrorCode = [self transverToOutputError:nErrorCode];
                    if ([self respondsToSelector:@selector(setResultCode:)])
                    {
                        NSInvocation *invocation = [NSInvocation NdCPInvocationWithTarget:self selector:@selector(setResultCode:)];
                        [invocation setArgument:&nErrorCode atIndex:2];
                        [invocation invoke];
                    }
                    
                    if ([self respondsToSelector:@selector(setResultDesc:)])
                    {
                        [self performSelector:@selector(setResultDesc:) withObject:strDesc];
                    }
                    
                    if (nErrorCode != 0) {
                        NDLOG(@"action = %d resultCode =  %d", self.actionNumber, nErrorCode);
                        [self resultCodeCheckFail:paramDict];
                        
                        NSError *error = errorWithCodeAndDesc(nErrorCode, strDesc);
                        [self fail:error];
                        return;
                    }
                }                
            }
            
        }
        
		[self generateResponse:paramDict];
        [self success];
	}
}

- (void)communicateDidFail:(NdCPServerCommunicater *)communicater error:(NSError *)error
{
	NDLOG(@"gamecenter action %d failed, net work fail %@", self.actionNumber, error);
	
	[[self class] removeSessionalOperation:self];
	
	int errorCode = ND_COM_PLATFORM_ERROR_NETWORK_FAIL;
	if ([[error domain] isEqual:NSNDCommunicateErrorDomain])
		errorCode = ND_COM_PLATFORM_ERROR_NETWORK_ERROR;
	NSDictionary *userInfo = [NSDictionary dictionaryWithObjectsAndKeys:NDSTR(@"ND_WEB_ERROR"),NSLocalizedDescriptionKey,nil];
    //here use different domain!
	NSError *errorNew = [[NSError alloc] initWithDomain:NSNDCommunicateErrorDomain code:errorCode userInfo:userInfo];
	[self fail:errorNew];
	[errorNew release];
}

- (void)communicateDidCancel:(NdCPServerCommunicater *)communicater
{
	NDLOG(@"gamecenter action %d canceled", self.actionNumber);
	
	[[self class] removeSessionalOperation:self];
	
	if (self.operationDelegate && [self.operationDelegate respondsToSelector:@selector(serverOperationDidCancel:)])
		[self.operationDelegate serverOperationDidCancel:self];
}

- (void)fail:(NSError *)error  shouldPostError:(BOOL)should
{
    NDLOG(@"gamecenter action %d fail %@", self.actionNumber, error);
    self.errorMessage = error;
    [super fail:error shouldPostError:should];
}
@end
