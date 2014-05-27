//
//  ActivityAndTaskJumpBar.m
//  GameCenter91
//
//  Created by Sun pinqun on 12-12-12.
//
//

#import "ActivityAndTaskJumpBar.h"
#import "GameTableViewCell.h"
#import "ActivityTableViewCell.h"
#import "UITableViewCell+Addition.h"
#import "UIViewController+Extent.h"
#import "ActivityInfo.h"
#import "CommUtility.h"
#import "UserData.h"
#import "SoftItem.h"
#import "ProgessButton.h"
#import "Notifications.h"
#import "GameDetailController.h"
#import "ActivityDetailCtrl.h"
#import "AppDescriptionInfo.h"
#import "AssigningControlBackgroundView.h"

#define kGameJumpBarHeight              [GameTableViewCell cellHeight]
#define kActivityJumpBarHeight          60.f
#define kIncompatibleGameJumpBarHeight  44.f

@interface ActivityAndTaskJumpBar()
@property (nonatomic, assign) NSInteger jumpType;
@end

@implementation ActivityAndTaskJumpBar
@synthesize jumpType;
@synthesize appId, activityUrl, activityName, activityDetail, targetId, targetAppId;
@synthesize appInfo;

- (id)initWithView:(UIView *)view jumpType:(JumpBarType)type
{
    float height;
    switch (type) {
        case TYPE_TASK_TO_ACTIVITY:
            height = kActivityJumpBarHeight;
            break;
        case TYPE_INCOMPATIBLE_GAME:
            height = kIncompatibleGameJumpBarHeight;
            break;
        default:
            height = kGameJumpBarHeight-2;//减2为了隐藏标签
            break;
    }
    CGRect frame = CGRectMake(0.f, view.frame.size.height - height, view.frame.size.width, height);
    self = [super initWithFrame:frame style:UITableViewStylePlain];
    if (self) {
        self.scrollEnabled = NO;
        self.delegate = self;
        self.dataSource = self;
        self.jumpType = type;
        self.autoresizingMask = UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin;
        
        //侦听下载按钮需要的消息
        if (self.jumpType == TYPE_ACTIVITY_TO_GAME || self.jumpType == TYPE_TASK_TO_GAME){
            [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadQueueChanged:) name:kGC91DownloadQueueChangeNotification object:nil];
            [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(downloadPercentChanged:) name:kGC91DownloadPercentChangeNotification object:nil];
        }
    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    self.activityUrl = nil;
    self.activityDetail = nil;
    self.activityName = nil;
    self.appInfo = nil;
    [super dealloc];
}

- (float)JumpBarHeight
{
    if (self.jumpType == TYPE_TASK_TO_ACTIVITY) 
        return kActivityJumpBarHeight;
    else if (self.jumpType == TYPE_INCOMPATIBLE_GAME)
        return kIncompatibleGameJumpBarHeight;
    else
        return kGameJumpBarHeight; 
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [self JumpBarHeight];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = nil;
    if (self.jumpType == TYPE_ACTIVITY_TO_GAME || self.jumpType == TYPE_TASK_TO_GAME) {
        GameTableViewCell *gameCell = [GameTableViewCell loadFromNib];
        
        gameCell.textLabel.text = @"";
		gameCell.detailTextLabel.text = @"";
        [gameCell.gameStateButton reset];
        [gameCell updateCellWithAppInfo:self.appInfo];
    
        cell = gameCell;
    }
    else if (self.jumpType == TYPE_INCOMPATIBLE_GAME) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@""] autorelease];
        cell.textLabel.backgroundColor = [UIColor clearColor];
        cell.textLabel.textAlignment = UITextAlignmentCenter;
        cell.textLabel.font = [UIFont systemFontOfSize:16];
        cell.textLabel.text = @"您的手机固件版本不支持这款游戏哦!";
    }
    else {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@""] autorelease];
        cell.textLabel.backgroundColor = [UIColor clearColor];
        cell.textLabel.text = self.activityName;
        cell.detailTextLabel.backgroundColor = [UIColor clearColor];
        cell.detailTextLabel.text = self.activityDetail;
        
        UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
        btn.frame = CGRectMake(0, 0, 80, 35);
        [btn setBackgroundImage:[UIImage imageNamed:@"btn_blue.png"] forState:UIControlStateNormal];
        btn.titleLabel.font = [UIFont boldSystemFontOfSize:15];
        [btn setTitle:@"马上查看" forState:UIControlStateNormal];
        cell.accessoryView = btn;
        [btn addTarget:self action:@selector(gotoActivityDetail:) forControlEvents:UIControlEventTouchUpInside];
    }
    
    UIImage *img = [UIImage imageNamed:@"bg_top.png"];
    img = [img stretchableImageWithLeftCapWidth:img.size.width/2 topCapHeight:img.size.height/2];
    cell.backgroundView = [[[UIImageView alloc] initWithImage:img] autorelease];
//    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    [AssigningControlBackgroundView assignCellSelectedBackgroundView:cell];
    
    return cell;
}


#pragma mark - Table view delegate

- (UIViewController*)getParentController:(UIView *)view {
    for (UIView* next = [view superview]; next; next = next.superview) {
        UIResponder* nextResponder = [next nextResponder];
        if ([nextResponder isKindOfClass:[UIViewController class]]) {
            return (UIViewController*)nextResponder;
        }
    }
    return nil;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    UIViewController *parentCtr = [self getParentController:self];
    if (parentCtr == nil)
        return;
    
    if (self.jumpType == TYPE_ACTIVITY_TO_GAME || self.jumpType == TYPE_TASK_TO_GAME) {
        //跳转到游戏详情
        [CommUtility pushGameDetailController:self.appInfo.identifier gameName:self.appInfo.appName navigationController:parentCtr.navigationController];
    }
}

#pragma mark -

- (void)downloadQueueChanged:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[[aNotify userInfo] objectForKey:@"ITEM"];
    if (item) {
        if ([item.identifier isEqualToString:self.appInfo.identifier]) {
            [self reloadData];
        }
    }
}

- (void)downloadPercentChanged:(NSNotification *)aNotify
{
    SoftItem *item = (SoftItem *)[aNotify object];
    if (item){
        if ([item.identifier isEqualToString:self.appInfo.identifier]) {
            for (UITableViewCell *cell in self.visibleCells) {
                if ([cell isKindOfClass:[GameTableViewCell class]]) {
                    GameTableViewCell *aCell = (GameTableViewCell *)cell;
                    if ([aCell.gameStateButton.identifier isEqualToString:item.identifier]) {
                        [aCell updateBtnState:item];
                    }
                }
            }
        }
    }
}

- (void)gotoActivityDetail:(id)sender
{
//    UIViewController *parentCtr = [self getParentController:self];
//    if (parentCtr == nil)
//        return;
//    
//    //跳转到活动详情
//    int activityID = self.targetId;
//    NSString *title = self.activityName;
//    int appid = self.targetAppId;
//    NSString *contentUrl = self.activityUrl;
//    
//    [CommUtility pushActivityDetailCtrl:appid activityId:activityID activityUrl:contentUrl activityTitle:title navigationController:parentCtr.navigationController];
}

@end
