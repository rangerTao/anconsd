/*
 *  NdPhoneInfo.h
 *  NdComPlatform
 *
 *  Created by Sie Kensou on 10-8-12.
 *  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
 *
 */
#import <UIKit/UIKit.h>

//get the type of device	
NSString *getCurrentDeviceType();

//currently iphone only have 3 type,iphone 320 * 480,iphone4 640 * 960,ipad 1024 * 768
CGSize getCurrentScreenResolution();

//get chaos imsi 
NSString *getChaosIMSI();

//get imsi
NSString *getIMSI();

#if TARGET_IPHONE_SIMULATOR
void setIMSI(NSString *IMSI);
#endif

//get phone device id
NSString *getDeviceID();

//get chaos device id
NSString *getChaosDeviceID();

//get the main screen bounds
CGRect	getScreenBounds();