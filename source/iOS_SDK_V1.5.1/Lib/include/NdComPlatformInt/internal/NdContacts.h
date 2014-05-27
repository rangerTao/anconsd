//
//  NdContacts.h
//  NdComPlatform_SNS
//
//  Created by Sie Kensou on 10-10-12.
//  Copyright 2010 NetDragon WebSoft Inc.. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface NdContacts : NSObject {	
	NSMutableDictionary	*_addDict;
	NSMutableDictionary	*_delDict;
	
	int				_cachedModifyTime;
	NSString		*_cachedHashValue;
	NSString		*_cachedSendTime;	
	NSDictionary	*_allContactDict;
	int				_newModifyTime;
	NSString		*_newHashValue;
}
@property (nonatomic, retain) NSString *newHashValue;
@property (nonatomic, assign) int newModifyTime;

- (NSDictionary *)allContacts;
- (NSDictionary *)addedContacts;
+ (NSString *)createNSStringFromContactDictionary:(NSDictionary *)contact;

@end
