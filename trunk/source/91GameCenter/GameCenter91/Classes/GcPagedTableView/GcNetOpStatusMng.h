//
//  GcNetOpStatusMng.h
//  NdComPlatform_SNS
//
//  Created by xujianye on 10-10-14.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//
#import <UIKit/UIKit.h>

#define		NET_STATUS_NONE			0	//未请求
#define		NET_STATUS_FINISH		-1	
#define		NET_STATUS_FAIL			-2	
#define		NET_STATUS_NOTEXISTED	-3	//没有记录网络请求的标记位

#define		LIST_PAGE_SIZE			5

@interface GcNetOpStatusMng : NSObject

@property (nonatomic, assign)   int		nNetOpCount;	//用来记录正在请求的网络链接数，cancelAllNetOp时会清空
@property (nonatomic, assign)	int		pageSize;       //每页多少行
@property (nonatomic, assign)	int		pageCount_increaseOnce;// 调用increase_maxPageLimited时，增加的页数

//根据pageSize计算出所需页数，如果totalItemCount为0，返回1
- (int)getPageCount:(int)totalItemCount;

- (int) totalCount;
- (void)updateTotalCount:(int)totalNew;
- (void)updateTotalCount:(int)totalNew reset:(BOOL)reset;

- (void)updatePageItem:(int)pageIdx  pageItems:(NSArray*)arrPage  netStatus:(int)netStatus;
- (id)  getNetResultWithRowIndex:(int)row;
- (int) getNetStatusWithRowIndex:(int)row;
- (BOOL)isNetResultEmptyWithRowIndex:(int)row;  //如果该行已经下载过，且内容为nil，则返回YES
- (BOOL)isFirstPageLoaded;	//第一页是否下载完成

- (int)	totalCountLoaded;	//所有页都下载完成，累积有效的行数
- (BOOL)isAllPageLoaded;    //如果所有页都下载完成，返回YES
- (BOOL)isEmptyData;        //所有页都下载完成后，如果都为空返回YES
- (BOOL)isTotalCountZero;   //return totalCount <= 0;

- (void)increase_maxPageLimited;//继续展开pageCount_increaseOnce页
- (int)limitedRowsCount;        //当前已经展开的虚行数
- (int)nextPageIndexToDownload; //如果是分页模式，下一个欲展开的页索引
- (BOOL)isPageMode;             //如果是分页模式，返回YES
- (BOOL)isAllPageExpanded;      //是否所有页都已经展开

- (void)reset;  //重置数据为一行未加载的数据
- (BOOL)deleteItemWithRowIndex:(int)row;    //删除一行数据


+ (int)getMaxNetOpCount;    //默认允许并行请求的网络链接数

@end

