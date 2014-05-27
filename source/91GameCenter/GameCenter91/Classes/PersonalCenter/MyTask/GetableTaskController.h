//
//  GetableTaskController.h
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-12.
//  Copyright 2012 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GcPageTable.h"
#import "RequestorAssistant.h"

@interface GetableTaskController : UIViewController <GcPageTableDelegate, GetTaskListProtocol, ClaimTaskProtocol>{ 
    GcPageTable*			pageTable;
}

@end
