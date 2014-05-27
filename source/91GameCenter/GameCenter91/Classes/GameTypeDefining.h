//
//  TypeDefining.h
//  GameCenter91
//
//  Created by Li.Binbin on 10/11/13.
//
//

#ifndef GameCenter91_TypeDefining_h
#define GameCenter91_TypeDefining_h

typedef enum _GAME_TYPE {
	GAME_RANK,          //排行
    GAME_CATAGORY,      //分类
    
}GAME_TYPE;

typedef enum _GAME_DETAIL_TYPE {
    GAME_DETAIL_NEW = 0x01,           //最新
    GAME_DETAIL_HOT,           //最热
}GAME_DETAIL_TYPE;

#endif
