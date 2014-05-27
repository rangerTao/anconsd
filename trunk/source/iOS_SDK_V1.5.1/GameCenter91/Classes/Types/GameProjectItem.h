//
//  GameProjectItem.h
//  GameCenter91
//
//  Created by Sun pinqun on 13-1-23.
//  Copyright (c) 2013年 net dragon. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface GameProjectItem : NSObject
@property (nonatomic, assign) int gameProjectId;
@property (nonatomic, retain) NSString *title;
@property (nonatomic, retain) NSString *introduce;
@property (nonatomic, assign) int gameCount;

+ (GameProjectItem *)itemFromDictionary:(NSDictionary *)dict;
@end
