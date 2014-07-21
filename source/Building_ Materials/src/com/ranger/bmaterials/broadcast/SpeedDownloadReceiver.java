package com.ranger.bmaterials.broadcast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.download.SpeedDownloadCallback;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.SpeedDownLoadInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.SpeedDownloadResult;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.Md5Tools;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.ui.MainHallActivity;
import com.ranger.bmaterials.ui.ManagerActivity;

public class SpeedDownloadReceiver extends BroadcastReceiver{

	public final static String SPEEDDOWN_ACTION = "com.duoku.gamesearch.speeddownload.REQUESTRECEIVER";
	private Context mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		String action = intent.getAction();
		if (SPEEDDOWN_ACTION.equals(action)) {
			final String gameId = intent.getStringExtra("gameId");
			new Thread(new Runnable() {
				@Override
				public void run() {
					getSpeedDownloadInfos(gameId);
				}
			}).start();
		}
	}
	
	private void getSpeedDownloadInfos(final String docids) {
		NetUtil.getInstance().requestSpeedDownloadInfos(docids, new IRequestListener() {
			@Override
			public void onRequestSuccess(BaseResult responseData) {
				SpeedDownloadResult speedlinfo = (SpeedDownloadResult) responseData;
				ArrayList<SpeedDownLoadInfo> infoList = speedlinfo.getContentList();
				parseDownloadInfo(infoList);
			}
			@Override
			public void onRequestError(int requestTag, int requestId,
					int errorCode, String msg) {
				Toast.makeText(mContext, "no downloadInfo of "+docids, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * 解析根据docid获取的下载信息
	 * 
	 * @param context Context
	 * @param returnString 获取到的下载信息
	 * @param dlType dlType.
	 */
	private void parseDownloadInfo(ArrayList<SpeedDownLoadInfo> list) {
		try {
			if (list.size() == 0) {
				String info = "服务端没有该游戏的下载信息";
				showToast(info);
				return;
			}
			for (SpeedDownLoadInfo dlInfo : list) {
				String url = dlInfo.getUrl();
				String packagename = dlInfo.getPackagename();
				String versionname = dlInfo.getVersionname();
				int versioncode = dlInfo.getVersioncode();
				String iconurl = dlInfo.getIconurl();
				String appname = dlInfo.getAppname();

				DownloadItemInput item = new DownloadItemInput();
				item.setDisplayName(appname);
				item.setPackageName(packagename);
				item.setVersionInt(versioncode);
				item.setDownloadUrl(url);
				item.setIconUrl(iconurl);
				item.setVersion(versionname);
				item.setSaveName(Md5Tools.toMd5(url.getBytes(), true));
				item.setGameId(dlInfo.getGameid());
				item.setAction(dlInfo.getStartaction());
				item.setNeedLogin(dlInfo.isNeedLogin());
				item.setSize(Long.valueOf(dlInfo.getApkSize()));

				// 检查此应用是否需要下载
				PackageManager pm = mContext.getPackageManager();
				try {
					PackageInfo pi = pm.getPackageInfo(packagename, 0);
					if (pi.versionCode != item.getVersionInt()) {
						startSpeedDownload(item, dlInfo.getFilePath());
					} else {
						// 若应用已存在则提示用户应用已安装
						String info = item.getDisplayName()+ "-" + item.getVersion() + 
								mContext.getString(R.string.speed_download_installed);
						showToast(info);
					}
				} catch (Exception e) {
					if (AppManager.getInstance(mContext).getDownloadGameForId(item.getGameId(), false) != null) {
						// 若应用已下载则提示用户一下
						String info = "下载列表中已存在 " + item.getDisplayName() + ",无需再下载";
						showToast(info);
						go2Manager();
						return;
					}
					startSpeedDownload(item, dlInfo.getFilePath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startSpeedDownload(DownloadItemInput app, String filePath) {
		PackageHelper.download_speed(app, new SpeedDownloadCallback(mContext), 0, false);
		String appName = app.getDisplayName();
		DownloadStatistics.addHighSpeedDownloadGameStatistics(mContext, appName);
		showToast(appName + mContext.getString(R.string.speed_download_start));
		go2Manager();
		if (filePath != null) {
			File f = new File(filePath);
			if (f.exists()){
				f.delete();
			}			
		}
	}

	private void go2Manager() {
		if (isRunningOfApp()) {
			// 跳转到管理view
			Intent i = new Intent(mContext, ManagerActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(i);			
		} else {
			//大厅没有运行，带参数启动
			Intent intent = new Intent(mContext,MainHallActivity.class);
			intent.setFlags(0x10010000);
			intent.putExtra("fromSpeedDownload", true);
			mContext.startActivity(intent);			
		}
	}

	private Handler handler = new Handler();

	private void showToast(final String info) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				CustomToast.showToast(mContext, info);
			}
		});
	}
	
	public boolean isRunningOfApp() {
		boolean isAppRunning = false;
		String pkgName = "com.duoku.gamesearch";
		ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
	    for (RunningTaskInfo info : list) {
	        if (info.topActivity.getPackageName().equals(pkgName) && info.baseActivity.getPackageName().equals(pkgName)) {
	            isAppRunning = true;
	            break;
	        }
	    }
	    return isAppRunning;
	}
}