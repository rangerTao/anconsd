//
//  StrategyCacheInfo.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import <Foundation/Foundation.h>

@interface StrategyItem : NSObject
@property (nonatomic, retain) NSString *strategyName;
@property (nonatomic, retain) NSString *strategyUrl;
+ (StrategyItem *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)itemArrayFromDicArray:(NSArray *)dicArry;

@end
