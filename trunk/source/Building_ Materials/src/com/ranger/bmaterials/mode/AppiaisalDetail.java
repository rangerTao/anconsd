package com.ranger.bmaterials.mode;


import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.netresponse.BaseResult;

public class AppiaisalDetail extends BaseResult {

	public long getInfostime() {
		return infostime;
	}
	public void setInfostime(long infostime) {
		this.infostime = infostime;
	}
	public String getInfossource() {
		return infossource;
	}
	public void setInfossource(String infossource) {
		this.infossource = infossource;
	}
	public String getInfoscontent() {
		return infoscontent;
	}
	public void setInfoscontent(String infoscontent) {
		this.infoscontent = infoscontent;
	}
	public String getInfostitle() {
		return infostitle;
	}
	public void setInfostitle(String infostitle) {
		this.infostitle = infostitle;
	}
	/**
	 * 资讯时间
	 */
	private long infostime;
	/**
	 * 资讯来源
	 */
	private String infossource;
	/**
	 * 资讯内容
	 */
	private String infoscontent;
	/**
	 * 游戏相关信息
	 */
	public SearchItem item;
	/**
	 * 资讯标题
	 */
	private String infostitle;
}
