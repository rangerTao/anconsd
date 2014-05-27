//
//  GameDetailController.m
//  GameCenter91
//
//  Created by hiyo on 12-9-11.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "GameDetailController.h"
#import "ActivityCommonCtrl.h"
#import "UIViewController+Extent.h"
#import <NdComPlatform/NdComPlatform.h>
#import "SoftManagementCenter.h"
#import "CommUtility.h"
#import "MBProgressHUD.h"
#import "FlexibleTextView.h"
#import "GameDetailDownloadCell.h"
#import "RequestorAssistant.h"
#import "AppDetailCacheInfo.h"
#import "UIImageView+WebCache.h"
#import "BaseBorderlessCell.h"
#import "DetailViewGuideCell.h"
#import "UITableViewCell+Addition.h"
#import "DownloadButtonBar.h"
#import "ActivityCacheInfo.h"
#import "FullScreenImageViewController.h"
#import "SectionHeadCell.h"
#import "GameDetailWebCtrl.h"
#import "GuideCacheInfo.h"
#import "GameTableViewCell.h"
#import "ActivityCacheInfo.h"
#import "StrategyCacheInfo.h"
#import "ForumCacheInfo.h"
#import "ActivityDetailCtrl.h"
#import "ReportCenter.h"
#import "UpdatableItemCell.h"
#import "UIBarButtonItem+Extent.h"
#import "SoftItem.h"
#define SPACE_SECTIONS  1
#define EXPENDLABELTAG  200
#define EXPENDIMAGE 202
#define DESCRIPVIEWTAG 203
#define START_X 5
#define ADAPT_X 12 //适配ios7


@interface GameDetailController()<FlexibleTextViewProtocol,UITableViewDataSource,UITableViewDelegate,GetAppDetailViewInfoProtocol,GetAppLastedVersionProtocol>

@property (nonatomic, retain) UITableView *detailTableView;
@property (nonatomic, retain) NSMutableArray *sectionsArr;
@property (nonatomic, assign) BOOL isUnInstalledFlag;
@property (nonatomic, assign) BOOL descripHideFlag;
@property (nonatomic, assign) BOOL activitySectionIsNilFlag;
@property (nonatomic, assign) BOOL onlyHaveDescripFlag;
@property (nonatomic, retain) UITableViewCell *imagesCell;
@property (nonatomic, retain) SectionHeadCell * descriptionCell;
@property (nonatomic, retain) NSString *identifier;
@property (nonatomic, retain) AppDetailViewInfo *detailInfo;
@property (nonatomic, retain) UIScrollView *imgScroll;
@property (nonatomic, assign) CGPoint scollOffset;
@property (nonatomic, retain) GameTableViewCell *briefCell;
@property (nonatomic, retain) UpdatableItemCell *briefCellForSDKUpgrade;

@property (nonatomic, assign) BOOL  showMoreStragy;
@property (nonatomic, assign) BOOL  showMoreForum;

@property (nonatomic, assign) BOOL isSDKUpgrade;
@end

@implementation GameDetailController


- (void)dealloc
{
    [self clearAllProperty];
    [super dealloc];
}

+ (GameDetailController *)gameDetailWithIdentifier:(NSString *)identifier  gameName:(NSString *)gameName
{
    GameDetailController *detail = [[[GameDetailController alloc] initWithIdentifier:identifier] autorelease];
    if (gameName == nil) {
        gameName = @"游戏名";
    }
    detail.customTitle = gameName;
    detail.hidesBottomBarWhenPushed = YES;
    
    detail.isSDKUpgrade = NO;

    return detail;
}

+ (GameDetailController *)gameDetailForSDKUpgradeWithIdentifier:(NSString *)identifier gameName:(NSString *)gameName
{
    GameDetailController *detail = [[[GameDetailController alloc] initWithIdentifier:identifier] autorelease];
    if (gameName == nil) {
        gameName = @"游戏名";
    }
    detail.customTitle = gameName;
    detail.hidesBottomBarWhenPushed = YES;
    detail.navigationItem.leftBarButtonItem = [UIBarButtonItem rightItemWithCustomStyle:@"关闭" target:detail action:@selector(closeDetailView)];
//    SoftItem *item = [[SoftManagementCenter sharedInstance] updatableSoftItemForIdentifier:identifier];

//    if (item != nil) {
        detail.isSDKUpgrade = YES;
//    }
    
    return detail;

}

- (id)initWithIdentifier:(NSString *)aIdentifier
{
    self = [super init];
    if (self) {
        self.identifier = aIdentifier;
        self.isUnInstalledFlag = YES;
        self.activitySectionIsNilFlag = NO;
        self.onlyHaveDescripFlag = NO;
        self.showMoreForum = NO;
        self.showMoreStragy = NO;
        self.isSDKUpgrade = NO;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    CGFloat height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:NO otherExcludeHeight:0];
    self.detailTableView = [[[UITableView alloc] initWithFrame:CGRectMake(-4, 0, 320  + 8, height) style:UITableViewStyleGrouped] autorelease];
    self.detailTableView.dataSource = self;
    self.detailTableView.delegate = self;
    self.detailTableView.separatorColor = [UIColor clearColor];
    UIView *backGroundView = [[UIView new] autorelease];
    backGroundView.backgroundColor = [CommUtility colorWithHexRGB:@"dcdedf"];
    [self.detailTableView setBackgroundView:backGroundView];
    self.sectionsArr = [self AllSectionsCellForUnInstall];
    
    if (self.isSDKUpgrade) {
        SoftItem *item = [[SoftManagementCenter sharedInstance] updatableSoftItemForIdentifier:self.identifier];
        [self.briefCellForSDKUpgrade setSoftInfo:item];
        self.briefCellForSDKUpgrade.backgroundColor = [UIColor whiteColor];
        [self.briefCellForSDKUpgrade adjustForDetailViewWithInfo:nil];
//        [CommUtility showBarItemForCallBack:self];//由openUrl打开进入时显示回游戏按钮

    }else
    {
        [self.briefCell adjustForDetailViewCtrWithInfo:nil];
        self.briefCell.backgroundColor = [UIColor whiteColor];

    }
    [self.view addSubview:self.detailTableView];
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    NSNumber *ret = [RequestorAssistant requestAppDetailViewInfo:self.identifier delegate:self];
    if ([ret intValue] < 0) {
        [MBProgressHUD hideHUDForView:self.view animated:YES];
        [MBProgressHUD showHintHUD:@"错误" message:[NSString stringWithFormat:@"%d", [ret intValue]] hideAfter:DEFAULT_TIP_LAST_TIME];
    }
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (void)closeDetailView
{
    [self dismissModalViewControllerAnimated:YES];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.imgScroll.contentOffset = self.scollOffset;
    [self.detailTableView reloadData];
 
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (NSMutableArray *)AllSectionsCellForUnInstall
{
    self.isUnInstalledFlag = YES;
    self.imgScroll = [self getScrollImageView];
    
    if (self.isSDKUpgrade) {
        self.briefCellForSDKUpgrade = [UpdatableItemCell loadFromNib];
    }
    else
    {
        self.briefCell = [GameTableViewCell loadFromNib];
    }
    NSMutableArray *sectionsArr = [NSMutableArray arrayWithObjects:[self cellsInActivitySection], nil];
    
    id obj = [self cellsInDesriptionSection];
    if (obj) {
        [sectionsArr addObject:obj];
        self.descriptionCell = [(NSArray *)obj lastObject];
    }
    
    obj = [self cellsInGuideSection];
    if (obj) {
        [sectionsArr addObject:obj];
    }
    
    obj = [self cellsInHotSpotSection];
    if (obj) {
        [sectionsArr addObject:obj ];
    }
    
    obj = [self cellsInStrategySection];
    if (obj) {
        [sectionsArr addObject:obj];
    }
    
    obj = [self cellsInforumSection];
    if (obj) {
        [sectionsArr addObject:obj];
    }
    if (self.descriptionCell != nil && [sectionsArr count] == 2) {
        self.onlyHaveDescripFlag = YES;
    }
    return sectionsArr;
}

- (NSMutableArray *)AllSectionsCellForInstall
{
    self.isUnInstalledFlag = NO;
    self.descripHideFlag = YES;
    NSMutableArray *descriptionSection = [self cellsInDesriptionSection];
    self.imgScroll = [self getScrollImageView];
    if (self.isSDKUpgrade) {
        self.briefCellForSDKUpgrade = [UpdatableItemCell loadFromNib];
    }
    else
    {
        self.briefCell = [GameTableViewCell loadFromNib];
    }

    NSMutableArray *sectionsArr = [NSMutableArray arrayWithObjects:[self cellsInActivitySection],nil];
    id obj = [self cellsInGuideSection];
    if (obj) {
        [sectionsArr addObject:obj];
    }
    
    obj = [self cellsInHotSpotSection];
    if (obj) {
        [sectionsArr addObject:obj ];
    }
    
    obj = [self cellsInStrategySection];
    if (obj) {
        [sectionsArr addObject:obj];
    }
    
    obj = [self cellsInforumSection];
    if (obj) {
        [sectionsArr addObject:obj];
    }
    [sectionsArr addObject:descriptionSection];
    
    self.descriptionCell = [descriptionSection lastObject];
    if ([sectionsArr count] != 2) {
        [descriptionSection removeLastObject];//隐藏简介
        self.imagesCell = [self getScrollImageCell];
        self.onlyHaveDescripFlag = NO;
    }else
    {
        self.onlyHaveDescripFlag = YES;
        [[descriptionSection objectAtIndex:0] removeExpendRightButton];
    }
  
    return sectionsArr;
}

#pragma mark - table view delegate
- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{

    if (section == 0 ) {
        return  SPACE_SECTIONS + 95 + 2 ;
    }
    if (section == 1 && self.isUnInstalledFlag && [self.detailInfo.softImages count] != 0) {
        return SPACE_SECTIONS + 255 + 2;
    }
    if (self.onlyHaveDescripFlag && [self.detailInfo.softImages count] != 0) {
        return SPACE_SECTIONS + 255 + 2;
    }
    return SPACE_SECTIONS;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return  1;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    if (section == 0) {
        
        if (self.isSDKUpgrade) {
            UpdatableItemCell *brief = self.briefCellForSDKUpgrade;
            UIView *view = [[[UIView alloc] initWithFrame:CGRectMake(0, 2, 320, brief.frame.size.height)] autorelease];
            [view addSubview:brief];
            return  view;
        }else
        {
            GameTableViewCell *brief = self.briefCell;
            UIView *view = [[[UIView alloc] initWithFrame:CGRectMake(0, 2, 320, brief.frame.size.height)] autorelease];
            [view addSubview:brief];
            return  view;
        }
        
//        [brief adjustForDetailViewCtrWithInfo:self.detailInfo];
//        brief.backgroundColor = [UIColor whiteColor];
       
    }
    if ((section == 1 && self.isUnInstalledFlag )|| (!self.isUnInstalledFlag && self.onlyHaveDescripFlag)) {
        UIScrollView *scroll = self.imgScroll;
        if (!scroll) {
            return  nil;
        }
        CGRect rc = scroll.frame;
        rc.origin.x = 4;
//        rc.size.width = 312;
        scroll.frame = rc;
        UIView *headerView = [[[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, SPACE_SECTIONS + scroll.frame.size.height)] autorelease];
        [headerView addSubview:scroll];
        headerView.backgroundColor = [UIColor clearColor];
        return headerView;
    }
    return nil;
}

- (void)expandDescriptionSection:(NSIndexPath *)indexPath
{

    NSMutableArray *descriptionSection = [self.sectionsArr lastObject];
    if (self.descripHideFlag) {
        if (self.imagesCell) {
            [descriptionSection addObject:self.imagesCell];
        }
        CGRect rc = self.descriptionCell.frame;
        rc.size.height -= 1;// FIXME: XX
        self.descriptionCell.frame = rc;
        [descriptionSection addObject:self.descriptionCell];
    }else
    {
        [descriptionSection removeLastObject];
        if (self.imagesCell) {
            [descriptionSection removeLastObject];

        }
    }
    FlexibleTextView *descripView = (FlexibleTextView *)[self.descriptionCell.contentView viewWithTag:DESCRIPVIEWTAG];
    if (!descripView.expendFlag) {
        [descripView performSelector:@selector(btnPress:) withObject:descripView.tapGest];
        descripView.expendView.hidden =YES;
    }
    self.descripHideFlag = !self.descripHideFlag;
    [self.detailTableView reloadData];
    [self.detailTableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionTop animated:NO];

}

#pragma mark - table view dataSource
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [self cellInCacheAtIndexPath:indexPath];
//    NSLog(@"%0.lf %@",cell.bounds.size.height,indexPath);
//
//    if (indexPath.row == 0) {
//        return cell.bounds.size.height - 1;// 第0个cell每次reload时不知道为什么会加1；
//    }
//    return cell.bounds.size.height;
    if (cell.bounds.size.height > 66) {
        return cell.bounds.size.height;
    }
    return 33;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return  [self.sectionsArr count];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[self.sectionsArr objectAtIndex:section] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return  [self cellInCacheAtIndexPath:indexPath];;
}

#pragma mark - create and get cell method
- (UITableViewCell *)cellInCacheAtIndexPath:(NSIndexPath *)indexPath
{
    NSArray *cellsArr = [self.sectionsArr objectAtIndex:indexPath.section];
    UITableViewCell *cell = [cellsArr objectAtIndex:indexPath.row];
    return cell;
}

- (NSArray *)cellsInBriefSection
{
    NSMutableArray *cellsArr = [[[NSMutableArray alloc] init] autorelease];
    GameDetailDownloadCell *cell0 = [GameDetailDownloadCell loadFromNib];
    [cellsArr addObject:cell0];
    return cellsArr;
}

- (CGRect)rectForThumbImageViewAtIndex:(int)index
{
    return CGRectMake(START_X + 170*index, 5, 160, 240);
}

- (NSArray *)cellsInActivitySection
{
    NSMutableArray *cellsArr = [[[NSMutableArray alloc] init] autorelease];
    NSArray *recommendList = [self getRecommendActivities:self.detailInfo.activityList];
    if ([recommendList count] == 0) {
        return cellsArr;
    }
    int cellCount = [recommendList count];
    if (cellCount > 3) {//最多显示3行
        cellCount = 3;
    }
    
    for (int i = 0; i < cellCount; i++) {
        ActivityItem *item = [recommendList objectAtIndex:i];
        BaseBorderlessCell *cell = [[[BaseBorderlessCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
        cell.bNeedRedBorder = YES;
        cell.backgroundColor = [UIColor clearColor];
        cell.textLabel.font = [UIFont systemFontOfSize:14.0];
        [self setActivityCell:cell withInfo:item];
        
        if (item.titleTagColor == nil) {
            cell.textLabel.textColor = [CommUtility colorWithHexRGB:@"333333"];
        } else {            
            cell.textLabel.textColor = [CommUtility colorWithHexRGB:item.titleTagColor];
        }
        
        cell.jumpUrl = item.contentUrl;
        UITapGestureRecognizer *gesture = [[UITapGestureRecognizer new] autorelease];
        [gesture addTarget:self action:@selector(jumpToActivityDetailCtr:)];
        [cell addGestureRecognizer:gesture];
        [cellsArr addObject:cell];

    }
    
    return cellsArr;
}

- (NSArray *)getRecommendActivities:(NSArray *)activityList
{
    NSMutableArray *recommendActivities = [[[NSMutableArray alloc] init] autorelease];
    for (ActivityItem *item in activityList) {
        if (item.isRecommend == 1) {
            [recommendActivities addObject:item];
        }
    }
    return recommendActivities;
}

- (UIScrollView *)getScrollImageView
{
    int count = [self.detailInfo.softImages count];
    if (self.detailInfo.softImages == nil || count == 0) {
        return nil;
    }
    UIScrollView *scroll = [[[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, 255)] autorelease];
    scroll.delegate = self;
    scroll.backgroundColor = [UIColor whiteColor];
    scroll.contentSize = CGSizeMake(170 * count + START_X, 240);
    
    for (int i = 0; i < count; i++) {
        UIImageView *pic = [[UIImageView alloc] init];
        NSString *url = [self.detailInfo.softImages objectAtIndex:i];
        pic.tag = TAG_ROTATE_IMAGE_IF_WIDTH_BIGGER_THEN_HEIGHT;
        [pic setImageWithURL:[NSURL URLWithString:url] placeholderImage:[UIImage imageNamed:@"default_screen_shot.jpg"]];
        pic.frame = [self rectForThumbImageViewAtIndex:i];
        [scroll addSubview:pic];
        [pic release];
        
        //touch to see big pic
        pic.userInteractionEnabled = YES;
        UITapGestureRecognizer *gesture = [[UITapGestureRecognizer new] autorelease];
        [gesture addTarget:self action:@selector(showBigPic:)];
        [pic addGestureRecognizer:gesture];
    }
    [scroll flashScrollIndicators];
    return scroll;
}

- (UITableViewCell *)getScrollImageCell
{
    UIScrollView *scrollView = self.imgScroll;
    if (!scrollView) {
        return nil;
    }
    CGRect rc = scrollView.frame;
    rc.size.width = 300;
    rc.origin.x = 1;
    scrollView.frame = rc;
    BaseBorderlessCell *cell = [[[BaseBorderlessCell alloc] init] autorelease];
    rc = cell.frame;
    rc.size.height = 255;
    cell.frame = rc;
    scrollView.contentOffset = self.scollOffset;
    [cell.contentView addSubview:scrollView];
    return cell;
}

- (int)indexForThumbImageView:(UIImageView *)imgView
{
    CGRect rt = imgView.frame;
    return (rt.origin.x - START_X) / 170;
}

- (void)showBigPic:(UITapGestureRecognizer *)gesture
{
    UIImageView *imgView = (UIImageView *)gesture.view;
    UIScrollView *scoll = (UIScrollView *) [imgView superview];
    self.scollOffset = scoll.contentOffset;
    int index = [self indexForThumbImageView:imgView];
    [FullScreenImageViewController show:index ofImages:self.detailInfo.softImages inController:self];
}

- (NSMutableArray *)cellsInDesriptionSection
{
    if (self.detailInfo.description == nil) {
        return nil;
    }
    NSMutableArray *cellsArr = [[[NSMutableArray alloc] init] autorelease];
    SectionHeadCell *headCell = [[[SectionHeadCell alloc] init] autorelease];
    headCell.textLabel.text = @"简介";
   
    [cellsArr addObject:headCell];
    
    if (!self.isUnInstalledFlag) {
        [headCell addexpendRightButtonWithTarget:self action:@selector(expend:) forControlEvents:UIControlEventTouchUpInside];
    }
    
    //去除前后的空行
    NSString *trimingStr = [self.detailInfo.appDescription stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];

    FlexibleTextView *desc = [FlexibleTextView flexibleTextViewWithText:trimingStr
                                                               originXY:CGPointMake(0, 0)
                                                               delegate:self];
   
    
    desc.tag = DESCRIPVIEWTAG;
    BaseBorderlessCell *cell = [[[BaseBorderlessCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.backgroundColor = [UIColor clearColor];
    
    if ([CommUtility isIOS7]) {//适配ios7 调整frame
        CGRect tempRect = desc.frame;
        tempRect.origin.x = ADAPT_X;
        desc.frame = tempRect;

    }
    
    [cell.contentView addSubview:desc];
    cell.frame = CGRectMake(0, 0, 320, CGRectGetHeight(desc.frame));
    [cellsArr addObject:cell];
    
    return cellsArr;
}

- (void)expend:(UITapGestureRecognizer *)tapGes
{
    UIView *view = (UIView *)tapGes.view;
    UILabel *label = (UILabel *)[view viewWithTag:EXPENDLABELTAG];
    UIImageView *imgView = (UIImageView *)[view viewWithTag:EXPENDIMAGE];
    
    UIView *contentView = (UIView *)[view superview];
    SectionHeadCell *cell = (SectionHeadCell *) [contentView superview];
    
    if (!self.descripHideFlag) {
        label.text = @"展开 ";
        imgView.image = [UIImage imageNamed:@"dropDown_Arrow"];
        cell.lineView.hidden = YES;
    }
    else
    {
        label.text = @"收起 ";
        imgView.image = [UIImage imageNamed:@"upArrow"] ;
        cell.lineView.hidden = NO;
    }
    
    
    CGRect rc = cell.frame;
    rc.size.height = 33;// FIXME: XXX
    cell.frame = rc;
    NSIndexPath *indexPath = [self.detailTableView indexPathForCell:cell];
    [self expandDescriptionSection:indexPath];
}

- (NSArray *)cellsInGuideSection
{
    int totalCount = [self.detailInfo.guideList count];
    if (totalCount == 0) {
        return nil;
    }
    
    NSMutableArray *cellsArr = [[[NSMutableArray alloc] init] autorelease];
    SectionHeadCell *headCell = [[SectionHeadCell alloc] init];
    headCell.textLabel.text = @"指引";
    headCell.textLabel.textColor = [CommUtility colorWithHexRGB:@"64b640"];
    headCell.lineView.backgroundColor = [CommUtility colorWithHexRGB:@"64b640"];
    [cellsArr addObject:headCell];
    
    
    int rowCount = totalCount / 4;
    if (totalCount % 4 != 0) {
        rowCount += 1;
    }
    for (int i = 0; i < rowCount; i++) {
        DetailViewGuideCell *cell = [DetailViewGuideCell loadFromNib];
        [cell updateWithInfos:self.detailInfo.guideList cellRowNum:i target:self];
        [cellsArr addObject:cell];
    }

    return cellsArr;
    
}

- (void)guideCellBtnPress:(id)sender
{
    UIButton *btn = (UIButton *)sender;
    NSInteger index = btn.tag;
    GuideItem *guideItem = [self.detailInfo.guideList objectAtIndex:index];
    
    GameDetailWebCtrl *webViewCtr = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:guideItem.guideUrl];
    webViewCtr.customTitle = guideItem.guideName;
    
    [ReportCenter report:ANALYTICS_EVENT_15063 label:self.detailInfo.identifier];
    [self.navigationController pushViewController:webViewCtr animated:YES];
}


- (NSArray *)cellsInHotSpotSection
{
    NSArray *unRecommendActivities = [self getUnRecommendActivities:self.detailInfo.activityList];
    int totalCount = [unRecommendActivities count];
    if (totalCount == 0) {
        return nil;
    }
    
    NSMutableArray *cellsArr = [[[NSMutableArray alloc] init] autorelease];
    SectionHeadCell *headCell = [[[SectionHeadCell alloc] init] autorelease];
    [headCell addMoreRightButtonWithTarget:self action:@selector(showMoreHotSpot:) forControlEvents:UIControlEventTouchUpInside];
    headCell.textLabel.text = @"热点";
    headCell.textLabel.textColor = [CommUtility colorWithHexRGB:@"ff3f3f"];
    headCell.lineView.backgroundColor = [CommUtility colorWithHexRGB:@"ff3f3f"];
    [cellsArr addObject:headCell];
    
    int cellsCount = totalCount;
    if (cellsCount > 4) {//最多显示4行
        cellsCount = 4;
    }
    
    for (int i = 0; i < cellsCount; i++) {
        
        ActivityItem *item = [unRecommendActivities objectAtIndex:i];
        BaseBorderlessCell *cell = [[[BaseBorderlessCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
        cell.backgroundColor = [UIColor clearColor];
        cell.textLabel.font = [UIFont systemFontOfSize:14.0];
        [self setActivityCell:cell withInfo:item];
        cell.jumpUrl = item.contentUrl;
        UITapGestureRecognizer *gesture = [[UITapGestureRecognizer new] autorelease];
        [gesture addTarget:self action:@selector(jumpToActivityDetailCtr:)];
        [cell addGestureRecognizer:gesture];
        [cellsArr addObject:cell];
        
    }
    
    return cellsArr;
    
}
- (void)setActivityCell:(BaseBorderlessCell *)cell withInfo:(ActivityItem *)item
{
    NSString *textTitle;
    UIImageView *typeImgView;
    switch (item.activityType) {
        case 1:
        {
            textTitle =[NSString stringWithFormat:@"礼包  |  %@",item.title];
            cell.jumpTitle = @"礼包";
            typeImgView = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"giftBag"]] autorelease];
        }
            break;
        case 2:
        {
            textTitle =[NSString stringWithFormat:@"活动  |  %@",item.title];
            cell.jumpTitle = @"活动";
            typeImgView = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"activity"]] autorelease];
        }
            
            break;
        case 3:
        {
            textTitle =[NSString stringWithFormat:@"公告  |  %@",item.title];
            cell.jumpTitle = @"公告";
            typeImgView = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"notice"]] autorelease];
        }
            
            break;
        case 4:
        {
            textTitle =[NSString stringWithFormat:@"开服  |  %@",item.title];
            cell.jumpTitle = @"开服";
           typeImgView = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"kaifu"]] autorelease];
        }
            
            break;
        default:
            break;
    }
    cell.accessoryView = typeImgView;
    cell.textLabel.text = textTitle;
}

- (NSArray *)getUnRecommendActivities:(NSArray *)activityList
{
    NSMutableArray *unRecommendActivities = [[[NSMutableArray alloc] init] autorelease];
    for (ActivityItem *item in activityList) {
        if (item.isRecommend != 1) {
            [unRecommendActivities addObject:item];
        }
    }
    return unRecommendActivities;
}

- (void)showMoreHotSpot:(id)sender
{
    ActivityCommonCtrl *moreAcitvityCtr = [[[ActivityCommonCtrl alloc] init] autorelease];
    moreAcitvityCtr.bNeedShowIcon = NO;
    moreAcitvityCtr.identifier = self.identifier;
    moreAcitvityCtr.tableStyle = UITableViewStylePlain;
    moreAcitvityCtr.customTitle = @"热点";
    [ReportCenter report:ANALYTICS_EVENT_15065 label:self.detailInfo.identifier];
    [self.navigationController pushViewController:moreAcitvityCtr animated:YES];
}

- (NSArray *)cellsInStrategySection
{
    
    int totalCount = [self.detailInfo.strategyList count];
    if (totalCount == 0) {
        return nil;
    }
    
    NSMutableArray *cellsArr = [[[NSMutableArray alloc] init] autorelease];
    SectionHeadCell *headCell = [[[SectionHeadCell alloc] init] autorelease];
    [headCell addMoreRightButtonWithTarget:self action:@selector(showMoreStrategy:) forControlEvents:UIControlEventTouchUpInside];
    headCell.textLabel.text = @"攻略";
    headCell.textLabel.textColor = [CommUtility colorWithHexRGB:@"7d24ab"];
    headCell.lineView.backgroundColor = [CommUtility colorWithHexRGB:@"7d24ab"];
    
    [cellsArr addObject:headCell];
    int cellsCount = totalCount;
    if (cellsCount > 4) {//最多显示4行
        cellsCount = 4;
    }
    
    for (int i = 0; i < cellsCount; i++) {
        StrategyItem *item = [self.detailInfo.strategyList objectAtIndex:i];
        BaseBorderlessCell *cell = [[[BaseBorderlessCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
        cell.backgroundColor = [UIColor clearColor];
        cell.textLabel.font = [UIFont systemFontOfSize:14.0];
        cell.textLabel.text = item.strategyName;
        cell.jumpUrl = item.strategyUrl;
        cell.jumpTitle = headCell.textLabel.text;
        UITapGestureRecognizer *gesture = [[UITapGestureRecognizer new] autorelease];
        [gesture addTarget:self action:@selector(jumpToNewCtr:)];
        [cell addGestureRecognizer:gesture];
        [cellsArr addObject:cell];
    }

    return cellsArr;
}

- (void)showMoreStrategy:(id)sender
{
    if (self.detailInfo.strategyUrl == nil) {
        self.showMoreStragy = YES;
        return;
    }
    GameDetailWebCtrl *webViewCtr = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:self.detailInfo.strategyUrl];
    webViewCtr.customTitle = @"攻略";
    [ReportCenter report:ANALYTICS_EVENT_15067 label:self.detailInfo.identifier];
    [self.navigationController pushViewController:webViewCtr animated:YES];

}

- (void)gotoGameMoredetail:(NSString *)more
{

    if ([more isEqualToString:@"activity"]) {
        [self showMoreHotSpot:nil];
        //        [gameDetail performSelector:@selector(showMoreHotSpot:) withObject:nil afterDelay:0.5];
        
    }
    else if([more isEqualToString:@"bbs"])
    {
        [self showMoreForum:nil];
    }
    else if([more isEqualToString:@"strategy"])
    {
        [self showMoreStrategy:nil];
        
    }if ([more isEqualToString:@"download"]) {
        
    }

}

- (NSArray *)cellsInforumSection
{
    
    int totalCount = [self.detailInfo.forumList count];
    if (totalCount == 0) {
        return nil;
    }
    
    NSMutableArray *cellsArr = [[[NSMutableArray alloc] init] autorelease];
    SectionHeadCell *headCell = [[[SectionHeadCell alloc] init] autorelease];
    [headCell addMoreRightButtonWithTarget:self action:@selector(showMoreForum:) forControlEvents:UIControlEventTouchUpInside];
    headCell.textLabel.text = @"论坛";
    headCell.textLabel.textColor = [CommUtility colorWithHexRGB:@"f7b109"];
    headCell.lineView.backgroundColor = [CommUtility colorWithHexRGB:@"f7b109"];
    [cellsArr addObject:headCell];
    int cellsCount = totalCount;
    if (cellsCount > 4) {//最多显示4行
        cellsCount = 4;
    }
    
    for (int i = 0; i < cellsCount; i++) {
        
        ForumItem *item = [self.detailInfo.forumList objectAtIndex:i];
        BaseBorderlessCell *cell = [[[BaseBorderlessCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
        cell.backgroundColor = [UIColor clearColor];
        cell.textLabel.font = [UIFont systemFontOfSize:14.0];
        cell.textLabel.text = item.forumName;
        cell.jumpUrl = item.forumUrl;
        cell.jumpTitle = headCell.textLabel.text;
        UITapGestureRecognizer *gesture = [[UITapGestureRecognizer new] autorelease];
        [gesture addTarget:self action:@selector(jumpToNewCtr:)];
        [cell addGestureRecognizer:gesture];
        [cellsArr addObject:cell];
        
    }
    
    return cellsArr;
}

- (void)showMoreForum: (id)sender
{
    if (self.detailInfo.forumUrl == nil) {
        self.showMoreForum = YES;
        return;
    }
    GameDetailWebCtrl *webViewCtr = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:self.detailInfo.forumUrl];
    webViewCtr.customTitle = @"论坛";
    [ReportCenter report:ANALYTICS_EVENT_15069 label:self.detailInfo.identifier];
    [self.navigationController pushViewController:webViewCtr animated:YES];
}

- (void)jumpToNewCtr:(UITapGestureRecognizer *)gesture
{
    BaseBorderlessCell *cell = (BaseBorderlessCell *)gesture.view;
    cell.selected = YES;
    GameDetailWebCtrl *webViewCtr = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:cell.jumpUrl];
    webViewCtr.customTitle = cell.jumpTitle;
    if ([cell.jumpTitle isEqualToString:@"攻略"]) {
        [ReportCenter report:ANALYTICS_EVENT_15066 label:self.detailInfo.identifier];
    }
    if ([cell.jumpTitle isEqualToString:@"论坛"]) {
        [ReportCenter report:ANALYTICS_EVENT_15068 label:self.detailInfo.identifier];
    }
    [self.navigationController pushViewController:webViewCtr animated:YES];
}

- (void)jumpToActivityDetailCtr:(UITapGestureRecognizer *)gesture
{
    BaseBorderlessCell *cell = (BaseBorderlessCell *)gesture.view;
    cell.selected = YES;
    ActivityDetailCtrl *ctrl = [[[ActivityDetailCtrl alloc] init] autorelease];
    ctrl.contentUrl = cell.jumpUrl;
    ctrl.appIdentifier = self.detailInfo.identifier;
    ctrl.activityId = cell.activityId;
    ctrl.customTitle = cell.jumpTitle;
    ctrl.hidesBottomBarWhenPushed = YES;
    [ReportCenter report:ANALYTICS_EVENT_15064 label:self.detailInfo.identifier];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)flexibleTextViewFrameChanged:(NSNumber *)offset
{
    UITableViewCell *cell;
    if (self.isUnInstalledFlag) {
        cell = [[self.sectionsArr objectAtIndex:1] objectAtIndex:1];
    }else if ([self.detailInfo.softImages count] != 0 && !self.onlyHaveDescripFlag)
    {
        cell = [[self.sectionsArr lastObject] objectAtIndex:2];
    }else
    {
        cell = [[self.sectionsArr lastObject] objectAtIndex:1];

    }
    CGRect rect = cell.frame;
    rect.size.height += [offset floatValue];
    rect.size.height -= 1;// FIXME: XXX
    NSLog(@"%0.lf",rect.size.height);
    cell.frame = rect;
    
    [self.detailTableView reloadData];
}

#pragma mark - scroll delegate

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
//    self.scollOffset = scrollView.contentOffset;

}

#pragma mark - GetAppDetailViewInfoProtocol
- (void)operation:(GameCenterOperation *)operation getAppDetailViewInfoDidFinish:(NSError *)error appDetailViewInfo:(AppDetailViewInfo *)detailViewInfo
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];

    if (error == nil && detailViewInfo != nil) {
        self.detailInfo = detailViewInfo;
        self.customTitle = self.detailInfo.appName;
        self.sectionsArr = [[SoftManagementCenter sharedInstance] isAnInstalledGame:self.detailInfo.identifier] ?  [self AllSectionsCellForInstall]: [self AllSectionsCellForUnInstall];
        
        if (self.isSDKUpgrade) {
            [MBProgressHUD showHUDAddedTo:self.view animated:YES];
            SoftItem *soft = [[SoftItem new] autorelease];
            soft.identifier = detailViewInfo.identifier;
            soft.localVersion = [CommUtility getInstallAppVerByIdentifier:detailViewInfo.identifier];
            soft.localShortVersion = [CommUtility getInstallAppShortVerByIdentifier:detailViewInfo.identifier];
        
            [RequestorAssistant requestAppLatestVersion:[NSArray arrayWithObject:soft] delegate:self];


            
        }else
        {
            [self.briefCell adjustForDetailViewCtrWithInfo:detailViewInfo];
            self.briefCell.backgroundColor = [UIColor whiteColor];
            
            [self.detailTableView reloadData];
            [DownloadButtonBar downloadBarWithAppIdentifier:self.identifier appDetailInfo:detailViewInfo superView:self.view upperView:self.detailTableView showUpgreda:self.isSDKUpgrade];

        }
        
        
        if (self.onlyHaveDescripFlag) {
            FlexibleTextView *descripView = (FlexibleTextView *)[self.descriptionCell.contentView viewWithTag:DESCRIPVIEWTAG];
            if (!descripView.expendFlag) {
                [descripView performSelector:@selector(btnPress:) withObject:descripView.tapGest];
                descripView.expendView.hidden =YES;
            }
            
        }
      

        
        //是否跳到更多
        if (self.showMoreStragy) {
            [self showMoreStrategy:nil];
            self.showMoreStragy = NO;
        }else if (self.showMoreForum)
        {
            [self showMoreForum:nil];
            self.showMoreForum = NO;
        }
    }
    else
    {
        [MBProgressHUD showHintHUD:@"获取游戏详情失败" message:[error localizedDescription] hideAfter:DEFAULT_TIP_LAST_TIME];

    }
}

//获取游戏最新版本回调
- (void)operation:(GameCenterOperation *)operation getAppLastedVersionDidFinish:(NSError *)error appList:(NSArray *)appList
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    if (error == nil && [appList count] == 1)
    {
        
        [[SoftManagementCenter sharedInstance] increUpdatableDic:[appList objectAtIndex:0]];
        
        SoftItem *item = [[SoftManagementCenter sharedInstance] updatableSoftItemForIdentifier:self.identifier];
        [self.briefCellForSDKUpgrade setSoftInfo:item];
        self.briefCellForSDKUpgrade.backgroundColor = [UIColor whiteColor];
        [self.briefCellForSDKUpgrade adjustForDetailViewWithInfo:self.detailInfo];
        [self.detailTableView reloadData];

        [DownloadButtonBar downloadBarWithAppIdentifier:self.identifier appDetailInfo:self.detailInfo superView:self.view upperView:self.detailTableView showUpgreda:self.isSDKUpgrade];
    }
    if (error == nil && [appList count] == 0) {
        
        if ([CommUtility getInstallAppShortVerByIdentifier:self.identifier] != nil) {
            SoftItem *soft = [[SoftItem new] autorelease];
            soft.identifier = self.detailInfo.identifier;
            soft.localVersion = [CommUtility getInstallAppVerByIdentifier:self.detailInfo.identifier];
            soft.localShortVersion = [CommUtility getInstallAppShortVerByIdentifier:self.detailInfo.identifier];
            [[SoftManagementCenter sharedInstance] increInstalledDic:soft];
        }
        self.isSDKUpgrade = NO;
        self.sectionsArr = [[SoftManagementCenter sharedInstance] isAnInstalledGame:self.detailInfo.identifier] ?  [self AllSectionsCellForInstall]: [self AllSectionsCellForUnInstall];
        [self.briefCell adjustForDetailViewCtrWithInfo:self.detailInfo];
        self.briefCell.backgroundColor = [UIColor whiteColor];
        [self.detailTableView reloadData];
        [DownloadButtonBar downloadBarWithAppIdentifier:self.identifier appDetailInfo:self.detailInfo superView:self.view upperView:self.detailTableView showUpgreda:self.isSDKUpgrade];
    }
    if (error != nil) {
        [MBProgressHUD showHintHUD:@"获取软件升级信息失败" message:@"请检查网络后重试" hideAfter:DEFAULT_TIP_LAST_TIME];
    }
}

@end
