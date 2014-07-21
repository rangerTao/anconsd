package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.IgnoredUpdatableAppListAdapter;
import com.ranger.bmaterials.adapter.AbstractListAdapter.OnListItemClickListener;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.mode.DownloadCallback;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.statistics.GeneralStatistics;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.view.DuokuDialog;
import com.ranger.bmaterials.view.PopupWindowCompat;
import com.ranger.bmaterials.work.DBTaskManager;
import com.ranger.bmaterials.work.IgnoredUpdatableAppLoader;

public class IgnoredUpdatableAppsActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<UpdatableAppInfo>>, OnListItemClickListener, OnClickListener,
		OnItemClickListener, OnItemLongClickListener {
	public static String DATA = "data";
	private ListView listView;
	private View progressBar;
	private TextView tvHintPlain;
	private TextView tvHintRed;
	private IgnoredUpdatableAppListAdapter adapter;
	private PopupWindow pw;
	private LoaderManager loader;

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
		
	};

	private PackageCallback packageCallback = new PackageCallback() {

		UpdatableAppInfo target = null;

		@Override
		public void onPackageStatusChanged(PackageMode mode) {
			if (null == adapter) {
				return;
			} else {

				List<UpdatableAppInfo> data = adapter.getData();
				
				if (data == null) {
					return;
				}

				// Find which item need to refresh.
				for (UpdatableAppInfo item : data) {
					if (!mode.packageName.equals("") && mode.packageName.equals(item.getPackageName())) {
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
				
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						AppManager manager = AppManager.getInstance(IgnoredUpdatableAppsActivity.this);
						manager.updateIgnoreState(false, target.getPackageName());
						adapter.remove(target);
						updateView(true);
						adapter.notifyDataSetChanged();
						
						target = null;
					}
				});

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ignoredapplist_activity);

		if (packageCallback != null) {
			PackageHelper.registerPackageStatusChangeObserver(packageCallback);
		}

		init();

	}

	private void init() {
		initTitleBar();

		listView = (ListView) findViewById(R.id.manager_ignored_list);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		View view = (View) listView.getParent();
		progressBar = findViewById(R.id.manager_ignored_progressbar);
		view.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);

		View hintParent = findViewById(R.id.manager_ignored_hint_text);
		tvHintPlain = (TextView) hintParent.findViewById(R.id.red_notify_plain_text);
		tvHintRed = (TextView) hintParent.findViewById(R.id.red_notify_red_text);
		tvHintRed.setVisibility(View.GONE);

		View btnCancleIgnored = findViewById(R.id.manager_cancle_all_ignored_button);
		btnCancleIgnored.setOnClickListener(this);

		Intent intent = getIntent();
		ArrayList<Parcelable> data = intent.getParcelableArrayListExtra(DATA);
		if (data != null) {
			view.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
		} else {
			loader = getSupportLoaderManager();
			loader.initLoader(0, null, this);
		}

	}

	private void initTitleBar() {
		TextView tvTitle = (TextView) findViewById(R.id.label_title);
		tvTitle.setText(R.string.title_manager);

		View viewBack = findViewById(R.id.img_back);
		viewBack.setOnClickListener(this);
	}

	private void updateView(boolean showing) {
		List<UpdatableAppInfo> apps = adapter.getData();
		if (apps != null && apps.size() > 0) {
			adapter.setData(apps);
			listView.setAdapter(adapter);
			tvHintPlain.setText("已忽略" + "(" + apps.size() + ")");
			// tvHintRed.setText(""+adapter.getCount());
		} else {
			tvHintPlain.setText("没有忽略更新");
			// tvHintRed.setVisibility(View.GONE);
		}

	}

	private void unignoreUpdateItem(UpdatableAppInfo item) {

		AppManager manager = AppManager.getInstance(IgnoredUpdatableAppsActivity.this);
		manager.updateIgnoreState(false, item.getPackageName());

		adapter.remove(item);

		updateView(true);

	}

	private void populateData(List<UpdatableAppInfo> apps) {
		((View) listView.getParent()).setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.INVISIBLE);

		adapter = new IgnoredUpdatableAppListAdapter(this);
		adapter.setOnListItemClickListener(this);
		if (apps != null && apps.size() > 0) {
			adapter.setData(apps);
			listView.setAdapter(adapter);
			tvHintPlain.setText("已忽略" + "(" + apps.size() + ")");
			tvHintRed.setText("" + adapter.getCount());
		} else {
			tvHintPlain.setText("没有忽略更新");
			// tvHintRed.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	protected void onPause() {
		GeneralStatistics.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		GeneralStatistics.onResume(this);
		if (null != adapter)
			adapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (packageCallback != null) {
			PackageHelper.unregisterPackageStatusChangeObserver(packageCallback);
		}
		super.onDestroy();
	}

	@Override
	public Loader<List<UpdatableAppInfo>> onCreateLoader(int arg0, Bundle arg1) {
		return new IgnoredUpdatableAppLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<List<UpdatableAppInfo>> loader, List<UpdatableAppInfo> apps) {
		if (apps == null || apps.size() == 0) {

		} else {
			Iterator<UpdatableAppInfo> iterator = apps.iterator();
			while (iterator.hasNext()) {
				UpdatableAppInfo updatableAppInfo = (UpdatableAppInfo) iterator.next();
				
				if (!updatableAppInfo.isIgnoreUpdate()) {
					iterator.remove();
				}
			}
		}
		populateData(apps);

		try {
			if (pw != null && pw.isShowing()) {
				pw.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onLoaderReset(Loader<List<UpdatableAppInfo>> arg0) {

	}

	@Override
	public void onItemIconClick(View view, int position) {
		showPopupWindow(view, position);
	}

	@Override
	public void onItemButtonClick(View view, final int position) {
		final UpdatableAppInfo item = adapter.getItem(position);

		DBTaskManager.submitTask(new Runnable() {

			@Override
			public void run() {
				AppManager manager = AppManager.getInstance(IgnoredUpdatableAppsActivity.this);
				manager.updateIgnoreState(false, item.getPackageName());
			}
		});
		adapter.remove(item);
		updateView(true);
	}

	// ///////////////////////////////////////
	private void showPopupWindow(View view, int position) {
		Context context = this;

		View contentView = null;
		boolean showingTowardUp = showingTowardUp(view);
		if (!showingTowardUp) {
			contentView = View.inflate(context, R.layout.manager_ignore_update_popupwindow, null);
		} else {
			contentView = View.inflate(context, R.layout.manager_ignore_update_popupwindow_up, null);
		}

		contentView.setTag(position);
		contentView.findViewById(R.id.manager_ignore_update_popupwindow_update).setOnClickListener(popupWindowOnClickListener);
		contentView.findViewById(R.id.manager_ignore_update_popupwindow_detail).setOnClickListener(popupWindowOnClickListener);
		pw = new PopupWindowCompat(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pw.setFocusable(true);
		pw.setOutsideTouchable(true);
		// pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		pw.setBackgroundDrawable(new BitmapDrawable(getResources()));
		// get icon's location
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
		int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());

		if (!showingTowardUp(view)) {
			pw.setAnimationStyle(R.style.popup_down_animation);
			pw.showAsDropDown(view, horizontalMargin, verticalMargin);
		} else {
			pw.setAnimationStyle(R.style.popup_up_animation);
			int[] screenWH = DeviceUtil.getScreensize(context);
			pw.showAtLocation(view, Gravity.BOTTOM | Gravity.LEFT, location[0] + horizontalMargin, screenWH[1] - location[1] + verticalMargin);
		}

		// pw.showAsDropDown(view, view.getMeasuredWidth(),
		// view.getMeasuredHeight());
	}

	private boolean showingTowardUp(View iconView) {
		View listView = (View) iconView.getParent().getParent().getParent();
		int[] location = new int[2];
		listView.getLocationOnScreen(location);

		int listViewCenterY = listView.getHeight() / 2 + location[1];
		iconView.getLocationOnScreen(location);
		int iconY = location[1];

		return (iconY + iconView.getHeight()) > listViewCenterY;
	}

	private UpdatableAppInfo findItem(Intent data) {
		String url = data.getStringExtra(DownloadDialogActivity.ARG2);
		List<UpdatableAppInfo> list = adapter.getData();
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

		if (requestCode == REQ_CODE_SINGLE) {
			UpdatableAppInfo item = findItem(data);
			if (item != null) {
				PackageHelper.download(formDownloadInput(item), myDownloadCallback);
			}
		}
	}

	private boolean checkNetwork(int position, UpdatableAppInfo item, int reqCode) {
		boolean networkAvailable = DeviceUtil.isNetworkAvailable(getApplicationContext());
		if (!networkAvailable) {
			CustomToast.showToast(getApplicationContext(), getString(R.string.alert_network_inavailble));
			return false;
		}

		Integer activeNetworkType = DeviceUtil.getActiveNetworkType(getApplicationContext());
		if (MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
			if (activeNetworkType != null && activeNetworkType == ConnectivityManager.TYPE_MOBILE) {

				DuokuDialog.showNetworkAlertDialog(IgnoredUpdatableAppsActivity.this, reqCode, item.getPackageName(), item.getDownloadUrl(), position);
				return false;
			}
		}
		return true;
	}

	private DownloadItemInput formDownloadInput(UpdatableAppInfo item) {
		if (item.isDiffUpdate()) {
			DownloadItemInput downloadItemInput = new DownloadItemInput(item.getIconUrl(), item.getGameId(), item.getPackageName(), item.getName(), item.getName(),
					item.getVersionInt(), item.getVersion(), item.getPatchUrl(), null, item.getPatchSize(), null, -1, item.getExtra(), item.isNeedLogin(), true);
			return downloadItemInput;
		} else {
			DownloadItemInput downloadItemInput = new DownloadItemInput(item.getIconUrl(), item.getGameId(), item.getPackageName(), item.getName(), item.getName(),
					item.getVersionInt(), item.getVersion(), item.getDownloadUrl(), null, item.getSize(), null, -1, item.getExtra(), item.isNeedLogin(), false);
			return downloadItemInput;
		}

	}

	MyDownloadCallback myDownloadCallback = new MyDownloadCallback();

	class MyDownloadCallback implements DownloadCallback {

		private UpdatableAppInfo findTarget(String url) {
			if (adapter == null || adapter.getData() == null) {
				return null;
			}

			List<UpdatableAppInfo> data = adapter.getData();
			int size = data.size();
			UpdatableAppInfo target = null;
			for (int i = 0; i < size; i++) {
				UpdatableAppInfo item = data.get(i);
				if (url.equals(item.getDownloadUrl())) {
					target = item;
				}
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
		}

		@Override
		public void onResumeDownloadResult(String url, boolean successful, Integer reason) {
			UpdatableAppInfo target = findTarget(url);
			if (target == null) {
				return;
			}
			String gameName = target.getName();

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

	private void download(int position, UpdatableAppInfo item) {
		boolean checkNetwork = checkNetwork(position, item, IgnoredUpdatableAppsActivity.REQ_CODE_SINGLE);
		if (!checkNetwork) {
			return;
		}
		PackageHelper.download(formDownloadInput(item), myDownloadCallback);
	}

	static final int REQ_CODE_SINGLE = 200;
	OnClickListener popupWindowOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				if (pw != null && pw.isShowing()) {
					pw.getContentView().invalidate();
					pw.dismiss();
				}
				View parent = (View) v.getParent();
				final Integer p = (Integer) parent.getTag();
				final UpdatableAppInfo item = adapter.getItem(p);

				switch (v.getId()) {
				case R.id.manager_ignore_update_popupwindow_update:
					download(p, item);
					unignoreUpdateItem(item);
					break;

				case R.id.manager_ignore_update_popupwindow_detail:
					AppManager manager = AppManager.getInstance(IgnoredUpdatableAppsActivity.this);
                    manager.jumpToDetail(IgnoredUpdatableAppsActivity.this, item.getGameId(), item.getName(), item.getPackageName(), false);
                    break;
				}
			} catch (Exception e) {
			}
		}

	};

	@Override
	public void onClick(View v) {
		//
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.manager_cancle_all_ignored_button:
			try {
				final List<UpdatableAppInfo> apps = adapter.getData();
				DBTaskManager.submitTask(new Runnable() {

					@Override
					public void run() {
						String[] packageNames = new String[apps.size()];
						int i = 0;
						for (UpdatableAppInfo updatableAppInfo : apps) {
							packageNames[i] = updatableAppInfo.getPackageName();
							i++;
						}
						AppManager manager = AppManager.getInstance(IgnoredUpdatableAppsActivity.this);
						manager.updateAllIgnoreState(false, packageNames);
					}
				});
				adapter.clear();
				updateView(true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			// intent.putExtra("data", "hello");
			setResult(RESULT_OK, intent);
			;
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		View subView = view.findViewById(R.id.manager_activity_updatable_list_item_icon);
		showPopupWindow(subView, position);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long arg3) {
		View subView = view.findViewById(R.id.manager_activity_updatable_list_item_icon);
		showPopupWindow(subView, position);
		return true;
	}

}
