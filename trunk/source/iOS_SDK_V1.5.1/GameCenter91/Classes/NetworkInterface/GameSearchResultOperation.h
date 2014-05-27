//
//  GameSearchResultOperation.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-18.
//
//

#import "CommonOperation.h"

@interface GameSearchResultOperation : CommonOperation
@property (nonatomic, retain) NSString *keyword;

@property (nonatomic, retain) NSArray *searchResultList;
@end
