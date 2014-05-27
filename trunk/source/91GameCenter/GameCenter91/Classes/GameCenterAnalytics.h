//
//  GameCenterAnalytics.h
//  GameCenter91
//
//  Created by hiyo on 12-12-24.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#ifndef GameCenter91_GameCenterAnalytics_h
#define GameCenter91_GameCenterAnalytics_h


#import <NdAnalytics/NdAnalytics.h>

#define ANALYTICS_EVENT_15001   15001   //点击首页标签页
#define ANALYTICS_EVENT_15002   15002   //点击游戏标签页
#define ANALYTICS_EVENT_15003   15003   //点击活动标签页
#define ANALYTICS_EVENT_15004   15004   //点击管理标签页
#define ANALYTICS_EVENT_15005   15005   //下载游戏
#define ANALYTICS_EVENT_15006   15006   //升级游戏
#define ANALYTICS_EVENT_15007   15007   //下载成功
#define ANALYTICS_EVENT_15008   15008   //下载失败
#define ANALYTICS_EVENT_15009   15009   //安装成功
#define ANALYTICS_EVENT_15010   15010   //安装失败
#define ANALYTICS_EVENT_15011   15011   //打开具体游戏（开始玩）
#define ANALYTICS_EVENT_15012   15012   //接收到SDK的跳转
#define ANALYTICS_EVENT_15013   15013   //我的热点-首条内容（类型为活动）
#define ANALYTICS_EVENT_15014   15014   //我的热点-首条内容（类型为公告）
#define ANALYTICS_EVENT_15015   15015   //我的热点-首条内容（类型为礼包）
#define ANALYTICS_EVENT_15016   15016   //我的热点-首条内容（类型为开服）
#define ANALYTICS_EVENT_15017   15017   //我的热点-首条内容（类型为详情）
#define ANALYTICS_EVENT_15018   15018   //我的热点-首条内容（类型为其他）
#define ANALYTICS_EVENT_15019   15019   //我的热点-其余内容（类型为活动）
#define ANALYTICS_EVENT_15020   15020   //我的热点-其余内容（类型为公告）
#define ANALYTICS_EVENT_15021   15021   //我的热点-其余内容（类型为礼包）
#define ANALYTICS_EVENT_15022   15022   //我的热点-其余内容（类型为开服）
#define ANALYTICS_EVENT_15023   15023   //我的热点-其余内容（类型为详情）
#define ANALYTICS_EVENT_15024   15024   //我的热点-其余内容（类型为其他）
#define ANALYTICS_EVENT_15025   15025   //我的热点-更多
#define ANALYTICS_EVENT_15026   15026   //关注的游戏->开始玩
#define ANALYTICS_EVENT_15027   15027   //关注的游戏->专区
#define ANALYTICS_EVENT_15028   15028   //关注的游戏->攻略
#define ANALYTICS_EVENT_15029   15029   //关注的游戏->论坛
#define ANALYTICS_EVENT_15030   15030   //关注的游戏->礼包活动公告
#define ANALYTICS_EVENT_15031   15031   //关注的游戏->去专区查看更多
#define ANALYTICS_EVENT_15032   15032   //编辑我关注的游戏列表
#define ANALYTICS_EVENT_15033   15033   //我的游戏->查看推荐游戏
#define ANALYTICS_EVENT_15034   15034   //编辑推荐->今日一荐
#define ANALYTICS_EVENT_15035   15035   //编辑推荐->其他推荐
#define ANALYTICS_EVENT_15036   15036   //靓点推荐->1号位
#define ANALYTICS_EVENT_15037   15037   //靓点推荐->2号位
#define ANALYTICS_EVENT_15038   15038   //靓点推荐->3号位
#define ANALYTICS_EVENT_15039   15039   //靓点推荐->4号位
#define ANALYTICS_EVENT_15040   15040   //靓点推荐->5号位
#define ANALYTICS_EVENT_15041   15041   //靓点推荐->6号位
#define ANALYTICS_EVENT_15042   15042   //靓点推荐->7号位
#define ANALYTICS_EVENT_15043   15043   //靓点推荐->8号位
#define ANALYTICS_EVENT_15044   15044   //靓点推荐->9号位
#define ANALYTICS_EVENT_15045   15045   //靓点推荐->10号位
#define ANALYTICS_EVENT_15046   15046   //靓点推荐->11号位
#define ANALYTICS_EVENT_15047   15047   //靓点推荐->12号位
#define ANALYTICS_EVENT_15048   15048   //靓点推荐->13号位
#define ANALYTICS_EVENT_15049   15049   //靓点推荐->14号位
#define ANALYTICS_EVENT_15050   15050   //查看具体分类
#define ANALYTICS_EVENT_15051   15051   //在具体分类下查看游戏详情
#define ANALYTICS_EVENT_15052   15052   //在排行列表中查看游戏详情
#define ANALYTICS_EVENT_15053   15053   //搜索游戏
#define ANALYTICS_EVENT_15054   15054   //专题页面查看游戏详情
#define ANALYTICS_EVENT_15055   15055   //游戏礼包-具体礼包
#define ANALYTICS_EVENT_15056   15056   //游戏礼包-广告位
#define ANALYTICS_EVENT_15057   15057   //活动公告-具体活动
#define ANALYTICS_EVENT_15058   15058   //活动公告-广告位
#define ANALYTICS_EVENT_15059   15059   //新服预告-具体预告
#define ANALYTICS_EVENT_15060   15060   //领取礼包
#define ANALYTICS_EVENT_15061   15061   //点击开服
#define ANALYTICS_EVENT_15062   15062   //取消开服
#define ANALYTICS_EVENT_15063   15063   //游戏详情中点击指引
#define ANALYTICS_EVENT_15064   15064   //游戏详情中点击热点
#define ANALYTICS_EVENT_15065   15065   //游戏详情中点击热点的更多
#define ANALYTICS_EVENT_15066   15066   //游戏详情中点击攻略
#define ANALYTICS_EVENT_15067   15067   //游戏详情中点击攻略的更多
#define ANALYTICS_EVENT_15068   15068   //游戏详情中点击论坛
#define ANALYTICS_EVENT_15069   15069   //游戏详情中点击论坛的更多

//以下为转化率设定的埋点（关于转化率，请参照说明）
#define ANALYTICS_EVENT_15070   15070   //下载转化成功数（每日推荐->今日一荐）
#define ANALYTICS_EVENT_15071   15071   //下载转化成功数（每日推荐->其他推荐）
#define ANALYTICS_EVENT_15072   15072   //下载转化成功数（我的热点-首条内容（类型为活动））
#define ANALYTICS_EVENT_15073   15073   //下载转化成功数（我的热点-首条内容（类型为公告））
#define ANALYTICS_EVENT_15074   15074   //下载转化成功数（我的热点-首条内容（类型为礼包）)
#define ANALYTICS_EVENT_15075   15075   //下载转化成功数(我的热点-首条内容（类型为开服）)
#define ANALYTICS_EVENT_15076   15076   //下载转化成功数(我的热点-首条内容（类型为详情）)
#define ANALYTICS_EVENT_15077   15077   //下载转化成功数（我的热点-其余内容（类型为活动））
#define ANALYTICS_EVENT_15078   15078   //下载转化成功数（我的热点-其余内容（类型为公告））
#define ANALYTICS_EVENT_15079   15079   //下载转化成功数（我的热点-其余内容（类型为礼包））
#define ANALYTICS_EVENT_15080   15080   //下载转化成功数（我的热点-其余内容（类型为开服））
#define ANALYTICS_EVENT_15081   15081   //下载转化成功数（我的热点-其余内容（类型为详情））
#define ANALYTICS_EVENT_15082   15082   //下载转化成功数（我的游戏->查看推荐游戏）
#define ANALYTICS_EVENT_15083   15083   //下载转化成功数（靓点推荐->1号位）
#define ANALYTICS_EVENT_15084   15084   //下载转化成功数（靓点推荐->2号位）
#define ANALYTICS_EVENT_15085   15085   //下载转化成功数（靓点推荐->3号位）
#define ANALYTICS_EVENT_15086   15086   //下载转化成功数（靓点推荐->4号位）
#define ANALYTICS_EVENT_15087   15087   //下载转化成功数（靓点推荐->5号位）
#define ANALYTICS_EVENT_15088   15088   //下载转化成功数（靓点推荐->6号位)
#define ANALYTICS_EVENT_15089   15089   //下载转化成功数（靓点推荐->7号位)
#define ANALYTICS_EVENT_15090   15090   //下载转化成功数（靓点推荐->8号位)
#define ANALYTICS_EVENT_15091   15091   //下载转化成功数（靓点推荐->9号位)
#define ANALYTICS_EVENT_15092   15092   //下载转化成功数（靓点推荐->10号位)
#define ANALYTICS_EVENT_15093   15093   //下载转化成功数（靓点推荐->11号位)
#define ANALYTICS_EVENT_15094   15094   //下载转化成功数（靓点推荐->12号位)
#define ANALYTICS_EVENT_15095   15095   //下载转化成功数（靓点推荐->13号位)
#define ANALYTICS_EVENT_15096   15096   //下载转化成功数（靓点推荐->14号位)
#define ANALYTICS_EVENT_15097   15097   //下载转化成功数（游戏礼包-具体礼包)
#define ANALYTICS_EVENT_15098   15098   //下载转化成功数（游戏礼包-广告位)
#define ANALYTICS_EVENT_15099   15099   //下载转化成功数（活动公告-具体活动)
#define ANALYTICS_EVENT_15100   15100   //下载转化成功数（活动公告-广告位)
#define ANALYTICS_EVENT_15101   15101   //下载转化成功数（新服预告-具体预告)
#define ANALYTICS_EVENT_15102   15102   //下载转化成功数（SDK过来的转化）

#endif
