//
//  GcNetOpStatusMng.m
//  NdComPlatform_SNS
//
//  Created by xujianye on 10-10-14.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//

#import "GcNetOpStatusMng.h"
#import <NetEngine/NDNetHttpTransfer.h>
#import <log/NDLogger.h>
#import "RequestorAssistant.h"

@interface GcNetOpStatusMng()

@property (nonatomic, assign)   int*    pIntPageStatus;
@property (nonatomic, assign)   int     countOfPageStatus;
@property (nonatomic, retain)   NSMutableArray*		arrNetResult;
@property (nonatomic, assign)   int		totalCount;
@property (nonatomic, assign)   int		nItemsEmpty;    //已经下载的数据中空行数
@property (nonatomic, assign)   int		nMaxPageLimited;    //分页模式下，已经展开的页数

@end


@implementation GcNetOpStatusMng

- (id) init
{   
	if (self = [super init]) {
		_pageSize = LIST_PAGE_SIZE;
	}
	return self;
}

- (void)dealloc {
	[self cancelAllNetOp];
	[self freeNetStatusArr];
	[_arrNetResult release];
	[super dealloc];
}

#pragma mark -
- (int)getPageCount:(int)totalItemCount
{
	int nPageCount = (totalItemCount > 0) ? ((totalItemCount + _pageSize - 1) / _pageSize) : 1;
	return nPageCount;
}

- (int)getCapacityOfItems:(int)pageCount_ceil
{
	int nRet = _totalCount;
	if (pageCount_ceil > 0) {
		nRet = MIN(pageCount_ceil * 2* _pageSize, _totalCount);
	}
	return MAX(nRet, _pageSize);
}

#pragma mark memory
- (void)freeNetStatusArr
{
	if (_pIntPageStatus) {
		free(_pIntPageStatus);
		_pIntPageStatus = NULL;
		_countOfPageStatus = 0;
	}
}

- (int*)mallocPageNetStatusArrWithCount:(int)count
{
	if (count > 0) {
		int nBytes = count * sizeof(int);
		int* pInt = (int*)malloc(nBytes);
		if (pInt) {
			memset(pInt, 0, nBytes);
            return pInt;
		}
	}
	return NULL;
}

- (void)updateCapacityOfNetResultArrWithRows:(int)rows isRemalloc:(BOOL)bRemall
{
    rows = MAX(rows, 0);
	if (nil == _arrNetResult) {
		_arrNetResult = [[NSMutableArray alloc] initWithCapacity:rows*2];
	}
	if (bRemall) {
		[_arrNetResult removeAllObjects];
		_nItemsEmpty = 0;
	}
   
	if ([_arrNetResult count] > rows) {
        NSRange rg = NSMakeRange(rows, [_arrNetResult count] - rows);
        for (NSUInteger i = rg.location; i < [_arrNetResult count]; ++i) {
            id obj = [_arrNetResult objectAtIndex:i];
            if ([NSNull null] == obj) {
                --_nItemsEmpty;
            }
        }
        [_arrNetResult removeObjectsInRange:rg];
	}
	else {
		for (int i = [_arrNetResult count]; i < rows; ++i) {
			[_arrNetResult addObject:[NSNull null]];
		}
 	}
}

- (void)updateCapacityOfNetStatusArrWithRows:(int)rows isRemalloc:(BOOL)bRemall
{
	int nPageCount = [self getPageCount:rows];  //默认pagecount至少为1
	if (nPageCount > _countOfPageStatus) {
		int capacity = nPageCount * 2;
		int* pIntNew = [self mallocPageNetStatusArrWithCount:capacity];
		if (pIntNew) {
			if (!bRemall && _pIntPageStatus) {
				memcpy(pIntNew, _pIntPageStatus, _countOfPageStatus*sizeof(int));
			}
			[self freeNetStatusArr];
			_pIntPageStatus = pIntNew;
			_countOfPageStatus = capacity;
		}
	}
	else if(_pIntPageStatus){
		if (bRemall) {
			[self cancelAllNetOp];
			memset(_pIntPageStatus, 0, _countOfPageStatus * sizeof(int));
		}
		else {
            nPageCount = ([self totalCount] > 0 ? nPageCount : 0);
			int eraseSize = (_countOfPageStatus-nPageCount);
			if (eraseSize > 0) {
				int* pIntErase = _pIntPageStatus + nPageCount;
				[self cancelNetOp:pIntErase arrSize:eraseSize];
				memset(pIntErase, 0, eraseSize* sizeof(int));
			}
		}
	}
}

#pragma mark - netop
- (void)cancelNetOp:(int*)pNetStatus  arrSize:(int)size
{
	if (pNetStatus && size > 0) {
		for (int i = 0; i < size; ++i){
			if (pNetStatus[i] > 0) {
				//[[NDNetHttpTransfer sharedTransfer] cancelConnection:pNetStatus[i]];
                [[RequestorAssistant sharedInstance] performSelector:@selector(cancelOperation:) withObject:[NSNumber numberWithInt:pNetStatus[i]] afterDelay:0.1];
				pNetStatus[i] = NET_STATUS_NONE;
				--_nNetOpCount;
			}
		}
	}
}

- (void)cancelAllNetOp
{
	if (_pIntPageStatus) {
		int nPageCount = MIN(_countOfPageStatus, [self getPageCount:_totalCount]);
		[self cancelNetOp:_pIntPageStatus arrSize:nPageCount];
	}
	self.nNetOpCount = 0;
}

- (void)setMaxPageLimited:(int)maxPageCount
{
	_nMaxPageLimited = maxPageCount;
	int nItems = (maxPageCount > _countOfPageStatus) ? [self getCapacityOfItems:maxPageCount] : (maxPageCount * _pageSize);
	nItems = MAX(nItems, _totalCount);
	[self updateCapacityOfNetStatusArrWithRows:nItems isRemalloc:NO];
	[self updateCapacityOfNetResultArrWithRows:nItems isRemalloc:NO];
}

- (void)updateTotalCount:(int)nTotalNew
{
    [self updateTotalCount:nTotalNew reset:NO];
}

- (void)updateTotalCount:(int)nTotalNew reset:(BOOL)reset
{
    nTotalNew = MAX(0, nTotalNew);
	if (nTotalNew != _totalCount) {
        _totalCount = nTotalNew;
        int nItems = [self getCapacityOfItems:_nMaxPageLimited];
        [self updateCapacityOfNetStatusArrWithRows:nItems isRemalloc:reset];
        [self updateCapacityOfNetResultArrWithRows:nItems isRemalloc:reset];
	}
}

- (void)updatePageItem:(int)pageIdx  pageItems:(NSArray*)arrPage  netStatus:(int)netStatus
{
	if (pageIdx >= 0 && pageIdx < _countOfPageStatus) {
		
		int idxBegin = pageIdx * _pageSize;
		int idxEnd = MIN((idxBegin + _pageSize), _totalCount) ;
		if (idxBegin >= idxEnd) {
            return;
        }
        
		if (_pIntPageStatus[pageIdx] < 0) {
			for (int i = idxBegin; i < idxEnd; ++i) {
				id objTmp = [_arrNetResult objectAtIndex:i];
				if ([NSNull null] == objTmp) {
					--_nItemsEmpty;
				}
				else {
					[_arrNetResult replaceObjectAtIndex:i withObject:[NSNull null]];
				}
			}
		}
		
		_pIntPageStatus[pageIdx] = netStatus;
		if (netStatus < 0) {
			if ([arrPage count] <= (idxEnd - idxBegin)) {
				_nItemsEmpty += (idxEnd - idxBegin) - [arrPage count];
				NDLOG(@"NdNetOpStatusMng no data total : %d", _nItemsEmpty);
			}
			for (id objTmp in arrPage) {
				[_arrNetResult replaceObjectAtIndex:idxBegin withObject:objTmp];
				++idxBegin;
				if (idxBegin >= idxEnd) {
					break;
				}
			}
		}
	}
}

- (id)  getNetResultWithRowIndex:(int)row
{
	if (row >= 0 && _arrNetResult && (row < [_arrNetResult count])) {
		id obj = [_arrNetResult objectAtIndex:row];
		return ([NSNull null] != obj) ? obj : nil;
	}
	return nil;
}

- (int) getNetStatusWithRowIndex:(int)row
{
	if (row >= 0 && (row < _totalCount) && _pIntPageStatus) {
		int nPageIdx = [self getPageCount:(row + 1)] - 1;
		if (nPageIdx < _countOfPageStatus)
			return _pIntPageStatus[nPageIdx];
	}
	return NET_STATUS_NOTEXISTED;
}

- (BOOL)isNetResultEmptyWithRowIndex:(int)row
{
	return (([self getNetStatusWithRowIndex:row] < 0) && ([self getNetResultWithRowIndex:row] == nil));
}

- (int)totalCount
{
	return _totalCount;
}

- (int)	totalCountLoaded
{
	return _totalCount - _nItemsEmpty;
}

- (int) limitedRowsCount
{
	if (_nMaxPageLimited > 0) {
		return MIN(_nMaxPageLimited * _pageSize, _totalCount);
	}
	return _totalCount;
}

- (BOOL)isAllPageLoaded
{
	BOOL bRet = YES;
	if ((_totalCount > 0) && _pIntPageStatus) {
		int nPageCount = [self getPageCount:_totalCount];
		if (nPageCount > _countOfPageStatus) {
			return NO;
		}
		else {
			for (int i = _countOfPageStatus - 1; i >= 0; --i) {
				if (_pIntPageStatus[i] >= 0) {
					return NO;
				}
			}
		}
	}
	return bRet;
}

- (BOOL)isEmptyData
{
	return (_totalCount <= 0) || (([self isAllPageLoaded]) && (_totalCount <= _nItemsEmpty));
}

- (BOOL)isAllPageExpanded
{
	return [self limitedRowsCount] >= _totalCount;
}

- (BOOL)isPageMode
{
	return _nMaxPageLimited > 0;
}

- (BOOL)isTotalCountZero
{
	return (_totalCount <= 0);
}

- (BOOL)isFirstPageLoaded
{
	if (_countOfPageStatus > 0 && _pIntPageStatus) {
		return _pIntPageStatus[0] < 0;
	}
	return YES;
}

- (void)setPageCount_increaseOnce:(int)pageOnce
{
	_pageCount_increaseOnce = pageOnce;
	if (_nMaxPageLimited <= 1) {
		[self setMaxPageLimited:pageOnce];
	}
}

- (void)reset
{
	[self updateTotalCount:0];
	[self updateTotalCount:1];
	[self setMaxPageLimited:_pageCount_increaseOnce];
}

- (void)increase_maxPageLimited
{
	[self setMaxPageLimited:_nMaxPageLimited + _pageCount_increaseOnce];
}

- (BOOL)deleteItemWithRowIndex:(int)row
{
	int nMaxRow = [self limitedRowsCount];
	int nPageCount = [self getPageCount:nMaxRow];
	int nPageIdx = [self getPageCount:(row + 1)] - 1;
	if (row >= 0 && row < nMaxRow) {
		int nNetStatus = _pIntPageStatus[nPageIdx];
		id objTmp = [_arrNetResult objectAtIndex:row];
		if (nNetStatus < 0 && [NSNull null] == objTmp) {
			--_nItemsEmpty;
		}
		[_arrNetResult removeObjectAtIndex:row];
		[_arrNetResult addObject:[NSNull null]];
		--_totalCount;
		for (int nIdxTmp = nPageIdx + 1; nIdxTmp < nPageCount; ++nIdxTmp) {
			if (NET_STATUS_NONE == _pIntPageStatus[nIdxTmp]) {
				for (int i = nIdxTmp + 1; i < nPageCount; ++i) {
					[self updatePageItem:i pageItems:nil netStatus:NET_STATUS_NONE];
				}
				break;
			}
		}
		return YES;
	}
	return NO;
}

- (int)nextPageIndexToDownload
{
	return MAX(0, _nMaxPageLimited + _pageCount_increaseOnce - 1);
}

+ (int)getMaxNetOpCount
{
	return 2;
}

@end

