//
//  AppDetailViewInfo.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import <Foundation/Foundation.h>

@interface AppDetailViewInfo : NSObject
@property (nonatomic, assign) int f_id;
@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, retain) NSString *appIconUrl;
@property (nonatomic, retain) NSString *appName;
@property (nonatomic, assign) int appScore;
@property (nonatomic, assign) int isChinese;
@property (nonatomic, assign) int isGreen;
@property (nonatomic, assign) int downloadNumber;
@property (nonatomic, assign) long fileSize;
@property (nonatomic, retain) NSString *appVersionName;
@property (nonatomic, retain) NSString *appDescription;
@property (nonatomic, retain) NSArray  *softImages;
@property (nonatomic, retain) NSString *strategyUrl;
@property (nonatomic, retain) NSString *forumUrl;
@property (nonatomic, retain) NSArray *activityList;
@property (nonatomic, retain) NSArray *guideList;
@property (nonatomic, retain) NSArray *strategyList;
@property (nonatomic, retain) NSArray *forumList;
+ (AppDetailViewInfo *)itemFromDictionary:(NSDictionary *)dict;
@end