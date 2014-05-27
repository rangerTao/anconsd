//
//  GetHomePageOperation.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "GameCenterOperation.h"

@class HomePageInfo;

@interface GetHomePageOperation : GameCenterOperation

@property (nonatomic, retain) NSArray *identifiers;
@property (nonatomic, retain) NSArray *myGameIdentifiers;

@property (nonatomic, retain) HomePageInfo *homePageInfo;



@end
