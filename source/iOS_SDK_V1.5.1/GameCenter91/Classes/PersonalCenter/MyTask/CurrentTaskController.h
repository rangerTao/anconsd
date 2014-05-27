//
//  CurrentTaskController.h
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-12.
//  Copyright 2012 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GcPageTable.h"
#import "RequestorAssistant.h"

@interface CurrentTaskController : UIViewController <GcPageTableDelegate, GetTaskListProtocol, FinishTaskProtocol>{ 
    GcPageTable*			pageTable;
}

@end
