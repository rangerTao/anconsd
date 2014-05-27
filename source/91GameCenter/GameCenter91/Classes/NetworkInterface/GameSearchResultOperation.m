//
//  GameSearchResultOperation.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-18.
//
//

#import "GameSearchResultOperation.h"
#import "AppDescriptionInfo.h"
@implementation GameSearchResultOperation
- (id)init {
    self = [super init];
    if (self) {
        self.searchResultList = nil;
        self.requestUrl = @"http://ressearch.sj.91.com/service.ashx";
        self.usePost = NO;
        self.protocolMethod = @selector(operation:getGameSearchResultDidFinish:resultList:);
    }
    return self;
}

//- (NSString *)requestUrl
//{
//    NSString *firmversion = [[UIDevice currentDevice] systemVersion];
//    NSString *url = [NSString stringWithFormat:@"http://ressearch.sj.91.com/service.ashx?act=29&platform=1&keyword=%@&fw=%@", self.keyword, firmversion];
//    return url;
//}

- (NSDictionary *)paramDict
{
    NSString *firmversion = [[UIDevice currentDevice] systemVersion];
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                          [NSNumber numberWithInt:0], @"act",
                          @"1", @"platform",
                          self.keyword, @"keyword",
                          firmversion, @"fw",
                          [NSNumber numberWithInt:3200], @"proj",       //3200代表游戏中心
                          [NSNumber numberWithInt:32], @"bcid",         //32为游戏类别
                          nil];
    return dict;
}

- (void)generateResponse:(id)object
{
    NSDictionary *dict = (NSDictionary *)object;
    if ([[dict objectForKey:@"Code"] integerValue] != 0) {
        //error
        return;
    }
    
    NSArray *arr = [dict objectForKey:@"data"];
    int count = [arr count];
    NSMutableArray *items = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        NSDictionary *subDic = [arr objectAtIndex:i];
        AppDescriptionInfo *obj = [[AppDescriptionInfo new] autorelease];
        obj.f_id = [[subDic objectForKey:@"f_id"] intValue];
        obj.identifier = [subDic objectForKey:@"f_identifier"];
        obj.appIconUrl = [subDic objectForKey:@"f_imgsrc"];
        obj.appName = [subDic objectForKey:@"f_name"];
        obj.appScore = [[subDic objectForKey:@"f_intro"] intValue];
        obj.labelIcons = nil;
        obj.downloadNumber = [[subDic objectForKey:@"f_downnum"] intValue];
        obj.fileSize = [[subDic objectForKey:@"f_size"] intValue];
        obj.appVersionName = [subDic objectForKey:@"f_version"];
        if (obj)
            [items addObject:obj];
    }
    
    if ([items count] != 0)
    {
        self.searchResultList = [NSArray arrayWithArray:items];
    }
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.searchResultList), nil];
}

@end
