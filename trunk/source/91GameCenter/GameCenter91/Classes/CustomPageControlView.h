//
//  CustomPageControlView.h
//  GameCenter91
//
//  Created by Li.Binbin on 11/12/13.
//
//

#import <UIKit/UIKit.h>

@interface CustomPageControlView : UIView

+ (CustomPageControlView *)customPageControlViewWithTotalNumber:(NSInteger)totalNumber;

- (void)resetCustomStarsViewWithCurrentPageNumber:(NSInteger)currentPageNumber;

@end
