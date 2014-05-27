//
//  SuggestionSoftItem.h
//  GameCenter91
//
//  Created by  hiyo on 13-10-17.
//
//

#import <Foundation/Foundation.h>

@interface SuggestionSoftItem : NSObject

@property (nonatomic, assign) int softId;
@property (nonatomic, retain) NSString *softName;
@property (nonatomic, retain) NSString *imgUrl;

+ (SuggestionSoftItem *)itemFromDictionary:(NSDictionary *)dict;

@end
