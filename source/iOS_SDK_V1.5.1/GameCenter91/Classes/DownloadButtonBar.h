//
//  DownloadButtonBar.h
//  GameCenter91
//
//  Created by Sun pinqun on 13-1-25.
//  Copyright (c) 2013年 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>

@class AppDetailViewInfo;
@interface DownloadButtonBar : UIView
@property(nonatomic, retain) NSString *identifier;

- (id)initWithView:(UIView *)view;
- (float)barHeight;

+ (DownloadButtonBar *)downloadBarWithAppIdentifier:(NSString *)aIdentifer
                                      appDetailInfo:(AppDetailViewInfo *)info
                                          superView:(UIView *)superView
                                          upperView:(UIView *)upperView;

+ (DownloadButtonBar *)downloadBarWithAppIdentifier:(NSString *)aIdentifer
                                      appDetailInfo:(AppDetailViewInfo *)info
                                          superView:(UIView *)superView
                                          upperView:(UIView *)upperView
                                        showUpgreda:(BOOL)isSDkUpgrade;
@end
