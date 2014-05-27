//
//  GameDetailWebCtrl.h
//  GameCenter91
//
//  Created by hiyo on 12-10-18.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GameDetailWebCtrl : UIViewController <UIWebViewDelegate>
{
    UIWebView *webView;
    NSString *myUrl;
    int appid;
    BOOL bHasSegment;
}
@property (nonatomic, retain) UIWebView *webView;
@property (nonatomic, retain) NSString *myUrl;
@property (nonatomic, assign) int appid;
@property (nonatomic, assign) BOOL bHasSegment;

+ (GameDetailWebCtrl *)GameDetailWebCtrlWithUrl:(NSString *)aUrl;
- (void)initWebView;
- (void)loadWebView:(NSString *)urlStr;

@end
