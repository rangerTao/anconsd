//
//  myGamesDeafultCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/28/13.
//
//

#import "MyGamesDefaultCell.h"
#import "MyGameInfo.h"
#import "GameDetailController.h"

#import "UIImageView+WebCache.h"
#import "NSArray+Extent.h"

#import "ColorfulImage.h"

#import "ReportCenter.h"

@interface MyGamesDefaultCell ()

@property (nonatomic, retain) NSArray *suggestedGames;

@property (retain, nonatomic) IBOutlet UIButton *suggestedGameButton1;
@property (retain, nonatomic) IBOutlet UIButton *suggestedGameButton2;
@property (retain, nonatomic) IBOutlet UIButton *suggestedGameButton3;
@property (retain, nonatomic) IBOutlet UIButton *suggestedGameButton4;
@property (retain, nonatomic) IBOutlet UIImageView *suggestedGameIcon1;
@property (retain, nonatomic) IBOutlet UIImageView *suggestedGameIcon2;
@property (retain, nonatomic) IBOutlet UIImageView *suggestedGameIcon3;
@property (retain, nonatomic) IBOutlet UIImageView *suggestedGameIcon4;
@property (retain, nonatomic) IBOutlet UILabel *suggestedGameName1;
@property (retain, nonatomic) IBOutlet UILabel *suggestedGameName2;
@property (retain, nonatomic) IBOutlet UILabel *suggestedGameName3;
@property (retain, nonatomic) IBOutlet UILabel *suggestedGameName4;
@property (retain, nonatomic) IBOutlet UILabel *firstPromptTextLine;
@property (retain, nonatomic) IBOutlet UILabel *secondPromptTextLine;
@property (retain, nonatomic) IBOutlet UIImageView *suggestedGamesBackground;

@end

@implementation MyGamesDefaultCell

- (void)dealloc {
    self.suggestedGameButton1 = nil;
    self.suggestedGameButton2 = nil;
    self.suggestedGameButton3 = nil;
    self.suggestedGameButton4 = nil;
    self.suggestedGameIcon1 = nil;
    self.suggestedGameIcon2 = nil;
    self.suggestedGameIcon3 = nil;
    self.suggestedGameIcon4 = nil;
    self.suggestedGameName1 = nil;
    self.suggestedGameName2 = nil;
    self.suggestedGameName3 = nil;
    self.suggestedGameName4 = nil;
    self.firstPromptTextLine = nil;
    self.secondPromptTextLine = nil;
    self.suggestedGamesBackground = nil;
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

- (void)refresMyGamesDefaultCell:(NSArray *)myGames isMyGameDownloadedExist:(BOOL)isMyGameDownloadedExist
{
    UIImage *image = [UIImage imageNamed:@"bg_suggested_game_background.png"];
    image = [image stretchableImageWithLeftCapWidth:image.size.width/2 topCapHeight:image.size.height/2];
    self.suggestedGamesBackground.image = image;
    
    self.suggestedGames = [NSArray arrayWithArray:myGames];

    MyGameInfo *myGame1 = [self.suggestedGames valueAtIndex:0];
    [self assignMyGame:myGame1 suggestedGameButton:self.suggestedGameButton1 suggestedGameIcon:self.suggestedGameIcon1 suggestedGameName:self.suggestedGameName1];
    self.suggestedGameButton1.tag = 101;
        
    MyGameInfo *myGame2 = [self.suggestedGames valueAtIndex:1];
    [self assignMyGame:myGame2 suggestedGameButton:self.suggestedGameButton2 suggestedGameIcon:self.suggestedGameIcon2 suggestedGameName:self.suggestedGameName2];
    self.suggestedGameButton2.tag = 102;
    
    MyGameInfo *myGame3 = [self.suggestedGames valueAtIndex:2];
    [self assignMyGame:myGame3 suggestedGameButton:self.suggestedGameButton3 suggestedGameIcon:self.suggestedGameIcon3 suggestedGameName:self.suggestedGameName3];
    self.suggestedGameButton3.tag = 103;
    
    MyGameInfo *myGame4 = [self.suggestedGames valueAtIndex:3];
    [self assignMyGame:myGame4 suggestedGameButton:self.suggestedGameButton4 suggestedGameIcon:self.suggestedGameIcon4 suggestedGameName:self.suggestedGameName4];
    self.suggestedGameButton4.tag = 104;
    
    if (isMyGameDownloadedExist == NO) {
        self.firstPromptTextLine.text = @"亲，您还没有安装游戏哟，";
        self.secondPromptTextLine.text = @"下载一款试试吧，我们将为您提供量身服务！";
    } else {
        self.firstPromptTextLine.text = @"亲，你可以点击“编辑”移动游戏位置，";
        self.secondPromptTextLine.text = @" 海量礼包、火热活动、攻略秘籍让你快人一步！";
    }
}

- (void)showMyGameDetails:(id)sender
{
    UIButton *button = (UIButton *)sender;
    MyGameInfo *myGame = [self.suggestedGames valueAtIndex:[self realIndexInSuggestedGames:button.tag]];
    GameDetailController *gameDetailController = [GameDetailController gameDetailWithIdentifier:myGame.identifier gameName:myGame.appName];
    
    [ReportCenter report:ANALYTICS_EVENT_15033 label:myGame.identifier downloadFromNum:ANALYTICS_EVENT_15082];
    
    [((UITableViewController *)self.fatherView).navigationController pushViewController:gameDetailController animated:YES];
}

- (void)assignMyGame:(MyGameInfo *)suggestedGame suggestedGameButton:(UIButton *)suggestedGameButton suggestedGameIcon:(UIImageView *)suggestedGameIcon suggestedGameName:(UILabel *)suggestedGameName
{
    [suggestedGameButton addTarget:self action:@selector(showMyGameDetails:) forControlEvents:UIControlEventTouchUpInside];
    [suggestedGameButton setBackgroundImage:[ColorfulImage imageWithColor:[UIColor whiteColor]]  forState:UIControlStateSelected];
    [suggestedGameButton setBackgroundImage:[ColorfulImage imageWithColor:[UIColor whiteColor]]  forState:UIControlStateHighlighted];
    [suggestedGameIcon setImageWithURL:[NSURL URLWithString:suggestedGame.appIconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
    suggestedGameName.text = suggestedGame.appName;
}

- (NSInteger)realIndexInSuggestedGames:(NSInteger)buttonTag
{
    return buttonTag - 101;
}

@end
