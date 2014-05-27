//
//  GuideCellInDetailView.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-10-28.
//
//

#import "DetailViewGuideCell.h"
#import "GuideCacheInfo.h"
#import "UIImage+Extent.h"
#import "ColorfulImage.h"
#import "CommUtility.h"
#define BASE_TAG    100
#define BTN_BASE_TAG    200
#define ADAPT_X 12 //适配ios7

@interface DetailViewGuideCell ()
@property (retain, nonatomic) IBOutlet UILabel *label0;
@property (retain, nonatomic) IBOutlet UILabel *label1;
@property (retain, nonatomic) IBOutlet UILabel *label2;

@end

@implementation DetailViewGuideCell


- (void)dealloc {
    [_label0 release];
    [_label1 release];
    [_label2 release];
    [super dealloc];
}
- (void)updateWithInfos:(NSArray *)guidelist cellRowNum:(unsigned int)rowNum target:(id)target
{
    NSUInteger startIndex = rowNum * 4;
    
    for (int i = 0; i < 4; i++) {
        if (startIndex + i < [guidelist count]) {
            GuideItem *item = [guidelist objectAtIndex:startIndex + i];
            if (item) {
                UILabel *label = (UILabel *)[self viewWithTag:BASE_TAG + i];
                label.font = [UIFont systemFontOfSize:12.0];
                label.hidden = NO;
                UIButton *btn = (UIButton *) [self viewWithTag:BTN_BASE_TAG + i];
                [btn setTitle:item.guideName forState:UIControlStateNormal];
                btn.titleLabel.font = [UIFont systemFontOfSize:14.0];
                [btn setTitleColor:[UIColor grayColor] forState:UIControlStateHighlighted];
                [btn addTarget:target action:@selector(guideCellBtnPress:) forControlEvents:UIControlEventTouchUpInside];
                UIImage *bgSelectedImage = [ColorfulImage imageWithColor:[CommUtility colorWithHexRGB:@"b1daec"]];
                bgSelectedImage = [bgSelectedImage stretchableImageWithLeftCapWidth:bgSelectedImage.size.width / 2 topCapHeight:bgSelectedImage.size.height/2];
                [btn setBackgroundImage:bgSelectedImage forState:UIControlStateHighlighted];
                btn.hidden = NO;
                btn.tag = startIndex + i;
                
                if ([CommUtility isIOS7]) {//适配ios7 调整frame
                    CGRect rect = label.frame;
                    rect.origin.x += ADAPT_X;
                    label.frame = rect;
                    
                    rect = btn.frame;
                    rect.origin.x += ADAPT_X;
                    btn.frame = rect;

                }
            }
            
        }
    }

}

@end
