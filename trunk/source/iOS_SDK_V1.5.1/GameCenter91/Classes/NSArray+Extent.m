//
//  NSArray+Extent.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-10.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "NSArray+Extent.h"

@implementation NSArray(Extent)
- (id)valueAtIndex:(int)index
{
    if (index < 0 || index >= [self count])
        return nil;
    return [self objectAtIndex:index];
}

- (id)valueAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section < 0 || indexPath.section >= [self count]) {
        return nil;
    }
    
    NSArray *subArray = [NSArray arrayWithArray:[self objectAtIndex:indexPath.section]];
    if (indexPath.row < 0 || indexPath.row >= [subArray count]) {
        return nil;
    }
    
    return [subArray objectAtIndex:indexPath.row];
}

- (NSArray *)sortedArrayWithKey:(NSString *)key ascending:(BOOL)ascending
{
    if ([key length] == 0)
        return self;
    
    NSSortDescriptor *descriptor = [NSSortDescriptor sortDescriptorWithKey:key ascending:ascending];
    NSArray *sort = [NSArray arrayWithObject:descriptor];
    return [self sortedArrayUsingDescriptors:sort];
}

- (NSArray *)randomizedArray
{
    NSMutableArray *results = [NSMutableArray arrayWithArray:self];
    int i = [results count];
    while (--i > 0) {
        int j = rand() % (i+1);
        [results exchangeObjectAtIndex:i withObjectAtIndex:j];
    }
    return [NSArray arrayWithArray:results];
}
@end


@implementation NSMutableArray(Extent)

- (void)sortWithKey:(NSString *)key ascending:(BOOL)ascending
{
    if ([key length] == 0)
        return;
    
    NSSortDescriptor *descriptor = [NSSortDescriptor sortDescriptorWithKey:key ascending:ascending];
    NSArray *sort = [NSArray arrayWithObject:descriptor];
    [self sortUsingDescriptors:sort];
}

@end