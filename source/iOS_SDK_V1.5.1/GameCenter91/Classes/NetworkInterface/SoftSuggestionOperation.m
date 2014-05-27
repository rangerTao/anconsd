//
//  SoftSuggestionOperation.m
//  GameCenter91
//
//  Created by  hiyo on 13-10-17.
//
//

#import "SoftSuggestionOperation.h"
#import "SuggestionSoftItem.h"

@implementation SoftSuggestionOperation
@synthesize keyword, suggestionList;

- (id)init {
    self = [super init];
    if (self) {
        self.suggestionList = nil;
        
        self.usePost = NO;
        self.requestUrl = @"http://suggestion.sj.91.com/service.ashx";
        self.protocolMethod = @selector(operation:getSoftSuggestionDidFinish:suggestionList:);
    }
    return self;
}

- (NSDictionary *)paramDict
{
    NSString *firmversion = [[UIDevice currentDevice] systemVersion];
    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                            [NSNumber numberWithInt:29], @"act",
                            @"iphone", @"platform",
                            self.keyword, @"keyword",
                            firmversion, @"fw",
                            [NSNumber numberWithInt:0], @"pad",
                            [NSNumber numberWithInt:20], @"size",
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
    
    NSArray *arr = [dict objectForKey:@"words"];
    int count = [arr count];
    NSMutableArray *items = [NSMutableArray arrayWithCapacity:count];
    for (int i = 0; i < count; i++)
    {
        SuggestionSoftItem *obj = [SuggestionSoftItem itemFromDictionary:[arr objectAtIndex:i]];
        if (obj)
            [items addObject:obj];
    }
    
    if ([items count] != 0)
    {
        self.suggestionList = [NSArray arrayWithArray:items];
    }
}

- (NSInvocation *)callbackInvocationOnObject:(id)object
{
    return [self invocationOnTarget:object action:self.protocolMethod withArguments:INVOC_PARAM(self.suggestionList), nil];
}

@end
