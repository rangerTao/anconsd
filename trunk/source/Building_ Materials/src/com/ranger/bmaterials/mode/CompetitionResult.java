package com.ranger.bmaterials.mode;

import java.util.ArrayList;

import com.ranger.bmaterials.netresponse.BaseResult;

public class CompetitionResult extends BaseResult {

	public String desc;// 争霸赛说明

	public int gamesCount;

	public String bannerIconUrl;//

	public ArrayList<CompetitionInfo> competitions_list;

	public static class CompetitionInfo {
		public String title="";// 比赛名称
		public String gameId="";
		public String gameName="";
		public String picUrl="";
		public String date="";//开始时间
		public String end_date="";//结束时间
		public String memebers="";// 参赛人数
		public String rewards="";// 奖品
		public String rule="";// 规则
		public String pkgName="";
		public String coins="";// 金币数

		// 用来查询下载状态的信息
		public String downloadUrl="";
		public String version="";
		public String versionInt="";

		// 用来启动游戏
		public String startAction="";
		public boolean isNeedLogin;

		public String topreward="";// 最高奖项
		public int rescode;// 0:参赛进行中1：参赛已结束 2 即将开始的参赛

		public static final int competitioning = 0;
		public static final int competitioned = 1;
		public static final int competitionsoon = 2;
	}
}
