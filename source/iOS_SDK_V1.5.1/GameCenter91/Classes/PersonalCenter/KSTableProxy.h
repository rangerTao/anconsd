//
//  KSTableProxy.h
//  TableProxy
//
//  Created by sie kensou on 13-8-19.
//  Copyright (c) 2013年 sie kensou. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface KSTableProxy : NSObject
- (void)becomeProxyForTable:(UITableView *)table withSections:(NSArray *)sections;
- (void)reload;
- (void)reloadSectionAtIndex:(NSUInteger)section withAnimation:(UITableViewRowAnimation)animation;
@end

@interface KSTableProxySection : NSObject
@property (nonatomic, retain) NSArray *rows;

@property (nonatomic, copy) NSString * (^headerTitle)(NSInteger section);
@property (nonatomic, copy) NSString * (^footerTitle)(NSInteger section);

@property (nonatomic, copy) CGFloat (^headerHeight)(NSInteger section);
@property (nonatomic, copy) CGFloat (^footerHeight)(NSInteger section);

@property (nonatomic, copy) UIView * (^viewForHeader)(NSInteger section);
@property (nonatomic, copy) UIView * (^viewForFooter)(NSInteger section);

@property (nonatomic, copy) NSInteger (^repeatCount)();
@end

@interface KSTableProxyRow : NSObject
@property (nonatomic, copy) UITableViewCell * (^cell)(NSInteger section, NSInteger row);
@property (nonatomic, copy) CGFloat (^cellHeight)(NSInteger section, NSInteger row);
@property (nonatomic, copy) void (^didSelected)(NSInteger section, NSInteger row);
@property (nonatomic, copy) NSUInteger (^repeatCount)();
@end
