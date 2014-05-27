/**
 * 
 */
package com.andconsd.net;

import com.andconsd.net.response.BaseResult;

public class NetMessage {

	private BaseResult		mResponseData;
	private NetMessageType 	mMessageType;
	private long			mCurentSize;
	private long			mTotalSize;
	private int				mRequestId;
	private String 	 		mErrorStr;
	private int				mRequestTag;
	private int 			mErrorCode;
	
	public static enum NetMessageType {
		NetSuccess,
		NetFailure,
		NetDownloadling,
		NetDownloadSuccess,
		NetDownloadFailure,
		NetCancel,
	};
	
	public int getErrorCode(){
		return mErrorCode;
	}

	public void setErrorCode(int ecode){
		mErrorCode = ecode;
	}
	public BaseResult getResponseData() {
		return mResponseData;
	}

	public void setResponseData(BaseResult data) {
		this.mResponseData = data;
	}
	
	public NetMessageType getMessageType() {
		return mMessageType;
	}

	public void setMessageType(NetMessageType messageType) {
		this.mMessageType = messageType;
	}

	public long getCurentSize() {
		return mCurentSize;
	}

	public void setCurentSize(long curentSize) {
		this.mCurentSize = curentSize;
	}

	public long getTotalSize() {
		return mTotalSize;
	}

	public void setTotalSize(long totalSize) {
		this.mTotalSize = totalSize;
	}

	public int getRequestId() {
		return mRequestId;
	}

	public void setRequestId(int requestId) {
		this.mRequestId = requestId;
	}

	public void setErrorString(String errorString){
		this.mErrorStr = errorString;
	}
	
	public String getErrorString(){
		return this.mErrorStr;
	}
	
	public int getRequestTag(){
		return this.mRequestTag;
	}
	
	public void setRequestTag(int requestTag){
		this.mRequestTag = requestTag;
	}
}
