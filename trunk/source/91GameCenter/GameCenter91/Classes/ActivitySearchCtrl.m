//
//  ActivitySearchCtrl.m
//  GameCenter91
//
//  Created by hiyo on 12-12-12.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "ActivitySearchCtrl.h"
#import "ActivityTableViewCell.h"
#import "ActivityNewServersNoticeCell.h"
#import "ActivityDetailCtrl.h"
#import "ActivityInfo.h"

#import "GcPagination.h"
#import "CommUtility.h"
#import "UserData.h"
#import "GameCenterOperation.h"

#import "UIViewController+Extent.h"
#import "UITableViewCell+Addition.h"
#import "MBProgressHUD.h"
#import <QuartzCore/CALayer.h>
#import "NSDate+Utilities.h"
#import "ColorfulImage.h"

#define GATABLE_PAGE_SIZE       999
#define OFFSET_TABLE            110.0
#define CELL_HEIGHT				44.0
#define TAG_BG_VIEW				254795

@interface ActivitySearchCtrl()
@property (nonatomic, retain) UITableView *chooseTableView;
@property (nonatomic, retain) NSArray *chooseArr;
@property (retain, nonatomic) IBOutlet UIButton *reset_btn;

- (NSString *)activityCtrlTitle:(id)data;
- (void)showCell:(ActivityTableViewCell *)cell withActivityInfo:(ActivityInfo *)info;
- (void)showNewServersNoticeCell:(ActivityNewServersNoticeCell *)cell withActivityInfo:(ActivityInfo *)info;
- (void)showChooseTableView;
- (void)hideChooseTableView;
@end

@implementation ActivitySearchCtrl
@synthesize table_result;
@synthesize act_type;
@synthesize chooseView, chooseLabel, keywordTextField;
@synthesize chooseTableView, chooseArr;

- (id)init
{
	self = [super init];
	if (self) {
		self.table_result = nil;
        self.chooseTableView = nil;
        self.chooseArr = [NSArray arrayWithObjects:@"在所有活动中搜索", @"在游戏礼包中搜索", @"在活动公告中搜索", @"在新服预告中搜索", nil];
        
        self.act_type = SEARCH_ALL_ACTIVITYS;
	}
	return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.customTitle = @"活动搜索";
    self.chooseView.layer.cornerRadius = 4.0;
	self.chooseView.layer.borderWidth = 0.4;
    self.chooseView.layer.borderColor = [UIColor lightGrayColor].CGColor;
    self.view.backgroundColor = [CommUtility defaultBgColor];
    switch (self.act_type) {
        case SEARCH_GAME_GIFTS:
            self.chooseLabel.text = [self.chooseArr objectAtIndex:1];
            break;
        case SEARCH_ACTIVITY_NOTICE:
            self.chooseLabel.text = [self.chooseArr objectAtIndex:2];
            break;
        case SEARCH_NEW_SERVERS_NOTICE:
            self.chooseLabel.text = [self.chooseArr objectAtIndex:3];
            break;
        default:
            self.chooseLabel.text = [self.chooseArr objectAtIndex:0];
            break;
    }
    
    //搜索结果列表
    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:YES otherExcludeHeight:OFFSET_TABLE];
	self.table_result = [[[GcPageTable alloc] initWithFrame:CGRectMake(0, OFFSET_TABLE, 320.0, height) style:UITableViewStylePlain] autorelease];
	table_result.strMoreTip = @"更多记录⋯⋯";
	table_result.strNoDataTip = @"亲，没有搜索到您需要的内容呢。\n换一个关键字再试一试？";
	table_result.rowHeight = 80;
	table_result.pageTableDelegate = self;
	table_result.customCellCache = [ActivityTableViewCell loadFromNib];
	[table_result setNdPageTableTransparent];
	[table_result setPageSize:GATABLE_PAGE_SIZE pageCountOnce:1];
	[table_result enableEgoRefreshTableHeaderView];
    table_result.hidden = YES;
    [self.view addSubview:table_result];
    //搜索条件列表
    self.chooseTableView = [[[UITableView alloc] initWithFrame:CGRectMake(0, 0, 200, CELL_HEIGHT*4) style:UITableViewStylePlain] autorelease];
	chooseTableView.layer.cornerRadius = 8;
	chooseTableView.dataSource = self;
	chooseTableView.delegate = self;
    
    self.reset_btn.hidden = YES;
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textChanged)
                                                 name:UITextFieldTextDidChangeNotification object:nil];
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)dealloc {
    self.table_result = nil;
    self.chooseTableView = nil;
    self.chooseArr = nil;
    [[RequestorAssistant sharedInstance] cancelAllOperationOfRequestor:self];

    [_reset_btn release];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super dealloc];
}

#pragma mark - search bar action
- (IBAction)doChoose:(id)sender
{
    [self.keywordTextField resignFirstResponder];
    [self showChooseTableView];
}

- (IBAction)doSearch:(id)sender
{
    [self.keywordTextField resignFirstResponder];
    if ([self.keywordTextField.text length] == 0) {
        [MBProgressHUD showHintHUD:@"搜索内容不能为空" message:@"请输入游戏名或活动名关键字" hideAfter:DEFAULT_TIP_LAST_TIME];
        return;
    }
    self.table_result.hidden = NO;
    [self.table_result clearDataAndReload];
}

- (IBAction)doDelete:(id)sender
{
    self.keywordTextField.text = @"";
    self.table_result.hidden = YES;
    self.reset_btn.hidden = YES;
}

- (void)textChanged
{
    if ([self.keywordTextField.text length] > 0) {
        self.reset_btn.hidden = NO;
    }else
    {
        self.reset_btn.hidden = YES;
    }
}

#pragma mark - UITextFieldDelegate
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self.keywordTextField resignFirstResponder];
	return YES;
}
- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    if ([textField.text length] > 0) {
        self.reset_btn.hidden = NO;
    }
    
    
}
- (void)textFieldDidEndEditing:(UITextField *)textField
{
    if ([textField.text length] <= 0) {
        self.reset_btn.hidden = YES;
    }
}

#pragma mark NdPageTableDelegate
- (void)GcPageTable:(GcPageTable*)table customCell:(UITableViewCell*)cell customData:(id)data
{
    ActivityTableViewCell* actCell = (ActivityTableViewCell*)cell;
    [self showCell:actCell withActivityInfo:data];
    
    //点击态颜色
    UIImageView *bgImgView = [[[UIImageView alloc] init] autorelease];
    UIImage *bgSelectedImage = [ColorfulImage imageWithColor:[CommUtility colorWithHexRGB:@"b1daec"]];
    bgImgView.image = bgSelectedImage;
    actCell.selectedBackgroundView = bgImgView;
}

- (CGFloat)	GcPageTable:(GcPageTable*)table heightForCustomCell:(UITableViewCell*)cellCache  customData:(id)data
{
    return [ActivityTableViewCell cellHeight];
}

- (int)GcPageTable:(GcPageTable*)table downloadPageIndex:(NSInteger)pageIdx  pageSize:(NSInteger)pageSize
{
    if (self.table_result.hidden == NO) {
        GcPagination* pg = [[GcPagination new] autorelease];
        pg.pageIndex = pageIdx + 1;
        pg.pageSize = pageSize;
        NSNumber *ret = [NSNumber numberWithInt:-1];
        [MBProgressHUD showHUDAddedTo:self.view animated:YES]; 
        ret = [RequestorAssistant requestGetActivityList:nil type:self.act_type page:pg keyword:self.keywordTextField.text delegate:self];
        if ([ret intValue] < 0) {
            [MBProgressHUD hideHUDForView:self.view animated:YES];
            [self performSelector:@selector(delayCustomDidDownloadWhenError) withObject:nil afterDelay:0.5];
        }
        
        return [ret intValue];
    }
	return YES;
}

- (UITableViewCell*)GcPageTable:(GcPageTable*)table cellCopyFromCacheCell:(UITableViewCell*)cellCache
{
	ActivityTableViewCell* cellNew = [ActivityTableViewCell loadFromNib];
	return cellNew;
}

//在活动搜索中，采用统一的cell，新服预告不特别显示
//- (UITableViewCell*)GcPageTable:(GcPageTable*)table cellCopyByCustomData:(id)data
//{
//    ActivityInfo *info = (ActivityInfo*)data;
//    if (info.activityType == ACTIVITY_TYPE_NEW_SERVERS_NOTICE) {
//        return [ActivityNewServersNoticeCell loadFromNib];
//    }
//    else {
//        return [ActivityTableViewCell loadFromNib];
//    }
//}
//
//- (NSString*)GcPageTable:(GcPageTable*)table cellIdentifierBycustomData:(id)data
//{
//    ActivityInfo *info = (ActivityInfo*)data;
//    if (info.activityType == ACTIVITY_TYPE_NEW_SERVERS_NOTICE) {
//        return [ActivityNewServersNoticeCell cellReuseIdentifier];
//    }
//    else {
//        return [ActivityTableViewCell cellReuseIdentifier];
//    }
//}

- (void) GcPageTable:(GcPageTable*)table didSelectRowWithData:(id)data 
{
	//跳转到活动详情
	ActivityDetailCtrl  *ctrl = [[ActivityDetailCtrl alloc] init];
    ctrl.customTitle = [self activityCtrlTitle:data];
    ctrl.appIdentifier = [data identifier];
    ctrl.contentUrl = [data contentUrl];
    ctrl.activityId = [data activityID];
	ctrl.hidesBottomBarWhenPushed = YES;
    [self.navigationController pushViewController:ctrl animated:YES];
    [self.table_result deselectRowAtIndexPath:self.table_result.indexPathForSelectedRow animated:YES];
	[ctrl release];
}

#pragma mark GetActivityListProtocol
- (void)operation:(GameCenterOperation *)operation getActivityListDidFinish:(NSError *)error activityList:(NSArray *)activityList page:(GcPagination *)page totalCount:(int)totalCount
{
    if (self.table_result.hidden == NO) {
        [MBProgressHUD hideHUDForView:self.view animated:YES];
        [table_result didDownloadPage:page.pageIndex-1 
                           totalCount:totalCount
                            dataArray:activityList
                              success:operation.errorMessage == nil];
        if (error) {
            [MBProgressHUD showHintHUD:@"获取活动列表失败" message:[error localizedDescription] hideAfter:DEFAULT_TIP_LAST_TIME];
        }
    }
}

#pragma mark UITableView Delegate
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.chooseArr count];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return CELL_HEIGHT;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
    cell.textLabel.text = [self.chooseArr objectAtIndex:indexPath.row];
    cell.textLabel.font = [UIFont systemFontOfSize:16];
    if ([chooseLabel.text isEqualToString:[chooseArr objectAtIndex:indexPath.row]]) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    }
    else {
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
	return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    [self hideChooseTableView];
    if (indexPath.row < [chooseArr count]) {
        self.chooseLabel.text = [self.chooseArr objectAtIndex:indexPath.row];
        [chooseTableView reloadData];
        switch (indexPath.row) {
            case 1:
                self.act_type = SEARCH_GAME_GIFTS;
                break;
            case 2:
                self.act_type = SEARCH_ACTIVITY_NOTICE;
                break;
            case 3:
                self.act_type = SEARCH_NEW_SERVERS_NOTICE;
                break;
            default:
                self.act_type = SEARCH_ALL_ACTIVITYS;
                break;
        }
    }
}

#pragma mark - other
- (NSString *)activityCtrlTitle:(id)data
{
    NSString *ctrlTitle = nil;
    int type = ((ActivityInfo *)data).activityType;
    switch (type) {
		case ACTIVITY_TYPE_GAME_GIFT:
			ctrlTitle = @"礼包";
			break;
		case ACTIVITY_TYPE_ACTIVITY_NOTICE:
			ctrlTitle = @"活动公告";
			break;
		case ACTIVITY_TYPE_PRIZE_NOTICE:
			ctrlTitle = @"获奖公告";
			break;
        case ACTIVITY_TYPE_NEW_SERVERS_NOTICE:
			ctrlTitle = @"新服预告";
			break;
		default:
			break;
    }
    return ctrlTitle;
}

- (void)showCell:(ActivityTableViewCell *)cell withActivityInfo:(ActivityInfo *)info
{
    [cell setCellInfo:ACT_OTHER withActivityInfo:info];
}

- (void)showNewServersNoticeCell:(ActivityNewServersNoticeCell *)cell withActivityInfo:(ActivityInfo *)info
{
    [cell setCellInfo:ACT_OTHER withActivityInfo:info];
}

- (void)showChooseTableView
{
    UIView *topView = [[UIApplication sharedApplication].windows objectAtIndex:0];
    UIControl *grayBackgroud = [[[UIControl alloc] init] autorelease];
    grayBackgroud.tag = TAG_BG_VIEW;
    grayBackgroud.frame = topView.bounds;
    grayBackgroud.backgroundColor = [[UIColor blackColor] colorWithAlphaComponent:0.7];
    [topView addSubview:grayBackgroud];
    
    [grayBackgroud addTarget:self action:@selector(hideChooseTableView) forControlEvents:UIControlEventTouchUpInside];
	[UIView beginAnimations:nil context:nil];
	[UIView setAnimationDuration:0.5];
	CGSize sizeSubView = chooseTableView.frame.size;
	CGPoint pt = {0,0};
	pt.x = (CGRectGetWidth(grayBackgroud.bounds) - sizeSubView.width) * 0.5f;
	pt.y = grayBackgroud.frame.origin.y + grayBackgroud.frame.size.height;
	chooseTableView.frame = CGRectMake(pt.x, pt.y, sizeSubView.width, sizeSubView.height);	
	pt.y = (CGRectGetHeight(grayBackgroud.bounds) - sizeSubView.height) * 0.5f;
	chooseTableView.frame = CGRectMake(pt.x, pt.y, sizeSubView.width, sizeSubView.height);
    if (chooseTableView.superview == nil) {
        [grayBackgroud addSubview:chooseTableView];
    }
    
	[UIView commitAnimations];
}
- (void)hideChooseTableView
{
    UIControl *grayBackgroud = (UIControl *)[[[UIApplication sharedApplication].windows objectAtIndex:0] viewWithTag:TAG_BG_VIEW];
    if ([grayBackgroud.subviews count] > 0)
	{
		[UIView beginAnimations:nil context:nil];
		[UIView setAnimationDuration:0.5];
		[UIView setAnimationDelegate:self];
		UIView *subView = [[grayBackgroud subviews] objectAtIndex:0];
		CGRect rt = subView.frame;
		CGRect fr = grayBackgroud.frame;
		rt.origin.y =  fr.origin.y + fr.size.height;
		subView.frame = rt;
		[UIView commitAnimations];
	}
	else
	{
		[grayBackgroud removeFromSuperview];		
	}
}

-(void)animationDidStop:(NSString *)animationID finished:(NSNumber *)finished context:(void *)context
{
    UIView *grayBackgroudView = [[[UIApplication sharedApplication].windows objectAtIndex:0] viewWithTag:TAG_BG_VIEW];
	[grayBackgroudView removeFromSuperview];
}

- (void)viewDidUnload {
    [self setReset_btn:nil];
    [super viewDidUnload];
}
@end
