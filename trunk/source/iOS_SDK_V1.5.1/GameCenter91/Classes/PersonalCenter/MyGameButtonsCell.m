//
//  myGameButtonsCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 11/11/13.
//
//

#import "MyGameButtonsCell.h"
#import "ProgessButton.h"
#import "MyGameInfo.h"
#import "GameDetailController.h"
#import "GameDetailWebCtrl.h"
#import "GameCenterAnalytics.h"
#import "ReportCenter.h"

#define TRIANGLE_VIEW_TAG 100

#define kButtonImageReseted       @"btn_gray.png"
#define GameBtnNormalImage          [UIImage imageNamed:@"whiteButton_ Normal.png"]
#define GameBtnHighlightImage       [UIImage imageNamed:@"whiteButton_Touch.png"]

@interface MyGameButtonsCell ()

@property (retain, nonatomic) IBOutlet ProgessButton *stateButton;
@property (retain, nonatomic) IBOutlet UIButton *areaButton;
@property (retain, nonatomic) IBOutlet UIButton *thirdButton;
@property (retain, nonatomic) IBOutlet UIButton *forthButton;
@property (retain, nonatomic) IBOutlet UIImageView *myGamePromptTriangle;

@property (nonatomic, retain) MyGameInfo *myGame;

- (IBAction)showGameArea:(id)sender;

@end

@implementation MyGameButtonsCell

- (void)dealloc {
    self.stateButton = nil;
    self.areaButton = nil;
    self.thirdButton = nil;
    self.forthButton = nil;
    self.myGame = nil;
    self.myGamePromptTriangle = nil;
    [super dealloc];
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)refreshCellWithMyGameInfo:(MyGameInfo *)myGame withIndex:(NSInteger)index
{
    self.myGame = myGame;
    [self fetchButtons];

    if (myGame.suggestType == 0) {
        [self fetchTriangle:index];
    }
}

- (void)fetchButtons
{
    [self fetchProgessButtonAndAreaButton];
    [self fetchStrategyAndForum];
}

- (void)fetchProgessButtonAndAreaButton
{
    [self.stateButton setProgressButtonInfo:self.myGame.identifier f_id:self.myGame.f_id softName:self.myGame.appName iconUrl:self.myGame.appIconUrl];
    self.stateButton.tag = ProgessButton_DISPLAY_POSE_PERSON;
    [self.stateButton bringSubviewToFront:self.stateButton.normalButton];
    
    [self.areaButton setBackgroundImage:GameBtnNormalImage forState:UIControlStateNormal];
    [self.areaButton setBackgroundImage:GameBtnHighlightImage forState:UIControlStateHighlighted];
    [self.thirdButton setBackgroundImage:GameBtnNormalImage forState:UIControlStateNormal];
    [self.thirdButton setBackgroundImage:GameBtnHighlightImage forState:UIControlStateHighlighted];
    [self.forthButton setBackgroundImage:GameBtnNormalImage forState:UIControlStateNormal];
    [self.forthButton setBackgroundImage:GameBtnHighlightImage forState:UIControlStateHighlighted];
}

- (void)fetchStrategyAndForum
{
    self.thirdButton.hidden = (self.myGame.strategyUrl.length <= 0 && self.myGame.forumUrl.length <= 0);
    self.forthButton.hidden = (self.myGame.strategyUrl.length <= 0 || self.myGame.forumUrl.length <= 0);
    
    if (self.myGame.strategyUrl.length > 0) {
        [self.thirdButton setTitle:@"攻略" forState:UIControlStateNormal];
        [self.thirdButton addTarget:self action:@selector(showGameStrategy:) forControlEvents:UIControlEventTouchUpInside];
    }
    if (self.myGame.forumUrl.length > 0) {
        if (self.myGame.strategyUrl.length > 0) {
            [self.forthButton setTitle:@"论坛" forState:UIControlStateNormal];
            [self.forthButton addTarget:self action:@selector(showGameForum:) forControlEvents:UIControlEventTouchUpInside];
        } else {
            [self.thirdButton setTitle:@"论坛" forState:UIControlStateNormal];
            [self.thirdButton addTarget:self action:@selector(showGameForum:) forControlEvents:UIControlEventTouchUpInside];
        }
    }
}

- (IBAction)showGameArea:(id)sender {
    
    if (self.myGame.identifier == nil) {
        return;
    }
    
    GameDetailController *ctrl = [GameDetailController gameDetailWithIdentifier:self.myGame.identifier gameName:self.myGame.appName];
    ctrl.hidesBottomBarWhenPushed = YES;
    UIViewController *fatherController = self.fatherController;
    
    [ReportCenter report:ANALYTICS_EVENT_15027 label:self.myGame.identifier];
    
    [fatherController.navigationController pushViewController:ctrl animated:YES];
}

- (void)showGameStrategy:(id)sender
{
    if (self.myGame.strategyUrl == nil) {
        return;
    }
    
    GameDetailWebCtrl *webViewCtr = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:self.myGame.strategyUrl];
    webViewCtr.hidesBottomBarWhenPushed = YES;
    UIViewController *fatherController = self.fatherController;
    
    [ReportCenter report:ANALYTICS_EVENT_15028 label:self.myGame.identifier];
    
    [fatherController.navigationController pushViewController:webViewCtr animated:YES];
}

- (void)showGameForum:(id)sender
{
    if (self.myGame.forumUrl == nil) {
        return;
    }
    
    GameDetailWebCtrl *webViewCtr = [GameDetailWebCtrl GameDetailWebCtrlWithUrl:self.myGame.forumUrl];
    webViewCtr.hidesBottomBarWhenPushed = YES;
    UIViewController *fatherController = self.fatherController;
    
    [ReportCenter report:ANALYTICS_EVENT_15029 label:self.myGame.identifier];
    
    [fatherController.navigationController pushViewController:webViewCtr animated:YES];
}

- (void)fetchTriangle:(NSInteger)selectedButtonIndex
{
    self.myGamePromptTriangle.frame = CGRectMake(75 * selectedButtonIndex + 27, -8, 20, 9);
}

@end
