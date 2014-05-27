//
//  NdPhoneAccountBdMng.h
//  NdComPlatformInt
//
//  Created by xujianye on 12-3-1.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class NdPhoneAccountBdMng;

@protocol NdPhoneAccountBdObserver

- (void)updatePhoneAccountBdInfoWithMng:(NdPhoneAccountBdMng*)mng;

@end


@interface NdPhoneAccountBdMng : NSObject {
	BOOL	needBindPhone;
	BOOL	hasActivity;
	NSString*	strActivityInfo;
	NSString*	strPhoneNo;
	
	NSMutableDictionary*	dicCacheData;
	int		downloadInfoStatus;
	BOOL	observeCancelBinding;
	id<NdPhoneAccountBdObserver>		downloadObserver;
}

+ (NdPhoneAccountBdMng*)singleton;

@property (nonatomic, retain) 	NSString*	strActivityInfo;
@property (nonatomic, retain) 	NSString*	strPhoneNo;

- (void)reset;
- (void)setBindedStatus;
- (void)setCancelBindedStatus;
- (void)setUnbindedStatus;

- (BOOL)hasDownloadedBindInfo;
- (BOOL)hasFailedOnDownloadingBindInfo;

- (int)downloadBindInfoWithObserver:(id/*<NdPhoneAccountBdObserver>*/)observer;

- (BOOL)shouldGuideToBind;
- (BOOL)hasBindedAccount;
- (BOOL)hasActivity;
- (BOOL)hasCacheBindedFlag;

- (void)observeCancelPhoneBinding;

@end
