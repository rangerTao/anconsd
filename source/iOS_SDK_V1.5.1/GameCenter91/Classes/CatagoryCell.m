//
//  ClassificationCell.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/11/13.
//
//

#import "CatagoryCell.h"
#import "GameCatagoryInfo.h"
#import "ColorfulImage.h"
#import "NSArray+Extent.h"
#import "UIImageView+WebCache.h"
#import "CommUtility.h"
#import "Colors.h"
#import "SubCatagoryController.h"
#import "UIViewController+Extent.h"
#import "ReportCenter.h"

@interface CatagoryCell ()

@property (retain, nonatomic) IBOutlet UILabel *gameCatagoryLabelLeft;
@property (retain, nonatomic) IBOutlet UILabel *topAppLabelLeft1;
@property (retain, nonatomic) IBOutlet UILabel *topAppLabelLeft2;
@property (retain, nonatomic) IBOutlet UILabel *gameCatagoryLabelRight;
@property (retain, nonatomic) IBOutlet UILabel *topAppLabelRight1;
@property (retain, nonatomic) IBOutlet UILabel *topAppLabelRight2;
@property (retain, nonatomic) IBOutlet UIImageView *gameCatagoryImageViewLeft;
@property (retain, nonatomic) IBOutlet UIImageView *gameCatagoryImageViewRight;
@property (retain, nonatomic) IBOutlet UIButton *leftButton;
@property (retain, nonatomic) IBOutlet UIButton *rightButton;

@property (nonatomic, retain) NSArray *appCatagoryList;

@end

@implementation CatagoryCell

- (void)dealloc {
    self.gameCatagoryLabelLeft = nil;
    self.topAppLabelLeft1 = nil;
    self.topAppLabelLeft2 = nil;
    self.gameCatagoryLabelRight = nil;
    self.topAppLabelRight1 = nil;
    self.topAppLabelRight2 = nil;
    self.gameCatagoryImageViewLeft = nil;
    self.gameCatagoryImageViewRight = nil;
    self.leftButton = nil;
    self.rightButton = nil;
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

- (void)refreshCellWithAppCatagoryList:(NSArray *)appCatagoryList andIndexPath:(NSIndexPath *)indexPath
{
    self.tag = indexPath.row;
    self.appCatagoryList = [NSArray arrayWithArray:appCatagoryList];
    
    NSUInteger indexOfLeft = indexPath.row *2;
    GameCatagoryInfo *catagoryDictionaryLeft = [self.appCatagoryList valueAtIndex:indexOfLeft];
    [self refreshLeftPartInfo:catagoryDictionaryLeft];
    
    NSUInteger indexOfRight = indexPath.row *2 +1;
    if ([self.appCatagoryList count] > indexOfRight) {
        GameCatagoryInfo *catagoryDictionaryRight = [self.appCatagoryList valueAtIndex:indexOfRight];
        [self refreshRightPartInfo:catagoryDictionaryRight];
        }
    else {
        [self reassignRightPartInfo];
    }
}

- (void)refreshLeftPartInfo:(GameCatagoryInfo *)catagoryDictionaryLeft
{
    self.leftButton.tag = self.tag * 2;
    [self.leftButton addTarget:self action:@selector(touchGameCatagory:) forControlEvents:UIControlEventTouchUpInside];
    
    self.gameCatagoryLabelLeft.text = catagoryDictionaryLeft.catagoryName;
    self.topAppLabelLeft1.text = [catagoryDictionaryLeft.topAppList valueAtIndex:0];
    self.topAppLabelLeft2.text = [catagoryDictionaryLeft.topAppList valueAtIndex:1];
    
    [self.gameCatagoryImageViewLeft setImageWithURL:[NSURL URLWithString:catagoryDictionaryLeft.iconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
    
    [self.leftButton setBackgroundImage:[ColorfulImage imageWithColor:[CommUtility colorWithHexRGB:CELL_SELECTED_COLOR]]  forState:UIControlStateSelected];
    [self.leftButton setBackgroundImage:[ColorfulImage imageWithColor:[CommUtility colorWithHexRGB:CELL_SELECTED_COLOR]]  forState:UIControlStateHighlighted];
}

- (void)refreshRightPartInfo:(GameCatagoryInfo *)catagoryDictionaryRight
{
    self.rightButton.tag = self.tag * 2 + 1;
    [self.rightButton addTarget:self action:@selector(touchGameCatagory:) forControlEvents:UIControlEventTouchUpInside];
    
    self.gameCatagoryLabelRight.text = catagoryDictionaryRight.catagoryName;
    self.topAppLabelRight1.text = [catagoryDictionaryRight.topAppList valueAtIndex:0];
    self.topAppLabelRight2.text = [catagoryDictionaryRight.topAppList valueAtIndex:1];
    
    [self.gameCatagoryImageViewRight setImageWithURL:[NSURL URLWithString:catagoryDictionaryRight.iconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
    self.rightButton.hidden = NO;
    
    [self.rightButton setBackgroundImage:[ColorfulImage imageWithColor:[CommUtility colorWithHexRGB:CELL_SELECTED_COLOR]]  forState:UIControlStateSelected];
    [self.rightButton setBackgroundImage:[ColorfulImage imageWithColor:[CommUtility colorWithHexRGB:CELL_SELECTED_COLOR]]  forState:UIControlStateHighlighted];

}

- (void)reassignRightPartInfo
{
    self.gameCatagoryLabelRight.text = nil;
    self.topAppLabelRight1.text = nil;
    self.topAppLabelRight2.text = nil;
    self.gameCatagoryImageViewRight.image = nil;
    self.rightButton.hidden = YES;
}

#pragma mark - action
- (void)touchGameCatagory:(id)sender
{
    UIButton *touchButton = (UIButton *)sender;
    GameCatagoryInfo * gameCatagoryInfo = [self.appCatagoryList valueAtIndex:touchButton.tag];
    
    SubCatagoryController *ctrl = [SubCatagoryController SubCatagoryControllerWithCatagoryId:gameCatagoryInfo.catagoryId];
    
    ctrl.hidesBottomBarWhenPushed = YES;
    UIViewController *fatherViewController = self.fatherViewController;
    [fatherViewController.parentContainerController.navigationController pushViewController:ctrl animated:YES];
    ctrl.customTitle = gameCatagoryInfo.catagoryName;
    
    //统计
    [ReportCenter report:ANALYTICS_EVENT_15050 label:gameCatagoryInfo.catagoryName];
}

@end
