//
//  WifiCheckManager.h
//  GameCenter91
//
//  Created by  hiyo on 13-12-27.
//
//

#import <Foundation/Foundation.h>
#import "SynthesizeSingleton.h"

@interface WifiCheckManager : NSObject
SYNTHESIZE_SINGLETON_FOR_CLASS_HEADER(WifiCheckManager)

- (void)startCheckFor:(id)obj wifiOkSelStr:(NSString *)selStr;

@end
