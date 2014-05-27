//
//  GameDetailDownloadCell.h
//  GameCenter91
//
//  Created by hiyo on 12-9-20.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@class AppDetailViewInfo;
@interface GameDetailDownloadCell : UITableViewCell

@property(nonatomic, retain) IBOutlet UIImageView *cell_icon;
@property(nonatomic, retain) IBOutlet UILabel *nameLabel;
@property(nonatomic, retain) IBOutlet UILabel *downNumLabel;
@property(nonatomic, retain) IBOutlet UILabel *versionLabel;
@property(nonatomic, retain) IBOutlet UILabel *fileSizeLable;
@property(nonatomic, retain) IBOutlet UILabel *languageLable;

- (void)updateCellWithInfo:(AppDetailViewInfo *)info;

@end
