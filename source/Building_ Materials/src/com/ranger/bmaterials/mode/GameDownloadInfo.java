package com.ranger.bmaterials.mode;

import java.io.File;

public class GameDownloadInfo {
	public int game_status = -1;
	public static final int GAME_INSTALLED = 0;
	public static final int GAME_UNINSTALL = 1;
	public static final int GAME_UNDOWNLOAD = 2;
	public static final int GAME_DOWNLOADED = 3;
	public static final int GAME_DOWNLOADING = 4;
	public static final int GAME_DOWNLOADING_PAUSE = 5;
	public static final int GAME_DOWNLOADING_FAILED = 6;
	public static final int GAME_PENDING = 7;
	
	public long current_download_bytes;
	public long total_bytes;
	public int download_percent;
	
	public boolean isInit;
	public File apkFile;
	public boolean isIconLoading;
	
	public PackageMode download_status;
	public QueryInput qin;
}
