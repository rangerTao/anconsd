//
//  DatabaseUtility.h
//  GameCenter91
//
//  Created by kensou on 12-9-21.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <NdFMDatabase/NdFMDatabase.h>

@class AdsBriefInfoList;
@class AdsBriefInfo;
@class HomePageInfo;

@interface DatabaseUtility : NSObject
+ (NdFMDatabase *)defaultDatabase;
+ (BOOL)tableExistsInDatabase:(NSString *)tableName;
//create table with field type and name string
//field type and name string example:  @"name1 type1, name2 type2"
+ (BOOL)createTable:(NSString *)tableName withFieldNameAndTypes:(NSString *)fieldNameAndTypeStrings;
+ (void)clearTable:(NSString *)tableName;
   
+ (void)prepare;


+ (NSString *)cachedAdsListLastModifyTime;
+ (void)recordAdsListLastModifyTime:(NSString *)time;

+ (AdsBriefInfoList *)cachedAdsList;
+ (NSArray *)cachedAdsForPosition:(int)position;
+ (void)recordAdsList:(AdsBriefInfoList *)list;

#pragma mark - home page related
+ (HomePageInfo*)cachedHomePageInfo;
+ (NSArray *)cachedAllAppIdsList;
+ (NSArray *)cachedMyGameIdsList;

+ (void)recordHomePageInfo:(HomePageInfo *)info;    //保存首页
+ (void)recordAllAppIdsList:(NSArray *)identifiersArr;  //保存所有应用的identifier
+ (void)recordMyGameIdsList:(NSArray *)identifiersArr;  //保存我关注的游戏的identifier
+ (void)removeCacheByDeletedIdentifiers:(NSArray *)identifiersArr;  //删除指定identifiers的应用
@end
