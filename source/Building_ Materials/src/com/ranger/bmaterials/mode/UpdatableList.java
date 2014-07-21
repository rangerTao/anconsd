package com.ranger.bmaterials.mode;

import java.util.List;

import com.ranger.bmaterials.netresponse.BaseResult;

public class UpdatableList extends BaseResult {
	List<UpdatableItem> data ;

	
	
	public UpdatableList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UpdatableList(List<UpdatableItem> data) {
		super();
		this.data = data;
	}

	public List<UpdatableItem> getData() {
		return data;
	}

	public void setData(List<UpdatableItem> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "UpdatableList [data=" + data + "]";
	}
	
	
}
