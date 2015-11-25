package com.andconsd.model;

import java.util.ArrayList;

import com.andconsd.framework.net.response.BaseResult;

public class PictureList extends BaseResult {

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public ArrayList<Picture> getPics() {
		return pics;
	}

	public void setPics(ArrayList<Picture> pics) {
		this.pics = pics;
	}

	private String page;
	private ArrayList<Picture> pics;

}
