//
//  SoftIncrementUpdateCenter.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-11-7.
//
//

#import "SoftIncreUpdateModule.h"
#include "miniunz.h"
#include "unzip.h"
#include "xdelta3-internal.h"
#import "SoftItem.h"
#import <NdJSON/NDSBJSON.h>
#import "Md5Generator.h"


@implementation SoftIncreUpdateModule
+ (BOOL)currentDeviceSupportIncrementUpadate
{
    return YES;
}

+ (BOOL)isFilelistMatchedForSoft:(NSString *)identifier path:(NSString *)appPath
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    if (![fileManager fileExistsAtPath:appPath]) {
        NSLog(@"increment failed:there is none app");
        return NO;
    }
    NSString *zipFilelistPath = [self getFileListPackagePathForSoft:identifier];
    NSString *unZipFilelistPath = [self getUnzipFilelistPathForSoft:identifier];

    //解压filelist.ini文件

    int retZip = unZipMain((char *)[zipFilelistPath UTF8String], (char *)[unZipFilelistPath UTF8String], NULL);
    if (retZip != 0) {
        NSLog(@"unzip filelist.ini failure");
        return NO;
    }

    NSArray *array = [fileManager contentsOfDirectoryAtPath:unZipFilelistPath error:nil];
    
    //对比filelist.ini清单
    if (array == nil || [array count] ==0) {
        NSLog(@"incre update failed:none filelist");
        return NO;
    }
    
    NSString *fileListINIPath = unZipFilelistPath;
    for (NSString *file in array) {
        if ([file hasSuffix:@"ini"]) {
            fileListINIPath = [NSString stringWithFormat:@"%@/%@",fileListINIPath,file] ;
            break;
        }
    }
    
    if (![fileListINIPath hasSuffix:@"ini"]) {
        NSLog(@"incre update failed：no ini file");
        return NO;
    }
   
    
    //解压后的ini数据
    NSData *filelistData = [[NSData alloc] initWithContentsOfFile:fileListINIPath];
    if (filelistData == nil) {
        NSLog(@"increment update failed:none ini data");
        return  NO;
    }
    
    
    NSString *filelistJsonData = [[NSString alloc] initWithData:filelistData encoding:NSUTF8StringEncoding];
    NDSBJSON *json = [NDSBJSON new];// FIXME: XXX
    NSError *parseError = nil;
    NSDictionary *filelist = [json objectWithString:filelistJsonData error:&parseError];
    if (parseError) {
        NSLog(@"increment update failed:ini parse ini jsondata");
        return  NO;
    }
    [filelistJsonData release];
    
    NSArray *fileInfos = [filelist objectForKey:@"fileinfos"];
    //检查ini数据是否为空
    if (fileInfos == nil || [fileInfos count] == 0) {
        NSLog(@"increment update failed:fileInfo none data");
        return NO;
    }
    // 通过info文件获取可执行文件名称
    
    NSString *infoPath = [NSString stringWithFormat:@"%@/Info.plist",appPath];
    NSDictionary *infoPlist = [NSDictionary dictionaryWithContentsOfFile:infoPath];
    NSString *exeFile = [infoPlist objectForKey:@"CFBundleExecutable"];
    
    BOOL checkCodeSign = YES;    //检查codesign，比较一次后跳过，以后就不用比较.   这里不明白？？？!!
    for (NSDictionary *fileInfo in fileInfos) {
        NSString *ignorecompare = [fileInfo objectForKey:@"ignore"];
        NSString *filePath = [fileInfo objectForKey:@"name"];
        NSString *sizeInfo = [fileInfo objectForKey:@"size"];
        NSString *checksum = [fileInfo objectForKey:@"checksum"];
        
        // 可执行文件md5值校验
        if ([[filePath  lastPathComponent] isEqualToString:exeFile])
        {
            NSString *localExeFilePath = [NSString stringWithFormat:@"%@/%@",appPath,exeFile];
            Md5Generator *md5Generator = [[[Md5Generator alloc] init] autorelease];
            NSString *localExeFileMD5 = [md5Generator generateMd5ForFile:localExeFilePath];
            if([localExeFileMD5 compare:checksum options:NSCaseInsensitiveSearch] != NSOrderedSame)
            {
                NSLog(@"increment update failed: exe file md5 fail!");
                return NO;
            }else
            {
                continue;
            }
            
        }
        
        //是否ignore
        if ([ignorecompare isEqualToString:@"true"])
        {
            continue;
        }
        
        NSRange range;
        //路径是否包含.app
        if( (range = [filePath rangeOfString:@".app/"]).location == NSNotFound)
        {
            continue;
        }
        if (range.location+range.length == filePath.length)//跳过.app文件夹路径
        {
            continue;
        }
        //获取该文件名是否存在 ?? // MARK: THINK
        filePath = [filePath substringFromIndex:range.location+range.length];
        if (filePath == nil)
        {
            continue;
        }
        
        if (checkCodeSign)
        {
            if ([filePath rangeOfString:@"CodeResources"].location != NSNotFound)
            {
                checkCodeSign =NO;//跳过codesign
                continue;
            }
        }
        
        NSString *fileFullPath = [NSString stringWithFormat:@"%@/%@",appPath, filePath];
        BOOL directory = NO;
        //
        if (![fileManager fileExistsAtPath:fileFullPath isDirectory:&directory]) {
            NSLog(@"increment update failed:no increment file %@",fileFullPath);
            return NO;
        }
        // MARK: THINK
        if (directory == YES)//跳过目录比较大小
        {
            continue;
        }
        NSDictionary *attributes = [fileManager attributesOfItemAtPath:fileFullPath error:nil];
        NSNumber *localFileSize = [attributes objectForKey:NSFileSize];
        if (![[localFileSize stringValue] isEqualToString:sizeInfo]) {
            NSLog(@"increment update failed: size different file %@",fileFullPath);
            return NO;
        }
        
        
    }

    return YES;
    
}

+ (NSString *)xdeltaIncrementPackageForSoft:(NSString *)identifier path:(NSString *)appPath
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    if (![fileManager fileExistsAtPath:appPath]) {
        NSLog(@"increment failed: there is none app");
        return nil;
    }
    
    NSString *zipIncrementPackagePath = [self getIncrementPackagePathForSoft:identifier];
    NSString *incrementPackagePath = [self getUnzipIncrementPackagePahtForSoft:identifier];

    //解压增量包

    int retZip = unZipMain((char *)[zipIncrementPackagePath UTF8String], (char *)[incrementPackagePath UTF8String], NULL);
    if (retZip != 0) {
        NSLog(@"increment update failed:upZip increment failed ");
        return nil;
    }
    
    [self copyAppFilesForSoft:identifier path:appPath];
    BOOL xdeltaSuccess = [self xdeltaWithPath:identifier appPath:appPath];
    if (!xdeltaSuccess) {
        return nil;
    }
    return [self getIncreInstallPathForSoft:identifier path:appPath];
}

+ (void)deleteIncrementTmpFileForSoft:(NSString *)identifier
{
    NSFileManager *filemanager = [NSFileManager defaultManager];
    NSString *incrementUpdateDirectory = [self getIncrementUpadateDirectoryForSoft:identifier];
    [filemanager removeItemAtPath:incrementUpdateDirectory error:nil];
}

+ (void)copyAppFilesForSoft:(NSString *)identifier path:(NSString * )appPath
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *sourcePath = appPath;
    NSString *desPath = [self getAppTempPathForSoft:identifier path:appPath];

    [fileManager copyItemAtPath:sourcePath toPath:desPath error:nil];
}

+ (BOOL)xdeltaWithPath:(NSString *)identifier appPath:appPath
{
    NSLock *xdeltaLock = [[[NSLock alloc] init] autorelease];
    //获取需要删除到文件列表
    NSString  *zipFilePath = [self getIncrementPackagePathForSoft:identifier];
    NSString  *appTempPath = [self getAppTempPathForSoft:identifier path:appPath];// FIXME: xxx
    

    
    unzFile file = unzOpen([zipFilePath UTF8String]);

    unz_global_info fileInfo;
    unzGetGlobalInfo(file, &fileInfo);
    uLong sizeComment = fileInfo.size_comment + 1;
    char *comment = (char *) malloc(sizeComment);
    unzGetGlobalComment(file, comment, sizeComment);
    
    NSString *commentInfo = [[[NSString alloc]initWithUTF8String:comment] autorelease];
    free(comment);
    unzClose(file);
    NDSBJSON *json = [[NDSBJSON new] autorelease];
    NSError *parseError = nil;
    NSDictionary *delFilelist = [json objectWithString:commentInfo error:&parseError];
    NSArray *delFileInfos = [delFilelist objectForKey:@"fileinfos"];
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    //删除要删除的文件
    for (NSDictionary *delFileDic in delFileInfos)
    {
        NSString *delFile = [delFileDic objectForKey:@"name"];
        NSRange range = [delFile rangeOfString:@".app/"];
        if (range.location == NSNotFound ||(range.location + range.length) == [delFile length])//只处理.app包内的文件
        {
            continue;
        }
        delFile =[delFile substringFromIndex:range.location + range.length];
        delFile = [appTempPath stringByAppendingFormat:@"/%@", delFile];
        if( [fileManager fileExistsAtPath:delFile])
        {
            [fileManager removeItemAtPath:delFile error:nil];
        }
    }
    //获取增量包中.app目录地址
    NSString *sourPath = nil;
    NSString *incrementPackagePath = [self getUnzipIncrementPackagePahtForSoft:identifier];

    NSArray *filePaths = [fileManager subpathsOfDirectoryAtPath:incrementPackagePath error:nil];
    for (NSString *filePath in filePaths) {
        if ([filePath hasSuffix:@".app"]) {
            sourPath = [incrementPackagePath stringByAppendingFormat:@"/%@",filePath];// FIXME: xxx
        }
    }
    if (sourPath == nil) {
        NSLog(@"increment update failed: none .app in increment package ");
        return  NO;
    }
    NSArray *sourFiles = [fileManager subpathsOfDirectoryAtPath:sourPath error:nil];
    for (NSString *increFile in sourFiles) {
        NSString *tempFile = nil;//[appPath stringByAppendingFormat:@"/%@",increFile]; // FIXME: xxx
        NSString *sourceFile = [sourPath stringByAppendingFormat:@"/%@",increFile];
        NSString *desFile = [appTempPath stringByAppendingFormat:@"/%@",increFile];
//        oldFile = desFile;
        if ([increFile hasSuffix:@".xdt"]) {
            desFile = [desFile stringByDeletingPathExtension];
            tempFile = desFile;
            int retXdelta = [self bsxdeltaWithSrcFile:tempFile newFile:desFile xdelta:sourceFile withLock:xdeltaLock]; // FIXME: XXX
            if (retXdelta != 0) {
                NSLog(@"increment update failed: xdt failed");
                return NO;
            }
            continue;
        }
        BOOL isDirectory=NO;
        BOOL exisitFile = NO;
        exisitFile = [fileManager fileExistsAtPath:desFile isDirectory:&isDirectory];
        if (isDirectory)
        {
            continue;
        }
        // MARK: THINK
        if (exisitFile) {
            [fileManager removeItemAtPath:desFile error:nil];
//            desFile = [desFile stringByDeletingLastPathComponent];
        }
        [fileManager copyItemAtPath:sourceFile toPath:desFile error:nil];
    }
    return YES;
}

+(int)bsxdeltaWithSrcFile:(NSString *)src newFile:(NSString *)newFile xdelta:(NSString *)xdelta withLock:(NSLock *)xdeltaLock
{
    // NSLog(@"############# bsxdeltaWithSrcFile:%@ newFile:%@ xdelta:%@", src,newFile,xdelta);
//    self.XdeltaFilesNum++;
    char * src_char = (char *)[src UTF8String];
    char * newFile_char = (char *)[newFile UTF8String];
    char * xdelta_char = (char *)[xdelta UTF8String];
    char *argv[7] = {"xdelta3", "-f", "-d", "-s", src_char, xdelta_char, newFile_char};
    [xdeltaLock lock];
    int result = xd3_main_cmdline(7, argv);
    [xdeltaLock unlock];
    
    return result;
}

+ (NSString *)getAppTempPathForSoft:(NSString *)identifier path:(NSString *)appPath
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *appName = [[appPath lastPathComponent] stringByReplacingOccurrencesOfString:@".app" withString:@" "];
    NSString *incrementUpdateDirectory = [self getIncrementUpadateDirectoryForSoft:identifier];
    NSString *tempAppDirect = [incrementUpdateDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_app",appName]];
    NSString *payLoadDirect = [tempAppDirect stringByAppendingPathComponent:@"Payload"];
    [fileManager createDirectoryAtPath:payLoadDirect withIntermediateDirectories:YES attributes:nil error:nil];
    return [payLoadDirect stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.app",appName]];

}

+ (NSString *)getIncreInstallPathForSoft:(NSString *)identifier path:(NSString *)appPath
{
    NSString *path = [self getAppTempPathForSoft:identifier path:appPath];
    return [[path stringByDeletingLastPathComponent] stringByDeletingLastPathComponent];
}

+ (NSString *)getFileListPackagePathForSoft:(NSString *)identifier
{
    NSString *incrementUpdateDirectory = [self getIncrementUpadateDirectoryForSoft:identifier];
    NSString *fileListPackageDirectory = [incrementUpdateDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_fileListPackage",identifier]];
    NSString *fileListPackagePath = [fileListPackageDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_filelist.zip",identifier ]];
    return fileListPackagePath;
}

+ (NSString *)getUnzipFilelistPathForSoft:(NSString *)identifier
{
    NSString *incrementUpdateDirectory = [self getIncrementUpadateDirectoryForSoft:identifier];
    NSString *fileListPackageDirectory = [incrementUpdateDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_fileListPackage",identifier]];
    return fileListPackageDirectory;
}

+ (NSString *)getIncrementPackagePathForSoft:(NSString *)identifier
{
    NSString *incrementUpdateDirectory = [self getIncrementUpadateDirectoryForSoft:identifier];
    NSString *incrementPackageDirectory = [incrementUpdateDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_incrementPackage",identifier]];
    NSString *incrementPackagePath = [incrementPackageDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_increPackage.zip",identifier]];
    return incrementPackagePath;
}

+ (NSString *)getUnzipIncrementPackagePahtForSoft:(NSString *)identifier
{
    NSString *incrementUpdateDirectory = [self getIncrementUpadateDirectoryForSoft:identifier];
    NSString *incrementPackageDirectory = [incrementUpdateDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_incrementPackage",identifier]];
    return incrementPackageDirectory;
}

+ (NSString *)getIncrementUpadateDirectoryForSoft:(NSString *)identifier
{
    return [NSTemporaryDirectory() stringByAppendingPathComponent:identifier];
}
+ (NSString *)requiredFileListPackageNameForSoft:(NSString *)identifier //名字和地址都返回 ?// MARK: THINK
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *incrementUpdateDirectory = [self getIncrementUpadateDirectoryForSoft:identifier];
    NSString *fileListPackageDirectory = [incrementUpdateDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_fileListPackage",identifier]];
    [fileManager createDirectoryAtPath:fileListPackageDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    NSString *fileListPackageName = [fileListPackageDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_filelist.zip",identifier ]];
    return fileListPackageName;
}
+ (NSString *)requiredIncrementPackageNameForSoft:(NSString *)identifier
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *incrementUpdateDirectory = [self getIncrementUpadateDirectoryForSoft:identifier];
    NSString *incrementPackageDirectory = [incrementUpdateDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_incrementPackage",identifier]];
    [fileManager createDirectoryAtPath:incrementPackageDirectory withIntermediateDirectories:YES attributes:nil error:nil];
    NSString *incrementPackageName = [incrementPackageDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_increPackage.zip",identifier]];
    return incrementPackageName;
}
@end
