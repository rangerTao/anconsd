package com.ranger.bmaterials.mode;

import java.util.List;

import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.netresponse.BaseResult;


public class ActivityDetail extends BaseResult{
	
	
	public static class ActivityItem{
		String activityPic ;
		String activityContent ;
		public ActivityItem(String activityPic, String activityContent) {
			super();
			this.activityPic = activityPic;
			this.activityContent = activityContent;
		}
		public String getActivityPic() {
			return activityPic;
		}
		public void setActivityPic(String activityPic) {
			this.activityPic = activityPic;
		}
		public String getActivityContent() {
			return activityContent;
		}
		public void setActivityContent(String activityContent) {
			this.activityContent = activityContent;
		}
		
		
		
		
	}
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public long getTime() {
		return time;
	}


	public void setTime(long time) {
		this.time = time;
	}


	public String getActsource() {
		return actsource;
	}


	public void setActsource(String actsource) {
		this.actsource = actsource;
	}


	public List<ActivityItem> getData() {
		return data;
	}
	private String id ;
	private long time ;
	private String actsource;
	private String acttitle;



	private List<ActivityItem> data ;
	
	
	public ActivityDetail() {
		super();
	}
	
	
	public ActivityDetail(SearchItem item, List<ActivityItem> data) {
		super();
		this.data = data;
		this.item=item;
	}
	public SearchItem item;
	public void setData(List<ActivityItem> data) {
		this.data = data;
	}


	public String getActtitle() {
		return acttitle;
	}


	public void setActtitle(String acttitle) {
		this.acttitle = acttitle;
	}
	
}
