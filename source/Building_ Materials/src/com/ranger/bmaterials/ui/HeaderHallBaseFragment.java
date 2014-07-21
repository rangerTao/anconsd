package com.ranger.bmaterials.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.broadcast.DownLoadPopNumReceiver;
import com.ranger.bmaterials.broadcast.DownLoadPopNumReceiver.IPopNumChanged;
import com.ranger.bmaterials.mode.KeywordsList;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.UIUtil;

//主界面tab基类
public abstract class HeaderHallBaseFragment extends Fragment implements OnClickListener, IPopNumChanged, ViewSwitcher.ViewFactory {

	private DownLoadPopNumReceiver popNumReceiver;// 各个界面一个监听

	private TextView hall_header_download_pop_tv;
	private TextSwitcher hall_header_search_keyword;

	protected View root;
	private String last_keyword = "";

	// protected HeaderCoinAnimationTask headerCoinTask;

	private static final long MSG_DELAY = 5 * 1000;
	private static final int MSG_WHAT_REFRESH_RECOM_KEYWORDS = 100;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_WHAT_REFRESH_RECOM_KEYWORDS:
				try {
					String keyword = (String) msg.obj;
					if (getActivity() == null)
						return;
					if (!keyword.equals("")) {
						String app_name = getString(R.string.app_name);
						if (hall_header_download_pop_tv != null) {
							String tempKeyword = !keyword.equals(app_name) ? getString(R.string.hall_home_search_hint, keyword) : app_name;
							hall_header_search_keyword.setText(tempKeyword);
							last_keyword = keyword;
						}
					}

					Message newmsg = mHandler.obtainMessage();
					newmsg.what = MSG_WHAT_REFRESH_RECOM_KEYWORDS;
					if (KeywordsList.getInstance().getRecomKeywords() != null && KeywordsList.getInstance().getRecomKeywords().size() > 0) {
						newmsg.obj = KeywordsList.getInstance().getRandomRecomKeyword();
					} else {
						newmsg.obj = getString(R.string.app_name);
					}
					mHandler.sendMessageDelayed(newmsg, MSG_DELAY);
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			}
			super.handleMessage(msg);

		}
	};

	protected void initHeader() {

		hall_header_search_keyword = (TextSwitcher) root.findViewById(R.id.tv_header_titel_keyword);
		hall_header_search_keyword.setFactory(this);
		hall_header_search_keyword.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_bottom));
		hall_header_search_keyword.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_top));

		root.findViewById(R.id.tv_item_title_hall).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// start search activity
				if (!last_keyword.equals(getString(R.string.app_name))) {
					startSearchActivity(last_keyword);
				} else {
					startSearchActivity("");
				}
			}
		});

		root.findViewById(R.id.tv_header_titel_keyword).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// start search activity with keyword.
				startSearchActivity("");
			}
		});

		Message msg = mHandler.obtainMessage();
		msg.what = MSG_WHAT_REFRESH_RECOM_KEYWORDS;
		msg.obj = KeywordsList.getInstance().getRandomRecomKeyword();
		mHandler.sendMessageDelayed(msg, MSG_DELAY);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// if (headerCoinTask != null)
		// {
		// headerCoinTask.onDestroy();
		// headerCoinTask = null;
		// }
		unregisterPopNumReceiver();
	}

	private void registerPopNumReceiver() {
		popNumReceiver = new DownLoadPopNumReceiver();
		IntentFilter filter = new IntentFilter(BroadcaseSender.ACTION_MANAGER_APPS_CHANGED);
		filter.addAction(BroadcaseSender.ACTION_UPDATABLE_LIST_INITIALIZED);

		getActivity().registerReceiver(popNumReceiver, filter);
		popNumReceiver.listeners.add(this);
	}

	private void unregisterPopNumReceiver() {
		try {
			if (popNumReceiver != null)
				getActivity().unregisterReceiver(popNumReceiver);
			popNumReceiver = null;
		} catch (Exception e) {

		}
	}

	@Override
	public void onPopNumChanged(Intent intent) {
		// popnum
		try {
			String result = intent.getStringExtra(BroadcaseSender.MANAGER_APPS_CHANGED_ARG);
			if (!TextUtils.isEmpty(result) && !"null".equals(result))
				result = result.replace("a", "");
			else
				result = "0";
			int pop = Integer.parseInt(result);
			changePopNumTv(pop);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void changePopNumTv(int result) {
		if (result > 0) {
			hall_header_download_pop_tv.setVisibility(View.VISIBLE);
			if (result <= 99)
				hall_header_download_pop_tv.setText("" + result);
			else
				hall_header_download_pop_tv.setText("*");
		} else {
			hall_header_download_pop_tv.setVisibility(View.GONE);
		}
	}

	private AtomicBoolean isTaskRunning = new AtomicBoolean(false);

	private void checkPopNum() {

		if (!isTaskRunning.get()) {
			isTaskRunning.set(true);
			new AsyncTask<Void, Void, Integer>() {

				@Override
				protected Integer doInBackground(Void... params) {

					return AppManager.getInstance(getActivity()).getPopNumber();
				}

				protected void onPostExecute(Integer result) {
					changePopNumTv(result);

					isTaskRunning.set(false);
				};

			}.execute();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		checkPopNum();

		// if (headerCoinTask != null)
		// {
		// headerCoinTask.onResume();
		// headerCoinTask.onWindowFocusChanged(true);
		// }
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		// if (headerCoinTask != null)
		// headerCoinTask.onWindowFocusChanged(false);
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		// if (headerCoinTask != null)
		// headerCoinTask.onStop();
		super.onStop();
	}

	public void startSearchActivity(String key) {
	}

	@Override
	public View makeView() {
		TextView tv = new TextView(getActivity());
		tv.setHint(getString(R.string.app_name));
		tv.setTextSize(18);
        tv.setEllipsize(TextUtils.TruncateAt.END);
		tv.setHintTextColor(Color.parseColor("#b9b9b9"));
		tv.setGravity(Gravity.CENTER | Gravity.LEFT);
		tv.setSingleLine();
		return tv;
	}
}