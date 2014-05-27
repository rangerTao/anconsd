//
//  AboutViewController.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-11-13.
//
//

#import "IntroductionViewCtr.h"
#import "GameDetailWebCtrl.h"
#import <QuartzCore/CALayer.h>
#import "AttributedLabel.h"
#import "UIViewController+Extent.h"
#import "CommUtility.h"
@interface IntroductionViewCtr ()
@property (retain, nonatomic) IBOutlet UIView *borderView;
@property (retain, nonatomic)  AttributedLabel *linkLabel;

@end

@implementation IntroductionViewCtr

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
    self.customTitle = @"功能介绍";
        
    self.linkLabel = [[[AttributedLabel alloc] initWithFrame:CGRectMake(-23, 189, 167, 21)] autorelease];
    self.linkLabel.text = @"http://bbs.18183.com";
    NSRange range = NSMakeRange(0, [self.linkLabel.text length]);
    [self.linkLabel setColor:[UIColor blueColor] fromIndex:range.location length:range.length];
    [self.linkLabel setStyle:kCTUnderlineStyleSingle fromIndex:range.location length:range.length];

    
    UITapGestureRecognizer *tagGes = [[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(link:)] autorelease];
    [self.linkLabel addGestureRecognizer:tagGes];
    self.linkLabel.userInteractionEnabled = YES;
    [self.view addSubview:self.linkLabel];
    
    self.borderView.layer.borderColor = [UIColor lightGrayColor].CGColor;
    self.borderView.layer.cornerRadius = 8.0;
    self.borderView.layer.borderWidth = 0.8;
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (void)link:(UITapGestureRecognizer *)tapGes
{
    UITextView *linkView = (UITextView *)tapGes.view;
    GameDetailWebCtrl *ctrl = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:linkView.text];
    [self.navigationController pushViewController:ctrl animated:YES];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [_borderView release];
    [_linkLabel release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setBorderView:nil];
    [self setLinkLabel:nil];
    [super viewDidUnload];
}
@end
