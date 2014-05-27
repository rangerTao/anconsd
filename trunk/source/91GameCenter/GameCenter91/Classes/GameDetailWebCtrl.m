//
//  GameDetailWebCtrl.m
//  GameCenter91
//
//  Created by hiyo on 12-10-13.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "GameDetailWebCtrl.h"
#import "UIViewController+Extent.h"
#import "CommUtility.h"
#import "TabContainerController.h"
#import "MBProgressHUD.h"

#define INDEX_BACK      1
#define INDEX_FORWARD   3
#define INDEX_REFRESH   5

@interface GameDetailWebCtrl()
@property (nonatomic, retain) UIToolbar *toolbar;
@property (nonatomic, retain) UIActivityIndicatorView *activityView;
@property (nonatomic, assign) BOOL bAppear;
@property (nonatomic, retain) NSString *errorTitle;
@property (nonatomic, retain) NSString *errorContent;
- (void)initToolbar;
@end

@implementation GameDetailWebCtrl
@synthesize webView, myUrl, appid, bHasSegment;
@synthesize toolbar;
@synthesize activityView;
@synthesize bAppear, errorTitle, errorContent;

+ (GameDetailWebCtrl *)GameDetailWebCtrlWithUrl:(NSString *)aUrl
{
    GameDetailWebCtrl *ctrl = [[[GameDetailWebCtrl alloc] init] autorelease];
    ctrl.myUrl = aUrl;
    return ctrl;
}

#pragma mark - View lifecycle
- (id)init
{
    self = [super init];
    if (self) {
        self.webView = nil;
        self.myUrl = nil;
        self.toolbar = nil;
        self.bHasSegment = NO;
        
        self.bAppear = NO;
        self.errorTitle = nil;
        self.errorContent = nil;
    }
    return self;
}

- (void)dealloc
{
    self.webView = nil;
    self.myUrl = nil;
    self.toolbar = nil;
    self.activityView = nil;
    self.errorTitle = nil;
    self.errorContent = nil;
    [super dealloc];
}

- (void)loadWebView:(NSString *)urlStr
{
    if (urlStr == nil) {
        return;
    }
    self.myUrl = urlStr;
    NSURL *url = [NSURL URLWithString:urlStr]; 
	NSMutableURLRequest *urlRequest = [[[NSMutableURLRequest alloc] initWithURL:url] autorelease]; 
	[webView loadRequest:urlRequest];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //show error
    self.bAppear = YES;
    if (self.errorTitle || self.errorContent) {
        [MBProgressHUD showHintHUD:errorTitle message:errorContent hideAfter:DEFAULT_TIP_LAST_TIME];
        self.errorTitle = nil;
        self.errorContent = nil;
    }
    
    if ([self.title isEqualToString:@"攻略"]) {
    }
    else if ([self.title isEqualToString:@"论坛"]) {
    }
    [self loadWebView:self.myUrl];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    
    self.bAppear = NO;
}

- (void)initWebView
{
    int toolbar_height = 44;
    float height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:NO otherExcludeHeight:toolbar_height + (bHasSegment?[TabContainerController defaultSegmentHeight] : 0.0)];
    self.webView = [[[UIWebView alloc] initWithFrame:CGRectMake(0, 0, 320.0, height)] autorelease];
    webView.delegate = self;
    [self.view addSubview:webView];
    
    [self initToolbar];
    
    self.activityView = [[[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray] autorelease];
    self.activityView.center = CGPointMake(self.webView.frame.size.width/2, self.webView.frame.size.height/2);
    [self.webView addSubview:self.activityView];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self initWebView];
    [self loadWebView:self.myUrl];
    
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

#pragma mark - toolbar
- (UIBarButtonItem *)createBarItem:(NSString *)imageStr action:(NSString *)selStr
{
	UIBarButtonItem *item = nil;
    if ([imageStr length] > 0) {
        UIImage *img = [UIImage imageNamed:imageStr];
        item = [[[UIBarButtonItem alloc] initWithImage:img style:UIBarButtonItemStylePlain target:self action: NSSelectorFromString(selStr)] autorelease];
    }
    else {
        item = [[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:self action:nil] autorelease];
    }
    
	return item;
}

- (void)initToolbar
{
	NSArray *items = [NSArray arrayWithObjects:
                      [NSArray arrayWithObjects:@"",@"",nil],
                      [NSArray arrayWithObjects:@"menu_bbs_0",@"backward:",nil],
                      [NSArray arrayWithObjects:@"",@"",nil],
                      [NSArray arrayWithObjects:@"menu_bbs_1",@"forward:",nil],
                      [NSArray arrayWithObjects:@"",@"",nil],
                      [NSArray arrayWithObjects:@"menu_bbs_5",@"freshOrBreak:",nil],
                      [NSArray arrayWithObjects:@"",@"",nil], nil];
	NSMutableArray *barItems = [NSMutableArray arrayWithCapacity:7];
	for(int i = 0;i < [items count];i++)
	{
		UIBarButtonItem *item = [self createBarItem:[[items objectAtIndex:i] objectAtIndex:0] action:[[items objectAtIndex:i] objectAtIndex:1]];
		[barItems addObject:item];
	}

    CGRect rt = self.webView.frame;
    CGFloat y = rt.origin.y + rt.size.height;
    self.toolbar = [[[UIToolbar alloc] initWithFrame:CGRectMake(0, y, 320, 44)] autorelease];
    toolbar.items = barItems;
    [self.view addSubview:toolbar];
}

- (void)switchFreshOrBreakButton
{
    NSString *itemStr = webView.isLoading ? @"menu_bbs_4" : @"menu_bbs_5";
	NSMutableArray *newArray = [NSMutableArray arrayWithArray:self.toolbar.items];
    if ([newArray count] <= INDEX_REFRESH) {
        return;
    }
    [newArray replaceObjectAtIndex:INDEX_REFRESH withObject:[self createBarItem:itemStr action:@"freshOrBreak:"]];
    
    self.toolbar.items = newArray;
}

- (void)updateToolBar
{
	[[self.toolbar.items objectAtIndex:INDEX_BACK] setEnabled:self.webView.canGoBack];
	[[self.toolbar.items objectAtIndex:INDEX_FORWARD] setEnabled:self.webView.canGoForward];
    [self switchFreshOrBreakButton];
}

- (void)backward:(id)sender
{
    [webView goBack];
    [self updateToolBar];
}

- (void)forward:(id)sender
{
    [webView goForward];
    [self updateToolBar];
}

-(void)freshOrBreak:(id)sender
{
    if (webView.isLoading) {
        [webView stopLoading];
    }
    else {
        [self loadWebView:myUrl];
//        [webView reload];
    }
	
    [self updateToolBar];
}

#pragma mark -
- (BOOL)webView:(UIWebView*)webView shouldStartLoadWithRequest:(NSURLRequest*)request navigationType:(UIWebViewNavigationType)navigationType {	
//	NSString *urlstr = [[request URL] absoluteString];
	return YES;
}

- (void)webViewDidStartLoad:(UIWebView *)webView {
    [self updateToolBar];
//    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    [self.activityView startAnimating];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    [self updateToolBar];
//    [MBProgressHUD hideHUDForView:self.view animated:YES];
    [self.activityView stopAnimating];
    
    if (self.navigationController && self.customTitle == nil) {
        self.customTitle = [self.webView stringByEvaluatingJavaScriptFromString:@"document.title"];
    }
}

- (void)webView:(UIWebView *)webview didFailLoadWithError:(NSError *)error {
    
//    [MBProgressHUD hideHUDForView:self.view animated:YES];
    [self.activityView stopAnimating];
    if ([error.domain isEqualToString:@"NSURLErrorDomain"] && error.code == NSURLErrorCancelled)
	{
		return;		//this error was cause by cancel a connection;
	}
    
    [self updateToolBar];
    
    if (self.bAppear) {
        [MBProgressHUD showHintHUD:@"网络错误" message:[error localizedDescription] hideAfter:DEFAULT_TIP_LAST_TIME];
    }
    else {
        self.errorTitle = @"网络错误";
        self.errorContent = [error localizedDescription];
    }
}

@end
