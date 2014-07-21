package com.ranger.bmaterials.ui;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.InstalledAppListAdapter;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.StartGame;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.tools.PhoneHelper;
import com.ranger.bmaterials.tools.LogcatScanner.LogcatObserver;
import com.ranger.bmaterials.view.PopupWindowCompat;
import com.ranger.bmaterials.view.StickyListHeadersListView;
import com.ranger.bmaterials.work.InstalledAppListLoader;

public class InstalledAppListFragment extends
		AbstractAppListFragment<InstalledAppInfo> {

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.manager_activity_installed_fragment, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		ViewGroup parent = (ViewGroup) getView();
		ListView listView = (ListView) parent
				.findViewById(R.id.manager_installed_list);
		View parentView = (View) listView.getParent();
		parentView.setVisibility(View.INVISIBLE);
		listView.setEmptyView(parentView.findViewById(R.id.install_null));
		// Create an empty adapter we will use to display the loaded data.
		mAdapter = new InstalledAppListAdapter(getActivity());
		mAdapter.setOnListItemClickListener(this);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(onLongClickListener);

		super.onActivityCreated(savedInstanceState);

		// Prepare the loader. Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(2, null, this);
	}
	
	AdapterView.OnItemLongClickListener onLongClickListener = new  AdapterView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			View subView = view.findViewById(R.id.manager_activity_installed_list_item_icon);
			showPopupWindow(subView, position);
			return true;
		}
	};
	
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		super.onItemClick(parent, view, position, id);
		View subView = view
				.findViewById(R.id.manager_activity_installed_list_item_icon);
		showPopupWindow(subView, position);

	}

	@Override
	public void onItemIconClick(View view, int position) {
		super.onItemIconClick(view, position);
		showPopupWindow(view, position);
	}

	OnClickListener popupWindowOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			dismissPopupWindow();
			try {
				if(mAdapter == null ){
					return ;
				}
				View parent = (View) v.getParent();
				final Integer p = (Integer) parent.getTag();
				InstalledAppInfo item = mAdapter.getItem(p);
				
				switch (v.getId()) {
					case R.id.manager_update_popupwindow_share:
						shareGame(item);
						break;
		
					case R.id.manager_update_popupwindow_open:
						StartGame internalStartGame = new StartGame(getActivity(), item.getPackageName(),
								item.getExtra(), item.getGameId(), item.isNeedLogin());
						internalStartGame.startGame();
						
						break;
					case R.id.manager_update_popupwindow_uninstall:
						uninstallApk(item);
						updateCountChanged();
						break;
					case R.id.manager_update_popupwindow_viewdetail:
						viewDetail(item);
						break;
					}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		
	};
	
	public void dismissPopupWindow() {
		if (pw != null && pw.isShowing()) {
			pw.getContentView().invalidate();
			pw.dismiss();
		}
	}

	private void shareGame(InstalledAppInfo item) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		String content = getString(R.string.share_game_text);
		String format = String.format(content, item.getName());
		sendIntent.putExtra(Intent.EXTRA_TEXT, format);
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "分享"));
	}

	private void uninstallApk(InstalledAppInfo item) {
		Uri packageURI = Uri.parse("package:" + item.getPackageName());
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivity(uninstallIntent);
		listen(item.getPackageName());
	}

	
	/**
	 * 有些机器上安装广播非常慢，所以这里启动Thread监听（不是必须的）
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
							Log.i("MyLogcatObserver", "消耗时间：" + o + " current:"
									+ c + "  start:" + startTime);
					}
					try {
						if (f.exists()) {
							if (Constants.DEBUG)
								Log.i("MyLogcatObserver", "该设备有/data/data/"
										+ packageName + "继续");
							Thread.sleep(400);
						} else {
							if (Constants.DEBUG)
								Log.i("MyLogcatObserver", "该设备没有/data/data/"
										+ packageName + ",停止");
							notifyInstallResult(packageName, true);
							break;
						}

					} catch (Throwable e1) {
						if (Constants.DEBUG)
							Log.i("MyLogcatObserver", "/data/data/"
									+ packageName + "出错,停止");
						break;
					}
				}

			}

		}.start();
	}

	static final int WHAT_NOTIFY_INSTALL_RESULT = 100;

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			
			if (msg.what == WHAT_NOTIFY_INSTALL_RESULT) {
				String packageName = (String) msg.obj;
				List<InstalledAppInfo> data = mAdapter.getData();
				if (data == null)
					return;
				for (Iterator iterator2 = data.iterator(); iterator2.hasNext();) {
					InstalledAppInfo downloadAppInfo = (InstalledAppInfo) iterator2
							.next();
					if (downloadAppInfo.getPackageName().equals(packageName)) {
						iterator2.remove();
						break;
					}

				}
				dismissPopupWindow();
				updateCountChanged();
				mAdapter.notifyDataSetChanged();
				if (Constants.DEBUG)
					Log.i("MyLogcatObserver", "Adapter 刷新:" + packageName);
			}

		}
	}

	MyHandler myHandler = new MyHandler();
	
	/**
	 * 通知监听结果
	 * @param packageName
	 * @param sure
	 */
	private void notifyInstallResult(String packageName, boolean sure) {
		if (Constants.DEBUG)
			Log.i("MyLogcatObserver", "notifyInstallResult");
		Message message = new Message();
		message.what = WHAT_NOTIFY_INSTALL_RESULT;
		message.obj = packageName;
		myHandler.sendMessage(message);

		BroadcaseSender sender = BroadcaseSender
				.getInstance(GameTingApplication.getAppInstance());
		sender.sendPreBroadcastForPackageEvent(false, packageName);
	}


	/**
	 * 查看详情
	 * @param item
	 */
	private void viewDetail(InstalledAppInfo item) {
		AppManager manager = AppManager.getInstance(getActivity());
        manager.jumpToDetail(getActivity(), item.getGameId(), item.getName(), item.getPackageName(), false, String.valueOf(item.getVersionInt()), item.getVersion());

	}

	private PopupWindow pw;
	@SuppressWarnings("deprecation")
	private void showPopupWindow(View view, int position) {
		View contentView = null;
		boolean showingTowardUp = showingTowardUp(view);
		if (!showingTowardUp) {
			contentView = View.inflate(getActivity(),
					R.layout.manager_installed_popupwindow, null);
		} else {
			contentView = View.inflate(getActivity(),
					R.layout.manager_installed_popupwindow_up, null);
		}
		contentView.setTag(position);
		contentView.findViewById(R.id.manager_update_popupwindow_share)
				.setOnClickListener(popupWindowOnClickListener);
		contentView.findViewById(R.id.manager_update_popupwindow_open)
				.setOnClickListener(popupWindowOnClickListener);
		contentView.findViewById(R.id.manager_update_popupwindow_uninstall)
				.setOnClickListener(popupWindowOnClickListener);
		contentView.findViewById(R.id.manager_update_popupwindow_viewdetail)
				.setOnClickListener(popupWindowOnClickListener);
		pw = new PopupWindowCompat(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		pw.setFocusable(true);
		pw.setOutsideTouchable(true);
		// pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		pw.setBackgroundDrawable(new BitmapDrawable(getResources()));
		// get icon's location
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int verticalMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 2, getActivity().getResources()
						.getDisplayMetrics());
		int horizontalMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 2, getActivity().getResources()
						.getDisplayMetrics());

		if (!showingTowardUp) {
			pw.setAnimationStyle(R.style.popup_down_animation);
			pw.showAsDropDown(view, horizontalMargin, verticalMargin);
		} else {
			pw.setAnimationStyle(R.style.popup_up_animation);
			// pw.showAsDropDown(view,0, -view.getMeasuredHeight());
			int[] screenWH = DeviceUtil.getScreensize(getActivity());
            pw.showAtLocation(view, Gravity.TOP | Gravity.LEFT, location[0]
                    + horizontalMargin, location[1] - view.getHeight()
                    + verticalMargin);
		}

	}

	private boolean showingTowardUp(View iconView) {
		View listView = (View) iconView.getParent().getParent().getParent().getParent().getParent()
				.getParent();
		int[] location = new int[2];
		listView.getLocationOnScreen(location);

		int listViewCenterY = listView.getHeight() / 2 + location[1];
		iconView.getLocationOnScreen(location);
		int iconY = location[1];

		return (iconY + iconView.getHeight()) > listViewCenterY;
	}

	// /////////////////////////////////////////////////////////


	public void reloadApplications() {
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<List<InstalledAppInfo>> onCreateLoader(int id, Bundle args) {
		loading(true);
		return new InstalledAppListLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<InstalledAppInfo>> loader,
			List<InstalledAppInfo> data) {
		
		super.onLoadFinished(loader, data);
		dismissPopupWindow();
		ViewGroup parent = (ViewGroup) getView();
		final StickyListHeadersListView listView = (StickyListHeadersListView) parent
				.findViewById(R.id.manager_installed_list);
		// listView.setVisibility(View.VISIBLE);
		View viewParent = (View) listView.getParent();
		viewParent.setVisibility(View.VISIBLE);
		View progressBar = parent
				.findViewById(R.id.manager_installed_list_progressbar);
		progressBar.setVisibility(View.GONE);
		listView.setOnItemClickListener(this);
		updateCountChanged();
	}
	
	/**
	 * 更新title的数字
	 */
	private void updateCountChanged() {
		try {
			ManagerActivity activity = (ManagerActivity) getActivity();
			if (activity != null) {
				activity.updateTitle(2, mAdapter.getCount());
			}
		} catch (Exception e) {
			ManagerActivity activity = (ManagerActivity) getActivity();
			if (activity != null) {
				activity.updateTitle(2, mAdapter.getCount());
			}
		}
	}

}
