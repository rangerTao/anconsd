//
//  NdCPDeviceInfo.h
//  NdComPlatformFoundation
//
//  Created by BeiQi56 on 13-10-21.
//  Copyright (c) 2013年 NdCP. All rights reserved.
//

#import <Foundation/Foundation.h>

//#define NDCP_SUPPORT_APPSTORE   0

@interface NdCPDeviceInfo : NSObject

//may return advertisingIdentifier in iOS7
+ (NSString *)uniqueDeviceID;

//may return @""
+ (NSString *)macAddress;

//valid in iOS6+ , else return nil
+ (NSString *)identifierForAdvertising;

@end
