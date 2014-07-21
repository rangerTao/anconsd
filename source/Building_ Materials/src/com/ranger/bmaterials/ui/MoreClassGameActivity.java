package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.StartGame;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.broadcast.BroadcaseSender;
import com.ranger.bmaterials.mode.DownloadCallback;
import com.ranger.bmaterials.mode.DownloadItemInput;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.mode.GameTypeInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.OnlineGamesAndTypesResult;
import com.ranger.bmaterials.netresponse.SingleClassGamesResult;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.enumeration.StatusLoading;
import com.ranger.bmaterials.view.DuokuDialog;
import com.ranger.bmaterials.view.GameLabelView;

public class MoreClassGameActivity extends HeaderCoinBackBaseActivity implements OnClickListener {
	// @author liushuohui / @date 2013-09-23 / START
	private View mViewLoading;
	private View mViewLoadingFailed;
	private View mViewLoadingOngoing;
	private View mViewNoGame;
	// @author liushuohui / @date 2013-09-23 / END

	private WindowManager mWindowManager;
	private int device_dp;
	private TextView tv_title_more_games_act;
	private ImageView iv_icon_more_types_more_class_games;
	private View v_more_types_more_class_games;
	private OnlineGamesAndTypesResult mOnlineGamesAndTypesResult;
	private SingleClassGamesResult mSingleClassGamesResult;
	private int request_page = 1;
	private String game_type;
	private String game_type_number;
	private int page_count = 20;
	private View mFooterView = null;

	private LinearLayout ll_loading_more_rd_games_act;
	ArrayList<String> singe_type_and_number_list;
	int current_singlegames_request_id;

	private final static int ADAPTER_NOTIFY = 1000;
	private final static int ADAPTER_FLASH = 1001;
	private final static int ADAPTER_REFRESH_FOOTER = 1003;
	private final static int ADAPTER_ITEM_NOTIFY = 1002;

	private static final byte STATUS_LOADING = 0;
	private static final byte STATUS_NO_DATA = 1;
	private static final byte STATUS_NO_MORE = 2;
	private static final byte STATUS_NO_GONE = 3;

	ForegroundColorSpan sizeSavedColorSpan;
	ImageSpan imageSpanFace;

	private OnClickListener mReloadClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (ConnectManager.isNetworkConnected(v.getContext())) {
				refreshLoadingStatus(StatusLoading.LOADING);
				if ("0".equals(game_type)) {// 单机
					current_singlegames_request_id = NetUtil.getInstance().requestSingleClassGames(game_type, game_type_number, request_page, page_count, mSingleRequestListenerCommon);
				} else if ("1".equals(game_type)) {// 网游
					current_singlegames_request_id = NetUtil.getInstance().requestOnlineGamesAndTypes(request_page, page_count, mOnlineRequestListenerCommon);
				}
			} else {
				CustomToast.showToast(v.getContext(), v.getContext().getString(R.string.alert_network_inavailble));
			}
		}
	};

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == ADAPTER_NOTIFY) {
				adapter.notifyDataSetChanged();

				current_singlegames_request_id = -1;
			} else if (msg.what == ADAPTER_REFRESH_FOOTER) {
				Byte status = (Byte) msg.obj;

				if (null != status) {
					setFooterStatus(status.byteValue());
				}
			} else if (msg.what == ADAPTER_FLASH) {
				if (null == adapter) {
					adapter = new MyAdapter(MoreClassGameActivity.this, game_list);
					lv_more_recommend_games_act.setAdapter(adapter);
				} else {
					adapter.setDataList(game_list);
				}
			} else if (msg.what == ADAPTER_ITEM_NOTIFY) {
				final Object obj = msg.obj;

				if (null == obj) {
					if (Constants.DEBUG) {
						Log.d("MoreClass", "Return handling message ADAPTER_ITEM_NOTIFY because of NULL mode.");
					}
					return;
				}

				PackageMode modeObj = null;

				if (obj instanceof PackageMode) {
					modeObj = (PackageMode) obj;
				} else if (obj instanceof GameInfo) {
					modeObj = ((GameInfo) obj).download_status;
				}

				if (null == modeObj) {
					if (Constants.DEBUG) {
						Log.d("MoreClass", "Return handling message ADAPTER_ITEM_NOTIFY because of invalid message object.");
					}
					return;
				}

				final PackageMode mode = modeObj;
				boolean found = false;
				int index_first_item = lv_more_recommend_games_act.getFirstVisiblePosition();
				int index_last_item = lv_more_recommend_games_act.getLastVisiblePosition();
				int index_gameInfo = index_first_item;
				MyHolder holder = null;

				for (; (index_gameInfo <= index_last_item && index_gameInfo < game_list.size()); ++index_gameInfo) {
					GameInfo tmp = game_list.get(index_gameInfo);

					if (null != mode.gameId && mode.gameId.equals(tmp.getGameId())) {
						found = true;
						tmp.download_status = mode;
						break;
					}
				}

				if (!found) {
					if (Constants.DEBUG) {
						Log.d("MoreClass", "Return handling message ADAPTER_ITEM_NOTIFY because of NO game info found.");
					}
					return;
				}

				final GameInfo gameInfo = game_list.get(index_gameInfo);

				if (Constants.DEBUG) {
					Log.d("MoreClass", "To refresh view with GAME INFO: " + gameInfo.getGameId());
				}

				if (index_gameInfo >= index_first_item && index_gameInfo <= index_last_item) {
					holder = (MyHolder) lv_more_recommend_games_act.getChildAt(index_gameInfo - index_first_item).getTag();
				}

				if (holder != null) {
					adapter.refresh(holder, gameInfo);
				} else {
					if (Constants.DEBUG) {
						Log.d("MoreClass", "Return handling message ADAPTER_ITEM_NOTIFY because of NULL view holder.");
					}
				}
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	private ArrayList<GameTypeInfo> gt_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PackageHelper.registerPackageStatusChangeObserver(download_listener);

		sizeSavedColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.color_diff_update_total_size_to_download));
		imageSpanFace = new ImageSpan(getApplicationContext(), R.drawable.icon_face);

		mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);
		device_dp = dm.densityDpi;

		game_type = getIntent().getStringExtra("game_type");
		game_type_number = getIntent().getStringExtra("game_type_number");

		tv_title_more_games_act = (TextView) findViewById(R.id.header_title);
		if ("1".equals(game_type)) {
			tv_title_more_games_act.setText(getIntent().getStringExtra("title"));
		} else {
			tv_title_more_games_act.setText(getIntent().getStringExtra("title"));
		}

		try {
			gt_list = (ArrayList<GameTypeInfo>) getIntent().getSerializableExtra("cates");
		} catch (Exception e) {
			e.printStackTrace();
		}

		singe_type_and_number_list = getIntent().getStringArrayListExtra("singe_type_and_number_list");

		// @author liushuohui / @date 2013-09-23 / START
		mViewNoGame = findViewById(R.id.iv_load_no_data_view_loading);
		mViewLoading = findViewById(R.id.loading);
		mViewLoadingFailed = mViewLoading.findViewById(R.id.loading_error_layout);
		mViewLoadingOngoing = mViewLoading.findViewById(R.id.network_loading_pb);
		// @author liushuohui / @date 2013-09-23 / END

		ll_loading_more_rd_games_act = (LinearLayout) findViewById(R.id.ll_loading_more_rd_games_act);

		lv_more_recommend_games_act = (ListView) findViewById(R.id.lv_more_recommend_games_act);

		addFooterView();
		lv_more_recommend_games_act.setOnScrollListener(mScrollListener);

		current_singlegames_request_id = -1;

		iv_icon_more_types_more_class_games = (ImageView) findViewById(R.id.iv_icon_more_types_more_class_games);
		iv_icon_more_types_more_class_games.setVisibility(View.VISIBLE);

		iv_icon_more_types_more_class_games.setOnClickListener(this);
		tv_title_more_games_act.setOnClickListener(this);

		v_more_types_more_class_games = findViewById(R.id.v_more_types_more_class_games);
		v_more_types_more_class_games.setOnClickListener(this);

		// if ("0".equals(game_type)) {// 单机
		// initSingleGameMenuView();

		// current_singlegames_request_id = NetUtil.getInstance()
		// .requestSingleClassGames(game_type, game_type_number,
		// request_page, page_count,
		// mSingleRequestListenerCommon);
		// } else if ("1".equals(game_type)) {// 网游
		// current_singlegames_request_id = NetUtil.getInstance()
		// .requestOnlineGamesAndTypes(request_page, page_count,
		// mOnlineRequestListenerCommon);
		current_singlegames_request_id = NetUtil.getInstance().requestSingleClassGames(game_type, game_type_number, request_page, page_count, mSingleRequestListenerCommon);
		// }
	}

	private void addFooterView() {
		mFooterView = getLayoutInflater().inflate(R.layout.item_loading_bottom_game_list, null);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout footerParent = new RelativeLayout(this);
		footerParent.addView(mFooterView, lp);
		lv_more_recommend_games_act.addFooterView(footerParent);
	}

	@Override
	protected void onDestroy() {
		if (mPop != null && mPop.isShowing()) {
			mPop.dismiss();
		}

		PackageHelper.unregisterPackageStatusChangeObserver(download_listener);

		if (mMyInstalledReceiver != null) {
			this.unregisterReceiver(mMyInstalledReceiver);
		}

		if (mMyDownloadReceiver != null) {
			this.unregisterReceiver(mMyDownloadReceiver);
		}

		super.onDestroy();
	}

	private void initSingleGameMenuView() {
		if (singe_type_and_number_list != null) {
			if (singe_type_and_number_list.size() > 1) {
				mMenuParent = (LinearLayout) View.inflate(MoreClassGameActivity.this, R.layout.popup_menu_more_class_games, null);
				menu_layout = (LinearLayout) mMenuParent.findViewById(R.id.content);

				SinglGameMenuItemOnClickListener mSinglGameMenuItemOnClickListener = new SinglGameMenuItemOnClickListener();
				int times = singe_type_and_number_list.size();

				menu_layout.removeAllViews();

				for (int i = 0; i < times; i++) {
					View middle_view = View.inflate(MoreClassGameActivity.this, R.layout.item_bottom_popup_menu_more_class_games, null);
					Button tv3 = (Button) middle_view.findViewById(R.id.tv_item_bottom_popup_more_class_games_left);

					tv3.setText(singe_type_and_number_list.get(i).split("##")[0]);
					tv3.setTag(singe_type_and_number_list.get(i));
					tv3.setOnClickListener(mSinglGameMenuItemOnClickListener);

					tv3 = (Button) middle_view.findViewById(R.id.tv_item_bottom_popup_more_class_games_right);
					if (++i < times) {
						tv3.setText(singe_type_and_number_list.get(i).split("##")[0]);
						tv3.setTag(singe_type_and_number_list.get(i));
						tv3.setOnClickListener(mSinglGameMenuItemOnClickListener);
					} else {
						tv3.setVisibility(View.INVISIBLE);
					}

					menu_layout.addView(middle_view);
				}
			}
		}
	}

	LinearLayout mMenuParent;
	LinearLayout menu_layout;
	HashMap<String, String> game_type_map;

	private void initMenuView() {

		if (gt_list == null || gt_list.size() < 1) {
			return;
		}

		if (game_type_map == null) {
			game_type_map = new HashMap<String, String>();
		}

		mMenuParent = (LinearLayout) View.inflate(MoreClassGameActivity.this, R.layout.popup_menu_more_class_games, null);
		menu_layout = (LinearLayout) mMenuParent.findViewById(R.id.content);

		menu_layout.removeAllViews();

		MenuItemOnClickListener itemOnClickListener = new MenuItemOnClickListener();
		// view_bottom_divider
		View middle_view = null;
		for (int i = 0; i < gt_list.size(); i++) {

			GameTypeInfo gti = gt_list.get(i);

			middle_view = View.inflate(MoreClassGameActivity.this, R.layout.item_bottom_popup_menu_more_class_games, null);
			Button tv3 = (Button) middle_view.findViewById(R.id.tv_item_bottom_popup_more_class_games_left);

			// if (i < 0) {
			// tv3.setOnClickListener(firstClickListener);
			// tv3.setText("全部网游");
			// } else {
			tv3.setText(gti.getGametypename());
			tv3.setTag(gti.getGametypename());
			tv3.setOnClickListener(itemOnClickListener);

			game_type_map.put(gti.getGametypename(), gti.getGametypenumber());

			// }

			tv3 = (Button) middle_view.findViewById(R.id.tv_item_bottom_popup_more_class_games_right);
			if (++i < gt_list.size()) {
				String tname = gt_list.get(i).getGametypename();
				String tnum = gt_list.get(i).getGametypenumber();
				tv3.setText(tname);
				tv3.setTag(tname);
				tv3.setOnClickListener(itemOnClickListener);
				game_type_map.put(tname, tnum);
			} else {
				tv3.setVisibility(View.INVISIBLE);
			}

			menu_layout.addView(middle_view);
		}

		if (middle_view != null) {
			middle_view.findViewById(R.id.view_bottom_divider).setVisibility(View.GONE);
		}
	}

	class SinglGameMenuItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String type_name_and_number = (String) v.getTag();
			if (type_name_and_number != null) {
				String type_name = null;
				String type_number = null;
				try {
					type_name = type_name_and_number.split("##")[0];
					type_number = type_name_and_number.split("##")[1];
				} catch (Exception e) {
				}

				if (type_name == null || type_number == null || game_type_number.equals(type_number)) {
					mPop.dismiss();
					return;
				}

				tv_title_more_games_act.setText(type_name);

				game_type_number = type_number;
				request_page = 1;

				ll_loading_more_rd_games_act.setVisibility(View.VISIBLE);

				// @author liushuohui / @date 2013-09-23 / START: 刷新加载中
				setFooterStatus(STATUS_NO_GONE);
				refreshLoadingStatus(StatusLoading.LOADING);

				current_singlegames_request_id = NetUtil.getInstance().requestSingleClassGames(game_type, game_type_number, request_page, page_count, mSingleRequestListenerCommon);
				mPop.dismiss();
			}
		}
	}

	class MenuItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String type_name = (String) v.getTag();

			if (type_name == null || game_type_map.get(type_name) == null || game_type_map.get(type_name).equals(game_type_number)) {
				mPop.dismiss();
				return;
			}

			tv_title_more_games_act.setText(type_name);

			game_type_number = game_type_map.get(type_name);
			request_page = 1;

			ll_loading_more_rd_games_act.setVisibility(View.VISIBLE);

			// @author liushuohui / @date 2013-09-23 / START: 刷新加载中
			setFooterStatus(STATUS_NO_GONE);
			refreshLoadingStatus(StatusLoading.LOADING);

			current_singlegames_request_id = NetUtil.getInstance().requestSingleClassGames(game_type, game_type_number, request_page, page_count, mSingleRequestListenerCommon);
			mPop.dismiss();
		}
	}

	private PopupWindow mPop;

	private void initPopWindow() {
		if (mPop == null) {
			mPop = new PopupWindow(mMenuParent, LayoutParams.MATCH_PARENT, UIUtil.dip2px(this, 294f));
			mPop.setOutsideTouchable(true);
			mPop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			mPop.setFocusable(true);

			mPop.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					iv_icon_more_types_more_class_games.setImageResource(R.drawable.icon_title_more_types_more_class_games);
				}
			});
		}
	}

	ListView lv_more_recommend_games_act;
	MyAdapter adapter;
	ArrayList<GameInfo> game_list = new ArrayList<GameInfo>();
	private Map<String, GameInfo> map_games = Collections.synchronizedMap(new HashMap<String, GameInfo>());

	@SuppressLint("NewApi")
	private void initList() {

		if (mOnlineGamesAndTypesResult != null) {
			if (mOnlineGamesAndTypesResult.getGame_list().size() > 0) {
				map_games.clear();
				game_list.clear();
				map_games.putAll(mOnlineGamesAndTypesResult.getMap_game());
				game_list.addAll(mOnlineGamesAndTypesResult.getGame_list());

				if (null == adapter) {
					adapter = new MyAdapter(this, game_list);
					lv_more_recommend_games_act.setAdapter(adapter);
				} else {
					adapter.setDataList(game_list);
				}

				lv_more_recommend_games_act.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
						if (pos < game_list.size() && pos >= 0) {
							Intent intent = new Intent(MoreClassGameActivity.this, GameDetailsActivity.class);
							intent.putExtra("gameid", game_list.get(pos).getGameId());
							intent.putExtra("gamename", game_list.get(pos).getGameName());
							MoreClassGameActivity.this.startActivity(intent);
						}
					}
				});

				if (mMyInstalledReceiver == null) {
					mMyInstalledReceiver = new MyInstalledReceiver();
					IntentFilter filter = new IntentFilter();
					filter.addAction("android.intent.action.PACKAGE_ADDED");

					filter.addDataScheme("package");
					MoreClassGameActivity.this.registerReceiver(mMyInstalledReceiver, filter);
				}

				if (mMyDownloadReceiver == null) {
					mMyDownloadReceiver = new MyDownloadReceiver();
					IntentFilter filter2 = new IntentFilter();
					filter2.addAction(BroadcaseSender.ACTION_DOWNLOAD_START);
					MoreClassGameActivity.this.registerReceiver(mMyDownloadReceiver, filter2);
				}

				// lv_more_recommend_games_act.smoothScrollToPosition(0);
			} else {
				// no data
			}
		}

		if (mSingleClassGamesResult != null) {
			if (mSingleClassGamesResult.getGame_list().size() > 0) {
				map_games.clear();
				game_list.clear();
				map_games.putAll(mSingleClassGamesResult.getMap_game());
				game_list.addAll(mSingleClassGamesResult.getGame_list());

				// if (null == adapter) {
				adapter = new MyAdapter(this, game_list);
				lv_more_recommend_games_act.setAdapter(adapter);
				// } else {
				// adapter.setDataList(game_list);
				// }
				lv_more_recommend_games_act.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
						if (pos < game_list.size() && pos >= 0) {
							Intent intent = new Intent(MoreClassGameActivity.this, GameDetailsActivity.class);
							intent.putExtra("gameid", game_list.get(pos).getGameId());
							intent.putExtra("gamename", game_list.get(pos).getGameName());
							MoreClassGameActivity.this.startActivity(intent);

						}
					}
				});

				if (mMyInstalledReceiver == null) {
					mMyInstalledReceiver = new MyInstalledReceiver();
					IntentFilter filter = new IntentFilter();
					filter.addAction("android.intent.action.PACKAGE_ADDED");

					filter.addDataScheme("package");
					MoreClassGameActivity.this.registerReceiver(mMyInstalledReceiver, filter);

				}

				if (mMyDownloadReceiver == null) {
					mMyDownloadReceiver = new MyDownloadReceiver();
					IntentFilter filter2 = new IntentFilter();
					filter2.addAction("com.duoku.action.download.begin");
					MoreClassGameActivity.this.registerReceiver(mMyDownloadReceiver, filter2);
				}

//				lv_more_recommend_games_act.smoothScrollToPosition(0);
			} else {
				// no data
			}

		}

	}

	private void flashOnlineGamesAndTypesList() {
		if (mOnlineGamesAndTypesResult != null) {
			if (mOnlineGamesAndTypesResult.getGame_list().size() > 0) {
				// map_games.clear();
				// game_list.clear();
				game_list.addAll(mOnlineGamesAndTypesResult.getGame_list());
				map_games.putAll(mOnlineGamesAndTypesResult.getMap_game());

				if (null == adapter) {
					adapter = new MyAdapter(this, game_list);
					lv_more_recommend_games_act.setAdapter(adapter);
				} else {
					adapter.setDataList(game_list);
				}
			} else {
				// no data
			}

		}
	}

	private void flashSingleClassGamesList() {
		if (mSingleClassGamesResult != null) {
			if (mSingleClassGamesResult.getGame_list().size() > 0) {
				// map_games.clear();
				// game_list.clear();
				game_list.addAll(mSingleClassGamesResult.getGame_list());
				map_games.putAll(mSingleClassGamesResult.getMap_game());

				if (null == adapter) {
					adapter = new MyAdapter(this, game_list);
					lv_more_recommend_games_act.setAdapter(adapter);
				} else {
					adapter.setDataList(game_list);
				}
			} else {
				// no data
			}
		}
	}

	class MyAdapter extends BaseAdapter {
		MoreClassGameActivity context;
		LayoutInflater inflater;
		volatile ArrayList<GameInfo> list;

		private static final int TYPE_ITEM = 0;
		private static final int TYPE_LOADING = 1;

		DisplayImageOptions options = ImageLoaderHelper.getCustomOption(true, R.drawable.game_icon_list_default);

		public MyAdapter(MoreClassGameActivity c, ArrayList<GameInfo> list) {
			if (c == null) {
				return;
			}
			inflater = LayoutInflater.from(c);
			this.list = list;
			this.context = c;
		}

		public void setDataList(ArrayList<GameInfo> list) {
			this.list = list;

			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (list != null)
				return list.size();
			else
				return 0;
		}

		@Override
		public int getItemViewType(int position) {
			if (position < list.size())
				return TYPE_ITEM;
			else
				return TYPE_LOADING;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {
			MyHolder holder = null;
			GameInfo gInfo = null;

			gInfo = list.get(position);

			if (contentView != null) {
				holder = (MyHolder) contentView.getTag();
				if (holder.bt_download == null)
					contentView = null;
			}

			final GameInfo gameInfo = gInfo;

			if (contentView == null) {
				holder = new MyHolder();

				contentView = inflater.inflate(R.layout.item_list_more_games_rd_activity, null);
				holder.game_icon = (RoundCornerImageView) contentView.findViewById(R.id.iv_item_list_more_games_rd_act);
				// holder.game_icon.setDisplayImageOptions(options);
				holder.game_name = (TextView) contentView.findViewById(R.id.tv_name_item_list_more_games_rd_act);
				holder.rb_star = (RatingBar) contentView.findViewById(R.id.rating_item_list_more_games_rd_act);
				holder.ll_rb_star = (LinearLayout) contentView.findViewById(R.id.ll_rating_item_list_more_games_rd_act);
				holder.downloaded_times = (TextView) contentView.findViewById(R.id.tv_downloaded_item_list_more_games_rd_act);
				holder.size = (TextView) contentView.findViewById(R.id.tv_size_item_list_more_games_rd_act);
				holder.pb_download = (ProgressBar) contentView.findViewById(R.id.pbra_downlaod_item_list_more_games_rd_act);
				holder.pb_download_diff = (ProgressBar) contentView.findViewById(R.id.pbra_diff_downlaod_item_list_more_games_rd_act);
				holder.download_percent = (TextView) contentView.findViewById(R.id.tv_percent_item_list_more_games_rd_act);
				holder.download_percent2 = (TextView) contentView.findViewById(R.id.tv_percent_item_list_more_games_rd_act2);
				holder.download_current = (TextView) contentView.findViewById(R.id.tv_current_item_list_more_games_rd_act);
				holder.download_total = (TextView) contentView.findViewById(R.id.tv_total_item_list_more_games_rd_act);
				holder.ll_middle = (LinearLayout) contentView.findViewById(R.id.ll_download_status_item_list_more_games_rd_act);
				holder.ll_middle_diff = (LinearLayout) contentView.findViewById(R.id.ll_diff_download_status_item_list_more_games_rd_act);
				holder.ll_bottom = (LinearLayout) contentView.findViewById(R.id.ll_info_item_list_more_games_rd_act);
				holder.ll_diff_msg = (LinearLayout) contentView.findViewById(R.id.ll_update_diff_msg_item_list_more_games_rd_act);
				holder.bt_download = (TextView) contentView.findViewById(R.id.tv_bt_download_item_list_more_games_rd_act);
				holder.rl_bt_download = (RelativeLayout) contentView.findViewById(R.id.rl_tv_bt_download_item_list_more_games_rd_act);
				holder.ll_bt_download = (LinearLayout) contentView.findViewById(R.id.ll_tv_bt_download_item_list_more_games_rd_act);
				holder.comingsoon = (ImageView) contentView.findViewById(R.id.iv_comingsoon_game_list);
				holder.iv_download_bt = (ImageView) contentView.findViewById(R.id.iv_download_bt_item_list_more_games_rd_act);
				holder.iv_line_update_diff = (ImageView) contentView.findViewById(R.id.iv_line_update_diff);
				holder.tv_save_size = (TextView) contentView.findViewById(R.id.tv_save_size_item_list_more_games_rd_act);
				holder.download_current_diff = (TextView) contentView.findViewById(R.id.tv_diff_current_item_list_more_games_rd_act);
				holder.download_total_diff = (TextView) contentView.findViewById(R.id.tv_diff_total_item_list_more_games_rd_act);
				holder.tv_label_patch_size = (TextView) contentView.findViewById(R.id.tv_label_patch_size);
				holder.tv_patch_size = (TextView) contentView.findViewById(R.id.tv_patch_size);
				holder.tv_old_size = (TextView) contentView.findViewById(R.id.tv_old_size_item_list_more_games_rd_act);
				holder.tv_lower_version_name = (TextView) contentView.findViewById(R.id.tv_lower_version_name);
				holder.tv_higher_version_name = (TextView) contentView.findViewById(R.id.tv_higher_version_name);
				holder.item_card_label_name = (GameLabelView) contentView.findViewById(R.id.item_card_label_name);

				holder.front_shade = (ImageView) contentView.findViewById(R.id.iv_front_shade_item_list_more_games_rd_act);
				holder.under_shade = (ImageView) contentView.findViewById(R.id.iv_under_shade_item_list_more_games_rd_act);

				holder.front_shade.setImageBitmap(context.getFrontSade());
				holder.under_shade.setImageBitmap(context.getUnderSade());

				contentView.setTag(holder);
			} else {
				holder = (MyHolder) contentView.getTag();
			}

			holder.ll_bt_download.setTag(gameInfo);
			holder.ll_bt_download.setOnClickListener(mDownloadClickListener);

			ImageLoaderHelper.displayImage(gameInfo.getIconUrl(), holder.game_icon, options);
			// holder.game_icon.setImageUrl(gameInfo.getIconUrl());
			holder.game_name.setText(gameInfo.getGameName());
			holder.rb_star.setProgress((int) (gameInfo.getStar()));
			holder.size.setText(StringUtil.getDisplaySize(gameInfo.getSize()));

			holder.downloaded_times.setText(gameInfo.getDisplaydownloadtimes() + context.getResources().getString(R.string.label_download_times_more_rd_games));

			if (gameInfo.getLabelName() != null && !gameInfo.getLabelName().equals("")) {
				holder.item_card_label_name.setText(gameInfo.getLabelName());
				holder.item_card_label_name.setLabelColor(gameInfo.getLabelColor());
				holder.item_card_label_name.setVisibility(View.VISIBLE);
			} else {
				holder.item_card_label_name.setVisibility(View.GONE);
			}

			if ("2".equals(gameInfo.getComingsoon())) {
				holder.ll_bt_download.setVisibility(View.INVISIBLE);
				holder.comingsoon.setVisibility(View.VISIBLE);
			} else {
				holder.ll_bt_download.setVisibility(View.VISIBLE);
				holder.comingsoon.setVisibility(View.INVISIBLE);
			}

			refresh(holder, gameInfo);

			if (list.size() < 20) {
				mHandler.sendEmptyMessage(ADAPTER_NOTIFY);
			}

			return contentView;
		}

		public void refresh(MyHolder holder, GameInfo gameInfo) {
			if (gameInfo.download_status != null) {
				switch (gameInfo.download_status.status) {
				case PackageMode.UNDOWNLOAD:

					holder.ll_middle.setVisibility(View.GONE);
					holder.ll_middle_diff.setVisibility(View.GONE);

					holder.rb_star.setVisibility(View.VISIBLE);
					holder.ll_rb_star.setVisibility(View.VISIBLE);
					holder.ll_bottom.setVisibility(View.VISIBLE);

					holder.ll_diff_msg.setVisibility(View.GONE);

					holder.iv_download_bt.setImageResource(R.drawable.btn_download_selector);
					holder.download_percent2.setText(R.string.label_download);

					break;
				case PackageMode.UPDATABLE:
					holder.ll_middle.setVisibility(View.GONE);
					holder.ll_middle_diff.setVisibility(View.GONE);

					holder.rb_star.setVisibility(View.GONE);
					holder.ll_rb_star.setVisibility(View.GONE);
					holder.ll_bottom.setVisibility(View.GONE);

					holder.ll_diff_msg.setVisibility(View.VISIBLE);
					holder.iv_line_update_diff.setVisibility(View.INVISIBLE);
					holder.tv_label_patch_size.setVisibility(View.INVISIBLE);
					holder.tv_patch_size.setVisibility(View.INVISIBLE);

					holder.iv_download_bt.setImageResource(R.drawable.icon_update_list);
					holder.download_percent2.setText(R.string.label_update);

					holder.tv_old_size.setText(StringUtil.getDisplaySize(Long.valueOf(gameInfo.getSize())));

					if (gameInfo.download_status.localVersion != null && gameInfo.download_status.localVersion.equals(gameInfo.download_status.version)) {
						holder.tv_lower_version_name.setText(gameInfo.download_status.localVersion + "(" + gameInfo.download_status.localVersionCode + ")");
						holder.tv_higher_version_name.setText(gameInfo.download_status.version + "(" + gameInfo.download_status.versionCode + ")");
					} else {
						holder.tv_lower_version_name.setText(gameInfo.download_status.localVersion);
						holder.tv_higher_version_name.setText(gameInfo.download_status.version);
					}

					break;
				case PackageMode.UPDATABLE_DIFF:
					holder.ll_middle.setVisibility(View.GONE);
					holder.ll_middle_diff.setVisibility(View.GONE);

					holder.rb_star.setVisibility(View.GONE);
					holder.ll_rb_star.setVisibility(View.GONE);
					holder.ll_bottom.setVisibility(View.GONE);

					holder.ll_diff_msg.setVisibility(View.VISIBLE);
					holder.iv_line_update_diff.setVisibility(View.VISIBLE);
					holder.tv_label_patch_size.setVisibility(View.VISIBLE);
					holder.tv_patch_size.setVisibility(View.VISIBLE);

					SpannableString sizeString = new SpannableString("1"
							+ String.format(context.getString(R.string.update_managment_hint_patchsize), Formatter.formatFileSize(context, gameInfo.download_status.pacthSize)));
					sizeString.setSpan(sizeSavedColorSpan, 0, sizeString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
					sizeString.setSpan(imageSpanFace, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
					holder.tv_patch_size.setText(sizeString);

					holder.iv_download_bt.setImageResource(R.drawable.btn_download_diff_update_selector);
					holder.download_percent2.setText(R.string.label_diff_update);

					holder.tv_old_size.setText(StringUtil.getDisplaySize(gameInfo.download_status.totalApkSize));

					if (gameInfo.download_status.localVersion != null && gameInfo.download_status.localVersion.equals(gameInfo.download_status.version)) {
						holder.tv_lower_version_name.setText(gameInfo.download_status.localVersion + "(" + gameInfo.download_status.localVersionCode + ")");
						holder.tv_higher_version_name.setText(gameInfo.download_status.version + "(" + gameInfo.download_status.versionCode + ")");
					} else {
						holder.tv_lower_version_name.setText(gameInfo.download_status.localVersion);
						holder.tv_higher_version_name.setText(gameInfo.download_status.version);
					}

					break;

				case PackageMode.DOWNLOAD_RUNNING:
					if (!gameInfo.download_status.isDiffDownload) {
						holder.ll_middle.setVisibility(View.GONE);
						holder.ll_middle_diff.setVisibility(View.GONE);

						holder.rb_star.setVisibility(View.VISIBLE);
						holder.ll_rb_star.setVisibility(View.VISIBLE);
						holder.ll_bottom.setVisibility(View.VISIBLE);

						holder.ll_diff_msg.setVisibility(View.GONE);
						holder.download_percent2.setText((int) ((gameInfo.download_status.currentSize * 1.0f / Long.valueOf(gameInfo.getSize())) * 100) + "%");
						holder.iv_download_bt.setImageResource(R.drawable.btn_download_pause_selector);
					} else {
						holder.ll_middle.setVisibility(View.GONE);
						holder.ll_middle_diff.setVisibility(View.VISIBLE);

						holder.rb_star.setVisibility(View.GONE);
						holder.ll_rb_star.setVisibility(View.GONE);
						holder.ll_bottom.setVisibility(View.GONE);

						holder.ll_diff_msg.setVisibility(View.GONE);

						long save_size = gameInfo.download_status.totalApkSize - gameInfo.download_status.pacthSize;
						int secondary_progress = (int) ((save_size * 1.0f / gameInfo.download_status.totalApkSize) * 100);
						int first_progress = (int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalApkSize) * 100) + secondary_progress;

						holder.pb_download_diff.setProgress(first_progress);
						holder.pb_download_diff.setSecondaryProgress(secondary_progress);

						holder.tv_save_size.setText("节省" + StringUtil.getDisplaySize(save_size));

						holder.download_total_diff.setText("/" + StringUtil.getDisplaySize(gameInfo.download_status.pacthSize));
						holder.download_current_diff.setText(StringUtil.getDisplaySize(gameInfo.download_status.currentSize));

						holder.download_percent2.setText(R.string.label_pause);
						holder.iv_download_bt.setImageResource(R.drawable.btn_download_pause_selector);
					}

					break;

				case PackageMode.DOWNLOAD_PAUSED:
					if (!gameInfo.download_status.isDiffDownload) {
						holder.ll_middle.setVisibility(View.GONE);
						holder.ll_middle_diff.setVisibility(View.GONE);

						holder.rb_star.setVisibility(View.VISIBLE);
						holder.ll_rb_star.setVisibility(View.VISIBLE);
						holder.ll_bottom.setVisibility(View.VISIBLE);

						holder.ll_diff_msg.setVisibility(View.GONE);

						holder.download_percent2.setText((int) ((gameInfo.download_status.currentSize * 1.0f / Long.valueOf(gameInfo.getSize())) * 100) + "%");
						holder.iv_download_bt.setImageResource(R.drawable.icon_resume_list);
					} else {
						holder.ll_middle.setVisibility(View.GONE);
						holder.ll_middle_diff.setVisibility(View.VISIBLE);

						holder.rb_star.setVisibility(View.GONE);
						holder.ll_rb_star.setVisibility(View.GONE);
						holder.ll_bottom.setVisibility(View.GONE);

						holder.ll_diff_msg.setVisibility(View.GONE);

						long save_size = gameInfo.download_status.totalApkSize - gameInfo.download_status.pacthSize;
						int first_progress = (int) ((save_size * 1.0f / gameInfo.download_status.totalApkSize) * 100);
						int secondary_progress = (int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalApkSize) * 100) + first_progress;
						if (secondary_progress >= 100)
							secondary_progress = 99;
						holder.pb_download_diff.setProgress(secondary_progress);
						holder.pb_download_diff.setSecondaryProgress(first_progress);

						holder.tv_save_size.setText("节省" + StringUtil.getDisplaySize(save_size));

						holder.download_total_diff.setText("/" + StringUtil.getDisplaySize(gameInfo.download_status.pacthSize));
						holder.download_current_diff.setText(StringUtil.getDisplaySize(gameInfo.download_status.currentSize));

						holder.download_percent2.setText(R.string.label_continue);
						holder.iv_download_bt.setImageResource(R.drawable.icon_resume_list);
					}

					break;

				case PackageMode.CHECKING_FINISHED:
				case PackageMode.DOWNLOADED:
					holder.ll_middle.setVisibility(View.GONE);
					holder.ll_middle_diff.setVisibility(View.GONE);

					holder.rb_star.setVisibility(View.VISIBLE);
					holder.ll_rb_star.setVisibility(View.VISIBLE);
					holder.ll_bottom.setVisibility(View.VISIBLE);

					holder.ll_diff_msg.setVisibility(View.GONE);

					holder.download_percent2.setText(R.string.label_install);
					holder.iv_download_bt.setImageResource(R.drawable.icon_install_list);

					break;

				case PackageMode.DOWNLOAD_FAILED:
					holder.ll_middle.setVisibility(View.GONE);
					holder.ll_middle_diff.setVisibility(View.GONE);

					holder.rb_star.setVisibility(View.VISIBLE);
					holder.ll_rb_star.setVisibility(View.VISIBLE);
					holder.ll_bottom.setVisibility(View.VISIBLE);

					holder.ll_diff_msg.setVisibility(View.GONE);
					holder.download_percent2.setText(R.string.label_retry);
					holder.iv_download_bt.setImageResource(R.drawable.btn_download_retry_selector);

					break;

				case PackageMode.INSTALLED:
					holder.ll_middle.setVisibility(View.GONE);
					holder.ll_middle_diff.setVisibility(View.GONE);

					holder.rb_star.setVisibility(View.VISIBLE);
					holder.ll_rb_star.setVisibility(View.VISIBLE);
					holder.ll_bottom.setVisibility(View.VISIBLE);

					holder.ll_diff_msg.setVisibility(View.GONE);

					holder.download_percent2.setText(R.string.label_start);
					holder.iv_download_bt.setImageResource(R.drawable.icon_start_list);
					break;
				case PackageMode.CHECKING:
				case PackageMode.MERGING:
					holder.ll_middle.setVisibility(View.GONE);
					holder.ll_middle_diff.setVisibility(View.GONE);

					holder.rb_star.setVisibility(View.VISIBLE);
					holder.ll_rb_star.setVisibility(View.VISIBLE);
					holder.ll_bottom.setVisibility(View.VISIBLE);

					holder.ll_diff_msg.setVisibility(View.GONE);

					holder.download_percent2.setText(R.string.label_checking_diff_update);
					holder.iv_download_bt.setImageResource(R.drawable.icon_checking_list);
					break;
				case PackageMode.MERGE_FAILED:
					holder.ll_middle.setVisibility(View.GONE);
					holder.ll_middle_diff.setVisibility(View.VISIBLE);

					holder.rb_star.setVisibility(View.GONE);
					holder.ll_rb_star.setVisibility(View.GONE);
					holder.ll_bottom.setVisibility(View.GONE);

					holder.ll_diff_msg.setVisibility(View.GONE);

					holder.download_percent2.setText(R.string.label_retry);
					holder.iv_download_bt.setImageResource(R.drawable.btn_download_retry_selector);
					break;
				case PackageMode.MERGED:
					holder.ll_middle.setVisibility(View.GONE);
					holder.ll_middle_diff.setVisibility(View.GONE);

					holder.rb_star.setVisibility(View.VISIBLE);
					holder.ll_rb_star.setVisibility(View.VISIBLE);
					holder.ll_bottom.setVisibility(View.VISIBLE);

					holder.ll_diff_msg.setVisibility(View.GONE);

					holder.download_percent2.setText(R.string.label_install);
					holder.iv_download_bt.setImageResource(R.drawable.icon_install_list);

					break;

				case PackageMode.INSTALLING:
					holder.ll_middle.setVisibility(View.GONE);
					holder.ll_middle_diff.setVisibility(View.GONE);

					holder.rb_star.setVisibility(View.VISIBLE);
					holder.ll_rb_star.setVisibility(View.VISIBLE);
					holder.ll_bottom.setVisibility(View.VISIBLE);

					holder.ll_diff_msg.setVisibility(View.GONE);

					holder.download_percent2.setText(R.string.label_installing);
					holder.iv_download_bt.setImageResource(R.drawable.a_installing);
					AnimationDrawable animationDrawable = (AnimationDrawable) holder.iv_download_bt.getDrawable();
					animationDrawable.start();
					break;

				case PackageMode.DOWNLOAD_PENDING:
					if (!gameInfo.download_status.isDiffDownload) {
						holder.ll_middle.setVisibility(View.GONE);
						holder.ll_middle_diff.setVisibility(View.GONE);

						holder.rb_star.setVisibility(View.VISIBLE);
						holder.ll_rb_star.setVisibility(View.VISIBLE);
						holder.ll_bottom.setVisibility(View.VISIBLE);

						holder.ll_diff_msg.setVisibility(View.GONE);
						holder.download_percent2.setText(R.string.label_waiting);
						holder.iv_download_bt.setImageResource(R.drawable.icon_waiting_list);
					} else {
						holder.ll_middle.setVisibility(View.GONE);
						holder.ll_middle_diff.setVisibility(View.VISIBLE);

						holder.rb_star.setVisibility(View.GONE);
						holder.ll_rb_star.setVisibility(View.GONE);
						holder.ll_bottom.setVisibility(View.GONE);

						holder.ll_diff_msg.setVisibility(View.GONE);

						long save_size = gameInfo.download_status.totalApkSize - gameInfo.download_status.pacthSize;
						int first_progress = (int) ((save_size * 1.0f / gameInfo.download_status.totalApkSize) * 100);
						int secondary_progress = (int) ((gameInfo.download_status.currentSize * 1.0f / gameInfo.download_status.totalApkSize) * 100) + first_progress;
						if (secondary_progress >= 100)
							secondary_progress = 99;
						holder.pb_download_diff.setProgress(secondary_progress);
						holder.pb_download_diff.setSecondaryProgress(first_progress);

						holder.tv_save_size.setText("节省" + StringUtil.getDisplaySize(save_size));

						holder.download_total_diff.setText("/" + StringUtil.getDisplaySize(gameInfo.download_status.pacthSize));
						holder.download_current_diff.setText(StringUtil.getDisplaySize(gameInfo.download_status.currentSize));

						holder.download_percent2.setText(R.string.label_pause);
						holder.iv_download_bt.setImageResource(R.drawable.icon_waiting_list);
					}
					break;

				}
			} else {
				CustomToast.showToast(context, gameInfo.getGameName() + " no status");
			}
		}
	}// end adapter class

	class MyHolder {
		RoundCornerImageView game_icon;
		TextView game_name;
		RatingBar rb_star;
		LinearLayout ll_rb_star;
		TextView downloaded_times;
		TextView size;
		ProgressBar pb_download;
		ProgressBar pb_download_diff;
		TextView download_percent;
		TextView download_percent2;
		TextView download_current;
		TextView download_total;
		TextView bt_download;
		RelativeLayout rl_bt_download;
		LinearLayout ll_bt_download;
		ImageView comingsoon;
		ImageView iv_download_bt;
		ImageView iv_line_update_diff;
		TextView tv_label_patch_size;
		TextView tv_patch_size;
		TextView tv_save_size;
		TextView download_current_diff;
		TextView download_total_diff;
		TextView tv_old_size;
		TextView tv_lower_version_name;
		TextView tv_higher_version_name;
		GameLabelView item_card_label_name;

		LinearLayout ll_middle;
		LinearLayout ll_middle_diff;
		LinearLayout ll_bottom;
		LinearLayout ll_diff_msg;

		ImageView under_shade;
		ImageView front_shade;
	}

	private void adaptTriangle(View view) {
		String title = tv_title_more_games_act.getText().toString();
		TextPaint paint = tv_title_more_games_act.getPaint();
		float width = paint.measureText(title);

		if (mMenuParent != null) {
			View triangle = mMenuParent.findViewById(R.id.uptriangle);

			if (null != triangle) {
				android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) triangle.getLayoutParams();

				lp.leftMargin = (int) (width / 2) + UIUtil.dip2px(this, 22f);
				lp.gravity = Gravity.CENTER_HORIZONTAL;

				triangle.setLayoutParams(lp);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_title:
		case R.id.v_more_types_more_class_games:
		case R.id.iv_icon_more_types_more_class_games:
			if (mMenuParent == null)
				return;
			if (mPop == null) {
				initPopWindow();
				mPop.dismiss();
				mPop.showAsDropDown(findViewById(R.id.rl_header), 0, 0);
				// adaptTriangle(iv_icon_more_types_more_class_games);
			} else {
				if (!mPop.isShowing()) {
					mPop.showAsDropDown(findViewById(R.id.rl_header), 0, 0);
					// adaptTriangle(iv_icon_more_types_more_class_games);
				} else
					mPop.dismiss();
			}
			iv_icon_more_types_more_class_games.setImageResource(R.drawable.icon_title_more_types_more_class_games_close);
			break;
		case R.id.loading_text:
			finish();
			break;
		}

	}

	MyInstalledReceiver mMyInstalledReceiver;

	class MyInstalledReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
				String data = intent.getDataString();
				String packageName = null;
				if (data != null)
					packageName = data.substring(data.indexOf(":") + 1);

				GameInfo gameInfo = map_games.get(packageName);
				if (gameInfo != null) {
					gameInfo.game_status = GameInfo.GAME_INSTALLED;
					gameInfo.download_status.status = PackageMode.INSTALLED;

					Message msg = mHandler.obtainMessage();

					msg.what = ADAPTER_ITEM_NOTIFY;
					msg.obj = gameInfo;

					mHandler.sendMessage(msg);
				}
			}
		}
	}

	MyDownloadReceiver mMyDownloadReceiver;

	class MyDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcaseSender.ACTION_DOWNLOAD_START)) {
				if (game_type_number == null && "1".equals(game_type)) {
					if (mOnlineGamesAndTypesResult != null) {
						String packageName = intent.getStringExtra("pkgname");
						GameInfo gameInfo = map_games.get(packageName);
						if (gameInfo != null) {
							gameInfo.isInit = false;

							Message msg = mHandler.obtainMessage();

							msg.what = ADAPTER_ITEM_NOTIFY;
							msg.obj = gameInfo;

							mHandler.sendMessage(msg);
						}
					}
				} else {
					if (mSingleClassGamesResult != null) {
						String packageName = intent.getStringExtra("pkgname");
						GameInfo gameInfo = map_games.get(packageName);
						if (gameInfo != null) {
							gameInfo.isInit = false;

							Message msg = mHandler.obtainMessage();

							msg.what = ADAPTER_ITEM_NOTIFY;
							msg.obj = gameInfo;

							mHandler.sendMessage(msg);
						}
					}
				}
			}
		}
	}

	HashMap<String, GameInfo> wait2download_map = new HashMap<String, GameInfo>();
	private final static int REQUEST_DOWNLOAD_IN_WAP_NETWORK = 1000;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null && REQUEST_DOWNLOAD_IN_WAP_NETWORK == requestCode) {
			String downloadurl = data.getStringExtra(DownloadDialogActivity.ARG2);
			final GameInfo gameInfo = wait2download_map.remove(downloadurl);
			if (gameInfo != null) {
				switch (gameInfo.download_status.status) {
				case PackageMode.UNDOWNLOAD:
					DownloadItemInput dInfo = new DownloadItemInput();
					dInfo.setGameId(gameInfo.getGameId());
					dInfo.setDownloadUrl(gameInfo.getDownloadurl());
					dInfo.setDisplayName(gameInfo.getGameName());
					dInfo.setPackageName(gameInfo.getPkgname());
					dInfo.setIconUrl(gameInfo.getIconUrl());
					dInfo.setAction(gameInfo.getStartaction());
					dInfo.setNeedLogin(gameInfo.isNeedlogin());
					dInfo.setVersion(gameInfo.getGameversion());
					dInfo.setVersionInt(gameInfo.getGameversioncode());
					try {
						dInfo.setSize(Long.parseLong(gameInfo.getSize()));
					} catch (NumberFormatException e) {
						dInfo.setSize(0);
					}

					PackageHelper.download(dInfo, new DownloadCallback() {

						@Override
						public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
						}

						@Override
						public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
							// TODO called in sub thread
							if (!status) {
								Message msg = mHandler.obtainMessage();

								msg.what = ADAPTER_ITEM_NOTIFY;
								msg.obj = gameInfo;
								msg.arg1 = reason;

								mHandler.sendMessage(msg);
							}

						}

						@Override
						public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
						}
					});

					break;
				case PackageMode.UPDATABLE_DIFF:
					DownloadItemInput dInfo2 = new DownloadItemInput();
					dInfo2.setGameId(gameInfo.download_status.gameId);
					dInfo2.setDownloadUrl(gameInfo.download_status.downloadUrl);
					dInfo2.setDisplayName(gameInfo.getGameName());
					dInfo2.setPackageName(gameInfo.download_status.packageName);
					dInfo2.setIconUrl(gameInfo.getIconUrl());
					dInfo2.setAction(gameInfo.getStartaction());
					dInfo2.setNeedLogin(gameInfo.isNeedlogin());
					dInfo2.setVersion(gameInfo.download_status.version);
					dInfo2.setVersionInt(gameInfo.download_status.versionCode);

					dInfo2.setSize(gameInfo.download_status.pacthSize);

					PackageHelper.download(dInfo2, new DownloadCallback() {

						@Override
						public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
						}

						@Override
						public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
							// TODO called in sub thread
							if (!status) {
								Message msg = mHandler.obtainMessage();

								msg.what = ADAPTER_ITEM_NOTIFY;
								msg.obj = gameInfo;
								msg.arg1 = reason;

								mHandler.sendMessage(msg);
							}

						}

						@Override
						public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
						}
					});

					break;
				case PackageMode.DOWNLOAD_PAUSED:
					PackageHelper.resumeDownload(gameInfo.download_status.downloadId, new DownloadCallback() {
						@Override
						public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
							// TODO called in sub thread
							if (!successful) {
								Message msg = mHandler.obtainMessage();

								msg.what = ADAPTER_ITEM_NOTIFY;
								msg.obj = gameInfo;
								msg.arg1 = reason;

								mHandler.sendMessage(msg);
							}

						}

						@Override
						public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
						}

						@Override
						public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
						}
					});
					DownloadStatistics.addResumeDownloadGameStatistics(getApplicationContext(), gameInfo.getGameName());
					// }
					break;

				case PackageMode.DOWNLOAD_FAILED:

					PackageHelper.restartDownload(gameInfo.download_status.downloadId, new DownloadCallback() {

						@Override
						public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
						}

						@Override
						public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
						}

						@Override
						public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
							// TODO called in sub thread
							if (!successful) {
								Message msg = mHandler.obtainMessage();

								msg.what = ADAPTER_ITEM_NOTIFY;
								msg.obj = gameInfo;
								msg.arg1 = reason;

								mHandler.sendMessage(msg);
							}
						}
					});

					DownloadStatistics.addDownloadGameStatistics(getApplicationContext(), gameInfo.getGameName(), false);
					break;

				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	Bitmap default_icon;

	public Bitmap getDefaultIcon() {
		if (default_icon == null) {
			default_icon = BitmapFactory.decodeResource(getResources(), R.drawable.game_icon_list_default);
		}

		return default_icon;
	}

	Bitmap front_shade;
	Bitmap under_shade;

	public Bitmap getFrontSade() {
		if (front_shade == null) {
			front_shade = BitmapFactory.decodeResource(getResources(), R.drawable.icon_front_shade_game_list);
		}

		return front_shade;
	}

	public Bitmap getUnderSade() {
		if (under_shade == null) {
			under_shade = BitmapFactory.decodeResource(getResources(), R.drawable.icon_under_shade_game_list);
		}

		return under_shade;
	}

	// @author liushuohui / @date 2013-09-23 / START
	private void refreshLoadingStatus(StatusLoading status) {
		mViewLoadingFailed.setClickable(false);
		switch (status) {
		case INVALID: {
			// RESERVED
		}
			break;

		case LOADING: {
			mViewLoading.setVisibility(View.VISIBLE);
			mViewLoadingOngoing.setVisibility(View.VISIBLE);
			mViewLoadingFailed.setVisibility(View.GONE);

			mViewNoGame.setVisibility(View.GONE);
		}
			break;

		case SUCCEED: {
			mViewLoading.setVisibility(View.GONE);
			mViewNoGame.setVisibility(View.GONE);
		}
			break;

		case FAILED: {
			mViewLoading.setVisibility(View.VISIBLE);
			mViewLoadingOngoing.setVisibility(View.GONE);
			mViewLoadingFailed.setVisibility(View.VISIBLE);
			mViewLoadingFailed.setOnClickListener(mReloadClick);
			mViewLoadingFailed.setClickable(true);

			mViewNoGame.setVisibility(View.GONE);
		}
			break;

		case NONE: {
			mViewLoading.setVisibility(View.GONE);
			mViewNoGame.setVisibility(View.VISIBLE);
		}
			break;

		default:
			break;
		}
	}

	// @author liushuohui / @date 2013-09-23 / END

	@Override
	public int getLayout() {
		return R.layout.more_class_games_activity;
	}

	@Override
	public String getHeaderTitle() {
		return null;
	}

	private OnClickListener mDownloadClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final GameInfo gameInfo = (GameInfo) v.getTag();

			if (null == gameInfo) {
				return;
			}

			if (gameInfo.download_status != null) {
				switch (gameInfo.download_status.status) {
				case PackageMode.UNDOWNLOAD:
				case PackageMode.UPDATABLE:

					if (ConnectManager.isNetworkConnected(MoreClassGameActivity.this)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							CustomToast.showToast(MoreClassGameActivity.this, "请检查您的SD卡");
							break;
						}

						if (gameInfo.getDownloadurl() == null || "".equals(gameInfo.getDownloadurl())) {
							CustomToast.showToast(MoreClassGameActivity.this, "无下载地址");
							v.setClickable(false);
							break;
						}

						v.setClickable(true);

						if (ConnectManager.isWifi(MoreClassGameActivity.this) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
							// 直接下载
							DownloadItemInput dInfo = new DownloadItemInput();
							dInfo.setGameId(gameInfo.getGameId());
							// dInfo.setSaveName(Md5Tools.toMd5(gameInfo.getDownloadurl().getBytes(),
							// true));
							dInfo.setDownloadUrl(gameInfo.getDownloadurl());
							dInfo.setDisplayName(gameInfo.getGameName());
							dInfo.setPackageName(gameInfo.getPkgname());
							dInfo.setIconUrl(gameInfo.getIconUrl());
							dInfo.setAction(gameInfo.getStartaction());
							dInfo.setNeedLogin(gameInfo.isNeedlogin());
							dInfo.setVersion(gameInfo.getGameversion());
							dInfo.setVersionInt(gameInfo.getGameversioncode());
							try {
								dInfo.setSize(Long.parseLong(gameInfo.getSize()));
							} catch (NumberFormatException e) {
								dInfo.setSize(0);
							}

							Log.i("whb", "download game name:" + gameInfo.getGameName());
							PackageHelper.download(dInfo, new DownloadCallback() {

								@Override
								public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
								}

								@Override
								public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
									// TODO called in sub thread
									if (!status) {
										Message msg = mHandler.obtainMessage();

										msg.what = ADAPTER_ITEM_NOTIFY;
										msg.obj = gameInfo;
										msg.arg1 = reason;

										mHandler.sendMessage(msg);
									}
								}

								@Override
								public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
								}
							});
						} else {
							// 非wifi下需要用户确认是否下载
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							DuokuDialog.showNetworkAlertDialog(MoreClassGameActivity.this, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}

					} else {
						// CustomToast.showToast(MoreClassGameActivity.this,
						// "请检查您的网络连接");
					}
					break;
				case PackageMode.UPDATABLE_DIFF:
					if (ConnectManager.isNetworkConnected(MoreClassGameActivity.this)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							CustomToast.showToast(MoreClassGameActivity.this, "请检查您的SD卡");
							break;
						}

						if (gameInfo.getDownloadurl() == null || "".equals(gameInfo.getDownloadurl())) {
							CustomToast.showToast(MoreClassGameActivity.this, "无下载地址");
							v.setClickable(false);
							break;
						}

						v.setClickable(true);

						if (ConnectManager.isWifi(MoreClassGameActivity.this) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
							// 直接下载
							DownloadItemInput dInfo = new DownloadItemInput();
							dInfo.setGameId(gameInfo.download_status.gameId);
							dInfo.setDownloadUrl(gameInfo.download_status.downloadUrl);
							dInfo.setDisplayName(gameInfo.getGameName());
							dInfo.setPackageName(gameInfo.download_status.packageName);
							dInfo.setIconUrl(gameInfo.getIconUrl());
							dInfo.setAction(gameInfo.getStartaction());
							dInfo.setNeedLogin(gameInfo.isNeedlogin());
							dInfo.setVersion(gameInfo.download_status.version);
							dInfo.setVersionInt(gameInfo.download_status.versionCode);

							dInfo.setSize(gameInfo.download_status.pacthSize);

							Log.i("whb", "download game name:" + gameInfo.getGameName());
							PackageHelper.download(dInfo, new DownloadCallback() {

								@Override
								public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
								}

								@Override
								public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
									// TODO called in sub thread
									if (!status) {
										Message msg = mHandler.obtainMessage();

										msg.what = ADAPTER_ITEM_NOTIFY;
										msg.obj = gameInfo;
										msg.arg1 = reason;

										mHandler.sendMessage(msg);
									}

								}

								@Override
								public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
								}
							});
						} else {
							// 非wifi下需要用户确认是否下载
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							DuokuDialog.showNetworkAlertDialog(MoreClassGameActivity.this, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}
					} else {
						// CustomToast.showToast(MoreClassGameActivity.this,
						// "请检查您的网络连接");
					}
					break;

				case PackageMode.DOWNLOAD_PENDING:
				case PackageMode.DOWNLOAD_RUNNING:
					long donwloadid = gameInfo.download_status.downloadId;// dai.getDownloadId();

					if (donwloadid > 0) {
						PackageHelper.pauseDownloadGames(donwloadid);
					} else {
						PackageHelper.pauseDownloadGames(gameInfo.getDownloadurl());
					}

					DownloadStatistics.addUserPauseDownloadGameStatistics(getApplicationContext(), gameInfo.getGameName());

					break;

				case PackageMode.DOWNLOAD_PAUSED:

					if (ConnectManager.isNetworkConnected(MoreClassGameActivity.this)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							CustomToast.showToast(MoreClassGameActivity.this, "请检查您的SD卡");
							break;
						}
						if (ConnectManager.isWifi(MoreClassGameActivity.this) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {

							PackageHelper.resumeDownload(gameInfo.download_status.downloadId, new DownloadCallback() {

								@Override
								public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
									// TODO called in sub thread
									if (!successful) {
										Message msg = mHandler.obtainMessage();

										msg.what = ADAPTER_ITEM_NOTIFY;
										msg.obj = gameInfo;
										msg.arg1 = reason;

										mHandler.sendMessage(msg);
									}

								}

								@Override
								public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
								}

								@Override
								public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
								}
							});
							DownloadStatistics.addResumeDownloadGameStatistics(getApplicationContext(), gameInfo.getGameName());

						} else {
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							DuokuDialog.showNetworkAlertDialog(MoreClassGameActivity.this, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}

					} else {
						// CustomToast.showToast(MoreClassGameActivity.this,
						// "请检查您的网络连接");
					}

					break;
				case PackageMode.CHECKING_FINISHED:
				case PackageMode.DOWNLOADED:
					if (!gameInfo.download_status.isDiffDownload) {
						PackageHelper.installApp(MoreClassGameActivity.this, gameInfo.getGameId(), gameInfo.getPkgname(), gameInfo.download_status.downloadDest);
					} else {
						PackageHelper.sendMergeRequestFromUI(gameInfo.download_status.downloadId);
					}
					break;
				case PackageMode.MERGED:
					PackageHelper.installApp(MoreClassGameActivity.this, gameInfo.getGameId(), gameInfo.getPkgname(), gameInfo.download_status.downloadDest);
					break;
				case PackageMode.MERGE_FAILED:
					if (gameInfo.download_status.mergeFailedCount >= 2) {
						if (ConnectManager.isNetworkConnected(MoreClassGameActivity.this)) {
							if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
								CustomToast.showToast(MoreClassGameActivity.this, "请检查您的SD卡");
								break;
							}
							if (ConnectManager.isWifi(MoreClassGameActivity.this) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {

								PackageHelper.restartDownload(gameInfo.download_status.downloadId, new DownloadCallback() {

									@Override
									public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
									}

									@Override
									public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
									}

									@Override
									public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
										// TODO called in sub
										// thread
										if (!successful) {
											Message msg = mHandler.obtainMessage();

											msg.what = ADAPTER_ITEM_NOTIFY;
											msg.obj = gameInfo;
											msg.arg1 = reason;

											mHandler.sendMessage(msg);
										}
									}
								});

								DownloadStatistics.addDownloadGameStatistics(getApplicationContext(), gameInfo.getGameName(), false);
							} else {
								wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
								DuokuDialog.showNetworkAlertDialog(MoreClassGameActivity.this, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
							}
						} else {
							// CustomToast.showToast(MoreClassGameActivity.this,
							// "请检查您的网络连接");
						}
					} else {
						PackageHelper.sendMergeRequestFromUI(gameInfo.download_status.downloadId);
					}
					break;

				case PackageMode.DOWNLOAD_FAILED:
					if (ConnectManager.isNetworkConnected(MoreClassGameActivity.this)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							CustomToast.showToast(MoreClassGameActivity.this, "请检查您的SD卡");
							break;
						}
						if (ConnectManager.isWifi(MoreClassGameActivity.this) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
							PackageHelper.restartDownload(gameInfo.download_status.downloadId, new DownloadCallback() {

								@Override
								public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
								}

								@Override
								public void onDownloadResult(String downloadUrl, boolean status, long downloadId, String saveDest, Integer reason) {
								}

								@Override
								public void onRestartDownloadResult(String downloadUrl, String saveDest, boolean successful, Integer reason) {
									// TODO called in sub thread
									if (!successful) {
										Message msg = mHandler.obtainMessage();

										msg.what = ADAPTER_ITEM_NOTIFY;
										msg.obj = gameInfo;
										msg.arg1 = reason;

										mHandler.sendMessage(msg);
									}

								}
							});

							DownloadStatistics.addDownloadGameStatistics(getApplicationContext(), gameInfo.getGameName(), false);
						} else {
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							DuokuDialog.showNetworkAlertDialog(MoreClassGameActivity.this, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}
					} else {
						// CustomToast.showToast(MoreClassGameActivity.this,
						// "请检查您的网络连接");
					}
					break;

				case PackageMode.INSTALLED:
					new StartGame(MoreClassGameActivity.this, gameInfo.getPkgname(), gameInfo.getStartaction(), gameInfo.getGameId(), gameInfo.isNeedlogin()).startGame();
					break;

				default:
					break;
				}
			} else
				CustomToast.showToast(MoreClassGameActivity.this, gameInfo.getGameName() + " no status");
		}
	};

	private void loadMore() {
		if (!ConnectManager.isNetworkConnected(this)) {
			CustomToast.showToast(this, getString(R.string.alert_network_inavailble));
			return;
		}

		if (game_type_number == null && "1".equals(game_type)) {
			if (!(null == mOnlineGamesAndTypesResult || mOnlineGamesAndTypesResult.getTotalCount() > game_list.size())) {
				return;
			}

			if (current_singlegames_request_id > 0) {
				return;
			}

			setFooterStatus(STATUS_LOADING);

			current_singlegames_request_id = NetUtil.getInstance().requestOnlineGamesAndTypes(request_page, page_count, mOnlineRequestListenerSimple);
		} else {
			if (!(null == mSingleClassGamesResult || mSingleClassGamesResult.getTotalCount() > game_list.size())) {
				return;
			}

			if (current_singlegames_request_id > 0) {
				return;
			}

			setFooterStatus(STATUS_LOADING);

			current_singlegames_request_id = NetUtil.getInstance().requestSingleClassGames(game_type, game_type_number, request_page, page_count, mSingleRequestListenerSimple);
		}
	}

	private boolean mLastItemVisible;

	OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mLastItemVisible && scrollState == SCROLL_STATE_IDLE) {
				loadMore();
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
		}
	};

	private void setFooterStatus(byte status) {
		if (STATUS_NO_GONE == status) {
			mFooterView.setVisibility(View.GONE);
			return;
		}

		mFooterView.setVisibility(View.VISIBLE);
		TextView tv = (TextView) mFooterView.findViewById(R.id.loading_text);
		ProgressBar progress = (ProgressBar) mFooterView.findViewById(R.id.loading_progress);

		if (STATUS_NO_MORE == status) {
			if (null != tv) {
				tv.setText(R.string.footer_no_more_see_more);
			}

			if (null != progress) {
				progress.setVisibility(View.GONE);
			}

			tv.setTextColor(getResources().getColor(R.color.no_more_data_text));
			tv.setOnClickListener(this);
		} else if (STATUS_LOADING == status) {
			if (null != tv) {
				tv.setText(R.string.pull_to_refresh_refreshing_label);
			}

			if (null != progress) {
				progress.setVisibility(View.VISIBLE);
			}

			tv.setOnClickListener(null);
		} else if (STATUS_NO_DATA == status) {
			if (null != tv) {
				tv.setText(R.string.pull_to_refresh_from_bottom_pull_label);
			}

			if (null != progress) {
				progress.setVisibility(View.GONE);
			}
			tv.setOnClickListener(null);
		} else {
			// RESERVED
		}
	}

	PackageCallback download_listener = new PackageCallback() {

		@Override
		public void onPackageStatusChanged(PackageMode mode) {
			if (Constants.DEBUG) {
				Log.d("MoreClass", "On package status changed >> status: " + mode.status + " | game id: " + mode.gameId);
			}

			Message msg = mHandler.obtainMessage();

			msg.what = ADAPTER_ITEM_NOTIFY;
			msg.obj = mode;

			mHandler.sendMessage(msg);
		}
	};

	private IRequestListener mSingleRequestListenerCommon = new IRequestListener() {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			if (current_singlegames_request_id != responseData.getRequestID())
				return;
			mSingleClassGamesResult = (SingleClassGamesResult) responseData;

			current_singlegames_request_id = -1;

			new Thread(new Runnable() {

				@Override
				public void run() {
					byte status = STATUS_NO_DATA;

					if (mSingleClassGamesResult != null) {
						if (mSingleClassGamesResult.getGame_list().size() > 0) {
							for (GameInfo gIn : mSingleClassGamesResult.getGame_list()) {
								QueryInput qin = new QueryInput();
								qin.gameId = gIn.getGameId();
								qin.packageName = gIn.getPkgname();
								qin.version = gIn.getGameversion();
								qin.versionCode = gIn.getGameversioncode();
								qin.downloadUrl = gIn.getDownloadurl();
								gIn.qin = qin;

								gIn.download_status = PackageHelper.queryPackageStatus(gIn.qin).get(gIn.qin);
							}
							setFooterStatus(STATUS_NO_DATA);
						} else {
							status = STATUS_NO_MORE;
							mHandler.sendMessage(mHandler.obtainMessage(ADAPTER_REFRESH_FOOTER, Byte.valueOf(status)));
						}

						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if (request_page == 1) {
									initList();
									initMenuView();
								} else {
									flashSingleClassGamesList();
								}

								if (mSingleClassGamesResult.getTotalCount() <= game_list.size()) {
									mHandler.sendMessage(mHandler.obtainMessage(ADAPTER_REFRESH_FOOTER, Byte.valueOf(STATUS_NO_MORE)));
								}

								request_page++;

								if (mSingleClassGamesResult != null) {
									if (mSingleClassGamesResult.getGame_list().size() == 0) {
										// @author liushuohui /
										// @date 2013-09-23 / START:
										// 刷新暂无游戏
										refreshLoadingStatus(StatusLoading.NONE);
									} else {
										// @author liushuohui /
										// @date 2013-09-23 / START:
										// 刷新成功
										refreshLoadingStatus(StatusLoading.SUCCEED);

										ll_loading_more_rd_games_act.setVisibility(View.GONE);
									}
								} else {
									ll_loading_more_rd_games_act.setVisibility(View.GONE);
								}
							}
						});
					}
				}
			}).start();
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			if (current_singlegames_request_id != requestId) {
				return;
			}

			current_singlegames_request_id = -1;

			// @author liushuohui / @date 2013-09-23 / START: 刷新加载失败
			refreshLoadingStatus(StatusLoading.FAILED);

			setFooterStatus(STATUS_NO_DATA);
		}
	};

	private IRequestListener mSingleRequestListenerSimple = new IRequestListener() {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			if (current_singlegames_request_id != responseData.getRequestID())
				return;
			mSingleClassGamesResult = (SingleClassGamesResult) responseData;

			current_singlegames_request_id = -1;

			new Thread(new Runnable() {

				@Override
				public void run() {
					byte status = STATUS_NO_DATA;

					if (mSingleClassGamesResult != null) {
						if (mSingleClassGamesResult.getGame_list().size() > 0) {
							for (GameInfo gIn : mSingleClassGamesResult.getGame_list()) {
								QueryInput qin = new QueryInput();
								qin.gameId = gIn.getGameId();
								qin.packageName = gIn.getPkgname();
								qin.version = gIn.getGameversion();
								qin.versionCode = gIn.getGameversioncode();
								qin.downloadUrl = gIn.getDownloadurl();
								gIn.qin = qin;

								gIn.download_status = PackageHelper.queryPackageStatus(gIn.qin).get(gIn.qin);
							}

							game_list.addAll(mSingleClassGamesResult.getGame_list());
							map_games.putAll(mSingleClassGamesResult.getMap_game());
							request_page++;

							if (mSingleClassGamesResult.getTotalCount() <= game_list.size()) {
								status = STATUS_NO_MORE;
							}
						} else {
							status = STATUS_NO_MORE;
						}
						mHandler.sendEmptyMessage(ADAPTER_NOTIFY);
					}

					mHandler.sendMessage(mHandler.obtainMessage(ADAPTER_REFRESH_FOOTER, Byte.valueOf(status)));
				}
			}).start();
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			current_singlegames_request_id = -1;

			setFooterStatus(STATUS_NO_DATA);
		}
	};

	private IRequestListener mOnlineRequestListenerCommon = new IRequestListener() {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			if (current_singlegames_request_id != responseData.getRequestID())
				return;
			mOnlineGamesAndTypesResult = (OnlineGamesAndTypesResult) responseData;

			current_singlegames_request_id = -1;

			new Thread(new Runnable() {

				@Override
				public void run() {
					byte status = STATUS_NO_DATA;

					if (mOnlineGamesAndTypesResult != null) {
						if (mOnlineGamesAndTypesResult.getGame_list().size() > 0) {
							for (GameInfo gIn : mOnlineGamesAndTypesResult.getGame_list()) {
								QueryInput qin = new QueryInput();
								qin.gameId = gIn.getGameId();
								qin.packageName = gIn.getPkgname();
								qin.version = gIn.getGameversion();
								qin.versionCode = gIn.getGameversioncode();
								qin.downloadUrl = gIn.getDownloadurl();
								gIn.qin = qin;

								gIn.download_status = PackageHelper.queryPackageStatus(gIn.qin).get(gIn.qin);
							}

							mHandler.post(new Runnable() {

								@Override
								public void run() {
									if (request_page == 1) {
										initList();
										initMenuView();
									} else {
										flashOnlineGamesAndTypesList();
									}

									if (mOnlineGamesAndTypesResult.getTotalCount() <= game_list.size()) {
										mHandler.sendMessage(mHandler.obtainMessage(ADAPTER_REFRESH_FOOTER, Byte.valueOf(STATUS_NO_MORE)));
									}

									request_page++;

									if (mOnlineGamesAndTypesResult != null) {
										if (mOnlineGamesAndTypesResult.getGame_list().size() == 0) {
											// @author liushuohui /
											// @date 2013-09-23 / START:
											// 刷新暂无游戏
											refreshLoadingStatus(StatusLoading.NONE);
										} else {
											// @author liushuohui /
											// @date 2013-09-23 / START:
											// 刷新成功
											refreshLoadingStatus(StatusLoading.SUCCEED);

											ll_loading_more_rd_games_act.setVisibility(View.GONE);
										}
									} else {
										ll_loading_more_rd_games_act.setVisibility(View.GONE);
									}
								}
							});
						} else {
							status = STATUS_NO_MORE;

							mHandler.sendMessage(mHandler.obtainMessage(ADAPTER_REFRESH_FOOTER, Byte.valueOf(status)));
						}
					}
				}
			}).start();
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			if (current_singlegames_request_id != requestId) {
				return;
			}

			current_singlegames_request_id = -1;

			// @author liushuohui / @date 2013-09-23 / START: 刷新加载失败
			refreshLoadingStatus(StatusLoading.FAILED);

			setFooterStatus(STATUS_NO_DATA);
		}
	};

	private IRequestListener mOnlineRequestListenerSimple = new IRequestListener() {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			if (current_singlegames_request_id != responseData.getRequestID())
				return;
			mOnlineGamesAndTypesResult = (OnlineGamesAndTypesResult) responseData;

			current_singlegames_request_id = -1;

			new Thread(new Runnable() {

				@Override
				public void run() {
					byte status = STATUS_NO_DATA;

					if (mOnlineGamesAndTypesResult != null) {
						if (mOnlineGamesAndTypesResult.getGame_list().size() > 0) {
							for (GameInfo gIn : mOnlineGamesAndTypesResult.getGame_list()) {
								QueryInput qin = new QueryInput();
								qin.gameId = gIn.getGameId();
								qin.packageName = gIn.getPkgname();
								qin.version = gIn.getGameversion();
								qin.versionCode = gIn.getGameversioncode();
								qin.downloadUrl = gIn.getDownloadurl();
								gIn.qin = qin;

								gIn.download_status = PackageHelper.queryPackageStatus(gIn.qin).get(gIn.qin);
							}

							game_list.addAll(mOnlineGamesAndTypesResult.getGame_list());
							map_games.putAll(mOnlineGamesAndTypesResult.getMap_game());
							request_page++;

							if (mOnlineGamesAndTypesResult.getTotalCount() <= game_list.size()) {
								status = STATUS_NO_MORE;
							}
						} else {
							status = STATUS_NO_MORE;
						}
						mHandler.sendEmptyMessage(ADAPTER_NOTIFY);
					}

					mHandler.sendMessage(mHandler.obtainMessage(ADAPTER_REFRESH_FOOTER, Byte.valueOf(status)));
				}
			}).start();
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			current_singlegames_request_id = -1;

			setFooterStatus(STATUS_NO_DATA);
		}
	};
}
