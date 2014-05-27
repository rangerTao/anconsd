//
//  CurrentTaskController.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-12.
//  Copyright 2012 net dragon. All rights reserved.
//

#import "CurrentTaskController.h"
#import "UITableViewCell+Addition.h"
#import "UIViewController+Extent.h"
#import "UIViewController+Extent.h"
#import "UnCompletedTaskTitleCell.h"
#import "UnCompletedTaskItemCell.h"
#import "TaskDetailController.h"
#import "TaskList.h"
#import "MBProgressHUD.h"
#import "Notifications.h"
#import "TabContainerController.h"
#import "CommUtility.h"

@implementation CurrentTaskController

#pragma mark -
#pragma mark Initialization


- (id)init {
    if ((self = [super init])) {
        self.title = @"当前";
    }
    return self;
}

#pragma mark -
#pragma mark View lifecycle


- (void)viewDidLoad {
    [super viewDidLoad];

    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:[CommUtility isTabbarHide] otherExcludeHeight:[TabContainerController defaultSegmentHeight]];
	pageTable = [[[GcPageTable alloc] initWithFrame:CGRectMake(0, 0, 320.0, height) style:UITableViewStyleGrouped] autorelease];
	pageTable.strMoreTip = @"更多记录⋯⋯";
    pageTable.strNoDataTip = @"还没有接受任务？out啦！积金在向您招手，速速领取新任务吧。";
	pageTable.pageTableDelegate = self;
	pageTable.customCellCache = [UnCompletedTaskItemCell loadFromNib];
    pageTable.titleCellCache = [UnCompletedTaskTitleCell loadFromNib];
    pageTable.titleRowHeight = [UnCompletedTaskTitleCell cellHeight];
	[pageTable setNdPageTableTransparent];
	[pageTable enableEgoRefreshTableHeaderView];
	[self.view addSubview:pageTable];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(taskChanged:) name:kGC91ClaimTaskNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(taskChanged:) name:kGC91CompleteTaskNotification object:nil];
}

- (int)requestGetTaskList {
    NSNumber *ret = [RequestorAssistant requestGetTaskList:2 delegate:self];
    return [ret intValue];
}

#pragma mark -
#pragma mark NdPageTableDelegate
- (void)GcPageTable:(GcPageTable*)table customCell:(UITableViewCell*)cell customData:(id)data
{
	UnCompletedTaskItemCell* taskCell = (UnCompletedTaskItemCell*)cell;
	if(data != nil) {
        TaskItem* info = (TaskItem*)data;
        [taskCell setInfo:info.taskName detail:info.summary score:info.rewardNumber];
        BOOL taskStatus = (info.status == TASK_STATUS_EXPIRED) ? NO : YES;
        NSString *title = (info.status == TASK_STATUS_EXPIRED) ? @"已到期" : (info.status == TASK_STATUS_ON_PROCESS) ? @"未完成": @"领奖";
        [taskCell setButton:info.taskId title:title status:taskStatus target:self action:@selector(doCompleteTask:)];
	}
}

- (CGFloat)	GcPageTable:(GcPageTable*)table heightForCustomCell:(UITableViewCell*)cellCache  customData:(id)data
{
	return [UnCompletedTaskItemCell cellHeight];
}

- (int)GcPageTable:(GcPageTable*)table downloadPageIndex:(NSInteger)pageIdx  pageSize:(NSInteger)pageSize
{
    return [self requestGetTaskList];
}

- (UITableViewCell*)GcPageTable:(GcPageTable*)table cellCopyFromCacheCell:(UITableViewCell*)cellCache
{
	UnCompletedTaskItemCell* cellNew = [UnCompletedTaskItemCell loadFromNib];
	return cellNew;
}

- (void) GcPageTable:(GcPageTable*)table didSelectRowWithData:(id)data 
{
    TaskDetailController *detailViewController = [[TaskDetailController alloc] init];
    TaskItem* info = (TaskItem*)data;
    detailViewController.taskId = info.taskId;
    detailViewController.taskType = TYPE_TASK_CURRENT;
    [self.parentContainerController.navigationController pushViewController:detailViewController animated:YES];
    [detailViewController release];
}


#pragma mark -
#pragma mark Memory management

- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Relinquish ownership any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
    // Relinquish ownership of anything that can be recreated in viewDidLoad or on demand.
    // For example: self.myOutlet = nil;
}


- (void)dealloc {
    pageTable.pageTableDelegate = nil;
    [[RequestorAssistant sharedInstance] cancelAllOperationOfRequestor:self];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super dealloc];
}

#pragma mark -
#pragma mark OptionProtocols delegate
- (void)operation:(GameCenterOperation *)operation getTaskListDidFinish:(NSError *)error taskList:(TaskList *)taskList {
    if (error != nil) {
        [MBProgressHUD showHintHUD:@"" message:[error localizedDescription] hideAfter:DEFAULT_TIP_LAST_TIME];
    }
    else {
        [pageTable setPageSize:[taskList.taskList count]  pageCountOnce:1];
        [pageTable didDownloadPage:0
                        totalCount:[taskList.taskList count] 
                         dataArray:taskList.taskList 
                           success:(error == nil)];
        
        int availableCount = 0;
        for (TaskItem *item in taskList.taskList) {
            if (item.status != TASK_STATUS_EXPIRED)
                availableCount++;
        }
        [self setBadgeNum:availableCount controller:self bAutoHideWhenZero:NO];
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
        [[NSNotificationCenter defaultCenter] postNotificationName:kGC91CompleteTaskNotification object:nil];
    }
}

#pragma mark -
#pragma mark action
- (void)doCompleteTask:(id)sender {
    UIButton *button = (UIButton*)sender;
    [RequestorAssistant requestFinishTask:button.tag delegate:self];
}

- (void)taskChanged:(NSNotification *)aNotify {
    [self requestGetTaskList];
}

@end

