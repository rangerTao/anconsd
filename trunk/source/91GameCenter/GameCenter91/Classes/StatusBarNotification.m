//
//  StatusBarNotification.m
//
//  Created by Sun pinqun on 12-8-2.
//  Copyright net dragon 2012. All rights reserved.
//

#import "StatusBarNotification.h"
#import "UIView+Addition.h"
#import <QuartzCore/CALayer.h>

#define kSecondsVisibleDelay 3.0f

@interface StatusBarNotification()

-(void)addNotificationViewWithMessage:(NSString *)message;
-(void)addNotificationViewWithView:(UIView *)view;
-(void)showNotificationView:(UIView *)messageView;

@end


@implementation StatusBarNotification

+(StatusBarNotification *)shared
{
    static StatusBarNotification *instance = nil;
    if(instance == nil) {
        instance = [[StatusBarNotification alloc] init];
    }
    return instance;
}

-(id)init
{
    if( (self = [super init]) ) {
        messageQueue = [[NSMutableArray alloc] init];
        showingNotification = NO;
        
        UIWindow *window = [[UIWindow alloc] initWithFrame:CGRectZero];
//        [window setBackgroundColor:[UIColor grayColor]];
        window.windowLevel = UIWindowLevelStatusBar + 1.0f;
        window.hidden = NO;
        notificationView = window;
    }
    return self;
}

-(void)dealloc
{
    [messageQueue release];
    [notificationView release];
    
    [super dealloc];
}

#pragma messages
+(void)notificationWithMessage:(NSString *)message
{
    [[StatusBarNotification shared] addNotificationViewWithMessage:message];
}

+(void)notificationWithView:(UIView *)view
{
    [[StatusBarNotification shared] addNotificationViewWithView:view];
}

-(void)addNotificationViewWithView:(UIView *)view
{
    [messageQueue addObject:view];
    
    if(!showingNotification) {
        [self showNotificationView:view];
    }
}

-(void)addNotificationViewWithMessage:(NSString *)message
{
//    UILabel *label = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, 320, 44)] autorelease];
//    [label setText:message];
//    [label setFont:[UIFont systemFontOfSize:30.0f]];
//    [label setTextAlignment:UITextAlignmentCenter];
//    [label setTextColor:[UIColor whiteColor]];
//    [label setBackgroundColor:[UIColor clearColor]];
//    
//    [self addNotificationViewWithView:label];
    StatusBarNotification *temp = (StatusBarNotification *)[[self class] loadFromNIB];
    temp.layer.borderWidth = 0.4;
    temp.layer.borderColor = [UIColor lightGrayColor].CGColor;
    temp.layer.cornerRadius = 8.0;
    temp.label1.text = @"91游戏中心";
    temp.label1.textColor = [UIColor colorWithRed:0x1c/255.0 green:0x1d/255.0 blue:0x1e/255.0 alpha:1.0];
    temp.label2.text = message;
    temp.label2.textColor = [UIColor colorWithRed:0x54/255.0 green:0x55/255.0 blue:0x57/255.0 alpha:1.0];
    [self addNotificationViewWithView:temp];
}

-(void)showNotificationView:(UIView *)messageView
{
    showingNotification = YES;

    notificationView.frame = CGRectMake(0, -messageView.frame.size.height, 320, messageView.frame.size.height);
    [notificationView addSubview:messageView];
    
    [UIView beginAnimations:@"" context:nil];
    [UIView setAnimationDelegate:self];
    [UIView setAnimationDidStopSelector:@selector(showNotificationAnimationComplete:finished:context:)];
    [UIView setAnimationDuration:0.5f];
    
    [notificationView setFrame:CGRectMake(notificationView.frame.origin.x, notificationView.frame.origin.y+notificationView.frame.size.height, notificationView.frame.size.width, notificationView.frame.size.height)];
    
    [UIView commitAnimations];
}

-(void)showNotificationAnimationComplete:(NSString*)animationID finished:(NSNumber*)finished context:(void*)context
{
    [self performSelector:@selector(hideCurrentNotification) withObject:nil afterDelay:kSecondsVisibleDelay];
}

-(void)hideCurrentNotification
{
    [UIView beginAnimations:@"" context:nil];
    [UIView setAnimationDelegate:self];
    [UIView setAnimationDidStopSelector:@selector(hideNotificationAnimationComplete:finished:context:)];
    [UIView setAnimationDuration:0.5f];
    
    [notificationView setFrame:CGRectMake(notificationView.frame.origin.x, notificationView.frame.origin.y-notificationView.frame.size.height, notificationView.frame.size.width, notificationView.frame.size.height)];
    
    [UIView commitAnimations];
}

-(void)hideNotificationAnimationComplete:(NSString*)animationID finished:(NSNumber*)finished context:(void*)context
{
    UIView *currentMessage = [messageQueue objectAtIndex:0];
    [currentMessage removeFromSuperview];
    [messageQueue removeObject:currentMessage];

    showingNotification = NO;
    
    if([messageQueue count] > 0) {
        UIView *nextMessage = [messageQueue objectAtIndex:0];
        [self showNotificationView:nextMessage];
    }
}

@end
