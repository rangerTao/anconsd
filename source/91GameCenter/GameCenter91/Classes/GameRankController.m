    //
//  GameCommonController.m
//  GameCenter91
//
//  Created by  hiyo on 12-9-6.
//  Copyright 2012 Nd. All rights reserved.
//

#import "GameRankController.h"
#import "GcPageTable.h"
#import "GameRankDetailController.h"
#import "CommUtility.h"
#import "UIViewController+Extent.h"
#import "NSArray+Extent.h"
#import "Colors.h"

#define GAME_RANK_DETAIL_TABLE_HEIGHT 40
#define GAME_RANK_DETAIL_HEIGHT_SEGMENT	35.0f

#define HOTEST_BUTTON_TAG 1
#define LATEST_BUTTON_TAG 2


@interface GameRankController()

@property (nonatomic, retain) NSMutableArray *gameRankDetailControllers;
@property (nonatomic, retain) UIView *buttonBackgroundView;
@property (nonatomic, retain) UIButton *latestGameRankButton;
@property (nonatomic, retain) UIButton *hotestGameRankButton;
@property (nonatomic, retain) UIView *lineView;

@property (nonatomic, assign) NSInteger currentButtonTag;

@end

@implementation GameRankController

- (void)dealloc
{
    self.gameRankDetailControllers = nil;
    self.buttonBackgroundView = nil;
    self.latestGameRankButton = nil;
    self.hotestGameRankButton = nil;
    [super dealloc];
}

+ (GameRankController *)gameRankController
{
    GameRankDetailController *ctr0 = [[[GameRankDetailController alloc] initWithType:GAME_DETAIL_HOT] autorelease];
    GameRankDetailController *ctr1 = [[[GameRankDetailController alloc] initWithType:GAME_DETAIL_NEW] autorelease];
    
    GameRankController * gameRankController = [[[GameRankController alloc] init] autorelease];
    gameRankController.title = @"排行";
    
    gameRankController.gameRankDetailControllers = [NSMutableArray arrayWithObjects:ctr0, ctr1, nil];
    
    for (NSUInteger index = 0; index < [gameRankController.gameRankDetailControllers count]; index++) {
        GameRankDetailController *ctr = [gameRankController.gameRankDetailControllers valueAtIndex:index];
        ctr.fatherViewController = (GameRankController *)gameRankController;
    }

    return gameRankController;
}

- (CGFloat)defaultSegmentHeight
{
    return GAME_RANK_DETAIL_HEIGHT_SEGMENT;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    self.buttonBackgroundView = [[[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 30)] autorelease];
    [self.view addSubview:self.buttonBackgroundView];
    
    self.latestGameRankButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.latestGameRankButton setTitle:@"最新" forState:UIControlStateNormal];
    self.latestGameRankButton.titleLabel.font = [UIFont systemFontOfSize:16.0];
    self.latestGameRankButton.tag = LATEST_BUTTON_TAG;
    [self.latestGameRankButton addTarget:self action:@selector(upadteViewAndButton:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.latestGameRankButton];
    
    self.hotestGameRankButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [self.hotestGameRankButton setTitle:@"最热" forState:UIControlStateNormal];
    self.hotestGameRankButton.titleLabel.font = [UIFont systemFontOfSize:16.0];
    self.hotestGameRankButton.tag = HOTEST_BUTTON_TAG;
    [self.hotestGameRankButton addTarget:self action:@selector(upadteViewAndButton:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:self.hotestGameRankButton];
    
    CGFloat height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:YES otherExcludeHeight:[self defaultSegmentHeight]];
    
    self.view.frame = CGRectMake(0, 0, 320.0, height);
    
    GameRankDetailController *gmctr0 = [self.gameRankDetailControllers valueAtIndex:0];
    GameRankDetailController *gmctr1 = [self.gameRankDetailControllers valueAtIndex:1];
    
    [self.view addSubview:gmctr1.view];
    [self.view addSubview:gmctr0.view];
    
    [self assignColorsAndSizes];
    
    [self assignGrayLine];
    
    self.currentButtonTag = LATEST_BUTTON_TAG;
    
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
    self.hotestGameRankButton.frame = CGRectMake(0, 0, 160.0, 30);
    self.latestGameRankButton.frame = CGRectMake(160, 0, 160.0, 30);
    self.lineView.frame = CGRectMake(0, 29, 320, 1);
    
    GameRankDetailController *gmctr = [self.gameRankDetailControllers valueAtIndex:(self.currentButtonTag - 1)];
    [gmctr viewWillAppear:YES];
}

- (void)assignColorsAndSizes
{
    self.buttonBackgroundView.backgroundColor = [CommUtility colorWithHexRGB:BACKGROUND_COLOR];
//    [UIColor colorWithRed:0xD9/255.0 green:0xDB/255.0 blue:0xDC/255.0 alpha:1.0];
//    [self.latestGameRankButton setBackgroundColor:[UIColor colorWithRed:0xFF/255.0 green:0xFF/255.0 blue:0xFF/255.0 alpha:1.0]];
//    [self.hotestGameRankButton setBackgroundColor:[UIColor colorWithRed:0xD9/255.0 green:0xDB/255.0 blue:0xDC/255.0 alpha:1.0]];
    
    [self.hotestGameRankButton setTitleColor:[CommUtility colorWithHexRGB:@"01B715"] forState:UIControlStateNormal];
    [self.latestGameRankButton setTitleColor:[CommUtility colorWithHexRGB:@"666666"] forState:UIControlStateNormal];
    self.hotestGameRankButton.titleLabel.font = [UIFont boldSystemFontOfSize:16.0];
    self.latestGameRankButton.titleLabel.font = [UIFont systemFontOfSize:16.0];
}

- (void)upadteViewAndButton:(id)sender
{
    UIButton *buttton = (UIButton *)sender;
    [self.view bringSubviewToFront:[[self.gameRankDetailControllers valueAtIndex:(buttton.tag - 1)] view]];
    [self resetButtonColor:sender];
}

- (void)resetButtonColor:(id)sender
{
    UIButton *button = (UIButton *)sender;
    if (button.tag == LATEST_BUTTON_TAG) {
//        [self.latestGameRankButton setBackgroundColor:[UIColor colorWithRed:0xFF/255.0 green:0xFF/255.0 blue:0xFF/255.0 alpha:1.0]];
//        [self.hotestGameRankButton setBackgroundColor:[UIColor colorWithRed:0xD9/255.0 green:0xDB/255.0 blue:0xDC/255.0 alpha:1.0]];
        
        [self.latestGameRankButton setTitleColor:[CommUtility colorWithHexRGB:@"01B715"] forState:UIControlStateNormal];
        self.latestGameRankButton.titleLabel.font = [UIFont boldSystemFontOfSize:16.0];
        [self.hotestGameRankButton setTitleColor:[CommUtility colorWithHexRGB:@"666666"] forState:UIControlStateNormal];
        self.hotestGameRankButton.titleLabel.font = [UIFont systemFontOfSize:16.0];
    } else if (button.tag == HOTEST_BUTTON_TAG) {
//        [self.hotestGameRankButton setBackgroundColor:[UIColor colorWithRed:0xFF/255.0 green:0xFF/255.0 blue:0xFF/255.0 alpha:1.0]];
//        [self.latestGameRankButton setBackgroundColor:[UIColor colorWithRed:0xD9/255.0 green:0xDB/255.0 blue:0xDC/255.0 alpha:1.0]];
        
        [self.hotestGameRankButton setTitleColor:[CommUtility colorWithHexRGB:@"01B715"] forState:UIControlStateNormal];
        self.hotestGameRankButton.titleLabel.font = [UIFont boldSystemFontOfSize:16.0];
        [self.latestGameRankButton setTitleColor:[CommUtility colorWithHexRGB:@"666666"] forState:UIControlStateNormal];
        self.latestGameRankButton.titleLabel.font = [UIFont systemFontOfSize:16.0];
    }
}

- (void)assignGrayLine
{
    self.lineView = [[[UIView alloc] init] autorelease];
    self.lineView.backgroundColor = [CommUtility colorWithHexRGB:@"E0E0E0"];
    [self.view addSubview:self.lineView];
}

@end
