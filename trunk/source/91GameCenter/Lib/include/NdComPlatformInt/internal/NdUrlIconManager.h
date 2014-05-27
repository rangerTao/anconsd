//
//  NdUrlIconManager.h
//  NdComPlatformInt
//
//  Created by xujianye on 11-3-16.
//  Copyright 2011 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NdIconManagerBase.h"


@interface NdUrlIconManager : NdIconManagerBase {
	NSMutableDictionary*	dicMd5ToUrl;
}

+ (NdUrlIconManager*)singleton;

- (void)clearAllCacheImage;

- (UIImage*)getUrlIcon:(NSString*)url  observer:(id)observer;
- (void)removeUrlIconObserver:(NSString*)url observer:(id)observer;


#pragma mark  override
- (id)downloadIconWithKey:(NSString*)key checksum:(NSString*)checksum iconType:(NSUInteger)iconType;


@end


#pragma mark  urlOperation
@interface NdUrlDownloadOperation : NSObject
{
	NSString*			strUrl;
	UIImage*			image;
	NSMutableData*		imageData;
	NSURLConnection*	connection;
	id					delegate;
	NSString*			strSel_downloadFinish;	//带一个参数,NdUrlDownloadOperation*
}

@property (nonatomic, assign) id					delegate;
@property (nonatomic, retain) NSString*				strSel_downloadFinish;
@property (nonatomic, retain) NSString*				strUrl;
@property (nonatomic, retain) UIImage*				image;

- (void)setIconUrl:(NSString*)strUrlParam;

@end