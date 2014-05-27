//
//  HomePageInfo.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/28/13.
//
//

#import <Foundation/Foundation.h>

@interface HomePageInfo : NSObject

@property (nonatomic, retain) NSArray *hotList;
@property (nonatomic, retain) NSArray *myGames;
@property (nonatomic, retain) NSArray *appList;
@property (nonatomic, retain) NSArray *dayRecommendList;

+ (HomePageInfo *)itemFromDictionary:(NSDictionary *)dict;

+ (NSDictionary *)serializedDictionaryFromItem:(HomePageInfo *)item;

@end
