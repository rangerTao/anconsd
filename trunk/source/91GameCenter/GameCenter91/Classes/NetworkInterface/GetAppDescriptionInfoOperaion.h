//
//  GetAppDescriptionInfoOperaion.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-24.
//
//

#import "GameCenterOperation.h"
@class AppDescriptionInfo;
@interface GetAppDescriptionInfoOperaion : GameCenterOperation
@property (nonatomic, retain) NSString *softIdentifier;

@property (nonatomic, retain) AppDescriptionInfo *descriptionInfo;
@end
