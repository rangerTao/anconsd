//
//  ReportCenter.h
//  GameCenter91
//
//  Created by  hiyo on 13-11-28.
//
//

#import <Foundation/Foundation.h>
#import "SynthesizeSingleton.h"
#import "GameCenterAnalytics.h"

@interface ReportCenter : NSObject
SYNTHESIZE_SINGLETON_FOR_CLASS_HEADER(ReportCenter)

//计算转化率使用
@property (nonatomic, retain) NSString *reportIdentifier;
@property (nonatomic, assign) int reportNum;

+ (void)report:(int)eventNum;                                                       //事件ID
+ (void)report:(int)eventNum label:(NSString *)label;                               //事件ID，标签
+ (void)report:(int)eventNum label:(NSString *)label downloadFromNum:(int)fromNum;  //需要做下载转化率的使用这个

@end
