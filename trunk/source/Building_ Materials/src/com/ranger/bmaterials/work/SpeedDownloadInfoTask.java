package com.ranger.bmaterials.work;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.download.DefaultDownLoadCallBack;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.SpeedDownLoadInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.SpeedDownloadResult;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.Md5Tools;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.ui.ManagerActivity;

public final class SpeedDownloadInfoTask extends Thread {
	/*
	 * 高速下载修改要点： 1、切换到线上地址 2、去除引导页 3、修改application name 
	 * 失败原因：1、浏览器不兼容（已大部分兼容）
	 * 2、打的线下服务器的包没有该游戏数据 （打包时需注意）
	 * 3、如果下载任务已完成就不会跳转到下载管理页面 （已解决）
	 * 4、被360手机助手拦截了下载 下载后的包文件名被其修改（无法解决）
	 * 5、网络请求慢 启动高速下载需要等待一段时间（暂不解决）
	 * 6、手机没有存储卡或者存储卡未挂载 包被下载到手机内存 我们的应用只会扫描存储卡（暂不解决）
	 * 7、多个存储卡挂载（已兼容）
	 */
	/** Context */
	private Activity mContext = null;

	public static final String SPEED_DOWNLOAD_SP = "StartSpeedDownLoad";

	// private String test_ids = "50646,42503";

	public SpeedDownloadInfoTask(Activity context) {
		mContext = context;
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		SharedPreferences speed_sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		 boolean haveSpeed = speed_sp.getBoolean(SPEED_DOWNLOAD_SP, true);
		 if (haveSpeed) {
			 super.start();
			 speed_sp.edit().putBoolean(SPEED_DOWNLOAD_SP, false).commit();
		 }
	}

	@Override
	public void run() {
		// <uses-permission id="android.permission.RAISED_THREAD_PRIORITY"/>
		// android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpGet method = new HttpGet(
					Constants.GAMESEARCH_SPEEDDOWNLOAD_DIRSCAN_URL);
			HttpResponse response = httpClient.execute(method);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = new String(EntityUtils.toString(
						response.getEntity()).getBytes("ISO-8859-1"));
				if (!isInterrupted()) {
					parseDirs(result);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

	}

	private void parseDirs(String returnString) throws Exception {
		JSONArray jsonArray = new JSONArray(returnString);
		File dir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			dir = Environment.getExternalStorageDirectory();
		}
		ArrayList<String> apks = findApkFromLocalDir(dir, jsonArray);
		if (apks.isEmpty()) {
			//多个存储卡的情况
			File[] files = Environment.getExternalStorageDirectory()
					.getParentFile().listFiles();
//			Log.e("speed", "speedfiles:" + files.length);
			for (File f : files) {

				if (f.getName().toLowerCase().contains("sdcard")
						&& !f.getName().equals(
								Environment.getExternalStorageDirectory()
										.getName())) {
//					Log.e("speed", "speedname:" + f.getName());
					apks.addAll(findApkFromLocalDir(f, jsonArray));
					if (!apks.isEmpty())
						break;
				}
			}
		}

		StringBuffer docIds = new StringBuffer();
		HashMap<String, String> id_path_map = new HashMap<String, String>();
		for (String apkFilePath : apks) {
			// File file = new File(apkFilePath);
			// if (!name.toLowerCase().matches(
			// Constants.SPEED_DOWLOAD_APK_NAME_PATTERN)) {
			// continue;
			// }
			int index1 = apkFilePath.indexOf("_");
			int index2 = apkFilePath.lastIndexOf("_");
			// int index3 = apkFilePath.lastIndexOf(".");
			// int expiredTime = StringUtil.parseInt(name.substring(index2 + 1,
			// index3));
			//
			// long currentTime = System.currentTimeMillis();
			// long lastModified = file.lastModified();
			// long time = currentTime - lastModified;
			// if (time > 0 && time < expiredTime * DateUtils.MINUTE_IN_MILLIS)
			// {
			try {
				String id = apkFilePath.substring(index1 + 1, index2);
				if (!docIds.toString().contains(id)) {
					docIds.append(id).append(",");
				}
				// }
				id_path_map.put(id, apkFilePath);
			} catch (Exception e) {
			}
			// file.delete();
		}
//		Log.e("speed", "speed");
		if (!TextUtils.isEmpty(docIds)) {
			docIds.deleteCharAt(docIds.length() - 1);
//			Log.e("speed", "speedid:" + docIds);
			getSpeedDownloadInfos(docIds.toString(), id_path_map);
		}
	}

	private ArrayList<String> findApkFromLocalDir(File dir, JSONArray jsonArray)
			throws Exception {
		File downloadfile = null;
		final ArrayList<String> apks = new ArrayList<String>();
		int len = jsonArray.length();
		for (int i = 0; i < len; i++) {
			downloadfile = new File(dir, jsonArray.get(i).toString());

			downloadfile.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					filename = filename.toLowerCase();
					if (dir.getName().startsWith(".")
							|| filename.startsWith(".")
							|| dir.getName().startsWith("image")
							|| filename.startsWith("image")
							|| dir.getName().startsWith("cache")
							|| filename.startsWith("cache")
							|| dir.getName().startsWith("thumb")
							|| filename.startsWith("thumb")
							|| dir.getName().startsWith("ting")
							|| filename.startsWith("ting")) {
						return false;
					}
					File tempFile = new File(dir, filename);
					if (dir.isDirectory()) {
						if (tempFile.isFile()) {
							if (tempFile.getName().startsWith("gamesearch")
									&& tempFile.getName().endsWith(".apk")) {
								apks.add(tempFile.getAbsolutePath());
								return true;
							}
						} else {
							tempFile.listFiles(this);
						}
					} else {
						if (filename.startsWith("gamesearch")
								&& dir.getName().endsWith(".apk")) {
							apks.add(tempFile.getAbsolutePath());
							return true;
						}
					}

					return false;
				}
			});
		}

		return apks;
	}

	private void getSpeedDownloadInfos(String docids,
			final HashMap<String, String> id_path_map) {
		NetUtil.getInstance().requestSpeedDownloadInfos(docids,
				new IRequestListener() {

					@Override
					public void onRequestSuccess(BaseResult responseData) {
						SpeedDownloadResult speedlinfo = (SpeedDownloadResult) responseData;
						ArrayList<SpeedDownLoadInfo> infoList = speedlinfo
								.getContentList();

						for (SpeedDownLoadInfo info : infoList) {
							info.setFilePath(id_path_map.get(info.getGameid()));
						}
//						Log.e("speed", "speedlist:" + infoList.size());
						parseDownloadInfo(infoList);
					}

					@Override
					public void onRequestError(int requestTag, int requestId,
							int errorCode, String msg) {
					}
				});
	}

	/**
	 * 解析根据docid获取的下载信息
	 * 
	 * @param context
	 *            Context
	 * @param returnString
	 *            获取到的下载信息
	 * @param dlType
	 *            dlType.
	 */
	private void parseDownloadInfo(ArrayList<SpeedDownLoadInfo> list) {
		// 解析返回的下载信息
		try {
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
						String info = item.getDisplayName()
								+ "-"
								+ item.getVersion()
								+ mContext
										.getString(R.string.speed_download_installed);
						showToast(info);

					}
				} catch (Exception e) {
					if (AppManager.getInstance(mContext).getDownloadGameForId(
							item.getGameId(), false) != null) {
						// 若应用已下载则提示用户一下
						// String info = "下载列表中已存在 " + item.getDisplayName()
						// + "，无需再下载";
						// showToast(info);
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

	private Handler handler = new Handler();

	private void showToast(final String info) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				CustomToast.showToast(mContext, info);
			}
		});
	}

	private void go2Manager() {
		// 跳转到管理view
		Intent i = new Intent(mContext, ManagerActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mContext.startActivity(i);
	}

	private void startSpeedDownload(DownloadItemInput app, String filePath) {
		PackageHelper.download(app, new DefaultDownLoadCallBack(mContext));
		String appName = app.getDisplayName();
		DownloadStatistics
				.addHighSpeedDownloadGameStatistics(mContext, appName);

		showToast(appName + mContext.getString(R.string.speed_download_start));
		go2Manager();

		File f = new File(filePath);
		if (f.exists())
			f.delete();
	}
}