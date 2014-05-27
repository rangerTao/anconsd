//
//  Macros.h
//  GameCenter91
//
//  Created by Sun pinqun on 13-2-20.
//  Copyright (c) 2013年 net dragon. All rights reserved.
//

#ifndef GameCenter91_Macros_h
#define GameCenter91_Macros_h


#define APP_ID          104112
#if 1
//外网
#define APP_KEY         @"7dc1be3f2e6230d2439cbe83158fb231c384df516461add5"  
#define BASE_URL        @"http://newgamecenter.sj.91.com/servicev2/action/"
#define PUSH_ACTIVIT_URL @"http://newgamecenter.sj.91.com/servicev2/Activity/detail/"
#else
//内网
#define APP_KEY         @"97fc9825bd5aecfa2b6433adffb7d3675138a5c21d1bf753"
#define BASE_URL        @"http://192.168.189.31:1897/service/action/"
#define PUSH_ACTIVIT_URL @"http://192.168.189.31:1897/service/Activity/detail/"
#endif


//#define AppId_Analytics     109999
//#if 1
//#define AppKey_Analytics    @"7dc1be3f2e6230d2439cbe83158fb231c384df516461add5"     //外网
//#else
//#define AppKey_Analytics    @"97fc9825bd5aecfa2b6433adffb7d3675138a5c21d1bf753"     //内网
//#endif

#define HEXCOLOR(c) [UIColor colorWithRed:((c>>24)&0xFF)/255.0 \
 green:((c>>16)&0xFF)/255.0\
 blue:((c>>8)&0xFF)/255.0\
 alpha:((c)&0xFF)/255.0]

#endif
