//
//  StatusBarNotification.h
//
//  Created by Sun pinqun on 12-8-2.
//  Copyright net dragon 2012. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface StatusBarNotification : UIView {
    UIWindow *notificationView;
    NSMutableArray *messageQueue;
    BOOL showingNotification;
}
@property(nonatomic, strong) IBOutlet UILabel *label1;
@property(nonatomic, strong) IBOutlet UILabel *label2;

+(void)notificationWithMessage:(NSString *)message;
+(void)notificationWithView:(UIView *)view;
@end
