//
//  CustomAlertView.h
//  GameCenter91
//
//  Created by Sun pinqun on 12-10-18.
//  Copyright 2012 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CustomAlertView : UIAlertView

- (id)initWithCloseButton:(NSString *)title message:(NSString *)message delegate:(id)delegate firstButtonTitle:(NSString *)firstButtonTitle secondButtonTitles:(NSString *)secondButtonTitles;

@end
