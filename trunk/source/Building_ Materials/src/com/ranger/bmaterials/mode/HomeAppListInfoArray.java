package com.ranger.bmaterials.mode;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 一组数据
 * */
public class HomeAppListInfoArray {

	public ArrayList<HomeAppListItemInfo> homeAppListInfos = new ArrayList<HomeAppListItemInfo>();

	public HomeAppListBannerInfo bannerInfo = new HomeAppListBannerInfo();

	public static class HomeAppListItemInfo extends HomeAppListBaseInfo {
		private static final long serialVersionUID = -259757604451659583L;
		public String gamestar;
		public String gamedownloadcount;
		public String gamerecommenddesc;
		public String labelName;
		public String labelColor;
	}
	
	public static class HomeAppListBannerInfo extends HomeAppListBaseInfo{
		private static final long serialVersionUID = 7140827635930528160L;
		public String bannericon;
	}

	public static class HomeAppListBaseInfo implements Serializable {
		private static final long serialVersionUID = -259757604451659583L;
		public String gameid;
		public String gamename;
		public String pkgname;
		public String pkgsize;
		public String gameicon;
		public String startaction;
		public String downloadurl;
		public String versionname;
		public String versioncode="0";
		public long downloadId;
		public PackageMode packageMode;
		
		
	}
}
