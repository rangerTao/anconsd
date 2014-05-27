//
//  GameClassificationInfo.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/12/13.
//
//

#import <Foundation/Foundation.h>

@interface GameCatagoryInfo : NSObject

@property (nonatomic, assign) int catagoryId;
@property (nonatomic, retain) NSString *catagoryName;
@property (nonatomic, retain) NSString *iconUrl;
@property (nonatomic, retain) NSArray *topAppList;

+ (GameCatagoryInfo *)catagoryInfoFromDictionary:(NSDictionary *)dict;

@end
