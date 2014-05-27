//
//  TaskDetailController.h
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-14.
//  Copyright (c) 2012年 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "OptionProtocols.h"

typedef enum _Task_Type{
    TYPE_TASK_COMPLETED = 1,
    TYPE_TASK_CURRENT,
    TYPE_TASK_GETABLE,
}Task_Type;

@interface TaskDetailController : UIViewController <GetTaskDetailProtocol, FinishTaskProtocol, ClaimTaskProtocol>{
    IBOutlet UILabel *titleLabel;
    IBOutlet UILabel *expiredTitleLabel;
    IBOutlet UILabel *expiredTimeLabel;
    IBOutlet UILabel *incentiveLabel;
    IBOutlet UIScrollView *scrollView;
    IBOutlet UILabel *taskDetailTextView;
    IBOutlet UILabel *taskRequestLabel;
    IBOutlet UILabel *taskRequestTextView;
    IBOutlet UIButton  *taskButton;
}

@property (nonatomic, assign) NSInteger taskId;
@property (nonatomic, assign) Task_Type taskType;

- (IBAction)finishAction:(id)sender;

@end
