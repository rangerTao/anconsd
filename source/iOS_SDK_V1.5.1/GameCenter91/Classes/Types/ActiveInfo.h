//
//  ActivieInfo.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/23/13.
//
//

#import <Foundation/Foundation.h>

//首页中的活动
@interface ActiveInfo : NSObject

@property (nonatomic, assign) int activityId;
@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, retain) NSString *title;
@property (nonatomic, assign) int activityType;
@property (nonatomic, retain) NSString *contentUrl;

+ (ActiveInfo *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)listFromDictionary:(NSDictionary *)dict;

+ (NSArray *)serializedFromArr:(NSArray *)arr;

@end
