//
//  IncreUpdateInfo.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-11-11.
//
//

#import "IncreUpdateInfo.h"

@implementation IncreUpdateInfo
- (void)dealloc
{
    [self clearAllProperty];
    [super dealloc];
}
+ (IncreUpdateInfo *)increUpdateInfoFromDictionary:(NSDictionary *)dic
{
    NSString *incrementFileUrl = [dic objectForKey:@"IncrementalFileUrl"];
    if (!incrementFileUrl) {
        return nil;
    }
    IncreUpdateInfo *info = [[IncreUpdateInfo new] autorelease];
    info.updateUrl = [dic objectForKey:@"UpdateUrl"];
    info.increPackageUrl = incrementFileUrl;
    info.increFileSize = [[dic objectForKey:@"IncrementalFileSize"] longLongValue];
    info.filelistPackageUrl = [dic objectForKey:@"LowFileListUrl"];
    info.increInstallPackagePath = nil;
    info.isFilelistPackage = NO;
    info.isIncrePackage = NO;
    info.smartUpdateFailed= NO;
    
    return info;
}
@end
