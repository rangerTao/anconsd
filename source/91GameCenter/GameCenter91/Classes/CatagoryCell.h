//
//  ClassificationCell.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/11/13.
//
//

#import <UIKit/UIKit.h>

@interface CatagoryCell : UITableViewCell

@property (nonatomic, assign) id fatherViewController;

- (void)refreshCellWithAppCatagoryList:(NSArray *)appCatagoryList andIndexPath:(NSIndexPath *)indexPath;

@end
