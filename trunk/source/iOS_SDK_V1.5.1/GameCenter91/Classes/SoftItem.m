//
//  SoftItem.m
//  GameCenter91
//
//  Created by Sie Kensou on 12-8-21.
//  Copyright 2012 NetDragon WebSoft Inc. All rights reserved.
//

#import "SoftItem.h"
#import "CommUtility.h"
#import <Log/NDLogger.h>
#import "SoftIncreUpdateModule.h"

#define DOWNLOAD_PATH @"Download"
#define DEFAULT_ICON_NAME   @"defaultAppIcon"
#define DEFAULT_ICON_FILE   DEFAULT_ICON_NAME@".png"

@interface SoftItem()
@property (nonatomic, retain) NSString *defaultIconPath;

- (NSString *)absoluteSavePath;
@end

@implementation SoftItem
@synthesize f_id, identifier, localVersion, localShortVersion, version, shortVersion;

@synthesize iconPath, softName;
@synthesize defaultIconPath;

@synthesize timeStamp, savePath, fileName, url, downloadStatus, downloadedLen, totalLen; 
@synthesize installStatus;

+ (SoftItem *)itemWithAppIdentifier:(NSString *)aIdentifier softName:(NSString *)softName
{
    if (aIdentifier == nil)
        return nil;
    SoftItem *item = [[SoftItem new] autorelease];
    item.identifier = aIdentifier;
    item.softName = softName;
    return item;
}

- (NSString *)primaryKey
{
    return self.identifier;
}

- (id) init
{
    self = [super init];
    if (self != nil) {
        self.localVersion = nil;
        self.localShortVersion = nil;
        self.version = nil;
        self.shortVersion = nil;
        self.defaultIconPath = [[NSBundle mainBundle] pathForResource:DEFAULT_ICON_NAME ofType:@"png"];
        self.iconPath = self.defaultIconPath;
        self.increUpateInfo = nil;
        self.increInstallPackagePath = nil;
        self.downloadStatus = KS_DEFAULT_STATE;
        self.totalLen = 0;
        self.downloadedLen = 0;
        self.installStatus = INSTALL_DEFAULT_STATE;
        self.isAutoContinueDownload = YES;
    }
    return self;
}

- (BOOL)hasIcon
{
    return ![self.iconPath isEqualToString:self.defaultIconPath];
}

//- (BOOL)isEqual:(SoftItem *)object
//{
//    return [self.identifier isEqual:object.identifier];
//}

- (void) dealloc
{
    [self clearAllProperty];
    [super dealloc];
}

- (UIImage *)defaultIcon
{
    return [UIImage imageNamed:DEFAULT_ICON_FILE];
}

- (NSString *)absoluteSavePath
{
    return [[CommUtility getDocumentPath] stringByAppendingPathComponent:self.savePath];
}

- (void)generateSaveName
{
    if (self.savePath == nil)
    {
        self.savePath = DOWNLOAD_PATH;
        if ([[NSFileManager defaultManager] fileExistsAtPath:[self absoluteSavePath]] == NO)
        {
            NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithInt:0777], NSFilePosixPermissions, nil];            
            NSError *error = nil;
            [[NSFileManager defaultManager] createDirectoryAtPath:[self absoluteSavePath] withIntermediateDirectories:YES attributes:dict error:&error];
            if (error)
            {
                NSLog(@"can not create %@", self.savePath);
            }
        }
    }
    
    if (self.fileName == nil)
    {
        self.fileName = [NSString stringWithFormat:@"%@_v%@.ipa", self.identifier, self.version];
    }
}

- (NSString *)absoluteFilePath
{
    if(self.fileName == nil)
        return nil;
    
    NSString *path = [[self absoluteSavePath] stringByAppendingPathComponent:self.fileName];
    if (path == nil)
        path = @"";
    return path;
}

- (NSURL *)iconUrl
{
    NSURL *iconUrl = nil;
    if ([self.iconPath hasPrefix:@"/"])
    { 
        iconUrl = [NSURL fileURLWithPath:self.iconPath];
    }
    else
    {
        iconUrl = [NSURL URLWithString:self.iconPath];
    }
    return iconUrl;
}

- (NSURL *)placeholderIconUrl
{
    return [NSURL fileURLWithPath:self.defaultIconPath];
}

- (BOOL)fileExist
{
    if (self.increUpateInfo != nil && !self.increUpateInfo.smartUpdateFailed) {
        return [[NSFileManager defaultManager] fileExistsAtPath:[SoftIncreUpdateModule getIncrementPackagePathForSoft:self.identifier]];
    }
    return [[NSFileManager defaultManager] fileExistsAtPath:[self absoluteFilePath]];
}

- (float)downloadPercent
{
    if (self.downloadedLen == 0 || self.totalLen <= 0)
        return 0;
    return self.downloadedLen * 1.0 / self.totalLen;
}

- (long)localFileLength
{
    long len = 0;
    NSString *path = [self absoluteFilePath];
    if (self.increUpateInfo != nil && !self.increUpateInfo.smartUpdateFailed) {
        path = [SoftIncreUpdateModule getIncrementPackagePathForSoft:self.identifier];
    }
    NSError *error = nil;
    NSDictionary *dict = [[NSFileManager defaultManager] attributesOfItemAtPath:path error:&error];
    if (error)
    {
        //NDLOG(@"fail on update file path %@", error);
        len = 0;
    }
    else
    {
        len = [dict fileSize];
    }
    return len;
}
@end
