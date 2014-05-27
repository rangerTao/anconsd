//
//  NdCSNetworkClient.m
//  NdCloudSynchron
//
//  Created by kensou on 13-5-18.
//  Copyright (c) 2013年 kensou. All rights reserved.
//

#import "NdCSNetworkClient.h"

@interface NdCSNetworkClient()
@property (nonatomic, strong) NSMutableData *acceptedData;
@property (nonatomic, strong) NSURLConnection *urlConnection;

- (void)clearStatus;
- (int)connectWithUrl:(NSString *)url postData:(NSData *)data;
@end

@implementation NdCSNetworkClient
@synthesize delegate;
- (id) init
{
    self = [super init];
    if (self != nil) {
        self.acceptedData = [NSMutableData new];
        self.delegate = nil;
    }
    return self;
}


- (void) dealloc
{
    [self clearStatus];
    [super dealloc];
}

- (void)clearStatus
{
    [self.urlConnection cancel];
    self.urlConnection = nil;
    [self.acceptedData setData:nil];
}

- (void)setRequest:(NSMutableURLRequest *)request postData:(NSData *)data
{
    [request setHTTPMethod:@"POST"];
    //[request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField: @"Content-Type"];
    [request setValue:@"singlepart/form-data" forHTTPHeaderField: @"Content-Type"];

    if ([data length] != 0)
    {
        NSString *postLength = [NSString stringWithFormat:@"%d", [data length]];
        [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
        [request setHTTPBody:data];
    }
}

- (void)setGizpAccept:(NSMutableURLRequest*)urlRequest
{
	[urlRequest setValue:@"gzip" forHTTPHeaderField:@"Accept-Encoding"];
}

- (int)connectWithUrl:(NSString *)url postData:(NSData *)data
{
    [self clearStatus];
    if ([url length] == 0)
        return ND_NETWORK_CLIENT_PARAM_ERROR;
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    
    if (self.acceptGZip)
    {
        [self setGizpAccept:request];
    }
    
    if (self.usePost)
    {
        [self setRequest:request postData:data];
    }
    
    NSString *httpMethod = self.usePost ? @"POST" : @"GET";
    [request setHTTPMethod:httpMethod];
    
    self.urlConnection = [NSURLConnection connectionWithRequest:request delegate:self];
    return ND_NETWORK_CLIENT_NO_ERROR;
}

- (void)cancel
{
    [self clearStatus];
    self.delegate = nil;
}

- (int)connect
{
    return [self connectWithUrl:self.requestUrl postData:self.postData];
}

#pragma mark NSURLConnection Delegates
- (NSURLRequest *)connection:(NSURLConnection *)connection willSendRequest:(NSURLRequest *)request redirectResponse:(NSURLResponse *)response
{
	if ([response isKindOfClass:[NSHTTPURLResponse class]])
	{
		if ([(NSHTTPURLResponse*)response statusCode] >= 400)				//we do not allow redirect for errors like 404 and etc.
			return nil;
	}
	return request;
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
	if ([response isKindOfClass:[NSHTTPURLResponse class]])
    {
        self.responseHeader = [(NSHTTPURLResponse *)response allHeaderFields];
        self.statusCode = [(NSHTTPURLResponse *)response statusCode];
    }
    
    if ([self.delegate respondsToSelector:@selector(client:didReceiveResponse:)])
    {
        [self.delegate client:self didReceiveResponse:response];
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [self.acceptedData appendData:data];
    
    if ([self.delegate respondsToSelector:@selector(client:didReceiveData:)])
    {
        [self.delegate client:self didReceiveData:data];
    }
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    [self.delegate client:self didFinish:self.acceptedData error:nil];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    [self.delegate client:self didFinish:self.acceptedData error:error];
}

- (void)connection:(NSURLConnection *)connection didSendBodyData:(NSInteger)bytesWritten totalBytesWritten:(NSInteger)totalBytesWritten totalBytesExpectedToWrite:(NSInteger)totalBytesExpectedToWrite
{
    if ([self.delegate respondsToSelector:@selector(client:didSendBodyData:totalBytesWritten:totalBytesExpectedToWrite:)])
    {
        [self.delegate client:self didSendBodyData:bytesWritten totalBytesWritten:totalBytesWritten totalBytesExpectedToWrite:totalBytesExpectedToWrite];
    }
}

@end
