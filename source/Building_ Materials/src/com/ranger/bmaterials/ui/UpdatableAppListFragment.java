package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.text.format.Formatter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.UpdatableAppListAdapter;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.download.DownloadUtil;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.DownloadCallback;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.mode.UpdatableItem;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.view.DuokuDialog;
import com.ranger.bmaterials.view.PopupWindowCompat;
import com.ranger.bmaterials.work.DBTaskManager;
import com.ranger.bmaterials.work.UpdatableAppLoader;

public class UpdatableAppListFragment extends AbstractAppListFragment<UpdatableAppInfo> implements OnClickListener, OnItemLongClickListener {

	View viewIgnoreView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerListener();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.manager_activity_update_fragment, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		initView();
		try {
			if (savedInstanceState != null) {
				int number = savedInstanceState.getInt("ignoredNumber");
				this.ignoredNumber = number > 0 ? number : 0;
			}
		} catch (Exception e) {
		}

		super.onActivityCreated(savedInstanceState);
		registerStatusChangedListener();
		getLoaderManager().initLoader(1, null, this);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {

		getLoaderManager().restartLoader(0, null, UpdatableAppListFragment.this);
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterListener();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		unregisterStatusChangedListener();
	}

	@Override
	public Loader<List<UpdatableAppInfo>> onCreateLoader(int id, Bundle args) {
		return new UpdatableAppLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<UpdatableAppInfo>> loader, List<UpdatableAppInfo> data) {
		filterData(data);
		super.onLoadFinished(loader, data);
		updateView(true);
		showSaveSize();
		updateCountChanged();
		dismissPopupWindow();
		if (Constants.DEBUG)
			Log.i("updatetest", "onLoadFinished,data:" + ((data != null) ? data.size() : 0));
		if (Constants.DEBUG)
			Log.i("updatetest", "onLoadFinished,adapter count:" + mAdapter.getCount());
	}

	public void dismissPopupWindow() {
		try {
			if (pw != null && pw.isShowing()) {
				pw.getContentView().invalidate();
				pw.dismiss();
			}
			pw = null;
		} catch (Exception e) {
		}
	}

	private void updateCountChanged() {
		ManagerActivity activity = (ManagerActivity) getActivity();

		if (activity != null) {
			activity.updateTitle(1, mAdapter.getCount());
		}
	}

	private void showSaveSize() {
		List<UpdatableAppInfo> data = null;
		if (mAdapter != null && (data = mAdapter.getData()) != null) {
			long totalSize = 0;
			long patchSize = 0;
			long saveSize = 0;
			long increSize = 0;
			for (UpdatableAppInfo u : data) {

				saveSize += (u.getNewSize() - u.getPatchSize());

				if (u.isDiffUpdate()) {
					increSize += u.getPatchSize();
				} else {
					increSize += u.getNewSize();
				}

				totalSize += u.getNewSize();
				patchSize += u.getPatchSize();
			}
			if (saveSize > 0) {

				tvHintRight.setVisibility(View.VISIBLE);
				tvHintRightTotal.setVisibility(View.VISIBLE);

				StringBuffer format = new StringBuffer();
				if (totalSize > 0) {
					tvHintRightTotal.setVisibility(View.VISIBLE);
					format.append(String.format(getString(R.string.update_managment_hint_totalsize), Formatter.formatShortFileSize(getActivity(), totalSize)));
					tvHintRightTotal.setText(format.toString());
				}

				if (patchSize > 0) {
					tvHintRight.setVisibility(View.VISIBLE);
					format = new StringBuffer();
					format.append(String.format(getString(R.string.update_managment_hint_patchsize), Formatter.formatShortFileSize(getActivity(), increSize)));
					tvHintRight.setText(format.toString());
				} else {
					tvHintRight.setVisibility(View.GONE);
				}
			} else {
				tvHintRight.setVisibility(View.INVISIBLE);
				tvHintRightTotal.setVisibility(View.INVISIBLE);
			}

		}

	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BroadcaseSender.ACTION_IGNORED_STATE_CHANGED.equals(action)) {
				boolean status = intent.getBooleanExtra(BroadcaseSender.ARG_IGNORED_STATE, false);
				String[] packages = intent.getStringArrayExtra(BroadcaseSender.ARG_IGNORED_STATE_CHANGED_PACKAGES);
				onIgnoredStatusChanged(status, packages);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("ignoredNumber", ignoredNumber);
		super.onSaveInstanceState(outState);
	}

	public void onIgnoredStatusChanged(boolean ignored, String... packageNames) {
		if (Constants.DEBUG)
			Log.i("UpdatableAppListFragment", "OnAppStatusChangedListener onIgnoredStatusChanged");
		if (!ignored && getActivity() != null) {
			getLoaderManager().restartLoader(0, null, UpdatableAppListFragment.this);
		}

	}

	MyReceiver myReceiver;

	private void registerStatusChangedListener() {
		// if(myReceiver == null){
		// myReceiver = new MyReceiver();
		// IntentFilter intentFilter = new
		// IntentFilter(BroadcaseSender.ACTION_IGNORED_STATE_CHANGED);
		// getActivity().registerReceiver(myReceiver, intentFilter)
		// }

		// AppMananager manager = AppMananager.getInstance(getActivity());
		// manager.addOnAppStatusChangedListener(OnAppStatusChangedListener);
	}

	private void unregisterStatusChangedListener() {
		// if(myReceiver != null){
		// getActivity().unregisterReceiver(myReceiver);
		// myReceiver = null ;
		// }

		// AppMananager manager = AppMananager.getInstance(getActivity());
		// manager.removeOnAppStatusChangedListener(OnAppStatusChangedListener);
	}

	private ListView listView;
	private View progressBar;
	private TextView tvHintPlain;
	private TextView tvHintRightTotal;

    private Button btn_update_all;
    private View view_pager;
    private View hintParent;

	private void initView() {
		ViewGroup parent = (ViewGroup) getView();
		listView = (ListView) parent.findViewById(R.id.manager_activity_update_list);
		((View) listView.getParent()).setVisibility(View.INVISIBLE);

        btn_update_all = (Button) parent.findViewById(R.id.manager_activity_update_update_button);
        view_pager = parent.findViewById(R.id.manager_pager);


		progressBar = parent.findViewById(R.id.manager_update_progressbar);
		progressBar.setVisibility(View.VISIBLE);

		hintParent = parent.findViewById(R.id.manager_activity_update_hint_text);
		
		hintParent.setBackgroundColor(getResources().getColor(R.color.listview_header_background));

		tvHintPlain = (TextView) hintParent.findViewById(R.id.red_notify_plain_text);
		tvHintRight = (TextView) hintParent.findViewById(R.id.red_notify_plain_text_right);
		tvHintRightTotal = (TextView) hintParent.findViewById(R.id.red_notify_plain_text_right_total);
		View updateAllView = parent.findViewById(R.id.manager_activity_update_update_button);
		viewIgnoreView = parent.findViewById(R.id.manager_activity_update_view_button);

		updateAllView.setOnClickListener(this);
		viewIgnoreView.setOnClickListener(this);

		mAdapter = new UpdatableAppListAdapter(getActivity());
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		listView.setEmptyView(parent.findViewById(R.id.update_null));
		mAdapter.setOnListItemClickListener(this);
	}

	private int ignoredNumber = 0;

	private void filterData(List<UpdatableAppInfo> data) {
		try {
			ignoredNumber = 0;
			if (data == null) {
				return;
			}
			Iterator<UpdatableAppInfo> iterator = data.iterator();
			while (iterator.hasNext()) {
				UpdatableAppInfo updatableAppInfo = (UpdatableAppInfo) iterator.next();
				boolean ignoreUpdate = updatableAppInfo.isIgnoreUpdate();
				if (ignoreUpdate) {
					iterator.remove();
					ignoredNumber++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void updateView(boolean showing) {
		View parent = (View) listView.getParent();
		if (showing) {
			if (mAdapter.getCount() > 0) {
				tvHintPlain.setText("有更新" + "(" + mAdapter.getCount() + ")");
                btn_update_all.setEnabled(true);
                hintParent.setVisibility(View.VISIBLE);
			} else {
				tvHintPlain.setText("没有可更新的游戏");
                btn_update_all.setEnabled(false);
                hintParent.setVisibility(View.GONE);
			}
			Button btnShowIgnoreList = (Button) viewIgnoreView;
			if (ignoredNumber > 0) {
                btnShowIgnoreList.setEnabled(true);
				btnShowIgnoreList.setText(getString(R.string.btn_show_ignore_list) + " (" + ignoredNumber + ")");
                view_pager.setVisibility(View.VISIBLE);
			} else {
				btnShowIgnoreList.setText(getString(R.string.btn_show_ignore_list));
                btnShowIgnoreList.setEnabled(false);
                if(mAdapter.getCount() < 1){
                    view_pager.setVisibility(View.GONE);
                    hintParent.setVisibility(View.GONE);
                }
			}

			if (parent.getVisibility() != View.VISIBLE)
				parent.setVisibility(View.VISIBLE);
			if (progressBar.getVisibility() != View.INVISIBLE)
				progressBar.setVisibility(View.GONE);
		} else {
			if (parent.getVisibility() != View.INVISIBLE)
				parent.setVisibility(View.INVISIBLE);
			if (progressBar.getVisibility() != View.VISIBLE)
				progressBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<UpdatableAppInfo>> loader) {
		if (Constants.DEBUG)
			Log.i("UpdatableAppListFragment", "onLoaderReset");
		super.onLoaderReset(loader);
	}

	private UpdatableAppInfo findItem(Intent data) {
		String url = data.getStringExtra(DownloadDialogActivity.ARG2);
		List<UpdatableAppInfo> list = mAdapter.getData();
		if (list == null) {
			return null;
		}
		UpdatableAppInfo item = null;
		for (UpdatableAppInfo d : list) {
			if (d.getDownloadUrl().equals(url)) {
				item = d;
			}
		}
		return item;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == REQ_CODE_RESUME) {
			UpdatableAppInfo item = findItem(data);
			if (item != null) {
				PackageHelper.resumeDownload(item.getDownloadId(), myDownloadCallback);
				DownloadStatistics.addResumeDownloadGameStatistics(getActivity(), item.getName());
			}
		} else if (requestCode == REQ_CODE_RESTART) {
			UpdatableAppInfo item = findItem(data);
			if (item != null) {
				PackageHelper.restartDownload(item.getDownloadId(), myDownloadCallback);
				// DownloadStatistics.addDownloadGameStatistics(getActivity(),
				// item.getName());
			}
		} else if (requestCode == REQ_CODE_DOWNLOAD) {
			UpdatableAppInfo item = findItem(data);
			if (item != null) {
				PackageHelper.download(formDownloadInput(item), myDownloadCallback);
//				DownloadStatistics.addDownloadGameStatistics(getActivity(), item.getName());
			}

		} else if (requestCode == REQ_CODE_DOWNLOAD_MULPUTIL) {
			if (mAdapter == null) {
				return;
			}
			int count = mAdapter.getCount();
			// 没有可更新的游戏
			if (count == 0) {
				return;
			}
			ArrayList<DownloadItemInput> list = new ArrayList<DownloadItemInput>(count);
			for (int i = 0; i < count; i++) {
				list.add(formDownloadInput(mAdapter.getItem(i)));
			}
			PackageHelper.download(list, myDownloadCallback);
			updateCountChanged();
		}
	}

	// update all/view ignore
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.manager_activity_update_update_button:
			updateAll();
			ClickNumStatistics.addManageUpdateAllClickStatis(getActivity().getApplicationContext());
			break;
		case R.id.manager_activity_update_view_button:
			if (mAdapter == null) {
				return;
			}
			if (ignoredNumber > 0) {
				Intent intent = new Intent(getActivity(), IgnoredUpdatableAppsActivity.class);
				startActivity(intent);
			} else {
				// 没有可更新的游戏
			}
			ClickNumStatistics.addManageIgnoreAllClickStatis(getActivity().getApplicationContext());
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		super.onItemClick(parent, view, position, id);
		View subView = view.findViewById(R.id.manager_activity_updatable_list_item_icon);
		onItemIconClick(subView, position);

	}

	@Override
	public void onItemIconClick(View view, int position) {
		super.onItemIconClick(view, position);
		showPopupWindow(view, position);
	}

	// static final int REQ_CODE_SINGLE = 200 ;
	// static final int REQ_CODE_MULPUTIL = 300 ;

	static final int REQ_CODE_DOWNLOAD = 99;
	static final int REQ_CODE_DOWNLOAD_MULPUTIL = 100;
	static final int REQ_CODE_RESUME = 101;
	static final int REQ_CODE_RESTART = 102;

	@Override
	public void onItemButtonClick(View view, int position) {
		super.onItemButtonClick(view, position);
		final UpdatableAppInfo item = mAdapter.getItem(position);
		int apkStatus = item.getApkStatus();

		long donwloadid = -1;

		if (apkStatus == PackageMode.DOWNLOAD_RUNNING || apkStatus == PackageMode.DOWNLOAD_PAUSED || apkStatus == PackageMode.DOWNLOADED
				|| apkStatus == PackageMode.DOWNLOAD_PENDING) {
			donwloadid = item.getDownloadId();

			if (item.getDownloadId() < 0) {
				AppManager aManager = AppManager.getInstance(getActivity().getApplicationContext());
				DownloadAppInfo dai = aManager.getDownloadGameForId(item.getGameId() + "", false);
				if(null != dai)
					donwloadid = dai.getDownloadId();
				else
					return;
			}
		}

		switch (apkStatus) {
		case PackageMode.UNDOWNLOAD:
			//
			if (!checkSdCard()) {
				return;
			}
			return;
		case PackageMode.DOWNLOAD_PENDING:
		case PackageMode.DOWNLOAD_RUNNING:
			PackageHelper.pauseDownloadGames(donwloadid);
			break;
		case PackageMode.DOWNLOAD_PAUSED:
			if (!checkSdCard()) {
				break;
			}

			item.setDownloadId(donwloadid);
			resumeDownload(position, item);
			break;
		case PackageMode.DOWNLOAD_FAILED:
			if (!checkSdCard()) {
				break;
			}

			restartDownload(position, item);
			break;
		case PackageMode.DOWNLOADED:
			if (item.isDiffUpdate()) {
				PackageHelper.sendMergeRequestFromUI(donwloadid);
				// 增量更新，不做事情(应该后续会有MERGING)
				Log.e(TAG, String.format("%s is downloded(is diff update),but user is clicked!", item.getName()));
			} else {
				// 普通更新或者普通下载，安装
				PackageHelper.installApp(UpdatableAppListFragment.this.getActivity(), item.getGameId(), item.getPackageName(), item.getSaveDest());
			}
			break;
		case PackageMode.DOWNLOADED_DIFFERENT_SIGN:
		case PackageMode.MERGED_DIFFERENT_SIGN:
			return;
		case PackageMode.MERGING:
			// 正在合并，不做事情
			Log.e(TAG, String.format("%s is merging,but user is clicked!", item.getName()));
			break;
		case PackageMode.MERGED:
			if (!item.isDiffUpdate()) {
				Log.e(TAG, String.format("%s is merged,but is not diff update", item.getName()));
			}
			PackageHelper.installApp(UpdatableAppListFragment.this.getActivity(), item.getGameId(), item.getPackageName(), item.getSaveDest());

			if (DEBUG) {
				Log.e(TAG, item.getName() + " is merged:" + item);
			}
			break;

		case PackageMode.CHECKING_FINISHED:
			PackageHelper.installApp(UpdatableAppListFragment.this.getActivity(), item.getGameId(), item.getPackageName(), item.getSaveDest());

			if (DEBUG) {
				Log.e(TAG, item.getName() + " is CHECKING_FINISHED:" + item);
			}
			break;

		case PackageMode.MERGE_FAILED:
			if (!checkSdCard()) {
				break;
			}
			reMergeApp(item);
			break;
		case PackageMode.INSTALLING:
			Log.e(TAG, String.format("%s is installing,but user can clicked", item.getName()));
			break;
		case PackageMode.INSTALL_FAILED:
			PackageHelper.installApp(UpdatableAppListFragment.this.getActivity(), item.getGameId(), item.getPackageName(), item.getSaveDest());
			break;
		case PackageMode.INSTALLED:
			// openGame(item);
			break;
		case PackageMode.UPDATABLE:
			if (!checkSdCard()) {
				break;
			}

			download(position, item);
			break;
		case PackageMode.UPDATABLE_DIFF:
			if (!checkSdCard()) {
				break;
			}

			download(position, item);
			break;
		default:
			break;
		}
	}

	MyDownloadCallback myDownloadCallback = new MyDownloadCallback();

	class MyDownloadCallback implements DownloadCallback {

		private UpdatableAppInfo findTarget(String url) {
			if (mAdapter == null || mAdapter.getData() == null) {
				if (DEBUG) {
					Log.d(TAG, String.format("DownloadCallback.findTarget return null searchResultAdapter is null or data is null for:%s ", url));
				}
				return null;
			}

			List<UpdatableAppInfo> data = mAdapter.getData();
			int size = data.size();
			UpdatableAppInfo target = null;
			for (int i = 0; i < size; i++) {
				UpdatableAppInfo item = data.get(i);
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
			UpdatableAppInfo target = findTarget(downloadUrl);
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
			UpdatableAppInfo target = findTarget(url);
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
			UpdatableAppInfo target = findTarget(downloadUrl);
			if (target == null) {
				return;
			}
		}

	}

	private boolean checkNetwork(int position, UpdatableAppInfo item, int reqCode) {

		boolean networkAvailable = DeviceUtil.isNetworkAvailable(getActivity());
		if (!networkAvailable) {
			CustomToast.showToast(getActivity(), getString(R.string.alert_network_inavailble));
			return false;
		}

		Integer activeNetworkType = DeviceUtil.getActiveNetworkType(getActivity());
		if (MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
			if (activeNetworkType != null && activeNetworkType == ConnectivityManager.TYPE_MOBILE) {

				DuokuDialog.showNetworkAlertDialog(UpdatableAppListFragment.this, reqCode, item.getPackageName(), item.getDownloadUrl(), position);
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

				DuokuDialog.showNetworkAlertDialog(UpdatableAppListFragment.this, reqCode, null, null, -1);
				return false;
			}
		}
		return true;
	}

	private void updateAll() {
		if (mAdapter == null) {
			return;
		}
		int count = mAdapter.getCount();
		// 没有可更新的游戏
		if (count == 0) {
			return;
		}
		boolean checkNetwork = checkNetwork(REQ_CODE_DOWNLOAD_MULPUTIL);
		if (checkNetwork) {
			ArrayList<DownloadItemInput> list = new ArrayList<DownloadItemInput>(count);
			for (int i = 0; i < count; i++) {
				// list.add(formDownloadInput(mAdapter.getItem(i)));
				DownloadItemInput dii = formDownloadInput(mAdapter.getItem(i));

				DownloadItemOutput di = DownloadUtil.getDownloadInfo(getActivity().getApplicationContext(), dii.getDownloadUrl());

				if (null != di) {
					if (di.getStatus() == DownloadStatus.STATUS_PAUSED || di.getStatus() == DownloadStatus.STATUS_PENDING) {
						PackageHelper.resumeDownload(myDownloadCallback, di.getDownloadId());
					} else {
						PackageHelper.download(dii, myDownloadCallback);
					}
				} else {
					PackageHelper.download(dii, myDownloadCallback);
				}
			}
			updateCountChanged();
		}
	}

	private DownloadItemInput formDownloadInput(UpdatableAppInfo item) {
		if (!item.isDiffUpdate()) {
			DownloadItemInput downloadItemInput = new DownloadItemInput(item.getIconUrl(), item.getGameId(), item.getPackageName(), item.getName(), item.getName(),
					item.getNewVersionInt(), item.getNewVersion(), item.getDownloadUrl(), null, item.getNewSize(), null, -1, item.getExtra(), item.isNeedLogin(), false);
			return downloadItemInput;
		} else {
			DownloadItemInput downloadItemInput = new DownloadItemInput(item.getIconUrl(), item.getGameId(), item.getPackageName(), item.getName(), item.getName(),
					item.getNewVersionInt(), item.getNewVersion(), item.getPatchUrl(), null, item.getPatchSize(), null, -1, item.getExtra(), item.isNeedLogin(), true);
			return downloadItemInput;
		}
	}

	private void download(int position, UpdatableAppInfo item) {
		boolean checkNetwork = checkNetwork(position, item, UpdatableAppListFragment.REQ_CODE_DOWNLOAD);
		if (!checkNetwork) {
			return;
		}
		PackageHelper.download(formDownloadInput(item), myDownloadCallback);
//		DownloadStatistics.addDownloadGameStatistics(getActivity(), item.getName());
	}

	private void resumeDownload(int position, UpdatableAppInfo item) {
		boolean checkNetwork = checkNetwork(position, item, UpdatableAppListFragment.REQ_CODE_RESUME);
		if (!checkNetwork) {
			return;
		}
		/*
		 * if (DEBUG) { logForResumeDownload(item); }
		 */
		PackageHelper.resumeDownload(item.getDownloadId(), myDownloadCallback);
		DownloadStatistics.addResumeDownloadGameStatistics(getActivity(), item.getName());
	}

	private void reMergeApp(UpdatableAppInfo item) {
		/**
		 * 增量更新失败次数过多,走普通更新
		 */
		// if (Constants.mergeFailedCountMap.containsKey(item.getGameId()) &&
		// Constants.mergeFailedCountMap.get(item.getGameId()) >= 1) {
		// UpdatableAppInfo updatableGame =
		// AppManager.getInstance(getActivity()).getUpdatableGame(item.getPackageName());
		// if (updatableGame == null) {
		// }
		// // 注意与formDownloadInput的区别
		// item.setDiffUpdate(false);
		// DownloadItemInput downloadItemInput = new
		// DownloadItemInput(item.getIconUrl(), item.getGameId(),
		// item.getPackageName(), item.getName(), item.getName(),
		// item.getVersionInt(), item.getVersion(),
		// updatableGame.getDownloadUrl(), null, updatableGame.getNewSize(),
		// null, -1, item.getExtra(), item.isNeedLogin(), false);
		//
		// PackageHelper.restartDownloadNormally(item.getDownloadId(),
		// downloadItemInput, myDownloadCallback);
		// } else {
		// }
		PackageHelper.sendMergeRequestFromUI(item.getDownloadId());
	}

	private void restartDownloadNormally(PackageMode mode) {
		if (mAdapter == null) {
			return;
		}
		List<UpdatableAppInfo> data = mAdapter.getData();
		if (data == null) {
			return;
		}
		UpdatableAppInfo item = null;
		String gameId = mode.gameId;
		for (UpdatableAppInfo i : data) {
			if (gameId != null && gameId.equals(i.getGameId())) {
				item = i;
				break;
			}
		}
		if (item != null && Constants.mergeFailedCountMap.containsKey(item.getGameId()) && Constants.mergeFailedCountMap.get(item.getGameId()) >= 2) {
			DownloadItemInput formDownloadInput = formDownloadInput(item);
			// formDownloadInput.setDownloadUrl(item.getPatchUrl());
			formDownloadInput.setDownloadUrl(item.getDownloadUrl());
			formDownloadInput.setSize(item.getPatchSize());
			formDownloadInput.setSize(item.getNewSize());

			PackageHelper.download(formDownloadInput, myDownloadCallback);
			PackageHelper.restartDownloadNormally(item.getDownloadId(), formDownloadInput, myDownloadCallback);
		} else {
			// PackageHelper.sendMergeRequest(item.getDownloadId());
		}
	}

	private void restartDownload(int position, UpdatableAppInfo item) {

		boolean checkNetwork = checkNetwork(position, item, UpdatableAppListFragment.REQ_CODE_RESTART);
		if (!checkNetwork) {
			return;
		}

		PackageHelper.restartDownload(item.getDownloadId(), myDownloadCallback);
		// 统计下载
		// DownloadStatistics.addDownloadGameStatistics(getApplicationContext(),item.getGameName());
	}

	OnClickListener popupWindowOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				dismissPopupWindow();
				View parent = (View) v.getParent();
				final Integer p = (Integer) parent.getTag();
				final UpdatableAppInfo item = mAdapter.getItem(p);

				final DownloadItemOutput downloadinfo = DownloadUtil.getDownloadInfo(getActivity().getApplicationContext(),
						item.isDiffUpdate() ? item.getPatchUrl() : item.getDownloadUrl());

				switch (v.getId()) {
				case R.id.manager_update_popupwindow_ignore:

					switch (item.getApkStatus()) {
					case PackageMode.DOWNLOAD_PENDING:
					case PackageMode.DOWNLOAD_PAUSED:
					case PackageMode.DOWNLOAD_RUNNING:

						DBTaskManager.submitTask(new Runnable() {

							@Override
							public void run() {

								long downloadid = -1;
								if (null != downloadinfo)
									downloadid = downloadinfo.getDownloadId();
								PackageHelper.removeDownloadGames(downloadid);
							}
						});
						break;
					case PackageMode.UPDATABLE:
					case PackageMode.UPDATABLE_DIFF:
						ignoreListItem(item);
						break;
					case PackageMode.DOWNLOADED:
					case PackageMode.CHECKING_FINISHED:
					case PackageMode.MERGED:
					case PackageMode.MERGE_FAILED:
					case PackageMode.DOWNLOAD_FAILED:
						long downloadid = -1;
						if (null != downloadinfo)
							downloadid = downloadinfo.getDownloadId();

						PackageHelper.removeDownloadUpdateGames(downloadid);

						break;

					default:
						break;
					}
					updateView(true);
					updateCountChanged();

					break;

				case R.id.manager_update_popupwindow_detail:
					AppManager manager = AppManager.getInstance(getActivity());
                    manager.jumpToDetail(getActivity(), item.getGameId(), item.getName(), item.getPackageName(), false, String.valueOf(item.getVersionInt()), item.getVersion());

					break;
				}

			} catch (Exception e) {
			}

		}

		private void ignoreListItem(final UpdatableAppInfo item) {
			DBTaskManager.submitTask(new Runnable() {
				@Override
				public void run() {
					AppManager manager = AppManager.getInstance(getActivity());
					manager.updateIgnoreState(true, item.getPackageName());
				}
			});
			mAdapter.remove(item);
			ignoredNumber++;
		}
	};

	private boolean isDownloadingOrMerging(UpdatableAppInfo item) {
		int status = item.getApkStatus();
		switch (status) {
		case PackageMode.UNDOWNLOAD:
		case PackageMode.UPDATABLE:
		case PackageMode.UPDATABLE_DIFF:
			return false;
		case PackageMode.DOWNLOAD_PENDING:
		case PackageMode.DOWNLOAD_PAUSED:
		case PackageMode.DOWNLOAD_RUNNING:
		case PackageMode.DOWNLOAD_FAILED:
		case PackageMode.DOWNLOADED:
		case PackageMode.MERGING:
		case PackageMode.MERGE_FAILED:
		case PackageMode.MERGED:
		case PackageMode.INSTALLING:
		case PackageMode.INSTALL_FAILED:
			// case PackageMode.INSTALLED:
			return true;
		default:
			break;
		}
		return false;
	}

	PopupWindow pw;

	private void showPopupWindow(View view, int position) {

		View contentView = null;
		boolean showingTowardUp = showingTowardUp(view);
		if (!showingTowardUp) {
			contentView = View.inflate(getActivity(), R.layout.manager_update_popupwindow, null);
		} else {
			contentView = View.inflate(getActivity(), R.layout.manager_update_popupwindow_up, null);
		}
		UpdatableAppInfo item = mAdapter.getItem(position);

		contentView.setTag(position);

		/** 修改不同状态下的按钮提示 **/

		TextView ignoreView = (TextView) contentView.findViewById(R.id.manager_update_popupwindow_ignore);
		View detailView = contentView.findViewById(R.id.manager_update_popupwindow_detail);

		ignoreView.setVisibility(View.VISIBLE);

		switch (item.getApkStatus()) {
		case PackageMode.DOWNLOAD_RUNNING:
		case PackageMode.DOWNLOAD_PENDING:
			// Downloading.
			ignoreView.setText(getString(R.string.label_cancel));
			break;
		case PackageMode.DOWNLOAD_PAUSED:
			ignoreView.setText(getString(R.string.label_cancel));
		case PackageMode.UPDATABLE:
			break;
		case PackageMode.DOWNLOADED:
		case PackageMode.CHECKING_FINISHED:
			ignoreView.setText(getString(R.string.label_delete));
			break;
		case PackageMode.MERGING:
		case PackageMode.CHECKING:
		case PackageMode.INSTALLING:
			ignoreView.setVisibility(View.GONE);
			ignoreView.setEnabled(false);
			break;
		case PackageMode.MERGE_FAILED:
			ignoreView.setText(R.string.label_delete);
			ignoreView.setEnabled(true);
			break;
		case PackageMode.DOWNLOAD_FAILED:
			ignoreView.setText(R.string.label_cancel);
			ignoreView.setEnabled(true);
			break;
		default:
			break;
		}

		/** 修改不同状态下的按钮提示 **/

		ignoreView.setOnClickListener(popupWindowOnClickListener);

		detailView.setOnClickListener(popupWindowOnClickListener);
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

		if (!showingTowardUp(view)) {
			pw.setAnimationStyle(R.style.popup_down_animation);
			pw.showAsDropDown(view, horizontalMargin, verticalMargin);
		} else {
			pw.setAnimationStyle(R.style.popup_up_animation);
			// pw.showAsDropDown(view,0, -view.getMeasuredHeight());
			int[] screenWH = DeviceUtil.getScreensize(getActivity());
			pw.showAtLocation(view, Gravity.BOTTOM | Gravity.LEFT, location[0] + horizontalMargin, screenWH[1] - location[1] + verticalMargin);
		}
	}

	private boolean showingTowardUp(View iconView) {
		ListView listView = (ListView) iconView.getParent().getParent().getParent().getParent();
		int[] location = new int[2];
		listView.getLocationOnScreen(location);

		int listViewCenterY = listView.getHeight() / 2 + location[1];
		iconView.getLocationOnScreen(location);
		int iconY = location[1];

		return (iconY + iconView.getHeight()) > listViewCenterY;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
		View subView = view.findViewById(R.id.manager_activity_updatable_list_item_icon);
		onItemIconClick(subView, position);
		return true;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private boolean needRequery = false;
	private PackageCallback packageCallback;
	boolean DEBUG = true;
	static String TAG = "UpdatableAppListFragment";

	private void registerListener() {
		if (packageCallback == null) {
			packageCallback = new MyPackageCallback();
			PackageHelper.registerPackageStatusChangeObserver(packageCallback);
		}
	}

	private void unregisterListener() {
		if (packageCallback == null) {
			PackageHelper.unregisterPackageStatusChangeObserver(packageCallback);
		}

	}

	class MyPackageCallback implements PackageCallback {

		@Override
		public void onPackageStatusChanged(PackageMode mode) {

			if (mAdapter == null) {
				needRequery = true;
			} else {
				List<UpdatableAppInfo> data = mAdapter.getData();
				if (data == null) {
					return;
				}
				UpdatableAppInfo target = null;
				UpdatableItem updatableItem = null;
				for (UpdatableAppInfo item : data) {
					if (mode.downloadId > 0 && mode.downloadId == item.getDownloadId()) {
						target = item;
						break;
					} else if (mode.gameId != null && mode.gameId.equals(item.getGameId())) {
						target = item;
						break;
					}/*
					 * else if(mode.downloadUrl != null &&
					 * mode.downloadUrl.equals(item.getDownloadUrl())){ target =
					 * item ; break; }
					 */
				}
				if (target == null) {
					return;
				}
				
				switch (mode.status) {
				case PackageMode.DOWNLOAD_PENDING:
					// Progress maybe not set?
					// TODO
					target.setApkStatus(PackageMode.DOWNLOAD_PENDING);
					if (mode.totalSize > 0) {
						target.setCurrtentSize(mode.currentSize);
						target.setTotalSize(mode.totalSize);
					}
					if (mode.downloadDest != null) {
						target.setSaveDest(mode.downloadDest);
					}

					if (DEBUG) {
						Log.i(TAG, "Download pending,mode " + mode);
					}
					break;
				case PackageMode.DOWNLOAD_RUNNING:
					target.setApkStatus(PackageMode.DOWNLOAD_RUNNING);
					target.setCurrtentSize(mode.currentSize);
					target.setTotalSize(mode.totalSize);
					break;
				case PackageMode.DOWNLOAD_PAUSED:
					target.setApkStatus(PackageMode.DOWNLOAD_PAUSED);
					target.setApkReason(mode.reason);
					if (DEBUG) {
						Log.i(TAG, "Download Paused,reason " + mode.reason);
					}
					break;
				case PackageMode.DOWNLOAD_FAILED:
					target.setApkStatus(PackageMode.DOWNLOAD_FAILED);
					target.setApkReason(mode.reason);
					if (DEBUG) {
						Log.i(TAG, "Download Failed,reason " + mode.reason);
					}
					break;
				case PackageMode.DOWNLOADED:
					if (mode.isDiffDownload) {
						target.setApkStatus(PackageMode.DOWNLOADED);
					} else {
						target.setApkStatus(PackageMode.DOWNLOADED);
					}
					if (DEBUG) {
						Log.i(TAG, "Download finished,mode " + mode);
						Log.i(TAG, "Download finished,item " + target);
					}
					break;
				case PackageMode.DOWNLOADED_DIFFERENT_SIGN:
				case PackageMode.MERGED_DIFFERENT_SIGN:
					break;
				case PackageMode.MERGING:
					target.setApkStatus(PackageMode.MERGING);
					break;
				case PackageMode.MERGE_FAILED:
					target.setApkStatus(PackageMode.MERGE_FAILED);
					target.setApkReason(mode.reason);
					break;
				case PackageMode.MERGED:
					target.setApkStatus(PackageMode.MERGED);
					break;
				case PackageMode.INSTALLING:
					target.setApkStatus(PackageMode.INSTALLING);
					break;
				case PackageMode.INSTALL_FAILED:
					target.setApkStatus(PackageMode.INSTALL_FAILED);
					target.setApkReason(mode.reason);
					break;
				case PackageMode.INSTALLED:
					target.setApkStatus(PackageMode.INSTALLED);
					break;
				case PackageMode.CHECKING:
				case PackageMode.CHECKING_FINISHED:
					target.setApkStatus(mode.status);
					break;
				case PackageMode.UNDOWNLOAD:
				case PackageMode.UPDATABLE:
				case PackageMode.UPDATABLE_DIFF:
					target.setApkStatus(mode.status);
					refreshDownloadProgress(target.getGameId());
					return;
				default:
					return;
				}

				target.setCurrtentSize(mode.currentSize);
				target.setTotalSize(mode.totalSize);
				target.setApkStatus(mode.status);
				target.setApkReason(mode.reason);

				refreshDownloadProgress(target.getGameId());
				if (DEBUG) {
					Log.i(TAG, String.format("[refreshDownloadProgress]current:%s,total:%s for :%s", target.getCurrtentSize(), target.getTotalSize(), target.getName()));
				}
			}

		}
	}

	public void increaseMergeFailedCount(String gameid) {
		if (null != Constants.mergeFailedCountMap) {
			int count = 0;
			if (Constants.mergeFailedCountMap.containsKey(gameid)) {
				count = Constants.mergeFailedCountMap.get(gameid);
			}
			Constants.mergeFailedCountMap.put(gameid, count + 1);
		}
	}

	private boolean needToRefresh(String gameId) {
		List<UpdatableAppInfo> data = mAdapter.getData();
		if (data == null) {
			return false;
		}
		int size = data.size();
		UpdatableAppInfo target = null;
		for (int i = 0; i < size; i++) {
			UpdatableAppInfo downloadAppInfo = data.get(i);
			if (gameId == downloadAppInfo.getGameId()) {
				target = downloadAppInfo;
			}
		}
		if (target == null) {
			return false;
		}

		int apkStatus = target.getApkStatus();
		switch (apkStatus) {
		case PackageMode.UNDOWNLOAD:
		case PackageMode.DOWNLOAD_RUNNING:
		case PackageMode.INSTALLED:
			return true;
		case PackageMode.UPDATABLE:
		case PackageMode.UPDATABLE_DIFF:
			return true;
		default:
			break;
		}

		return false;
	}

	Handler handler = new Handler();
	private TextView tvHintRight;

	private void refreshDownloadProgress(final String gameId) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				try {

					if (needToRefresh(gameId)) {
						mAdapter.notifyDataSetChanged();
					} else {
						// searchResultAdapter.notifyDataSetChanged();
						// 这样也可以，不用刷新整个ListView
						((UpdatableAppListAdapter) mAdapter).updateItemView(listView, gameId);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}