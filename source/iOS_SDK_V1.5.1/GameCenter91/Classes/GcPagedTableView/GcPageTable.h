//
//  GcPageTable.h
//  GameCenter91
//
//  Created by xujianye on 11-2-18.
//  Copyright 2011 NetDragon WebSoft Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GcEGORefreshTableHeaderView.h"


@protocol GcPageTableDelegate;
@class GcNetOpStatusMng;
@interface GcPageTable : UITableView<UITableViewDelegate, UITableViewDataSource, GcEGORefreshTableHeaderDelegate> {
	GcNetOpStatusMng*		netOpResult;
	NSString*				strMoreTip;
	NSString*				strNoDataTip;
	UIImage*				imageNoDataTip;
    UIImage*				defaultNoDataImage;
	id<GcPageTableDelegate> pageTableDelegate;
	UITableViewCell*		customCellCache;
    UITableViewCell*		titleCellCache;
    NSInteger               titleRowHeight;
	BOOL					bReverseOrder;
	NSUInteger				sectionCount;
	NSUInteger				sectionIdxOfPageList;
	UIColor*				clrOdd;
	UIColor*				clrEven;
	NSIndexPath*			currentIdxPath;
	BOOL					bIsLoadingMoreRecords;
	UIActivityIndicatorView* actIndicator;
	BOOL					bRespondsToSel_willDisplayCell;
	GcEGORefreshTableHeaderView *myRefreshHeaderView;
}

@property (nonatomic, retain) NSString*					strMoreTip;
@property (nonatomic, retain) NSString*					strNoDataTip;
@property (nonatomic, retain) UIImage*					imageNoDataTip;
@property (nonatomic, assign) id<GcPageTableDelegate>	pageTableDelegate;
@property (nonatomic, retain) UITableViewCell*			customCellCache;
@property (nonatomic, retain) UITableViewCell*			titleCellCache;
@property (nonatomic, assign) NSInteger                 titleRowHeight;
@property (nonatomic, assign) BOOL						bReverseOrder;
@property (nonatomic, assign) NSUInteger				sectionCount;
@property (nonatomic, assign) NSUInteger				sectionIdxOfPageList;
@property (nonatomic, retain) UIColor*					clrOdd;
@property (nonatomic, retain) UIColor*					clrEven;
@property (nonatomic, retain) GcNetOpStatusMng*	netOpResult;

- (void)setDefaultParityCellBkColor;
- (void)setNdPageTableTransparent;
- (void)setPageSize:(NSInteger)pageSize pageCountOnce:(NSUInteger)pageCount;

- (void)didDownloadPage:(NSInteger)pageIdx totalCount:(NSInteger)total dataArray:(NSArray*)arr success:(BOOL)bSucess;
- (void)didDeleteRowsAtIndexPath:(NSIndexPath*)indexPath;
- (void)clearDataAndReload;
- (void)setEmptyData;
- (NSIndexPath*)currentIndexPath;
- (id)dataForRowIndex:(NSInteger)row;

- (void)enableEgoRefreshTableHeaderView;
- (void)freshEmptyTableFooterViewWithColor:(UIColor*)clr image:(UIImage *)image text:(NSString*)text;
- (void)freshEmptyTableWithColor:(UIColor*)clr image:(UIImage *)image text:(NSString*)text;
- (int) totalCount;
- (BOOL)isFirstPageLoaded;

@end


@protocol GcPageTableDelegate<NSObject>

@required
- (void)	GcPageTable:(GcPageTable*)table customCell:(UITableViewCell*)cell  	customData:(id)data;
- (CGFloat)	GcPageTable:(GcPageTable*)table heightForCustomCell:(UITableViewCell*)cellCache  customData:(id)data;
- (int)		GcPageTable:(GcPageTable*)table downloadPageIndex:(NSInteger)pageIdx  pageSize:(NSInteger)pageSize;
- (UITableViewCell*)GcPageTable:(GcPageTable*)table cellCopyFromCacheCell:(UITableViewCell*)cellCache;

@optional
- (NSUInteger)	GcPageTable:(GcPageTable*)table numberOfRowsForEmptyPage:(BOOL)bTotalCountZero;
- (CGFloat)		GcPageTable:(GcPageTable*)table rowHeightForEmptyPage:(BOOL)bTotalCountZero indexPath:(NSIndexPath *)indexPath;
- (UITableViewCell*)  GcPageTable:(GcPageTable*)table cellForEmptyPage:(BOOL)bTotalCountZero  indexPath:(NSIndexPath *)indexPath;
- (void)		GcPageTable:(GcPageTable*)table didSelectRowWithData:(id)data;
- (void)		GcPageTable:(GcPageTable*)table willDisplayCell:(UITableViewCell*)cell forRowAtIndexPath:(NSIndexPath *)indexPath;

- (CGFloat) GcPageTable:(GcPageTable *)table heightForHeaderInSection:(NSInteger)section;
- (UIView *)GcPageTable:(GcPageTable *)table viewForHeaderInSection:(NSInteger)section;

//@required, if sectionCount > 1
- (NSInteger)	GcPageTable:(GcPageTable *)table numberOfRowsInSection:(NSInteger)section;
- (UITableViewCell *) GcPageTable:(GcPageTable *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath;
- (CGFloat)		GcPageTable:(GcPageTable *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath;
- (void)		GcPageTable:(GcPageTable *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath;

//当表中需要多种类型的cell时,根据数据选择cell
- (UITableViewCell*)GcPageTable:(GcPageTable*)table cellCopyByCustomData:(id)data;
- (NSString*)GcPageTable:(GcPageTable*)table cellIdentifierBycustomData:(id)data;

@end
