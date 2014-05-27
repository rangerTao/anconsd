//
//  NdAppUpdateHelper.m
//  GameCenter91
//
//  Created by kensou on 12-11-14.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "NdAppUpdateHelper+HOOK.h"
#import "RIButtonItem.h"
#import "CustomAlertView.h"
#import "UIAlertView+Blocks.h"
#import <objc/runtime.h>
#import <objc/message.h>
#import <NdComPlatform/NdComPlatformError.h>

@implementation NdAppUpdateHelper(HOOK)
+ (void)hookForGameCenter
{
    //产品经理要求在软件更新回来的时候知道网络是否可用，但是SDK的更新接口又没有返回这个信息
    //所以HOOK SDK中的软件更新回调接口，检查原始的错误信息，判断是否网络不可用
    SEL origSEL = @selector(checkVersionUpdateDidFinish:notify:updateInfo:);
    SEL newSEL = @selector(gcCheckVersionUpdateDidFinish:notify:updateInfo:);
    
    Class c = [self class];
    Method origMethod = class_getInstanceMethod(c, origSEL);
    Method newMethod = class_getInstanceMethod(c, newSEL);
    
    if (class_addMethod(c, origSEL, method_getImplementation(newMethod),
                        method_getTypeEncoding(newMethod)))
    {
        class_replaceMethod(c, newSEL, method_getImplementation(origMethod),
                            method_getTypeEncoding(origMethod));
    }
    else
    {
        method_exchangeImplementations(origMethod, newMethod);
    }

}

- (void)showNetworkErrorAfterCheckAppVersion
{
    NSString *title = @"亲，没有网络，再好的游戏也出不来啊！请连接网络后再试一次，我们等着您回来。";
    RIButtonItem *cancelItem = [RIButtonItem itemWithLabel:@"确定"];
    //    cancelItem.action = ^{[self quitGameCenter:nil];};
    
    CustomAlertView *alert = [[CustomAlertView alloc] initWithTitle:title message:nil cancelButtonItem:cancelItem otherButtonItems:nil];    
    [alert show];
    [alert release];
    
}

- (void)gcCheckVersionUpdateDidFinish:(NSError *)error notify:(NSString*)notify updateInfo:(id)updateInfo
{
    if ([error code] ==  ND_COM_PLATFORM_ERROR_NETWORK_FAIL || [error code] == ND_COM_PLATFORM_ERROR_NETWORK_ERROR)
    {
        [self performSelector:@selector(showNetworkErrorAfterCheckAppVersion) withObject:nil afterDelay:0.1];
    }
    
    [self gcCheckVersionUpdateDidFinish:error notify:notify updateInfo:updateInfo];
}


@end
