package com.ranger.bmaterials.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.GameMoreDataResult;
import com.ranger.bmaterials.statistics.DownloadStatistics;
import com.ranger.bmaterials.tools.ApkUtil;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.tools.DialogFactory;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.ui.enumeration.StatusLoading;
import com.ranger.bmaterials.view.DuokuDialog;
import com.ranger.bmaterials.view.GameLabelView;

public class MoreGameFragment extends HeaderHallBaseFragment {

	// @author liushuohui / @date 2013-09-23 / START
	private View mViewLoading;

	private View mViewLoadingFailed;

	private View mViewLoadingOngoing;

	private View mViewNoGame;

	// @author liushuohui / @date 2013-09-23 / END

	private TextView tv_title_more_games_act;

	private GameMoreDataResult mGameMoreDataResult;

	private LinearLayout ll_loading_more_rd_games_act;

	private int request_page = 1;

	private String more_type;

	private int mCntFiltered = 0;

	private int page_count = 20;

	private View mFooterView = null;

	private final static int ADAPTER_NOTIFY = 1000;

	private final static int ADAPTER_ITEM_NOTIFY = 1001;

	private final static int ADAPTER_REFRESH_FOOTER = 1002;

	private final static int REQUEST_DOWNLOAD_IN_WAP_NETWORK = 1000;

	private static final byte STATUS_LOADING = 0;

	private static final byte STATUS_NO_DATA = 1;

	private static final byte STATUS_NO_MORE = 2;

	private volatile int mRequestId = -1;

	public static final String EXTRA_KEY_COUNT_FILTERED = "count_filtered";
	
	private Dialog rootDialog;

	// @author liushuohui / @date 2013-09-23 / START
	private View.OnClickListener mReloadListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ConnectManager.isNetworkConnected(v.getContext())) {
				requestData();
			} else {
				CustomToast.showToast(v.getContext(), v.getContext().getString(R.string.alert_network_inavailble));
			}
		}
	};

	// @author liushuohui / @date 2013-09-23 / END

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == ADAPTER_ITEM_NOTIFY) {
				final Object obj = msg.obj;

				if (null == obj) {
					if (Constants.DEBUG) {
						Log.d("MoreGame_" + more_type, "Return handling message ADAPTER_ITEM_NOTIFY because of NULL mode.");
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
						Log.d("MoreGame_" + more_type, "Return handling message ADAPTER_ITEM_NOTIFY because of invalid message object.");
					}
					return;
				}

				if (null == lv_more_recommend_games_act) {
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
						Log.d("MoreGame_" + more_type, "Return handling message ADAPTER_ITEM_NOTIFY because of NO game info found.");
					}
					return;
				}

				final GameInfo gameInfo = game_list.get(index_gameInfo);

				if (Constants.DEBUG) {
					Log.d("MoreGame_" + more_type, "To refresh view with GAME INFO: " + gameInfo.getGameId());
				}

				if (index_gameInfo >= index_first_item && index_gameInfo <= index_last_item) {
					holder = (MyHolder) lv_more_recommend_games_act.getChildAt(index_gameInfo - index_first_item).getTag();
				}

				if (holder != null) {
					holder.game_name.setText(gameInfo.getGameName());
					holder.rb_star.setProgress((int) (gameInfo.getStar()));
					holder.size.setText(StringUtil.getDisplaySize(gameInfo.getSize()));
					if ("0".equals(more_type) || "1".equals(more_type)) {
						holder.downloaded_times.setText(gameInfo.getDisplaydownloadtimes() + mContext.getResources().getString(R.string.label_download_times_more_rd_games));
					} else if ("2".equals(more_type)) {
						holder.downloaded_times.setText(gameInfo.getUpdatetimedate());
					}

					if (gameInfo.download_status != null) {
						if (gameInfo.download_status.mergeFailedCount >= 2) {
							gameInfo.download_status.isDiffDownload = false;
						}

						holder.refresh(gameInfo);
					} else
						CustomToast.showToast(mContext, gameInfo.getGameName() + " no status");
				} else {
					if (Constants.DEBUG) {
						Log.d("MoreGame_" + more_type, "Return handling message ADAPTER_ITEM_NOTIFY because of NULL view holder.");
					}
				}
			} else if (msg.what == ADAPTER_NOTIFY) {
				adapter.notifyDataSetChanged();
			} else if (msg.what == ADAPTER_REFRESH_FOOTER) {
				Byte status = (Byte) msg.obj;

				if (null != status) {
					setFooterStatus(mFooterView, status.byteValue());
				}
			}

			super.handleMessage(msg);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// new Thread(new Runnable()
		// {
		//
		// @Override
		// public void run()
		// {
		// if (game_list != null && game_list.size() > 0)
		// {
		// for (GameInfo gIn : game_list)
		// {
		// QueryInput qin = new QueryInput();
		// qin.gameId = gIn.getGameId();
		// qin.packageName = gIn.getPkgname();
		// qin.version = gIn.getGameversion();
		// qin.versionCode = gIn.getGameversioncode();
		// qin.downloadUrl = gIn.getDownloadurl();
		// gIn.qin = qin;
		// gIn.download_status = PackageHelper.queryPackageStatus(
		// gIn.qin).get(gIn.qin);
		// }
		// if (adapter != null && mHandler != null)
		// {
		// Message msg = mHandler.obtainMessage();
		// msg.what = ADAPTER_ITEM_NOTIFY;
		// mHandler.sendMessage(msg);
		// }
		// }
		// }
		// }).start();
		StatService.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);
	}

	private IRequestListener mReqListenerMore = new IRequestListener() {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			mGameMoreDataResult = (GameMoreDataResult) responseData;

			new Thread(new Runnable() {

				@Override
				public void run() {
					byte status = STATUS_NO_DATA;

					if (mGameMoreDataResult != null) {
						if (mGameMoreDataResult.getList_game().size() > 0) {

							for (GameInfo gIn : mGameMoreDataResult.getList_game()) {
								QueryInput qin = new QueryInput();
								qin.gameId = gIn.getGameId();
								qin.packageName = gIn.getPkgname();
								qin.version = gIn.getGameversion();
								qin.versionCode = gIn.getGameversioncode();
								qin.downloadUrl = gIn.getDownloadurl();
								gIn.qin = qin;

								gIn.download_status = PackageHelper.queryPackageStatus(gIn.qin).get(gIn.qin);
							}

							game_list.addAll(mGameMoreDataResult.getList_game());
							map_games.putAll(mGameMoreDataResult.getMap_game());
							request_page++;

							if (mGameMoreDataResult.getTotalCount() <= mCntFiltered + game_list.size()) {
								status = STATUS_NO_MORE;
							}
						} else {
							status = STATUS_NO_MORE;
						}
						mHandler.sendEmptyMessage(ADAPTER_NOTIFY);
					}

					mHandler.sendMessage(mHandler.obtainMessage(ADAPTER_REFRESH_FOOTER, Byte.valueOf(status)));

					mRequestId = -1;
				}
			}).start();
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			setFooterStatus(mFooterView, STATUS_NO_DATA);
			mRequestId = -1;
		}
	};

	private IRequestListener mReqListenerFirst = new IRequestListener() {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			if(getActivity()==null){
				return;
			}
			mGameMoreDataResult = (GameMoreDataResult) responseData;
			new Thread(new Runnable() {

				@Override
				public void run() {
					byte status = STATUS_NO_DATA;

					if (mGameMoreDataResult != null) {
						if (mGameMoreDataResult.getList_game().size() > 0) {
							if (mGameMoreDataResult.getTotalCount() <= mCntFiltered + mGameMoreDataResult.getList_game().size()) {
								status = STATUS_NO_MORE;
							}

							for (GameInfo gIn : mGameMoreDataResult.getList_game()) {
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
									request_page++;
									initList();

									if (mGameMoreDataResult != null) {
										if (mGameMoreDataResult.getList_game().size() == 0) {
											// @author liushuohui / @date
											// 2013-09-23 / START: 刷新暂无游戏
											refreshLoadingStatus(StatusLoading.NONE);
										} else {
											// @author liushuohui / @date
											// 2013-09-23 / START: 刷新成功
											refreshLoadingStatus(StatusLoading.SUCCEED);

											ll_loading_more_rd_games_act.setVisibility(View.GONE);
										}
									} else {
										ll_loading_more_rd_games_act.setVisibility(View.GONE);
									}

									mRequestId = -1;
								}
							});

						} else {
							status = STATUS_NO_MORE;
						}
					}

					mHandler.sendMessage(mHandler.obtainMessage(ADAPTER_REFRESH_FOOTER, Byte.valueOf(status)));
				}
			}).start();
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			// @author liushuohui / @date 2013-09-23 / START: 刷新加载失败
			setRequestFailedStatus();
		}

	};

	private void setRequestFailedStatus() {
		refreshLoadingStatus(StatusLoading.FAILED);
		mRequestId = -1;
		setFooterStatus(mFooterView, STATUS_NO_DATA);
	}

	private void requestData() {
		if (DeviceUtil.isNetworkAvailable(mContext)) {
			mRequestId = NetUtil.getInstance().requestGameMoreData(more_type, request_page, page_count, mReqListenerFirst);
		} else {
			CustomToast.showToast(mContext, getString(R.string.alert_network_inavailble));
			setRequestFailedStatus();
		}
	}

	private Activity mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (root != null) {
			ViewParent parent = this.root.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(this.root);

				return root;
			}
		}
		root = inflater.inflate(R.layout.more_recommend_games_activity, null);
		initHeader();
		// root.findViewById(R.id.iv_header_title_icon).setVisibility(View.GONE);
		// root.findViewById(R.id.more_recomd_title).setVisibility(View.GONE);

		PackageHelper.registerPackageStatusChangeObserver(download_listener);

		mContext = getActivity();

		PackageHelper.registerPackageStatusChangeObserver(download_listener);
		Intent intent = mContext.getIntent();

		more_type = intent.getStringExtra("more_type");
		if (more_type == null)
			more_type = "1";

		mCntFiltered = intent.getIntExtra(EXTRA_KEY_COUNT_FILTERED, 0);

		tv_title_more_games_act = (TextView) root.findViewById(R.id.header_title);
		if ("0".equals(more_type)) {
			// tv_title_more_games_act
			// .setText(R.string.title_more_recommend_game_activity);
			root.findViewById(R.id.more_game_title).setVisibility(View.GONE);
		} else if ("1".equals(more_type)) {
			tv_title_more_games_act.setText(R.string.title_more_hot_game_activity);
			root.findViewById(R.id.more_game_title).setVisibility(View.GONE);
		} else if ("2".equals(more_type)) {
			tv_title_more_games_act.setText(R.string.title_more_new_game_activity);
		} else if ("3".equals(more_type)) {
			tv_title_more_games_act.setText(R.string.home_section_title);
			more_type = "2";
		}

		if (null == lv_more_recommend_games_act) {
			// lv_more_recommend_games_act = (PinnedSectionListView)
			// findViewById(R.id.lv_more_recommend_games_act);
			// lv_more_recommend_games_act.setHandleId(R.id.handle);
			lv_more_recommend_games_act = (ListView) root.findViewById(R.id.lv_more_recommend_games_act);

			if (null == mFooterView) {
				mFooterView = inflater.inflate(R.layout.item_loading_bottom_game_list, null);
				lv_more_recommend_games_act.addFooterView(mFooterView);
				lv_more_recommend_games_act.setOnScrollListener(mScrollListener);
			}
		}

		// @author liushuohui / @date 2013-09-23 / START
		mViewNoGame = root.findViewById(R.id.iv_load_no_data_view_loading);
		mViewLoading = root.findViewById(R.id.loading);
		mViewLoadingFailed = mViewLoading.findViewById(R.id.loading_error_layout);
		mViewLoadingOngoing = mViewLoading.findViewById(R.id.network_loading_pb);

		mViewLoading.setOnClickListener(mReloadListener);

		refreshLoadingStatus(StatusLoading.LOADING);
		// @author liushuohui / @date 2013-09-23 / END

		ll_loading_more_rd_games_act = (LinearLayout) root.findViewById(R.id.ll_loading_more_rd_games_act);

		requestData();
		return root;
	}

	@Override
	public void onDestroy() {
		// TODO remove download listeners
		PackageHelper.unregisterPackageStatusChangeObserver(download_listener);

		if (mMyInstalledReceiver != null) {
			mContext.unregisterReceiver(mMyInstalledReceiver);
		}

		if (mMyDownloadReceiver != null) {
			mContext.unregisterReceiver(mMyDownloadReceiver);
		}
		if (rootDialog != null) {
			rootDialog.dismiss();
			rootDialog = null;
		}
		super.onDestroy();
	}

	// PinnedSectionListView lv_more_recommend_games_act;
	ListView lv_more_recommend_games_act;

	MyAdapter adapter;

	ArrayList<GameInfo> game_list = new ArrayList<GameInfo>();

	private HashMap<String, GameInfo> map_games = new HashMap<String, GameInfo>();

	private void initList() {
		if (mGameMoreDataResult != null && getActivity() != null) {
			if (mGameMoreDataResult.getList_game().size() > 0) {
				map_games.putAll(mGameMoreDataResult.getMap_game());
				game_list.addAll(mGameMoreDataResult.getList_game());
				adapter = new MyAdapter(this, game_list);

				lv_more_recommend_games_act.setAdapter(adapter);

				lv_more_recommend_games_act.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
						if (pos < game_list.size() && pos >= 0) {
							Intent intent = new Intent(mContext, GameDetailsActivity.class);
							intent.putExtra("gameid", game_list.get(pos).getGameId());
							intent.putExtra("gamename", game_list.get(pos).getGameName());
							mContext.startActivity(intent);

						}
					}
				});
				mMyInstalledReceiver = new MyInstalledReceiver();
				IntentFilter filter = new IntentFilter();
				filter.addAction("android.intent.action.PACKAGE_ADDED");

				filter.addDataScheme("package");
				mContext.registerReceiver(mMyInstalledReceiver, filter);

				mMyDownloadReceiver = new MyDownloadReceiver();
				IntentFilter filter2 = new IntentFilter();
				filter2.addAction(BroadcaseSender.ACTION_DOWNLOAD_START);
				mContext.registerReceiver(mMyDownloadReceiver, filter2);
			} else {
				// no data
			}

		}

	}

	// class MyAdapter extends BaseAdapter implements PinnedSectionListAdapter
	// {
	// final int LISTTYPE_ITEM_SECTION = 0;
	// final int LISTTYPE_ITEM_GAME = 1;
	// MoreGameActivity context;
	//
	// LayoutInflater inflater;
	//
	// ArrayList<GameInfo> list;
	//
	// DisplayImageOptions options = ImageLoaderHelper
	// .getCustomOption(R.drawable.game_icon_list_default);
	//
	// public MyAdapter(MoreGameActivity c, ArrayList<GameInfo> list)
	// {
	// inflater = LayoutInflater.from(c);
	// this.list = list;
	// this.context = c;
	// }
	//
	// @Override
	// public int getCount()
	// {
	// if (list != null)
	// return list.size();
	// else
	// return 0;
	// }
	//
	// @Override
	// public Object getItem(int arg0)
	// {
	// return list.get(arg0);
	// }
	//
	// @Override
	// public long getItemId(int arg0)
	// {
	// return arg0;
	// }
	//
	// @Override
	// public View getView(int position, View contentView, ViewGroup arg2)
	// {
	// MyHolder holder = null;
	// GameInfo gInfo = null;
	//
	// gInfo = list.get(position);
	//
	// if (contentView != null)
	// {
	// holder = (MyHolder) contentView.getTag();
	//
	// if (holder == null || holder.bt_download == null)
	// {
	// contentView = null;
	// }
	// }
	//
	// final GameInfo gameInfo = gInfo;
	// if (contentView == null)
	// {
	// holder = new MyHolder();
	// if(getItemViewType(position)==LISTTYPE_ITEM_SECTION){
	// contentView = inflater.inflate(
	// R.layout.selection_item_list_more_games_rd_activity, null);
	// }else{
	// contentView = inflater.inflate(
	// R.layout.item_list_more_games_rd_activity, null);
	// }
	// holder.card_label = (GameLabelView) contentView
	// .findViewById(R.id.item_card_label_name);
	// holder.game_icon = (NetImageView) contentView
	// .findViewById(R.id.iv_item_list_more_games_rd_act);
	// holder.game_icon.setDisplayImageOptions(options);
	// holder.game_name = (TextView) contentView
	// .findViewById(R.id.tv_name_item_list_more_games_rd_act);
	// holder.rb_star = (RatingBar) contentView
	// .findViewById(R.id.rating_item_list_more_games_rd_act);
	// holder.ll_rb_star = (LinearLayout) contentView
	// .findViewById(R.id.ll_rating_item_list_more_games_rd_act);
	// holder.downloaded_times = (TextView) contentView
	// .findViewById(R.id.tv_downloaded_item_list_more_games_rd_act);
	// holder.size = (TextView) contentView
	// .findViewById(R.id.tv_size_item_list_more_games_rd_act);
	// holder.pb_download = (ProgressBar) contentView
	// .findViewById(R.id.pbra_downlaod_item_list_more_games_rd_act);
	// holder.pb_download_diff = (ProgressBar) contentView
	// .findViewById(R.id.pbra_diff_downlaod_item_list_more_games_rd_act);
	// holder.download_percent = (TextView) contentView
	// .findViewById(R.id.tv_percent_item_list_more_games_rd_act);
	// holder.download_percent2 = (TextView) contentView
	// .findViewById(R.id.tv_percent_item_list_more_games_rd_act2);
	// holder.download_current = (TextView) contentView
	// .findViewById(R.id.tv_current_item_list_more_games_rd_act);
	// holder.download_total = (TextView) contentView
	// .findViewById(R.id.tv_total_item_list_more_games_rd_act);
	// holder.ll_middle = (LinearLayout) contentView
	// .findViewById(R.id.ll_download_status_item_list_more_games_rd_act);
	// holder.ll_middle_diff = (LinearLayout) contentView
	// .findViewById(R.id.ll_diff_download_status_item_list_more_games_rd_act);
	// holder.ll_bottom = (LinearLayout) contentView
	// .findViewById(R.id.ll_info_item_list_more_games_rd_act);
	// holder.ll_diff_msg = (LinearLayout) contentView
	// .findViewById(R.id.ll_update_diff_msg_item_list_more_games_rd_act);
	// holder.bt_download = (TextView) contentView
	// .findViewById(R.id.tv_bt_download_item_list_more_games_rd_act);
	// holder.rl_bt_download = (RelativeLayout) contentView
	// .findViewById(R.id.rl_tv_bt_download_item_list_more_games_rd_act);
	// holder.ll_bt_download = (LinearLayout) contentView
	// .findViewById(R.id.ll_tv_bt_download_item_list_more_games_rd_act);
	// holder.comingsoon = (ImageView) contentView
	// .findViewById(R.id.iv_comingsoon_game_list);
	// holder.iv_download_bt = (ImageView) contentView
	// .findViewById(R.id.iv_download_bt_item_list_more_games_rd_act);
	// holder.iv_line_update_diff = (ImageView) contentView
	// .findViewById(R.id.iv_line_update_diff);
	// holder.tv_save_size = (TextView) contentView
	// .findViewById(R.id.tv_save_size_item_list_more_games_rd_act);
	// holder.download_current_diff = (TextView) contentView
	// .findViewById(R.id.tv_diff_current_item_list_more_games_rd_act);
	// holder.download_total_diff = (TextView) contentView
	// .findViewById(R.id.tv_diff_total_item_list_more_games_rd_act);
	// holder.tv_label_patch_size = (TextView) contentView
	// .findViewById(R.id.tv_label_patch_size);
	// holder.tv_patch_size = (TextView) contentView
	// .findViewById(R.id.tv_patch_size);
	// holder.tv_old_size = (TextView) contentView
	// .findViewById(R.id.tv_old_size_item_list_more_games_rd_act);
	// holder.tv_lower_version_name = (TextView) contentView
	// .findViewById(R.id.tv_lower_version_name);
	// holder.tv_higher_version_name = (TextView) contentView
	// .findViewById(R.id.tv_higher_version_name);
	// holder.front_shade = (ImageView) contentView
	// .findViewById(R.id.iv_front_shade_item_list_more_games_rd_act);
	// holder.under_shade = (ImageView) contentView
	// .findViewById(R.id.iv_under_shade_item_list_more_games_rd_act);
	// holder.front_shade.setImageBitmap(context.getFrontSade());
	// holder.under_shade.setImageBitmap(context.getUnderSade());
	//
	// contentView.setTag(holder);
	// }
	// else
	// {
	// holder = (MyHolder) contentView.getTag();
	// }
	// if(getItemViewType(position)==LISTTYPE_ITEM_SECTION){
	// TextView
	// timerTv=(TextView)contentView.findViewById(lv_more_recommend_games_act.getHandle());
	// String timer = gameInfo.getUpdatetimedate().replace("-", ".");
	// timerTv.setText(timer.substring(timer.indexOf(".")+1));
	// }
	// holder.ll_bt_download.setTag(gameInfo);
	// holder.ll_bt_download.setOnClickListener(mDownloadClick);
	//
	// // ImageLoaderHelper.displayImage(gameInfo.getIconUrl(),
	// // holder.game_icon, options);
	// holder.game_icon.setImageUrl(gameInfo.getIconUrl());
	// holder.game_name.setText(gameInfo.getGameName());
	// holder.rb_star.setProgress((int) (gameInfo.getStar()));
	// holder.size.setText(StringUtil.getDisplaySize(gameInfo.getSize()));
	// if ("0".equals(more_type) || "1".equals(more_type))
	// {
	// holder.downloaded_times.setText(gameInfo
	// .getDisplaydownloadtimes()
	// + context.getResources().getString(
	// R.string.label_download_times_more_rd_games));
	// }
	// else if ("2".equals(more_type))
	// {
	// holder.downloaded_times.setText(gameInfo.getUpdatetimedate());
	// }
	//
	// if ("2".equals(gameInfo.getComingsoon()))
	// {
	// holder.ll_bt_download.setVisibility(View.INVISIBLE);
	// holder.comingsoon.setVisibility(View.VISIBLE);
	// }
	// else
	// {
	// holder.ll_bt_download.setVisibility(View.VISIBLE);
	// holder.comingsoon.setVisibility(View.INVISIBLE);
	// }
	//
	// if (gameInfo.getLabelName() != null
	// && !gameInfo.getLabelName().equals(""))
	// {
	// holder.card_label.setText(gameInfo.getLabelName());
	// holder.card_label.setLabelColor(gameInfo.getLabelColor());
	// holder.card_label.setVisibility(View.VISIBLE);
	// }
	// else
	// {
	// holder.card_label.setVisibility(View.GONE);
	// }
	//
	// if (gameInfo.download_status != null)
	// {
	// holder.refresh(gameInfo);
	// }
	// else
	// CustomToast.showToast(context, gameInfo.getGameName()
	// + " no status");
	//
	// return contentView;
	// }
	// @Override
	// public int getItemViewType(int position) {
	// if(position==0)
	// return LISTTYPE_ITEM_SECTION;
	// if(!list.get(position).getUpdatetimedate().equals(list.get(position-1).getUpdatetimedate()))
	// return LISTTYPE_ITEM_SECTION;
	// return LISTTYPE_ITEM_GAME;
	// }
	// @Override
	// public int getViewTypeCount() {
	// return 2;
	// }
	//
	// @Override
	// public boolean isItemViewTypePinned(int viewType) {
	// return viewType == LISTTYPE_ITEM_SECTION;
	// }
	// }
	// end adapter class
	class MyAdapter extends BaseAdapter {
		// MoreGameActivity context;
		MoreGameFragment context;

		LayoutInflater inflater;

		ArrayList<GameInfo> list;

		DisplayImageOptions options = ImageLoaderHelper.getCustomOption(R.drawable.game_icon_list_default);

		public MyAdapter(MoreGameFragment c, ArrayList<GameInfo> list) {
			inflater = LayoutInflater.from(c.getActivity());
			this.list = list;
			this.context = c;
		}

		@Override
		public int getCount() {
			if (list != null)
				return list.size();
			else
				return 0;
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

				if (holder == null || holder.bt_download == null) {
					contentView = null;
				}
			}

			final GameInfo gameInfo = gInfo;
			if (contentView == null) {
				holder = new MyHolder();

				contentView = inflater.inflate(R.layout.item_list_more_games_rd_activity, null);
				holder.card_label = (GameLabelView) contentView.findViewById(R.id.item_card_label_name);
				holder.game_icon = (RoundCornerImageView) contentView.findViewById(R.id.iv_item_list_more_games_rd_act);
				holder.game_icon.setDisplayImageOptions(options);

				// holder.ranking = (CircleTextview)
				// contentView.findViewById(R.id.ranking);
				holder.ranking = (TextView) contentView.findViewById(R.id.ranking);

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
				holder.front_shade = (ImageView) contentView.findViewById(R.id.iv_front_shade_item_list_more_games_rd_act);
				holder.under_shade = (ImageView) contentView.findViewById(R.id.iv_under_shade_item_list_more_games_rd_act);
				// holder.front_shade.setImageBitmap(context.getFrontSade());
				// holder.under_shade.setImageBitmap(context.getUnderSade());

				contentView.setTag(holder);
			} else {
				holder = (MyHolder) contentView.getTag();
			}

			holder.ll_bt_download.setTag(gameInfo);
			holder.ll_bt_download.setOnClickListener(mDownloadClick);

			// ImageLoaderHelper.displayImage(gameInfo.getIconUrl(),
			// holder.game_icon, options);
			// holder.game_icon.setImageUrl(gameInfo.getIconUrl());
			ImageLoaderHelper.displayImage(gameInfo.getIconUrl(), holder.game_icon, ImageLoaderHelper.getCustomOption(true, R.drawable.game_icon_list_default));

			holder.ranking.setTextColor(Color.WHITE);
			// holder.ranking.setRanking(position+1);
			holder.ranking.setText(String.valueOf(position + 1));
			holder.ranking.setVisibility(View.VISIBLE);
			if (position == 0) {
				// holder.ranking.setLabelColor("#FF0000");
				holder.ranking.setBackgroundResource(R.drawable.ranking_bg_1st);
			} else if (position == 1) {
				// holder.ranking.setLabelColor("#00FF00");
				holder.ranking.setBackgroundResource(R.drawable.ranking_bg_2ed);
			} else if (position == 2) {
				// holder.ranking.setLabelColor("#0000FF");
				holder.ranking.setBackgroundResource(R.drawable.ranking_bg_3rd);
			} else if (position > 98) {
				holder.ranking.setVisibility(View.GONE);
			} else {
				// holder.ranking.setLabelColor("#AAAAAA");
				holder.ranking.setTextColor(Color.parseColor("#666666"));
				holder.ranking.setBackgroundResource(R.drawable.ranking_bg_huise);
			}
			holder.game_name.setText(gameInfo.getGameName());
			holder.rb_star.setProgress((int) (gameInfo.getStar()));
			holder.size.setText(StringUtil.getDisplaySize(gameInfo.getSize()));
			if ("0".equals(more_type) || "1".equals(more_type)) {
				holder.downloaded_times.setText(gameInfo.getDisplaydownloadtimes() + context.getResources().getString(R.string.label_download_times_more_rd_games));
			} else if ("2".equals(more_type)) {
				holder.downloaded_times.setText(gameInfo.getUpdatetimedate());
			}

			if ("2".equals(gameInfo.getComingsoon())) {
				holder.ll_bt_download.setVisibility(View.INVISIBLE);
				holder.comingsoon.setVisibility(View.VISIBLE);
			} else {
				holder.ll_bt_download.setVisibility(View.VISIBLE);
				holder.comingsoon.setVisibility(View.INVISIBLE);
			}

			if (gameInfo.getLabelName() != null && !gameInfo.getLabelName().equals("")) {
				holder.card_label.setText(gameInfo.getLabelName());
				holder.card_label.setLabelColor(gameInfo.getLabelColor());
				holder.card_label.setVisibility(View.VISIBLE);
			} else {
				holder.card_label.setVisibility(View.GONE);
			}

			if (gameInfo.download_status != null) {
                try{
                    holder.refresh(gameInfo);
                }catch (OutOfMemoryError e){

                }

			} else
				CustomToast.showToast(mContext, gameInfo.getGameName() + " no status");

			return contentView;
		}
	}// end adapter class

	class MyHolder {
		GameLabelView card_label;

		RoundCornerImageView game_icon;

		// CircleTextview ranking;
		TextView ranking;
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

		LinearLayout ll_middle;

		LinearLayout ll_middle_diff;

		LinearLayout ll_bottom;

		LinearLayout ll_diff_msg;

		ImageView under_shade;

		ImageView front_shade;

		public void refresh(GameInfo info) {
			if (Constants.DEBUG) {
				Log.d("MoreGame_" + more_type, "View holder starts refreshing... ");
				Log.d("MoreGame_" + more_type, "STATUS: " + info.download_status.status);
			}

			switch (info.download_status.status) {
			case PackageMode.UNDOWNLOAD:

				ll_middle.setVisibility(View.GONE);
				ll_middle_diff.setVisibility(View.GONE);

				rb_star.setVisibility(View.VISIBLE);
				ll_rb_star.setVisibility(View.VISIBLE);
				ll_bottom.setVisibility(View.VISIBLE);

				ll_diff_msg.setVisibility(View.GONE);

                ImageLoaderHelper.displayImage("drawable://" + R.drawable.btn_download_selector,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.button_bg_download_normal));
//				iv_download_bt.setImageResource(R.drawable.btn_download_selector);
				download_percent2.setText(R.string.label_download);

				break;
			case PackageMode.UPDATABLE:
				ll_middle.setVisibility(View.GONE);
				ll_middle_diff.setVisibility(View.GONE);

				rb_star.setVisibility(View.GONE);
				ll_rb_star.setVisibility(View.GONE);
				ll_bottom.setVisibility(View.GONE);

				ll_diff_msg.setVisibility(View.VISIBLE);
				iv_line_update_diff.setVisibility(View.INVISIBLE);
				tv_label_patch_size.setVisibility(View.INVISIBLE);
				tv_patch_size.setVisibility(View.INVISIBLE);

//				iv_download_bt.setImageResource(R.drawable.icon_update_list);
                ImageLoaderHelper.displayImage("drawable://" + R.drawable.icon_update_list,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_update_list));
				download_percent2.setText(R.string.label_update);

				String val = StringUtil.getDisplaySize(Long.valueOf(info.getSize()));

				tv_old_size.setText(val);

				if (info.download_status.localVersion != null && info.download_status.localVersion.equals(info.download_status.version)) {
					tv_lower_version_name.setText(info.download_status.localVersion + "(" + info.download_status.localVersionCode + ")");
					tv_higher_version_name.setText(info.download_status.version + "(" + info.download_status.versionCode + ")");
				} else {
					tv_lower_version_name.setText(info.download_status.localVersion);
					tv_higher_version_name.setText(info.download_status.version);
				}

				break;
			case PackageMode.UPDATABLE_DIFF:
				ll_middle.setVisibility(View.GONE);
				ll_middle_diff.setVisibility(View.GONE);

				rb_star.setVisibility(View.GONE);
				ll_rb_star.setVisibility(View.GONE);
				ll_bottom.setVisibility(View.GONE);

				ll_diff_msg.setVisibility(View.VISIBLE);
				iv_line_update_diff.setVisibility(View.VISIBLE);
				tv_label_patch_size.setVisibility(View.VISIBLE);
				tv_patch_size.setVisibility(View.VISIBLE);
				tv_patch_size.setText(String.format(tv_patch_size.getContext().getString(R.string.update_managment_hint_patchsize),
						Formatter.formatFileSize(tv_patch_size.getContext(), info.download_status.pacthSize)));

//				iv_download_bt.setImageResource(R.drawable.btn_download_diff_update_selector);
                ImageLoaderHelper.displayImage("drawable://" + R.drawable.btn_download_diff_update_selector,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_download_diff_update));
				download_percent2.setText(R.string.label_diff_update);

				tv_old_size.setText(StringUtil.getDisplaySize(info.download_status.totalApkSize));

				if (info.download_status.localVersion != null && info.download_status.localVersion.equals(info.download_status.version)) {
					tv_lower_version_name.setText(info.download_status.localVersion + "(" + info.download_status.localVersionCode + ")");
					tv_higher_version_name.setText(info.download_status.version + "(" + info.download_status.versionCode + ")");
				} else {
					tv_lower_version_name.setText(info.download_status.localVersion);
					tv_higher_version_name.setText(info.download_status.version);
				}

				break;

			case PackageMode.DOWNLOAD_RUNNING:
				if (!info.download_status.isDiffDownload) {
					ll_middle.setVisibility(View.GONE);
					ll_middle_diff.setVisibility(View.GONE);

					rb_star.setVisibility(View.VISIBLE);
					ll_rb_star.setVisibility(View.VISIBLE);
					ll_bottom.setVisibility(View.VISIBLE);

					ll_diff_msg.setVisibility(View.GONE);
					download_percent2.setText((int) ((info.download_status.currentSize * 1.0f / Long.valueOf(info.getSize())) * 100) + "%");
//					iv_download_bt.setImageResource(R.drawable.btn_download_pause_selector);
                    ImageLoaderHelper.displayImage("drawable://" + R.drawable.btn_download_pause_selector,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_download_pause));
				} else {
					ll_middle.setVisibility(View.GONE);
					ll_middle_diff.setVisibility(View.VISIBLE);

					rb_star.setVisibility(View.GONE);
					ll_rb_star.setVisibility(View.GONE);
					ll_bottom.setVisibility(View.GONE);

					ll_diff_msg.setVisibility(View.GONE);

					long save_size = info.download_status.totalApkSize - info.download_status.pacthSize;
					int secondary_progress = (int) ((save_size * 1.0f / info.download_status.totalApkSize) * 100);
					int first_progress = (int) ((info.download_status.currentSize * 1.0f / info.download_status.totalApkSize) * 100) + secondary_progress;
					pb_download_diff.setProgress(first_progress);
					pb_download_diff.setSecondaryProgress(secondary_progress);

					tv_save_size.setText("节省" + StringUtil.getDisplaySize(save_size));

					download_total_diff.setText("/" + StringUtil.getDisplaySize(info.download_status.pacthSize));
					download_current_diff.setText(StringUtil.getDisplaySize(info.download_status.currentSize));

					download_percent2.setText(R.string.label_pause);
//					iv_download_bt.setImageResource(R.drawable.btn_download_pause_selector);
                    ImageLoaderHelper.displayImage("drawable://" + R.drawable.btn_download_pause_selector,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_download_pause));
				}

				break;

			case PackageMode.DOWNLOAD_PAUSED:
				if (!info.download_status.isDiffDownload) {
					ll_middle.setVisibility(View.GONE);
					ll_middle_diff.setVisibility(View.GONE);

					rb_star.setVisibility(View.VISIBLE);
					ll_rb_star.setVisibility(View.VISIBLE);
					ll_bottom.setVisibility(View.VISIBLE);

					ll_diff_msg.setVisibility(View.GONE);

					download_percent2.setText((int) ((info.download_status.currentSize * 1.0f / Long.valueOf(info.getSize())) * 100) + "%");
//					iv_download_bt.setImageResource(R.drawable.icon_resume_list);
                    ImageLoaderHelper.displayImage("drawable://" + R.drawable.icon_resume_list,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_resume_list));
				} else {
					ll_middle.setVisibility(View.GONE);
					ll_middle_diff.setVisibility(View.VISIBLE);

					rb_star.setVisibility(View.GONE);
					ll_rb_star.setVisibility(View.GONE);
					ll_bottom.setVisibility(View.GONE);

					ll_diff_msg.setVisibility(View.GONE);

					long save_size = info.download_status.totalApkSize - info.download_status.pacthSize;
					int first_progress = (int) ((save_size * 1.0f / info.download_status.totalApkSize) * 100);
					int secondary_progress = (int) ((info.download_status.currentSize * 1.0f / info.download_status.totalApkSize) * 100) + first_progress;
					if (secondary_progress >= 100)
						secondary_progress = 99;
					pb_download_diff.setProgress(secondary_progress);
					pb_download_diff.setSecondaryProgress(first_progress);

					tv_save_size.setText("节省" + StringUtil.getDisplaySize(save_size));

					download_total_diff.setText("/" + StringUtil.getDisplaySize(info.download_status.pacthSize));
					download_current_diff.setText(StringUtil.getDisplaySize(info.download_status.currentSize));

					download_percent2.setText(R.string.label_continue);
//					iv_download_bt.setImageResource(R.drawable.icon_resume_list);
                    ImageLoaderHelper.displayImage("drawable://" + R.drawable.icon_resume_list,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_resume_list));
				}

				break;

			case PackageMode.CHECKING_FINISHED:
			case PackageMode.DOWNLOADED:
				ll_middle.setVisibility(View.GONE);
				ll_middle_diff.setVisibility(View.GONE);

				rb_star.setVisibility(View.VISIBLE);
				ll_rb_star.setVisibility(View.VISIBLE);
				ll_bottom.setVisibility(View.VISIBLE);

				ll_diff_msg.setVisibility(View.GONE);

				download_percent2.setText(R.string.label_install);
//				iv_download_bt.setImageResource(R.drawable.icon_install_list);
                ImageLoaderHelper.displayImage("drawable://" + R.drawable.icon_install_list,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_install_list));
				break;

			case PackageMode.DOWNLOAD_FAILED:
				ll_middle.setVisibility(View.GONE);
				ll_middle_diff.setVisibility(View.GONE);

				rb_star.setVisibility(View.VISIBLE);
				ll_rb_star.setVisibility(View.VISIBLE);
				ll_bottom.setVisibility(View.VISIBLE);

				ll_diff_msg.setVisibility(View.GONE);
				download_percent2.setText(R.string.label_retry);
//				iv_download_bt.setImageResource(R.drawable.btn_download_retry_selector);
                ImageLoaderHelper.displayImage("drawable://" + R.drawable.btn_download_retry_selector,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_download_retry));
				break;

			case PackageMode.INSTALLED:
				ll_middle.setVisibility(View.GONE);
				ll_middle_diff.setVisibility(View.GONE);

				rb_star.setVisibility(View.VISIBLE);
				ll_rb_star.setVisibility(View.VISIBLE);
				ll_bottom.setVisibility(View.VISIBLE);

				ll_diff_msg.setVisibility(View.GONE);

				download_percent2.setText(R.string.label_start);
//				iv_download_bt.setImageResource(R.drawable.icon_start_list);
                ImageLoaderHelper.displayImage("drawable://" + R.drawable.icon_start_list,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_start_list));
				break;
			case PackageMode.CHECKING:
			case PackageMode.MERGING:
				ll_middle.setVisibility(View.GONE);
				ll_middle_diff.setVisibility(View.GONE);

				rb_star.setVisibility(View.VISIBLE);
				ll_rb_star.setVisibility(View.VISIBLE);
				ll_bottom.setVisibility(View.VISIBLE);

				ll_diff_msg.setVisibility(View.GONE);

				download_percent2.setText(R.string.label_checking_diff_update);
//				iv_download_bt.setImageResource(R.drawable.icon_checking_list);
                ImageLoaderHelper.displayImage("drawable://" + R.drawable.icon_checking_list,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_checking_list));
				break;
			case PackageMode.MERGE_FAILED:
				ll_middle.setVisibility(View.GONE);
				ll_middle_diff.setVisibility(View.VISIBLE);

				rb_star.setVisibility(View.GONE);
				ll_rb_star.setVisibility(View.GONE);
				ll_bottom.setVisibility(View.GONE);

				ll_diff_msg.setVisibility(View.GONE);

				download_percent2.setText(R.string.label_retry);
//				iv_download_bt.setImageResource(R.drawable.btn_download_retry_selector);
                ImageLoaderHelper.displayImage("drawable://" + R.drawable.btn_download_retry_selector,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_download_retry));
				break;
			case PackageMode.MERGED:
				ll_middle.setVisibility(View.GONE);
				ll_middle_diff.setVisibility(View.GONE);

				rb_star.setVisibility(View.VISIBLE);
				ll_rb_star.setVisibility(View.VISIBLE);
				ll_bottom.setVisibility(View.VISIBLE);

				ll_diff_msg.setVisibility(View.GONE);

				download_percent2.setText(R.string.label_install);
//				iv_download_bt.setImageResource(R.drawable.icon_install_list);
                ImageLoaderHelper.displayImage("drawable://" + R.drawable.icon_install_list,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_install_list));
				break;
			case PackageMode.INSTALLING:
				ll_middle.setVisibility(View.GONE);
				ll_middle_diff.setVisibility(View.GONE);

				rb_star.setVisibility(View.VISIBLE);
				ll_rb_star.setVisibility(View.VISIBLE);
				ll_bottom.setVisibility(View.VISIBLE);

				ll_diff_msg.setVisibility(View.GONE);

				download_percent2.setText(R.string.label_installing);
				iv_download_bt.setImageResource(R.drawable.a_installing);
				AnimationDrawable animationDrawable = (AnimationDrawable) iv_download_bt.getDrawable();
				animationDrawable.start();
				break;

			case PackageMode.DOWNLOAD_PENDING:
				if (!info.download_status.isDiffDownload) {
					ll_middle.setVisibility(View.GONE);
					ll_middle_diff.setVisibility(View.GONE);

					rb_star.setVisibility(View.VISIBLE);
					ll_rb_star.setVisibility(View.VISIBLE);
					ll_bottom.setVisibility(View.VISIBLE);

					ll_diff_msg.setVisibility(View.GONE);

					download_percent2.setText(R.string.label_waiting);
//					iv_download_bt.setImageResource(R.drawable.icon_waiting_list);
                    ImageLoaderHelper.displayImage("drawable://" + R.drawable.icon_waiting_list,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_waiting_list));
                } else {
					ll_middle.setVisibility(View.GONE);
					ll_middle_diff.setVisibility(View.VISIBLE);

					rb_star.setVisibility(View.GONE);
					ll_rb_star.setVisibility(View.GONE);
					ll_bottom.setVisibility(View.GONE);

					ll_diff_msg.setVisibility(View.GONE);

					long save_size = info.download_status.totalApkSize - info.download_status.pacthSize;
					int first_progress = (int) ((save_size * 1.0f / info.download_status.totalApkSize) * 100);
					int secondary_progress = (int) ((info.download_status.currentSize * 1.0f / info.download_status.totalApkSize) * 100) + first_progress;
					if (secondary_progress >= 100)
						secondary_progress = 99;
					pb_download_diff.setProgress(secondary_progress);
					pb_download_diff.setSecondaryProgress(first_progress);

					tv_save_size.setText("节省" + StringUtil.getDisplaySize(save_size));

					download_total_diff.setText("/" + StringUtil.getDisplaySize(info.download_status.pacthSize));
					download_current_diff.setText(StringUtil.getDisplaySize(info.download_status.currentSize));

					download_percent2.setText(R.string.label_pause);
//					iv_download_bt.setImageResource(R.drawable.icon_waiting_list);
                    ImageLoaderHelper.displayImage("drawable://" + R.drawable.icon_waiting_list,iv_download_bt,ImageLoaderHelper.getCustomOption(true,R.drawable.icon_waiting_list));
				}
				break;
			}
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

					Message m = mHandler.obtainMessage();

					m.what = ADAPTER_ITEM_NOTIFY;
					m.obj = gameInfo;

					mHandler.sendMessage(m);
				}
			}
		}
	}

	MyDownloadReceiver mMyDownloadReceiver;

	class MyDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcaseSender.ACTION_DOWNLOAD_START)) {

				if (mGameMoreDataResult != null) {
					String packageName = intent.getStringExtra("pkgname");
					GameInfo gameInfo = map_games.get(packageName);
					if (gameInfo != null) {
						gameInfo.isInit = false;

						Message m = mHandler.obtainMessage();

						m.what = ADAPTER_ITEM_NOTIFY;
						m.obj = gameInfo;

						mHandler.sendMessage(m);
					}
				}
			}

		}

	}

	HashMap<String, GameInfo> wait2download_map = new HashMap<String, GameInfo>();

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

					DownloadStatistics.addDownloadGameStatistics(mContext.getApplicationContext(), gameInfo.getGameName(), false);
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

					Log.i("whb", "download game name:" + gameInfo.getGameName());
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

					DownloadStatistics.addDownloadGameStatistics(mContext.getApplicationContext(), gameInfo.getGameName(), false);

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
					DownloadStatistics.addResumeDownloadGameStatistics(mContext.getApplicationContext(), gameInfo.getGameName());
					break;

				case PackageMode.DOWNLOAD_FAILED:

					PackageHelper.restartDownload(gameInfo.download_status.downloadId, new DownloadCallback() {

						@Override
						public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
							// TODO Auto-generated method stub

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

					DownloadStatistics.addDownloadGameStatistics(mContext.getApplicationContext(), gameInfo.getGameName(), false);
					break;

				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// Bitmap front_shade;
	//
	// Bitmap under_shade;
	//
	// public Bitmap getFrontSade()
	// {
	// if (front_shade == null)
	// {
	// front_shade = BitmapFactory.decodeResource(getResources(),
	// R.drawable.icon_front_shade_game_list);
	// }
	//
	// return front_shade;
	// }
	//
	// public Bitmap getUnderSade()
	// {
	// if (under_shade == null)
	// {
	// under_shade = BitmapFactory.decodeResource(getResources(),
	// R.drawable.icon_under_shade_game_list);
	// }
	//
	// return under_shade;
	// }

	// @author liushuohui / @date 2013-09-23 / START
	private void refreshLoadingStatus(StatusLoading status) {
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

			mViewLoading.setClickable(false);
		}
			break;

		case SUCCEED: {
			mViewLoading.setVisibility(View.GONE);
			mViewNoGame.setVisibility(View.GONE);

			mViewLoading.setClickable(false);
		}
			break;

		case FAILED: {
			mViewLoading.setVisibility(View.VISIBLE);
			mViewLoadingOngoing.setVisibility(View.GONE);
			mViewLoadingFailed.setVisibility(View.VISIBLE);

			mViewNoGame.setVisibility(View.GONE);

			mViewLoading.setClickable(true);
		}
			break;

		case NONE: {
			mViewLoading.setVisibility(View.GONE);
			mViewNoGame.setVisibility(View.VISIBLE);

			mViewLoading.setClickable(false);
		}
			break;

		default:
			break;
		}
	}

	// @author liushuohui / @date 2013-09-23 / END

	// @Override
	// public int getLayout()
	// {
	// return R.layout.more_recommend_games_activity;
	// }

	// @Override
	// public String getHeaderTitle()
	// {
	// return null;
	// }

	private OnClickListener mDownloadClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final GameInfo gameInfo = (GameInfo) v.getTag();
			int index = game_list.indexOf(gameInfo);
			MyHolder holder = null;

			if (index >= 0 && index < game_list.size()) {
				int first = lv_more_recommend_games_act.getFirstVisiblePosition();

				if (index >= first) {
					View view = lv_more_recommend_games_act.getChildAt(index - first);

					if (null != view) {
						holder = (MyHolder) view.getTag();
					}
				}
			}

			if (null != holder && null != gameInfo && gameInfo.download_status != null) {

				if (!ConnectManager.isNetworkConnected(getActivity().getApplicationContext()) && gameInfo.download_status.status != PackageMode.INSTALLED) {
					CustomToast.showToast(getActivity().getApplicationContext(), getString(R.string.network_error_hint));
					return;
				}
				switch (gameInfo.download_status.status) {
				case PackageMode.UNDOWNLOAD:
				case PackageMode.UPDATABLE:
					if (ApkUtil.shouldCheckRootUserDownload()) {
						rootDialog = DialogFactory.createCheckRootDownDialog(mContext);
						rootDialog.show();
					}
					if (ConnectManager.isNetworkConnected(mContext)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							CustomToast.showToast(mContext, "请检查您的SD卡");
							break;
						}

						if (gameInfo.getDownloadurl() == null || "".equals(gameInfo.getDownloadurl())) {
							CustomToast.showToast(mContext, "无下载地址");
							holder.ll_bt_download.setClickable(false);
							break;
						}

						holder.ll_bt_download.setClickable(true);

						if (ConnectManager.isWifi(mContext) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
							// 直接下载
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

							DownloadStatistics.addDownloadGameStatistics(mContext.getApplicationContext(), gameInfo.getGameName(), false);
						} else {
							// 非wifi下需要用户确认是否下载
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							DuokuDialog.showNetworkAlertDialog(mContext, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}

					} else {
						// CustomToast.showToast(mContext,
						// "请检查您的网络连接");
					}
					break;
				case PackageMode.UPDATABLE_DIFF:
					if (ConnectManager.isNetworkConnected(mContext)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							CustomToast.showToast(mContext, "请检查您的SD卡");
							break;
						}

						if (gameInfo.getDownloadurl() == null || "".equals(gameInfo.getDownloadurl())) {
							CustomToast.showToast(mContext, "无下载地址");
							holder.ll_bt_download.setClickable(false);
							break;
						}

						holder.ll_bt_download.setClickable(true);

						if (ConnectManager.isWifi(mContext) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
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

							DownloadStatistics.addDownloadGameStatistics(mContext.getApplicationContext(), gameInfo.getGameName(), false);
						} else {
							// 非wifi下需要用户确认是否下载
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							DuokuDialog.showNetworkAlertDialog(mContext, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}

					} else {
						// CustomToast.showToast(mContext,
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

					DownloadStatistics.addUserPauseDownloadGameStatistics(mContext.getApplicationContext(), gameInfo.getGameName());

					break;

				case PackageMode.DOWNLOAD_PAUSED:

					if (ConnectManager.isNetworkConnected(mContext)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							// Toast.makeText(context,
							// "请检查您的SD卡", 1).show();
							CustomToast.showToast(mContext, "请检查您的SD卡");
							break;
						}
						if (ConnectManager.isWifi(mContext) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {

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
							DownloadStatistics.addResumeDownloadGameStatistics(mContext.getApplicationContext(), gameInfo.getGameName());

						} else {
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							DuokuDialog.showNetworkAlertDialog(mContext, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}

					} else {
						// CustomToast.showToast(mContext,
						// "请检查您的网络连接");
					}

					break;
				case PackageMode.CHECKING_FINISHED:
				case PackageMode.DOWNLOADED:
					if (!gameInfo.download_status.isDiffDownload) {
						PackageHelper.installApp(mContext, gameInfo.getGameId(), gameInfo.getPkgname(), gameInfo.download_status.downloadDest);
					} else {
						PackageHelper.sendMergeRequestFromUI(gameInfo.download_status.downloadId);
					}
					break;
				case PackageMode.MERGED:
					PackageHelper.installApp(mContext, gameInfo.getGameId(), gameInfo.getPkgname(), gameInfo.download_status.downloadDest);
					break;
				case PackageMode.MERGE_FAILED:
					if (gameInfo.download_status.mergeFailedCount >= 2) {
						if (ConnectManager.isNetworkConnected(mContext)) {
							if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
								CustomToast.showToast(mContext, "请检查您的SD卡");
								break;
							}
							if (ConnectManager.isWifi(mContext) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
								PackageHelper.restartDownload(gameInfo.download_status.downloadId, new DownloadCallback() {

									@Override
									public void onResumeDownloadResult(String downloadUrl, boolean successful, Integer reason) {
										// TODO Auto-generated
										// method stub

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

								DownloadStatistics.addDownloadGameStatistics(mContext.getApplicationContext(), gameInfo.getGameName(), false);

							} else {
								wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
								DuokuDialog.showNetworkAlertDialog(mContext, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
							}

						} else {
							// CustomToast.showToast(mContext,
							// "请检查您的网络连接");
						}
					} else {
						PackageHelper.sendMergeRequestFromUI(gameInfo.download_status.downloadId);
					}
					break;
				case PackageMode.DOWNLOAD_FAILED:
					if (ConnectManager.isNetworkConnected(mContext)) {
						if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
							CustomToast.showToast(mContext, "请检查您的SD卡");
							break;
						}
						if (ConnectManager.isWifi(mContext) || !MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
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

							DownloadStatistics.addDownloadGameStatistics(mContext.getApplicationContext(), gameInfo.getGameName(), false);
						} else {
							wait2download_map.put(gameInfo.getDownloadurl(), gameInfo);
							DuokuDialog.showNetworkAlertDialog(mContext, REQUEST_DOWNLOAD_IN_WAP_NETWORK, "", gameInfo.getDownloadurl(), "");
						}

					} else {
						// CustomToast.showToast(mContext,
						// "请检查您的网络连接");
					}
					break;

				case PackageMode.INSTALLED:
					new StartGame(mContext, gameInfo.getPkgname(), gameInfo.getStartaction(), gameInfo.getGameId(), gameInfo.isNeedlogin()).startGame();
					break;

				default:
					break;
				}
			} else
				CustomToast.showToast(mContext, gameInfo.getGameName() + " no status");
		}
	};

	private void loadMore() {
		if (mRequestId > 0) {
			return;
		}

		if (!ConnectManager.isNetworkConnected(mContext)) {
			CustomToast.showToast(mContext, getString(R.string.alert_network_inavailble));
			return;
		}

		setFooterStatus(mFooterView, STATUS_LOADING);

		mRequestId = NetUtil.getInstance().requestGameMoreData(more_type, request_page, page_count, mReqListenerMore);
	}

	private void setFooterStatus(View view, byte status) {
		TextView tv = null;
		View progress = null;

		if (null != view) {
			tv = (TextView) view.findViewById(R.id.loading_text);
			progress = view.findViewById(R.id.loading_progress);
		}

		if (STATUS_NO_MORE == status) {
			if (null != tv) {
				// tv.setText(R.string.pull_to_refresh_no_more_data);
				tv.setText(getString(R.string.pull_to_refresh_no_more_data) + "," + getString(R.string.footer_no_more_see_more));
				tv.setTextColor(getResources().getColor(R.color.no_more_data_text));
			}

			if (null != progress) {
				progress.setVisibility(View.GONE);
			}
		} else if (STATUS_LOADING == status) {
			if (null != tv) {
				tv.setText(R.string.pull_to_refresh_refreshing_label);
			}

			if (null != progress) {
				progress.setVisibility(View.VISIBLE);
			}
		} else if (STATUS_NO_DATA == status) {
			if (null != tv) {
				tv.setText(R.string.pull_to_refresh_from_bottom_pull_label);
			}

			if (null != progress) {
				progress.setVisibility(View.GONE);
			}
		} else {
			// RESERVED
		}
	}

	OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (null != mFooterView && mFooterView.isShown() && (null == mGameMoreDataResult || mGameMoreDataResult.getTotalCount() > mCntFiltered + game_list.size())) {
				loadMore();
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		}
	};

	PackageCallback download_listener = new PackageCallback() {
		@Override
		public void onPackageStatusChanged(PackageMode mode) {
			if (Constants.DEBUG) {
				Log.d("MoreGame_" + more_type, "On package status changed >> status: " + mode.status + " | game id: " + mode.gameId);
			}

			Message msg = mHandler.obtainMessage();

			msg.what = ADAPTER_ITEM_NOTIFY;
			msg.obj = mode;

			mHandler.sendMessage(msg);
		}
	};

}
