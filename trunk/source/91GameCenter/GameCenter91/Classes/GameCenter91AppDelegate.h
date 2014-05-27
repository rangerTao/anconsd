//
//  GameCenter91AppDelegate.h
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-1.
//  Copyright NetDragon WebSoft Inc. 2012. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GameCenter91AppDelegate : NSObject <UIApplicationDelegate, UITabBarControllerDelegate> {
    UIWindow *window;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) UIImageView *defaultImageView;
@end

