//
//  ForumCacheInfo.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import <Foundation/Foundation.h>

@interface ForumItem : NSObject
@property (nonatomic, retain) NSString *forumName;
@property (nonatomic, retain) NSString *forumUrl;
+ (ForumItem *)itemFromDictionary:(NSDictionary *)dict;
+ (NSArray *)itemArrayFromDicArray:(NSArray *)dicArry;

@end

