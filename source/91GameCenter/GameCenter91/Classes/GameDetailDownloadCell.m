//
//  GameDetailDownloadCell.m
//  GameCenter91
//
//  Created by hiyo on 12-9-20.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "GameDetailDownloadCell.h"
#import "ColorfulLabelView.h"

#import "UIImageView+WebCache.h"
#import "CommUtility.h"
#import "UserData.h"
#import "AppDetailCacheInfo.h"
#define RGB(r,g,b)          [UIColor colorWithRed:r/255.0 green:g/255.0 blue:b/255.0 alpha:1.0]


@interface GameDetailDownloadCell()

@property (nonatomic, assign) ColorfulLabelView *colorLabelView;

- (NSString *)convertSupportPlatform:(NSString *)src;
- (BOOL)supportBothAndroidAndIos:(NSString *)src;

@end

@implementation GameDetailDownloadCell


#pragma mark -
- (void)dealloc
{
    [super dealloc];
}

#pragma mark - 

- (void)updateCellWithInfo:(AppDetailViewInfo *)info
{
    
    [self.cell_icon setImageWithURL:[NSURL URLWithString:info.appIconUrl] placeholderImage:[UIImage imageNamed:@"defaultAppIcon.png"]];
    self.nameLabel.text = info.appName;
    self.fileSizeLable.text = [NSString stringWithFormat:@"%@  |", [CommUtility readableFileSize:info.fileSize]];
    self.versionLabel.text = [NSString stringWithFormat:@"版本%@  |", info.appVersionName];
    self.downNumLabel.text = [NSString stringWithFormat:@"%d 次下载", info.downloadNumber];
    self.languageLable.text = (info.isChinese == 1) ? @"中文": @"非中文";
//    CGSize label2Size = [self.cell_label2.text sizeWithFont:self.cell_label2.font];
//    self.cell_version.frame = CGRectMake(self.cell_label2.frame.origin.x+label2Size.width, self.cell_version.frame.origin.y, CGRectGetWidth(self.cell_version.frame), CGRectGetHeight(self.cell_version.frame));
//    
//    CGSize size = [cell_number.text sizeWithFont:cell_number.font];
//    CGRect rect = cell_number.frame;
//    rect.size.width = size.width;
//    cell_number.frame = rect;
//    rect = cell_numAppend.frame;
//    rect.origin.x = CGRectGetMaxX(cell_number.frame) + 3.0;
//    cell_numAppend.frame = rect; 
//    rect = cell_time.frame;
//    rect.origin.x = CGRectGetMaxX(cell_numAppend.frame) + 3.0;
//    cell_time.frame = rect; 
//    
    //标签
//    [colorLabelView removeFromSuperview];
//    CGFloat originLabelX = CGRectGetMinX(cell_label1.frame);
//    CGFloat originLabelY = 5 + CGRectGetMaxY(cell_numAppend.frame);
//    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:3];
//    ColorfulLabelView *official = [ColorfulLabelView colorfulLabelView:CLV_FIX_OFFICAIL bVogue:NO];
//    [arr addObject:official];
//    ColorfulLabelView *virus = [ColorfulLabelView colorfulLabelView:CLV_FIX_VIRUS bVogue:NO];
//    [arr addObject:virus];
//    if ([self supportBothAndroidAndIos:info.supportPlatforms]) {
//        ColorfulLabelView *anios = [ColorfulLabelView colorfulLabelView:CLV_FIX_AnIos bVogue:NO];
//        [arr addObject:anios];
//    }
//    colorLabelView = [ColorfulLabelView colorfulLabelViewWithArray:arr];
//    colorLabelView.frame = CGRectOffset(colorLabelView.frame, originLabelX, originLabelY);
//    [self addSubview:colorLabelView];
}

- (NSString *)convertSupportPlatform:(NSString *)src
{
    NSArray *arr = [src componentsSeparatedByString:@","];
    NSString *retStr = @"";
    BOOL bFirst = YES;
    for (NSString *item in arr) {
        int platform = [item intValue];
        switch (platform) {
            case 1:
                retStr = (bFirst ? [retStr stringByAppendingString:@"iPhone"] : [retStr stringByAppendingString:@"/iPhone"]);
                bFirst = NO;
                break;
            case 2:
                retStr = (bFirst ? [retStr stringByAppendingString:@"WM"] : [retStr stringByAppendingString:@"/WM"]);
                bFirst = NO;
                break;
            case 4:
                retStr = (bFirst ? [retStr stringByAppendingString:@"S60"] : [retStr stringByAppendingString:@"/S60"]);
                bFirst = NO;
                break;
            case 8:
                retStr = (bFirst ? [retStr stringByAppendingString:@"Android"] : [retStr stringByAppendingString:@"/Android"]);
                bFirst = NO;
                break;
            default:
                break;
        }
    }
    return retStr;
}

- (BOOL)supportBothAndroidAndIos:(NSString *)src
{
    NSArray *arr = [src componentsSeparatedByString:@","];
    int temp = 0x00;
    for (NSString *item in arr) {
        temp |= [item intValue];
    }
    return (temp&9)==9;
}

@end
