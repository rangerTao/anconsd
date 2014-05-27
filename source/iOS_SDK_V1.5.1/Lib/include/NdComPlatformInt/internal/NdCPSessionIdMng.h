//
//  NdCPSessionIdMng.h
//  NdComPlatform_SNS
//
//  Created by xujianye on 12-5-30.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface NdCPSessionIdMng : NSObject {
	NSMutableArray*		arrInvocWhenSuccessful;
	NSMutableArray*		arrInvocWhenFailed;
	BOOL				isLoading;
	NSError*			errorForSessionId;
}

+ (id)sharedInstance;


- (BOOL)hasSessionId;

- (NSError*)errorForSessionId;

- (void)invokeWhenDidGetSessionId:(NSInvocation*)invocSuccessful  orInvokeWhenFailed:(NSInvocation*)invocFailed;

@end
