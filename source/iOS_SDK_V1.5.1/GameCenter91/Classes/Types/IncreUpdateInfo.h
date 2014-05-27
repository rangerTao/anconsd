//
//  IncreUpdateInfo.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-11-11.
//
//

#import <Foundation/Foundation.h>

@interface IncreUpdateInfo : NSObject
@property (nonatomic, retain) NSString *updateUrl;//增量升级失败使用全量升级地址
@property (nonatomic, retain) NSString *increPackageUrl;
@property (nonatomic, assign) long increFileSize;
@property (nonatomic, retain) NSString *filelistPackageUrl;//filelist.ini清单下载地址

@property (nonatomic, assign) BOOL smartUpdateFailed;//智能升级失败
@property (nonatomic, retain) NSString *increInstallPackagePath;//差分完后安装路径
@property (nonatomic, assign) BOOL isFilelistPackage;   //标记此时下载的是增量包
@property (nonatomic, assign) BOOL isIncrePackage;      //标记此时下载的是ini文件
+ (IncreUpdateInfo *)increUpdateInfoFromDictionary:(NSDictionary *)dic;
@end
