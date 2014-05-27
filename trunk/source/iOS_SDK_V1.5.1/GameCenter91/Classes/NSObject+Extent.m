//
//  NSObject+Extent.m
//  GameCenter91
//
//  Created by Kensou Sie on 12-9-10.
//  Copyright (c) 2012年 NetDragon WebSoft Inc. All rights reserved.
//

#import "NSObject+Extent.h"
#import <objc/runtime.h>

@implementation NSObject(Extent)
- (void)clearAllProperty
{
    //NSLog(@"clear %@", self);
    Class cl = [self class];
    Class clObj = [NSObject class];
    do
    {
        //NSLog(@"clear property of class %@", NSStringFromClass(cl));
        unsigned int count = 0;
        objc_property_t *propertyList = class_copyPropertyList(cl, &count);
        if (count > 0)
        {
            for (int i = 0; i < count; i++)
            {
                const char *propertyName = property_getName(propertyList[i]);
                int ch = toupper(propertyName[0]);
                NSString *method = [NSString stringWithFormat:@"set%c%s:", ch, &propertyName[1]];
                SEL sel = NSSelectorFromString(method);
                if ([self respondsToSelector:sel])
                {
                    //NSLog(@"%@ to nil", method);
                    [self performSelector:sel withObject:nil];
                }
            }
            
            free(propertyList);
        }
        cl = class_getSuperclass(cl);
    }while (cl != NULL && cl != clObj);
}
@end
