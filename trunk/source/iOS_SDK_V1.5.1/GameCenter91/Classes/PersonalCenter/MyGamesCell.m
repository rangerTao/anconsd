//
//  myGamesCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/22/13.
//
//

#import "MyGamesCell.h"
#import "MyGameInfo.h"
#import "MyGameButtonsCell.h"
#import "UIImageView+WebCache.h"
#import "ColorfulImage.h"
#import "NSArray+Extent.h"
#import "GameDetailController.h"
#import "GameCenterAnalytics.h"
#import "ReportCenter.h"

#define TRIANGLE_VIEW_TAG 100
#define SUGGESTED_GAMES_BACKGROUND 200

@interface MyGamesCell () 

@property (nonatomic, retain) NSArray *myGames;

@property (retain, nonatomic) IBOutlet UIImageView *myGameIcon1;
@property (retain, nonatomic) IBOutlet UIImageView *myGameIcon2;
@property (retain, nonatomic) IBOutlet UIImageView *myGameIcon3;
@property (retain, nonatomic) IBOutlet UIImageView *myGameIcon4;
@property (retain, nonatomic) IBOutlet UILabel *myGameLable1;
@property (retain, nonatomic) IBOutlet UILabel *myGameLable2;
@property (retain, nonatomic) IBOutlet UILabel *myGameLable3;
@property (retain, nonatomic) IBOutlet UILabel *myGameLable4;
@property (retain, nonatomic) IBOutlet UIButton *myGameButton1;
@property (retain, nonatomic) IBOutlet UIButton *myGameButton2;
@property (retain, nonatomic) IBOutlet UIButton *myGameButton3;
@property (retain, nonatomic) IBOutlet UIButton *myGameButton4;
@property (retain, nonatomic) IBOutlet UIImageView *myGameSelectedBackgorund;
@property (retain, nonatomic) IBOutlet UIImageView *suggestedGamesBackground;
@property (retain, nonatomic) IBOutlet UIImageView *myGameSelectedPromptTriangle;
@property (retain, nonatomic) IBOutlet UIImageView *hotIcon;

@end

@implementation MyGamesCell

- (void)dealloc {
    self.myGameIcon1 = nil;
    self.myGameIcon2 = nil;
    self.myGameIcon3 = nil;
    self.myGameIcon4 = nil;
    self.myGameLable1 = nil;
    self.myGameLable2 = nil;
    self.myGameLable3 = nil;
    self.myGameLable4 = nil;
    self.myGameButton1 = nil;
    self.myGameButton2 = nil;
    self.myGameButton3 = nil;
    self.myGameButton4 = nil;
    self.myGames = nil;
    self.myGameSelectedBackgorund = nil;
    self.myGameSelectedPromptTriangle = nil;
    self.suggestedGamesBackground = nil;
    self.hotIcon = nil;
    [super dealloc];
}

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        self.selectedButtonIndex = 0;
    }
    return self;
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

#pragma mark - setCell

- (void)refreshMyGamesCell:(NSMutableArray *)myGames selectedButtonIndex:(int)selectedButtonIndex;
{
    UIImage *image = [UIImage imageNamed:@"suggested_games_background.png"];
    image = [image stretchableImageWithLeftCapWidth:image.size.width/2 topCapHeight:image.size.height/2];
    self.suggestedGamesBackground.image = image;
    
    self.myGames = myGames;
    
    MyGameInfo *myGame1 = [myGames valueAtIndex:0];
     [self assignMyGame:myGame1 myGameButton:self.myGameButton1 myGameIcon:self.myGameIcon1 myGameLable:self.myGameLable1];
    
    MyGameInfo *myGame2 = [myGames valueAtIndex:1];
     [self assignMyGame:myGame2 myGameButton:self.myGameButton2 myGameIcon:self.myGameIcon2 myGameLable:self.myGameLable2];
    
    MyGameInfo *myGame3 = [myGames valueAtIndex:2];
    [self assignMyGame:myGame3 myGameButton:self.myGameButton3 myGameIcon:self.myGameIcon3 myGameLable:self.myGameLable3];
    
    MyGameInfo *myGame4 = [myGames valueAtIndex:3];
    [self assignMyGame:myGame4 myGameButton:self.myGameButton4 myGameIcon:self.myGameIcon4 myGameLable:self.myGameLable4];
    
    [self fetchSuggestedGamesBackground];
    
    MyGameInfo *myGame = [self.myGames valueAtIndex:self.selectedButtonIndex];
    if (myGame.suggestType == 0) {
        [self fetchPromptIconAndTriangle:selectedButtonIndex];
    }
}

- (void)assignMyGame:(MyGameInfo *)myGame myGameButton:(UIButton *)myGameButton myGameIcon:(UIImageView *)myGameIcon myGameLable:(UILabel *)myGameLable
{
    [myGameButton removeTarget:nil action:NULL forControlEvents:UIControlEventAllEvents];
    
    if (myGame.suggestType == 0) {
        [myGameButton addTarget:self action:@selector(showGameActivities:) forControlEvents:UIControlEventTouchUpInside];
    } else if (myGame.suggestType == 1) {
        [myGameButton addTarget:self action:@selector(showGameDeatil:) forControlEvents:UIControlEventTouchUpInside];
    }
    
    if (myGame.suggestType == 1) {
        [myGameButton setBackgroundImage:[ColorfulImage imageWithColor:[UIColor whiteColor]] forState:UIControlStateSelected];
        [myGameButton setBackgroundImage:[ColorfulImage imageWithColor:[UIColor whiteColor]] forState:UIControlStateHighlighted];
        
    } else {
        [myGameButton setBackgroundImage:[ColorfulImage imageWithColor:[UIColor colorWithRed:0xB1/255.0 green:0xDA/255.0 blue:0xEC/255.0 alpha:1.0]] forState:UIControlStateSelected];
        [myGameButton setBackgroundImage:[ColorfulImage imageWithColor:[UIColor colorWithRed:0xB1/255.0 green:0xDA/255.0 blue:0xEC/255.0 alpha:1.0]] forState:UIControlStateHighlighted];
    }
    
    [myGameIcon setImageWithURL:[NSURL URLWithString:myGame.appIconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
    
    myGameLable.text = myGame.appName;
}

- (void)fetchSuggestedGamesBackground
{
    NSInteger normalGamesCount = 0;
    for (NSInteger index = 0; index < [self.myGames count]; index ++) {
        MyGameInfo *myGame = [self.myGames valueAtIndex:index];
        if (myGame.suggestType == 1) {
            normalGamesCount = index;
            break;
        }
    }
    
    if (normalGamesCount == 0 || normalGamesCount > 3) {
        self.suggestedGamesBackground.hidden = YES;
        self.hotIcon.hidden = YES;
        return;
    }
    
    self.suggestedGamesBackground.hidden = NO;
    self.hotIcon.hidden = NO;
    
    CGRect frame = self.suggestedGamesBackground.frame;
    switch (normalGamesCount) {
        case 1:
            frame.origin.x = 75;
            frame.size.width = 225 + 1;
            self.suggestedGamesBackground.frame = frame;
            [self adjustHotIconWithSuggestedGamesBackground];
            break;
        case 2:
            frame.origin.x = 150;
            frame.size.width = 150 + 1;
            self.suggestedGamesBackground.frame = frame;
            [self adjustHotIconWithSuggestedGamesBackground];
            break;
        case 3:
            frame.origin.x = 225;
            frame.size.width = 75 + 1;
            self.suggestedGamesBackground.frame = frame;
            [self adjustHotIconWithSuggestedGamesBackground];
            break;
        default:
            break;
    }
}

- (void)adjustHotIconWithSuggestedGamesBackground
{
    CGRect frame = self.hotIcon.frame;
    frame.origin.x = self.suggestedGamesBackground.frame.origin.x;
    self.hotIcon.frame = frame;
}

- (void)showGameActivities:(id)sender
{
    UIButton *button = (UIButton *)sender;
    self.selectedButtonIndex = [self realTagOfButton:button.tag];
    
    [self fetchPromptIconAndTriangle:self.selectedButtonIndex];

    if ([self.fatherViewController respondsToSelector:@selector(showMyGameActivities:)]) {
        [self.fatherViewController performSelector:@selector(showMyGameActivities:) withObject:sender];
    }
}

- (void)showGameDeatil:(id)sender
{
    UIButton *button = (UIButton *)sender;
    self.selectedButtonIndex = [self realTagOfButton:button.tag];
    
    MyGameInfo *myGame = [self.myGames valueAtIndex:self.selectedButtonIndex];
    
    GameDetailController *ctrl = [GameDetailController gameDetailWithIdentifier:myGame.identifier gameName:myGame.appName];
    ctrl.hidesBottomBarWhenPushed = YES;
    UIViewController *fatherViewController = self.fatherViewController;
    
    [ReportCenter report:ANALYTICS_EVENT_15033 label:myGame.identifier downloadFromNum:ANALYTICS_EVENT_15082];
    
    [fatherViewController.navigationController pushViewController:ctrl animated:YES];
}

- (void)fetchPromptIconAndTriangle:(NSInteger)selectedButtonIndex
{
    [self fetchSelectedPromptIcon:selectedButtonIndex];
    [self fetchTriangle:selectedButtonIndex];
}

- (void)fetchTriangle:(NSInteger)selectedButtonIndex
{
    MyGameInfo *myGame = [self.myGames valueAtIndex:selectedButtonIndex];
    if (myGame.suggestType == 0) {
        self.myGameSelectedPromptTriangle.frame = CGRectMake(75 * selectedButtonIndex + 27, 92, 20, 9);
    }
}

- (void)fetchSelectedPromptIcon:(NSInteger)selectedButtonIndex
{
    MyGameInfo *myGame = [self.myGames valueAtIndex:selectedButtonIndex];
    if (myGame.suggestType == 0) {
        CGRect frame = self.myGameSelectedBackgorund.frame;
        frame.origin.x = 2 + 75 * selectedButtonIndex;
        self.myGameSelectedBackgorund.frame = frame;
    }
}

- (NSInteger)realTagOfButton:(NSInteger)tag
{
    return tag - 21;
}

@end
