//
//  GameSearchController.h
//  GameCenter91
//
//  Created by  hiyo on 12-9-6.
//  Copyright 2012 Nd. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GcPageTable.h"
#import "GameSearchBar.h"
#import "RequestorAssistant.h"


@interface GameSearchController : UIViewController <GcPageTableDelegate, GameSearchBarProtocol> {
    GcPageTable *table_result;
}
@property(nonatomic, retain) GcPageTable *table_result;

- (void)hideKeyboard;
- (void)doSearchWithKeyword:(NSString *)aKeyword;
@end
