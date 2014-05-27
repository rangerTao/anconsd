//
//  MIUtility.h
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-28.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface MIUtility : NSObject {
    
}
+ (NSArray *)allInstalledAppInfo;
+ (NSDictionary *)dicForAllIntalledAppInfo;
+ (BOOL)install:(NSString *)filePath error:(NSError **)error;
+ (BOOL)uninstall:(NSString *)identifier error:(NSError **)error;
@end
