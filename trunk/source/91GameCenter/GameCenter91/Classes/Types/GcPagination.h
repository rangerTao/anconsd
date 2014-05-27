//
//  GcPagination.h
//  GameCenter91
//
//  Created by  hiyo on 13-11-8.
//
//

#import <Foundation/Foundation.h>

/**
 @brief 分页信息
 */
@interface GcPagination : NSObject
{
	int pageIndex;
	int pageSize;
}

@property (nonatomic, assign) int pageIndex;		/**< 要获取的第几页记录，从1开始*/
@property (nonatomic, assign) int pageSize;			/**< 每页记录的个数（5的倍数），最大为50*/

@end