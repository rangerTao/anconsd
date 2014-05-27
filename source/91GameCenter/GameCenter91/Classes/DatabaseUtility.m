//
//  DatabaseUtility.m
//  GameCenter91
//
//  Created by kensou on 12-9-21.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "DatabaseUtility.h"
#import "CommUtility.h"
#import <NdFMDatabase/NdFMResultSet.h>
#import "AdsInfoCache.h"
#import "SoftItem.h"

#import "HomePageInfo.h"
#import "MyGameInfo.h"
#import "AppInfo.h"

#define DB_NAME @"GameCenter91_v1.0.db"

#define UserDefaultTable        @"UserDefault"
#define AdsListItemTable        @"AdsListItem"

@interface DatabaseUtility()

+ (void)initUserDefaultTable;
+ (void)initAdsListItemTable;

+ (NSString *)dbTextForObj:(id)param;

+ (AdsBriefInfoList *)cachedAdsListWithSql:(NSString *)aSql;

@end

@implementation DatabaseUtility
+ (NSString *)defaultDBPath
{
    return [[CommUtility getDocumentPath] stringByAppendingPathComponent:DB_NAME];
}

+ (NdFMDatabase *)defaultDatabase
{
    return [NdFMDatabase databaseWithPath:[self defaultDBPath]];
}

+ (BOOL)tableExistsInDatabase:(NSString *)tableName
{
    if ([tableName length] == 0)
        return NO;
    NdFMDatabase *db = [self defaultDatabase];
    [db open];
    NSString *sql = [NSString stringWithFormat:@"select count(*) from sqlite_master where type = 'table' and name = '%@'", tableName];
	NdFMResultSet *rs1 = [db executeQuery:sql];    
	[rs1 next];
    int result = [rs1 intForColumnIndex:0];
    [rs1 close];
    [db close];
    return (result == 0) ? NO : YES;
}

+ (BOOL)createTable:(NSString *)tableName withFieldNameAndTypes:(NSString *)fieldNameAndTypeStrings
{
    if ([fieldNameAndTypeStrings length] == 0)
        return NO;
    NdFMDatabase *db = [self defaultDatabase];
    [db open];
    NSString *sql = [NSString stringWithFormat:@"create table %@ (%@)", tableName, fieldNameAndTypeStrings];
	BOOL result = [db executeUpdate:sql];    
    [db close];
    return result;
}

+ (void)clearTable:(NSString *)tableName
{
    if ([tableName length] == 0)
        return;
    
    NdFMDatabase *db = [self defaultDatabase];
    [db open];
    NSString *sql = [NSString stringWithFormat:@"delete from %@", tableName];
    [db executeUpdate:sql];
    [db close];
}


+ (void)prepare
{
    [self dateBaseVersionCheckup];
    
    [self initUserDefaultTable];    
    [self initAdsListItemTable];
}

+ (void)dateBaseVersionCheckup
{
    if ([[NSUserDefaults standardUserDefaults] boolForKey:@"VersionHadCheckup"])
        return;
    
    BOOL (^doDropTable)(NdFMDatabase*, NSString*, NSString*) =^(NdFMDatabase *datebase, NSString *tableName, NSString *patternStr)
    {
        NdFMResultSet *columnNamesSet = [datebase executeQuery:[NSString stringWithFormat:@"PRAGMA table_info(%@)", tableName]];
        NSMutableArray* columnNames = [[[NSMutableArray alloc] init] autorelease];
        while ([columnNamesSet next]) {
            [columnNames addObject:[columnNamesSet stringForColumn:@"name"]];
        }
        [columnNamesSet close];
        
        NSSet *currentColumnSet = [NSSet setWithArray:columnNames];
        NSSet *patternColumnSet = [NSSet setWithArray:[patternStr componentsSeparatedByString:@","]];
        
        //检查表结构是否改变，如果改变则删除表
        if (![currentColumnSet isEqualToSet:patternColumnSet]) {
            [datebase executeUpdate:[NSString stringWithFormat:@"drop table %@", tableName]];
            return YES;
        }
        return NO;
    };
    
    
    NdFMDatabase *db = [self defaultDatabase];
    [db open];
    
    //数据库表结构检查，这个匹配字符串不能有空格
    NSString *patternStr = nil;
    //checkup AppListItemTable
    if ([self tableExistsInDatabase:AdsListItemTable]) {
        patternStr = @"imageUrl,actionParam,actionType,areaGroup,areaSize";
        
        if(doDropTable(db, AdsListItemTable, patternStr)) {
            [self recordAdsListLastModifyTime:nil];
        }
    }

    
    [db close];
    
    [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"VersionHadCheckup"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

+ (void)initUserDefaultTable
{
    if ([self tableExistsInDatabase:UserDefaultTable] == NO)
    {
        [self createTable:UserDefaultTable withFieldNameAndTypes:@"key text, value text"];
    }
}

+ (void)initAdsListItemTable
{
    if ([self tableExistsInDatabase:AdsListItemTable] == NO)
    {
        [self createTable:AdsListItemTable withFieldNameAndTypes:@"imageUrl text, actionParam text, actionType integer, areaGroup integer, areaSize text"];
    }
}

+ (NSString *)dbTextForObj:(id)param
{
    NSString *output = @"";
    if ([param isKindOfClass:[NSArray class]])
    {
        output = [param componentsJoinedByString:@","];
    }
    else if (param != nil)
    {
        output = [param description];
    }

    output = [output stringByReplacingOccurrencesOfString:@"'" withString:@"''"];
    
    return output;
}

#pragma mark -
#pragma mark UserDefault表读写

#define AdsListLastModifyTime               @"AdsListLastModifyTime"

+ (NSString *)readUserDefaultValueForKey:(NSString *)key
{
    if ([key length] == 0)
        return nil;
    
    NdFMDatabase *db = [self defaultDatabase];
    [db open];
    NSString *format = [NSString stringWithFormat:@"select value from %@ where key = '%@'", UserDefaultTable, key];
    NdFMResultSet *rs = [db executeQuery:format];
    NSString *value = nil;
    if ([rs next])
    {
        value = [rs stringForColumnIndex:0];
    }
    [rs close];
    [db close];
    return value;
}

+ (NSString *)cachedAdsListLastModifyTime
{
    return [self readUserDefaultValueForKey:AdsListLastModifyTime];
}

+ (void)writeUserDefaultValue:(NSString *)value forKey:(NSString *)key
{
    if ([key length] == 0)
        return;
    NdFMDatabase *db = [self defaultDatabase];
    [db open];
	
    NSString *format = [NSString stringWithFormat:@"select value from %@ where key = '%@'", UserDefaultTable, key];
    NdFMResultSet *rs = [db executeQuery:format];
    [rs next];
    if ([rs.resultDict count] != 0)
    {
        if ([value length] != 0)
        {
            format = [NSString stringWithFormat:@"update %@ set value = '%@' where key = '%@'", UserDefaultTable, value, key];            
        }
        else
        {
            format = [NSString stringWithFormat:@"delete from %@ where key = '%@'", UserDefaultTable, key];
        }
    }
    else 
    {
        if ([value length] != 0)
            format = [NSString stringWithFormat:@"insert into %@ (key, value) values ('%@', '%@')", UserDefaultTable, key, value];
    }
    [rs close];
    
    [db executeUpdate:format];            
	[db close];
}

+ (void)recordAdsListLastModifyTime:(NSString *)time
{
    [self writeUserDefaultValue:time forKey:AdsListLastModifyTime];
}


#pragma mark -
#pragma mark AdsListItem读写
+ (AdsBriefInfoList *)cachedAdsList
{
    return [self cachedAdsListWithSql:nil];
}

+ (AdsBriefInfoList *)cachedAdsListWithSql:(NSString *)aSql
{
    AdsBriefInfoList *list = nil;
    NdFMDatabase *db = [self defaultDatabase];
    [db open];
    
    NSString *lastModifyDate = [self cachedAdsListLastModifyTime];
    if ([lastModifyDate length] != 0)
    {
        list = [[AdsBriefInfoList new] autorelease];
        list.lastModifyDate = lastModifyDate;
        
        NSString *sql = aSql?aSql:[NSString stringWithFormat:@"select * from %@", AdsListItemTable];
        NdFMResultSet *rs = [db executeQuery:sql];
        
        NSMutableArray *muArray = [NSMutableArray array];
        
        while ([rs next])
        {
            AdsBriefInfo *obj = [[AdsBriefInfo new] autorelease];
            obj.imageUrl = [rs stringForColumn:@"imageUrl"];
            obj.actionParam = [rs stringForColumn:@"actionParam"];
            obj.actionType = [rs intForColumn:@"actionType"];
            obj.areaGroup = [rs intForColumn:@"areaGroup"];
            obj.areaSize = CGSizeFromString([rs stringForColumn:@"areaSize"]);
            
            if (obj.imageUrl != 0)
            {
                [muArray addObject:obj];
            }
        }
        
        if ([muArray count] != 0)
        {
            list.adsList = muArray;
        }
        else
        {
            list = nil;
        }
        [rs close];
    }
    
    [db close];
    return list;
}

+ (NSArray *)cachedAdsForPosition:(int)position
{
    if (position < ADS_POS_GAME_ALL || position > ADS_POS_ACTIVITY_NOTICE) {
        return nil;
    }
    
    NSString *sql = [NSString stringWithFormat:@"select * from %@ where areaGroup = '%d'", AdsListItemTable, position];
    AdsBriefInfoList *list = [self cachedAdsListWithSql:sql];
    
    return list.adsList;
}

+ (void)recordAdsBriefInfo:(AdsBriefInfo *)info
{
    if (info == nil)
        return;
    
    NdFMDatabase *db = [self defaultDatabase];
    [db open];
    
    NSString *format = nil;
    //we do not query whether an ads exist, for we have cleard table before we start cache
    
    
    format = [NSString stringWithFormat:@"insert into %@ (imageUrl, actionParam, actionType, areaGroup, areaSize)"
                                        " values ('%@', '%@', '%d', '%d', '%@')", AdsListItemTable, [self dbTextForObj:info.imageUrl],
                                        [self dbTextForObj:info.actionParam], info.actionType, info.areaGroup,
                                        [self dbTextForObj:NSStringFromCGSize(info.areaSize)]];    
    [db executeUpdate:format];
    if ([db lastErrorCode] != 0)
        NSLog(@"sql error %@", [db lastErrorMessage]);
    
	[db close];        
}

+ (void)recordAdsList:(AdsBriefInfoList *)list
{
    if (list == nil || [list.adsList count] == 0)
        return;
    
    NSString *lastModify = [self cachedAdsListLastModifyTime];
    if ([lastModify isEqualToString:list.lastModifyDate])
        return;
    
    [self recordAdsListLastModifyTime:nil];
    [self clearTable:AdsListItemTable];
    for (AdsBriefInfo *info in list.adsList)
    {
        [self recordAdsBriefInfo:info];
    }
    [self recordAdsListLastModifyTime:list.lastModifyDate];
}


#pragma mark - home page related

#define k_userDef_home_page           @"GC_HomePage"
#define k_userDef_all_appIds          @"GC_AllAppIds"
#define k_userDef_my_gameIds          @"GC_MyGameIds"

+ (HomePageInfo*)cachedHomePageInfo
{
    NSDictionary *dict = [[NSUserDefaults standardUserDefaults] objectForKey:k_userDef_home_page];
    HomePageInfo *info = [HomePageInfo itemFromDictionary:dict];
    return info;
}
+ (NSArray *)cachedAllAppIdsList
{
    NSArray *arr = [[NSUserDefaults standardUserDefaults] objectForKey:k_userDef_all_appIds];
    return arr;
}
+ (NSArray *)cachedMyGameIdsList
{
    NSArray *arr = [[NSUserDefaults standardUserDefaults] objectForKey:k_userDef_my_gameIds];
    return arr;
}
+ (void)recordHomePageInfo:(HomePageInfo *)info
{
    NSDictionary *dic = [HomePageInfo serializedDictionaryFromItem:info];
    [[NSUserDefaults standardUserDefaults] setValue:dic forKey:k_userDef_home_page];
    [[NSUserDefaults standardUserDefaults] synchronize];
}
+ (void)recordAllAppIdsList:(NSArray *)identifiersArr
{
    [[NSUserDefaults standardUserDefaults] setValue:identifiersArr forKey:k_userDef_all_appIds];
    [[NSUserDefaults standardUserDefaults] synchronize];
}
+ (void)recordMyGameIdsList:(NSArray *)identifiersArr
{
    [[NSUserDefaults standardUserDefaults] setValue:identifiersArr forKey:k_userDef_my_gameIds];
    [[NSUserDefaults standardUserDefaults] synchronize];
}
+ (void)removeCacheByDeletedIdentifiers:(NSArray *)identifiersArr
{
    if ([identifiersArr count] <= 0) {
        return;
    }
    //homePageInfo
    HomePageInfo *homePageInfo = [self cachedHomePageInfo];
    NSMutableArray *myGamesArr = [NSMutableArray arrayWithArray:homePageInfo.myGames];
    NSMutableArray *allGamesArr = [NSMutableArray arrayWithArray:homePageInfo.appList];
    NSMutableArray *tmpTobeRemovedArr = [NSMutableArray array];
    for (MyGameInfo *info in myGamesArr) {
        if ([identifiersArr containsObject:info.identifier]) {
            [tmpTobeRemovedArr addObject:info];
            if ([tmpTobeRemovedArr count] >= [identifiersArr count]) {
                break;
            }
        }
    }
    [myGamesArr removeObjectsInArray:tmpTobeRemovedArr];
    [tmpTobeRemovedArr removeAllObjects];
    for (AppInfo *info in allGamesArr) {
        if ([identifiersArr containsObject:info.identifier]) {
            [tmpTobeRemovedArr addObject:info];
            if ([tmpTobeRemovedArr count] >= [identifiersArr count]) {
                break;
            }
        }
    }
    [allGamesArr removeObjectsInArray:tmpTobeRemovedArr];
    [tmpTobeRemovedArr removeAllObjects];
    homePageInfo.myGames = myGamesArr;
    homePageInfo.appList = allGamesArr;
    [self recordHomePageInfo:homePageInfo];
    //allAppIdsList
    NSMutableArray *allAppIdsList = [NSMutableArray arrayWithArray:[self cachedAllAppIdsList]];
    [allAppIdsList removeObjectsInArray:identifiersArr];
    [self recordAllAppIdsList:allAppIdsList];
    //myGameIdsList
    NSMutableArray *myGameIdsList = [NSMutableArray arrayWithArray:[self cachedMyGameIdsList]];
    [myGameIdsList removeObjectsInArray:identifiersArr];
    [self recordMyGameIdsList:myGameIdsList];
}

@end
