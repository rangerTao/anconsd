//
//  NdAnalytics.h
//  NdAnalytics
//
//  Created by  hiyo on 11-8-29.
//  Copyright 2011 Nd. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "NdAnalyticsAPIResponse.h"

/**
 @brief 统计SDK
 */
@interface NdAnalytics : NSObject 

/**
 @brief 获取SDK版本
 @result SDK版本号
 */
+ (NSString*)version;

/**
 @brief 获取渠道信息
 @result 渠道信息
 */
+ (NSString *)getChannel;

/**
 @brief 设置用户标识符
 @param uid 用户标识符
 */
+ (void)setUid:(NSString*)uid;

/**
 @brief 初始化SDK，设置AppId和AppKey
 @param settings appId和appKey等配置信息
 */
+ (void)initialize:(NdAnalyticsSettings*)settings;

/**
 @brief 使用该函数，当应用切入后台，在某个时间内返回应用就将被其视作统一次启动。默认时间30s
 @param seconds 时间间隔
 */
+ (void)setContinuousSessionInterval:(int)seconds;

/**
 @brief 简单事件
 @param eventId 事件id
 */
+ (void)event:(int)eventId;

/**
 @brief 标签事件
 @param eventId 事件id
 @param label   事件标签
 */
+ (void)event:(int)eventId label:(NSString *)label;

/**
 @brief 完整事件
 @param eventId 事件id
 @param label   事件标签，
 @param params  事件额外属性描述，限定10组内，key和value的限制为20字符，数量和长度超过均截断
 @param extentData 事件拓展数据，不在报表体现，开发者自己分析
 */
+ (void)event:(int)eventId label:(NSString*)label params:(NSDictionary *)params extentData:(NSString*)extentData;

/**
 @brief 累计类型事件，不记录明细，只记录数量
 @param eventId 事件id
 */
+ (void)accmulateEvent:(int)eventId;

/**
 @brief 累计类型标签事件，不记录明细，只记录数量
 @param eventId 事件id
 @param label   事件标签
 */
+ (void)accmulateEvent:(int)eventId label:(NSString *)label;

@end

/**
 @brief 统计SDK，拓展接口
 */
@interface NdAnalytics(Extented)

/**
 @brief 启动接口，
 @note  表示一次应用启动,适用于后台常驻程序调用，统计活跃度
 */
+ (void)startup;

/**
 @brief 子模块事件
 @param moduleId 子模块ID
 @param eventId 事件ID
 */
+ (void)subModuleEvent:(int)moduleId eventId:(int)eventId;

/**
 @brief 子模块事件
 @param moduleId 子模块ID
 @param eventId 事件ID
 @param label   事件标签
 @param params  事件额外属性描述，限定10组内，key和value的限制为20字符，数量和长度超过均截断
 @param extentData  事件拓展数据，不在报表体现，开发者自己分析
 */
+ (void)subModuleEvent:(int)moduleId eventId:(int)eventId label:(NSString*)label params:(NSDictionary *)params extentData:(NSString*)extentData;

/**
 @brief 子模块累计事件
 @param moduleId 子模块ID
 @param eventId 事件ID
 */
+ (void)subModuleAccmulateEvent:(int)moduleId eventId:(int)eventId;

/**
 @brief 子模块累计事件
 @param moduleId 子模块ID
 @param eventId 事件ID
 @param label   事件标签
 */
+ (void)subModuleAccmulateEvent:(int)moduleId eventId:(int)eventId label:(NSString*)label;
@end


