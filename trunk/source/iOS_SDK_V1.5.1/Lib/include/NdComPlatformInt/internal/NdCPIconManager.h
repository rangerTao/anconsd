//
//  NdCPIconManager.h
//  NdComPlatform_SNS
//
//  Created by Sie Kensou on 10-9-29.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//
/* 
 使用例子：
 NdCPIconManager* singleton = [NdCPIconManager singleton];

 //加载图片
 get..Icon
 //删除监听
 remove..observer;
*/
#import <UIKit/UIKit.h>
#import "NdIconObservers.h"

#define  DEFAULT_USER_PHOTO_TYPE	1


@interface NdCPIconManager : NSObject {

}

- (UIImage *)getUserIconDefault:(NSUInteger)imgType;
- (UIImage *)getUserIconDefault;
- (UIImage *)getAppIconDefault;
- (UIImage *)getBoardIconDefault:(NSUInteger)boardType  imgType:(NSUInteger)imgType;

- (BOOL)uploadPhotoImg:(UIImage*)imgHead			setPortraitDelegate:(id)delegate;
- (BOOL)uploadPhotoFile:(NSString*)fileFullPath		setPortraitDelegate:(id)delegate;

- (UIImage *)getUserIcon:(NSString *)uin  
				checkSum:(NSString*)checkSum  
			   photoType:(NSUInteger)type  
				observer:(id<NdIconObserverDelegate>)observer;
- (UIImage *)getAppIcon:(NSString *)appID  
			   checkSum:(NSString*)checkSum  
			   observer:(id<NdIconObserverDelegate>)observer;

- (UIImage *)getCurrentUserIcon:(NSUInteger)type  observer:(id<NdIconObserverDelegate>)observer;

- (UIImage *)getBoardIcon:(NSString*)strId boardType:(NSUInteger)boardType checksum:(NSString*)checksum
				photoType:(NSUInteger)type	observer:(id<NdIconObserverDelegate>)observer;

- (void)removeUserIconObserver:(NSString *)uin   photoType:(NSUInteger)type  observer:(id<NdIconObserverDelegate>)observer;
- (void)removeAppIconObserver:(NSString *)appID  observer:(id<NdIconObserverDelegate>)observer;
- (void)removeBoardIconObserver:(NSString *)strId  photoType:(NSUInteger)type boardType:(NSUInteger)boardType 
					   observer:(id<NdIconObserverDelegate>)observer;

- (BOOL)isLocalCacheDefaultIcon:(NSString *)uin;

+ (NdCPIconManager*)singleton;

+ (UIImage*)imageForIcon3rdSNS:(NSUInteger)iconType;
+ (UIImage*)imageForIcon3rdDefault:(NSUInteger)iconType;
+ (UIImage*)imageForAchievementLock;

//!!!: 服务下发用户信息（列表）中的checksum对应的类型约定
+ (int)userIconTypeForServerChecksum;

@end


