//
//  ColorfulLabelView.h
//  GameCenter91
//
//  Created by hiyo on 13-2-1.
//  Copyright (c) 2013年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

#define MARGIN_WORD_2_EDGE  8.0
#define HEIGHT_LABEL        16.0
#define MARGIN_LABELS       5.0

typedef enum _CLV_FIX_TYPE {
	CLV_UNKNOWN = 0x00,
	CLV_FIX_AnIos,          //安卓/ios互通	
	CLV_FIX_OFFICAIL,		//官方版本	
    CLV_FIX_VIRUS,          //无病毒
    CLV_FIX_GIFT,           //礼包
    CLV_FIX_NewServer,      //新服
}CLV_FIX_TYPE;

@interface ColorfulLabelView : UIView

+ (ColorfulLabelView *)colorfulLabelView:(NSString *)labelText fontSize:(NSInteger)fontSize bgColor:(NSString *)bgHexARGB fontColor:(NSString *)fontHexARGB bVogue:(BOOL)bVogue;
+ (ColorfulLabelView *)colorfulLabelView:(NSString *)labelText bgColor:(NSString *)bgHexARGB fontColor:(NSString *)fontHexARGB bVogue:(BOOL)bVogue;
+ (ColorfulLabelView *)colorfulLabelViewWithPackIconsStr:(NSString *)iconsStr bVogue:(BOOL)bVogue;
+ (ColorfulLabelView *)colorfulLabelView:(CLV_FIX_TYPE)type bVogue:(BOOL)bVogue; 
+ (ColorfulLabelView *)colorfulLabelViewWithArray:(NSArray *)viewArr;

@end
