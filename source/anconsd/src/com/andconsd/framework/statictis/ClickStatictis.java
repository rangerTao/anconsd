package com.andconsd.framework.statictis;

import com.baidu.mobstat.StatService;

import android.content.Context;

public class ClickStatictis {

	/**
	 * 菜单按钮点击量
	 * @param context
	 * @param label
	 */
	public static void addMenuItemClick(Context context,String label){
		StatService.onEvent(context, "mitem_onclick", label);
	}
	
	/**
	 * 加载更多
	 * @param context
	 * @param label
	 */
	public static void addGetMoreStatictis(Context context,String label){
		StatService.onEvent(context, "get_more", label);
	}
	
	public static void addPicListOnItemClick(Context context,String label){
		StatService.onEvent(context, "item_onclick", label);
	}
	
	public static void addSendToButtonOnClick(Context context){
		StatService.onEvent(context, "sendto_click", "分享按钮");
	}
	
	public static void addSendToSinaClick(Context context){
		StatService.onEvent(context, "sendto_weibo", "分享至微博");
	}
	
	public static void addSendToWeixin(Context context){
		StatService.onEvent(context, "sendto_weixin", "分享至微信");
	}
	
	public static void addSendToWXZone(Context context){
		StatService.onEvent(context, "sendto_wxzone", "分享至朋友圈");
	}
	
	public static void addSendToMore(Context context){
		StatService.onEvent(context, "sendto_more", "更多分享");
	}
	
	public static void addFeedBackClick(Context context){
		StatService.onEvent(context, "menu_feedback", "意见反馈点击量");
	}
}
