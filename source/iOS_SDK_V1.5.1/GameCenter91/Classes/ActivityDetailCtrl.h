//
//  ActivityDetailCtrl.h
//  GameCenter91
//
//  Created by  hiyo on 12-9-4.
//  Copyright 2012 Nd. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface ActivityDetailCtrl : UIViewController <UIWebViewDelegate>

@property (nonatomic, retain) NSString *contentUrl;
@property (nonatomic, assign) int activityId;
@property (nonatomic, retain) NSString *appIdentifier;

@end
