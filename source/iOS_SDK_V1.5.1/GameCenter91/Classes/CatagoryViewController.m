//
//  ClassificationViewController.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/11/13.
//
//

#import "CatagoryViewController.h"
#import "GameCatagoryInfo.h"
#import "UITableViewCell+Addition.h"
#import "CatagoryCell.h"
#import "GameCenterOperation.h"
#import "MBProgressHUD.h"
#import "RequestorAssistant.h"
#import "CommUtility.h"
#import "Colors.h"

#define CATAGORYNAME @"CatagoryName"

@interface CatagoryViewController () <UITableViewDataSource, UITableViewDelegate, GetAppCatagoryListProtocol>

@property (nonatomic, retain) UITableView *catagoryTableView;
@property (nonatomic, retain) NSMutableArray *appCatagoryList;

@end

@implementation CatagoryViewController

- (void)dealloc
{
    self.catagoryTableView = nil;
    self.appCatagoryList = nil;
    [super dealloc];
}

- (id)init
{
    self = [super init];
    if (self) {
        self.title = @"分类";
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
	// Do any additional setup after loading the view.
    
    [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    NSNumber *ret = [RequestorAssistant requestAppCatagoryList:self];
    if ([ret intValue] < 0) {
        [MBProgressHUD hideHUDForView:self.view animated:YES];
        [MBProgressHUD showHintHUD:@"错误" message:[NSString stringWithFormat:@"%d", [ret intValue]] hideAfter:DEFAULT_TIP_LAST_TIME];
    }
    
    [self assignClassificationTableView];
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.catagoryTableView.frame = CGRectMake(0, 0, CGRectGetWidth(self.view.frame), CGRectGetHeight(self.view.frame));
    self.catagoryTableView.allowsSelection = NO;
}

- (void)assignClassificationTableView
{
    self.catagoryTableView = [[[UITableView alloc] init] autorelease];
    self.catagoryTableView.dataSource = self;
    self.catagoryTableView.delegate = self;
    self.catagoryTableView.backgroundColor = [CommUtility colorWithHexRGB:BACKGROUND_COLOR];
    self.catagoryTableView.backgroundView = nil;
    [self.view addSubview:self.catagoryTableView];
}

#pragma mark - UITableViewDataSource

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return (([self.appCatagoryList count] + 1) / 2);
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    CatagoryCell *cell = [CatagoryCell dequeOrCreateInTable:tableView];
    
    cell.fatherViewController = self;
    [cell refreshCellWithAppCatagoryList:self.appCatagoryList andIndexPath:indexPath];
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 70;
}

#pragma mark -
#pragma mark GetAppCatagoryListProtocol
- (void)operation:(GameCenterOperation *)operation getAppCatagoryListDidFinish:(NSError *)error appCatagoryList:(NSArray *)appCatagoryList
{
    [MBProgressHUD hideHUDForView:self.view animated:YES];
    if (error == nil) {
        self.appCatagoryList = [NSMutableArray arrayWithArray:appCatagoryList];
        [self.catagoryTableView reloadData];
    }
}

@end
