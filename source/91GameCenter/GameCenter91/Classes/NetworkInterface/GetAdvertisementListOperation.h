//
//  GetAdvertisementOperation.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "GameCenterOperation.h"

@class AdsBriefInfoList;
@interface GetAdvertisementListOperation : GameCenterOperation

@property (nonatomic, retain) NSString *adsLastModified;

@property (nonatomic, retain) AdsBriefInfoList *adsList;

@end
