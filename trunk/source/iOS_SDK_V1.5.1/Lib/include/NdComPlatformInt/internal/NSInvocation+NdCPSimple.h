//
//  NSInvocation+NdCPSimple.h
//  NdComPlatform_SNS
//
//  Created by Sie Kensou on 12-5-2.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

#pragma mark -

@interface NSInvocation(NdCPSimple)

+ (NSInvocation*)NdCPInvocationWithTarget:(id)target selector:(SEL)sel;
+ (NSInvocation*)NdCPErrorCodeInvocationWithTarget:(id)target selector:(SEL)sel errorIndex:(int)idx error:(NSError *)error;
@end
