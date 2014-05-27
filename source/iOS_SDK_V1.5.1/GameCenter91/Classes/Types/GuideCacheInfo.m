//
//  GuideCacheInfo.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-23.
//
//

#import "GuideCacheInfo.h"

@implementation GuideItem
- (void)dealloc
{
    [self clearAllProperty];
    [super dealloc];
}
+ (GuideItem *)itemFromDictionary:(NSDictionary *)dict
{
    GuideItem *item = [[GuideItem new] autorelease];
    item.guideName = [dict objectForKey:@"GuideName"];
    item.guideUrl = [dict objectForKey:@"GuideUrl"];
    return item;
}
+ (NSArray *)itemArrayFromDicArray:(NSArray *)dicArry
{
    int count = [dicArry count];
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:count];
    for (NSDictionary *dict in dicArry) {
        GuideItem *item = [GuideItem itemFromDictionary:dict];
        if (item != nil) {
            [arr addObject:item];
        }
    }
    return arr;
    
}

@end
