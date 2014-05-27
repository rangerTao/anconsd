//
//  ViewController.m
//  TestSns
//
//  Created by Teng Yongxiang on 13-12-31.
//  Copyright (c) 2013年 TengYongxiang. All rights reserved.
//

#import "ViewController.h"
#define kTextFieldTag 100

@interface ViewController ()<UITableViewDataSource,UITableViewDelegate,UIAlertViewDelegate>
@property (nonatomic, retain) NSArray *dataArr;
@property (nonatomic, retain) NSArray *actionArr;
@end

@implementation ViewController

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
    self.dataArr = [NSArray arrayWithObjects:[NSArray arrayWithObjects:@"游戏",@"活动",@"管理", nil],
                    [NSArray arrayWithObjects:@"游戏专区",@"游戏升级", nil],
                    [NSArray arrayWithObjects:@"礼包详情",@"开服详情",@"活动详情",@"游戏专题",nil],
                    nil];
    
    self.actionArr = [NSArray arrayWithObjects:[NSArray arrayWithObjects:@"goGameTab",@"goActivityTab",@"goManagerTab", nil],
                      [NSArray arrayWithObjects:@"goGameDetail",@"goSDKGrade", nil],
                      [NSArray arrayWithObjects:@"goGiftDetail",@"goKaifuDetail",@"goActivityDetail",@"goTopic",nil],
                      nil];
    
    UITableView *tableView = [[[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStyleGrouped] autorelease];
    tableView.dataSource = self;
    tableView.delegate = self;
    [self.view addSubview:tableView];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[self.dataArr objectAtIndex:section] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:nil];
    NSString *title = [[self.dataArr objectAtIndex:indexPath.section] objectAtIndex:indexPath.row];
    cell.textLabel.text = title;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [self.dataArr count];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *selecerStr = [[self.actionArr objectAtIndex:indexPath.section] objectAtIndex:indexPath.row];
    [self performSelector:NSSelectorFromString(selecerStr)];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)goGameTab
{
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91://"]]) {
        NSString *myUrlScheme = @"91-com.nd.TestSns";
        NSURL *tarUrl = [NSURL URLWithString:
                         [NSString stringWithFormat:@"gamecenter91://?Do=gameTab&Callback=%@", myUrlScheme]];
        if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
            [[UIApplication sharedApplication] openURL:tarUrl];
        }
    }
}

- (void)goActivityTab
{
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91://"]]) {
        NSString *myUrlScheme = @"91-com.nd.TestSns";
        NSURL *tarUrl = [NSURL URLWithString:
                         [NSString stringWithFormat:@"gamecenter91://?Do=activitytab&Callback=%@", myUrlScheme]];
        if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
            [[UIApplication sharedApplication] openURL:tarUrl];
        }
    }
    
}

- (void)goManagerTab
{
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91://"]]) {
        NSString *myUrlScheme = @"91-com.nd.TestSns";
        NSURL *tarUrl = [NSURL URLWithString:
                         [NSString stringWithFormat:@"gamecenter91://?Do=managertab&Callback=%@", myUrlScheme]];
        if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
            [[UIApplication sharedApplication] openURL:tarUrl];
        }
    }
}

- (void)goGameDetail {
    
    UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:nil message:nil delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"下载页",@"活动",@"攻略",@"论坛", nil] autorelease];
    [alert show];
    
}

- (void)goDownload
{
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91://"]]) {
        int iAppId = 102455;//[[NdComPlatform defaultPlatform] appId];
        //        NSDictionary *plistDic = [[NSBundle mainBundle] infoDictionary];
        NSString *myUrlScheme = @"91-com.nd.TestSns";
        //        NSString *myUrlScheme = [[NdComPlatform defaultPlatform] sdkUrlSchemeForCurrentApplication];
        NSURL *tarUrl = [NSURL URLWithString:
                         [NSString stringWithFormat:@"gamecenter91://?Do=Detail&TargetType=GameDetail&TargetAction=%d&Callback=%@#download", iAppId, myUrlScheme]];
        if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
            [[UIApplication sharedApplication] openURL:tarUrl];
        }
    }
    
}

- (void)goHotSpot
{
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91://"]]) {
        int iAppId = 102455;//[[NdComPlatform defaultPlatform] appId];
        //        NSDictionary *plistDic = [[NSBundle mainBundle] infoDictionary];
        NSString *myUrlScheme = @"91-com.nd.TestSns";
        //        NSString *myUrlScheme = [[NdComPlatform defaultPlatform] sdkUrlSchemeForCurrentApplication];
        NSURL *tarUrl = [NSURL URLWithString:
                         [NSString stringWithFormat:@"gamecenter91://?Do=Detail&TargetType=GameDetail&TargetAction=%d&Callback=%@#activity", iAppId, myUrlScheme]];
        if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
            [[UIApplication sharedApplication] openURL:tarUrl];
        }
    }
}

- (void)goForum
{
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91://"]]) {
        int iAppId = 102455;//[[NdComPlatform defaultPlatform] appId];
        //        NSDictionary *plistDic = [[NSBundle mainBundle] infoDictionary];
        NSString *myUrlScheme = @"91-com.nd.TestSns";
        //        NSString *myUrlScheme = [[NdComPlatform defaultPlatform] sdkUrlSchemeForCurrentApplication];
        NSURL *tarUrl = [NSURL URLWithString:
                         [NSString stringWithFormat:@"gamecenter91://?Do=Detail&TargetType=GameDetail&TargetAction=%d&Callback=%@#bbs", iAppId, myUrlScheme]];
        if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
            [[UIApplication sharedApplication] openURL:tarUrl];
        }
    }
}- (void)goStrategy
{
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91://"]]) {
        int iAppId = 102455;//[[NdComPlatform defaultPlatform] appId];
        //        NSDictionary *plistDic = [[NSBundle mainBundle] infoDictionary];
        NSString *myUrlScheme = @"91-com.nd.TestSns";
        //        NSString *myUrlScheme = [[NdComPlatform defaultPlatform] sdkUrlSchemeForCurrentApplication];
        NSURL *tarUrl = [NSURL URLWithString:
                         [NSString stringWithFormat:@"gamecenter91://?Do=Detail&TargetType=GameDetail&TargetAction=%d&Callback=%@#strategy", iAppId, myUrlScheme]];
        if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
            [[UIApplication sharedApplication] openURL:tarUrl];
        }
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if ([[alertView buttonTitleAtIndex:buttonIndex] isEqualToString:@"确定"]) {
        
        UITextField *appIdTextField = (UITextField *)[alertView viewWithTag:kTextFieldTag];
        NSString *appIdStr = appIdTextField.text;
        if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91Update://"]]) {
            int iAppId = [appIdStr integerValue];
            //        NSDictionary *plistDic = [[NSBundle mainBundle] infoDictionary];
            NSString *myUrlScheme = @"91-com.nd.TestSns";
            //        NSString *myUrlScheme = [[NdComPlatform defaultPlatform] sdkUrlSchemeForCurrentApplication];
            NSURL *tarUrl = [NSURL URLWithString:
                             [NSString stringWithFormat:@"gamecenter91Update://?Do=Detail&TargetType=sdkgrade&TargetAction=%d&Callback=%@", iAppId, myUrlScheme]];
            if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
                [[UIApplication sharedApplication] openURL:tarUrl];
            }
        }
        
    }
    else
    {
        switch (buttonIndex) {
            case 1:
                [self goDownload];
                break;
            case 2:
                [self goHotSpot];
                break;
            case 3:
                [self goStrategy];
                break;
            case 4:
                [self goForum];
                break;
            default:
                break;
        }
    }
    NSLog(@"%d %@",buttonIndex,[alertView buttonTitleAtIndex:buttonIndex]);
}

- (void)goSDKGrade {
    UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:@"请输入游戏id" message:@"\n\n" delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil] autorelease];
    
    UITextField *textField = [[[UITextField alloc] initWithFrame:CGRectMake(27.0, 60.0, 230.0, 25.0)] autorelease];
    textField.keyboardType = UIKeyboardTypeNumberPad;
    textField.text = @"108644";
    textField.backgroundColor = [UIColor whiteColor];
    textField.tag = kTextFieldTag;
    [alert addSubview:textField];
    
    [alert show];
    
    
}
- (void)goGiftDetail {
    
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91://"]]) {
        //        int iAppId = 100586;//[[NdComPlatform defaultPlatform] appId];
        NSString *action = @"100586,2799";
        NSString *title = @"";
        NSString *contentUrl = @"http://newgamecenter.sj.91.com/ServiceV2/Activity/Detail/2447/?ok=1";
        NSString *myUrlScheme = @"91-com.nd.TestSns";
        NSURL *tarUrl = [NSURL URLWithString:
                         [NSString stringWithFormat:@"gamecenter91://?Do=Detail&TargetType=giftdetail&TargetAction=%@&Callback=%@&Targettitle=%@&Targetactionurl=%@", action, myUrlScheme,title,contentUrl]];
        if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
            [[UIApplication sharedApplication] openURL:tarUrl];
        }
    }
    
}
- (void)goKaifuDetail {
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91://"]]) {
        //        int iAppId = 100586;//[[NdComPlatform defaultPlatform] appId];
        NSString *action = @"100586,2701";
        NSString *title = @"";
        NSString *contentUrl = @"http://newgamecenter.sj.91.com/ServiceV2/Activity/Detail/2825/?ok=1";
        NSString *myUrlScheme = @"91-com.nd.TestSns";
        NSURL *tarUrl = [NSURL URLWithString:
                         [NSString stringWithFormat:@"gamecenter91://?Do=Detail&TargetType=kaifuDetail&TargetAction=%@&Callback=%@&Targettitle=%@&Targetactionurl=%@", action, myUrlScheme,title,contentUrl]];
        if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
            [[UIApplication sharedApplication] openURL:tarUrl];
        }
    }
    
}
- (void)goActivityDetail {
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91://"]]) {
        //        int iAppId = 102556;//[[NdComPlatform defaultPlatform] appId];
        NSString *action = @"100586,2300";
        NSString *title = @"";
        NSString *contentUrl = @"http://newgamecenter.sj.91.com/ServiceV2/Activity/Detail/2744/?ok=1";
        NSString *myUrlScheme = @"91-com.nd.TestSns";
        NSURL *tarUrl = [NSURL URLWithString:
                         [NSString stringWithFormat:@"gamecenter91://?Do=Detail&TargetType=activitydetail&TargetAction=%@&Callback=%@&Targettitle=%@&Targetactionurl=%@", action, myUrlScheme,title,contentUrl]];
        if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
            [[UIApplication sharedApplication] openURL:tarUrl];
        }
    }
    
}


- (void)goTopic{
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"gamecenter91://"]]) {
        int topicId = 29;//[[NdComPlatform defaultPlatform] appId];
        //        NSDictionary *plistDic = [[NSBundle mainBundle] infoDictionary];
        NSString *myUrlScheme = @"91-com.nd.TestSns";
        //        NSString *myUrlScheme = [[NdComPlatform defaultPlatform] sdkUrlSchemeForCurrentApplication];
        NSURL *tarUrl = [NSURL URLWithString:
                         [NSString stringWithFormat:@"gamecenter91://?Do=Detail&TargetType=gametopic&TargetAction=%d&Callback=%@", topicId, myUrlScheme]];
        if ([[UIApplication sharedApplication] canOpenURL:tarUrl]) {
            [[UIApplication sharedApplication] openURL:tarUrl];
        }
    }
    
}

@end
