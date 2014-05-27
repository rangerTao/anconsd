//
//  MyGameInfo.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/23/13.
//
//

#import <Foundation/Foundation.h>

@interface MyGameInfo : NSObject <NSCopying>

@property (nonatomic, assign) int f_id;
@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, retain) NSString *appIconUrl;
@property (nonatomic, retain) NSString *appName;
@property (nonatomic, assign) int suggestType;
@property (nonatomic, retain) NSString *strategyUrl;
@property (nonatomic, retain) NSString *forumUrl;
@property (nonatomic, retain) NSArray *activeList;

+ (MyGameInfo *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)listFromDictionary:(NSDictionary *)dict;

+ (NSArray *)serializedFromArr:(NSArray *)arr;

@end
