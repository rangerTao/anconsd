//
//  AppDescriptionInfo.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-24.
//
//

#import <Foundation/Foundation.h>

@interface AppDescriptionInfo : NSObject
@property (nonatomic, assign) int f_id;
@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, retain) NSString *appIconUrl;
@property (nonatomic, retain) NSString *appName;
@property (nonatomic, assign) int appScore;
@property (nonatomic, retain) NSString *labelIcons;
@property (nonatomic, assign) int downloadNumber;
@property (nonatomic, assign) long fileSize;
@property (nonatomic, retain) NSString *appVersionName;
@property (nonatomic, assign) int isChinese;
+(AppDescriptionInfo *)itemFromDictionary:(NSDictionary *)dict;
@end
