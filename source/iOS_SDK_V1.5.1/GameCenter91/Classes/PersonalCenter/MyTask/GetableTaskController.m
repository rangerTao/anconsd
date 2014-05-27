//
//  GetableTaskController.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-12.
//  Copyright 2012 net dragon. All rights reserved.
//

#import "GetableTaskController.h"
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

@implementation GetableTaskController

#pragma mark -
#pragma mark Initialization


- (id)init {
    if ((self = [super init])) {
        self.title = @"可接";
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
    pageTable.strNoDataTip = @"暂时没有新任务可以领取了～\n太棒啦，您真是任务达人！";
	pageTable.pageTableDelegate = self;
	pageTable.customCellCache = [UnCompletedTaskItemCell loadFromNib];
    pageTable.titleCellCache = [UnCompletedTaskTitleCell loadFromNib];
    pageTable.titleRowHeight = [UnCompletedTaskTitleCell cellHeight];
    //pageTable.bReverseOrder = YES;
	[pageTable setNdPageTableTransparent];
	[pageTable setPageSize:10 pageCountOnce:1];
	[pageTable enableEgoRefreshTableHeaderView];
	[self.view addSubview:pageTable];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(taskChanged:) name:kGC91ClaimTaskNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(taskChanged:) name:kGC91CompleteTaskNotification object:nil];
}

- (int)requestGetTaskList {
    NSNumber *ret = [RequestorAssistant requestGetTaskList:1 delegate:self];
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
        BOOL taskStatus = (info.status == -1) ? NO : YES;
        [taskCell setButton:info.taskId title:@"接任务" status:taskStatus target:self action:@selector(doGetTask:)];
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
    detailViewController.taskType = TYPE_TASK_GETABLE;
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
        
        int getableCount = 0;
        for (TaskItem *item in taskList.taskList) {
            if (item.status == TASK_STATUS_AVAILABLE)
                getableCount++;
        }

        [self setBadgeNum:getableCount controller:self bAutoHideWhenZero:NO];
    }
}

- (void)operation:(GameCenterOperation *)operation claimTaskDidFinish:(NSError *)error task:(TaskItem *)task {
    if (error != nil) {
        [MBProgressHUD showHintHUD:@"" message:[error localizedDescription] hideAfter:DEFAULT_TIP_LAST_TIME];
    }
    else {
        [MBProgressHUD showHintHUD:@"该任务已成为您的当前任务" message:@"请到“当前”的任务中查看和领取奖励" hideAfter:DEFAULT_TIP_LAST_TIME];
        [[NSNotificationCenter defaultCenter] postNotificationName:kGC91ClaimTaskNotification object:nil];
    }
}

#pragma mark -
#pragma mark action
- (void)doGetTask:(id)sender {
    UIButton *button = (UIButton*)sender;
    [RequestorAssistant requestClaimTask:button.tag delegate:self];
}

- (void)taskChanged:(NSNotification *)aNotify {
    [self requestGetTaskList];
}

@end

