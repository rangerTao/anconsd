//
//  ColorfulLabelView.m
//  GameCenter91
//
//  Created by hiyo on 13-2-1.
//  Copyright (c) 2013年 __MyCompanyName__. All rights reserved.
//

#import "ColorfulLabelView.h"
#import "CommUtility.h"

@implementation ColorfulLabelView

+ (ColorfulLabelView *)colorfulLabelView:(NSString *)labelText fontSize:(NSInteger)fontSize bgColor:(NSString *)bgHexARGB fontColor:(NSString *)fontHexARGB bVogue:(BOOL)bVogue
{
    ColorfulLabelView *labelView = [[[ColorfulLabelView alloc] init] autorelease];
    labelView.backgroundColor = bVogue ? [UIColor clearColor]: [CommUtility colorWithHexRGB:bgHexARGB];
    
    UILabel *label = [[[UILabel alloc] init] autorelease];
    label.text = labelText;
    label.textColor = bVogue ? [CommUtility colorWithHexRGB:fontHexARGB] : [UIColor whiteColor];
    label.font = [UIFont systemFontOfSize:fontSize];
    label.backgroundColor = [UIColor clearColor];
    label.textAlignment = UITextAlignmentCenter;
    CGSize size = [label.text sizeWithFont:label.font];
    label.frame = CGRectMake(MARGIN_WORD_2_EDGE, 0, size.width, HEIGHT_LABEL);
    
    labelView.frame = CGRectMake(0, 0, MARGIN_WORD_2_EDGE*2+size.width, HEIGHT_LABEL);
    
    if (bVogue) {
        UIImageView *bg = [[[UIImageView alloc] initWithFrame:labelView.bounds] autorelease];
        UIImage *img = [UIImage imageNamed:@"button_1.png"];
        bg.image = [img stretchableImageWithLeftCapWidth:img.size.width/2.0 topCapHeight:img.size.height/2.0];
        [labelView addSubview:bg];
    }
    
    [labelView addSubview:label];
    
    return labelView;
}

+ (ColorfulLabelView *)colorfulLabelView:(NSString *)labelText bgColor:(NSString *)bgHexARGB fontColor:(NSString *)fontHexARGB bVogue:(BOOL)bVogue
{
    return [self colorfulLabelView:labelText fontSize:12 bgColor:bgHexARGB fontColor:fontHexARGB bVogue:bVogue];
}

+ (ColorfulLabelView *)colorfulLabelViewWithPackIconsStr:(NSString *)iconsStr bVogue:(BOOL)bVogue
{
    ColorfulLabelView *topView = [[[ColorfulLabelView alloc] init] autorelease];
    
    int offsetX = 0.0;
    NSArray *arr = nil;
    arr = [CommUtility unPackRecommendIconsStr:iconsStr];
    for (NSDictionary *dic in arr) {
        NSString *bgHexARGB = [dic objectForKey:KEY_RI_BGCOLOR];
        NSString *fontHexARGB = [dic objectForKey:KEY_RI_FONTCOLOR];
        NSString *labelText = [dic objectForKey:KEY_RI_NAME];
        ColorfulLabelView *itemView = [ColorfulLabelView colorfulLabelView:labelText bgColor:bgHexARGB fontColor:fontHexARGB bVogue:bVogue];
        itemView.frame = CGRectOffset(itemView.frame, offsetX, 0);
        [topView addSubview:itemView];
        
        offsetX += (MARGIN_LABELS+CGRectGetWidth(itemView.frame));
    }
    topView.frame = CGRectMake(0, 0, offsetX-MARGIN_LABELS, HEIGHT_LABEL);
    
    return topView;
}

+ (ColorfulLabelView *)colorfulLabelView:(CLV_FIX_TYPE)type bVogue:(BOOL)bVogue
{
    NSString *bgHexARGB = nil;
    NSString *fontHexARGB = @"FFFFFFFF";
    NSString *labelText = nil;
    switch (type) {
        case CLV_FIX_AnIos:
            bgHexARGB = @"FF137BB4";
            labelText = @"安卓/iOS互通";
            break;
        case CLV_FIX_OFFICAIL:
            bgHexARGB = @"FFF95426";
            labelText = @"官方版本";
            break;
        case CLV_FIX_VIRUS:
            bgHexARGB = @"FF1DCC4F";
            labelText = @"无病毒";
            break;
        case CLV_FIX_GIFT:
            bgHexARGB = @"FFF4B300";
            labelText = @"礼包";
            break;
        case CLV_FIX_NewServer:
            bgHexARGB = @"FF91D100";
            labelText = @"新服";
            break;
        default:
            break;
    }
    ColorfulLabelView *labelView = [ColorfulLabelView colorfulLabelView:labelText bgColor:bgHexARGB fontColor:fontHexARGB bVogue:bVogue];
    return labelView;
}

+ (ColorfulLabelView *)colorfulLabelViewWithArray:(NSArray *)viewArr
{
    ColorfulLabelView *topView = [[[ColorfulLabelView alloc] init] autorelease];
    
    int offsetX = 0.0;
    for (ColorfulLabelView *itemView in viewArr) {
        itemView.frame = CGRectOffset(itemView.frame, offsetX, 0);
        [topView addSubview:itemView];
        
        offsetX += (MARGIN_LABELS+CGRectGetWidth(itemView.frame));
    }
    topView.frame = CGRectMake(0, 0, offsetX-MARGIN_LABELS, HEIGHT_LABEL);
    
    return topView;
}

@end
