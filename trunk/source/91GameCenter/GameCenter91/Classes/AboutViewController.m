//
//  AboutViewController.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-11-19.
//
//

#import "AboutViewController.h"
#import "UIViewController+Extent.h"
#import "CommUtility.h"
#import "BaseBorderlessCell.h"
#import "IntroductionViewCtr.h"
#import <NdComPlatform/NdComPlatform.h>
#import "NdAppUpdateHelper+HOOK.h"
#import "MBProgressHUD.h"
#import "StatusBarNotification.h"
#import "CustomAlertView.h"
#import "UIAlertView+Blocks.h"
#import "RIButtonItem.h"
#import "Colors.h"
#import "UIImage+Extent.h"

@interface AboutViewController ()<UITableViewDataSource,UITableViewDelegate>
@property (retain, nonatomic) IBOutlet UILabel *versionLabel;
@property (nonatomic, assign) CGFloat rowHeight;
@end

@implementation AboutViewController
- (void)dealloc {
    [_versionLabel release];
    [super dealloc];
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.customTitle = @"关于";
    
    NSDictionary *plistDic = [[NSBundle mainBundle] infoDictionary];
    NSString *str = @"V";
    NSString *version = [plistDic objectForKey:@"CFBundleVersion"];
    self.versionLabel.text = [str stringByAppendingString:version];
    self.versionLabel.textAlignment = NSTextAlignmentCenter;
    
    self.view.backgroundColor = [CommUtility colorWithHexRGB:@"d9dbdc"];
    
    self.rowHeight = 50;
    UITableView *tableView = [[[UITableView alloc] initWithFrame:CGRectMake(0, 180, 320, self.view.frame.size.height - 180) style:UITableViewStyleGrouped] autorelease];
    tableView.dataSource = self;
    tableView.delegate = self;
    tableView.scrollEnabled = NO;
    tableView.separatorColor = [UIColor clearColor];
    UIView *backGroundView = [[UIView new] autorelease];
    backGroundView.backgroundColor = [CommUtility colorWithHexRGB:@"d9dbdc"];
    [tableView setBackgroundView:backGroundView];
    [self.view addSubview:tableView];
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 3;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    BaseBorderlessCell *cell = [[[BaseBorderlessCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil] autorelease];
    CGRect rc = cell.frame;
    rc.size.height = self.rowHeight;
    cell.frame = rc;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    cell.textLabel.font = [UIFont systemFontOfSize:16];
    
    //选中时背景色
    UIView *myBackView = [[[UIView alloc] initWithFrame:cell.frame] autorelease];
    myBackView.backgroundColor = [CommUtility colorWithHexRGB:CELL_SELECTED_COLOR];
    cell.selectedBackgroundView = myBackView;
    
    switch (indexPath.row) {
        case 0:
        {
            UIImageView *backgroundImgView = [[[UIImageView alloc] initWithFrame:cell.bounds] autorelease];
            backgroundImgView.image = [[UIImage imageNamed:@"bordless_first_row"] stretchableImageWithCenterPoint];
            
            
            
//            cell.backgroundView = backgroundImgView;
            cell.textLabel.text = @"检查更新";
            
        }
            
            break;
        case 1:
        {
            cell.textLabel.text = @"功能介绍";
            UIImageView *backgroundImgView = [[[UIImageView alloc] initWithFrame:cell.bounds] autorelease];
            backgroundImgView.image = [[UIImage imageNamed:@"bordless_middle_row"] stretchableImageWithCenterPoint];
//            cell.backgroundView = backgroundImgView;

        }
            break;
        case 2:
        {
            cell.textLabel.text = @"意见反馈";
            UIImageView *backgroundImgView = [[[UIImageView alloc] initWithFrame:cell.bounds] autorelease];
            backgroundImgView.image = [[UIImage imageNamed:@"bordless_last_row"] stretchableImageWithCenterPoint];
//            cell.backgroundView = backgroundImgView;

        }
            break;
            
        default:
            break;
    }
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return self.rowHeight;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    switch (indexPath.row) {
        case 0:
            [self checkUpdate];
            break;
        case 1:
            [self introdution];
            break;
        case 2:
            [self feedBack];
            break;
            
        default:
            break;
    }
    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (void)checkUpdate
{
    [[NdComPlatform defaultPlatform] NdAppVersionUpdate:0 delegate:self];

}

- (void)appVersionUpdateDidFinish:(ND_APP_UPDATE_RESULT)updateResult
{
    NSLog(@"update result %d", updateResult);
    if (updateResult == 0) {
        RIButtonItem *okItem = [RIButtonItem itemWithLabel:@"确定"];
        NSString *text = [NSString stringWithFormat:@"您的91游戏中心是最新版本"];
        CustomAlertView *alert = [[CustomAlertView alloc] initWithTitle:text message:nil cancelButtonItem:okItem otherButtonItems: nil];
        [alert show];
        [alert release];

        
    }
}

- (void)introdution
{
    IntroductionViewCtr *ctr = [[IntroductionViewCtr new] autorelease];
    [self.navigationController pushViewController:ctr animated:YES];
}

- (void)feedBack
{
    if ([[NdComPlatform defaultPlatform] isLogined]) {
        [[NdComPlatform defaultPlatform] NdUserFeedBack];
    }else
    {
        [[NdComPlatform defaultPlatform] NdSwitchAccount];
    }

}

- (void)viewDidUnload {
    [self setVersionLabel:nil];
    [super viewDidUnload];
}
@end
