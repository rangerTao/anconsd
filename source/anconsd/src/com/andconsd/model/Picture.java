package com.andconsd.model;

import com.andconsd.framework.net.response.BaseResult;

public class Picture extends BaseResult{

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getPinid() {
		return pinid;
	}

	public void setPinid(String pinid) {
		this.pinid = pinid;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescp() {
		return descp;
	}

	public void setDescp(String descp) {
		this.descp = descp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPfrom() {
		return pfrom;
	}

	public void setPfrom(String pfrom) {
		this.pfrom = pfrom;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIsused() {
		return isused;
	}

	public void setIsused(String isused) {
		this.isused = isused;
	}

	public String getLast_modify() {
		return last_modify;
	}

	public void setLast_modify(String last_modify) {
		this.last_modify = last_modify;
	}

	private String _id = "";
	private String pinid = "";
	private String width = "";
	private String height = "";
	private String title = "";
	private String descp = "";
	private String url = "";
	private String pfrom = "";
	private String category = "";
	private String isused = "";
	private String last_modify = "";

}
