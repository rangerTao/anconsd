package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.StartGame;
import com.ranger.bmaterials.app.AppManager.GameStatus;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.download.DownloadUtil;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemListener;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.mode.ActivityDetail;
import com.ranger.bmaterials.mode.ActivityInfo;
import com.ranger.bmaterials.mode.DownloadCallback;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.mode.ActivityDetail.ActivityItem;
import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.tools.DateUtil;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.install.InstallPacket;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;
import com.ranger.bmaterials.view.DuokuDialog;

public class ActivityDetailActivity extends SquareDetailBaseActivity {
	private TextView appraisal_title_text;
	private TextView appraisal_version_text;
	private TextView appraisal_source_text;
	private TextView appraisal_pkgsize_text;
	private boolean isfromrec = false;
	private ImageView search_item_action_iv;
	private ImageView search_item_comingsoon_iv;
	private TextView search_item_action_tv;
	private static final String TAG = "ActivityDetailActivity";
	protected void loadData() {
		NetUtil.getInstance().requestForActivityDetail(gameId, activityId, new RequestContentListener());
		registerListener();
		registerReceiver();
	}

	@Override
	protected void initTitleBar() {
		super.initTitleBar();
		TextView titleText = (TextView) findViewById(R.id.header_title);
		titleText.setText(R.string.square_activity_detail_title);
	}

	@Override
	protected void check() {

		Intent intent = getIntent();
		ActivityInfo info = (ActivityInfo) intent.getSerializableExtra(SquareDetailBaseActivity.ARG_DETAIL);
		this.gameId = info.getGameId();
		this.activityId = info.getId();
		this.isfromrec = intent.getBooleanExtra("isfromrec", false);
		if (isfromrec) {
			MainHallActivity.JUMP_TO_TAB_EXTRA = 3;
		}
		if (info == null || activityId == null) {
			finish();
			return;
		}
	}

	@Override
	protected void fillData(BaseResult d) {
		try {
			super.fillData(d);
			data = (ActivityDetail) d;
			if(TextUtils.isEmpty(data.getId())){
				fillEmptyData(R.string.none_activity_data);
				return;
			}
			titleTv.setText(StringUtil.convertEscapeString(data.getActtitle()));
			timeTv.setText(DateUtil.formatDate(new Date(data.getTime())));
			List<ActivityItem> list = data.getData();

			int size = list.size();

			String html = "";
			for (int i = 0; i < size; i++) {

				ActivityItem item = list.get(i);
				html += StringUtil.convertEscapeString(item.getActivityContent());

			}
			appraisal_source_text.setText(data.getActsource());
			detailLayout.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
			if (data.item != null && !TextUtils.isEmpty(gameId=data.item.getGameId())&&(!getPackageName().equals(data.item.getPackageName()))) {
				gameName = data.item.getGameName();
                String iconUrl = data.item.getIconUrl();
				ImageLoaderHelper.displayImage(iconUrl, iconIv);
				iconIv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						AppManager manager = AppManager.getInstance(getApplicationContext());
                        manager.jumpToDetail(ActivityDetailActivity.this, gameId, gameName, null, false);
                    }
				});

				appraisal_title_text.setText(data.item.getGameName());
				appraisal_version_text.setText("版本  " + 1.0);
				appraisal_pkgsize_text.setText(StringUtil.getDisplaySize(data.item.getPackageSize()));

				View game_detail_button_container = findViewById(R.id.game_detail_button_container);
				game_detail_button_container.setVisibility(View.VISIBLE);
				TranslateAnimation tAnimation = new TranslateAnimation(0.0f, 0.0f, 100.f, 0.0f);
				tAnimation.setDuration(200);
				tAnimation.setInterpolator(new DecelerateInterpolator());
				game_detail_button_container.setAnimation(tAnimation);
				checkStatus();
				updateView();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_item_action_layout:
			onGameItemClick();
			ClickNumStatistics.addSquareActivityDetailDownloadStatistics(this);
			break;

		default:
			super.onClick(v);
			break;
		}
	}

	@Override
	protected int getLayout() {
		return R.layout.appraisal_activity_detail;
	}

	@Override
	protected void initView() {
		super.initView();
		appraisal_title_text = (TextView) findViewById(R.id.appraisal_title_text);
		appraisal_version_text = (TextView) findViewById(R.id.appraisal_version_text);
		appraisal_source_text = (TextView) findViewById(R.id.appraisal_source_text);
		appraisal_pkgsize_text = (TextView) findViewById(R.id.appraisal_pkgsize_text);
		search_item_action_iv = (ImageView) findViewById(R.id.search_item_action_iv);
		search_item_comingsoon_iv = (ImageView) findViewById(R.id.search_item_comingsoon_iv);
		search_item_action_tv = (TextView) findViewById(R.id.search_item_action_tv);
		findViewById(R.id.search_item_action_layout).setOnClickListener(this);
	}


	private void updateView() {

		SearchItem item = data.item;
//		ImageLoaderHelper.displayImage(item.getIconUrl(), holder.icon);
		/**
		 * 默认的情形
		 */
		setDefaultViewStatus();

		boolean pendingOnLine = item.isPendingOnLine();
		/**
		 * 即将上线的情形
		 */
		if (pendingOnLine) {
			View p = (View) search_item_action_iv.getParent();
			p.setVisibility(View.GONE);
			search_item_comingsoon_iv.setVisibility(View.VISIBLE);
			return;
		}

		updateDownloadStatus();

		// holder.labeview.setLabelColor("#F86D22");
		// holder.labeview.setText("首发");
		// holder.labeview.setVisibility(View.VISIBLE);
//		if (item.labelName != null && !item.labelName.equals("")) {
//			holder.labeview.setText(item.labelName);
//			holder.labeview.setLabelColor(item.labelColor);
//			holder.labeview.setVisibility(View.VISIBLE);
//		} else {
//			holder.labeview.setVisibility(View.GONE);
//		}
	}
	private void setDefaultViewStatus() {
		View p = (View) search_item_action_iv.getParent();
		if (!p.isEnabled()) {
			p.setEnabled(true);
		}
		search_item_action_iv.setImageResource(R.drawable.btn_download_selector);
		search_item_action_tv.setText(R.string.download);
	};
	private void updateDownloadStatus() {
		View p = (View) search_item_action_iv.getParent();
		p.setEnabled(true);
		boolean diffUpdate = data.item.isDiffDownload();
		int progressValue = 0;
		int apkStatus = data.item.getApkStatus();
		String formatString = "%d%%";

		switch (apkStatus) {
		case PackageMode.UNDOWNLOAD:
			search_item_action_tv.setVisibility(View.GONE);
			search_item_action_iv.setImageResource(R.drawable.btn_download_selector);
			break;
		case PackageMode.INSTALLED:
			search_item_action_tv.setText(R.string.open);
			search_item_action_iv.setImageResource(R.drawable.btn_download_launch_selector);
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.UPDATABLE:
			search_item_action_tv.setText(R.string.update);
			search_item_action_iv.setImageResource(R.drawable.btn_download_update_selector);
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.UPDATABLE_DIFF:
			search_item_action_tv.setText(R.string.update_diff);
			search_item_action_iv.setImageResource(R.drawable.btn_download_diff_update_selector);
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.DOWNLOAD_PENDING:
			search_item_action_iv.setImageResource(R.drawable.btn_download_pending_selector);
			search_item_action_tv.setText(R.string.label_waiting);
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.DOWNLOAD_RUNNING:
			search_item_action_iv.setImageResource(R.drawable.btn_download_pause_selector);
			progressValue = getProgressValue(data.item.getTotalBytes(), data.item.getCurrentBytes());
			search_item_action_tv.setText(String.format(formatString, progressValue));
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.DOWNLOAD_FAILED:
			search_item_action_iv.setImageResource(R.drawable.btn_download_retry_selector);
			search_item_action_tv.setText(R.string.try_again);
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.DOWNLOAD_PAUSED:
			search_item_action_iv.setImageResource(R.drawable.btn_download_resume_selector);
			search_item_action_tv.setText(R.string.resume);

			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.DOWNLOADED:
			search_item_action_iv.setImageResource(R.drawable.btn_download_resume_selector);
			search_item_action_tv.setText(R.string.install);
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.MERGING:
			// 合并中
			p.setEnabled(false);
			search_item_action_iv.setImageResource(R.drawable.icon_checking_list);
			search_item_action_tv.setText("安全检查中");
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.MERGE_FAILED:
			// 合并失败，重新普通更新下载(要给出提示,不能删除原来的，更新即可)
			search_item_action_iv.setImageResource(R.drawable.btn_download_retry_selector);
			search_item_action_tv.setText(R.string.try_again);
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.MERGED:
			// 合并成功，并且签名一致（后台自动安装）
			search_item_action_iv.setImageResource(R.drawable.btn_download_retry_selector);
			search_item_action_tv.setText(R.string.install);
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.CHECKING:
			p.setEnabled(false);
			search_item_action_iv.setImageResource(R.drawable.icon_checking_list);
			search_item_action_tv.setText("安全检查中");
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;

		case PackageMode.CHECKING_FINISHED:
			search_item_action_iv.setImageResource(R.drawable.btn_download_install_selector);
			search_item_action_tv.setText(R.string.install);
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;

		case PackageMode.INSTALLING:
			p.setEnabled(false);
			search_item_action_iv.setImageResource(R.drawable.installing);
			search_item_action_tv.setText(R.string.installing);
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.INSTALL_FAILED:
			search_item_action_iv.setImageResource(R.drawable.btn_download_install_selector);
			search_item_action_tv.setText(R.string.install);
			search_item_action_tv.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

	};
	public int getProgressValue(long total, long current) {
		if (total <= 0)
			return 0;
		return (int) (100L * current / total);
	}
	public void onGameItemClick() {
		int apkStatus = data.item.getApkStatus();

		switch (apkStatus) {
		case PackageMode.UNDOWNLOAD:
			if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				CustomToast.showToast(this, getResources().getString(R.string.error_checksdcard));
				return;
			}
			download();
			// 统计下载
			// DownloadStatistics.addDownloadGameStatistics(
			// getApplicationContext(), data.item.getGameName());
			break;
		case PackageMode.DOWNLOAD_PENDING:
		case PackageMode.DOWNLOAD_RUNNING:
			pauseDownload();
			break;
		case PackageMode.DOWNLOAD_PAUSED:
			resumeDownload();
			break;
		case PackageMode.DOWNLOAD_FAILED:
			restartDownload();
			break;
		case PackageMode.DOWNLOADED:
			if (data.item.isDiffDownload()) {
				// 增量更新，不做事情(应该后续会有MERGING)
				Log.e(TAG, String.format("%s is downloded(is diff update),but user is clicked!", data.item.getGameName()));
			} else {
				// 普通更新或者普通下载，安装
				installApp();
			}
			break;

		case PackageMode.MERGING:
			// 正在合并，不做事情
			Log.e(TAG, String.format("%s is merging,but user is clicked!", data.item.getGameName()));
			break;
		case PackageMode.MERGED:
			if (!data.item.isDiffDownload()) {
				Log.e(TAG, String.format("%s is merged,but is not diff update", data.item.getGameName()));
			}
			installApp();
			break;
		case PackageMode.CHECKING_FINISHED:
			if (!data.item.isDiffDownload()) {
				Log.d(TAG, String.format("%s is CHECKING_FINISHED", data.item.getGameName()));
			}
			installApp();
			break;

		case PackageMode.MERGE_FAILED:
			reMergeApp();
			break;
		case PackageMode.INSTALLING:
			Log.e(TAG, String.format("%s is installing,but user can clicked", data.item.getGameName()));
			break;
		case PackageMode.INSTALL_FAILED:
			installApp();
			break;
		case PackageMode.INSTALLED:
			openGame();
			break;
		case PackageMode.UPDATABLE:
			download();
			// 统计更新（不太好，因为可能会失败）
			DownloadStatistics.addUpdateGameStatistics(getApplicationContext(), data.item.getGameName());
			break;
		case PackageMode.UPDATABLE_DIFF:
			downloadForDiff();
			// 统计更新（不太好，因为可能会失败）
			DownloadStatistics.addUpdateGameStatistics(getApplicationContext(), data.item.getGameName());
			break;
		default:
			break;
		}
	}
	/**
	 * 下载
	 * 
	 * @param position
	 *            adapter的position
	 * @param item
	 */
	private boolean download() {

		boolean checkNetwork = checkNetwork(REQ_CODE_DOWNLOAD);
		if (!checkNetwork) {
			return false;
		}

		// view.setEnabled(false);
		PackageHelper.download(formDownloadInput(data.item), myDownloadCallback);

		// 统计下载
		// DownloadStatistics
		// .addDownloadGameStatistics(
		// getApplicationContext(),
		// item.getGameName());
		return true;
	}
	/**
	 * 检查网络是否可用以及可用情况下是否允许下载
	 * 
	 * @param position
	 * @param item
	 * @return
	 */
	private boolean checkNetwork(int reqCode) {

		boolean networkAvailable = DeviceUtil.isNetworkAvailable(getApplicationContext());
		if (!networkAvailable) {
			CustomToast.showToast(getApplicationContext(), getString(R.string.alert_network_inavailble));
			return false;
		}

		Integer activeNetworkType = DeviceUtil.getActiveNetworkType(getApplicationContext());
		if (MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
			if (activeNetworkType != null && activeNetworkType == ConnectivityManager.TYPE_MOBILE) {

				DuokuDialog.showNetworkAlertDialog(this, REQ_CODE_DOWNLOAD, data.item.getPackageName(), data.item.getDownloadUrl(), 0);

				return false;
			}
		}

		return true;
	}
	static final int REQ_CODE_DOWNLOAD = 100;
	static final int REQ_CODE_RESUME = 101;
	static final int REQ_CODE_RESTART = 102;
	private DownloadItemInput formDownloadInput(SearchItem item) {
		DownloadItemInput downloadItemInput = new DownloadItemInput(item.getIconUrl(), item.getGameId(), item.getPackageName(), item.getGameName(), item.getGameName(), item.getVersionInt(),
				item.getVersion(), item.getDownloadUrl(), null, item.getPackageSize(), null, -1, item.getAction(), item.isNeedLogin(), item.isDiffDownload());
		return downloadItemInput;
	}
	MyDownloadCallback myDownloadCallback = new MyDownloadCallback();
	/**
	 * 下载、继续下载或者重试的回调方法
	 * 
	 * @author wangliang
	 * 
	 */
	class MyDownloadCallback implements DownloadCallback {

		private SearchItem findTarget(String url) {
//			if (listGameInfo.size() <= 0)
//				return null;

			SearchItem target = null;
//			for (int i = 0; i < listGameInfo.size(); i++) {
//				SearchItem item = listGameInfo.get(i);
				if (url.equals(data.item.getDownloadUrl())) {
					target = data.item;
				}
//			}
			return target;
		}

		@Override
		public void onDownloadResult(String downloadUrl, boolean successful, long downloadId, String saveDest, Integer reason) {
			SearchItem target = findTarget(downloadUrl);
			if (target == null) {
				return;
			}
			String gameName = target.getGameName();
			if (successful) {
				target.setDownloadId(downloadId);
				target.setSaveDest(saveDest);
			}

		}

		@Override
		public void onResumeDownloadResult(String url, boolean successful, Integer reason) {
			SearchItem target = findTarget(url);
			if (target == null) {
				return;
			}
			String gameName = target.getGameName();
		}

		@Override
		public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
			// TODO Auto-generated method stub
			SearchItem target = findTarget(downloadUrl);
			if (target == null) {
				return;
			}
		}

	}
	/**
	 * 暂停下载
	 * 
	 * @param item
	 */
	private void pauseDownload() {
		long downloadId = data.item.getDownloadId();
		if (downloadId < 0) {
			DownloadItemOutput downloadInfo = DownloadUtil.getDownloadInfo(this, data.item.getDownloadUrl());
			if (downloadInfo != null) {
				downloadId = downloadInfo.getDownloadId();
				data.item.setDownloadId(downloadId);
			}
		}
		PackageHelper.pauseDownloadGames(downloadId);
	}
	/**
	 * 继续下载
	 * 
	 * @param position
	 * @param item
	 */
	private void resumeDownload() {

		boolean checkNetwork = checkNetwork(REQ_CODE_RESUME);
		if (!checkNetwork) {
			return;
		}

		PackageHelper.resumeDownload(data.item.getDownloadId(), myDownloadCallback);
		DownloadStatistics.addResumeDownloadGameStatistics(getApplicationContext(), data.item.getGameName());

	}
	private void restartDownload() {

		boolean checkNetwork = checkNetwork(REQ_CODE_RESTART);
		if (!checkNetwork) {
			return;
		}

		PackageHelper.restartDownload(data.item.getDownloadId(), myDownloadCallback);
		// 统计下载
		// DownloadStatistics.addDownloadGameStatistics(getApplicationContext(),item.getGameName());
	}
	/**
	 * 手动安装apk
	 * 
	 * @param item
	 */
	private void installApp() {
		PackageHelper.installApp(this, data.item.getGameId(), data.item.getPackageName(), data.item.getSaveDest());
	}
	/**
	 * 重新合并或者普通更新
	 * 
	 * @param item
	 */
	private void reMergeApp() {
		if (data.item.getMergeFailedCount() >= 2) {
			DownloadItemInput formDownloadInput = formDownloadInput(data.item);
			PackageHelper.restartDownloadNormally(data.item.getDownloadId(), formDownloadInput, myDownloadCallback);
		} else {
			PackageHelper.sendMergeRequestFromUI(data.item.getDownloadId());
		}
	}
	private void openGame() {
		StartGame internalStartGame = new StartGame(this, data.item.getPackageName(), data.item.getAction(), data.item.getGameId(), data.item.isNeedLogin());
		internalStartGame.startGame();
	}
	/**
	 * 增量更新下载
	 */
	private boolean downloadForDiff() {

		boolean checkNetwork = checkNetwork(REQ_CODE_DOWNLOAD);
		if (!checkNetwork) {
			return false;
		}
		// view.setEnabled(false);
		DownloadItemInput formDownloadInput = formDownloadInput(data.item);
		formDownloadInput.setDownloadUrl(data.item.getDiffUrl());
		formDownloadInput.setDiffDownload(true);
		formDownloadInput.setSize(data.item.getPatchSize());
		PackageHelper.download(formDownloadInput, myDownloadCallback);

		// 统计下载
		// DownloadStatistics
		// .addDownloadGameStatistics(
		// getApplicationContext(),
		// item.getGameName());
		return true;

	}
	private void checkStatus() {
		// 还要考虑空包
			ArrayList<QueryInput> targets = new ArrayList<QueryInput>();
			HashMap<SearchItem, QueryInput> hashMap = new HashMap<SearchItem, QueryInput>();
			QueryInput queryInput_ = new QueryInput(data.item.getPackageName(), data.item.getVersion(), data.item.getVersionInt(), data.item.getDownloadUrl(), data.item.getGameId());
			targets.add(queryInput_);
			hashMap.put(data.item, queryInput_);
			Map<QueryInput, PackageMode> queryPackageStatus = PackageHelper.queryPackageStatus(targets);

			QueryInput queryInput = hashMap.get(data.item);
			PackageMode packageMode = queryPackageStatus.get(queryInput);
			data.item.setDiffDownload(packageMode.isDiffDownload);
			if (packageMode.status == PackageMode.UPDATABLE_DIFF) {
				data.item.setDiffUrl(packageMode.downloadUrl);
			}
			InstalledAppInfo appInfo = AppManager.getInstance(this).getInstalledGame(packageMode.packageName);
			if (null != appInfo) {
				String action = appInfo.getExtra();
				data.item.setAction(action);
			}

			data.item.setApkStatus(packageMode.status);
			data.item.setApkReason(packageMode.reason);
			data.item.setDownloadId(packageMode.downloadId);
			data.item.setSaveDest(packageMode.downloadDest);
			data.item.setCurrentBytes(packageMode.currentSize);
			data.item.setTotalBytes(packageMode.totalSize);
			data.item.setLocalVersion(packageMode.localVersion);
			data.item.setLocalVersionCode(packageMode.localVersionCode);
			data.item.setPatchSize(packageMode.pacthSize);
		
	}
	private PackageCallback packageCallback;

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

			if (data.item == null || mode == null) {
				return;
			}
			SearchItem target = null;

			if (mode.downloadId > 0 && mode.downloadId == data.item.getDownloadId()) {
				target = data.item;
			} else if (mode.gameId != null && mode.gameId.equals(data.item.getGameId())) {
				target = data.item;
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
					target.setCurrentBytes(mode.currentSize);
					target.setTotalBytes(mode.totalSize);
				}
				if (mode.downloadDest != null) {
					target.setSaveDest(mode.downloadDest);
				}

				break;
			case PackageMode.DOWNLOAD_RUNNING:
				target.setApkStatus(PackageMode.DOWNLOAD_RUNNING);
				target.setCurrentBytes(mode.currentSize);
				target.setTotalBytes(mode.totalSize);
				break;
			case PackageMode.DOWNLOAD_PAUSED:
				target.setApkStatus(PackageMode.DOWNLOAD_PAUSED);
				target.setApkReason(mode.reason);

				break;
			case PackageMode.DOWNLOAD_FAILED:
				target.setApkStatus(PackageMode.DOWNLOAD_FAILED);
				target.setApkReason(mode.reason);

				break;
			case PackageMode.DOWNLOADED:
				if (mode.isDiffDownload) {
					target.setApkStatus(PackageMode.DOWNLOADED);
				} else {
					target.setApkStatus(PackageMode.DOWNLOADED);
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
				reStartDownloadNormally(mode);
				break;
			case PackageMode.INSTALLED:
				target.setApkStatus(PackageMode.INSTALLED);
				break;
			case PackageMode.UNDOWNLOAD:
			case PackageMode.UPDATABLE:
			case PackageMode.UPDATABLE_DIFF:
				target.setApkStatus(mode.status);
				return;
			case PackageMode.RESET_STATUS:
				checkStatus();
				notifyDataSetChanged();
				refreshAllItemsView();
				return;
			default:
				return;
			}

			target.setCurrentBytes(mode.currentSize);
			target.setTotalBytes(mode.totalSize);
			target.setApkStatus(mode.status);
			target.setApkReason(mode.reason);
			// refreshList(mode);

			refreshDownloadProgress(target.getGameId());
		}
	}
	private void reStartDownloadNormally(PackageMode mode) {
//		if (listGameInfo.size() <= 0) {
//			return;
//		}

		SearchItem item = null;
		String gameId = mode.gameId;
		if (gameId != null && gameId.equals(data.item.getGameId())) {
			item = data.item;
		}
		if (item != null && item.getMergeFailedCount() >= 2) {
			DownloadItemInput formDownloadInput = formDownloadInput(item);
			PackageHelper.restartDownloadNormally(item.getDownloadId(), formDownloadInput, myDownloadCallback);
		} else {
			// PackageHelper.sendMergeRequest(item.getDownloadId());
		}
	}
	private void notifyDataSetChanged() {
		updateView();
	};
	public void refreshAllItemsView() {/*
		int firstVisiblePosition = listView.getFirstVisiblePosition();

		int childCount = listView.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = listView.getChildAt(i);
			int index = i + firstVisiblePosition;

			if (index > 0) {
				SearchItem item = getItem(index);

				if (!StringUtil.isEmpty(item.getGameId())) {
					this.updateItemView(childView, item);
				}
			}
		}
	*/
		updateItemView();	
	}
	private void updateItemView() {
		if (StringUtil.isEmpty(data.item.getGameId())) {
			return;
		}

//		ImageView actionIv = (ImageView) view.findViewById(R.id.search_item_action_iv);
//		TextView actionTv = (TextView) view.findViewById(R.id.search_item_action_tv);

		View p = (View) search_item_action_iv.getParent();
		Integer position = (Integer) p.getTag();
		String downloadUrl = data.item.getDownloadUrl();

		if (downloadUrl.equals(data.item.getDownloadUrl())) {
			updateDownloadStatus();
		}
	}
	private void refreshDownloadProgress(final String gameId) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					updateItemView();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
	/**
	 * BroadcastReceiver
	 */
	private MyReceiver myReceiver;

	/**
	 * 当app安装或者删除时刷新列表
	 */
	private void registerReceiver() {
		if (myReceiver == null) {
			myReceiver = new MyReceiver();
			//
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BroadcaseSender.ACTION_PRE_PACKAGE_EVENT);
			intentFilter.addDataScheme("package");
			registerReceiver(myReceiver, intentFilter);

			IntentFilter intentFilter2 = new IntentFilter();
			intentFilter2.addAction(BroadcaseSender.ACTION_DOWNLOAD_CHANGED);
			registerReceiver(myReceiver, intentFilter2);

			IntentFilter installFilter = new IntentFilter();
			installFilter.addAction(BroadcaseSender.ACTION_INSTALL_CHANGED);
			registerReceiver(myReceiver, installFilter);

			IntentFilter downloadFilter = new IntentFilter();
			downloadFilter.addAction("com.duoku.action.download.begin");
			registerReceiver(myReceiver, downloadFilter);
		}
	}
	/**
	 * Intent d_intent = new Intent("com.duoku.action.download.begin");
	 * d_intent.putExtra("pkgname", gameInfo.getPkgname());
	 * GameDetailsActivity.this.sendBroadcast(d_intent);
	 * 
	 * @author wangliang
	 * 
	 */
	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			if(data.item==null)
				return;

			String action = intent.getAction();
			// if (Constants.DEBUG)Log.i(TAG, "MyReceiver receive "+action);
			/**
			 * 程序安装或者卸载的通知（好像只有跳到ManagerActivity中才会出现）
			 */
			if (action.equals(BroadcaseSender.ACTION_PRE_PACKAGE_EVENT)) {
				String originalAction = intent.getStringExtra(BroadcaseSender.ARG_ORIGIANL_ACTION);
				if (originalAction.equals(Intent.ACTION_PACKAGE_ADDED)) {
					onPackageAdded(context, intent);
				} else if (originalAction.equals(Intent.ACTION_PACKAGE_REMOVED)) {
					onPackageRemoved(context, intent);
				}

			}
			/**
			 * 添加或者删除下载的通知（好像只有跳到ManagerActivity中才会出现）
			 */
			if (action.equals(BroadcaseSender.ACTION_DOWNLOAD_CHANGED)) {
				boolean downloadOrOtherwise = intent.getBooleanExtra(BroadcaseSender.DOWNLOAD_CHANGED_ARG, false);
				if (downloadOrOtherwise) {
//					if (gameInfoListAdapter != null && listGameInfo.size() > 0) {
						// 删除下载需要更新状态
						// checkAndFillSearchResult2(gameInfoListAdapter.getData());
//					}

				}
			}
			/**
			 * 静默安装的通知
			 */
			if (action.equals(BroadcaseSender.ACTION_INSTALL_CHANGED)) {
				// TODO
				onInstallChanged();
			}

			if ("com.duoku.action.download.begin".equals(action)) {
				try {
					// d_intent.putExtra("fromown", true);
					boolean fromOwn = intent.getBooleanExtra("fromown", false);
					String pkgName = intent.getStringExtra("pkgname");
					String gameId = intent.getStringExtra("gameid");
					long downloadId = intent.getLongExtra("downloadid", -1);

					if (downloadId > 0 && gameId != null && !fromOwn) {

//						if (listGameInfo.size() <= 0)
//							return;
						// gameInfoListAdapter.setNotifyOnChange(false);
//						for (SearchItem item : listGameInfo) {}

						String tmp = data.item.getGameId();
						if (gameId != null && tmp.equals(gameId)) {
							data.item.setCurrentBytes(0);
							data.item.setTotalBytes(data.item.getPackageSize());
							data.item.setDownloadStatus(DownloadStatus.STATUS_PENDING);
							data.item.setStatus(GameStatus.DOWNLOADING);
							Message msg = new Message();
							msg.what = MSG_REFRESH_PROGRESS;
							msg.obj = data.item.getGameId();
							handler.sendMessage(msg);
						}
					

						addDownloadListener(gameId, downloadId);
//						if (gameInfoListAdapter != null) {
							notifyDataSetChanged();
//						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

	}
	private void onPackageAdded(Context context, Intent intent) {
		String packageName = intent.getData().getSchemeSpecificPart();
		Boolean systemPackage = AppUtil.isSystemPackage(context.getPackageManager(), packageName);
		if (systemPackage == null || systemPackage) {
			return;
		}
//		if (gameInfoListAdapter != null) {
//			if (listGameInfo.size() <= 0)
//				return;

		if (packageName.equals(data.item.getPackageName()) && data.item.getStatus() == GameStatus.DONWLOADED) {
			data.item.setStatus(GameStatus.INSTALLED);
			data.item.setInstalleStatus(InstallStatus.INSTALLED);
		}
			notifyDataSetChanged();
//		}
	}
	private void onPackageRemoved(Context context, Intent intent) {
		String packageName = intent.getData().getSchemeSpecificPart();
//		if (gameInfoListAdapter != null) {
//			if (listGameInfo.size() <= 0)
//				return;

//			for (SearchItem searchItem : listGameInfo) {}

		if (packageName.equals(data.item.getPackageName())) {
			if (data.item.getStatus() == GameStatus.DONWLOADED
			/* || searchItem.getInstalleStatus() != null */) {
				data.item.setStatus(GameStatus.DONWLOADED);
				// searchItem.setInstalleStatus(null)
			} else {
				data.item.setStatus(GameStatus.UNDOWNLOAD);
			}

		}
	
			notifyDataSetChanged();
//		}

	}
	private void onInstallChanged() {
//		if (gameInfoListAdapter != null) {
//			if (listGameInfo.size() <= 0)
//				return;

			AppManager manager = AppManager.getInstance(getApplicationContext());
			Set<InstallPacket> silentInstallList = manager.getSilentInstallList();
			for (InstallPacket installPacket : silentInstallList) {
				String packageName = installPacket.getPackageName();
//				for (SearchItem searchItem : listGameInfo) {
//					// TODO 不能唯一确定
//					
//				}
				if (packageName.equals(data.item.getPackageName()) && data.item.getStatus() == GameStatus.DONWLOADED) {
					data.item.setInstalleStatus(installPacket.getStatus());
					data.item.setInstallErrorReason(installPacket.getErrorReason());
				}
			}

			notifyDataSetChanged();
//		}
	}
	private static final int MSG_REFRESH_PROGRESS = 300;
	private static final int MSG_REFRESH_TITLE_COUNT = 301;
	private void addDownloadListener(String gameId, long downloadId) {
		DownloadUtil.addDownloadItemListener(getApplicationContext(), downloadId, onItemDownloadListener);
		observersIds.put(gameId, downloadId);
	}
	private OnItemDownloadListener onItemDownloadListener = new OnItemDownloadListener();
	class OnItemDownloadListener implements DownloadItemListener {

		long last = -1;

		@Override
		public void onDownloadProcessing(DownloadItemOutput o) {

//			if (gameInfoListAdapter == null) {
//				return;
//			}

//			if (listGameInfo.size() <= 0)
//				return;
			// gameInfoListAdapter.setNotifyOnChange(false);
//			for (SearchItem item : listGameInfo) {}

			String downloadUrl = data.item.getDownloadUrl();
			if (downloadUrl != null && downloadUrl.equals(o.getUrl())) {
				long currentBytes = o.getCurrentBytes();
				long totalBytes = o.getTotalBytes();
				data.item.setCurrentBytes(currentBytes);
				data.item.setTotalBytes(totalBytes);
				DownloadStatus status = o.getStatus();
				data.item.setDownloadStatus(status);
				data.item.setDownloadReason(o.getReason());
				switch (status) {
				case STATUS_FAILED:
				case STATUS_PAUSED:
				case STATUS_RUNNING:
				case STATUS_PENDING:
					data.item.setStatus(GameStatus.DOWNLOADING);
					break;
				case STATUS_SUCCESSFUL:
					data.item.setStatus(GameStatus.DONWLOADED);
					addDownloadedData(data.item.getGameId(), o.getDest());
					break;
				}
				Message msg = new Message();
				msg.what = MSG_REFRESH_PROGRESS;
				msg.obj = data.item.getGameId();
				handler.sendMessage(msg);
			}
		
		}
	}
	private void addDownloadedData(String gameId, String dest) {
		downloadedIds.put(gameId, dest);
	}
	private Map<String, Long> observersIds = new HashMap<String, Long>();
	private Map<String, String> downloadedIds = new HashMap<String, String>();
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_PROGRESS:
				if (Constants.DEBUG)
					Log.i("wangliang", "refreshDownloadPregress " + Thread.currentThread().getName() + ":" + System.currentTimeMillis() / 1000);
				String gameId = (String) msg.obj;
				refreshDownloadProgress(gameId);
				break;
			case MSG_REFRESH_TITLE_COUNT:
				int count = (Integer) msg.obj;
//				titleLeftText.setText(titleLeftText.getText().toString() + "(" + count + ")");
				break;
			}
		};
	};
	private ActivityDetail data;
	private void unregisterReceiver() {
		if (myReceiver != null) {
			unregisterReceiver(myReceiver);
			myReceiver = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterListener();
		unregisterReceiver();
	}
}
