//
//  TaskDetailController.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-14.
//  Copyright (c) 2012年 net dragon. All rights reserved.
//

#import "TaskDetailController.h"
#import "RequestorAssistant.h"
#import "TaskDetail.h"
#import "TaskList.h"
#import "MBProgressHUD.h"
#import "Notifications.h"
#import "UIViewController+Extent.h"
#import "CommUtility.h"
#import "UserData.h"
#import "ActivityAndTaskJumpBar.h"

@implementation TaskDetailController
@synthesize taskId, taskType;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

- (void)dealloc {
    [super dealloc];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.customTitle = @"任务详情";
    
    taskDetailTextView.text = @"";
    taskRequestTextView.text = @"";
    expiredTitleLabel.text = @"";
    taskButton.hidden = YES;
    
    NSNumber *ret = [RequestorAssistant requestTaskDetail:self.taskId delegate:self];
    if ([ret intValue] > 0) {
        [MBProgressHUD showHUDAddedTo:self.view animated:YES];  
    }
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

#pragma mark - 
- (float)calcOffsetOfLabel:(UILabel *)label
{
    CGSize totoalSize = [label.text sizeWithFont:label.font constrainedToSize:CGSizeMake(CGRectGetWidth(label.frame), 10000)];
    CGSize oneLineSize = [@" " sizeWithFont:label.font];
    return (totoalSize.height - oneLineSize.height);
}

- (void)growView:(UIView *)aView withHeight:(float)offset
{
    CGRect rect = aView.frame;
    rect.size.height += offset;
    aView.frame = rect;
}

- (void)updateTastDetailFrame
{
    float offsetDetail = [self calcOffsetOfLabel:taskDetailTextView];
    float offsetRequest = [self calcOffsetOfLabel:taskRequestTextView];
    [self growView:taskDetailTextView withHeight:offsetDetail];
    [self growView:taskRequestTextView withHeight:offsetRequest];
    taskRequestLabel.frame = CGRectOffset(taskRequestLabel.frame, 0, offsetDetail);
    taskRequestTextView.frame = CGRectOffset(taskRequestTextView.frame, 0, offsetDetail);
    taskButton.frame = CGRectOffset(taskButton.frame, 0, offsetDetail+offsetRequest);
    scrollView.contentSize = CGSizeMake(CGRectGetWidth(scrollView.frame), CGRectGetMaxY(taskButton.frame) + 20.0);
}

- (void)addJumpBar:(TaskDetail *)detail
{
    if (detail.targetType == 0)
        return;
    
    ActivityAndTaskJumpBar *jumpBar = nil;
    if (detail.targetType == 1) {
        AppBriefInfo *appInfo = [[UserData sharedInstance] appInfoForID:[detail.targetAction intValue]];

        int barType = (appInfo == nil) ? TYPE_INCOMPATIBLE_GAME : TYPE_ACTIVITY_TO_GAME;
        
        jumpBar = [[[ActivityAndTaskJumpBar alloc] initWithView:self.view jumpType:barType] autorelease];
        jumpBar.appId = [detail.targetAction intValue];
    }
    else if (detail.targetType == 2) {
        if ([detail.targetAction length] <= 0)
            return;

        jumpBar = [[[ActivityAndTaskJumpBar alloc] initWithView:self.view jumpType:TYPE_TASK_TO_ACTIVITY] autorelease];
        jumpBar.targetId = detail.targetId;
        jumpBar.targetAppId = detail.targetAppId;
        jumpBar.activityUrl = detail.targetAction;
        jumpBar.activityName = detail.targetName;
        jumpBar.activityDetail = detail.targetDesc;
    }
    
    CGRect rect = scrollView.frame;
    rect.size.height -= [jumpBar JumpBarHeight];
    scrollView.frame = rect;
    [self.view addSubview:jumpBar];
}

#pragma mark -
#pragma mark OptionProtocols delegate

- (void)operation:(GameCenterOperation *)operation getTaskDetailDidFinish:(NSError *)error taskDetail:(TaskDetail *)detail 
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    
    if (error == nil) {
        taskButton.hidden = NO;
        switch (detail.status) {
            case TASK_STATUS_EXPIRED:
                [taskButton setTitle:@"已到期" forState:UIControlStateNormal];
                taskButton.enabled = NO;
                break;
            case TASK_STATUS_AVAILABLE:
                [taskButton setTitle:@"接任务" forState:UIControlStateNormal];
                taskButton.tag = TASK_STATUS_AVAILABLE;
                break;
            case TASK_STATUS_ON_PROCESS:
                [taskButton setTitle:@"未完成" forState:UIControlStateNormal];
                taskButton.tag = TASK_STATUS_ON_PROCESS;
                break;
            case TASK_STATUS_FINISHED:
                [taskButton setTitle:@"领奖" forState:UIControlStateNormal];
                taskButton.tag = TASK_STATUS_FINISHED;
                break;
            case TASK_STATUS_REWARDED:
                [taskButton setTitle:@"已完成" forState:UIControlStateNormal];
                taskButton.enabled = NO;
                break;
            default:
                taskButton.hidden = YES;
                break;
        }
        
        //显示跳转条
        [self addJumpBar:detail];
        
        titleLabel.text = detail.taskName;
        if (self.taskType == TYPE_TASK_GETABLE) {
            expiredTitleLabel.text = @"接受任务期限:";
            expiredTimeLabel.text = ([detail.expireTime length] <= 0) ? @"不限时" : [CommUtility dateFromLastModified:detail.expireTime];
        }
        else if (self.taskType == TYPE_TASK_COMPLETED || self.taskType == TYPE_TASK_CURRENT) {
            expiredTitleLabel.text = @"完成任务期限:";
            expiredTimeLabel.text = ([detail.completeEndTime length] <= 0) ? @"不限时" : [CommUtility dateFromLastModified:detail.completeEndTime];
        }
        else {
            //从外部跳转进来，不知道任务类型
            expiredTitleLabel.text = @"到期时间:";
            expiredTimeLabel.text = ([detail.expireTime length] <= 0) ? @"不限时" : [CommUtility dateFromLastModified:detail.expireTime];
        }
        
        NSString *rewardType = (detail.rewardType == 1) ? @"积金" : @"积金";//@"xx";
        incentiveLabel.text = [NSString stringWithFormat:@"%d%@", detail.rewardNumber, rewardType];
        
        taskDetailTextView.text = detail.taskDiscription;
        taskRequestTextView.text = detail.requirement;
        
        [self updateTastDetailFrame];
    }
}

- (void)operation:(GameCenterOperation *)operation finishTaskDidFinish:(NSError *)error task:(TaskItem *)task {
    if (error != nil) {
        if ([error code] == -304002) {
            [MBProgressHUD showHintHUD:@"你还没有完成任务哦~" message:task.description hideAfter:DEFAULT_TIP_LAST_TIME];
        }
        else {
            [MBProgressHUD showHintHUD:@"" message:[error localizedDescription] hideAfter:DEFAULT_TIP_LAST_TIME];
        }
    }
    else {
        [MBProgressHUD showHintHUD:@"任务完成啦！" message:[NSString stringWithFormat:@"恭喜您成功获得%d积金奖励", task.rewardNumber] hideAfter:DEFAULT_TIP_LAST_TIME];
        [taskButton setTitle:@"已完成" forState:UIControlStateNormal];
        taskButton.enabled = NO;
        
        [[NSNotificationCenter defaultCenter] postNotificationName:kGC91CompleteTaskNotification object:nil];
    }
}

- (void)operation:(GameCenterOperation *)operation claimTaskDidFinish:(NSError *)error task:(TaskItem *)task {
    if (error != nil) {
        [MBProgressHUD showHintHUD:@"" message:[error localizedDescription] hideAfter:DEFAULT_TIP_LAST_TIME];
    }
    else {
        [MBProgressHUD showHintHUD:@"该任务已成为您的当前任务" message:@"请到“当前”的任务中查看和领取奖励" hideAfter:DEFAULT_TIP_LAST_TIME];
        [taskButton setTitle:@"未完成" forState:UIControlStateNormal];
        taskButton.tag = TASK_STATUS_ON_PROCESS;
        [[NSNotificationCenter defaultCenter] postNotificationName:kGC91ClaimTaskNotification object:nil];
    }
}

- (void)finishAction:(id)sender {
    UIButton *button = (UIButton*)sender;
    if (button.tag == TASK_STATUS_ON_PROCESS || button.tag == TASK_STATUS_FINISHED) {
        [RequestorAssistant requestFinishTask:self.taskId delegate:self];
    }
    else if (button.tag == TASK_STATUS_AVAILABLE) {
        [RequestorAssistant requestClaimTask:self.taskId delegate:self];
    }
}

@end
