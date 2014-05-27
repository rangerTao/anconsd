//
//  SoftIncrementUpdateCenter.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-11-7.
//
//

#import <Foundation/Foundation.h>
@interface SoftIncreUpdateModule : NSObject
+ (BOOL)currentDeviceSupportIncrementUpadate;//目前设备是否支持智能升级 //预留

//identifier 为软件标识符 appPath为旧版本软件路径
+ (BOOL)isFilelistMatchedForSoft:(NSString *)identifier path:(NSString *)appPath;
+ (NSString *)xdeltaIncrementPackageForSoft:(NSString *)identifier path:(NSString *)appPath;
+ (void)deleteIncrementTmpFileForSoft:(NSString *)identifier;

//相关文件存放路径
+ (NSString *)requiredFileListPackageNameForSoft:(NSString *)identifier;
+ (NSString *)requiredIncrementPackageNameForSoft:(NSString *)identifier;
+ (NSString *)getIncrementPackagePathForSoft:(NSString *)identifier;
@end
