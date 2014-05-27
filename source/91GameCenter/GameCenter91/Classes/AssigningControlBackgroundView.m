//
//  AssigningControlColor.m
//  GameCenter91
//
//  Created by Li.Binbin on 11/15/13.
//
//

#import "AssigningControlBackgroundView.h"
#import "CommUtility.h"
#import "Colors.h"

@implementation AssigningControlBackgroundView

+ (void)assignCellSelectedBackgroundView:(UITableViewCell *)cell
{
    UIView *myBackView = [[[UIView alloc] initWithFrame:cell.frame] autorelease];
    myBackView.backgroundColor = [CommUtility colorWithHexRGB:CELL_SELECTED_COLOR];
    cell.selectedBackgroundView = myBackView;
}

@end
