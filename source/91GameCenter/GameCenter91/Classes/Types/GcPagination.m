//
//  GcPagination.m
//  GameCenter91
//
//  Created by  hiyo on 13-11-8.
//
//

#import "GcPagination.h"

@implementation GcPagination
@synthesize pageIndex, pageSize;
- (id) init
{
	self = [super init];
	if (self != nil) {
		pageIndex = 1;
		pageSize = 10;
	}
	return self;
}

- (void)setPageSize:(int)newPageSize
{
	if (newPageSize <= 0)
		return;
	
	pageSize = (newPageSize + 4) / 5 * 5;
	if (pageSize > 50)
		pageSize = 50;
	return;
}

- (void)setPageIndex:(int)newPageIndex
{
	if (newPageIndex <= 0)
		return;
	pageIndex = newPageIndex;
}

- (NSString*)description
{
	return [NSString stringWithFormat:@"<Pagination: currentPage/pageSize: (%d / %d)>", pageIndex, pageSize];
}
@end