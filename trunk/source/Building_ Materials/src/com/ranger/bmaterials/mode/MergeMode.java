package com.ranger.bmaterials.mode;


public class MergeMode {

//	+ MergeTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," //0
//	+ MergeTable.COLUMN_PKG_NAME + " TEXT ," //app attribute 1
//	+ MergeTable.COLUMN_VERSION + " TEXT," 				  	//app attribute 4
//	+ MergeTable.COLUMN_VERSION_INT + " INTEGER," 			//app attribute 5
//	+ MergeTable.COLUMN_GAME_ID + " TEXT," 					  //app attribute 11
//	
//	+ MergeTable.COLUMN_DOWNLOAD_ID + " INTEGER NOT NULL UNIQUE," 		//file attribute 13
//	+ MergeTable.COLUMN_DOWNLOAD_URL + " TEXT UNIQUE NOT NULL," 				  	//file attribute 14
//	+ MergeTable.COLUMN_SAVE_DEST+ " TEXT ," 				  	//file attribute 14
//	+ MergeTable.COLUMN_FAILED_COUNT + " INTEGER DEFAULT 0," 				  	//file attribute 14
//	+ MergeTable.COLUMN_FAILED_REASON + " INTEGER DEFAULT 0," 				  	//file attribute 14
//	
	
	public long downloadId ;
	public String downloadUrl ;
	public String saveDest ;
	public int versionInt ;
	public String version ;
	public String gameId ;
	public String packageName ;
	public int  failedCount ;
	public int failedReason ;
	
	public int status ;
	
	public MergeMode() {
		super();
	}

	public MergeMode(long downloadId, String downloadUrl, String saveDest,
			int versionInt, String version, String gameId, String packageName,
			int failedCount, int failedReason,int status) {
		super();
		this.downloadId = downloadId;
		this.downloadUrl = downloadUrl;
		this.saveDest = saveDest;
		this.versionInt = versionInt;
		this.version = version;
		this.gameId = gameId;
		this.packageName = packageName;
		this.failedCount = failedCount;
		this.failedReason = failedReason;
		this.status = status ;
	}
	
	
	
}
