//
//  IconObservers.h
//  NdComPlatformInt
//
//  Created by xujianye on 11-3-16.
//  Copyright 2011 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

//接口13定义
#define		ICON_TYPE_TINY_SIZE			{16 , 16}
#define		ICON_TYPE_SMALL_SIZE		{48 , 48}
#define		ICON_TYPE_MIDDLE_SIZE		{120,120}
#define		ICON_TYPE_BIG_SIZE			{200,200}


#pragma mark 图像观察者代理
@protocol NdIconObserverDelegate
@required
- (void)updateIcon:(UIImage*)image  checkSum:(NSString*)checkSum  iconType:(NSUInteger)iconType  errorCode:(NSError *)error;
@end

@protocol NdIconPathObserverDelegate
@required
- (void)updateIconPath:(NSString*)iconPath  checkSum:(NSString*)checkSum  iconType:(NSUInteger)iconType  errorCode:(NSError *)error;
@end



#pragma mark 同一图像的所有观察者
@interface NdIconObservers : NSObject
{
	UIImage*			imgIcon;
	NSMutableArray*		arrObserver;
}

@property (nonatomic, readonly) UIImage*	imgIcon;

- (void)updateObserversIcon:(UIImage *)image  checkSum:(NSString*)checkSum  iconType:(NSUInteger)iconType errorCode:(NSError *)error;
- (NSUInteger)addIconObserver:(id<NdIconObserverDelegate>) IconObserver;		//返回当前观察者数目
- (NSUInteger)removeIconObserver:(id<NdIconObserverDelegate>) IconObserver;	//返回当前观察者数目

@end

