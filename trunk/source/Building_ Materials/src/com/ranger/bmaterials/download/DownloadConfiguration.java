package com.ranger.bmaterials.download;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import com.ranger.bmaterials.net.HttpImpl;
import com.ranger.bmaterials.tools.MyLogger;

import android.content.Context;
import android.os.Environment;
/**
 * 下载的配置
 * @author wangliang
 *
 */
public class DownloadConfiguration {
	
	private static  DownloadConfiguration INSTANCE ;
	private String destinationFolder = null ;
	private boolean mobileNetworkAllowed = false ;
	private boolean roamingAllowed = true ;
	
	private Context context ;
	private DownloadConfiguration(Context context){
		this.context = context ;
		//internalInit();
	}
	public MyLogger mLogger = MyLogger.getLogger(HttpImpl.class.getName());
	
	public synchronized static DownloadConfiguration getInstance(Context context){
		if(INSTANCE == null){
			INSTANCE = new DownloadConfiguration(context);
		}
		return INSTANCE ;
	}
	
	private void internalInit(){
		File root = Environment.getExternalStorageDirectory();
		File destination = new File(root.getAbsolutePath(),context.getPackageName()+"/downloads/");
		if(!destination.exists()){
			destination.mkdirs();
		}
		setDefaultDestinationFolder(destination.getAbsolutePath());
	}
	
	/**
	 * 初始化方法
	 */
	public void init(){
		internalInit();
	}
	
	
	
	/* private String getDownloadIdFromUri(final Uri uri) {
	        return uri.getPathSegments().get(1);
	    }*/
    /*public void registerDownloadServiceCallback(DownloadServiceCallback callback){
    	
    }
    public void unregisterDownloadServiceCallback(DownloadServiceCallback callback){
    	
    }*/
    
    /**
     * 设置默认下载文件存放位置
     * @param path
     */
    public void setDefaultDestinationFolder(String path){
    	this.destinationFolder = path ;
    }
    /**
     * 
     * @return
     */
    public String getDefaultDestinationFolder(){
    	return this.destinationFolder ;
    }
    
    /**
     * 是否允许在移动网络环境(2g/3g)下下载
     * @param mobileNetworkAllowed
     */
    public void setMobileNetworkAllowed(boolean mobileNetworkAllowed){
    	this.mobileNetworkAllowed = mobileNetworkAllowed ;
    }
    
    public void setMobileNetworkAllowed(boolean mobileNetworkAllowed,boolean doAction){
    	this.mobileNetworkAllowed = mobileNetworkAllowed ;
    	if(doAction){
    		new Thread(){
    			public void run() {
    				DownloadUtil.resetAllowedNetworkType(context);
    			};
    		}.start();
    	}
    }
    /**
     * 是否允许在移动网络环境(2g/3g)下下载
     * @return
     */
    public boolean isMobileNetworkAllowed(){
    	return this.mobileNetworkAllowed ;
    }
    /**
     * 是否允许漫游下载
     * @param roamingAllowed
     */
    public void setRoamingAllowed(boolean roamingAllowed){
    	this.roamingAllowed = roamingAllowed ;
    }
    public boolean isRoamingAllowed(){
    	return this.roamingAllowed ;
    }
    /**
     * 最大同时下载任务个数
     * @param max
     */
    public void setMaxTaskNumber(int max){
    	if(max >= 1){
//    		Constants.MAX_THREAD = max-1 ;
    		Constants.MAX_THREAD = 2;
    		
    	}
    	
    }
    /**
     * 下载失败重试次数
     */
    public void setMaxRetryCount(int max){
    	if(max >= 1){
    		Constants.MAX_RETRIES = max ;
    	}
    	
    }
    /**
     * 下载对象
     * @author wangliang
     *
     */
    public static class DownloadItemOutput{
    	/**
    	 * 下载条目的状态
    	 * @author wangliang
    	 *
    	 */
    	public static enum DownloadStatus{
    		/**
    		 * 即将开始状态
    		 */
    		STATUS_PENDING,
    		/**
    		 * 正在下载
    		 */
    		STATUS_RUNNING,
    		/**
    		 * 暂停
    		 */
    		STATUS_PAUSED,
    		
    		/**
    		 * 下载成功
    		 */
    		STATUS_SUCCESSFUL,
    		/**
    		 * 下载失败
    		 */
    		STATUS_FAILED;
    		
    		
    		private int code ;
    		private  DownloadStatus(){
    			try {
    				String name = name();
        			Field field = DownloadManager.class.getDeclaredField(name);
        			field.setAccessible(true);
        			Integer  value = (Integer) field.get(null);
        			this.code = value ;
				} catch (Exception e) {
				}
    		}
    		public int getStatusCode(){
    			return code ;
    		}
    		static DownloadStatus getStatus(int original){
    			DownloadStatus ret = null ;
    			DownloadStatus[] values = values();
    			for (int i = 0; i < values.length; i++) {
    				if(original == values[i].code){
    					ret = values[i];
    				}
    			}
    			return ret ;
    		}
    		
    		
    	}

    	/**
    	 * 下载失败和暂停的原因
    	 * @author wangliang
    	 *
    	 */
		public static enum DownloadReason {
			
			
			
			/**Http错误，包括
			 * ERROR_UNHANDLED_HTTP_CODE
			 * ERROR_HTTP_DATA_ERROR
			 * ERROR_TOO_MANY_REDIRECTS
			 * PAUSED_WAITING_TO_RETRY
			 * 等
			 */
			ERROR_HTTP_ERROR, //
			/**
			 * 404或者断点下载的响应吗错误，不能恢复
			 */
			ERROR_HTTP_CANNOT_RUSUME,
			/**
			 * 未知的网络错误
			 */
			ERROR_HTTP_UNKNOWN,
			
			/**
			 * 文件错误，比如文件不可创建或者写数据出错等等
			 */
			ERROR_FILE_ERROR, //
			/**
			 * SDCard空间不足
			 */
			ERROR_INSUFFICIENT_SPACE, //
			/**
			 * 没有SDCard
			 */
			ERROR_DEVICE_NOT_FOUND, //
			
			/**
			 * 下载的文件已经存在
			 */
			ERROR_FILE_ALREADY_EXISTS, //
			/**
			 * 未知错误
			 */
			ERROR_UNKNOWN,
			
			CANCEL_UPDATE,
			
			
			/**
			 * 等待重试，比如SocketException: No route to host之类，可以不用考虑这个暂停的原因，因为Service在一定的时间之后回重试，
			 * 超过失败最大次数（默认是3次，可以设置），会报错（ERROR_*）
			 * 
			 */
			PAUSED_WAITING_TO_RETRY, 	//
			/**
			 * 移动网络或者wifi中断
			 */
			PAUSED_WAITING_FOR_NETWORK, //
			/**
			 * 文件大小超过移动网络限制，需要在wifi下才能下载
			 */
			PAUSED_QUEUED_FOR_WIFI, 	//
			/**
			 * 主动暂停
			 */
			PAUSED_BY_APP, 				//
			/**
			 * 未知原因
			 */
			PAUSED_UNKNOWN;//
			
			
			private int code ;
    		private  DownloadReason(){
    			try {
    				String name = name();
        			Field field = DownloadManager.class.getDeclaredField(name);
        			field.setAccessible(true);
        			Integer  value = (Integer) field.get(null);
        			this.code = value ;
				} catch (Exception e) {
				}
    		}
    		int getStatusCode(){
    			return code ;
    		}
			static DownloadReason getReason(int original){
				DownloadReason ret = null ;
    			DownloadReason[] values = values();
    			for (int i = 0; i < values.length; i++) {
    				if(original == values[i].code){
    					ret = values[i];
    				}
    			}
    			return ret ;
    		}
			
			int getReasonCode(){
    			return code ;
    		}
		}
		///////////////////////////////////////////////////////////////
		//
		///////////////////////////////////////////////////////////////
		/**
		 * 
		 */
		long downloadId ;
		/**
		 * 下载url
		 */
    	String url ;
    	/**
    	 * 保存位置的uri
    	 */
    	String dest ;
    	
		String title  ;
		String mimeType ;
		long totalBytes ;
		long currentBytes ;
		DownloadStatus status ;;
		DownloadReason reason ;
		String appData ; //额外信息
		
		
		/**
		 * 下载文件最后修改时间（文件开始下载以及下载成功或失败时记录这个字段）
		 */
		long date ;
		/**
		 * 原始的状态码
		 */
		int originalStatusCode;
		
		
		public int getOriginalStatusCode() {
			return originalStatusCode;
		}
		public void setOriginalStatusCode(int originalStatusCode) {
			this.originalStatusCode = originalStatusCode;
		}
		public String getDest() {
			return dest;
		}
		public void setDest(String dest) {
			this.dest = dest;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public long getDownloadId() {
			return downloadId;
		}
		public void setDownloadId(long downloadId) {
			this.downloadId = downloadId;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public long getTotalBytes() {
			return totalBytes;
		}
		public void setTotalBytes(long totalBytes) {
			this.totalBytes = totalBytes;
		}
		public long getCurrentBytes() {
			return currentBytes;
		}
		public void setCurrentBytes(long currentBytes) {
			this.currentBytes = currentBytes;
		}
		/**
		 * 下载状态
		 * @return
		 */
		public DownloadStatus getStatus() {
			return status;
		}
		public void setStatus(DownloadStatus status) {
			this.status = status;
		}
		public String getMimeType() {
			return mimeType;
		}
		public void setMimeType(String mimeType) {
			this.mimeType = mimeType;
		}
		/**
		 * 下载暂停或者失败的原因
		 * @return
		 */
		public DownloadReason getReason() {
			return reason;
		}
		public void setReason(DownloadReason reason) {
			this.reason = reason;
		}
		public long getDate() {
			return date;
		}
		public void setDate(long date) {
			this.date = date;
		}
		public String getAppData() {
			return appData;
		}
		public void setAppData(String extra) {
			this.appData = extra;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (downloadId ^ (downloadId >>> 32));
			result = prime * result + ((appData == null) ? 0 : appData.hashCode());
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DownloadItemOutput other = (DownloadItemOutput) obj;
			if (downloadId != other.downloadId)
				return false;
			if (appData == null) {
				if (other.appData != null)
					return false;
			} else if (!appData.equals(other.appData))
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}
    }

	public static class DownloadInputItem {
		String url;
		String mimetype;
		String destFolder;
		String saveName;
		String description;
		String extra;
		
		
		public DownloadInputItem(String url, String mimetype,
				String destFolder, String saveName, String description,
				String extra) {
			super();
			this.url = url;
			this.mimetype = mimetype;
			this.destFolder = destFolder;
			this.saveName = saveName;
			this.description = description;
			this.extra = extra;
		}
		public DownloadInputItem() {
			super();
			// TODO Auto-generated constructor stub
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getMimetype() {
			return mimetype;
		}
		public void setMimetype(String mimetype) {
			this.mimetype = mimetype;
		}
		public String getDestFolder() {
			return destFolder;
		}
		public void setDestFolder(String destFolder) {
			this.destFolder = destFolder;
		}
		public String getSaveName() {
			return saveName;
		}
		public void setSaveName(String saveName) {
			this.saveName = saveName;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getExtra() {
			return extra;
		}
		public void setExtra(String extra) {
			this.extra = extra;
		}
		
	}
    /**
     * 一个下载条目的Listener
     * @author wangliang
     *
     */
    public static interface DownloadItemListener{
    	void onDownloadProcessing(DownloadItemOutput item);
    }
    /**
     * 所有下载条目的Listener
     * @author wangliang
     *
     */
    public static interface DownloadListener{
    	void onDownloadProcessing(List<DownloadItemOutput> items);
    }
    
    /**
     *  比较器，用于比较两个下载条目是否为同一个下载文件
     * @author wangliang
     *
     */
    public static interface DownloadComprator{
    	/**
    	 * 
    	 * @param url1 下载条目1的url
    	 * @param url2 下载条目2的url
    	 * @return
    	 */
    	boolean isTheSame(String url1,String url2);
    }

    /**
     *  默认的比较器，如果两个路径完全相同则认为相同
     * @author wangliang
     *
     */
    public static class DefaultDownloadComprator implements DownloadComprator{
    	public boolean isTheSame(String url1,String url2){
    		if(url1 == null || url2 == null){
    			return false ;
    		}
    		if(url1.equals(url2)){
    			return true ;
    		}
    		return false ;
    	}
    }
    /*public*/ static interface OnNotifierClickListener{
    	//onDownloadCompleted
    	void onDownloadCompleted(boolean successful,long downloadId);
    	void onDownloadRunning(long downloadId);
    	void onDownloadPaused(long downloadId);
    }
    
    private OnNotifierClickListener onNotifierClickListener ;
    private boolean openOnClickSuccessfulDownload = true ;
    private boolean reStartOnClickFailedDownload = true ;
    private boolean autoResume = false ;
    
    public boolean isAutoResume() {
		return autoResume;
	}
    /**
     * 系统启动、网络重新连接时以及SDCard插入时自动重试
     * @param autoResume
     */
	public void setAutoResume(boolean autoResume) {
		this.autoResume = autoResume;
	}

	/**

	/*public*/ void setOnNotifierClickListener(OnNotifierClickListener callback){
    	this.onNotifierClickListener = callback ;
    }
    /**
     * 下载成功后点击通知栏是否自动打开文件
     * @param auto
     */
    /*public*/ void setOpenOnClickSuccessfulDownload(boolean auto){
    	this.openOnClickSuccessfulDownload = auto ;
    }
    /**
     * 下载失败后点击通知栏是否自动重新下载
     * @param restart
     */
   /* public*/ void setReStartOnClickFailedDownload(boolean restart){
    	this.reStartOnClickFailedDownload = restart ;
    }
    /*public*/ OnNotifierClickListener getOnNotifierClickListener(){
    	return this.onNotifierClickListener ;
    }
    /*public*/ boolean isOpenOnClickSuccessfulDownload(){
    	return this.openOnClickSuccessfulDownload ;
    }
    /*public */boolean isReStartOnClickFailedDownload(){
    	return this.reStartOnClickFailedDownload ;
    }
	
}
