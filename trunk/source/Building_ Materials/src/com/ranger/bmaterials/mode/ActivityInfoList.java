package com.ranger.bmaterials.mode;

import java.util.List;

import com.ranger.bmaterials.netresponse.BaseResult;

public class ActivityInfoList extends BaseResult{
	
	private List<ActivityInfo> data ;
	private int totalCount ;
	
	
	
	public ActivityInfoList() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ActivityInfoList(List<ActivityInfo> data, int totalCount) {
		super();
		this.data = data;
		this.totalCount = totalCount;
	}
	public List<ActivityInfo> getData() {
		return data;
	}
	public void setData(List<ActivityInfo> data) {
		this.data = data;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	
}
