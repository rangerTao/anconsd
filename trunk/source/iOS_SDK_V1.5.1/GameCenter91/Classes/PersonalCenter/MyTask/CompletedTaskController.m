//
//  CompletedTaskController.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-14.
//  Copyright 2012 net dragon. All rights reserved.
//

#import "CompletedTaskController.h"
#import "UITableViewCell+Addition.h"
#import "UIViewController+Extent.h"
#import "UIViewController+Extent.h"
#import "CompletedTaskTitleCell.h"
#import "CompletedTaskItemCell.h"
#import "TaskDetailController.h"
#import "TaskList.h"
#import "RequestorAssistant.h"
#import "MBProgressHUD.h"
#import "Notifications.h"
#import "CommUtility.h"
#import "TabContainerController.h"

@implementation CompletedTaskController

#pragma mark -
#pragma mark Initialization

- (id)init {

    if ((self = [super init])) {
        self.title = @"已完成";
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];

    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:[CommUtility isTabbarHide] otherExcludeHeight:[TabContainerController defaultSegmentHeight]];
	pageTable = [[[GcPageTable alloc] initWithFrame:CGRectMake(0, 0, 320.0, height) style:UITableViewStyleGrouped] autorelease];
	pageTable.strMoreTip = @"更多任务";
	pageTable.strNoDataTip = @"一项任务都还没有完成？亲，要加油喽，世界还等着你去拯救呢。";
	[pageTable setNdPageTableTransparent];
	[pageTable enableEgoRefreshTableHeaderView];
	pageTable.pageTableDelegate = self;
	pageTable.customCellCache = [CompletedTaskItemCell loadFromNib];
    pageTable.titleCellCache = [CompletedTaskTitleCell loadFromNib];
    pageTable.titleRowHeight = [CompletedTaskTitleCell cellHeight];
    [self.view addSubview:pageTable];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(taskChanged:) name:kGC91CompleteTaskNotification object:nil];
}

- (int)requestGetTaskList {
    NSNumber *ret = [RequestorAssistant requestGetTaskList:3 delegate:self];
    return [ret intValue];
}

#pragma mark -
#pragma mark NdPageTableDelegate
- (void)GcPageTable:(GcPageTable*)table customCell:(UITableViewCell*)cell customData:(id)data
{
	CompletedTaskItemCell* taskCell = (CompletedTaskItemCell*)cell;
	if(data != nil) {
        TaskItem* info = (TaskItem*)data;
        [taskCell setInfo:info.taskName detail:info.summary score:info.rewardNumber];
	}
}

- (CGFloat)GcPageTable:(GcPageTable*)table heightForCustomCell:(UITableViewCell*)cellCache  customData:(id)data
{
	return [CompletedTaskItemCell cellHeight];
}

- (int)GcPageTable:(GcPageTable*)table downloadPageIndex:(NSInteger)pageIdx  pageSize:(NSInteger)pageSize
{
    return [self requestGetTaskList];
}

- (UITableViewCell*)GcPageTable:(GcPageTable*)table cellCopyFromCacheCell:(UITableViewCell*)cellCache
{
	CompletedTaskItemCell* cellNew = [CompletedTaskItemCell loadFromNib];
	return cellNew;
}

- (void)GcPageTable:(GcPageTable*)table didSelectRowWithData:(id)data 
{
    TaskDetailController *detailViewController = [[TaskDetailController alloc] init];
    TaskItem* info = (TaskItem*)data;
    detailViewController.taskId = info.taskId;
    detailViewController.taskType = TYPE_TASK_COMPLETED;
    [self.parentContainerController.navigationController pushViewController:detailViewController animated:YES];
    [detailViewController release];
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
    }
}

- (void)taskChanged:(NSNotification *)aNotify {
    [self requestGetTaskList];
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


@end

