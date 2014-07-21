package com.ranger.bmaterials.mode;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;


public  class PackageMode implements  Parcelable,Serializable {
	////////////////////////////////////////////////////////////////////////////////////
	/**1、未下载并且没有安装，因为如果已经安装则状态为INSTALLED或者UPDATABLE或者UPDATABLE_DIFF；<BR/>
	 * 2、卸载apk并且删除下载记录
	 */
	public static final int UNDOWNLOAD = 0;
	public static final int DOWNLOAD_PENDING = 1 << 2;
	public static final int DOWNLOAD_RUNNING = 1 << 3;
	public static final int DOWNLOAD_PAUSED = 1 << 4;
	public static final int DOWNLOAD_FAILED = 1 << 5;
	/** 下载文件成功
	 * <li>1、如果是增量更新，提示DOWNLOADED后，后续会提示MERGING、MERGE_FAILED。如果合并成功，提示MERGED<br/>
	 * <li>2、如果普通更新或者普通下载，DOWNLOADED
	 */
	public static final int DOWNLOADED = 1 << 6;
	/**增量更新合并中*/
	public static final int MERGING = 1 << 7;
	/**增量更新合并失败*/
	public static final int MERGE_FAILED = 1 << 8;
	/**增量更新合并成功*/
	public static final int MERGED = 1 << 9;
	/**静默安装中*/
	public static final int INSTALLING = 1 << 10;
	/**静默安装失败*/
	public static final int INSTALL_FAILED = 1 << 11;
	/**安装成功或者已经安装*/
	public static final int INSTALLED = 1 << 12;
	/**可普通更新*/
	public static final int UPDATABLE = 1 << 13;
	/**可增量更新*/
	public static final int UPDATABLE_DIFF = 1 << 14;
	
	/**下载完成后如果apk签名不同提示的状态<br/>
	 * <li>注意：不再使用，下载完成后不再检查apk的签名是否与已安装的apk签名一致，
	 * 因为查询apk状态的时候没有检查签名（性能原因），所以可能造成状态不同。所以需要客户断在前台主动点击安装时检查签名 */
	@Deprecated
	public static final int DOWNLOADED_DIFFERENT_SIGN = 1 << 15;
	/**合并完成后如果apk签名不同提示的状态
	 * <li>注意：不再使用，下载完成后不再检查apk的签名是否与已安装的apk签名一致，
	 * 因为查询apk状态的时候没有检查签名（性能原因），所以可能造成状态不同。所以需要客户断在前台主动点击安装时检查签名 */
	@Deprecated 
	public static final int MERGED_DIFFERENT_SIGN = 1 << 16;
	
	/**暂时只有首页需要*/
	public static final int RESET_STATUS = 1 << 17;
	
	/**更新或者增量更新检查下载的apk的签名*/
	public static final int CHECKING = 1 << 18;
	/**更新或者增量更新检查下载的apk的签名完成*/
	public static final int CHECKING_FINISHED = 1 << 19;
	
	
	/**默认状态*/
	public static final int DEFAULT_STATUS = UNDOWNLOAD;
	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Http错误，包括 ERROR_UNHANDLED_HTTP_CODE ERROR_HTTP_DATA_ERROR
	 * ERROR_TOO_MANY_REDIRECTS PAUSED_WAITING_TO_RETRY 等
	 */
	public static final int ERROR_HTTP_ERROR = 1000;
	/**
	 * 文件错误，比如文件不可创建或者写数据出错等等
	 */
	public static final int ERROR_FILE_ERROR = 1001; //
	/**
	 * SDCard空间不足
	 */
	public static final int ERROR_INSUFFICIENT_SPACE = 1002; //
	/**
	 * 没有SDCard
	 */
	public static final int ERROR_DEVICE_NOT_FOUND = 1003; //

	/**
	 * 未知错误
	 */
	public static final int ERROR_UNKNOWN = 1004;
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 下载参数错误:
	 */
	public static final int ERROR_PARAM_NO_URL = 2000;
	
	/**
	 * 下载参数错误:
	 */
	public static final int ERROR_PARAM_NO_GAME_ID = 2001;
	
	/**
	 * 下载参数错误
	 */
	public static final int ERROR_PARAM_NO_PACKAGE_NAME = 2002;
	
	/**
	 * 下载参数错误
	 */
	public static final int ERROR_PARAM_NO_VERSION = 2003;
	
	public static final int ERROR_PARAM_ERROR = 2004;
	
	/**
	 * 插入数据库出现错误(下载等进行数据库操作出现错误)，此时如果status为DOWNLOAD_FAILED，可能downloadId为null或者为-1.
	 */
	public static final int ERROR_DATABASE_ERROR = 2005;
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 等待重试，比如SocketException: No route to
	 * host之类，可以不用考虑这个暂停的原因，因为Service在一定的时间之后回重试，
	 * 超过失败最大次数（默认是3次，可以设置），会报错（ERROR_*）
	 * 
	 */
	public static final int PAUSED_WAITING_TO_RETRY = 800; //
	/**
	 * 移动网络或者wifi中断
	 */
	public static final int PAUSED_WAITING_FOR_NETWORK = 801; //
	/**
	 * 文件大小超过移动网络限制，需要在wifi下才能下载
	 */
	public static final int PAUSED_QUEUED_FOR_WIFI = 802; //
	/**
	 * 主动暂停
	 */
	public static final int PAUSED_BY_APP = 803; //
	/**
	 * 未知原因
	 */
	public static final int PAUSED_UNKNOWN = 804;//
	
	/* 此游戏已经安装 */
	public static final int INSTALL_FAILED_ALREADY_EXISTS = com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_ALREADY_EXISTS;
	/* 此游戏已经安装 */
	public static final int INSTALL_FAILED_DUPLICATE_PACKAGE = com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_DUPLICATE_PACKAGE;
	/* 您的手机空间不足 */
	public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE = com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_INSUFFICIENT_STORAGE;
	/* 手机版本较 */
	public static final int INSTALL_FAILED_OLDER_SDK = com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_OLDER_SDK;
	/* 无法安装此游戏 */
	public static final int INSTALL_FAILED_MISSING_FEATURE = com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_MISSING_FEATURE;
	/* 权限问题 */
	public static final int INSTALL_FAILED_PERMISSION = com.ranger.bmaterials.tools.install.PackageUtils.INSTALL_FAILED_PERMISSION;
	/* 其他原因*/
	public static final int INSTALL_FAILED_OTHER =  -999;
	
	
	public static final int MERGE_ERROR_INSUFFICIENT_SPACE =  3000;
	public static final int MERGE_ERROR_OTHER =  3002;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5585484808654874903L;

	
	/**
	 * game id
	 */
	public  String gameId;
	/**更新状态或者增量更新状态是更新地址（不是查询地址）；下载状态是下载地址；其他情况是调用着传递的查询地址*/
	public String downloadUrl;
	public  String packageName;
//	/**更新状态更新的版本，下载状态则是下载的apk版本*/
	public String version; 		
//	/**更新状态是更新的版本，下载状态则是下载的apk版本	*/
	public int versionCode; 
	
	public String localVersion; 		
	public int localVersionCode; 
	
	
	public long downloadId;
	/**下载文件保存路径*/
	public String downloadDest ;
	
	public String title ;
	//apkSign和apkFileMd5非常消耗性能
	/*下载apk的签名的md5，下载成功以及合并成功之后才有用*/
	//public String apkSign ;
	/*下载文件的md5，暂时没有用到*/
	//public String apkFileMd5 ;   
	
	/*是否为增量更新下载,下载时才有用*/
	public boolean isDiffDownload; 
	
	
	/**
	 * <pre>
	 *  package的状态。参考
	 * public static final int UNDOWNLOAD
	 * public static final int DOWNLOAD_PENDING
	 * public static final int DOWNLOAD_RUNNING
	 * public static final int DOWNLOAD_PAUSED
	 * public static final int DOWNLOAD_FAILED
	 * public static final int DOWNLOAD_SUCCESSFUL
	 * public static final int MERGING 
	 * public static final int MERGE_FAILED
	 * public static final int MERGE_SUCCESSFUL
	 * public static final int INSTALLING 
	 * public static final int INSTALL_FAILED
	 * public static final int INSTALL_SUCCESSFUL
	 * public static final int UPDATABLE
	 * public static final int UPDATABLE_DIFF
	 * public static final int INSTALL_DIFFERENT_SIGN
	 * <pre>
	 */
	public int status ;
	
	/**
	 * <pre>
	 * 下载暂停、下载失败、合并失败以及安装失败的原因。参考
	 * ERROR_*
	 * PAUSED_*
	 * MERGE_ERROR_*
	 * INSTALL_FAILED_*
	 * </pre>
	 */
	public Integer reason ;
	/**当前下载量*/
	public long currentSize = 0;
	/**总文件大小*/
	public long totalSize = -1;
	/**增量更新和普通更新的时候有用，完整的apk大小*/
	public long totalApkSize ;
	/**增量更新的时候有用,补丁大小*/
	public long pacthSize ;

	/**增量更新合并过程失败次数*/
	public int mergeFailedCount ;
	
	public PackageMode() {
		super();
	}

	public PackageMode(String gameId, String downloadUrl,
			String packageName, String version, int versionCode,
			long downloadId, String dest, String title, int status,
			Integer reason, long currentSize, long totalSize,
			/*String sign,String fileMd5,*/boolean isDiffDownload) {
		super();
		this.gameId = gameId;				//1
		this.downloadUrl = downloadUrl;		//2
		this.packageName = packageName;		//3
		this.version = version;				//4
		this.versionCode = versionCode;		//5
		this.downloadId = downloadId;		//6
		this.downloadDest = dest;			//7
		this.title = title;					//8
		this.status = status;				//9
		this.reason = reason;				//10
		this.currentSize = currentSize;		//11
		this.totalSize = totalSize;			//12
		//this.apkSign = sign ;
		//this.apkFileMd5 = fileMd5 ;
		this.isDiffDownload = isDiffDownload ;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(gameId);
		dest.writeString(downloadUrl);
		dest.writeString(PackageMode.this.downloadDest);
		dest.writeString(packageName);
		dest.writeString(version);
		dest.writeInt(versionCode);
		dest.writeLong(downloadId);
		dest.writeInt(status);
		dest.writeInt(reason);
		
		
		dest.writeLong(currentSize);
		dest.writeLong(totalSize);
		
		//dest.writeString(apkSign);
		//dest.writeString(apkFileMd5);
		dest.writeInt(isDiffDownload?1:0);
		dest.writeLong(totalApkSize);
		dest.writeLong(pacthSize);
		
		dest.writeString(localVersion);
		dest.writeInt(localVersionCode);
		dest.writeInt(mergeFailedCount);
	}

	public static final Parcelable.Creator<PackageMode> CREATOR = new Parcelable.Creator<PackageMode>() {

		@Override
		public PackageMode createFromParcel(Parcel source) {
			PackageMode mode = new PackageMode();
			mode.title = source.readString();
			mode.gameId = source.readString();
			mode.downloadUrl = source.readString();
			mode.downloadDest = source.readString();
			mode.packageName = source.readString();
			mode.version = source.readString();
			mode.versionCode = source.readInt();
			mode.downloadId = source.readLong();
			mode.status = source.readInt();
			mode.reason = source.readInt();
			
			mode.currentSize = source.readLong();
			mode.totalSize = source.readLong();
			
			//mode.apkSign = source.readString();
			//mode.apkFileMd5 = source.readString();
			mode.isDiffDownload = (source.readInt()==1);
			
			mode.totalApkSize = source.readLong();
			mode.pacthSize = source.readLong();
			
			mode.localVersion = source.readString();
			mode.localVersionCode = source.readInt();
			mode.mergeFailedCount = source.readInt();
			
			return mode;
		}
		

		@Override
		public PackageMode[] newArray(int size) {
			return new PackageMode[size];
		}

	};

	@Override
	public String toString() {
		return "PackageMode [gameId=" + gameId + ", downloadUrl=" + downloadUrl
				+ ", packageName=" + packageName + ", version=" + version
				+ ", versionCode=" + versionCode + ", localVersion="
				+ localVersion + ", localVersionCode=" + localVersionCode
				+ ", downloadId=" + downloadId + ", downloadDest="
				+ downloadDest + ", title=" + title + ", isDiffDownload="
				+ isDiffDownload + ", status=" + status + ", reason=" + reason
				+ ", currentSize=" + currentSize + ", totalSize=" + totalSize
				+ ", totalApkSize=" + totalApkSize + ", pacthSize=" + pacthSize
				+ "]";
	}

	
	public static String getStatusString(int status){
		
		String r = "null" ;
		switch (status) {
			case UNDOWNLOAD:	
				r = "UNDOWNLOAD";
				break;
			case DOWNLOAD_PENDING:
				r = "DOWNLOAD_PENDING";
				break;
			case DOWNLOAD_RUNNING:
				r = "DOWNLOAD_RUNNING";
				break;
			case DOWNLOAD_PAUSED:
				r = "DOWNLOAD_PAUSED";
				break;
			case DOWNLOAD_FAILED:
				r = "DOWNLOAD_FAILED";
				break;
			case DOWNLOADED:
				r = "DOWNLOADED";
				break;
			case MERGING:
				r = "MERGING";
				break;
			case MERGE_FAILED:
				r = "MERGE_FAILED";
				break;
			case MERGED:
				r = "MERGED";
				break;
			case INSTALLING:
				r = "INSTALLING";
				break;
			case INSTALL_FAILED:
				r = "INSTALL_FAILED";
				break;
			case INSTALLED:
				r = "INSTALLED";
				break;
			case UPDATABLE:
				r = "UPDATABLE";
				break;
			case UPDATABLE_DIFF:
				r = "UPDATABLE_DIFF";
				break;
			case DOWNLOADED_DIFFERENT_SIGN:
				r = "DOWNLOADED_DIFFERENT_SIGN";
				break;
			case MERGED_DIFFERENT_SIGN:
				r = "DOWNLOADED_DIFFERENT_SIGN";
				break;
			case CHECKING:
				r = "CHECKING";
				break;
			case CHECKING_FINISHED:
				r = "CHECKING_FINISHED";
				break;
			default:
				break;
		}
		return r ;
	}

}