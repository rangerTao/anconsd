package com.ranger.bmaterials.mode;

import java.util.List;

import com.ranger.bmaterials.netresponse.BaseResult;

public class WhiteList extends BaseResult {
	List<BaseAppInfo> data ;

	
	public WhiteList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WhiteList(List<BaseAppInfo> data) {
		super();
		this.data = data;
	}

	@Override
	public String toString() {
		return "WhiteList [data=" + data + "]";
	}

	public List<BaseAppInfo> getData() {
		return data;
	}

	public void setData(List<BaseAppInfo> data) {
		this.data = data;
	}
	
	
}
