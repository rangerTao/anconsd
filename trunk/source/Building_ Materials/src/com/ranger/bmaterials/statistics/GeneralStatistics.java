package com.ranger.bmaterials.statistics;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.baidu.mobstat.StatService;
import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.download.tool.AppUtils;
import com.ranger.bmaterials.tools.PhoneHelper;

public class GeneralStatistics {

	// 启动
	public static void onResume(Context context) {
		StatService.onResume(context);
	}

	// 退出
	public static void onPause(Context context) {
		StatService.onPause(context);
	}

	// 启动游戏
	public static void addStartGameInternalStatistics(Context context, String pkgName) {
		String appName = getAppName(context, pkgName);
		if (appName != null) {
			StatService.onEvent(context, "start_game_internal", appName);
		}
	}

	// 游戏详情页统计
	public static void addGameDetailViewStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "open_game_detail", gameName);
		}
	}

	// 评论成功统计
	public static void addCommentSucceedStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "comment_game_succeed", gameName);
		}
	}

	// 游戏收藏统计
	public static void addCollectGameStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "collect_game", gameName);
		}
	}

	// 分享统计
	public static void addShareGameStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "share_game", gameName);
		}
	}

	private static String getAppName(Context context, String pkgName) {

		String appName = null;

		PackageInfo packageinfo = AppUtils.getPacakgeInfo(context, pkgName);
		if (packageinfo != null) {
			appName = ((String) packageinfo.applicationInfo.loadLabel(context.getPackageManager()));
		}

		return appName;
	}

	public static void addSquareSlideStatistics(Context context) {
		StatService.onEvent(context, "square_activity_slide", "发现页滑动次数");
	}

	public static void addGameSlideStatistics(Context context) {
		StatService.onEvent(context, "game_activity_slide", "游戏页滑动次数");
	}

	public static void uninstallGameCountStatistics(Context context, String gamename) {
		StatService.onEvent(context, "uninstall_game_count", gamename);
	}

	public static void addHomeLoadAllDevicesStatistics(Context context) {
		StatService.onEvent(context, "home_load_all_device", PhoneHelper.getUdid());
	}

	// 统计首页显示的次数，用于和MTJ的DAU数据进行对比
	public static void addHomePageShowCountStatistics(Context context) {

		SharedPreferences sPreferences = context.getSharedPreferences(Constants.SETTINGS_PREFERENCE, Context.MODE_PRIVATE);
		String last_date = sPreferences.getString(context.getString(R.string.sp_key_home_page_last_date), "");
		String now_aday = Constants.FORMATER_DATE_FORMAT.format(Calendar.getInstance().getTime());
		// 当天首次启动时
		if (!last_date.equals("") && !last_date.equals(now_aday)) {
			StatService.onEvent(context, "home_page_show", PhoneHelper.getUdid());
		} else
		// 非当天首次启动时
		{
			Editor editor = sPreferences.edit();
			editor.putString(context.getString(R.string.sp_key_home_page_last_date), now_aday);
			editor.commit();
		}
	}

	// 首页推荐弹窗统计
	public static void addRecDialogViewPVStatistics(Context context) {
		StatService.onEvent(context, "rec_dg_viewed_pv", "启动弹窗PV");
	}

	public static void addRecDialogViewUVStatistics(Context context) {
		String udid = PhoneHelper.getUdid();
		if (!TextUtils.isEmpty(udid)) {
			StatService.onEvent(context, "rec_dg_viewed_uv", udid);
		}
	}
}
