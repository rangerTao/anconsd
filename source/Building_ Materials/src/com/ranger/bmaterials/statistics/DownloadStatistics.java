package com.ranger.bmaterials.statistics;

import android.content.Context;

import com.baidu.mobstat.StatService;
import com.ranger.bmaterials.utils.NetUtil;

public class DownloadStatistics {

	// 下载游戏成功
	public static void addDownloadGameSucceedStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "all_dl_ok_num", gameName);// 复用
		}
	}

	// 新增下载游戏任务
	public static void addDownloadGameStatistics(Context context, String gameName, boolean isDownloadStart) {
		if (gameName != null) {
			StatService.onEvent(context, "all_dl_num", gameName);// 复用
			if (isDownloadStart) {
				NetUtil.getInstance().requestStartDownloadGame("", gameName, null);
			}
		}
	}

	// 点击首页精品list游戏下载 另外需要再统计
	public static void addHomeRecommendListGameDownload(Context cx, String gameName) {
		StatService.onEvent(cx, "home_recom_download", gameName);
	}

	// 点击首页精品banner游戏下载
	public static void addHomeRecommendBannerGameDownload(Context cx, String gameName) {
		StatService.onEvent(cx, "home_re_banner_down", gameName);
	}

	// 详情页新增下载游戏任务
	public static void addDownloadGameInDetailViewStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "detail_dl_num", gameName);// 复用
		}
	}

	// 首页推荐弹窗下载游戏任务
	public static void addDownloadGameInHomeRecStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "rec_dg_dl_num", gameName);// 复用
		}
	}

	// 新增高速下载游戏任务
	public static void addHighSpeedDownloadGameStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "speed_dl_num", gameName);// 复用
		}
	}

	// 更新游戏
	public static void addUpdateGameStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "update_dl_num", gameName);// 复用
		}
	}

	// 安装游戏
	public static void addInstallGameStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "all_install_num", gameName);// 复用
		}
	}

	// 用户暂停下载游戏量
	public static void addUserPauseDownloadGameStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "user_pause_num", gameName);
		}
	}

	// 非用户暂停下载游戏量（断网等原因，非用户主动点击暂停）
	public static void addPauseDownloadGameStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "pause_download_num", gameName);
		}
	}

	// 继续下载游戏点击量
	public static void addResumeDownloadGameStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "resume_download_num", gameName);
		}
	}

	// 下载游戏失败总量
	public static void addDownloadGameFailedStatistics(Context context, String gameName) {

		if (gameName != null) {
			StatService.onEvent(context, "download_failed_num", gameName);
		}
	}

	// 下载游戏失败(由于网络原因)
	public static void addDownloadGameFailedNetStatistics(final Context context, final String gameName) {
		new Thread() {
			@Override
			public void run() {
				if (gameName != null) {
					StatService.onEvent(context, "download_failed_net", gameName);
				}
			}
		}.start();
	}
}
