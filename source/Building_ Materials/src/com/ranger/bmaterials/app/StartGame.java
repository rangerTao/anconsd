package com.ranger.bmaterials.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.ranger.bmaterials.app.InternalGames.InternalStartGames;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.statistics.GeneralStatistics;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.utils.NetUtil;

public final class StartGame {

	private String action, packageName, gameid;
	private Context cx;
	// private boolean needLogin;
	private PackageManager pManager;

	public static final int START_GAME_REQUEST_CODE = 1024;

	public StartGame(Context cx, String packageName, String action,
			String gameid) {
		this(cx, packageName, action, gameid, false);
	}

	public StartGame(Context cx, String packageName, String action,
			String gameid, boolean need) {
		this.packageName = packageName;
		this.cx = cx;
		this.action = action;
		// this.needLogin = need;
		this.gameid = gameid;
		pManager = cx.getPackageManager();
	}

	public void startGame() {
		startGame(false);
	}

	// private boolean is_response=true;

	public void startGame(boolean forResultStart) {

//		if (MineProfile.getInstance().getIsLogin()) {
//			NetUtil.getInstance().requestGetCoin(
//					MineProfile.getInstance().getUserID(),
//					MineProfile.getInstance().getSessionID(), 0, 2,
//					this.gameid, new IRequestListener() {
//
//						@Override
//						public void onRequestSuccess(BaseResult responseData) {
//							BindPhoneResult result = (BindPhoneResult) responseData;
//
//							MineProfile.getInstance().addCoinnum(
//									result.getCoinnum());
//						}
//
//						@Override
//						public void onRequestError(int requestTag,
//								int requestId, int errorCode, String msg) {
//
//						}
//					});
//		}

		try {
			GeneralStatistics.addStartGameInternalStatistics(
					GameTingApplication.getAppInstance()
							.getApplicationContext(), packageName);
		} catch (Exception e) {
			e.printStackTrace();
			// 由于某些奇怪原因 导致游戏已被卸载 取不到游戏名称页面上也无法启动游戏
		}

		if (action == null || action.equals("")) {
			// 非联运则根据包名启动
			startActivity(forResultStart,
					AppUtil.getLauncherIntent(pManager, packageName));
		}
		// else if (needLogin) {
		// // 联运且需要登录
		// final MineProfile profile = MineProfile.getInstance();
		// boolean is_login = profile.getIsLogin();
		// if (is_login) {
		// // 已经登录 刷新token
		// if(is_response){
		// is_response=false;
		// NetUtil.getInstance().requestForGamesLoginToken(
		// profile.getUserID(), profile.getSessionID(), gameid,
		// new IRequestListener() {
		//
		// @Override
		// public void onRequestSuccess(BaseResult responseData) {
		// // TODO Auto-generated method stub
		// is_response=true;
		// GameLoginTokenResult result = (GameLoginTokenResult) responseData;
		// String token = result.getToken();
		// try {
		// Intent i = new Intent(action);
		// i.putExtra("user_id", profile.getUserID());
		// i.putExtra("user_name",
		// profile.getUserName());
		// i.putExtra("user_nickname",
		// profile.getNickName());
		// i.putExtra("channel_number", getChannel());
		// i.putExtra("user_session", token);
		//
		// cx.startActivity(i);
		// //注册启动联运游戏，三天未启动联运游戏后台发push
		// NetUtil.getInstance().requestRegisterStartGame(gameid, null);
		//
		// statisAppStartCount();
		// } catch (ActivityNotFoundException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// @Override
		// public void onRequestError(int requestTag,
		// int requestId, int errorCode, String msg) {
		// // TODO Auto-generated method stub
		// is_response=true;
		// if (errorCode == 1004)
		// // session失效
		// go2Login();
		// else {
		// // 网络异常
		// LoginRegisterToast.showErrorToast(cx,
		// cx.getString(R.string.network_error_hint));
		// }
		// }
		// });
		// }
		// } else {
		// // 未登录
		// go2Login();
		// }
		// }
		else {
			// 联运但不需要登录
			startAppByAction(action, forResultStart);
			// 注册启动联运游戏，三天未启动联运游戏后台发push
			NetUtil.getInstance().requestRegisterStartGame(gameid, null);

		}
	}

	// private void go2Login() {
	// MineProfile profile = MineProfile.getInstance();
	// profile.setIsLogin(false);
	// Intent intent = new Intent(cx, LoginActivity.class);
	// cx.startActivity(intent);
	// }

	// 根据action启动
	private void startAppByAction(String action, boolean forResultStart) {
		try {
			Intent i = new Intent();
			i.setAction(action);
			i.addCategory(Intent.CATEGORY_DEFAULT);
			if (!startActivity(forResultStart, i)) {
				// 尝试包名启动
				startActivity(forResultStart,
						AppUtil.getLauncherIntent(pManager, packageName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// // 获取本地渠道号
	// private int getChannel() {
	// try {
	// ApplicationInfo appInfo = pManager.getApplicationInfo(
	// cx.getPackageName(), PackageManager.GET_META_DATA);
	// return appInfo.metaData.getInt("BaiduMobAd_CHANNEL");
	// } catch (NameNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return -1;
	// }

	private boolean startActivity(boolean forResultStart, Intent i) {
		try {

			if (null == i) {
				return false;
			}

			Activity act = (Activity) cx;
			if (forResultStart) {
				act.startActivityForResult(i, START_GAME_REQUEST_CODE);
				statisAppStartCount();
			} else {
				// intent==null 有可能已卸载 但卸载监听失效
				cx.startActivity(i);
				statisAppStartCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void statisAppStartCount() {
		InternalStartGames.addStartGame(cx, packageName);
		// 首页我的游戏提示个数刷新
		cx.sendBroadcast(new Intent(
				BroadcaseSender.ACTION_WHITELIST_INITIALIZED));
	}
}
