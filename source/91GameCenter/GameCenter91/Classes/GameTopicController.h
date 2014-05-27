//
//  GameTopicController.h
//  GameCenter91
//
//  Created by hiyo on 13-1-30.
//  Copyright (c) 2013年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GcPageTable.h"
#import "RequestorAssistant.h"

@interface GameTopicController : UIViewController<GcPageTableDelegate, GetGameProjectDetailProtocol> {
    GcPageTable *topic_table;
    int topicId;
}
@property (nonatomic, retain) GcPageTable *topic_table;
@property (nonatomic, assign) int topicId;

@end
