//
//  WifiCheckManager.m
//  GameCenter91
//
//  Created by  hiyo on 13-12-27.
//
//

#import "WifiCheckManager.h"
#import "CommUtility.h"

#define INIT_CHECK_INTERVAL 2

@interface WifiCheckManager()
@property (nonatomic, assign) int checkInterval;    //unit seconds
@property (nonatomic, retain) NSMutableArray *objArr;   //检查发起对象
@property (nonatomic, retain) NSMutableArray *selArr;   //检查网络成功后处理方法
@property (nonatomic, retain) NSTimer *timer;
@end

@implementation WifiCheckManager
SYNTHESIZE_SINGLETON_FOR_CLASS(WifiCheckManager)

- (void) dealloc
{
    self.objArr = nil;
    self.selArr = nil;
    [self stopTimer];
    
    [super dealloc];
}

- (void)startCheckFor:(id)obj wifiOkSelStr:(NSString *)selStr
{
    if (obj == nil || selStr == nil) {
        return;
    }
    self.checkInterval = INIT_CHECK_INTERVAL;
    [self stopTimer];
    [self startTimer];
    if (self.objArr == nil) {
        self.objArr = [NSMutableArray array];
    }
    if (self.selArr == nil) {
        self.selArr = [NSMutableArray array];
    }
    if ([self.objArr indexOfObject:obj] == NSNotFound) {
        [self.objArr addObject:obj];
        [self.selArr addObject:selStr];
    }
}

- (void)startTimer
{
    if (self.timer == nil) {
        self.timer = [NSTimer scheduledTimerWithTimeInterval:self.checkInterval
                                                      target:self
                                                    selector:@selector(beginCheck)
                                                    userInfo:nil
                                                     repeats:NO];
    }
}

- (void)stopTimer
{
    if (self.timer != nil) {
        [self.timer invalidate];
        self.timer = nil;
    }
}

- (void)beginCheck
{
    NSLog(@"WifiCheck : beginCheck");
    BOOL isWifi = [CommUtility isWifiNetWork];
    if (isWifi) {
        NSLog(@"WifiCheck : Find Wifi");
        [self doAfterWifiOk];
    }
    else {
        NSLog(@"WifiCheck : can't find wifi");
        int interval = self.checkInterval * 2;
        self.checkInterval = (interval <= 60 ? interval : 60);
        [self stopTimer];
        [self startTimer];
    }
}

- (void)doAfterWifiOk
{
    if ([self.objArr count] != [self.selArr count]) {
        return;
    }
    //有wifi后处理
    for (int i = 0; i < [self.objArr count]; i++) {
        id obj = [self.objArr objectAtIndex:i];
        SEL sel = NSSelectorFromString([self.selArr objectAtIndex:i]);
        if ([obj respondsToSelector:sel]) {
            [obj performSelector:sel];
        }
    }
    //移除
    [self.objArr removeAllObjects];
    [self.selArr removeAllObjects];
    self.checkInterval = INIT_CHECK_INTERVAL;
}

@end
