//
//  FullScreenImageViewController.h
//  GameCenter91
//
//  Created by kensou on 12-12-5.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FullScreenImageViewController : UIViewController
{
    UIScrollView *scroll;
}
- (void)setupImageUrls:(NSArray *)imageUrls;

- (void)showIndex:(int)index;

+ (void)show:(int)index ofImages:(NSArray *)images inController:(UIViewController *)parentCtr;

@end
