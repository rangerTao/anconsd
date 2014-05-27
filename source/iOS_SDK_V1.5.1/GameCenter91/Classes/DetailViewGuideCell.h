//
//  GuideCellInDetailView.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-28.
//
//

#import <UIKit/UIKit.h>
#import "BaseBorderlessCell.h"
@interface DetailViewGuideCell : BaseBorderlessCell
- (void)updateWithInfos:(NSArray *)guidelist cellRowNum:(unsigned int)rowNum target:(id)target;

@end
