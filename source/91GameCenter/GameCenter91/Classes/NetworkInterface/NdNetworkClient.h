//
//  NdNetworkClient.h
//  NdCloudSynchron
//
//  Created by kensou on 13-5-18.
//  Copyright (c) 2013年 kensou. All rights reserved.
//

#import <Foundation/Foundation.h>

#define ND_NETWORK_CLIENT_NO_ERROR       0
#define ND_NETWORK_CLIENT_PARAM_ERROR    -(NSIntegerMax - 1)

@protocol NdNetworkClientDelegate;

@protocol NdNetworkClient <NSObject>
@property (nonatomic, assign) id<NdNetworkClientDelegate> delegate;

- (int)connect;
- (void)cancel;
@end


@protocol NdNetworkClientDelegate <NSObject>
- (void)client:(id<NdNetworkClient>)client didFinish:(NSData *)data error:(NSError *)error;
@optional
- (void)client:(id<NdNetworkClient>)client didReceiveResponse:(NSURLResponse *)response;
- (void)client:(id<NdNetworkClient>)client didReceiveData:(NSData *)data;

- (void)client:(id<NdNetworkClient>)client didSendBodyData:(NSInteger)bytesWritten totalBytesWritten:(NSInteger)totalBytesWritten totalBytesExpectedToWrite:(NSInteger)totalBytesExpectedToWrite;
@end

