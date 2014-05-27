//
//  Nd3rdAccountMng.h
//  NdComPlatformInt
//
//  Created by xujianye on 11-6-27.
//  Copyright 2011 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
@class Nd3rdAccountInfo, Nd3rdPlatformInfo;


@protocol Nd3rdAccountObserver

@optional
- (void)did3rdAccountBind:(Nd3rdAccountInfo*)info;
- (void)did3rdAccountUnbind:(Nd3rdAccountInfo*)info;
- (void)did3rdAccountReAuth:(Nd3rdAccountInfo*)info;

- (void)didLoad3rdPlatformList:(NSArray*)arr3rdPlfmInfo;
- (void)didLoad3rdAccountList:(NSArray*)arr3rdAccountInfo;

- (void)didFollowApplicationMicroBlogWithError:(NSError*)err;

@end


@interface Nd3rdAccountMng : NSObject {
	NSMutableArray* arr3rdPlatformInfo;
	NSMutableArray* arr3rdPlfmCacheBindedNotAuth;
	NSMutableArray* arr3rdPlfmCacheBindedAuth;
	NSMutableArray* arr3rdPlfmCacheUnbinded;
	NSMutableArray* arr3rdAccouts;
	NSMutableArray* arrObservers;
	int				nNetStatus3rdPlfmInfoList;
	int				nNetStatus3rdAccountInfoList;
	int				nNetStatus3rdAccountInfoList_fresh;
	BOOL			hasApplicationMicroBlog;
}

+ (id)singleton;
- (void)logout;
- (void)reset;

- (BOOL)isLoading3rdPlfmInfoList;
- (BOOL)isLoading3rdAccountInfoList;
- (BOOL)isDownloadFailedOn3rdPlfmInfoList;
- (BOOL)isDownloadFailedOn3rdAccountInfoList;

- (void)refresh3rdPlfmList;
- (void)refresh3rdAccountAuthStatus;
- (void)refresh3rdAccountListIfNeed;

- (void)bind3rdAccountDidFinish:(Nd3rdAccountInfo*)accountInfo;
- (void)unbind3rdAccountDidFinish:(int)n3rdPlatformId;
- (void)reAuth3rdAccountDidFinish:(int)n3rdPlatformId;

- (void)login3rdPlatfromDidFinish:(int)n3rdPlatformId;

- (BOOL)is3rdPlatformBinded:(int)platformId;
- (Nd3rdAccountInfo*)thirdAccountInfoForPlatformId:(int)platformId;		//return nil if not binded
- (NSString*)thirdPlfmNameForPlatformId:(int)platformId;	//return nil if not found
- (Nd3rdPlatformInfo*)thirdPlfmInfoForPlatformId:(int)platformId;	//return nil if not found

- (NSArray*)arrayFor3rdPlfmInfo;
- (NSArray*)arrayFor3rdPlfmInfoBindedNotAuth;
- (NSArray*)arrayFor3rdPlfmInfoBindedAuth;
- (NSArray*)arrayFor3rdPlfmInfoBindedWithoutCurrentLoginPlfm;
- (NSArray*)arrayFor3rdPlfmInfoUnbinded;
- (NSArray*)arrayFor3rdPlfmInfoSupportFriend;
- (NSArray*)arrayFor3rdAccountInfo;

- (BOOL)isMicroBlog3rdPlfm:(int)thirdPlatformId;
- (BOOL)shouldGuideToFollowApplicationMicroBlog;
- (BOOL)shouldGuideToFollowApplicationMicroBlogWithout3rdPlfms:(NSArray*)arr3rdAccountSwitchOff;
- (int)followApplicationMicroBlog;
- (void)cancelFollowApplicationMicroBlog;

- (void)NdAddObserver:(id/*<Nd3rdAccountObserver>*/)observer;
- (void)NdRemoveObserver:(id/*<Nd3rdAccountObserver>*/)observer;

@end


