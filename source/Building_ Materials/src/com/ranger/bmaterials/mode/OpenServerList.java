package com.ranger.bmaterials.mode;

import java.util.List;

import com.ranger.bmaterials.netresponse.BaseResult;

public class OpenServerList extends BaseResult {
	private int totalCount ;
	private List<OpenServer> data ;
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public List<OpenServer> getData() {
		return data;
	}
	public void setData(List<OpenServer> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "OpenServerList [totalCount=" + totalCount + ", data=" + data
				+ ", mErrorCode=" + mErrorCode + ", mErrorString="
				+ mErrorString + ", mTag=" + mTag + ", getTotalCount()="
				+ getTotalCount() + ", getData()=" + getData()
				+ ", getErrorCode()=" + getErrorCode() + ", getErrorString()="
				+ getErrorString() + ", getTag()=" + getTag() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	
}
