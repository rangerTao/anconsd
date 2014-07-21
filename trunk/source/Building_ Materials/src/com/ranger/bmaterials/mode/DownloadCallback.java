package com.ranger.bmaterials.mode;
public  interface DownloadCallback {
	
	/**
	 * 下载回调
	 * @param index 索引，批量下载有用；单个下载回调值为0
	 * @param downloadUrl 下载地址
	 * @param status 成功或者失败
	 * @param downloadId 下载返回的downloadId
	 * @param reason 下载失败或者继续下载或者重新下载失败（status为false）的原因，包括 <pre>
	* public static final int ERROR_PARAM_NO_URL;
	* public static final int ERROR_PARAM_NO_GAME_ID;
	* public static final int ERROR_PARAM_NO_PACKAGE_NAME;
	* public static final int ERROR_PARAM_NO_VERSION;
	* public static final int ERROR_DATABASE_ERROR;
	* public static final int ERROR_DEVICE_NOT_FOUND
	* public static final int ERROR_INSUFFICIENT_SPACE
	* 
	* </pre>
	
	 */
	void onDownloadResult(String downloadUrl,boolean status,long downloadId,String saveDest,Integer reason);
	
	/**恢复或者重试的回调
	 * @param index 索引，批量恢复或者批量重试有用；单个回调值为0
	 * @param downloadUrl 下载地址
	 * @param successful 成功或者失败
	 * @param downloadId 下载返回的downloadId
	 * @param reason 继续下载或者重新下载失败（status为false）的原因，包括 <pre>
	* public static final int ERROR_PARAM_NO_URL;
	* public static final int ERROR_PARAM_NO_GAME_ID;
	* public static final int ERROR_PARAM_NO_PACKAGE_NAME;
	* public static final int ERROR_PARAM_NO_VERSION;
	* public static final int ERROR_DATABASE_ERROR;
	* public static final int ERROR_DEVICE_NOT_FOUND
	* public static final int ERROR_INSUFFICIENT_SPACE
	* 
	* </pre>
	*/
	void onResumeDownloadResult(String downloadUrl,boolean successful,Integer reason);
	
	
	
	/**
	 * 增量更新失败后调用普通更新下载的回调（downloadId不会改变，只是改变了downloadurl,size）
	 * @param downloadUrl
	 * @param saveDest
	 * @param successful
	 * @param reason
	 */
	void onRestartDownloadResult(String downloadUrl,String saveDest,boolean successful,Integer reason);
}