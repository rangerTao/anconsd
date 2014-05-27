//
//  SubClassificationController.m
//  GameCenter91
//
//  Created by Li.Binbin on 10/12/13.
//
//

#import "SubCatagoryController.h"
#import "CommUtility.h"
#import "SubCatagoryTable.h"

@interface SubCatagoryController ()

@end

@implementation SubCatagoryController 

+ (SubCatagoryController *)SubCatagoryControllerWithCatagoryId:(int)catagoryId
{
    SubCatagoryTable *ctr0 = [[[SubCatagoryTable alloc] initWithType:GAME_DETAIL_HOT andCatagoryId:catagoryId] autorelease];
    SubCatagoryTable *ctr1 = [[[SubCatagoryTable alloc] initWithType:GAME_DETAIL_NEW andCatagoryId:catagoryId] autorelease];
    
    CGFloat height = [CommUtility viewHeightWithStatusBar:YES navBar:YES tabBar:NO otherExcludeHeight:[self defaultSegmentHeight]];
    SubCatagoryController * subCatagoryController = (SubCatagoryController *)[self controllerWithSubControllers:[NSArray arrayWithObjects:ctr0, ctr1, nil] subviewHight:height];
    return subCatagoryController;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    
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

@end
