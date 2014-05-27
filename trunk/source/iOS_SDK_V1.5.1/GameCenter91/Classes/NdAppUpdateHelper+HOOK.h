//
//  NdAppUpdateHelper.h
//  GameCenter91
//
//  Created by kensou on 12-11-14.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NdAppUpdateHelper : NSObject

@end

@interface NdAppUpdateHelper(HOOK)
+ (void)hookForGameCenter;
@end

