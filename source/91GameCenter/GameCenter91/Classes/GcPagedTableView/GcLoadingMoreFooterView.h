//
//  GcLoadingMoreFooterView.h
//  Miu Ptt
//
//  Created by Sun pinqun on 12-10-13.
//  Copyright 2012 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GcLoadingMoreFooterView : UIView
@property(nonatomic, readwrite) BOOL showActivityIndicator;
@property(nonatomic, readwrite, getter = isRefreshing) BOOL refreshing;
@property(nonatomic, readwrite) BOOL enabled;   // in case that no more items to load
@property(nonatomic, readwrite) UITextAlignment textAlignment;
@end
