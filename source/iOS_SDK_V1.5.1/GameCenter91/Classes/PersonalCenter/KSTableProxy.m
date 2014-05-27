//
//  KSTableProxy.m
//  TableProxy
//
//  Created by sie kensou on 13-8-19.
//  Copyright (c) 2013年 sie kensou. All rights reserved.
//

#import "KSTableProxy.h"

@interface KSTableProxyRow()
@property (nonatomic, assign) NSInteger cachedRepeatCount;

- (void)refreshRepeatCount;
@end

@implementation KSTableProxyRow
#if !__has_feature(objc_arc)
- (void)dealloc
{
    self.cell = nil;
    self.cellHeight = nil;
    self.didSelected = nil;
    self.repeatCount = nil;
    [super dealloc];
}
#endif

- (void)refreshRepeatCount
{
    int count = 1;
    if (self.repeatCount)
    {
        count = self.repeatCount();
        if (count < 0)
            count = 0;
    }
    self.cachedRepeatCount = count;
}
@end

@interface KSTableProxySection()
@property (nonatomic, assign) NSInteger cachedRepeatCount;
@property (nonatomic, assign) NSInteger cachedRowCount;

- (void)refreshRepeatCount;
@end

@implementation KSTableProxySection
#if !__has_feature(objc_arc)
- (void)dealloc
{
    self.rows = nil;
    self.headerTitle = nil;
    self.footerTitle = nil;
    self.headerHeight = nil;
    self.footerHeight = nil;
    self.viewForHeader = nil;
    self.viewForFooter = nil;
    self.repeatCount = nil;
    [super dealloc];
}
#endif

- (void)refreshRepeatCount
{
    int count = 1;
    if (self.repeatCount)
    {
        count = self.repeatCount();
        if (count < 1)
            count = 1;
    }
    self.cachedRepeatCount = count;
    
    int row = 0;
    for (KSTableProxyRow *proxyRow in self.rows) {
        [proxyRow refreshRepeatCount];
        row += proxyRow.cachedRepeatCount;
    }
    self.cachedRowCount = row;
}
@end

@interface KSTableProxy()<UITableViewDataSource, UITableViewDelegate>
@property (nonatomic, retain) NSArray *sections;
@property (nonatomic, assign) UITableView *table;

@property (nonatomic, assign) NSInteger cachedSectionCount;

#pragma data source quick methods
- (NSInteger)numberOfRowsInSection:(NSInteger)section;
- (UITableViewCell *)cellForRowAtIndexPath:(NSIndexPath *)indexPath;

- (NSInteger)numberOfSections;
- (NSString *)titleForHeaderInSection:(NSInteger)section;
- (NSString *)titleForFooterInSection:(NSInteger)section;


#pragma delegate quick methods
- (void)didSelectRowAtIndexPath:(NSIndexPath *)indexPath;
- (CGFloat)heightForRowAtIndexPath:(NSIndexPath *)indexPath;

- (CGFloat)heightForHeaderInSection:(NSInteger)section;
- (CGFloat)heightForFooterInSection:(NSInteger)section;

- (UIView *)viewForHeaderInSection:(NSInteger)section;
- (UIView *)viewForFooterInSection:(NSInteger)section;

@end

@implementation KSTableProxy
#if !__has_feature(objc_arc)
- (void)dealloc
{
    self.table = nil;
    self.sections = nil;
    [super dealloc];
}
#endif

- (void)becomeProxyForTable:(UITableView *)table withSections:(NSArray *)sections
{
    self.table = table;
    self.sections = sections;
    
    [self refreshSectionCounts];
    
    self.table.delegate = self;
    self.table.dataSource = self;
    
    [self.table reloadData];
}

- (void)reload
{
    [self refreshSectionCounts];
    [self.table reloadData];
}

- (void)reloadSectionAtIndex:(NSUInteger)section withAnimation:(UITableViewRowAnimation)animation
{
    [self refreshSectionCounts];
    NSInteger sectionIndex = [self realIndexForSection:section];
    [self.table reloadSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:animation];
}

- (void)refreshSectionCounts
{
    self.cachedSectionCount = 0;
    for (KSTableProxySection *section in self.sections)
    {
        [section refreshRepeatCount];
        self.cachedSectionCount += section.cachedRepeatCount;
    }
}

- (NSInteger)realIndexForSection:(NSInteger)section
{
    if (section < 0 || section >= self.cachedSectionCount)
        return -1;
    
    int sectionIndex = 0;
    for (int i = 0; i < [self.sections count]; i++)
    {
        KSTableProxySection *sec = [self.sections objectAtIndex:i];
        int begin = i;
        int length = sec.cachedRepeatCount;
        if (section >= begin && section - begin < length)
        {
            sectionIndex = i;
            break;
        }
    }
    return sectionIndex;
}

- (KSTableProxySection *)proxySectionAtIndex:(NSInteger)section
{
    NSInteger sectionIndex = [self realIndexForSection:section];
    if (sectionIndex < 0)
        return nil;
    return [self.sections objectAtIndex:sectionIndex];
}

- (NSInteger)realIndexForRow:(NSInteger)row inSection:(KSTableProxySection *)proxySection
{
    if (row < 0 || row >= proxySection.cachedRowCount)
        return -1;
    
    int rowIndex = 0;
    for (int i = 0, begin = i; i < [proxySection.rows count]; i++)
    {
        KSTableProxyRow *proxyRow = [proxySection.rows objectAtIndex:i];
        int length = proxyRow.cachedRepeatCount;
        if (row >= begin && row - begin < length)
        {
            rowIndex = i;
            break;
        }
        else
        {
            begin += length;
        }
    }
    return rowIndex;
}

- (KSTableProxyRow *)proxyRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSInteger section = [indexPath section];
    KSTableProxySection *proxySection = [self proxySectionAtIndex:section];
    NSInteger rowIndex = [self realIndexForRow:indexPath.row inSection:proxySection];
    return [proxySection.rows objectAtIndex:rowIndex];
}

#pragma data source quick methods
- (NSInteger)numberOfRowsInSection:(NSInteger)section
{
    return [self proxySectionAtIndex:section].cachedRowCount;
}

- (UITableViewCell *)cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    KSTableProxyRow *row = [self proxyRowAtIndexPath:indexPath];
    if (row.cell)
        return row.cell(indexPath.section, indexPath.row);
    return nil;
}

- (NSInteger)numberOfSections
{
    return self.cachedSectionCount;
}

- (NSString *)titleForHeaderInSection:(NSInteger)section
{
    KSTableProxySection *proxySection = [self proxySectionAtIndex:section];
    if (proxySection.headerTitle)
        return proxySection.headerTitle(section);
    return nil;
}

- (NSString *)titleForFooterInSection:(NSInteger)section
{
    KSTableProxySection *proxySection = [self proxySectionAtIndex:section];
    if (proxySection.footerTitle)
        return proxySection.footerTitle(section);
    return nil;
}


#pragma delegate quick methods
- (void)didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    KSTableProxyRow *row = [self proxyRowAtIndexPath:indexPath];
    if (row.didSelected)
        return row.didSelected(indexPath.section, indexPath.row);
}

- (CGFloat)heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    KSTableProxyRow *proxyRow = [self proxyRowAtIndexPath:indexPath];
    if (proxyRow.cellHeight)
        return proxyRow.cellHeight(indexPath.section, indexPath.row);
    return self.table.rowHeight;
}

- (CGFloat)heightForHeaderInSection:(NSInteger)section
{
    KSTableProxySection *proxySection = [self proxySectionAtIndex:section];
    if (proxySection.headerHeight)
        return proxySection.headerHeight(section);
    return self.table.sectionHeaderHeight;
}

- (CGFloat)heightForFooterInSection:(NSInteger)section
{
    KSTableProxySection *proxySection = [self proxySectionAtIndex:section];
    if (proxySection.footerHeight)
        return proxySection.footerHeight(section);
    return self.table.sectionFooterHeight;
}

- (UIView *)viewForHeaderInSection:(NSInteger)section
{
    KSTableProxySection *proxySection = [self proxySectionAtIndex:section];
    if (proxySection.viewForHeader)
        return proxySection.viewForHeader(section);
    return nil;    
}

- (UIView *)viewForFooterInSection:(NSInteger)section
{
    KSTableProxySection *proxySection = [self proxySectionAtIndex:section];
    if (proxySection.viewForFooter)
        return proxySection.viewForFooter(section);
    return nil;
}

#pragma UITableView DataSource and Delegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [self numberOfSections];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self numberOfRowsInSection:section];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [self cellForRowAtIndexPath:indexPath];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    return[self didSelectRowAtIndexPath:indexPath];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    return [self titleForHeaderInSection:section];
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return [self heightForHeaderInSection:section];
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    return [self viewForHeaderInSection:section];
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return [self heightForFooterInSection:section];
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    return [self viewForFooterInSection:section];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [self heightForRowAtIndexPath:indexPath];
}

@end
