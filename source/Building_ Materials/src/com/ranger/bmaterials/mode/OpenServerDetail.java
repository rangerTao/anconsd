package com.ranger.bmaterials.mode;

import java.util.List;

import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.netresponse.BaseResult;

public class OpenServerDetail extends BaseResult {
	
	public static class OpenServerItem{
		/**
		 * 活动图片地址	
		 */
		String picUrl;
		/**
		 * 活动内容
		 */
		String content ;
		public OpenServerItem(String picUrl, String content) {
			super();
			this.picUrl = picUrl;
			this.content = content;
		}
		public String getPicUrl() {
			return picUrl;
		}
		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		
		
		
	}
//	private OpenServer openServer ;
	private List<OpenServerItem> data ;
	
	
	
	
	public OpenServerDetail() {
		super();
		// TODO Auto-generated constructor stub
	}
	public OpenServerDetail(SearchItem item, List<OpenServerItem> data) {
		super();
		this.item = item;
		this.data = data;
	}
//	public String getGameId() {
//		return openServer.getGameId();
//	}
//	public String getId() {
//		return openServer.getId();
//	}
//		
//	public String getGameName() {
//		return openServer.getGameName();
//	}
//	public String getTitle() {
//		return openServer.getTitle();
//	}
//	public String getGameIcon() {
//		return openServer.getGameIcon();
//	}
	/*public OpenServerStatus getStatus() {
		return openServer.getStatus();
	}*/
//	public long getTime() {
//		return openServer.getTime();
//	}
//	public OpenServer getOpenServer() {
//		return openServer;
//	}
//	public void setOpenServer(OpenServer openServer) {
//		this.openServer = openServer;
//	}
	public List<OpenServerItem> getData() {
		return data;
	}
	public void setData(List<OpenServerItem> data) {
		this.data = data;
	}
	
	public SearchItem item;
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
	public String getOpentitle() {
		return opentitle;
	}
	public void setOpentitle(String opentitle) {
		this.opentitle = opentitle;
	}

	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
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
	 * 游戏相关信息
	 */
	private String opentitle;
	/**
	 * 开服id
	 */
	private String openid;
}
