//
//  HotSpotInfo.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import <Foundation/Foundation.h>

@interface HotSpotInfo : NSObject

@property (nonatomic, assign) int hotType;
@property (nonatomic, retain) NSString *imageUrl;
@property (nonatomic, retain) NSString *title;
@property (nonatomic, retain) NSString *content;
@property (nonatomic, retain) NSString *tagName;
@property (nonatomic, retain) NSString *targetAction;
@property (nonatomic, retain) NSString *targetActionUrl;

+ (HotSpotInfo *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)listFromDictionary:(NSDictionary *)dict;

@end
