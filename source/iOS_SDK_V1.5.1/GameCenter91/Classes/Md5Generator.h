//
//  Md5Generator.h
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-11-10.
//
//

#import <Foundation/Foundation.h>

@interface Md5Generator : NSObject
- (NSString *)generateMd5ForFile:(NSString *)filePath;
@end
