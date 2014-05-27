//
//  LvPageTableView.m
//  hiyo
//
//  Created by  hiyo on 13-5-15.
//
//

#import "LvPageTableView.h"
#import "EGORefreshTableHeaderView.h"
#import "EGORefreshTableFootView.h"

#define TAG_SUB_TABLEVIEW_CELL_BASE      799439

@interface LvPageTableView()<EGORefreshTableHeaderDelegate, EGORefreshTableFootDelegate, UITableViewDataSource, UITableViewDelegate, UIGestureRecognizerDelegate>

@property (nonatomic, retain) EGORefreshTableHeaderView *refreshHeadView;
@property (nonatomic, retain) EGORefreshTableFootView *refreshFootView;
@property (nonatomic, assign) BOOL reloading;

@property (nonatomic, retain) NSMutableArray *dataArr;
@property (nonatomic, retain) UITapGestureRecognizer *tapGestureRecognizer;

- (void)initTable;
- (void)addRefreshHeadView;
- (void)addRefreshFootView;
- (void)modifyFootFrame;
@end

@implementation LvPageTableView

#pragma mark -
- (void)didDownloadPage:(NSInteger)pageIdx totalCount:(NSInteger)total pageArray:(NSArray *)pageArr success:(BOOL)bSucess
{
    _reloading = NO;
    //停止footerView的转圈
    [self.refreshFootView egoRefreshScrollViewDataSourceDidFinishedLoading:self];
    
    if (bSucess) {
        self.currentPage++;
        self.totalCount = total;
        //到底了
        if (_totalCount <= pageIdx*self.pageSize) {
            _refreshFootView.reachedTheEnd = YES;
        }
        else {
            _refreshFootView.reachedTheEnd = NO;
        }
        //加数据
        if (pageIdx == 1) {
            //停止headerView的转圈
            [self.refreshHeadView egoRefreshScrollViewDataSourceDidFinishedLoading:self];
            [self.dataArr removeAllObjects];
            self.currentPage = 1;
        }
        [self.dataArr addObjectsFromArray:pageArr];
        //上拉一点，让人知道底下还有东西
        if (pageIdx != 1) {
            CGPoint pt = self.contentOffset;
            pt.y += 20.0;
            [self setContentOffset:pt animated:YES];
        }
    }
    
    [self reloadData];
    [self modifyFootFrame];
}

- (void)launchRefreshing
{
    [self initTable];
    [self reloadData];
    self.refreshFootView.reachedTheEnd = NO;
    
    [self setContentOffset:CGPointMake(0, -65)];
    [_refreshHeadView egoRefreshScrollViewDidEndDragging:self];
}

- (NSArray *)datasOfVisibleSubCells
{
    NSMutableArray *retArr = [NSMutableArray array];
    for (UITableViewCell *cell in self.visibleCells) {
        for (int i = 0; i < self.columnNum; i++) {
            UIView *subCell = [cell viewWithTag:TAG_SUB_TABLEVIEW_CELL_BASE*100+i];
            if (subCell != nil) {
                NSIndexPath *indexPath = [self indexPathForCell:cell];
                int index = indexPath.row*self.columnNum+i;
                if (index < [self.dataArr count]) {
                    id data = [self.dataArr objectAtIndex:index];
                    [retArr addObject:data];
                }
            }
        }
    }
    return retArr;
}

- (NSArray *)visibleSubCells
{
    NSMutableArray *retArr = [NSMutableArray array];
    for (UITableViewCell *cell in self.visibleCells) {
        for (int i = 0; i < self.columnNum; i++) {
            UIView *subCell = [cell viewWithTag:TAG_SUB_TABLEVIEW_CELL_BASE*100+i];
            if (subCell != nil) {
                [retArr addObject:subCell];
            }
        }
    }
    return retArr;
}

#pragma mark - others
- (void)dealloc
{
    self.refreshHeadView = nil;
    self.refreshFootView = nil;
    self.dataArr = nil;
    self.tapGestureRecognizer = nil;
    self.selectedColor = nil;
    [super dealloc];
}
- (void)initTable
{
    self.delegate = self;
    self.dataSource = self;
    self.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    self.currentPage = 0;
    self.totalCount = 0;
    _reloading = NO;
    if (self.dataArr==nil) {
        self.dataArr = [NSMutableArray array];
    }
    if (self.columnNum <= 0) {
        self.columnNum = 1;
    }
    
    if (self.tapGestureRecognizer == nil) {
        self.tapGestureRecognizer = [[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(viewTapped:)] autorelease];
        self.tapGestureRecognizer.delegate = self;
        [self addGestureRecognizer:self.tapGestureRecognizer];
    }
    
    [self addRefreshHeadView];
    [self addRefreshFootView];
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch
{
    if ([touch.view isKindOfClass:[UIControl class]] && touch.view != self)
    {
        return NO;
    }
    return YES;
}

- (void)addRefreshHeadView
{
    if (_refreshHeadView == nil) {
        _refreshHeadView = [[EGORefreshTableHeaderView alloc] initWithFrame: CGRectMake(0.0f, 0.0f - self.bounds.size.height, self.frame.size.width, self.bounds.size.height)];
		_refreshHeadView.delegate = self;
		[self addSubview:_refreshHeadView];
	}
}

- (void)addRefreshFootView
{
    if (_refreshFootView == nil) {
		_refreshFootView = [[EGORefreshTableFootView alloc] initWithFrame: CGRectMake(0, CGRectGetHeight(self.frame), CGRectGetWidth(self.frame), 650)];
		_refreshFootView.delegate = self;
		[self addSubview:_refreshFootView];
	}
}

- (void)modifyFootFrame
{
    float height = MAX(self.contentSize.height, CGRectGetHeight(self.frame));
    _refreshFootView.frame = CGRectMake(0.0f, height, self.frame.size.width, 650);
}

- (void)setColumnNum:(int)columnNum
{
    _columnNum = 1;
    if (columnNum > 0) {
        _columnNum = columnNum;
    }
}

- (void)viewTapped:(UIGestureRecognizer *)recognizer {
    
    if ([recognizer state] != UIGestureRecognizerStateEnded) {
        return;
    }
    
    CGPoint tapPoint = [recognizer locationInView:self];

    for (UITableViewCell *cell in self.visibleCells) {
        if (CGRectContainsPoint(cell.frame, tapPoint)) {
            CGPoint pt = [self convertPoint:tapPoint toView:cell];
            for (int i = 0; i < self.columnNum; i++) {
                UIView *subCell = [cell viewWithTag:TAG_SUB_TABLEVIEW_CELL_BASE*100+i];
                if (subCell != nil) {
                    if (CGRectContainsPoint(subCell.frame, pt)) {
                        if (self.selectedColor != nil) {
                            //seleted color
                            UIView *tmpView = [[[UIView alloc] initWithFrame:subCell.bounds] autorelease];
                            tmpView.backgroundColor = self.selectedColor;
                            [subCell addSubview:tmpView];
                            [subCell sendSubviewToBack:tmpView];
                            
                            [UIView animateWithDuration:0.2 animations:^{
                                tmpView.alpha = 0.0;
                            } completion:^(BOOL finished) {
                                [tmpView removeFromSuperview];
                            }];
                        }
                        
                        NSIndexPath *indexPath = [self indexPathForCell:cell];
//                        NSLog(@"selected row : %d, column : %d", indexPath.row, i);
                        if ([self.pageDelegate respondsToSelector:@selector(pageTable:didSelectRowAtPoint:dataForCell:)]) {
                            int index = indexPath.row*self.columnNum+i;
                            if (index < [self.dataArr count]) {
                                id data = [self.dataArr objectAtIndex:index];
                                CGPoint desPt = CGPointMake(indexPath.row, i);
                                [self.pageDelegate pageTable:self didSelectRowAtPoint:desPt dataForCell:data];
                            }
                        }
                        break;
                    }
                }
            }
            break;
        }
    }
    
}

#pragma mark EGORefreshTableHeaderDelegate Methods

- (void)egoRefreshTableHeaderDidTriggerRefresh:(EGORefreshTableHeaderView*)view{
    if ([self.pageDelegate respondsToSelector:@selector(pageTable:downloadPageIndex:pageSize:)]) {
        [self.pageDelegate pageTable:self downloadPageIndex:0 pageSize:self.pageSize];
        _reloading = YES;
    }
}

- (BOOL)egoRefreshTableHeaderDataSourceIsLoading:(EGORefreshTableHeaderView *)view{
	
	return _reloading;
}

#pragma mark EGORefreshTableFootorDelegate Methods

- (void)egoRefreshTableFootDidTriggerRefresh:(EGORefreshTableFootView*)view{
    //让delegate开始加载下一页
    if ([self.pageDelegate respondsToSelector:@selector(pageTable:downloadPageIndex:pageSize:)]) {
        [self.pageDelegate pageTable:self downloadPageIndex:self.currentPage pageSize:self.pageSize];
        _reloading = YES;
    }
}

- (BOOL)egoRefreshTableFootDataSourceIsLoading:(EGORefreshTableFootView*)view{
	
	return _reloading;
}

#pragma mark - UITableViewDelegate and datasource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    int total = MIN(self.totalCount, (self.currentPage) * self.pageSize);
    return (int)ceilf(1.0*total/self.columnNum);
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = nil;
    if ([self.pageDelegate respondsToSelector:@selector(pageTable:cellForRowAtPoint:dataForCell:)]) {
        
        static NSString *CellIdentifier = @"CellIdentifier";
        cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
        if (cell == nil) {
            cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
        }
        
        for (int i = indexPath.row * self.columnNum; i < self.totalCount && i < (indexPath.row+1)*self.columnNum; i++) {
            CGPoint pt = CGPointMake(i/self.columnNum, i%self.columnNum);
            id data = [self.dataArr objectAtIndex:i];
            UIView *subCell = [self.pageDelegate pageTable:self cellForRowAtPoint:pt dataForCell:data];
            float cellHeight = [self tableView:self heightForRowAtIndexPath:indexPath];
            subCell.frame = CGRectMake(CGRectGetWidth(cell.frame)/self.columnNum*pt.y, 0, CGRectGetWidth(cell.frame)/self.columnNum, cellHeight);
            
            UIView *oldCell = [cell viewWithTag:TAG_SUB_TABLEVIEW_CELL_BASE*100+i-indexPath.row * self.columnNum];
            subCell.tag = TAG_SUB_TABLEVIEW_CELL_BASE*100+i-indexPath.row * self.columnNum;
            if (oldCell != nil) {
                [oldCell removeFromSuperview];
            }
            [cell addSubview:subCell];
        }
    }
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    float height = 0;
    if ([self.pageDelegate respondsToSelector:@selector(pageTable:heightForRowAtIndexPath:)]) {
        height = [self.pageDelegate pageTable:self heightForRowAtIndexPath:indexPath];
    }
    
    return height;
}

#pragma mark - UIScrollViewDelegate Methods

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    [_refreshHeadView egoRefreshScrollViewDidScroll:scrollView];
	[_refreshFootView egoRefreshScrollViewDidScroll:scrollView];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {
    [_refreshHeadView egoRefreshScrollViewDidEndDragging:scrollView];
	[_refreshFootView egoRefreshScrollViewDidEndDragging:scrollView];
}

@end
