//
//  RequestorAssistant.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-7.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "RequestorAssistant.h"
#import "GameCenterOperation.h"
#import "CommonOperation.h"

#import "GetActivityListOperation.h"
#import "GetMyActivityGiftListOperation.h"
#import "GetHotSearchListOperation.h"
#import "GetGameProjectListOperation.h"
#import "GetGameProjectDetailOperation.h"
#import "GetAppCatagoryListOperation.h"
#import "GetAppListOperation.h"
#import "SoftSuggestionOperation.h"
#import "GameSearchResultOperation.h"
#import "GetAppLatestVersionOperation.h"
#import "GetAppDownloadUrloperaion.h"
#import "GetAppDetailInfoOperation.h"
#import "GetAppDescriptionInfoOperaion.h"

#import "GetHomePageOperation.h"
#import "GetUserBasicInfomationOperation.h"
#import "GetRecommendedPointsOperation.h"
#import "GetAdvertisementListOperation.h"
#import "GetMyHotSpotsOperation.h"
#import "GetGamesFilteredListOperation.h"
#import "UserActivitiesAnalyzeOperation.h"

#import "GcPagination.h"

@interface RequestorAssistant()
@property (nonatomic, retain) NSMutableDictionary *requestor2Opertaion;
@property (nonatomic, retain) NSMutableDictionary *reference2Requestor;
@property (nonatomic, retain) NSMutableArray *requestorContainer;

@property (nonatomic, assign) NSUInteger nextRef;

- (NSNumber *)addRequesotr:(id)requestor opertaion:(id<OperationCommonProtocol>)operation;
- (void)removeOperation:(id<OperationCommonProtocol>)operation;
//- (void)removeRequestorIfUseless:(id)requestor;
@end

@implementation RequestorAssistant
SYNTHESIZE_SINGLETON_FOR_CLASS(RequestorAssistant)

@synthesize requestor2Opertaion, reference2Requestor;
@synthesize requestorContainer;
@synthesize nextRef;
#pragma mark -
#pragma mark base works
+ (void)prepare
{
    [self sharedInstance].nextRef = 1;
    [self sharedInstance].requestor2Opertaion = [NSMutableDictionary dictionary];
    [self sharedInstance].reference2Requestor = [NSMutableDictionary dictionary];
    [self sharedInstance].requestorContainer = [NSMutableArray array];
}

//- (void)removeRequestorIfUseless:(id)requestor
//{
//    if (requestor)
//    {
//        NSValue *value = [NSValue valueWithNonretainedObject:requestor];
//        NSMutableDictionary *dict = [self.requestor2Opertaion objectForKey:value];
//        if ([dict count] == 0)
//        {
//            [self.requestor2Opertaion removeObjectForKey:value];
//            [self.requestorContainer removeObject:requestor];
//        }
//    }
//}

- (NSNumber *)addRequesotr:(id)requestor opertaion:(id<OperationCommonProtocol>)operation
{
    if (operation == nil)
        return nil;
    if (requestor == nil)
        requestor = [NSNull null];
    
    NSNumber *number = [NSNumber numberWithUnsignedInt:self.nextRef++];
    [operation setReferenceNumber:number];
    NSValue *value = [NSValue valueWithNonretainedObject:requestor];
    NSMutableDictionary *operationDict = [self.requestor2Opertaion objectForKey:value];
    if (operationDict == nil)
    {
        operationDict = [NSMutableDictionary dictionary];            
        
        //custom class can not be a key of dict, unless it implement copy        
        //so we use nsvalue noretained instead
        [self.requestor2Opertaion setObject:operationDict forKey:value];      
        //however the requestor must be retain, so we use requestor container to hold the requestor
        //it may be ugly, anyone who has better idea can show up
        if ([self.requestorContainer containsObject:requestor] == NO)
        {
            [self.requestorContainer addObject:requestor];        
        }
    }
    [operationDict setObject:operation forKey:number];
    
    [self.reference2Requestor setObject:requestor forKey:number];
    return number;
}

- (void)cancelOperation:(NSNumber *)operationReference shouldCancel:(BOOL)shouldCancel
{
    id requestor = [self.reference2Requestor objectForKey:operationReference];
    if (requestor)
    {
        NSValue *value = [NSValue valueWithNonretainedObject:requestor];
        NSMutableDictionary *dict = [self.requestor2Opertaion objectForKey:value];
        id<OperationCommonProtocol> operation = [dict objectForKey:operationReference];
        if (operation)
        {
            if (shouldCancel)
                [operation cancelOperation];
            [dict removeObjectForKey:operationReference];
            [self.reference2Requestor removeObjectForKey:operationReference];
            
            if ([dict count] == 0)
            {
                [self.requestor2Opertaion removeObjectForKey:value];
                [self.requestorContainer removeObject:requestor];
            }
        }
    }
    
}

- (void)cancelOperation:(NSNumber *)operationReference
{
    [self cancelOperation:operationReference shouldCancel:YES];
}

- (void)cancelAllOperationOfRequestor:(id)requestor
{
    if (requestor == nil)
        return;
    
    NSValue *value = [NSValue valueWithNonretainedObject:requestor];
    NSMutableDictionary *dict = [self.requestor2Opertaion objectForKey:value];
    if (dict)
    {
        for (NSNumber *reference in dict)
        {
            id<OperationCommonProtocol> operation = [dict objectForKey:reference];
            if (operation)
            {
                [operation cancelOperation];
                [self.reference2Requestor removeObjectForKey:reference];
            }
        }
        [dict removeAllObjects];
    }
    [self.requestor2Opertaion removeObjectForKey:value];
    [self.requestorContainer removeObject:requestor];
}

- (void)removeOperation:(id<OperationCommonProtocol>)operation
{
    [self cancelOperation:[operation referenceNumber] shouldCancel:NO];
}

#pragma mark -
#pragma detail requests

+ (NSNumber *)sendOperation:(id<OperationCommonProtocol>)operation delegate:(id)delegate
{
    operation.operationDelegate = [self sharedInstance];
    int res = [operation operation];
    if (res >= 0)
    {
        return [[self sharedInstance] addRequesotr:delegate opertaion:operation];
    }
    return [NSNumber numberWithInt:res];
}

+ (NSNumber *)requestGetActivityList:(NSString *)identifier type:(int)type page:(GcPagination *)page keyword:(NSString *)keyword delegate:(id<GetActivityListProtocol>)delegate
{
    GetActivityListOperation *operation = [[GetActivityListOperation new] autorelease];
    operation.identifier = identifier;
    operation.activityType = type;
    operation.requestPage = page;
    operation.keyword = keyword;
    return [self sendOperation:operation delegate:delegate];
}

+ (NSNumber *)requestMyActivityGiftList:(GcPagination *)page delegate:(id<GetMyActivityGiftListProtocol>)delegate
{
    GetMyActivityGiftListOperation *operation = [[GetMyActivityGiftListOperation new] autorelease];
    operation.requestPage = page;
    return [self sendOperation:operation delegate:delegate];
}



+ (NSNumber *)requestSoftSuggestionList:(NSString *)aKeyword delegate:(id<GetSoftSuggestionProtocol>)delegate
{
    SoftSuggestionOperation *operation = [[SoftSuggestionOperation new] autorelease];
    operation.keyword = aKeyword;
    return [self sendOperation:operation delegate:delegate];
}
+ (NSNumber *)requestGameSearchResultList:(NSString *)akeyword delegate:(id<GetGameSearchResultProtocol>)delegate
{
    GameSearchResultOperation *operation = [[GameSearchResultOperation new] autorelease];
    operation.keyword = akeyword;
    return  [self sendOperation:operation delegate:delegate];
}


+ (NSNumber *)requestAppCatagoryList:(id<GetAppCatagoryListProtocol>)delegate
{
    GetAppCatagoryListOperation *operation = [[GetAppCatagoryListOperation new] autorelease];
    return [self sendOperation:operation delegate:delegate];
}

+ (NSNumber *)requestAppList:(GcPagination *)page catagoryId:(int)catagoryId sortType:(int)sortType delegate:(id<GetAppListProtocol>)delegate
{
    GetAppListOperation *operation = [[GetAppListOperation new] autorelease];
    operation.catagoryId = catagoryId;
    operation.sortType = sortType;
    operation.requestPage = page;
    return [self sendOperation:operation delegate:delegate];
}
+ (NSNumber *)requestHotSearchList:(id<GetHotSearchListProtocol>)delegate
{
    GetHotSearchListOperation *operation = [[GetHotSearchListOperation new] autorelease];
    return [self sendOperation:operation delegate:delegate];
}
+ (NSNumber *)requestAppLatestVersion:(NSArray *)applist delegate:(id<GetAppLastedVersionProtocol>)delegate
{
    GetAppLatestVersionOperation *operation = [[GetAppLatestVersionOperation new] autorelease];
    operation.installedApplist = applist;
    return [self sendOperation:operation delegate:delegate];
}
+ (NSNumber *)requestAppDownloadUrlList:(NSDictionary *)aDic delegate:(id<GetAppDownloadUrlProtocol>)delegate
{
    GetAppDownloadUrloperaion *operation = [[GetAppDownloadUrloperaion new] autorelease];
    operation.dic = aDic;
    return  [self sendOperation:operation delegate:delegate];
}
+ (NSNumber *)requestAppDetailViewInfo:(NSString *)identifier delegate:(id<GetAppDetailViewInfoProtocol>)delegate
{
    GetAppDetailInfoOperation *operation = [[GetAppDetailInfoOperation new] autorelease];
    operation.identifer = identifier;
    return [self sendOperation:operation delegate:delegate];
}
+ (NSNumber *)requestAppDesciptionInfo:(NSString *)aSoftIdentifier delegate:(id<GetAppDescriptionInfoProtocol>)delegate
{
    GetAppDescriptionInfoOperaion *operation = [[GetAppDescriptionInfoOperaion new] autorelease];
    operation.softIdentifier = aSoftIdentifier;
    return [self sendOperation:operation delegate:delegate];
}
+ (NSNumber *)requestGameProjectList:(id<GetGameProjectListProtocol>)delegate
{
    GetGameProjectListOperation *operation = [[GetGameProjectListOperation new] autorelease];
    return [self sendOperation:operation delegate:delegate];
}

+ (NSNumber *)requestGameProjectDetail:(int)gameProjectid delegate:(id<GetGameProjectDetailProtocol>)delegate
{
    GetGameProjectDetailOperation *operation = [[GetGameProjectDetailOperation new] autorelease];
    operation.gameProjectId = gameProjectid;
    return [self sendOperation:operation delegate:delegate];
}

+ (NSNumber *)requestAppIdentifier:(int)aAppid savedArr:(NSArray *)aSavedArr delegate:(id<GetAppIdentifierProtocol>)delegate
{
    GetAppIdentifierOperation *operation = [[GetAppIdentifierOperation new] autorelease];
    operation.appid = aAppid;
    operation.savedArr = aSavedArr;
    return [self sendOperation:operation delegate:delegate];
}

+ (NSNumber *)requestAppIdentifier:(int)aAppid complete:(IdentifierCallback)complete
{
    GetAppIdentifierOperation *operation = [[GetAppIdentifierOperation new] autorelease];
    operation.appid = aAppid;
    operation.completionHandler = complete;
    return [self sendOperation:operation delegate:nil];
}

+ (NSNumber *)requestHomePage:(NSArray *)identifiers myGameIdentifiers:(NSArray *)myGameIdentifiers delegate:(id<GetHomePageProtocol>)delegate
{
    GetHomePageOperation *operation = [[GetHomePageOperation new] autorelease];
    operation.identifiers = identifiers;
    operation.myGameIdentifiers = myGameIdentifiers;
    return [self sendOperation:operation delegate:delegate];
}
+ (NSNumber *)requestUserBasicInfomation:(id<GetUserBasicInfomationProtocol>)delegate
{
    GetUserBasicInfomationOperation *operation = [[GetUserBasicInfomationOperation new] autorelease];
    return [self sendOperation:operation delegate:delegate];
}
+ (NSNumber *)requestRecommendedPoint:(id<GetRecommendedPointProtocol>)delegate
{
    GetRecommendedPointsOperation *operation = [[GetRecommendedPointsOperation new] autorelease];
    return [self sendOperation:operation delegate:delegate];
}
+ (NSNumber *)requestAdsList:(NSString *)adsLastModified delegate:(id<GetAdvertisementListProtocol>)delegate
{
    GetAdvertisementListOperation *operation = [[GetAdvertisementListOperation new] autorelease];
    operation.adsLastModified = adsLastModified;
    return [self sendOperation:operation delegate:delegate];
}
+ (NSNumber *)requestMyHotSpots:(NSArray *)myGameIdentifiers delegate:(id<GetMyHotSpotsProtocol>)delegate
{
    GetMyHotSpotsOperation *operation = [[GetMyHotSpotsOperation new] autorelease];
    operation.myGameIdentifiers = myGameIdentifiers;
    return [self sendOperation:operation delegate:delegate];
}

+ (NSNumber *)requestGamesFilteredList:(NSArray *)identifiers delegate:(id<GetGamesFilteredProtocol>)delegate
{
    GetGamesFilteredListOperation *operation = [[GetGamesFilteredListOperation new] autorelease];
    operation.identifers = identifiers;
    return [self sendOperation:operation delegate:delegate];
}

+ (NSNumber *)requestUserActivitiesAnalyze:(NSInteger)f_id statType:(USER_ANALYTICS_TYPE)statType
{
    UserActivitiesAnalyzeOperation *operation = [[UserActivitiesAnalyzeOperation new] autorelease];
    operation.f_id = f_id;
    operation.statType = statType;
    return [self sendOperation:operation delegate:nil];
}

#pragma mark operation callbacks
- (void)serverOperationDidFinish:(id<OperationCommonProtocol>)operation error:(NSError *)error
{
    NSNumber *reference = operation.referenceNumber;
    id requestor = [self.reference2Requestor objectForKey:reference];
    if (requestor)
    {
        //the delagate method may call cancel operation
        //it'll cause operation being freed before we finish our work
        //so retain it to prevent this
        [operation retain];
        
        if ([requestor isKindOfClass:[NSNull class]] == NO)
        {
            [operation callProtocolMethodOnObject:requestor];
        }
        [self removeOperation:operation];
        
        //we should release it now
        [operation release];
    }
}
@end
