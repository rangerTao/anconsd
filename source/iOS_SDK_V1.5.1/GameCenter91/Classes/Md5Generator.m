//
//  Md5Generator.m
//  GameCenter91
//
//  Created by Teng Yongxiang on 13-11-10.
//
//

#import "Md5Generator.h"
#import <CommonCrypto/CommonDigest.h>
#define DATA_LENGTH     1024 * 1024
@interface Md5Generator()
{
    CC_MD5_CTX _hashObject;
    unsigned char _md5[CC_MD5_DIGEST_LENGTH];
}
@property(nonatomic, assign) long long dataSize;
@property(nonatomic, retain) NSFileHandle *fileHandle;
@end
@implementation Md5Generator
- (void)dealloc
{
    self.fileHandle = nil;
    [super dealloc];
}
- (NSString *)generateMd5ForFile:(NSString *)filePath
{
    [self md5Init];
    [self prepareDataFromFile:filePath];
    long long offset = 0;
    while (offset < self.dataSize) {
        NSData *data = [self provideData:offset];
        [self md5Update:data];
        offset += [data length];
    }
    [self md5Final];
    return  [self md5Result];
    
}

- (void)prepareDataFromFile:(NSString *)filePath
{
    NSDictionary *fileAttributes = [[NSFileManager defaultManager] attributesOfItemAtPath:filePath error:nil];
    NSNumber *fileSize = [fileAttributes objectForKey:NSFileSize];
    self.dataSize = [fileSize longLongValue];
    self.fileHandle = [NSFileHandle fileHandleForReadingAtPath:filePath];
}
- (NSData *)provideData:(long long)offset
{
    [self.fileHandle seekToFileOffset:offset];
    return [self.fileHandle readDataOfLength:DATA_LENGTH];
}
- (void)md5Init
{
    CC_MD5_Init(&_hashObject);
}

- (void)md5Update:(NSData *)data
{
    CC_MD5_Update(&_hashObject, [data bytes], [data length]);
}

- (void)md5Final
{
    memset(_md5, 0, CC_MD5_DIGEST_LENGTH);
    CC_MD5_Final(_md5, &_hashObject);
}
- (NSString *)md5Result
{
    return [self getHexStringFromBytes:_md5 length:CC_MD5_DIGEST_LENGTH];
}
- (NSString *)getHexStringFromBytes:(const void *)bytes length:(int)length
{
	if (bytes == NULL || length == 0)
		return nil;
	
	NSMutableString *mutString = [[NSMutableString alloc] initWithCapacity:length * 2];
	for (int i = 0; i < length; i++)
	{
		[mutString appendFormat:@"%02x", (unsigned char)((const char *)bytes)[i]];
	}
	return mutString;
}
@end
