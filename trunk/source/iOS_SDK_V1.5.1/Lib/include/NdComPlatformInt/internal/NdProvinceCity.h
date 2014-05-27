//
//  provinceCity.h
//  NdComPlatform_SNS
//
//  Created by xujianye on 10-11-10.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface NdProvinceCity : NSObject {

	NSArray*	arrProvince;
}

+ (NdProvinceCity*)singleton;
+ (void)releaseSingleton;

- (NSString*)findName:(NSString*)provinceId  cityId:(NSString*)cityId;
- (void)findName_split:(NSString*)provinceId  cityId:(NSString*)cityId 
			   proName:(NSString**)proName cityName:(NSString**)cityName
			 outProRow:(int*)rowPro	outCityRow:(int*)rowCity;
- (void)findRowIndex:(NSString*)provinceId  cityId:(NSString*)cityId
		   outProRow:(int*)rowPro	outCityRow:(int*)rowCity;


- (int)provinceCount;
- (int)cityCount:(int)provinceRow;

- (void)provinceAtRow:(int)row  outName:(NSString**)proName  outId:(NSString**)proId;
- (void)cityAtRow:(int)proRow  cityRow:(int)cityRow  outName:(NSString**)cityName  outId:(NSString**)cityId;
- (void)getNameIdAtRow:(int)proRow  cityRow:(int)cityRow 
			  outProName:(NSString**)proName  outCityName:(NSString**)cityName
			  outProId:(NSString**)proId  outCityId:(NSString**)cityId;

@end
