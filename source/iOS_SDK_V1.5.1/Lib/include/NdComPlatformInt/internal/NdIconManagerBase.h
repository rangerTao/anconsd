//
//  NdIconManagerBase.h
//  NdComPlatformInt
//
//  Created by xujianye on 11-3-16.
//  Copyright 2011 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NdIconObservers.h"

@interface NdIconManagerBase : NSObject {
	NSString*			strCachePath;
	NSUInteger			maxIconType;
	NSMutableArray*		arrTypeOfDefaultIcon;
	NSMutableArray*		arrTypeOfDefaultIconPath;
	NSMutableArray*		arrTypeOfChecksumDic;			//dic: strKey  -- checksum
	NSMutableArray*		arrTypeOfIconObserversDic;		//dic: strKey  -- IconObservers
	NSMutableArray*		arrTypeOfServerOperationDic;	//dic: strKey  -- serverOperation
	NSMutableDictionary*	dicKeyType2IconPathObservers;
}

#pragma mark configure method
- (void)setMaxIconType:(NSUInteger)maxType;
- (void)setCachePathComponent:(NSString*)pathComponent;
- (void)loadCacheFile:(NSString*)defaultIconPre;
- (void)loadCacheFile:(NSString*)defaultIconPre  commonImage:(NSString*)comImage;

#pragma mark need to override
/* return serverOperation object, if return nil, means failure */
- (id)downloadIconWithKey:(NSString*)key checksum:(NSString*)checksum iconType:(NSUInteger)iconType;

#pragma mark after download finished
- (void)didDownloadIconWithKey:(NSString*)key  strBase64:(NSString*)strBase64 checksum:(NSString*)checksum iconType:(NSUInteger)iconType errorCode:(NSError *)error;
- (void)didDownloadIconWithKey:(NSString*)key  image:(UIImage*)image checksum:(NSString*)checksum iconType:(NSUInteger)iconType errorCode:(NSError *)error;


#pragma mark tool method
- (NSString*)rootPathOfIcon;
- (NSString*)iconFileFullNameWithKey:(NSString*)key  iconType:(NSUInteger)iconType;
- (NSString*)checksumFileFullNameWithIconType:(NSUInteger)iconType;
- (NSString*)checksumWithKey:(NSString*)key iconType:(NSUInteger)iconType;

- (BOOL)isDefaultIconChecksum:(NSString*)checksum;
- (BOOL)isIconExistWithKey:(NSString*)key iconType:(NSUInteger)iconType;

- (UIImage*)defaultIconWithType:(NSUInteger)iconType;
- (UIImage*)iconCacheWithKey:(NSString*)strKey iconType:(NSUInteger)iconType  cacheExist:(BOOL*)pExist;

- (void)updateIconWithKey:(NSString*)strKey  iconImg:(UIImage*)iconImg 
				 checksum:(NSString*)checksum iconType:(NSUInteger)iconType  errorCode:(NSError *)error;

- (void)iconPathWithKey:(NSString*)strKey  checksum:(NSString*)checksumNew   iconType:(NSUInteger)iconType observer:(id<NdIconPathObserverDelegate>)observer;
- (UIImage*)iconWithKey:(NSString*)strKey  checksum:(NSString*)checksum  iconType:(NSUInteger)iconType observer:(id<NdIconObserverDelegate>)observer;
- (void)removeIconObserverWithKey:(NSString*)strKey iconType:(NSUInteger)iconType  observer:(id<NdIconObserverDelegate>)observer;

- (void)cancelAllServerOperation;
- (void)cancelServerOperationWithKey:(NSString*)strKey iconType:(NSUInteger)iconType;
- (void)deleteCacheIconWithkey:(NSString*)key;

#pragma mark -
#pragma mark static method

- (NSDate*)getLastAccessTimeOfFile:(NSString*)file;
- (void)deleteFile:(NSString*)fileFullName;
- (BOOL)isFileExist:(NSString*)fileFullName;
- (void)saveDic:(NSDictionary*)dic path:(NSString*)filePath;
- (void)removeEmptyStringOfDicItem:(NSMutableDictionary*)dic;
- (UIImage*)imageLimitSize:(UIImage*)imgSrc  maxWidth:(CGFloat)width  maxHeight:(CGFloat)height;
- (UIImage*)imageLimitToSize_Stretch:(UIImage*)imgSrc  maxWidth:(CGFloat)width  maxHeight:(CGFloat)height;
- (UIImage*)imageScaleToSize:(UIImage*)img size:(CGSize)size;

+ (UIImage*)imageLimitSize:(UIImage*)imgSrc  maxWidth:(CGFloat)width  maxHeight:(CGFloat)height;
+ (UIImage*)imageLimitToSize_Stretch:(UIImage*)imgSrc  maxWidth:(CGFloat)width  maxHeight:(CGFloat)height;
+ (UIImage*)imageScaleToSize:(UIImage*)img size:(CGSize)size;

- (UIImage*)downloadImageFromUrl:(NSString*)strUrl;
- (UIImage*)imageFromBase64:(NSString*)base64;
- (void)saveImg:(UIImage*)icon  path:(NSString*)fileFullName;

@end
