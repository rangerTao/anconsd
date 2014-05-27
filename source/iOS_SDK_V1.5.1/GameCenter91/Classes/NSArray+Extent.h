//
//  NSArray+Extent.h
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-10.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSArray(Extent)
- (id)valueAtIndex:(int)index;
- (id)valueAtIndexPath:(NSIndexPath *)indexPath;
- (NSArray *)sortedArrayWithKey:(NSString *)key ascending:(BOOL)ascending;

//随机排序
- (NSArray *)randomizedArray;
@end

@interface NSMutableArray(Extent) 
- (void)sortWithKey:(NSString *)key ascending:(BOOL)ascending;
@end
