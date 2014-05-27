//
//  SuggestionSoftItem.m
//  GameCenter91
//
//  Created by  hiyo on 13-10-17.
//
//

#import "SuggestionSoftItem.h"

@implementation SuggestionSoftItem

@synthesize softId, softName, imgUrl;

- (void)dealloc {
    [self clearAllProperty];
    [super dealloc];
}

- (NSString *)description
{
    return [NSString stringWithFormat:@"\nSoftId:%d\nSoftName:%@\nImgUrl:%@\n",
            self.softId,self.softName,self.imgUrl];
}

+ (SuggestionSoftItem *)itemFromDictionary:(NSDictionary *)dict
{
    SuggestionSoftItem *item = [[SuggestionSoftItem new] autorelease];
    item.softId = [[dict objectForKey:@"id"] intValue];
    item.softName = [dict objectForKey:@"name"];
    item.imgUrl = [dict objectForKey:@"imgsrc"];
    return item;
}

@end
