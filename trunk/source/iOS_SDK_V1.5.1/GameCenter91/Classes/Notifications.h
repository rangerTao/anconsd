//
//  Notifications.h
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-29.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

extern NSString * const kGC91DownloadPercentChangeNotification;
extern NSString * const kGC91DownloadQueueChangeNotification;

extern NSString * const kGC91UpdatingPercentChangeNotification;
extern NSString * const kGC91UpdateQueueChangeNotification;
extern NSString * const kGC91SmartUpdateFailedNotification;
extern NSString * const kGC91InstallingNotification;
extern NSString * const kGC91InstallFinishedNotification;

extern NSString * const kGC91ClaimTaskNotification;         /*任务领取成功时的通知*/
extern NSString * const kGC91CompleteTaskNotification;      /*任务完成时的通知*/

extern NSString * const kGC91UACCompletedNotification;      /*用户行为收集下发积分后的通知*/

extern NSString * const kGC91GetCodeSuccessNotification;    /*活动详情点击抢包成功时通知*/

extern NSString * const kGC91ApplicationDidBecomeActive;    /*返回前台后发送的通知，以便刷新一些界面*/

extern NSString * const kGC91NeedRreshHomePage;             /*通知首页调用1接口然后进行刷新*/