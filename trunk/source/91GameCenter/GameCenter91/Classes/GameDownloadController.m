//
//  GameDownloadController.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-3.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "GameDownloadController.h"
#import "UITableViewCell+Addition.h"
#import "DownloadedItemCell.h"
#import "DownloadingItemCell.h"
#import "SoftManagementCenter.h"
#import "Notifications.h"
#import "SoftItem.h"
#import "MBProgressHUD.h"
#import "GameDetailController.h"
#import "UIViewController+Extent.h"
#import "CommUtility.h"
#import "UIAlertView+Blocks.h"
#import "RIButtonItem.h"
#import "CustomAlertView.h"

@interface GameDownloadController()
@property (nonatomic, retain) NSArray *downloadedSofts;
@property (nonatomic, retain) NSArray *downloadingSofts;

@property (nonatomic, retain) NSMutableArray *expandedIndexPathArray;

- (void)showHintView:(BOOL)show;
@end


@implementation GameDownloadController
@synthesize downloadedSofts, downloadingSofts;
@synthesize expandedIndexPathArray;

- (id)initWithStyle:(UITableViewStyle)style
{
    style = UITableViewStyleGrouped;
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
        self.title = @"全部游戏";
        hintView = nil;
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
    self.downloadingSofts = nil;
    self.downloadedSofts = nil;
    self.expandedIndexPathArray = nil;
    [super dealloc];
}
#pragma mark - View lifecycle

- (void)showHintView:(BOOL)show
{
    hintView.hidden = !show;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [CommUtility clearGroupTableBgColor:self.tableView];
    
    hintView = [[[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 120)] autorelease];
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
    label.text = @"你还未安装游戏，91游戏中心有海量游戏等你玩，赶紧下载吧";
    label.textAlignment = UITextAlignmentCenter;
    [hintView addSubview:label];
    
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    
    self.expandedIndexPathArray = [NSMutableArray array];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadQueueChanged:) name:kGC91DownloadQueueChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadPercentChanged:) name:kGC91DownloadPercentChangeNotification object:nil];
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)downloadQueueChanged:(NSNotification *)aNotify
{
    self.downloadedSofts = [[SoftManagementCenter sharedInstance] downloadedSoftItemList];
    self.downloadingSofts = [[SoftManagementCenter sharedInstance] downloadingSoftItemList];
    
    [self.expandedIndexPathArray removeAllObjects];
    
    [self.tableView reloadData];
}

- (void)downloadPercentChanged:(NSNotification *)aNotify
{
//    NSLog(@"%@", aNotify);
    SoftItem *item = (SoftItem *)[aNotify object];
    if (item)
    {
        for (UITableViewCell *cell in self.tableView.visibleCells) {
            if ([cell isKindOfClass:[DownloadingItemCell class]])
            {
                DownloadingItemCell *aCell = (DownloadingItemCell *)cell;
                if ([aCell.appIdentifier isEqualToString:item.identifier])
                {
                    [aCell setSoftItem:item];
                }
            }
        }
    }
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    self.downloadedSofts = [[SoftManagementCenter sharedInstance] downloadedSoftItemList];
    self.downloadingSofts = [[SoftManagementCenter sharedInstance] downloadingSoftItemList];
    
    [super viewWillAppear:animated];
    
    [self.tableView reloadData];
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
    // Return the number of sections.
    if ([self.downloadedSofts count] + [self.downloadingSofts count] == 0)
    {
        [self showHintView:YES];
        return 0;
    }
    [self showHintView:NO];
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    if (section == 0)
        return [self.downloadingSofts count];
    return [self.downloadedSofts count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = nil;
    if (indexPath.section == 0)
    {
        DownloadingItemCell *aCell = [DownloadingItemCell dequeOrCreateInTable:tableView];
        [aCell setSoftItem:[self.downloadingSofts objectAtIndex:indexPath.row]];
        [aCell setExpand:[self.expandedIndexPathArray containsObject:indexPath] showRoundCorner:(indexPath.row == [self.downloadingSofts count] - 1)];        
        [aCell setExpandRightButtonAction:self action:@selector(removeTask:)];
        [aCell setExpandLeftButtonAction:self action:@selector(enterGameDetail:)];

        cell = aCell;
    }
    else
    {
        DownloadedItemCell *aCell = [DownloadedItemCell dequeOrCreateInTable:tableView];
        if (indexPath.row < [self.downloadedSofts count])
        {
            [aCell setSoftInfo:[self.downloadedSofts objectAtIndex:indexPath.row]];
        }
        
        [aCell setExpand:[self.expandedIndexPathArray containsObject:indexPath] showRoundCorner:(indexPath.row == [self.downloadedSofts count] - 1)];
        [aCell setExpandRightButtonAction:self action:@selector(removeTask:)];
        [aCell setExpandLeftButtonAction:self action:@selector(enterGameDetail:)];
        cell = aCell;
    }
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 0) 
    {
        return [self.expandedIndexPathArray containsObject:indexPath] ? [DownloadingItemCell expandedCellHeight] : [DownloadingItemCell normalCellHeight];
    }
    else
    {
        return [self.expandedIndexPathArray containsObject:indexPath] ? [DownloadedItemCell expandedCellHeight] : [DownloadedItemCell normalCellHeight];
    }
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    if ([self.downloadingSofts count] == 0 || [self.downloadedSofts count] == 0) {
        return nil;
    }
    UILabel *label = [[[UILabel alloc] initWithFrame:CGRectMake(0, 12, 300, 40)] autorelease];
    label.backgroundColor = [UIColor clearColor];
    label.textColor = [UIColor colorWithRed:0.46 green:0.46 blue:0.46 alpha:1];
    label.font = [UIFont boldSystemFontOfSize:17];
    label.shadowColor = [UIColor whiteColor];
    label.shadowOffset = CGSizeMake(1, 1);
    if (section == 0)
    {
        label.text = @"   下载中";
    }
    else
    {   
        label.text = @"   全部游戏";
    }
    label.clipsToBounds = YES;
    if (section == 0)
    {
        label.hidden = ([self.downloadingSofts count] == 0);
    }
    else
    {
        label.hidden = ([self.downloadedSofts count] == 0);    
    }
    return label;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if ([self.downloadingSofts count] == 0 || [self.downloadedSofts count] == 0) {
        return 0.1;
    }
    float height = 40;
    if (section == 0 && [self.downloadingSofts count] == 0)
    {
        height = 0;
    }
    else if (section == 1 && [self.downloadedSofts count] == 0)
    {
        height = 0;
    }
    return height;
}


#pragma mark - Table view delegate

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

- (void)startRemove:(NSString *)appIdentifier
{
    [MBProgressHUD showBlockHUD:YES];
    [self performSelector:@selector(removeItem:) withObject:appIdentifier afterDelay:0.01];
}

- (void)removeTask:(UIButton *)btn
{
    ExpandableCell *cell = [self getCellViewBySubView:btn];
    NSString *appIdentifier = cell.appIdentifier;
    
    RIButtonItem *okItem = [RIButtonItem itemWithLabel:@"确定"];
    okItem.action = ^{[self startRemove:appIdentifier];};
    RIButtonItem *cancelItem = [RIButtonItem itemWithLabel:@"取消"];
    
    NSString *actionText = @"取消下载";
    SoftItem *item = [[SoftManagementCenter sharedInstance] softItemForIdentifier:appIdentifier];
    if ([[SoftManagementCenter sharedInstance] isAnInstalledSoftItem:item])
        actionText = @"卸载";
    
    NSString *text = [NSString stringWithFormat:@"您确定要%@\"%@\"？", actionText, item.softName];
    
    CustomAlertView *alert = [[CustomAlertView alloc] initWithTitle:text message:nil cancelButtonItem:nil otherButtonItems:okItem, cancelItem, nil];
    [alert show];
    [alert release];
}

- (void)removeItem:(NSString *)appIdentifier
{
    [[SoftManagementCenter sharedInstance] removeTask:appIdentifier];
    [self.expandedIndexPathArray removeAllObjects];
    [self.tableView reloadData];
    [MBProgressHUD hideBlockHUD:YES];
}

- (void)enterGameDetail:(UIButton *)button
{
    ExpandableCell *cell = [self getCellViewBySubView:button];
    
    NSString *appIdentifier = cell.appIdentifier;
    
    GameDetailController *ctr = [GameDetailController gameDetailWithIdentifier:appIdentifier gameName:cell.gameName];
    [self.parentContainerController.navigationController pushViewController:ctr animated:YES];
}

- (ExpandableCell *)getCellViewBySubView:(UIButton *)button
{
    ExpandableCell *cell = (ExpandableCell *)[[[button superview] superview] superview];
    
    if (![cell isKindOfClass:[ExpandableCell class]]) {//适配ios7
        cell = (ExpandableCell *)cell.superview;
    }
    return cell;
}
@end
