//
//  GuideCacheInfo.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import <Foundation/Foundation.h>

@interface GuideItem : NSObject
@property (nonatomic, retain) NSString *guideName;
@property (nonatomic, retain) NSString *guideUrl;

+ (GuideItem *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)itemArrayFromDicArray:(NSArray *)dicArry;

@end

