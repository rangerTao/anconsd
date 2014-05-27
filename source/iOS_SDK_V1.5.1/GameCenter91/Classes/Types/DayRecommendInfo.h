//
//  DayRecommendInfo.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/23/13.
//
//

#import <Foundation/Foundation.h>

@interface DayRecommendInfo : NSObject

@property (nonatomic, retain) NSString *imageUrl;
@property (nonatomic, retain) NSString *title;
@property (nonatomic, retain) NSString *summary;
@property (nonatomic, assign) int f_id;
@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, retain) NSString *appIconUrl;
@property (nonatomic, retain) NSString *labelIcons;

+ (DayRecommendInfo *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)listFromDictionary:(NSDictionary *)dict;

+ (NSArray *)serializedFromArr:(NSArray *)arr;

@end
