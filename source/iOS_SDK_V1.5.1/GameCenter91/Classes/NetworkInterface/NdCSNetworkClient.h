//
//  NdCSNetworkClient.h
//  NdCloudSynchron
//
//  Created by kensou on 13-5-18.
//  Copyright (c) 2013年 kensou. All rights reserved.
//

#import "NdNetworkClient.h"

@interface NdCSNetworkClient : NSObject<NdNetworkClient>
@property (nonatomic, assign) BOOL acceptGZip;
@property (nonatomic, strong) NSDictionary *responseHeader;
@property (nonatomic, assign) int statusCode;

@property (nonatomic, assign) BOOL usePost;

@property (nonatomic, strong) NSString *requestUrl;
@property (nonatomic, strong) NSData *postData;

@end
