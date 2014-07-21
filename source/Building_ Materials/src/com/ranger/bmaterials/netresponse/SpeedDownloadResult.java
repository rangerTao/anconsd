/**
 * 
 */
package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import com.ranger.bmaterials.mode.SpeedDownLoadInfo;

public class SpeedDownloadResult extends BaseResult {
	private ArrayList<SpeedDownLoadInfo> contentList;
	
	private int errorCode;
	private String errorMsg;
	private String tag;

	public final int getErrorCode() {
		return errorCode;
	}

	public final void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public final String getErrorMsg() {
		return errorMsg;
	}

	public final void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public final String getTag() {
		return tag;
	}

	public final void setTag(String tag) {
		this.tag = tag;
	}

	public ArrayList<SpeedDownLoadInfo> getContentList() {
		return contentList;
	}

	public void setContentList(ArrayList<SpeedDownLoadInfo> contentList) {
		this.contentList = contentList;
	}
}
