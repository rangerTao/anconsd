package com.ranger.bmaterials.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.DownloadAdapter;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.Constants.CancelReason;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.download.DownloadConfiguration;
import com.ranger.bmaterials.download.DownloadUtil;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadListener;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadReason;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.DownloadCallback;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.tools.LogcatScanner.LogcatObserver;
import com.ranger.bmaterials.tools.install.AppSilentInstaller;
import com.ranger.bmaterials.tools.install.PackageUtils;
import com.ranger.bmaterials.tools.install.ShellUtils.CommandResult;
import com.ranger.bmaterials.view.DuokuDialog;
import com.ranger.bmaterials.view.PopupWindowCompat;
import com.ranger.bmaterials.work.DowloadAppsLoader;

public class DownloadAppListFragment extends AbstractAppListFragment<DownloadAppInfo> implements DownloadListener, OnScrollListener, OnClickListener, OnItemLongClickListener {
	public static final String TAG = "DownloadAppListFragment";

	LinearLayout view_pager;
	String intentFrom = "";

	private final static int REFRESH_ITEM_COUNT = 1;
	private final static int REFRESH_ITEM_LIST = 1 << 1;
	private final static int REFRESH_LIST = 1 << 2;

	private DownloadAppInfo downloadItem;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case REFRESH_ITEM_COUNT:
				ManagerActivity activity = (ManagerActivity) getActivity();
				if (activity != null) {
					activity.updateTitle(0, mAdapter.getCount());
				}

				if (mAdapter.getCount() < 1) {
					if (view_pager != null) {
						view_pager.setVisibility(View.GONE);
					}
				}

				break;
			case REFRESH_ITEM_LIST:
				mAdapter.remove((DownloadAppInfo) msg.obj);
				mAdapter.notifyDataSetChanged();

				updateCount();
				break;
			case REFRESH_LIST:
				if (getActivity() != null) {
					try {
						if (getLoaderManager() != null && getLoaderManager().getLoader(0) != null && getLoaderManager().getLoader(0).isStarted()) {
							getLoaderManager().restartLoader(0, null, DownloadAppListFragment.this);
						}
					} catch (Exception e) {
					}

				}

				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerDownloadListener();
	}

	@Override
	public Loader<List<DownloadAppInfo>> onCreateLoader(int id, Bundle args) {
		return new DowloadAppsLoader(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.manager_activity_download_fragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initView();

		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {

		try {
			if (null != getActivity() && null != getActivity().getIntent() && null != getActivity().getIntent().getExtras()) {
				String temp = getActivity().getIntent().getExtras().getString("from");
				if (temp != null)
					intentFrom = temp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Message msg = new Message();
		msg.what = REFRESH_LIST;
		mHandler.sendMessage(msg);

		ClickNumStatistics.addManageDownloadClickStatis(getActivity().getApplicationContext());

		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterDownloadListener();
	}

	private void initView() {
		View view = getView();
		View resumeAll = view.findViewById(R.id.manager_download_downloadall);
		View pauseAll = view.findViewById(R.id.manager_download_pauseall);
		resumeAll.setOnClickListener(this);
		pauseAll.setOnClickListener(this);

		listView = (ListView) view.findViewById(R.id.manager_activity_download_list);
		listView.setEmptyView(view.findViewById(R.id.download_null));
		View parent = (View) listView.getParent();
		parent.setVisibility(View.INVISIBLE);

		progressBar = view.findViewById(R.id.manager_download_list_progressbar);
		mAdapter = new DownloadAdapter(getActivity());
		mAdapter.setOnListItemClickListener(this);
		listView.setAdapter(mAdapter);
		listView.setOnScrollListener(this);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);

		notifyView = view.findViewById(R.id.manager_activity_download_hint_text);
		notifyTextView = (TextView) notifyView.findViewById(R.id.red_notify_plain_text);
		notifyView.findViewById(R.id.red_notify_red_text).setVisibility(View.GONE);
		notifyView.setBackgroundColor(getResources().getColor(R.color.listview_header_background));

		view_pager = (LinearLayout) view.findViewById(R.id.manager_pager);
	}

	boolean DEBUG = true;
	MyDownloadCallback myDownloadCallback = new MyDownloadCallback();

	/**
	 * 下载、继续下载或者重试的回调方法
	 * 
	 * @author wangliang
	 * 
	 */
	class MyDownloadCallback implements DownloadCallback {

		private DownloadAppInfo findTarget(String url) {
			if (mAdapter == null || mAdapter.getData() == null) {
				if (DEBUG) {
					Log.d(TAG, String.format("DownloadCallback.findTarget return null searchResultAdapter is null or data is null for:%s ", url));
				}
				return null;
			}

			List<DownloadAppInfo> data = mAdapter.getData();
			int size = data.size();
			DownloadAppInfo target = null;
			for (int i = 0; i < size; i++) {
				DownloadAppInfo item = data.get(i);
				if (url.equals(item.getDownloadUrl())) {
					target = item;
				}
			}
			if (DEBUG) {
				Log.d(TAG, String.format("DownloadCallback.findTarget return null for %s ", url));
			}
			return target;
		}

		@Override
		public void onDownloadResult(String downloadUrl, boolean successful, long downloadId, String saveDest, Integer reason) {
			DownloadAppInfo target = findTarget(downloadUrl);
			if (target == null) {
				return;
			}
			String gameName = target.getName();
			if (successful) {
				target.setDownloadId(downloadId);
				target.setSaveDest(saveDest);
			}
			if (DEBUG) {
				if (successful) {
					Log.d(TAG, String.format("[onDownloadResult]target:%s download successful,downloadId:%s", gameName, downloadId));
				} else {
					Log.d(TAG, String.format("[onDownloadResult]target:%s download error,reason:%s", gameName, reason));
				}
			}

		}

		@Override
		public void onResumeDownloadResult(String url, boolean successful, Integer reason) {
			DownloadAppInfo target = findTarget(url);
			if (target == null) {
				return;
			}
			String gameName = target.getName();
			if (DEBUG) {
				if (successful) {
					Log.d(TAG, String.format("[onResumeDownloadResult]target:%s resume/restart successful", gameName));
				} else {
					Log.d(TAG, String.format("[onResumeDownloadResult]target:%s resume/restart error,reason:%s", gameName, reason));
				}
			}

		}

		@Override
		public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
			// TODO Auto-generated method stub
			DownloadAppInfo target = findTarget(downloadUrl);
			if (target == null) {
				return;
			}
		}

	}

	private boolean checkNetwork(int position, DownloadAppInfo item, int reqCode) {

		boolean networkAvailable = DeviceUtil.isNetworkAvailable(getActivity());
		if (!networkAvailable) {
			CustomToast.showToast(getActivity(), getString(R.string.alert_network_inavailble));
			return false;
		}

		Integer activeNetworkType = DeviceUtil.getActiveNetworkType(getActivity());
		if (MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
			if (activeNetworkType != null && activeNetworkType == ConnectivityManager.TYPE_MOBILE) {
				DuokuDialog.showNetworkAlertDialog(DownloadAppListFragment.this, reqCode, item.getPackageName(), item.getDownloadUrl(), position);
				return false;
			}
		}
		return true;
	}

	private boolean checkNetwork(int reqCode) {

		boolean networkAvailable = DeviceUtil.isNetworkAvailable(getActivity());
		if (!networkAvailable) {
			CustomToast.showToast(getActivity(), getString(R.string.alert_network_inavailble));
			return false;
		}

		Integer activeNetworkType = DeviceUtil.getActiveNetworkType(getActivity());
		if (MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
			if (activeNetworkType != null && activeNetworkType == ConnectivityManager.TYPE_MOBILE) {

				DuokuDialog.showNetworkAlertDialog(DownloadAppListFragment.this, reqCode, null, null, -1);
				return false;
			}
		}
		return true;
	}

	/**
	 * 暂停下载
	 * 
	 * @param app
	 */
	private void pauseDownload(DownloadAppInfo app) {
		if (DEBUG) {
			logForPauseDownload(app);
		}
		PackageHelper.pauseDownloadGames(app.getDownloadId());
	}

	/**
	 * 重新合并或者普通更新
	 * 
	 * @param item
	 */
	private void reMergeApp(DownloadAppInfo item) {
		/**
		 * 增量更新失败次数过多,走普通更新
		 */
		if (item.getMergeFailedCount() >= 1) {
			UpdatableAppInfo updatableGame = AppManager.getInstance(getActivity()).getUpdatableGame(item.getPackageName());
			if (updatableGame == null) {
				// Log.e()出错
			}
			DownloadItemInput downloadItemInput = new DownloadItemInput(item.getIconUrl(), item.getGameId(), item.getPackageName(), item.getName(), item.getName(), item.getVersionInt(),
					item.getVersion(), updatableGame.getDownloadUrl(), null, updatableGame.getNewSize(), null, -1, item.getExtra(), item.isNeedLogin(), false);

			PackageHelper.restartDownloadNormally(item.getDownloadId(), downloadItemInput, myDownloadCallback);
		} else {
			PackageHelper.sendMergeRequestFromUI(item.getDownloadId());
		}
	}

	private void reStartnNormally(PackageMode mode) {
		if (mAdapter == null) {
			return;
		}
		List<DownloadAppInfo> data = mAdapter.getData();
		if (data == null) {
			return;
		}
		DownloadAppInfo item = null;
		String gameId = mode.gameId;
		for (DownloadAppInfo searchItem : data) {
			if (gameId != null && gameId.equals(searchItem.getGameId())) {
				item = searchItem;
				break;
			}
		}
		if (item != null && item.getMergeFailedCount() >= 2) {
			DownloadItemInput formDownloadInput = formDownloadInput(item);
			PackageHelper.restartDownloadNormally(item.getDownloadId(), formDownloadInput, myDownloadCallback);
		} else {
			// PackageHelper.sendMergeRequest(item.getDownloadId());
		}
	}

	private DownloadItemInput formDownloadInput(DownloadAppInfo item2) {
		AppManager manager = AppManager.getInstance(GameTingApplication.getAppInstance());
		UpdatableAppInfo item = manager.getUpdatableGame(item2.getPackageName());

		DownloadItemInput downloadItemInput = new DownloadItemInput(item.getIconUrl(), item.getGameId(), item.getPackageName(), item.getName(), item.getName(), item.getNewVersionInt(),
				item.getNewVersion(), item.getDownloadUrl(), null, item.getNewSize(), null, -1, item.getExtra(), item.isNeedLogin(), false);
		return downloadItemInput;

	}

	/**
	 * 重新下载
	 * 
	 * @param position
	 * @param item
	 */
	private void restartDownload(int position, DownloadAppInfo item) {

		PackageHelper.restartDownload(item.getDownloadId(), myDownloadCallback);
		// 统计下载
		// DownloadStatistics.addDownloadGameStatistics(getApplicationContext(),item.getGameName());
	}

	private void resumeAllDownload() {
		List<DownloadAppInfo> data = mAdapter.getData();
		ArrayList<Long> ids = new ArrayList<Long>();
		for (DownloadAppInfo downloadAppInfo : data) {

			int status = downloadAppInfo.getApkStatus();
			if (status == PackageMode.DOWNLOAD_FAILED || status == PackageMode.DOWNLOAD_PAUSED || status == PackageMode.DOWNLOAD_PENDING) {
				ids.add(downloadAppInfo.getDownloadId());
			}
			// if (downloadAppInfo.getStatus() == DownloadStatus.STATUS_PENDING
			// || downloadAppInfo.getStatus() == DownloadStatus.STATUS_PAUSED ||
			// downloadAppInfo.getStatus() == DownloadStatus.STATUS_FAILED) {
			// ids.add(downloadAppInfo.getDownloadId());
			// }
		}
		if (ids.size() == 0) {
			return;
		}
		boolean checkNetwork = checkNetwork(REQ_CODE_DOWNLOAD_ALL);
		if (checkNetwork) {
			final long[] idsArray = new long[ids.size()];
			int i = 0;
			for (long id : ids) {
				idsArray[i] = id;
				i++;
			}
			PackageHelper.resumeDownload(myDownloadCallback, idsArray);
		}

	}

	/**
	 * 继续下载
	 * 
	 * @param position
	 * @param item
	 */
	private void resumeDownload(int position, DownloadAppInfo item) {

		boolean checkNetwork = checkNetwork(position, item, DownloadAppListFragment.REQ_CODE_RESUME);
		if (!checkNetwork) {
			return;
		}
		if (DEBUG) {
			logForResumeDownload(item);
		}
		PackageHelper.resumeDownload(item.getDownloadId(), myDownloadCallback);
		DownloadStatistics.addResumeDownloadGameStatistics(getActivity(), item.getName());

	}

	private void logForPauseDownload(DownloadAppInfo item) {
		String fmt = "Pause download for %s,is diff update? %s,downloadId:%s,apk status:%s";
		Log.i(TAG, String.format(fmt, item.getName(), item.isDiffUpdate(), item.getDownloadId(), PackageMode.getStatusString(item.getApkStatus())));

	}

	private void logForResumeDownload(DownloadAppInfo item) {
		String fmt = "Resume Download for %s,is diff update? %s,apk status:%s";
		Log.i(TAG, String.format(fmt, item.getName(), item.isDiffUpdate(), PackageMode.getStatusString(item.getApkStatus())));

	}

	// click button's callback.
	@Override
	public void onItemButtonClick(View view, int position) {

		super.onItemButtonClick(view, position);
		if (mAdapter == null) {
			return;
		}
		DownloadAppInfo item = mAdapter.getItem(position);
		int apkStatus = item.getApkStatus();
		switch (apkStatus) {
		case PackageMode.UNDOWNLOAD:
			break;
		case PackageMode.DOWNLOAD_PENDING:
		case PackageMode.DOWNLOAD_RUNNING:
			pauseDownload(item);
			break;
		case PackageMode.DOWNLOAD_PAUSED:
			if (checkSdCard())
				resumeDownload(position, item);
			break;
		case PackageMode.DOWNLOAD_FAILED:
			if (checkSdCard())
				restartDownload(position, item);
			break;
		case PackageMode.DOWNLOADED:
			if (item.isDiffUpdate()) {
				// 增量更新
				PackageHelper.sendMergeRequestFromUI(item.getDownloadId());
				Log.e(TAG, String.format("%s is downloded(is diff update),but user is clicked!", item.getName()));
			} else {
				// 普通更新或者普通下载，安装
				installApp(item);
			}
			break;
		case PackageMode.DOWNLOADED_DIFFERENT_SIGN:
		case PackageMode.MERGED_DIFFERENT_SIGN:
			break;
		case PackageMode.MERGING:
			// 正在合并，不做事情
			Log.e(TAG, String.format("%s is merging,but user is clicked!", item.getName()));
			break;
		case PackageMode.MERGED:
			if (!item.isDiffUpdate()) {
				Log.d(TAG, String.format("%s is merged,but is not diff update", item.getName()));
			}
			installApp(item);
			if (DEBUG) {
				Log.e(TAG, item.getName() + " is merged:" + item);
			}
			break;
		case PackageMode.CHECKING_FINISHED:
			installApp(item);
			if (DEBUG) {
				Log.e(TAG, item.getName() + " is CHECKING_FINISHED:" + item);
			}
			break;
		case PackageMode.MERGE_FAILED:
			if (checkSdCard())
				reMergeApp(item);
			break;
		case PackageMode.INSTALLING:
			Log.e(TAG, String.format("%s is installing,but user can clicked", item.getName()));
			break;
		case PackageMode.INSTALL_FAILED:
			installApp(item);
			break;
		case PackageMode.INSTALLED:
			// openGame(item);
			break;
		case PackageMode.UPDATABLE:
			// download(position, item);
			break;
		case PackageMode.UPDATABLE_DIFF:
			// downloadForDiff(position, item);
			break;
		default:
			break;
		}

	}

	private void showFailedDialog(DownloadAppInfo item/* long downloadId */, String dialogBody) {
		new AlertDialog.Builder(getActivity()).setTitle("安装程序出错").setMessage(dialogBody).setNegativeButton("取消"/* "删除" */, getDeleteClickHandler(item/* downloadId */))
				.setPositiveButton("重新下载", getRestartClickHandler(item/* downloadId */)).show();
	}

	private DialogInterface.OnClickListener getDeleteClickHandler(final DownloadAppInfo item /*
																							 * final
																							 * long
																							 * downloadId
																							 */) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// AppManager manager = AppManager.getInstance(getActivity());
				// HashMap<Long, String> hashMap = new HashMap<Long, String>(1);
				// hashMap.put(item.getDownloadId(), item.getPackageName());
				// manager.removeDownloadGames(hashMap);

				// manager.removeDownloadGames(downloadId);
			}
		};
	}

	private DialogInterface.OnClickListener getRestartClickHandler(final DownloadAppInfo item /*
																							 * long
																							 * downloadId
																							 */) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AppManager manager = AppManager.getInstance(getActivity());
				manager.restartDownload(item.getDownloadId());
			}
		};

	}

	private void installApp(DownloadAppInfo item) {

		PackageHelper.installApp(DownloadAppListFragment.this.getActivity(), item.getGameId(), item.getPackageName(), item.getSaveDest());
		/*
		 * try {
		 * getActivity().getContentResolver().openFileDescriptor(Uri.parse(
		 * item.getSaveDest()), "r").close(); } catch (FileNotFoundException
		 * exc) { showFailedDialog(item.getDownloadId(), "下载文件已经删除,请选择：");
		 * return ; } catch (Exception e) { e.printStackTrace();
		 * showFailedDialog(item.getDownloadId(), "解析文件出现问题,请选择："); return ; }
		 * Intent intent = new Intent(Intent.ACTION_VIEW);
		 * intent.setDataAndType(Uri.parse(item.getSaveDest()),
		 * "application/vnd.android.package-archive");
		 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
		 * Intent.FLAG_GRANT_READ_URI_PERMISSION);
		 * //com.android.packageinstaller/PackageInstallerActivity
		 * intent.setClassName("com.android.packageinstaller",
		 * "com.android.packageinstaller.PackageInstallerActivity");
		 * 
		 * try { startActivityForResult(intent, 100);
		 * listen(item.getPackageName());
		 * //LogcatScanner.startScanLogcatInfo(new
		 * MyLogcatObserver(item.getPackageName())); } catch
		 * (ActivityNotFoundException ex) { }
		 */
		/*
		 * try { File f = new File("/system/app/Superuser.apk"); if (f.exists())
		 * { Log.i("MyLogcatObserver", "该设备有/system/app/Superuser.apk "); }else{
		 * Log.i("MyLogcatObserver", "该设备没有/system/app/Superuser.apk "); } }
		 * catch (Throwable e1) { Log.i("MyLogcatObserver",
		 * "/system/app/Superuser.apk出错 ",e1); } try { File f = new
		 * File("/data/data/com.duoku.gamesearch"); if (f.exists()) {
		 * Log.i("MyLogcatObserver", "该设备有/data/data/com.duoku.gamesearch");
		 * }else{ Log.i("MyLogcatObserver",
		 * "该设备没有/data/data/com.duoku.gamesearch"); } } catch (Throwable e1) {
		 * Log.i("MyLogcatObserver", "/data/data/com.duoku.gamesearch出错 ",e1); }
		 */
	}

	private void installAPKBySUCommand(String path) {
		if (true) {
			CommandResult result = PackageUtils.installSilent(path);
			if (Constants.DEBUG)
				Log.i("SilentInstallService", "error msg:" + result.errorMsg);
			return;
		}
		if (true) {
			PackageUtils.installSilent(getActivity(), path);
			return;
		}
		Log.i("SilentInstallService", "installAPKBySUCommand");
		// String path = "/mnt/sdcard/duoku/GameSearch/downloads/杀手.apk";
		boolean installSuccess = false;

		java.lang.Process process = null;
		boolean needDestroyProcess = true;
		try {
			Log.i("SilentInstallService", "SilentInstallService execute 'su'");
			process = Runtime.getRuntime().exec("su");
			// int waitFor = process.waitFor();
			OutputStream os = process.getOutputStream();
			if (os != null) {
				DataOutputStream dos = new DataOutputStream(os);
				// 将文件路径用单引号括起来。
				byte[] command = ("pm install -r \'" + path + "\'\n").getBytes("UTF-8");
				// dos.writeUTF("pm install -r " + path + "\n");
				Log.i("SilentInstallService", "SilentInstallService 'pm install -r " + path + "'");
				dos.write(command);
				dos.flush();

				dos.writeBytes("exit\n");
				// 等待用户选择（如果用户不选择超时后exitValue==1），阻塞；如果用户拒绝立刻返回（exitValue==1）；如果用户同意，那么会在安装完成之后返回（需要时间）
				Log.i("SilentInstallService", "SilentInstallService execute 'exit',waitting...");
				dos.flush();
				int exitValue = process.waitFor();

				needDestroyProcess = false; // waitFor()方法中已将Process teminate

				Log.i(TAG, "silent install finished, exit value is:" + exitValue);
				Log.i("SilentInstallService", "SilentInstallService waitFor exitValue:" + (exitValue == 0));
				if (exitValue == 0) {
					// 只有在exitValue为0时才读输出信息。
					// 因在某些机型上可能出现Segmentation fault,读输出流时会阻塞。
					DataInputStream dis = new DataInputStream(process.getInputStream());
					String line;
					while ((line = dis.readLine()) != null) {
						Log.i("SilentInstallService", "readLine " + line);
						if (line.toLowerCase().contains("success")) {
							installSuccess = true;
							break;
						}
					}
					dis.close();

					/*
					 * if (DEBUG) { DataInputStream error = new
					 * DataInputStream(process.getErrorStream()); while ((line =
					 * error.readLine()) != null) { if (DEBUG) { Log.i(TAG,
					 * "error:" + line); } // pm在安装时会用error信息打印出安装包路径 //
					 * 出错情况暂不处理 } error.close(); }
					 */
				}
				DataInputStream error = new DataInputStream(process.getErrorStream());
				String line;
				while ((line = error.readLine()) != null) {
					if (Constants.DEBUG)
						Log.i("SilentInstallService", "error line:" + line);
					// pm在安装时会用error信息打印出安装包路径
					// 出错情况暂不处理
				}
				error.close();
				dos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (Constants.DEBUG)
				Log.i("SilentInstallService", "IOException ", e);
			// 一般是没有su命令
			installSuccess = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.i("SilentInstallService", "InterruptedException ", e);
			installSuccess = false;
		} catch (Exception e) {
			e.printStackTrace();
			installSuccess = false;
			Log.i("SilentInstallService", "Other Exception ", e);
		} finally {
			if (process != null && needDestroyProcess) {
				process.destroy();
				Log.i("SilentInstallService", "SilentInstallService process destroy");
			}
		}
		Log.i("SilentInstallService", "SilentInstallService finished. 结果:" + installSuccess);
		Log.i(TAG, "silent install finished, 结果:" + installSuccess);
	}

	/**
	 * i9100通过receiver监听非常漫
	 * 
	 * @param packageName
	 */
	private void listen(final String packageName) {

		new Thread() {
			private int timeout = 40 * 1000;
			private long startTime = -1;

			@Override
			public void run() {
				startTime = System.currentTimeMillis();
				File f = new File("/data/data/" + packageName);
				while (true) {
					long c = System.currentTimeMillis();
					long o = (c - startTime) / 1000;
					if ((c - startTime) >= timeout) {
						if (Constants.DEBUG)
							Log.i("MyLogcatObserver", "超时,停止");
						break;
					} else {
						if (Constants.DEBUG)
							Log.i("MyLogcatObserver", "消耗时间：" + o + " current:" + c + "  start:" + startTime);
					}
					try {
						if (!f.exists()) {
							if (Constants.DEBUG)
								Log.i(TAG, "该设备没有/data/data/" + packageName + "继续");
							Thread.sleep(400);
						} else {
							if (Constants.DEBUG)
								Log.i(TAG, "该设备有/data/data/" + packageName + ",停止");
							notifyInstallResult(packageName, true);
							break;
						}

					} catch (Throwable e1) {
						if (Constants.DEBUG)
							Log.i(TAG, "/data/data/" + packageName + "出错,停止");
						break;
					}
				}

			}
		}.start();
	}

	static final int WHAT_NOTIFYINSTALLRESULT = 200;

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WHAT_NOTIFYINSTALLRESULT) {
				try {
					String packageName = (String) msg.obj;
					List<DownloadAppInfo> data = mAdapter.getData();
					if (data == null)
						return;
					for (Iterator iterator2 = data.iterator(); iterator2.hasNext();) {
						DownloadAppInfo downloadAppInfo = (DownloadAppInfo) iterator2.next();
						if (downloadAppInfo.getPackageName().equals(packageName)) {
							iterator2.remove();
							break;
						}

					}
					mAdapter.notifyDataSetChanged();

					populateData();
					updateCount();

					if (pw != null && pw.isShowing()) {
						pw.dismiss();
					}
					pw = null;

					if (Constants.DEBUG)
						Log.i("MyLogcatObserver", "Adapter 刷新:" + packageName);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
	}

	MyHandler myHandler = new MyHandler();

	private void notifyInstallResult(String packageName, boolean sure) {
		if (Constants.DEBUG)
			Log.i("MyLogcatObserver", "notifyInstallResult");
		Message message = new Message();
		message.what = WHAT_NOTIFYINSTALLRESULT;
		message.obj = packageName;
		myHandler.sendMessage(message);

		BroadcaseSender sender = BroadcaseSender.getInstance(GameTingApplication.getAppInstance());
		sender.sendPreBroadcastForPackageEvent(true, packageName);
	}

	class MyLogcatObserver implements LogcatObserver {
		private static final String TAG = "MyLogcatObserver";
		private String packageName;

		public MyLogcatObserver(String packageName) {
			this.packageName = packageName;
		}

		@Override
		public void handleNewLine(String line) {
			if (line.contains("android.intent.action.DELETE") && line.contains(packageName)) {
				if (Constants.DEBUG)
					Log.i(TAG, "logcat输出信息:" + packageName + " " + line);
				// 启动删除提示
			}
			if (line.contains(packageName)) {
				if (Constants.DEBUG)
					Log.i(TAG, "logcat输出信息:" + packageName + " " + line);
				// 启动删除提示
			}
			File file = new File("/data/app/");
			if (file != null) {
				String[] list = file.list();
				if (list != null) {
					for (String string : list) {
						if (string.contains(packageName)) {
							if (Constants.DEBUG)
								Log.e(TAG, ">>>apk:" + string);
						} else {
							if (Constants.DEBUG)
								Log.i(TAG, ">>>apk:" + string);
						}

					}

				}
			}
			try {
				File f = new File("/data/data/" + packageName);
				if (f.exists()) {
					if (Constants.DEBUG)
						Log.i(TAG, "该设备有/data/data/" + packageName);
				} else {
					if (Constants.DEBUG)
						Log.i(TAG, "该设备没有/data/data/" + packageName);
				}
			} catch (Throwable e1) {
				if (Constants.DEBUG)
					Log.i(TAG, "/data/data/" + packageName + "出错");
			}

		}

	}

	static final int REQ_CODE_RESTART = 200;
	static final int REQ_CODE_RESUME = 201;
	static final int REQ_CODE_DOWNLOAD_ALL = 202;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		try {
			if (requestCode == REQ_CODE_RESTART) {
				handleRestartDownload(data);
			} else if (requestCode == REQ_CODE_RESUME) {
				handleResumeDownload(data);
			} else if (requestCode == REQ_CODE_DOWNLOAD_ALL) {
				handleDownloadAll(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void handleDownloadAll(Intent intent) {
		if (intent == null) {
			return;
		}
		List<DownloadAppInfo> data = mAdapter.getData();
		ArrayList<Long> ids = new ArrayList<Long>();

		for (DownloadAppInfo downloadAppInfo : data) {
			if (downloadAppInfo.getStatus() == DownloadStatus.STATUS_PAUSED || downloadAppInfo.getStatus() == DownloadStatus.STATUS_FAILED) {
				ids.add(downloadAppInfo.getDownloadId());
			}
		}
		final long[] idsArray = new long[ids.size()];
		int i = 0;
		for (long id : ids) {
			idsArray[i] = id;
			i++;
		}
		if (ids.size() == 0) {
			return;
		}
		PackageHelper.resumeDownload(myDownloadCallback, idsArray);

	}

	private void handleResumeDownload(Intent data) {
		int intExtra = data.getIntExtra(DownloadDialogActivity.ARG_EXTRA, -1);
		if (intExtra == -1) {
			return;
		}

		String url = data.getStringExtra(DownloadDialogActivity.ARG2);
		List<DownloadAppInfo> list = mAdapter.getData();
		if (list == null) {
			return;
		}
		DownloadAppInfo item = null;
		for (DownloadAppInfo d : list) {
			if (d.getDownloadUrl().equals(url)) {
				item = d;
			}
		}
		// DownloadAppInfo item = mAdapter.getItem(intExtra);
		if (item != null) {
			// TODO 可能有潜在的问题
			PackageHelper.resumeDownload(item.getDownloadId(), myDownloadCallback);
			DownloadStatistics.addResumeDownloadGameStatistics(getActivity(), item.getName());
		}

	}

	private void handleRestartDownload(Intent data) {

		int intExtra = data.getIntExtra(DownloadDialogActivity.ARG_EXTRA, -1);
		if (intExtra == -1) {
			return;
		}
		String url = data.getStringExtra(DownloadDialogActivity.ARG2);
		List<DownloadAppInfo> list = mAdapter.getData();
		if (list == null) {
			return;
		}
		DownloadAppInfo item = null;
		for (DownloadAppInfo d : list) {
			if (d.getDownloadUrl().equals(url)) {
				item = d;
			}
		}
		// DownloadAppInfo item = mAdapter.getItem(intExtra);
		if (item != null) {
			PackageHelper.restartDownload(item.getDownloadId(), myDownloadCallback);
			DownloadStatistics.addDownloadGameStatistics(GameTingApplication.getAppInstance(), item.getName(), false);
		}

	}

	@Override
	public void onItemIconClick(View view, int position) {
		super.onItemIconClick(view, position);
		DownloadAppInfo item = mAdapter.getItem(position);
		DownloadStatus status = item.getStatus();
		showPopupWindow(view, position, status, item.getApkStatus(), item.isDiffUpdate());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		super.onItemClick(parent, view, position, id);
		View subView = view.findViewById(R.id.manager_activity_download_list_item_icon);
		onItemIconClick(subView, position);
	}

	class DeleteConfirmClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialog_button_left:
				DownloadAppInfo item = (DownloadAppInfo) v.getTag();
				if(item!=null){
					deleteDownload(item);
					updateCount();
				}
				break;
			case R.id.dialog_button_right:
				break;

			default:
				break;
			}
			dismissDeleteDialog();
		}

	}

	class CancleAllConfirmClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialog_button_left:
				deleteAllDownload();
				updateCount();
				break;
			case R.id.dialog_button_right:
				break;

			default:
				break;
			}
			dismissCancleAllDialog();
		}

	}

	private void showDeleteConfirmDialog(DownloadAppInfo item) {
		confirmDialog = new Dialog(this.getActivity(), R.style.dialog_style_zoom);
		confirmDialog.setCancelable(true);
		View contentView = View.inflate(this.getActivity(), R.layout.custom_delete_confirm_dialog_layout, null);
		TextView buttonLeft = (TextView) contentView.findViewById(R.id.dialog_button_left);
		buttonLeft.setTag(item);
		TextView buttonRight = (TextView) contentView.findViewById(R.id.dialog_button_right);
		DeleteConfirmClickListener deleteConfirmClickListener = new DeleteConfirmClickListener();
		buttonLeft.setOnClickListener(deleteConfirmClickListener);
		buttonRight.setOnClickListener(deleteConfirmClickListener);

		confirmDialog.addContentView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		confirmDialog.show();
	}

	private void showCancleAllConfirmDialog() {

		if (null == cancleAllDialog) {
			cancleAllDialog = new Dialog(this.getActivity(), R.style.dialog_style_zoom);
			cancleAllDialog.setCancelable(true);
			View contentView = View.inflate(this.getActivity(), R.layout.custom_delete_confirm_dialog_layout, null);
			TextView titleText = (TextView) contentView.findViewById(R.id.progress_message_body);

			titleText.setText("确定要全部取消所有下载任务?");
			TextView buttonLeft = (TextView) contentView.findViewById(R.id.dialog_button_left);

			TextView buttonRight = (TextView) contentView.findViewById(R.id.dialog_button_right);
			CancleAllConfirmClickListener listener = new CancleAllConfirmClickListener();
			buttonLeft.setOnClickListener(listener);
			buttonRight.setOnClickListener(listener);

			cancleAllDialog.addContentView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}

		cancleAllDialog.show();
	}

	private void dismissDeleteDialog() {
		if (confirmDialog != null && confirmDialog.isShowing()) {
			confirmDialog.dismiss();
			confirmDialog=null;
		}
	}

	private void dismissCancleAllDialog() {
		if (cancleAllDialog != null && cancleAllDialog.isShowing()) {
			cancleAllDialog.dismiss();
			cancleAllDialog = null;
		}
	}

	private void deleteDownload(final DownloadAppInfo item) {

		// DBTaskManager.submitTask(new Runnable() {
		//
		// @Override
		// public void run() {

		if (item.getDownloadId() <= 0) {
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					PackageHelper.removeDownloadGames(item.getDownloadId());

					Message msg_refresh_item_list = new Message();
					msg_refresh_item_list.what = REFRESH_ITEM_LIST;
					msg_refresh_item_list.obj = item;
					mHandler.sendMessage(msg_refresh_item_list);

				}
			}).start();

		}
		// }
		// });

	}

	private void deleteAllDownload() {
		List<DownloadAppInfo> data = mAdapter.getData();
		if (data == null) {
			return;
		}
		final List<DownloadAppInfo> items = new ArrayList<DownloadAppInfo>();
		for (DownloadAppInfo downloadAppInfo : data) {
			int apkStatus = downloadAppInfo.getApkStatus();

			// if (apkStatus != PackageMode.DOWNLOADED && apkStatus !=
			// PackageMode.CHECKING_FINISHED && apkStatus!=
			// PackageMode.CHECKING) {
			if (apkStatus == PackageMode.DOWNLOAD_RUNNING || apkStatus == PackageMode.DOWNLOAD_PENDING || apkStatus == PackageMode.DOWNLOAD_PAUSED || apkStatus == PackageMode.DOWNLOAD_FAILED) {
				items.add(downloadAppInfo);
			}
		}
		int size = items.size();
		if (size == 0) {
			// Toast.makeText(getActivity(), "已经全部在下载或者下载完成",
			// Toast.LENGTH_LONG).show();
		} else {
			long[] idsArr = new long[size];
			for (int i = 0; i < size; i++) {
				idsArr[i] = items.get(i).getDownloadId();
			}
			PackageHelper.removeDownloadGames(idsArr);

			mAdapter.setNotifyOnChange(false);
			for (DownloadAppInfo d : items) {
				mAdapter.remove(d);
			}
			mAdapter.setNotifyOnChange(true);
			mAdapter.notifyDataSetChanged();

			// hide control buttons.
			if (mAdapter.getCount() < 1 && view_pager != null) {
				view_pager.setVisibility(View.GONE);
			}
		}
	}

	OnClickListener popupWindowOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				View parent = (View) v.getParent();
				final Integer p = (Integer) parent.getTag();

				DownloadAppInfo item = mAdapter.getItem(p);
				if (null != downloadItem) {
					item = downloadItem;
				}

				switch (v.getId()) {
				case R.id.manager_download_popupwindow_cancle:
					deleteDownload(item);
					break;
				case R.id.manager_download_popupwindow_delete:
					showDeleteConfirmDialog(item);
					break;
				case R.id.manager_download_popupwindow_detail:
					AppManager manager = AppManager.getInstance(getActivity());
                    manager.jumpToDetail(getActivity(), item.getGameId(), item.getName(), item.getPackageName(), false);
                    break;
				}

				if (pw != null && pw.isShowing()) {
					pw.getContentView().invalidate();
					pw.dismiss();
				}
				pw = null;

				downloadItem = null;
				getLoaderManager().restartLoader(0, null, DownloadAppListFragment.this);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	};

	PopupWindow pw;

	private void showPopupWindow(View view, int position, DownloadStatus status, int apkStatus, boolean diffUpdate) {
		View contentView = null;
		boolean showingTowardUp = showingTowardUp(view);

		downloadItem = mAdapter.getItem(position);
		if (!showingTowardUp) {
			contentView = View.inflate(getActivity(), R.layout.manager_download_popupwindow, null);
		} else {
			contentView = View.inflate(getActivity(), R.layout.manager_download_popupwindow_up, null);
		}
		contentView.setTag(position);
		TextView cancleText = (TextView) contentView.findViewById(R.id.manager_download_popupwindow_cancle);
		TextView deleteText = (TextView) contentView.findViewById(R.id.manager_download_popupwindow_delete);
		// boolean flag = (status == DownloadStatus.STATUS_SUCCESSFUL);
		boolean flag = false;

		if (apkStatus == PackageMode.MERGED || apkStatus == PackageMode.MERGE_FAILED || apkStatus == PackageMode.MERGING || apkStatus == PackageMode.INSTALLING
				|| apkStatus == PackageMode.INSTALL_FAILED || apkStatus == PackageMode.DOWNLOADED || apkStatus == PackageMode.CHECKING || apkStatus == PackageMode.CHECKING_FINISHED) {

			flag = true;
		} else {
			flag = false;
		}
		cancleText.setVisibility(flag ? View.GONE : View.VISIBLE);
		deleteText.setVisibility(flag ? View.VISIBLE : View.GONE);
		if (!flag) {
			cancleText.setOnClickListener(popupWindowOnClickListener);
		} else {
			deleteText.setOnClickListener(popupWindowOnClickListener);
		}
		contentView.findViewById(R.id.manager_download_popupwindow_detail).setOnClickListener(popupWindowOnClickListener);
		pw = new PopupWindowCompat(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pw.setFocusable(true);
		pw.setOutsideTouchable(true);
		// pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		pw.setBackgroundDrawable(new BitmapDrawable(getResources()));
		// get icon's location
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getActivity().getResources().getDisplayMetrics());
		int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getActivity().getResources().getDisplayMetrics());

		if (!showingTowardUp) {
			pw.setAnimationStyle(R.style.popup_down_animation);
			pw.showAsDropDown(view, horizontalMargin, verticalMargin);
		} else {
			pw.setAnimationStyle(R.style.popup_up_animation);
			// pw.showAsDropDown(view,0, -view.getMeasuredHeight());
			int[] screenWH = DeviceUtil.getScreensize(getActivity());
			pw.showAtLocation(view, Gravity.TOP | Gravity.LEFT, location[0] + horizontalMargin, location[1] - view.getHeight() + verticalMargin);
		}

		// pw.showAsDropDown(view, view.getMeasuredWidth(),
		// view.getMeasuredHeight());
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private boolean showingTowardUp(View iconView) {

		if (null == iconView) {
			return false;
		}

		View listView = (View) iconView.getParent().getParent().getParent().getParent().getParent();

		if (null == listView) {
			return false;
		}

		int[] location = new int[2];
		listView.getLocationOnScreen(location);

		int listViewCenterY = listView.getHeight() / 2 + location[1];
		iconView.getLocationOnScreen(location);
		int iconY = location[1];

		return (iconY + iconView.getHeight()) > listViewCenterY;
	}

	/**
	 * populate data.
	 */
	private void populateData() {
		List<DownloadAppInfo> data = mAdapter.getData();
		if (data == null || data.size() == 0) {
			notifyView.setVisibility(View.VISIBLE);
			notifyTextView.setText("没有正在下载的游戏");
			notifyView.setVisibility(View.GONE);
			view_pager.setVisibility(View.GONE);
		} else {
			notifyView.setVisibility(View.GONE);
			view_pager.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onLoadFinished(Loader<List<DownloadAppInfo>> loader, List<DownloadAppInfo> data) {
		dismissPWIfNecessary();
		if (data != null) {
			sort(data);
			super.onLoadFinished(loader, data);
		} else {
			super.onLoadFinished(loader, data);
		}
		View parent = (View) listView.getParent();
		parent.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		populateData();
		// test();
		updateCount();

	}

	private void dismissPWIfNecessary() {
		try {
			if (pw != null && pw.isShowing()) {
				pw.dismiss();
			}
			pw = null;
		} catch (Exception e) {
		}
	}

	public void dismissPopupWindow() {
		if (null != pw && pw.isShowing()) {
			pw.getContentView().invalidate();
			pw.dismiss();
			pw = null;
			if (null != downloadItem) {
				downloadItem = null;
			}
		}
	}

	private void updateCount() {
		Message msg_refresh_count = new Message();
		msg_refresh_count.what = REFRESH_ITEM_COUNT;
		mHandler.sendMessage(msg_refresh_count);
	}

	@Override
	public void onLoaderReset(Loader<List<DownloadAppInfo>> loader) {
		super.onLoaderReset(loader);
	}

	private void test() {
		// http://androidcracking.blogspot.com/2010/12/getting-apk-signature-outside-of.html
		// //2|org.ivywire.crushthezombiesfree|file:///mnt/sdcard/duoku/GameSearch/downloads/%E7%B2%89%E7%A2%8E%E5%83%B5%E5%B0%B8.apk
		// String path =
		// "file:///mnt/sdcard/duoku/GameSearch/downloads/%E7%B2%89%E7%A2%8E%E5%83%B5%E5%B0%B8.apk";
		// //org.ivywire.crushthezombiesfree
		//
		// String path2 = Uri.parse(path).getPath();
		// InstalledAppInfo app =
		// AppUtil.loadAppInfo(getActivity().getPackageManager(),
		// "org.ivywire.crushthezombiesfree");
		// String sign = app.getSign();//36f0720140158605e4d48ffcadad8ece
		// //36f0720140158605e4d48ffcadad8ece
		//
		// String sign2 = ApkCerMgr2.getSign(path2);
		// byte[] bytes = sign2.getBytes();
		// String md5 = AppUtil.getMD5(bytes);

	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void onScrollStateChanged(AbsListView absListView, int scrollState) {
		// Pause fetcher to ensure smoother scrolling when flinging
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
			// BitmapCacheHelper.pauseWork(this.bitmapLoader,true);
		} else {
			// BitmapCacheHelper.pauseWork(this.bitmapLoader,false);
		}
	}

	@Override
	public void onDownloadProcessing(List<DownloadItemOutput> items) {
		if (Constants.DEBUG)
			logList(items);
		refreshList(items);
	}

	private Handler handler = new Handler();
	private View progressBar;
	private ListView listView;

	private void refreshList(final List<DownloadItemOutput> items) {

		this.handler.post(new Runnable() {
			public void run() {
				if (listView == null || mAdapter == null) {
					return;
				}
				int firstVisiblePosition = listView.getFirstVisiblePosition();
				List<DownloadAppInfo> data = mAdapter.getData();
				if (items != null && data != null) {
					mAdapter.setNotifyOnChange(false);
					for (DownloadAppInfo app : data) {
						for (DownloadItemOutput file : items) {
							long downloadId = file.getDownloadId();

							if (Constants.DEBUG)
								Log.i(TAG,
										String.format("Refresh set data:appId:%s,fileId:%s,app.package:%s,file.package:%s", app.getDownloadId(), downloadId, app.getPackageName(), file.getAppData()));

							if ((app.getDownloadId() == downloadId) && !app.getPackageName().equals(file.getAppData())) {
								continue;
							} else if (app.getPackageName().equals(file.getAppData()) && (app.getDownloadId() != downloadId)) {
								continue;
							}
							/**
							 * 可能出现包名不同的情形，所以只用downloadid匹配
							 */
							if (app.getDownloadId() == downloadId /*
																 * && app.
																 * getPackageName
																 * (
																 * ).equals(file
																 * .getExtra())
																 */) {
								app.setCurrtentSize(file.getCurrentBytes());
								app.setTotalSize(file.getTotalBytes());
								// app.setSaveDest(file.getDest());
								app.setStatus(file.getStatus());
								app.setReason(file.getReason());

								// int position =
								// ((DownloadAdapter)mAdapter).getPositionForId(downloadId);
								// View child = listView.getChildAt(position -
								// firstVisiblePosition);
								//
								// 这样也可以，不用刷新整个ListView
								// ((DownloadAdapter)mAdapter).updateItemView(listView);
							}
						}
					}

					// tryAutoInstall(mAdapter.getData());
					sort(mAdapter.getData());
					if (Constants.DEBUG)
						Log.i(TAG, "refresh :data " + mAdapter.getData());
					mAdapter.notifyDataSetChanged();

				} else {
					// mAdapter.setData(new ArrayList<DownloadAppInfo>());
				}
				// mAdapter.clear();
				// mAdapter.addAll(items);

			}
		});
	}

	private List<DownloadAppInfo> sort(List<DownloadAppInfo> apps) {

		List<DownloadAppInfo> successfulList = new ArrayList<DownloadAppInfo>();

		StringBuffer sb = new StringBuffer();
		sb.append("original:");
		for (DownloadAppInfo a : apps) {
			sb.append("[" + a.getPackageName() + "]");
		}
		for (Iterator<DownloadAppInfo> iterator = apps.iterator(); iterator.hasNext();) {
			DownloadAppInfo downloadAppInfo = (DownloadAppInfo) iterator.next();
			DownloadStatus status = downloadAppInfo.getStatus();
			DownloadReason reason = downloadAppInfo.getReason();

			int apkStatus = downloadAppInfo.getApkStatus();

			if (downloadAppInfo != null
					&& (apkStatus == PackageMode.MERGED || apkStatus == PackageMode.INSTALLING || apkStatus == PackageMode.MERGE_FAILED || apkStatus == PackageMode.INSTALL_FAILED
							|| apkStatus == PackageMode.DOWNLOADED || apkStatus == PackageMode.CHECKING_FINISHED)) {
				successfulList.add(downloadAppInfo);
				iterator.remove();
			}
		}

		Collections.sort(successfulList, comparator);
		apps.addAll(successfulList);
		return apps;
	}

	/*
	 * private List<DownloadAppInfo> sort(List<DownloadAppInfo> apps){
	 * List<DownloadAppInfo> pendingList = new ArrayList<DownloadAppInfo>();
	 * List<DownloadAppInfo> pausedList = new ArrayList<DownloadAppInfo>();
	 * List<DownloadAppInfo> successfulList = new ArrayList<DownloadAppInfo>();
	 * List<DownloadAppInfo> failedList = new ArrayList<DownloadAppInfo>();
	 * 
	 * StringBuffer sb = new StringBuffer(); sb.append("original:"); for
	 * (DownloadAppInfo a : apps) { sb.append("["+a.getPackageName()+"]"); } for
	 * (Iterator<DownloadAppInfo> iterator = apps.iterator();
	 * iterator.hasNext();) { DownloadAppInfo downloadAppInfo =
	 * (DownloadAppInfo) iterator .next(); DownloadStatus status =
	 * downloadAppInfo.getStatus(); DownloadReason reason =
	 * downloadAppInfo.getReason(); if(status == DownloadStatus.STATUS_PENDING){
	 * pendingList.add(downloadAppInfo); iterator.remove(); }else if(status ==
	 * DownloadStatus.STATUS_PAUSED){ //if(reason ==
	 * DownloadReason.PAUSED_BY_APP){ pausedList.add(downloadAppInfo); //}
	 * iterator.remove(); }else if(status == DownloadStatus.STATUS_SUCCESSFUL){
	 * successfulList.add(downloadAppInfo); iterator.remove(); }else if(status
	 * == DownloadStatus.STATUS_FAILED){ failedList.add(downloadAppInfo);
	 * iterator.remove(); }
	 * 
	 * } Log.i(TAG,
	 * String.format("running apps:%d,pending:%d,paused:%d,successful:%d,failed:%d"
	 * , apps.size()
	 * ,pendingList.size(),pausedList.size(),successfulList.size(),
	 * failedList.size()));
	 * 
	 * sb.append(" running:"); for (DownloadAppInfo a : apps) {
	 * sb.append("["+a.getPackageName()+"]"); } sb.append(" successful:"); for
	 * (DownloadAppInfo a : successfulList) {
	 * sb.append("["+a.getPackageName()+"]"); } Log.i(TAG,
	 * "running and successful "+sb.toString() );
	 * 
	 * apps.addAll(pendingList); Collections.sort(pausedList, comparator);
	 * apps.addAll(pausedList); Collections.sort(successfulList, comparator);
	 * apps.addAll(successfulList); apps.addAll(failedList); return apps ; }
	 */

	Comparator<DownloadAppInfo> comparator = new Comparator<DownloadAppInfo>() {
		@Override
		public int compare(DownloadAppInfo lhs, DownloadAppInfo rhs) {

			long leftDownloadDate = lhs.getDownloadDate();
			long rightDownloadDate = rhs.getDownloadDate();

			if (leftDownloadDate < rightDownloadDate) {
				return -1;
			} else if (leftDownloadDate > rightDownloadDate) {
				return 1;
			}
			return 0;
		}
	};

	private Dialog confirmDialog;

	AppSilentInstaller installer;

	private Dialog cancleAllDialog;

	private View notifyView;

	private TextView notifyTextView;

	private void logList(List<DownloadConfiguration.DownloadItemOutput> paramAnonymousList) {
		if ((paramAnonymousList == null) || (paramAnonymousList.size() == 0)) {
			if (Constants.DEBUG)
				Log.v("DownloadAppListFragment", "[Download List is empty]");
			return;
		}
		;
		Iterator localIterator = paramAnonymousList.iterator();
		while (localIterator.hasNext()) {
			DownloadConfiguration.DownloadItemOutput localDownloadItemOutput = (DownloadConfiguration.DownloadItemOutput) localIterator.next();
			String str = localDownloadItemOutput.getTitle();
			DownloadConfiguration.DownloadItemOutput.DownloadStatus localDownloadStatus = localDownloadItemOutput.getStatus();
			long l1 = localDownloadItemOutput.getCurrentBytes();
			long l2 = localDownloadItemOutput.getTotalBytes();
			long l3 = localDownloadItemOutput.getDownloadId();
			Object[] arrayOfObject = new Object[5];
			arrayOfObject[0] = Long.valueOf(l3);
			arrayOfObject[1] = str;
			arrayOfObject[2] = localDownloadStatus;
			arrayOfObject[3] = Long.valueOf(l1);
			arrayOfObject[4] = Long.valueOf(l2);
			Log.v("DownloadAppListFragment", String.format("[Download List item:id=%d,title=%s,status=%s,current=%d,total=%d]", arrayOfObject));
		}
	}

	@Override
	public void onClick(View v) {
		List<DownloadAppInfo> data = mAdapter.getData();
		if (data == null) {
			return;
		}
		ArrayList<Long> ids = new ArrayList<Long>();
		switch (v.getId()) {
		case R.id.manager_download_downloadall:
			resumeAllDownload();
			ClickNumStatistics.addManageStartAllClickStatis(getActivity().getApplicationContext());
			break;

		case R.id.manager_download_pauseall:
			// 取消未完成的任务
			for (DownloadAppInfo downloadAppInfo : data) {

				DownloadItemOutput dio = DownloadUtil.getDownloadInfo(GameTingApplication.getAppInstance(), downloadAppInfo.getDownloadId());

				if (null != dio) {
					DownloadStatus status = dio.getStatus();

					if (status != DownloadStatus.STATUS_SUCCESSFUL) {
						ids.add(downloadAppInfo.getDownloadId());
					}
				}
			}
			if (ids.size() == 0) {
				// 没有正在下载
			} else {
				showCancleAllConfirmDialog();
			}

			ClickNumStatistics.addManageCancelAllClickStatis(getActivity().getApplicationContext());
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
		View subView = view.findViewById(R.id.manager_activity_download_list_item_icon);
		onItemIconClick(subView, position);
		return true;
	}

	// /////////////////////////////////////////////////////////////////////////
	private boolean needRequery = false;
	private PackageCallback packageCallback;

	class MyPackageCallback implements PackageCallback {

		@Override
		public void onPackageStatusChanged(PackageMode mode) {

			if (mAdapter == null) {
				needRequery = true;
			} else {
				List<DownloadAppInfo> data = mAdapter.getData();
				if (data == null) {
					return;
				}

				DownloadAppInfo target = null;
				// Find which item need to refresh.
				for (DownloadAppInfo item : data) {
					if (mode.downloadId > 0 && mode.downloadId == item.getDownloadId()) {
						target = item;
						break;
					} else if (mode.gameId != null && mode.gameId.equals(item.getGameId())) {
						target = item;
						break;
					} else if (mode.downloadUrl != null && mode.downloadUrl.equals(item.getDownloadUrl())) {
						target = item;
						break;
					}
				}
				if (target == null) {
					return;
				}

				switch (mode.status) {
				case PackageMode.DOWNLOAD_PENDING:
				case PackageMode.DOWNLOAD_RUNNING:
				case PackageMode.DOWNLOAD_PAUSED:
				case PackageMode.DOWNLOAD_FAILED:
				case PackageMode.DOWNLOADED:
					break;
				case PackageMode.DOWNLOADED_DIFFERENT_SIGN:
					break;
				case PackageMode.MERGING:
					break;
				case PackageMode.MERGE_FAILED:
					// reStartnNormally(mode);
					break;
				case PackageMode.MERGED:

					if (null != mode.reason && mode.reason == CancelReason.CANCEL_UPDATE) {
						deleteDownload(target);
					}
					break;
				case PackageMode.MERGED_DIFFERENT_SIGN:
					break;
				case PackageMode.INSTALLING:
					break;
				case PackageMode.INSTALL_FAILED:
					break;
				case PackageMode.CHECKING:
					break;
				case PackageMode.CHECKING_FINISHED:
					break;
				case PackageMode.INSTALLED:
					getLoaderManager().restartLoader(0, null, DownloadAppListFragment.this);
					return;
				case PackageMode.UNDOWNLOAD:
				case PackageMode.UPDATABLE:
				case PackageMode.UPDATABLE_DIFF:
					deleteDownload(target);
					return;
				default:
					return;
				}

				target.setCurrtentSize(mode.currentSize);
				target.setTotalSize(mode.totalSize);
				target.setApkStatus(mode.status);
				target.setApkReason(mode.reason);

				refreshDownloadProgress(target.getDownloadId());

				refreshList(mode);
				if (DEBUG && mode.status == PackageMode.DOWNLOAD_PAUSED) {
					Log.i(TAG, String.format("[onPackageStatusChanged]DOWNLOAD_PAUSED current:%s total:%s,for %s", target.getCurrtentSize(), target.getTotalSize(), target.getName()));
				}
				if (DEBUG && mode.status == PackageMode.DOWNLOADED) {
					Log.i(TAG, String.format("[onPackageStatusChanged]DOWNLOADED current:%s total:%s,for %s", target.getCurrtentSize(), target.getTotalSize(), target.getName()));
				}
				if (DEBUG && mode.status == PackageMode.MERGING) {
					Log.i(TAG, String.format("[onPackageStatusChanged]MERGING current:%s total:%s,for %s", target.getCurrtentSize(), target.getTotalSize(), target.getName()));
				}
				if (DEBUG && mode.status == PackageMode.MERGED) {
					Log.i(TAG, String.format("[onPackageStatusChanged]MERGED current:%s total:%s,for %s", target.getCurrtentSize(), target.getTotalSize(), target.getName()));
				}
				if (DEBUG && mode.status == PackageMode.MERGE_FAILED) {
					Log.i(TAG, String.format("[onPackageStatusChanged]MERGE_FAILED current:%s total:%s,for %s", target.getCurrtentSize(), target.getTotalSize(), target.getName()));
				}
			}

		}
	}

	/**
	 * 排序
	 */
	private boolean sortForList(long downloadId) {

		List<DownloadAppInfo> data = mAdapter.getData();
		if (data == null) {
			return false;
		}
		int size = data.size();
		DownloadAppInfo target = null;
		for (int i = 0; i < size; i++) {
			DownloadAppInfo downloadAppInfo = data.get(i);
			if (downloadId == downloadAppInfo.getDownloadId()) {
				target = downloadAppInfo;
			}
		}

		if (target != null && (target.getApkStatus() == PackageMode.MERGED || target.getApkStatus() == PackageMode.DOWNLOADED || target.getApkStatus() == PackageMode.CHECKING_FINISHED)) {
			mAdapter.remove(target);
			data.add(target);
			return true;
		}
		return false;
	}

	private void refreshDownloadProgress(final long downloadId) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				try {
					boolean r = sortForList(downloadId);
					if (r) {
						mAdapter.notifyDataSetChanged();
					} else {
						// searchResultAdapter.notifyDataSetChanged();
						// 这样也可以，不用刷新整个ListView
						((DownloadAdapter) mAdapter).updateItemView(listView, downloadId);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void refreshList(final PackageMode mode) {
		this.handler.post(new Runnable() {
			public void run() {
				if (listView == null || mAdapter == null) {
					return;
				}
				int firstVisiblePosition = listView.getFirstVisiblePosition();
				List<DownloadAppInfo> data = mAdapter.getData();
				if (data != null) {
					mAdapter.setNotifyOnChange(false);
					for (DownloadAppInfo app : data) {
						/**
						 * 可能出现包名不同的情形，所以只用downloadid匹配
						 */
						if (app.getDownloadId() == mode.downloadId) {
							app.setCurrtentSize(mode.currentSize);
							app.setTotalSize(mode.totalSize);
							app.setApkStatus(mode.status);
							app.setApkReason(mode.reason);
						}
					}

					sort(mAdapter.getData());
					if (Constants.DEBUG)
						Log.i(TAG, "refresh :data " + mAdapter.getData());
					mAdapter.notifyDataSetChanged();
					mAdapter.setNotifyOnChange(true);

				} else {
					// mAdapter.setData(new ArrayList<DownloadAppInfo>());
				}
				// mAdapter.clear();
				// mAdapter.addAll(items);

			}
		});
	}

	private void registerDownloadListener() {
		if (packageCallback == null) {
			packageCallback = new MyPackageCallback();
			PackageHelper.registerPackageStatusChangeObserver(packageCallback);
		}

	}

	private void unregisterDownloadListener() {
		if (packageCallback == null) {
			PackageHelper.unregisterPackageStatusChangeObserver(packageCallback);
		}

	}
	// /////////////////////////////////////////////////////////////////////////
}
