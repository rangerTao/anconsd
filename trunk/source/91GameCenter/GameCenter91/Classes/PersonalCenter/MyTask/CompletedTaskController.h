//
//  CompletedTaskController.h
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-14.
//  Copyright 2012 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GcPageTable.h"
#import "OptionProtocols.h"

@interface CompletedTaskController : UIViewController <GcPageTableDelegate, GetTaskListProtocol>{ 
    GcPageTable*			pageTable;
}

@end
