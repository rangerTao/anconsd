//
//  GameClassificationInfo.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/12/13.
//
//

#import "GameCatagoryInfo.h"

@implementation GameCatagoryInfo
- (void)dealloc
{
    [self clearAllProperty];
    [super dealloc];
}

+ (GameCatagoryInfo *)catagoryInfoFromDictionary:(NSDictionary *)dict
{
    GameCatagoryInfo *catagoryInfo = [[GameCatagoryInfo new] autorelease];
    
    catagoryInfo.catagoryId = [[dict objectForKey:@"CatagoryId"] intValue];
    catagoryInfo.catagoryName = [dict objectForKey:@"CatagoryName"];
    catagoryInfo.iconUrl = [dict objectForKey:@"IconUrl"];
    NSArray *array =[dict objectForKey:@"TopAppList"];
    NSMutableArray *mutableArray = [NSMutableArray array];
    for (NSInteger index = 0; index < [array count]; index++) {
        [mutableArray addObject:[[array objectAtIndex:index] objectForKey:@"AppName"]];
    }
    catagoryInfo.topAppList = [NSArray arrayWithArray:mutableArray];

    return catagoryInfo;
}

@end
