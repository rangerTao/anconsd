//
//  SectionHeaderCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 11/12/13.
//
//

#import <UIKit/UIKit.h>
#import "HomePageSectionType.h"

@interface SectionHeaderCell : UITableViewCell

@property (nonatomic, retain) NSArray *appList;
@property (nonatomic, assign) id fatherController;

- (void)refreshCellWithSectionHeaderType:(SECTION_TYPE)sectionHeaderType;

@end
