//
//  CommonOperation.m
//  GameCenter91
//
//  Created by  hiyo on 13-10-16.
//
//

#import "CommonOperation.h"
#import "NdCSNetworkClient.h"
#import <Log/NDLogger.h>
#import <NdJSON/NDSBJSON.h>

@implementation CommonOperation
- (void) dealloc
{
    [self.networkClient cancel];
    self.networkClient.delegate = nil;
    [super dealloc];
}

- (id)init
{
    self = [super init];
    if (self) {
        Class cl = [self networkClientClass];
        if (cl == nil)
            return nil;
        self.networkClient = [[cl alloc] init];
        self.networkClient.delegate = self;
        self.isJsonRespone = YES;
    }
    return self;
}

- (Class<NdNetworkClient>)networkClientClass
{
    return [NdCSNetworkClient class];
}

- (int)checkParamValidity
{
    return ND_REQUESTOR_NO_ERROR;
}

- (void)cancel
{
    [self.networkClient cancel];
}

- (void)fail:(NSError *)error
{
    self.errorMessage = error;
    id operationDelegate = self.operationDelegate;
    if (operationDelegate && [operationDelegate respondsToSelector:@selector(requestorDidFinish:error:)])
    {
        [operationDelegate requestorDidFinish:self error:error];
    }
}

- (void)success
{
    id operationDelegate = self.operationDelegate;
	if (operationDelegate && [operationDelegate respondsToSelector:@selector(serverOperationDidFinish:error:)])
		[operationDelegate serverOperationDidFinish:self error:nil];
}

- (NSDictionary *)paramDict
{
    return nil;
}

- (NSData *)postData
{
    return nil;
}

- (NSError *)checkResponseError:(int)statusCode responseHeader:(NSDictionary *)responseHeaderFields responseData:(NSData *)responseData
{
    if (statusCode == 200)
        return nil;
    
    NSString *errDesc = nil;
    int errorCode = statusCode;
    if (self.isJsonRespone) {
        NSString *jsonString = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
        NDSBJSON *json = [NDSBJSON new];
        NSError *parseError = nil;
        NSDictionary* errContent = (NSDictionary*)[json objectWithString:jsonString error:&parseError];
        if (!parseError) {
            errDesc = [errContent objectForKey:@"error"];
            errorCode = [[errContent objectForKey:@"error_code"] intValue];
        }
    }
    
    if (errDesc == nil)
        errDesc = @"服务端返回响应错误";
    NSError *error = [NSError errorWithDomain:@"NdCSRequestorErrorDomain" code:errorCode userInfo:
                      [NSDictionary dictionaryWithObject:errDesc forKey:NSLocalizedDescriptionKey]];
    return error;
}

- (void)generateResponse:(id)object
{
    
}

- (NSError *)dealWithData:(NSData *)data
{
    NdCSNetworkClient *client = (NdCSNetworkClient *)(self.networkClient);
    NSError *err = [self checkResponseError:client.statusCode responseHeader:client.responseHeader responseData:data];
    if (err)
        return err;
    
    id content;
    if (self.isJsonRespone && [data length] > 0) {
        NSString *jsonString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NDSBJSON *json = [NDSBJSON new];
        NSError *parseError = nil;
        content = [json objectWithString:jsonString error:&parseError];
        if (parseError)
            return parseError;
    }
    else {
        content = (id)data;
    }
    
    [self generateResponse:content];
    return nil;
}

- (void)client:(id<NdNetworkClient>)client didFinish:(NSData *)data error:(NSError *)error
{
    if (error)
    {
        [self fail:error];
        return;
    }
    
    NSError *err = [self dealWithData:data];
    if (err)
    {
        [self fail:err];
        return;
    }
    
    [self success];
}

#pragma mark dynamic callback
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
    
    int index = 2;  //the first two has been set to target and selector
    
    [invoc setArgument:(void *)(&self) atIndex:index++];
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
    return [self invocationOnTarget:object action:self.protocolMethod withArguments: nil];
}

//- (NSInvocation *)updateInvocationOnObject:(id)object
//{
//    return [self invocationOnTarget:object action:self.updateMethod withArguments: nil];
//}

#pragma mark - OperationCommonProtocol
- (int)operation
{
    int res = [self checkParamValidity];
    if (res != ND_REQUESTOR_NO_ERROR)
        return res;
    
    NSDictionary *paramDict = [self paramDict];
    NSData *rawPostData = [self postData];
    NSString *url = self.requestUrl;
    NSData *data = nil;
    if (self.usePost)
    {
        if (rawPostData) {
            data = rawPostData;
        }
        else {
            NDSBJSON *json = [NDSBJSON new];
            NSError *jsonErr = nil;
            NSString *jsonString = [json stringWithObject:paramDict error:&jsonErr];
            if (jsonErr) {
                NSLog(@"%@", jsonErr);
            }
            data = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
        }
    }
    else
    {
        if (paramDict)
        {
            NSString *params = @"";
            
            BOOL first = YES;
            for (NSString *key in paramDict)
            {
                NSString *value = [paramDict objectForKey:key];
                if (first)
                {
                    params = [params stringByAppendingFormat:@"?%@=%@", key, value];
                }
                else
                {
                    params = [params stringByAppendingFormat:@"&%@=%@", key, value];
                }
                first = NO;
            }
            url = [url stringByAppendingString:params];
        }
    }
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    NdCSNetworkClient *client = (NdCSNetworkClient *)(self.networkClient);
    client.requestUrl = url;
    client.postData = data;
    client.usePost = self.usePost;
    
    return [self.networkClient connect];
}

- (void)cancelOperation
{
    [self.networkClient cancel];
}

- (void)callProtocolMethodOnObject:(id)object
{
    NSInvocation *invoc = [self callbackInvocationOnObject:object];
    [invoc invoke];
}

@end