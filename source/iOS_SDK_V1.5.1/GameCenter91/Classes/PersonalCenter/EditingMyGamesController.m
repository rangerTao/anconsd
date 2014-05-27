//
//  EditingMyGames.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/25/13.
//
//

#import "EditingMyGamesController.h"
#import "MyGameInfo.h"
#import "EditingMyGamesCell.h"
#import "UIViewController+Extent.h"
#import "AppInfo.h"
#import "UserData.h"
#import "NSArray+Extent.h"
#import "UITableViewCell+Addition.h"
#import "CommUtility.h"

#define MY_TOP_GAMES_SECTION 0
#define MY_REST_GAMES_SECTION 1

@interface EditingMyGamesController ()

@property (nonatomic, retain) NSMutableArray *myGamesToBeEdited;

@end

@implementation EditingMyGamesController

- (void)dealloc
{
    self.myGamesToBeEdited = nil;
    self.selectedIndexPath = nil;
    self.appList = nil;
    [super dealloc];
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.customTitle = @"编辑";
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
    [self assignMyGamesTobeEdited];
    
    self.hidesBottomBarWhenPushed = YES;
    
    self.tableView.backgroundView = nil;
    self.view.backgroundColor = [UIColor colorWithRed:0xD1/255.0 green:0xD3/255.0 blue:0xD5/255.0 alpha:1.0];
    self.tableView.editing = YES;
    
#ifdef __IPHONE_7_0
    if ([CommUtility isIOS7]) {
        self.edgesForExtendedLayout = UIRectEdgeNone;//适配视图提高问题
    }
#endif
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self.tableView reloadData];
}

- (void)viewWillDisappear:(BOOL)animated
{
    NSMutableArray *topGameIdsList = [NSMutableArray array];
    for (NSInteger index = 0; index < [[self.myGamesToBeEdited valueAtIndex:0] count]; index++) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:index inSection:0];
        AppInfo *game = [self.myGamesToBeEdited valueAtIndexPath:indexPath];
        [topGameIdsList addObject:game.identifier];
    }
    
    NSMutableArray *allGameIdsList = [NSMutableArray arrayWithArray:[self.myGamesToBeEdited valueAtIndex:0]];
    if ([self.myGamesToBeEdited count] == 2) {
        for (NSInteger index = 0; index < [[self.myGamesToBeEdited valueAtIndex:1] count]; index++) {
            NSIndexPath *indexPath = [NSIndexPath indexPathForRow:index inSection:1];
            AppInfo *game = [self.myGamesToBeEdited valueAtIndexPath:indexPath];
            [allGameIdsList addObject:game];
        }
    }
    
    [[UserData sharedInstance] quitGameEditPageWithGameIdsList:topGameIdsList gameList:allGameIdsList];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    NSInteger numberOfRows = [[self.myGamesToBeEdited valueAtIndex:section] count];
	
	return numberOfRows;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    EditingMyGamesCell *myGameCell = [EditingMyGamesCell dequeOrCreateInTable:self.tableView];
    [myGameCell refreshEditingMyGamesCell:self.myGamesToBeEdited withIndexPath:indexPath];
    myGameCell.showsReorderControl = YES;
        
    return myGameCell;
    
}

- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)sourceIndexPath toIndexPath:(NSIndexPath *)destinationIndexPath
{
    MyGameInfo *myGameToBeMoved = [[[self.myGamesToBeEdited valueAtIndexPath:sourceIndexPath] copy] autorelease];
    [[self.myGamesToBeEdited valueAtIndex:sourceIndexPath.section] removeObjectAtIndex:[sourceIndexPath row]];
    [[self.myGamesToBeEdited valueAtIndex:destinationIndexPath.section] insertObject:myGameToBeMoved atIndex:destinationIndexPath.row];
    
    [self performSelector:@selector(delayRefresh) withObject:nil afterDelay:0.1];
}

// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath{
    return UITableViewCellEditingStyleNone;
}

- (BOOL)tableView:(UITableView *)tableview shouldIndentWhileEditingRowAtIndexPath:(NSIndexPath *)indexPath {
    return NO;
}

#pragma mark - Table view delegate

- (NSIndexPath *)tableView:(UITableView *)tableView targetIndexPathForMoveFromRowAtIndexPath:(NSIndexPath *)sourceIndexPath toProposedIndexPath:(NSIndexPath *)proposedDestinationIndexPath
{	
	return proposedDestinationIndexPath;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if (section == 0){
        //没有游戏时显示提示文字时使用
        if ([[self.myGamesToBeEdited valueAtIndex:MY_TOP_GAMES_SECTION] count] == 0 && [[self.myGamesToBeEdited valueAtIndex:MY_REST_GAMES_SECTION] count] == 0) {
            return 30.0;
        }
        //我关注的游戏为空时，展现方框时使用
        if ([[self.myGamesToBeEdited valueAtIndex:0] count] == 0) {
            return 100.0;
        }
        //我关注的游戏存在时使用
        else {
            return 35.0;
        }
    }
    //我关注的游戏下方的提示文字使用
    return 30.0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    if (section == MY_REST_GAMES_SECTION) {
        return 66.0;
    }
    return 1.0;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
    if (section == MY_REST_GAMES_SECTION) {
        return @" ";
    }
    return nil;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    if (section == 0) {
        
        UIImageView *imageView = [[[UIImageView alloc] initWithFrame:CGRectMake(0, 10, 300, 100)] autorelease];
        
        UILabel *interactionPromptLabel = [[[UILabel alloc] init] autorelease];
        interactionPromptLabel.textAlignment = UITextAlignmentCenter;
        interactionPromptLabel.font = [UIFont systemFontOfSize:14];
        interactionPromptLabel.textColor = [UIColor colorWithRed:0x66/255.0 green:0x66/255.0 blue:0x66/255.0 alpha:1.0];
        interactionPromptLabel.backgroundColor = [UIColor clearColor];
        interactionPromptLabel.frame = CGRectMake(10, 10, 300, 20);
        interactionPromptLabel.text =  @"点击≡拖拽，调整“我的游戏”列表";
        
        [imageView addSubview:interactionPromptLabel];
        
        if ([[self.myGamesToBeEdited valueAtIndex:MY_TOP_GAMES_SECTION] count] == 0 && [[self.myGamesToBeEdited valueAtIndex:MY_REST_GAMES_SECTION] count] > 0) {
            
            UIImageView *subImageView = [[[UIImageView alloc]initWithFrame:CGRectMake(10, 35, 300, 60)] autorelease];
            
            UILabel *sectionLable = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, 300, 20)] autorelease];
            sectionLable.textAlignment = UITextAlignmentCenter;
            sectionLable.font = [UIFont systemFontOfSize:14];
            sectionLable.textColor = [UIColor colorWithRed:0x66/255.0 green:0x66/255.0 blue:0x66/255.0 alpha:1.0];
            sectionLable.backgroundColor = [UIColor clearColor];

            sectionLable.frame = CGRectMake(0, 0, 300, 60);
            sectionLable.text = @"你可以移动游戏到该位置哟";
            UIImage *image = [UIImage imageNamed:@"activity_area_single_row"];
            image = [image stretchableImageWithLeftCapWidth:image.size.width/2 topCapHeight:image.size.height/2];
            subImageView.image = image;
            
            [subImageView addSubview:sectionLable];
            [imageView addSubview:subImageView];
        }
        
        return imageView;
    }
    
    if (section == MY_REST_GAMES_SECTION) {
        
        UILabel *secondSectionLable = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, 320, 20)] autorelease];
        secondSectionLable.textAlignment = UITextAlignmentCenter;
        secondSectionLable.font = [UIFont systemFontOfSize:14];
        secondSectionLable.textColor = [UIColor colorWithRed:0x66/255.0 green:0x66/255.0 blue:0x66/255.0 alpha:1.0];
        secondSectionLable.backgroundColor = [UIColor clearColor];
        
        if ([[self.myGamesToBeEdited valueAtIndex:MY_TOP_GAMES_SECTION] count] > 0 || [[self.myGamesToBeEdited valueAtIndex:MY_REST_GAMES_SECTION] count] > 0) {
            secondSectionLable.text = @"上面游戏将在“我的游戏”中顺序显示";
        } else if ([[self.myGamesToBeEdited valueAtIndex:MY_TOP_GAMES_SECTION] count] == 0 && [[self.myGamesToBeEdited valueAtIndex:MY_REST_GAMES_SECTION] count] == 0) {
            secondSectionLable.text = @"安装更多游戏，您将可以调整游戏顺序";
        }
        return secondSectionLable;
    }
    return nil;
}

#pragma mark - others

- (void)resetMyGamesToBeEdited
{
    if ([[self.myGamesToBeEdited valueAtIndex:MY_TOP_GAMES_SECTION] count] == 5) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:4 inSection:MY_TOP_GAMES_SECTION];
        MyGameInfo *myGameInfo = [[[self.myGamesToBeEdited valueAtIndexPath:indexPath] copy] autorelease];
        [[self.myGamesToBeEdited valueAtIndex:MY_TOP_GAMES_SECTION] removeObjectAtIndex:4];
        [[self.myGamesToBeEdited valueAtIndex:MY_REST_GAMES_SECTION] insertObject:myGameInfo atIndex:0];
    }
}

- (void)assignMyGamesTobeEdited
{
    self.myGamesToBeEdited = [NSMutableArray array];
    
    UserData *user = [UserData sharedInstance];
    
    NSMutableArray *myTopGames = [NSMutableArray array];
    for (NSInteger index = 0; index < [self.appList count]; index++) {
        if ([myTopGames count] == 4) {
            break;
        } else {
            AppInfo *appInfo = [self.appList valueAtIndex:index];
            if ([user.myGameIdsList containsObject:appInfo.identifier]) {
                [myTopGames addObject:appInfo];
            }
        }
    }
    [self.myGamesToBeEdited addObject:myTopGames];
    
    NSMutableArray *myRestGames = [NSMutableArray array];
    for (NSInteger index = [myTopGames count]; index < [self.appList count]; index++) {
        AppInfo *appInfo = [self.appList valueAtIndex:index];
        [myRestGames addObject:appInfo];
    }
    [self.myGamesToBeEdited addObject:myRestGames];
}

- (void)delayRefresh
{
    if ([[self.myGamesToBeEdited valueAtIndex:MY_TOP_GAMES_SECTION] count] > 4) {
        [self resetMyGamesToBeEdited];
    }
    
    [self.tableView reloadData];
}

@end
