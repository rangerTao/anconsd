package com.ranger.bmaterials.mode;

import java.util.List;

import com.ranger.bmaterials.mode.SnapNumber.SnapNumberStatus;
import com.ranger.bmaterials.netresponse.BaseResult;

public class SnapNumberDetail extends BaseResult{
	/*游戏包名*/
	private String packageName	;
	private String gameName ;
	/*游戏下载地址  */
	private String downloadurl ;
	private List<SnapNumberItem> data ;
	private SnapNumber snapNumber =new SnapNumber();
	
	public static class SnapNumberItem{
		/*活动图片*/
		String picUrl;
		/*活动内容*/
		String content;
		public SnapNumberItem(String picUrl, String content) {
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
	
	
	public SnapNumberDetail(String packageName, String gameName ,String downloadurl,
			List<SnapNumberItem> data, SnapNumber snapNumber) {
		super();
		this.packageName = packageName;
		this.gameName = gameName ;
		this.downloadurl = downloadurl;
		this.data = data;
		this.snapNumber = snapNumber;
	}

	public SnapNumberDetail() {
		super();
	}
	
	public String getGameId() {
		if(!check()){
			return null ;
		}
		return snapNumber.getGameId();
		
	}
	public String getId() {
		return snapNumber.getId();
	}
	public void setId(String id){
		snapNumber.setId(id);
	}
	public String getTitle() {
		if(!check()){
			return null ;
		}
		return snapNumber.getTitle();
	}
	public String getIconUrl() {
		if(!check()){
			return null ;
		}
		return snapNumber.getIconUrl();
	}
	public SnapNumberStatus getStatus() {
		if(!check()){
			return null ;
		}
		return snapNumber.getStatus();
	}
	
	public void setStatus(SnapNumberStatus status) {
		this.snapNumber.setStatus(status);
	}
	
	public int getLeftCount() {
		if(!check()){
			return -1 ;
		}
		return snapNumber.getLeftCount();
	}
	
	public void setLeftCount(int leftCount) {
		this.snapNumber.setLeftCount(leftCount);
	}
	public int getTotalCount() {
		if(!check()){
			return -1 ;
		}
		return snapNumber.getTotalCount();
	}
	
	private boolean check(){
		return snapNumber != null ;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getDownloadurl() {
		return downloadurl;
	}

	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}

	public List<SnapNumberItem> getData() {
		return data;
	}

	public void setData(List<SnapNumberItem> data) {
		this.data = data;
	}

	public SnapNumber getSnapNumber() {
		return snapNumber;
	}

	public void setSnapNumber(SnapNumber snapNumber) {
		this.snapNumber = snapNumber;
	}
	public long getTime() {
		return snapNumber.getTime();
	}
	public void setTime(long time) {
		this.snapNumber.setTime(time);
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	
	public String getNumber() {
		return snapNumber.getNumber();
	}
	public void setNumber(String number) {
		this.snapNumber.setNumber(number);
	}
	
}
