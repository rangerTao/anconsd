//
//  ForumCacheInfo.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "ForumCacheInfo.h"

@implementation ForumItem
- (void)dealloc
{
    [self clearAllProperty];
    [super dealloc];
}
+ (ForumItem *)itemFromDictionary:(NSDictionary *)dict
{
    ForumItem *item = [[ForumItem new] autorelease];
    item.forumName = [dict objectForKey:@"ForumName"];
    item.forumUrl = [dict objectForKey:@"ForumUrl"];
    return item;
}
+ (NSArray *)itemArrayFromDicArray:(NSArray *)dicArry
{
    int count = [dicArry count];
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:count];
    for (NSDictionary *dict in dicArry) {
        ForumItem *item = [ForumItem itemFromDictionary:dict];
        if (item != nil) {
            [arr addObject:item];
        }
    }
    return arr;

}
@end

