//
//  SoftItem.h
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-21.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KSDownloadItem.h"
#import "IncreUpdateInfo.h"

@interface SoftItem : NSObject<KSDownloadItem>
@property (nonatomic, assign) int f_id;
@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, retain) NSString *localVersion;
@property (nonatomic, retain) NSString *localShortVersion;
@property (nonatomic, retain) NSString *version;
@property (nonatomic, retain) NSString *shortVersion;
@property (nonatomic, retain) NSString *iconPath;
@property (nonatomic, retain) NSString *softName;
@property (nonatomic, retain) NSString *updateUrl;//全量升级地址
@property (nonatomic, retain) IncreUpdateInfo *increUpateInfo;//为空表示不能智能升级
@property (nonatomic, retain) NSString *increInstallPackagePath;//差分完后安装路径

@property (nonatomic, assign) BOOL isAutoContinueDownload;  //是否在wifi情况下继续自动下载，默认是


- (UIImage *)defaultIcon;
- (void)generateSaveName;
- (BOOL)fileExist;
- (NSString *)absoluteFilePath;
- (NSURL *)iconUrl;
- (BOOL)hasIcon;

- (float)downloadPercent;
- (long)localFileLength;


+ (SoftItem *)itemWithAppIdentifier:(NSString *)aIdentifier softName:(NSString *)softName;
@end
