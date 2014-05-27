//
//  NdBYBorderlessCell.m
//  NdComPlatformUI
//
//  Created by Sun pinqun on 13-7-16.
//  Copyright (c) 2013年 NdCP. All rights reserved.
//

#import "BaseBorderlessCell.h"
#import "UIImage+Extent.h"
typedef enum {
    Location_Single_Row,
    Location_First_Row,
    Location_Middle_Row,
    Location_Last_Row,
    Location_Not_Visible,
}CellLocation;

@interface BaseBorderlessCell()

@property (nonatomic, retain) UIImageView *borderlessBgView;
@property (nonatomic, retain) UIImageView *selectedBorderlessBgView;
@property (nonatomic, assign)   CellLocation    cellLocation;
@end

@implementation BaseBorderlessCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        //调整cell高度
        CGRect rc = self.frame;
        rc.size.height = 33;
        self.frame = rc;
        
        self.clipsToBounds = YES;
        self.backgroundColor = [UIColor clearColor];
        self.borderlessBgView = [[[UIImageView alloc] initWithFrame:CGRectZero] autorelease];
        self.selectedBorderlessBgView = [[[UIImageView alloc] initWithFrame:CGRectZero] autorelease];
        self.cellLocation = Location_Not_Visible;
        self.bNeedRedBorder = NO;
    }
    return self;
}


- (void)updateCellLocation:(CellLocation)cellLocation {
    if (_cellLocation != cellLocation) {
        _cellLocation = cellLocation;
        UIImage *bgImage = nil;
        UIImage *bgSelectedImage = nil;
        switch (_cellLocation) {
            case Location_Single_Row:
                bgImage = [UIImage imageNamed:@"bg_cell_singleRow" ];
                if (self.bNeedRedBorder) {
                    bgImage = [UIImage imageNamed:@"bg_cell_singleRow_Red" ];
                }
                bgSelectedImage = [UIImage imageNamed:@"bg_cell_singleRow_selected"];
                break;
            case Location_First_Row:
                bgImage = [UIImage imageNamed:@"bg_cell_firestRow"];
                if (self.bNeedRedBorder) {
                    bgImage = [UIImage imageNamed:@"bg_cell_firestRow_Red" ];
                }
                bgSelectedImage = [UIImage imageNamed:@"bg_cell_firestRow_selected" ];
                break;
            case Location_Middle_Row:
                bgImage = [UIImage imageNamed:@"bg_cell_midRow" ];
                if (self.bNeedRedBorder) {
                    bgImage = [UIImage imageNamed:@"bg_cell_midRow_Red" ];
                }
                bgSelectedImage = [UIImage imageNamed:@"bg_cell_midRow_selected"];
                break;
            case Location_Last_Row:
                bgImage = [UIImage imageNamed:@"bg_cell_lastRow" ];
                if (self.bNeedRedBorder) {
                    bgImage = [UIImage imageNamed:@"bg_cell_lastRow_Red" ];
                }
                bgSelectedImage = [UIImage imageNamed:@"bg_cell_lastRow_selected"];
                break;
            default:
                break;
        }
        
        if (bgImage && bgSelectedImage) {
            bgImage = [bgImage stretchableImageWithCenterPoint];
            bgSelectedImage = [bgSelectedImage stretchableImageWithWidth];
            self.borderlessBgView.image = bgImage;
            self.selectedBorderlessBgView.image = bgSelectedImage;
            self.backgroundView = self.borderlessBgView;
            self.selectedBackgroundView = self.selectedBorderlessBgView;
        }
    }
}

- (void)dealloc {
//    self.imgBundle = nil;
    self.borderlessBgView = nil;
    self.selectedBorderlessBgView = nil;
    self.jumpTitle = nil;
    self.jumpUrl = nil;
    [super dealloc];
}

- (int)getLocatonInTableView
{
    UITableView *table = (UITableView *)[self superview];
    
    if (![table isKindOfClass:[UITableView class]])table = (UITableView *)table.superview; //适配ios7
    
    NSIndexPath *indexPath = [table indexPathForCell:self];
    if (indexPath == nil) {
        return Location_Not_Visible;
    }
    
    int row = indexPath.row;
    int rowsInSection = [table numberOfRowsInSection:indexPath.section];
    if (rowsInSection == 1) {
        return Location_Single_Row;
    }
    else if (row == 0) {
        return Location_First_Row;
    }
    else if (row == rowsInSection - 1) {
        return Location_Last_Row;
    }
    else {
        return Location_Middle_Row;
    }
}

- (void)layoutSubviews {
	[super layoutSubviews];
    self.clipsToBounds = YES;
    self.cellLocation = Location_Not_Visible;
    self.backgroundColor = [UIColor clearColor];
    if (self.borderlessBgView == nil) {
        self.borderlessBgView = [[[UIImageView alloc] initWithFrame:CGRectZero] autorelease];
    }
    if (self.selectedBorderlessBgView == nil) {
        self.selectedBorderlessBgView = [[[UIImageView alloc] initWithFrame:CGRectZero] autorelease];

    }
    
    [self updateCellLocation:[self getLocatonInTableView]];
    self.borderlessBgView.frame = self.bounds;
    self.selectedBorderlessBgView.frame = self.bounds;
}

@end
