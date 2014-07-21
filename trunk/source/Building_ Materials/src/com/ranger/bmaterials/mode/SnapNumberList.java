package com.ranger.bmaterials.mode;

import java.util.List;

import com.ranger.bmaterials.netresponse.BaseResult;

public class SnapNumberList extends BaseResult{

	private int totalCount ;
	private List<SnapNumber> data ;
	public SnapNumberList() {
		super();
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public List<SnapNumber> getData() {
		return data;
	}
	public void setData(List<SnapNumber> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "SnapNumberList [totalCount=" + totalCount + ", data=" + data
				+ ", mErrorCode=" + mErrorCode + ", mErrorString="
				+ mErrorString + ", mTag=" + mTag + ", getTotalCount()="
				+ getTotalCount() + ", getData()=" + getData()
				+ ", getErrorCode()=" + getErrorCode() + ", getErrorString()="
				+ getErrorString() + ", getTag()=" + getTag() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	
	
	
}
