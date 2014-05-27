//
//  ReportCenter.m
//  GameCenter91
//
//  Created by  hiyo on 13-11-28.
//
//

#import "ReportCenter.h"
#import <Log/NDLogger.h>

@implementation ReportCenter
SYNTHESIZE_SINGLETON_FOR_CLASS(ReportCenter)
@synthesize reportIdentifier, reportNum;

+ (void)report:(int)eventNum
{
    [ReportCenter report:eventNum label:nil];
}

+ (void)report:(int)eventNum label:(NSString *)label
{
    [ReportCenter report:eventNum label:label downloadFromNum:0];
}

+ (void)report:(int)eventNum label:(NSString *)label downloadFromNum:(int)fromNum
{
    NDLOG(@"[Analytics] : eventId = %d, label = %@", eventNum, label);
    if ([label length] <= 0) {
        [NdAnalytics event:eventNum];
    }
    else {
        [NdAnalytics event:eventNum label:label];
    }
    //下载来源统计
    if (fromNum > 0 && [label length] > 0) {
        [self sharedInstance].reportIdentifier = label;
        [self sharedInstance].reportNum = fromNum;
    }
}

@end
