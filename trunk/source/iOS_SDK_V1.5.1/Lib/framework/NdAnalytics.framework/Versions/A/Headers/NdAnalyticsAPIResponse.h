//
//  NdAnalyticsAPIResponse.h
//  NdAnalytics
//
//  Created by  hiyo on 11-8-30.
//  Copyright 2011 Nd. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NdAnalyticsSettings : NSObject {
	NSString *appId;
    NSString *appKey;
}

@property (nonatomic,retain) NSString *appId;
@property (nonatomic,retain) NSString *appKey;

@end