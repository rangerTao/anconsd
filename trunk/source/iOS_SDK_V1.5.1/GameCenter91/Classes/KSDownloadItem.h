//
//  KSDownloadItem.h
//  GameCenter91
//
//  Created by kensou on 12-10-6.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

typedef enum _KS_DOWNLOAD_STATUS
{
    KS_DEFAULT_STATE = 0,
    KS_DOWNLOADING = 1,
    KS_QUEUING = 1 << 2,
    KS_STOPPED = 1 << 3,
    KS_INITIALIZING = 1 << 4,
    KS_FINISHED = 1 << 5,
}KS_DOWNLOAD_STATUS;

typedef enum _KS_INSTALL_STATUS
{
    INSTALL_DEFAULT_STATE = 0,
    INSTALL_INSTALLING = 1,
    INSTALL_QUEUING = 1 << 2,
    INSTALL_STOPPED = 1 << 3,
    INSTALL_INITIALIZING = 1 << 4,
    INSTALL_FINISHED = 1 << 5,
}KS_INSTALL_STATUS;

@protocol KSDownloadItem <NSObject>
@required
@property (nonatomic, readonly) NSString *primaryKey;
@property (nonatomic, assign) long timeStamp;
@property (nonatomic, retain) NSString *savePath;
@property (nonatomic, retain) NSString *fileName;
@property (nonatomic, retain) NSString *url;
@property (nonatomic, assign) int downloadStatus;
@property (nonatomic, assign) long downloadedLen;
@property (nonatomic, assign) long totalLen;
@property (nonatomic, assign) int installStatus;

@end