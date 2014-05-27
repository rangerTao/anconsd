//
//  NdCPFileDownloader.h
//  NdComPlatform
//
//  Created by chenjianshe on 10-10-23.
//  Copyright 2010 NetDragon WebSoft Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@class NDNetHttpTransferClient;

enum NdCPFileDownloaderError
{
	urlError = -1001,		/**< 传入URl参数为无效或空值*/
	fileNameError = -1002,	/**< 传入filename参数为无效*/
	directoryError = -1003	/**< 传入directory参数为无效或空值*/
};
typedef enum NdCPFileDownloaderError NdCPFileDownloaderError;

@interface NdCPFileDownloader : NSObject {

	id theDelegate;
	NSMutableDictionary *dictionary;
	NDNetHttpTransferClient *transferClient;
	NSString *downloadFileName;
	BOOL isAutoRename;
	BOOL isShowName;
}
@property (nonatomic, retain, readonly) NSString *downloadFileName;
@property (nonatomic, assign) BOOL isAutoRename;	/**<是否自动重命名，默认YES*/
@property (nonatomic, assign) BOOL isShowName;		/**<是否显示下载的文件名在alertview中，默认YES*/
/**
 *@brief 初始化对象 
 *@param id delegate：回调对象
 *@return 返回初始化对象
 */
- (id)initWithDelegate:(id)delegate;

/**
 *@brief 根据传入的url，filename，directory，
 *@param NSString* URL:下载文件的地址
 *@param NSString* fileName:下载文件的名称,包含文件后缀名
 *@param NSString* directory:文件存放的路径，路径必须存在并且是目录，否则不会下载。
 *@param BOOL overWrite:if NO, the fileName will be truncated to zero if the fileName has been existed;
						if YES, download will be started from the size of fileName
 *@return 返回错误码,0表示成功。
 *@see enum NdCPFileDownloaderError
 */
- (NdCPFileDownloaderError)downloadFileWithURL:(NSString*)URL fileName:(NSString*)fileName saveToDirectory:(NSString*)directory allowOverWrite:(BOOL)overWrite;

/**
 *@brief 取消下载
 */
- (void)cancelDownload;

@end

@protocol NdCPFileDownloaderDelegate

/**
 *@brief download did cancel
 *@param NdCPFileDownloader* fileDownloader:the filedownloader
 */
- (void)onDownloadDidCancel:(NdCPFileDownloader*)fileDownloader;

/**
 *@brief download did Finish
 *@param NdCPFileDownloader* fileDownloader:the filedownloader
 */
- (void)onDownloadDidFinish:(NdCPFileDownloader*)fileDownloader;

/**
 *@brief download progress
 *@param NdCPFileDownloader* fileDownloader:the filedownloader
 *@param float progress:the progress of the download
 */
- (void)onDownload:(NdCPFileDownloader*)fileDownloader downloadProgress:(float)progress;

/**
 *@brief download fail with error
 *@param NdCPFileDownloader* fileDownloader:the filedownloader
 *@param nSError* error:the error occured
 */
- (void)onDownload:(NdCPFileDownloader*)fileDownloader didFailWithError:(NSError*)error;

@end
