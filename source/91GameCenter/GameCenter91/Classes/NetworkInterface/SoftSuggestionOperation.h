//
//  SoftSuggestionOperation.h
//  GameCenter91
//
//  Created by  hiyo on 13-10-17.
//
//

#import "CommonOperation.h"

@interface SoftSuggestionOperation : CommonOperation

@property (nonatomic, retain) NSString *keyword;

@property (nonatomic, retain) NSArray *suggestionList;
@end
