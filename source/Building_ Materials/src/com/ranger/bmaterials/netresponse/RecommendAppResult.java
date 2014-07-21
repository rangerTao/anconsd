package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;
import java.util.List;

import com.ranger.bmaterials.mode.RecommendAppItemInfo;

public class RecommendAppResult extends BaseResult{

	private int totalcount = 0;
	private List<RecommendAppItemInfo> appList = new ArrayList<RecommendAppItemInfo>();
	private String title;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getTotalcount() {
		return totalcount;
	}
	public void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}
	public List<RecommendAppItemInfo> getAppList() {
		return appList;
	}
	public void setAppList(List<RecommendAppItemInfo> appList) {
		this.appList = appList;
	}
}
