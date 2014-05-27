//
//  ProjectInfo.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "ProjectInfo.h"
#import "CommUtility.h"

@implementation ProjectInfo

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

+ (ProjectInfo *)itemFromDictionary:(NSDictionary *)dict
{
    ProjectInfo *info = [[ProjectInfo new] autorelease];
    info.position = [[dict objectForKey:@"Position"] intValue];
    info.imageUrl = [dict objectForKey:@"ImageUrl"];
    info.mainTitle = [dict objectForKey:@"MainTitle"];
    info.subTitle = [dict objectForKey:@"SubTitle"];
    info.projectType = [[dict objectForKey:@"ProjectType"] intValue];
    info.targetType = [[dict objectForKey:@"TargetType"] intValue];
    info.targetAction = [dict objectForKey:@"TargetAction"];
    info.labelList = [CommUtility packRecommendIconsStr:[dict objectForKey:@"LabelList"]];
    info.targetActionUrl = [dict objectForKey:@"TargetActionUrl"];
    info.bgColor = [dict objectForKey:@"BGColor"];
    return info;
}

+ (NSArray *)listFromDictionary:(NSDictionary *)dict
{
    NSArray *arr = [dict objectForKey:@"ProjectList"];
    int count = [arr count];
    NSMutableArray *items = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        ProjectInfo *obj = [ProjectInfo itemFromDictionary:[arr objectAtIndex:i]];
        if (obj)
            [items addObject:obj];
    }
    
    if ([items count] != 0)
    {
        return [NSArray arrayWithArray:items];
    }
    else
    {
        return nil;
    }
}
@end
