//
//  UIImage+Extent.h
//  GameCenter91
//
//  Created by Sun pinqun on 12-9-25.
//  Copyright 2012 net dragon. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIImage (SplitImageIntoTwoParts)
+ (NSArray*)splitImageIntoTwoParts:(UIImage*)image;

- (UIImage*)stretchableImageWithWidth;
- (UIImage*)stretchableImageWithCenterPoint;

@end
