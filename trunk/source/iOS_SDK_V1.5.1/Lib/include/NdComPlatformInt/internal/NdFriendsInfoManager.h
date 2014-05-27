//
//  NdFriendsInfoManager.h
//  NdComPlatformInt
//
//  Created by xujianye on 11-3-19.
//  Copyright 2011 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
@class NdFriendRemarkUserInfo;
@class NdFriendsInfoManager;
@class NdUserInfo;

@protocol NdFriendObserverDelegate
@required
- (void)updateNdFriendsWithData:(NdFriendsInfoManager*)singleton;

@end



@interface NdFriendsInfoManager : NSObject {
	NSMutableArray*			arrObservers;
	NSMutableDictionary*	dicGroupFriends;
	NSMutableDictionary*	dicUinToFriendInfo_auxi;
	id						downloadDelegate;
	NSString*				filterKeySrc;
	NSString*				filterKeyPinyinInitial;
	NSArray*				arrSectionTitle_auxi;
}

@property (nonatomic, retain) NSMutableDictionary* dicGroupFriends;

+ (NdFriendsInfoManager*)singleton;
+ (void)releaseSingleton;

- (void)reset;

- (void)NdMngDownloadAllFriends;

- (BOOL)isMyFriend:(NSString*)strUin;
- (BOOL)isFriendsEmpty;
- (int)friendsCount;
- (NdFriendRemarkUserInfo*)friendRemarkUserInfoForUin:(NSString*)uin;
- (NSArray*)arrSectionTitle;
- (NSMutableArray*)filterFriendWithKey:(NSString*)key;
- (NSMutableArray*)getAllFriendWithOrder;


- (void)addFriendsInfoObserver:(id<NdFriendObserverDelegate>)friendsObserver;
- (void)removeFriendsInfoObserver:(id<NdFriendObserverDelegate>)friendsObserver;

- (void)addFriendWithUin:(NSString*)strUin;
- (void)removeFriendWithData:(NdFriendRemarkUserInfo*)friendInfo;
- (void)removeFriendWithUin:(NSString*)uin;
- (void)updateFriendRemark:(NSString*)uin  friendRemark:(NSString*)remark;
- (void)updateFriendInfo:(NdUserInfo*)info;

@end
