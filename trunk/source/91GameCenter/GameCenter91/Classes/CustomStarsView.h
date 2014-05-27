//
//  CustomStarsView.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/31/13.
//
//

#import <UIKit/UIKit.h>

@interface CustomStarsView : UIView

+ (CustomStarsView *)customStarsViewWithNumber:(NSInteger)number;
- (void)resetCustomStarsViewWithNumber:(NSInteger)number;

@end
