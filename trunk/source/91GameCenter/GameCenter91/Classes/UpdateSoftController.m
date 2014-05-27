//
//  UpdateSoftController.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-3.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "UpdateSoftController.h"
#import "SoftManagementCenter.h"
#import "UpdatableItemCell.h"
#import "UITableViewCell+Addition.h"
#import "Notifications.h"
#import "UITableViewCell+Addition.h"
#import "UpdatableItemCell.h"
#import "UpdatingItemCell.h"
#import "SoftItem.h"
#import "UserData.h"
#import "CommUtility.h"
#import "UIViewController+Extent.h"
#import "TabContainerController.h"
#import "RIButtonItem.h"
#import "MBProgressHUD.h"
#import "CustomAlertView.h"
#import "UIAlertView+Blocks.h"
#import "GameDetailController.h"
#import "StatusBarNotification.h"

@interface UpdateSoftController()
@property (nonatomic, retain) NSArray *updatingSofts;
@property (nonatomic, retain) NSArray *updateAvailabelSofts;

@property (nonatomic, retain) UIView *hintView;
@property (nonatomic, retain) NSMutableArray *expandedIndexPathArray;

- (void)initHintView;
- (void)showHintView:(BOOL)show;
@end

@implementation UpdateSoftController
@synthesize updatingSofts, updateAvailabelSofts;
@synthesize expandedIndexPathArray;

- (id)initWithStyle:(UITableViewStyle)style
{
    style = UITableViewStyleGrouped;
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
        self.title = @"可升级";
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
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    self.updateAvailabelSofts = nil;
    self.updatingSofts = nil;
    self.hintView = nil;
    self.expandedIndexPathArray = nil;
    [super dealloc];
}

- (void)updateQueueChanged
{
    self.updatingSofts = [[SoftManagementCenter sharedInstance] updatingSoftItemList];
    self.updateAvailabelSofts = [[SoftManagementCenter sharedInstance] updatableSoftItemList];    
    [self refreshBadgeNumber];
    [self.expandedIndexPathArray removeAllObjects];
    [self.tableView reloadData];
}

- (void)smartUpdateFailed:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[aNotify object];
//    self.updatingSofts = [[SoftManagementCenter sharedInstance] updatingSoftItemList];
//    self.updateAvailabelSofts = [[SoftManagementCenter sharedInstance] updatableSoftItemList];
    NSString *massage = [NSString stringWithFormat:@"(%@) 智能升级失败，正在普通升级",item.softName];
    [StatusBarNotification notificationWithMessage:massage];
    [self.tableView reloadData];
}

- (void)updatingPercentChanged:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[aNotify object];
    if (item)
    {
        for (UITableViewCell *cell in self.tableView.visibleCells) {
            if ([cell isKindOfClass:[UpdatingItemCell class]])
            {
                UpdatingItemCell *aCell = (UpdatingItemCell *)cell;
                if ([aCell.appIdentifier isEqualToString:item.identifier])
                {
                    [aCell setSoftItem:item];
                }
            }
        }
    }
    
}

- (void)refreshBadgeNumber
{
    int count = [[SoftManagementCenter sharedInstance] updatableCount];
        
    [(TabContainerController *)(self.parentContainerController) setBadgeNumber:count forSubController:self bAutoHideWhenZero:YES];
}


#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];

    [CommUtility clearGroupTableBgColor:self.tableView];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    
    self.expandedIndexPathArray = [NSMutableArray array];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updatingPercentChanged:) name:kGC91UpdatingPercentChangeNotification object:nil];  
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateQueueChanged) name:kGC91UpdateQueueChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(smartUpdateFailed:) name:kGC91SmartUpdateFailedNotification object:nil];//智能更新失败
    
    [self initHintView];
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.updateAvailabelSofts = [[SoftManagementCenter sharedInstance] updatableSoftItemList];
    self.updatingSofts = [[SoftManagementCenter sharedInstance] updatingSoftItemList];
    [self.tableView reloadData];
    [self refreshBadgeNumber];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    if ([self.updatingSofts count] + [self.updateAvailabelSofts count] == 0)
    {
        [self showHintView:YES];
        return 0;
    }
    [self showHintView:NO];
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.updatingSofts count] + [self.updateAvailabelSofts count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = nil;
    int row = indexPath.row;
    if (row < [self.updatingSofts count])
    {
        UpdatingItemCell *aCell = [UpdatingItemCell dequeOrCreateInTable:tableView];
        [aCell setSoftItem:[self.updatingSofts objectAtIndex:row]];
        [aCell setExpand:[self.expandedIndexPathArray containsObject:indexPath] showRoundCorner:(indexPath.row == [self.updatingSofts count] + [self.updateAvailabelSofts count] - 1)];
        [aCell setExpandLeftButtonAction:self action:@selector(enterGameDetail:)];
        [aCell setExpandRightButtonAction:self action:@selector(cancelTask:)];
        cell = aCell;
    }
    else
    {
        row = row - [self.updatingSofts count];
        UpdatableItemCell *aCell = [UpdatableItemCell dequeOrCreateInTable:tableView];
        [aCell setSoftInfo:[self.updateAvailabelSofts objectAtIndex:row]];
        cell = aCell;
    }
    // Configure the cell...
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row < [self.updatingSofts count])
    {
        return [self.expandedIndexPathArray containsObject:indexPath] ? [UpdatingItemCell expandedCellHeight] : [UpdatingItemCell normalCellHeight];
    }
    return [UpdatableItemCell cellHeight];
}

//- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
//{
//    UILabel *label = [[[UILabel alloc] initWithFrame:CGRectMake(0, 12, 300, 40)] autorelease];
//    label.backgroundColor = [UIColor clearColor];
//    label.textColor = [UIColor colorWithRed:0.46 green:0.46 blue:0.46 alpha:1];
//    label.font = [UIFont boldSystemFontOfSize:17];
//    label.shadowColor = [UIColor whiteColor];
//    label.shadowOffset = CGSizeMake(1, 1);
//    if (section == 0)
//    {
//        label.text = @"   可升级";
//        int count = [self.updatingSofts count] + [self.updateAvailabelSofts count];
//        label.hidden = (count == 0);
//    }
//    return label;
//}

//- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
//{
//    float height = 40;
//    if (section == 0 && ([self.updatingSofts count] + [self.updateAvailabelSofts count] == 0))
//    {
//        height = 0;
//    }
//    return height;
//}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSIndexPath *previous = nil;
    
    if ([self.expandedIndexPathArray containsObject:indexPath])
    {
        [self.expandedIndexPathArray removeObject:indexPath];
    }
    else
    {
        //currently we only allow one expand
        if ([self.expandedIndexPathArray count] != 0)
            previous = [[[self.expandedIndexPathArray objectAtIndex:0] retain] autorelease];
        [self.expandedIndexPathArray removeAllObjects];
        [self.expandedIndexPathArray addObject:indexPath];
    }
    
	[tableView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:indexPath, previous, nil] withRowAnimation:UITableViewRowAnimationNone];
    [tableView scrollToNearestSelectedRowAtScrollPosition:UITableViewScrollPositionNone animated:NO];
}

#pragma mark -
- (void)initHintView
{
    UIView *hintView = [[[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 120)] autorelease];
    hintView.hidden = YES;
    hintView.center = CGPointMake(160, 180);
    [self.view addSubview:hintView];
    UIImageView *icon = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"errorIcon.png"]] autorelease];
    icon.center = CGPointMake(160, 30);
    [hintView addSubview:icon];
    UILabel *label = [[[UILabel alloc] initWithFrame:CGRectMake(0, 85, 320, 60)] autorelease];
    label.backgroundColor = [UIColor clearColor];
    label.textColor = [UIColor darkGrayColor];
    label.numberOfLines = 0;
    label.font = [UIFont systemFontOfSize:14];
    label.textAlignment = UITextAlignmentCenter;
    [hintView addSubview:label];
    
    self.hintView = hintView;
}

- (void)showHintView:(BOOL)show
{
    self.hintView.hidden = !show;
    
    if (show) {
        NSString *hint = [[[SoftManagementCenter sharedInstance] installed91SDKSoft] count] > 0 ? @"太棒了，你的游戏都是最新版本" : @"你还未安装游戏，91游戏中心有海量游戏等你玩，赶紧下载吧";
        for (UIView *aView in self.hintView.subviews) {
            if ([aView isKindOfClass:[UILabel class]]) {
                UILabel *lbl = (UILabel *)aView;
                lbl.text = hint;
                break;
            }
        }
    }
}

- (void)cancelTask:(UIButton *)btn
{
    ExpandableCell *cell = (ExpandableCell *)[[[btn superview] superview] superview];
    NSString *appIdentifier = cell.appIdentifier;
    
    [[SoftManagementCenter sharedInstance] cancelTask:appIdentifier];
    [self.expandedIndexPathArray removeAllObjects];
    [self.tableView reloadData];
    [MBProgressHUD hideBlockHUD:YES];
}

- (void)enterGameDetail:(UIButton *)button
{
    ExpandableCell *cell = (ExpandableCell *)[[[button superview] superview] superview];
    NSString *appIdentifier = cell.appIdentifier;
    
    GameDetailController *ctr = [GameDetailController gameDetailWithIdentifier:appIdentifier gameName:cell.gameName];
    [self.parentContainerController.navigationController pushViewController:ctr animated:YES];
}

@end
