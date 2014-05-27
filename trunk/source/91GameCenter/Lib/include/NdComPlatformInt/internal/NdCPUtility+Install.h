//
//  NdCPUtility+Install.h
//  NdComPlatformInt
//
//  Created by xujianye on 12-8-27.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>



BOOL isSandBox();
BOOL hasAppSyncInstalled();

int  doInstall(NSString *fullPath);
int  doInstall_Non_Block(NSString *fullPath);
void repairIcon();
