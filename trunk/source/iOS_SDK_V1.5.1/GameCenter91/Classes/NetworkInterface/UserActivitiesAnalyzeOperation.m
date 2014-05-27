//
//  UserActivitiesAnalyzeOperation.m
//  GameCenter91
//
//  Created by Li.Binbin on 11/28/13.
//
//

#import "UserActivitiesAnalyzeOperation.h"
#import "NdCPDeviceInfo.h"
#import <Log/NDLogger.h>

@implementation UserActivitiesAnalyzeOperation

- (id)init {
    self = [super init];
    if (self) {
        self.actionNumber = 8;
        self.beWithSessionId = NO;
    }
    return self;
}

- (int)operation
{
    NSString *f_id = [NSString stringWithFormat:@"%d", self.f_id];
    NSString *statType = [NSString stringWithFormat:@"%d", self.statType];
    NSString *platform = @"1";
    
    NSString *udid = [NdCPDeviceInfo uniqueDeviceID];
    if (udid == nil) {
        udid = @"";
    }

    NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                          f_id,@"f_id",
                          statType,@"StatType",
                          platform,@"Platform",
                          udid,@"Udid" ,nil];
    NDLOG(@"[Analytics]interface 8 : %@", dict);
    return [self sendRequest:dict encrytType:ND_CP_HTTP_PACKAGE_ENCRYPTION_NONE];
}

@end
