//
//  GcPageTable.m
//  GameCenter91
//
//  Created by xujianye on 11-2-18.
//  Copyright 2011 NetDragon WebSoft Inc. All rights reserved.
//

#import "GcPageTable.h"
#import "GcNetOpStatusMng.h"

@interface GcPageTable()

@property (nonatomic, retain) NSIndexPath*		currentIdxPath;
@property (nonatomic, assign) BOOL				bIsLoadingMoreRecords;
@property (nonatomic, retain) UIActivityIndicatorView* actIndicator;
@property (nonatomic, retain) UIImage*			defaultNoDataImage;
@property (nonatomic, retain) GcEGORefreshTableHeaderView *myRefreshHeaderView;

- (BOOL)isRowMoreItem:(NSIndexPath*)indexPath;
- (NSIndexPath*)indexPathForMoreItem;

- (NSUInteger)rowRedirect:(NSIndexPath*)indexPath;
- (UITableViewCell*) prepareMoreCell:(UITableView *)tableView withTip:(NSString *)tip;
- (void)loadMoreRecordsIndicator:(UITableViewCell*)cell;
- (void)didLoadMoreRecordsIndicator;
- (void)loadMoreRecordsIfNeed;

@end

@implementation GcPageTable

@synthesize strMoreTip, strNoDataTip, imageNoDataTip, netOpResult;
@synthesize pageTableDelegate, customCellCache, titleCellCache, titleRowHeight;
@synthesize bReverseOrder, bIsLoadingMoreRecords;
@synthesize actIndicator;
@synthesize sectionCount, sectionIdxOfPageList;
@synthesize clrOdd, clrEven;
@synthesize currentIdxPath;
@synthesize defaultNoDataImage;
@synthesize myRefreshHeaderView;

- (id)initWithFrame:(CGRect)frame style:(UITableViewStyle)style             
{
    if ((self = [super initWithFrame:frame style:style])) {
		sectionCount = 1;
		self.strMoreTip = @"更多记录⋯⋯";
		self.strNoDataTip = @"现在暂时还没有内容哦。";
		self.defaultNoDataImage = [UIImage imageNamed:@"errorIcon.png"];
//        [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"errorIcon" ofType:@"png"]];
        self.netOpResult = [[GcNetOpStatusMng new] autorelease];
		[netOpResult updateTotalCount:1];
		
		self.backgroundColor = [UIColor clearColor];
//		self.separatorColor = [UIColor grayColor];
		self.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
		self.delegate = self;
		self.dataSource = self;		
    }
    return self;
}

- (void)dealloc {
	self.strNoDataTip = nil;
	self.imageNoDataTip = nil;
    self.defaultNoDataImage = nil;
	self.strMoreTip = nil;
	self.netOpResult = nil;
	self.pageTableDelegate = nil;
	self.customCellCache = nil;
    self.titleCellCache = nil;
	self.clrOdd = nil;
	self.clrEven = nil;
	self.currentIdxPath = nil;
	self.actIndicator = nil;
	self.myRefreshHeaderView = nil;
    [super dealloc];
}

- (void)setDefaultParityCellBkColor
{
	self.clrOdd  = [UIColor colorWithRed:185/255.0f green:184/255.0f blue:190/255.0f alpha:1.0f];
	self.clrEven = [UIColor colorWithRed:217/255.0f green:217/255.0f blue:225/255.0f alpha:1.0f];
}

- (void)setNdPageTableTransparent
{
	if ([self respondsToSelector:@selector(setBackgroundView:)]) {
		UIView* viewTmp = [[UIView new] autorelease];
		viewTmp.backgroundColor = [UIColor clearColor];
		[self setBackgroundView:viewTmp];
	}
	[self setBackgroundColor:[UIColor clearColor]];
}

- (BOOL)isRowMoreItem:(NSIndexPath*)indexPath
{
    if (self.titleCellCache == nil) {
        return ([netOpResult isPageMode] && ![netOpResult isAllPageExpanded]
                && ((bReverseOrder && indexPath.row == 0) || (!bReverseOrder && indexPath.row == [netOpResult limitedRowsCount])));
    }
    else {
        return ([netOpResult isPageMode] && ![netOpResult isAllPageExpanded]
                && ((bReverseOrder && indexPath.row == 1) || (!bReverseOrder && indexPath.row == [netOpResult limitedRowsCount]+1)));
    }
}

- (BOOL)isRowTitleItem:(NSIndexPath*)indexPath {
    return (self.titleCellCache != nil && indexPath.section == sectionIdxOfPageList && indexPath.row == 0 && ![netOpResult isEmptyData]);
}

- (NSIndexPath*)indexPathForMoreItem
{
	if ([netOpResult isPageMode] && ![netOpResult isAllPageExpanded]) {
		if (bReverseOrder) {
			return [NSIndexPath indexPathForRow:0 inSection:sectionIdxOfPageList];
		}
		else {
			return [NSIndexPath indexPathForRow:[netOpResult limitedRowsCount] inSection:sectionIdxOfPageList];
		}
	}
	return nil;
}

- (NSUInteger)rowRedirect:(NSIndexPath*)indexPath
{
	if (bReverseOrder) {
		NSUInteger count = [self tableView:self numberOfRowsInSection:indexPath.section];
		return count - 1- indexPath.row;
	}
	return (self.titleCellCache == nil) ? indexPath.row : indexPath.row - 1;
}

- (void)didDeleteRowsAtIndexPath:(NSIndexPath*)indexPath
{
	if ([self.netOpResult deleteItemWithRowIndex:indexPath.row]) {
		[self deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] 
					withRowAnimation:UITableViewRowAnimationBottom];
	}
}

- (void)loadMoreRecordsIndicator:(UITableViewCell*)cell
{
	self.bIsLoadingMoreRecords = YES;
	if (nil == actIndicator) {
		UIActivityIndicatorViewStyle theStyle = UIActivityIndicatorViewStyleGray ;
		if (self.backgroundColor == [UIColor clearColor]) {
			theStyle = UIActivityIndicatorViewStyleWhite;
		}
		actIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:theStyle];
		CGRect rc = CGRectMake(0, 0, 30, 30);
		rc.origin.x = (self.rowHeight - rc.size.width) * 0.5f;
		rc.origin.y = 15;
		actIndicator.frame = rc;
	}
	if (actIndicator.superview) {
		[actIndicator removeFromSuperview];
	}
	[cell.contentView addSubview:actIndicator];
	
	if (![actIndicator isAnimating]) {
		[actIndicator startAnimating];
	}
	
	int nNextPageIdx = [netOpResult nextPageIndexToDownload];
	int nNetStatus = [pageTableDelegate GcPageTable:self downloadPageIndex:nNextPageIdx pageSize:netOpResult.pageSize];
	if (nNetStatus >= 0) {
		netOpResult.nNetOpCount = netOpResult.nNetOpCount + 1;
	}
	else {
		[netOpResult increase_maxPageLimited];
		[netOpResult updatePageItem:nNextPageIdx pageItems:nil netStatus:NET_STATUS_FAIL];
		[self didLoadMoreRecordsIndicator];
	}	
}

- (void)didLoadMoreRecordsIndicator
{
	if (actIndicator) {
		if ([actIndicator  isAnimating]) {
			[actIndicator stopAnimating];
		}
		if (actIndicator.superview) {
			[actIndicator removeFromSuperview];
		}
		self.actIndicator = nil;
	}
	self.bIsLoadingMoreRecords = NO;
}

- (void)loadMoreRecordsIfNeed
{
	if (!bIsLoadingMoreRecords) {
		NSIndexPath* idxPath = [self indexPathForMoreItem];
		if (idxPath) {
			[self loadMoreRecordsIndicator:[self cellForRowAtIndexPath:idxPath]];
		}
	}
}

#pragma mark -
- (void)didDownloadPage:(NSInteger)pageIdx totalCount:(NSInteger)total dataArray:(NSArray*)arr success:(BOOL)bSucess
{
	self.currentIdxPath = nil;
	if (0 == pageIdx) {
		[myRefreshHeaderView egoRefreshScrollViewDataSourceDidFinishedLoading:self];
	}
	
	if (bIsLoadingMoreRecords) {
		[netOpResult increase_maxPageLimited];
		[self didLoadMoreRecordsIndicator];
	}

	if (netOpResult.nNetOpCount > 0) {
		netOpResult.nNetOpCount = netOpResult.nNetOpCount -1;
	}
	if (bSucess) {
		[netOpResult updateTotalCount:total];
		[netOpResult updatePageItem:pageIdx pageItems:arr netStatus:NET_STATUS_FINISH];
	}
	else {
		[netOpResult updatePageItem:pageIdx pageItems:nil netStatus:NET_STATUS_FAIL];
	}
	if (bReverseOrder) {
		CGPoint ptOffset = CGPointMake(0.0f, 0.0f);
		if ([netOpResult isPageMode] && ![netOpResult isAllPageExpanded]) {
			ptOffset.y = self.rowHeight;
		}
		self.contentOffset = ptOffset;
	}
	if ([netOpResult isEmptyData]) {
        UIImage *img = (self.imageNoDataTip != nil) ? self.imageNoDataTip : defaultNoDataImage;
		[self freshEmptyTableWithColor:[UIColor clearColor] image:img text:strNoDataTip];
	}

	[self reloadData];
}

- (void)setPageSize:(NSInteger)pageSize pageCountOnce:(NSUInteger)pageCount
{
	if (pageSize > 0)
		netOpResult.pageSize = pageSize;
	netOpResult.pageCount_increaseOnce = pageCount;
}

- (void)clearDataAndReload
{
	[netOpResult reset];
	[self reloadData];
}

- (void)setEmptyData
{
	[netOpResult updateTotalCount:0];
}

- (NSIndexPath*)currentIndexPath
{
	return self.currentIdxPath;
}

- (id)dataForRowIndex:(NSInteger)row
{
	NSIndexPath* indexPath = [NSIndexPath indexPathForRow:row inSection:sectionIdxOfPageList];
	NSUInteger rowNew = [self rowRedirect:indexPath];
	id data = [netOpResult getNetResultWithRowIndex:rowNew];
	return data;
}

#pragma mark UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
	self.currentIdxPath = indexPath;
	if (indexPath.section != sectionIdxOfPageList) { //customed
		return [pageTableDelegate GcPageTable:self heightForRowAtIndexPath:indexPath];
	}
	NSUInteger row = indexPath.row;
	if ([netOpResult isEmptyData]) {
		if ([pageTableDelegate respondsToSelector:@selector(GcPageTable:rowHeightForEmptyPage:indexPath:)]) {
			return [pageTableDelegate GcPageTable:self 
							rowHeightForEmptyPage:([netOpResult totalCount] == 0)
										indexPath:indexPath];
		}
	}
	else if ([self isRowMoreItem:indexPath]) {
		return self.rowHeight;
	}
    else if ([self isRowTitleItem:indexPath]) {
        return titleRowHeight;
    }
	else {
		row = [self rowRedirect:indexPath];
		int nStatus = [netOpResult getNetStatusWithRowIndex:row];
		if (nStatus >= NET_STATUS_NONE) {
			return self.rowHeight;
		}
		else if (NET_STATUS_FINISH == nStatus) {
			id data = [netOpResult getNetResultWithRowIndex:row];
			if (data) {
				return [pageTableDelegate GcPageTable:self heightForCustomCell:customCellCache  customData:data];
			}
		}
		return 0.0f;
	}
	return self.rowHeight;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	self.currentIdxPath = indexPath;
	if (indexPath.section != sectionIdxOfPageList) { //customed
		return [pageTableDelegate GcPageTable:self didSelectRowAtIndexPath:indexPath];
	}

    if ([self isRowTitleItem:indexPath]) {
        return;
    }
	else if ([self isRowMoreItem:indexPath]) {
		if (!bIsLoadingMoreRecords) {
			[self loadMoreRecordsIndicator:[tableView cellForRowAtIndexPath:indexPath]];
		}
	}
	else {
		NSUInteger row = [self rowRedirect:indexPath];
		id data = [netOpResult getNetResultWithRowIndex:row];
		if (data && [pageTableDelegate respondsToSelector:@selector(GcPageTable:didSelectRowWithData:)])
			[pageTableDelegate GcPageTable:self didSelectRowWithData:data];
	}
}

-(void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
	self.currentIdxPath = indexPath;
	if(self.clrOdd != nil && self.clrEven !=nil)
		cell.backgroundColor = (indexPath.row % 2) ? self.clrOdd :  self.clrEven;
	if (0 == indexPath.row) {
		bRespondsToSel_willDisplayCell = [pageTableDelegate respondsToSelector:@selector(GcPageTable:willDisplayCell:forRowAtIndexPath:)];
		[self freshEmptyTableFooterViewWithColor:nil image:nil text:nil];
	}
	if (bRespondsToSel_willDisplayCell) {
		[pageTableDelegate GcPageTable:self willDisplayCell:cell forRowAtIndexPath:indexPath];
	}
	self.currentIdxPath = nil;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
	if ([pageTableDelegate respondsToSelector:@selector(GcPageTable:heightForHeaderInSection:)]) {
		return [pageTableDelegate GcPageTable:self heightForHeaderInSection:section];
	}
	return 0.0f;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
	if ([pageTableDelegate respondsToSelector:@selector(GcPageTable:viewForHeaderInSection:)]) {
		return [pageTableDelegate GcPageTable:self viewForHeaderInSection:section];
	}
	return nil;
}

#pragma mark UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
	return sectionCount;
}

- (NSInteger)tableView:(UITableView *)table numberOfRowsInSection:(NSInteger)section
{
	if (section != sectionIdxOfPageList) { //customed
		return [pageTableDelegate GcPageTable:self numberOfRowsInSection:section];
	}
	
	if ([netOpResult isEmptyData]) {
		if ([pageTableDelegate respondsToSelector:@selector(GcPageTable:numberOfRowsForEmptyPage:)]) {
			return [pageTableDelegate GcPageTable:self numberOfRowsForEmptyPage:[netOpResult isTotalCountZero]];
		}
		return 0;// emptyData;
	}
	else if ([netOpResult isAllPageExpanded]){
		return (self.titleCellCache == nil) ? [netOpResult totalCount] : [netOpResult totalCount] + 1;
	}
	else {
		return (self.titleCellCache == nil) ? [netOpResult limitedRowsCount] + 1 : [netOpResult limitedRowsCount] + 2;
	}

	return 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	self.currentIdxPath = indexPath;
	if (indexPath.section != sectionIdxOfPageList) { //customed
		return [pageTableDelegate GcPageTable:self cellForRowAtIndexPath:indexPath];
	}

	UITableViewCell *cell = nil;
	if ([netOpResult isEmptyData]) {
		if ([pageTableDelegate respondsToSelector:@selector(GcPageTable:cellForEmptyPage:indexPath:)]) {
			cell = [pageTableDelegate GcPageTable:self cellForEmptyPage:[netOpResult isTotalCountZero] indexPath:indexPath];
		}
		else {
			cell = [self prepareMoreCell:tableView withTip:strNoDataTip];
		}

	}
	else if ([self isRowMoreItem:indexPath]) {
		return [self prepareMoreCell:tableView withTip:self.strMoreTip];
	}
    if ([self isRowTitleItem:indexPath]) {
        return self.titleCellCache;
    }
	else  { 
		NSUInteger row = [self rowRedirect:indexPath];		
		int nPageStatus = [netOpResult getNetStatusWithRowIndex:row];
		id data = [netOpResult getNetResultWithRowIndex:row];
		if (nPageStatus >= 0 || ((NET_STATUS_FINISH == nPageStatus) && data)) {
			if (!data) {
                cell = [[[UITableViewCell alloc] initWithStyle:0 reuseIdentifier:@"Loading"] autorelease];
                cell.selectionStyle = UITableViewCellSelectionStyleNone;
				cell.textLabel.text = @"加载中⋯";
			}
            else {
                if ([pageTableDelegate respondsToSelector:@selector(GcPageTable:cellIdentifierBycustomData:)] &&
                    [pageTableDelegate respondsToSelector:@selector(GcPageTable:cellCopyByCustomData:)]) {
                    
                    NSString* customCellIdentifier = [pageTableDelegate GcPageTable:self cellIdentifierBycustomData:data];
                    cell = [tableView dequeueReusableCellWithIdentifier:customCellIdentifier];
                    if (nil == cell) {
                        cell = [pageTableDelegate GcPageTable:self cellCopyByCustomData:data];
                    }
                }
                else {
                    NSString* cellIdentifier = customCellCache.reuseIdentifier;
                    cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
                    if (nil == cell) {
                        cell = [pageTableDelegate GcPageTable:self cellCopyFromCacheCell:customCellCache];
                    }
                }
                [pageTableDelegate GcPageTable:self customCell:cell customData:data];
            }
            
			if (0 == nPageStatus && netOpResult.nNetOpCount < [GcNetOpStatusMng getMaxNetOpCount]){
				int nPageIdx = [netOpResult getPageCount:row + 1] - 1;
				int nNetStatus = [pageTableDelegate GcPageTable:self downloadPageIndex:nPageIdx pageSize:netOpResult.pageSize];
				if (nNetStatus >= 0) {
					netOpResult.nNetOpCount = netOpResult.nNetOpCount + 1;
				}
				[netOpResult updatePageItem:nPageIdx pageItems:nil netStatus:nNetStatus];
			}		
        }
		else {
			if ([cell.textLabel.text length] <= 0) {
				cell.textLabel.text = @"没有相关数据";
			}
		}

	}
	if (nil == cell) {
		cell = [[[UITableViewCell alloc] initWithStyle:0 reuseIdentifier:@"xx"] autorelease];
		cell.selectionStyle = UITableViewCellSelectionStyleNone;
	}
	return cell;
}

-(UITableViewCell*) prepareMoreCell:(UITableView *)tableView withTip:(NSString *)tip{
	static NSString* identifier = @"more";
	
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
	if(nil == cell) {
		cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier] autorelease];
	}
	cell.accessoryType = UITableViewCellAccessoryNone;
	cell.selectionStyle = UITableViewCellSelectionStyleNone;
	cell.textLabel.textAlignment = UITextAlignmentCenter;
	cell.textLabel.numberOfLines = 0;
	cell.textLabel.text = tip;
	return cell;
}

#pragma mark -
- (void)enableEgoRefreshTableHeaderView
{
	if (myRefreshHeaderView == nil) {
		
		GcEGORefreshTableHeaderView *view = [[GcEGORefreshTableHeaderView alloc] initWithFrame:
					CGRectMake(0.0f, 0.0f - self.bounds.size.height, self.frame.size.width, self.bounds.size.height)];
		view.delegate = self;
		[self addSubview:view];
		self.myRefreshHeaderView = view;
		[view release];
		
	}
	
	//  update the last update date
	[myRefreshHeaderView refreshLastUpdatedDate];
	
}

- (void)addTableFooterViewWithColor:(UIColor*)clr image:(UIImage *)image text:(NSString*)text viewHeight:(CGFloat)fHeight
{
	if (fHeight > 0) {
		CGRect rc = CGRectMake(0, 0, CGRectGetWidth(self.frame), fHeight);
		UIView* view = [[UIView new] autorelease];
		view.frame = rc;
		self.tableFooterView = view;
		if (nil == clr) {
			clr = [UIColor clearColor];
		}
		view.backgroundColor = clr;
        
        if (image) {
			UIImageView *imgView = [[[UIImageView alloc] initWithImage:image] autorelease];
			imgView.center = CGPointMake(CGRectGetWidth(self.frame)/2, CGRectGetHeight(self.frame)*0.382);
			[view addSubview:imgView];
		}
				
		if ([text length] > 0) {
			UILabel *label = [[UILabel new] autorelease];
            label.numberOfLines = 0;
			label.text = text;
			label.textColor = [UIColor darkGrayColor];
			label.backgroundColor = [UIColor clearColor];
            
            float margin = 20;
            float labelWidth = CGRectGetWidth(self.frame) - margin*2;
			CGSize textSize = [text sizeWithFont:label.font constrainedToSize:CGSizeMake(labelWidth, 300)];
            float originWithoutImageY = CGRectGetHeight(self.frame)*0.382;
            rc.origin.x = CGRectGetWidth(self.frame)/2 - textSize.width/2;
            rc.origin.y = (image ? originWithoutImageY+image.size.height/2 : originWithoutImageY);
            rc.size.width = textSize.width;
			rc.size.height = textSize.height;
			label.frame = rc;
            
			[view addSubview:label];
		}
	}
	else {
		self.tableFooterView = nil;
	}
}

- (void)freshEmptyTableFooterViewWithColor:(UIColor*)clr image:(UIImage *)image text:(NSString*)text
{
	CGFloat fHeight = CGRectGetHeight(self.frame) - self.contentSize.height + CGRectGetHeight(self.tableFooterView.frame);
	[self addTableFooterViewWithColor:clr image:image text:text viewHeight:fHeight];
}

- (void)freshEmptyTableWithColor:(UIColor*)clr image:(UIImage *)image text:(NSString*)text
{
	[self addTableFooterViewWithColor:clr image:image text:text viewHeight:CGRectGetHeight(self.frame)];
}

- (int) totalCount
{
	return [netOpResult totalCount];
}

- (BOOL)isFirstPageLoaded
{
	return [netOpResult isFirstPageLoaded];
}

#pragma mark -
#pragma mark UIScrollViewDelegate Methods

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {	
	[myRefreshHeaderView egoRefreshScrollViewDidScroll:scrollView];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {
	[myRefreshHeaderView egoRefreshScrollViewDidEndDragging:scrollView];
	CGFloat fDelta = scrollView.contentOffset.y + scrollView.frame.size.height - scrollView.contentSize.height;
	if (fDelta > 40) {
		[self loadMoreRecordsIfNeed];
	}
}

#pragma mark -
#pragma mark EGORefreshTableHeaderDelegate Methods

- (void)egoRefreshTableHeaderDidTriggerRefresh:(GcEGORefreshTableHeaderView*)view{
	self.currentIdxPath = nil;
	if (!bIsLoadingMoreRecords) {
		[netOpResult reset];
		int nNetStatus = [pageTableDelegate GcPageTable:self downloadPageIndex:0 pageSize:netOpResult.pageSize];
		if (nNetStatus >= 0) {
			netOpResult.nNetOpCount = netOpResult.nNetOpCount + 1;
		}
		[netOpResult updatePageItem:0 pageItems:nil netStatus:nNetStatus];
	}
}

- (BOOL)egoRefreshTableHeaderDataSourceIsLoading:(GcEGORefreshTableHeaderView*)view{
	return netOpResult.nNetOpCount > 0;	
}

- (NSDate*)egoRefreshTableHeaderDataSourceLastUpdated:(GcEGORefreshTableHeaderView*)view{
	return [NSDate date];
}

@end

