//
//  myGamesDeafultCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/28/13.
//
//

#import "MyGamesDeafultCell.h"
#import "MyGameInfo.h"
#import "GameDetailController.h"

@interface MyGamesDeafultCell ()

@property (nonatomic, retain) NSArray *suggestedGames;

@property (retain, nonatomic) IBOutlet UIButton *suggestedGameIcon1;
@property (retain, nonatomic) IBOutlet UIButton *suggestedGameIcon2;
@property (retain, nonatomic) IBOutlet UIButton *suggestedGameIcon3;
@property (retain, nonatomic) IBOutlet UIButton *suggestedGameIcon4;
@property (retain, nonatomic) IBOutlet UILabel *suggestedGameName1;
@property (retain, nonatomic) IBOutlet UILabel *suggestedGameName2;
@property (retain, nonatomic) IBOutlet UILabel *suggestedGameName3;
@property (retain, nonatomic) IBOutlet UILabel *suggestedGameName4;

@end

@implementation MyGamesDeafultCell

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

- (void)refresMyGamesDefaultCell:(NSArray *)myGames
{
    self.suggestedGames = myGames;

    MyGameInfo *myGame1 = [self.suggestedGames objectAtIndex:0];
    UIImage *image1 = [UIImage imageWithData: [NSData dataWithContentsOfURL: [NSURL URLWithString:myGame1.appIconUrl]]];
    if (image1 == nil) {
        [self.suggestedGameIcon1 setBackgroundImage:[UIImage imageNamed:@"icon.png"] forState:UIControlStateNormal];
    } else {
        [self.suggestedGameIcon1 setBackgroundImage:image1 forState:UIControlStateNormal];
    }
    self.suggestedGameIcon1.tag = 0;
    [self.suggestedGameIcon1 addTarget:self action:@selector(showMyGameDetails:) forControlEvents:UIControlEventTouchUpInside];
    
    self.suggestedGameName1.text = ((MyGameInfo *)[self.suggestedGames objectAtIndex:0]).appName;
    
    MyGameInfo *myGame2 = [self.suggestedGames objectAtIndex:1];
    UIImage *image2 = [UIImage imageWithData: [NSData dataWithContentsOfURL: [NSURL URLWithString:myGame2.appIconUrl]]];
    if (image2 == nil) {
        [self.suggestedGameIcon2 setBackgroundImage:[UIImage imageNamed:@"icon.png"] forState:UIControlStateNormal];
    } else {
        [self.suggestedGameIcon2 setBackgroundImage:image2 forState:UIControlStateNormal];
    }
    
    self.suggestedGameIcon2.tag = 1;
    [self.suggestedGameIcon2 addTarget:self action:@selector(showMyGameDetails:) forControlEvents:UIControlEventTouchUpInside];
    
    self.suggestedGameName2.text = ((MyGameInfo *)[self.suggestedGames objectAtIndex:1]).appName;
    
    MyGameInfo *myGame3 = [self.suggestedGames objectAtIndex:2];
    UIImage *image3 = [UIImage imageWithData: [NSData dataWithContentsOfURL: [NSURL URLWithString:myGame3.appIconUrl]]];
    if (image3 == nil) {
        [self.suggestedGameIcon3 setBackgroundImage:image3 forState:UIControlStateNormal];
    } else {
        [self.suggestedGameIcon3 setBackgroundImage:[UIImage imageNamed:@"icon.png"] forState:UIControlStateNormal];
    }
    
    self.suggestedGameIcon3.tag = 2;
    [self.suggestedGameIcon3 addTarget:self action:@selector(showMyGameDetails:) forControlEvents:UIControlEventTouchUpInside];
    
    self.suggestedGameName3.text = ((MyGameInfo *)[self.suggestedGames objectAtIndex:2]).appName;
    
    MyGameInfo *myGame4 = [self.suggestedGames objectAtIndex:3];
    UIImage *image4 = [UIImage imageWithData: [NSData dataWithContentsOfURL: [NSURL URLWithString:myGame4.appIconUrl]]];
    if (image4 == nil) {
        [self.suggestedGameIcon4 setBackgroundImage:[UIImage imageNamed:@"icon.png"] forState:UIControlStateNormal];
    } else {
        [self.suggestedGameIcon4 setBackgroundImage:image4 forState:UIControlStateNormal];
    }
    
    self.suggestedGameIcon4.tag = 3;
    [self.suggestedGameIcon4 addTarget:self action:@selector(showMyGameDetails:) forControlEvents:UIControlEventTouchUpInside];
    
    self.suggestedGameName4.text = ((MyGameInfo *)[self.suggestedGames objectAtIndex:3]).appName;
}

- (void)dealloc {
    self.suggestedGameIcon1 = nil;
    self.suggestedGameIcon2 = nil;
    self.suggestedGameIcon3 = nil;
    self.suggestedGameIcon4 = nil;
    self.suggestedGameName1 = nil;
    self.suggestedGameName2 = nil;
    self.suggestedGameName3 = nil;
    self.suggestedGameName4 = nil;
    [super dealloc];
}

- (void)showMyGameDetails:(id)sender
{
    UIButton *button = (UIButton *)sender;
    MyGameInfo *myGame = [self.suggestedGames objectAtIndex:button.tag];
    GameDetailController *gameDetailController = [GameDetailController gameDetailWithIdentifier:myGame.identifier];
    [((UITableViewController *)self.fatherView).navigationController pushViewController:gameDetailController animated:YES];
}

@end
