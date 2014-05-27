//
//  AppDownLoadInfo.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import <Foundation/Foundation.h>

@interface AppDownLoadInfo : NSObject
@property (nonatomic, assign) int f_id;
@property (nonatomic, retain) NSString *versionCode;
@property (nonatomic, retain) NSString *versionName;
@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, assign) long      packageSize;
@property (nonatomic, retain) NSString *downloadUrl;
+ (AppDownLoadInfo *)itemFromDictionary:(NSDictionary *)dict;
@end
