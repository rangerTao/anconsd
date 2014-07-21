package com.ranger.bmaterials.mode;

import java.io.Serializable;

public class GameGuideInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4441487332434416869L;
	protected String guideid;
	protected String guidetitle;
	protected String guidetime;
	private GameInfo info;
	
	public String getGuideid() {
		return guideid;
	}
	public void setGuideid(String guideid) {
		this.guideid = guideid;
	}
	public String getGuidetitle() {
		return guidetitle;
	}
	public void setGuidetitle(String guidetitle) {
		this.guidetitle = guidetitle;
	}
	public String getGuidetime() {
		return guidetime;
	}
	public void setGuidetime(String guidetime) {
		this.guidetime = guidetime;
	}
	public GameInfo getInfo() {
		return info;
	}
	public void setInfo(GameInfo info) {
		this.info = info;
	}
	
	
}
