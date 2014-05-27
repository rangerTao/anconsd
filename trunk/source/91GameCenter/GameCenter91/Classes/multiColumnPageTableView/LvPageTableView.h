//
//  LvPageTableView.h
//  hiyo
//
//  Created by  hiyo on 13-5-15.
//
//

#import <UIKit/UIKit.h>

@protocol LvPageTableDelegate;
@interface LvPageTableView : UITableView
@property (nonatomic, assign) id<LvPageTableDelegate> pageDelegate;
@property (nonatomic, assign) int totalCount;
@property (nonatomic, assign) int currentPage;
@property (nonatomic, assign) int pageSize;

@property (nonatomic, assign) int columnNum;
@property (nonatomic, retain) UIColor *selectedColor;

//由delegate来用，通知已经完成下一页的加载了
- (void)didDownloadPage:(NSInteger)pageIdx totalCount:(NSInteger)total pageArray:(NSArray *)pageArr success:(BOOL)bSucess;
//整个刷新
- (void)launchRefreshing;

- (NSArray *)datasOfVisibleSubCells; //所有可见的subCell的数据
- (NSArray *)visibleSubCells;        //所有可见的subCell
@end


@protocol LvPageTableDelegate<NSObject>

@required
//加载分页
- (void)pageTable:(LvPageTableView *)pageTable downloadPageIndex:(NSInteger)pageIdx pageSize:(NSInteger)pageSize;

- (UITableViewCell *)pageTable:(LvPageTableView*)tableView cellForRowAtPoint:(CGPoint)point dataForCell:(id)data;
- (CGFloat)pageTable:(LvPageTableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath;
- (void)pageTable:(LvPageTableView *)tableView didSelectRowAtPoint:(CGPoint)point dataForCell:(id)data;
@end