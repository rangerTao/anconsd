package com.ranger.bmaterials.mode;

import java.util.List;

import com.ranger.bmaterials.netresponse.BaseResult;

public class MyDownloadedGames extends BaseResult {
	
	List<MyDownloadedGame> data ;

	public List<MyDownloadedGame> getData() {
		return data;
	}

	public void setData(List<MyDownloadedGame> data) {
		this.data = data;
	}
	
	
}
