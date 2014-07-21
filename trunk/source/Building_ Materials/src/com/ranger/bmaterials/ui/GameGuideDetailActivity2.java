package com.ranger.bmaterials.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.PackageHelper.PackageCallback;
import com.ranger.bmaterials.mode.GameGuideDetailInfo;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.GameGuideDetailResult;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.AnimationDrawableView;

public class GameGuideDetailActivity2 extends GameDetailsBaseActivity {
	GameGuideDetailResult mGameGuideDetailResult;

	RelativeLayout rl_loading_game_guide_detail;
	LinearLayout iv_load_failed_game_guide_detail;
	// ProgressBar pb_loading_game_guide_detail;
	// TextView tv_label_loading_game_guide_detail;
	AnimationDrawableView network_loading_pb_game_guide_detail_act;
	TextView iv_no_guide_game_guide_detail;

	// 安装loading
	// ProgressBar pb_loading_install_bottom_game_guide_detail;

	LinearLayout ll_bar_bottom_download_game_guide_detail;


	// List<String> installed_apps;
	HashMap<String, PackageCallback> listeners = new HashMap<String, PackageCallback>();
	// ggdInfo ggdInfo;

	private final int DOWNLOAD_NOTIFY = 1000;

	private WebChromeClient mClient = new WebChromeClient() {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				rl_loading_game_guide_detail.setVisibility(View.GONE);
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		guideid = getIntent().getStringExtra("guideid");

		rl_loading_game_guide_detail = (RelativeLayout) findViewById(R.id.rl_loading_game_guide_detail);
		iv_load_failed_game_guide_detail = (LinearLayout) findViewById(R.id.loading_error_layout);
		network_loading_pb_game_guide_detail_act = (AnimationDrawableView) findViewById(R.id.network_loading_pb_game_guide_detail_act);
		iv_no_guide_game_guide_detail = (TextView) findViewById(R.id.iv_no_guide_game_guide_detail);

		ll_bar_bottom_download_game_guide_detail = (LinearLayout) findViewById(R.id.ll_bar_download_bottom_game_detail);


		iv_load_failed_game_guide_detail.setOnClickListener(this);
		loadContentData();
	}

	@Override
	protected void onDestroy() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Collection<PackageCallback> callbacks = listeners.values();
				for (PackageCallback callback : callbacks) {
					PackageHelper.unregisterPackageStatusChangeObserver(callback);
				}

			}
		}) {
		}.start();
		super.onDestroy();
	}



	TextView tv_guide_title_game_guide_detail;
	TextView tv_guide_time_game_guide_detail;
	LinearLayout ll_sc_game_guide_detail;




	private void initView() {
		GameGuideDetailInfo ggdInfo = mGameGuideDetailResult.getmGameGuideDetailInfo();
		gameInfo=ggdInfo.getInfo();
		if (gameInfo.getGameId() == null || "".equals(gameInfo.getGameId())) {
		} else {
			ll_bar_bottom_download_game_guide_detail.setVisibility(View.VISIBLE);
			if ("2".equals(gameInfo.getComingsoon())) {
				tv_download_status_bottom_game_detail.setText("即将上线");
				tv_download_status_bottom_game_detail.setTextColor(StringUtil.getColor("068BC2"));
				ll_bt_download_game_detail.setBackgroundResource(R.drawable.bg_prg_img_game_detail4);
				iv_icon_download_bottom_game_detail.setImageResource(R.drawable.icon_comingsoon_game_detail);

				tv_download_status_bottom_game_detail.setVisibility(View.VISIBLE);
				iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);

			} else
				checkDownloadBtnState();
		}

		// tv_guide_title_game_guide_detail = (TextView)
		// findViewById(R.id.tv_guide_title_game_guide_detail);

		if (null != tv_guide_title_game_guide_detail) {
			tv_guide_title_game_guide_detail.setText(ggdInfo.getGuidetitle());
		}

		// tv_guide_time_game_guide_detail = (TextView)
		// findViewById(R.id.tv_guide_time_game_guide_detail);

		if (null != tv_guide_time_game_guide_detail) {
			tv_guide_time_game_guide_detail.setText(ggdInfo.getGuidetime());
		}

		ll_sc_game_guide_detail = (LinearLayout) findViewById(R.id.ll_sc_game_guide_detail);

		updateHeaderTitle(gameInfo.getGameName());

		if (mGameGuideDetailResult.getIsUseHTML()) {
			View v = GameGuideDetailActivity2.this.findViewById(R.id.guide_content);
			WebView web = (WebView) v.findViewById(R.id.web_detail);

			if (Constants.DEBUG) {
				Log.d("HTML", mGameGuideDetailResult.getHTMLContent());
			}

			web.getSettings().setJavaScriptEnabled(true);
			web.loadDataWithBaseURL(null, mGameGuideDetailResult.getHTMLContent(), "text/html", "utf-8", null);
			web.setWebChromeClient(mClient);

			v.setVisibility(View.VISIBLE);
		} else {
			/**
			 * Use webview instead of old textview
			 * 
			 * @author liushuohui
			 * 
			 *         ArrayList<GameGuideDetailInfo.GameGuideContent> list_ggc
			 *         = ggdInfo.getList_guide_content(); if (list_ggc.size() >
			 *         0) { for (int i = 0; i < list_ggc.size(); i++) {
			 *         GameGuideDetailInfo.GameGuideContent ggc =
			 *         list_ggc.get(i); View v =
			 *         GameGuideDetailActivity2.this.findViewById
			 *         (R.id.guide_content
			 *         );//View.inflate(GameGuideDetailActivity2.this,
			 *         R.layout.item_game_guide_detail2, null); //
			 * 
			 *         TextView tv = (TextView) v.findViewById(R.id.
			 *         tv_content_item_game_guide_detail_new); ImageView iv =
			 *         (ImageView)
			 *         v.findViewById(R.id.iv_item_game_guide_detail); if
			 *         (ggc.content != null) { tv.setText("\u3000\u3000" +
			 *         checkspace(ggc.content.trim().replaceAll("\\[BR\\]",
			 *         "\n"))); tv.setVisibility(View.VISIBLE); //
			 *         LinearLayout.LayoutParams lp = new
			 *         LinearLayout.LayoutParams
			 *         (LinearLayout.LayoutParams.MATCH_PARENT,
			 *         tv.mTextViewHeight); // tv.setLayoutParams(lp); } if
			 *         (ggc.picurl != null) {
			 *         ImageLoaderHelper.displayImage(ggc.picurl, iv);
			 *         iv.setVisibility(View.VISIBLE); }
			 * 
			 *         // if (ggc.content != null || ggc.picurl != null) // { //
			 *         ll_sc_game_guide_detail.removeAllViews(); //
			 *         ll_sc_game_guide_detail.addView(v); // }
			 * 
			 *         v.setVisibility(View.VISIBLE); }
			 */
		}
		if (gameInfo.isIscollected())
			iv_collect_game_detail.setImageResource(R.drawable.bt_collected_game_detail_selector);
		else
			iv_collect_game_detail.setImageResource(R.drawable.bt_collect_game_detail_selector);
	}

	private String checkspace(String content) {
		Pattern p = Pattern.compile("　{1,10}");
		Matcher m = p.matcher(content);
		StringBuffer sb = new StringBuffer();
		boolean result = m.find();
		while (result) {
			m.appendReplacement(sb, "");

			result = m.find();
		}
		m.appendTail(sb);

		Pattern p2 = Pattern.compile("[ | |　]{0,140}\n{1,140}[ | |　]{0,140}");
		Matcher m2 = p2.matcher(sb.toString());
		StringBuffer sb2 = new StringBuffer();
		boolean result2 = m2.find();
		while (result2) {
			m2.appendReplacement(sb2, "\n\u3000\u3000");

			result2 = m2.find();
		}
		m2.appendTail(sb2);
		return sb2.toString();
	}


	private IRequestListener mRequestGuideListener = new IRequestListener() {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			mGameGuideDetailResult = (GameGuideDetailResult) responseData;
			new Thread(new Runnable() {

				@Override
				public void run() {
					GameInfo gIn = mGameGuideDetailResult.getmGameGuideDetailInfo().getInfo();
					QueryInput qin = new QueryInput();
					qin.gameId = gIn.getGameId();
					qin.packageName = gIn.getPkgname();
					qin.version = gIn.getGameversion();
					qin.versionCode = gIn.getGameversioncode();
					qin.downloadUrl = gIn.getDownloadurl();
					gIn.qin = qin;

					gIn.download_status = PackageHelper.queryPackageStatus(gIn.qin).get(gIn.qin);

					mHandler.post(new Runnable() {

						@Override
						public void run() {
							initView();

						}
					});

				}
			}) {
			}.start();

		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			if (DcError.DC_GAME_OUTOFSTOCK == errorCode) {
				iv_no_guide_game_guide_detail.setVisibility(View.VISIBLE);
				network_loading_pb_game_guide_detail_act.setVisibility(View.INVISIBLE);
			} else {
				network_loading_pb_game_guide_detail_act.setVisibility(View.INVISIBLE);
				iv_load_failed_game_guide_detail.setVisibility(View.VISIBLE);

			}
		}
	};

	private String guideid;

	@Override
	public int getLayout() {
		return R.layout.game_guide_detail_activity2;
	}
	@Override
	protected void checkDownloadBtnState() {
		if (iv_icon_download_bottom_game_detail != null)
			iv_icon_download_bottom_game_detail.setVisibility(View.VISIBLE);
		if (tv_download_status_bottom_game_detail != null)
			tv_download_status_bottom_game_detail.setVisibility(View.VISIBLE);

		if (!listeners.keySet().contains(gameInfo.getGameId())) {
			PackageCallback download_listener = new PackageCallback() {

				@Override
				public void onPackageStatusChanged(PackageMode mode) {
					if (mode.gameId != null && mode.gameId.equals(gameInfo.getGameId())) {
						gameInfo.download_status = mode;

						Message msg = new Message();
						msg.what = DOWNLOAD_NOTIFY;
						mHandler.sendMessage(msg);
					}
				}
			};

			listeners.put(gameInfo.getGameId(), download_listener);
			PackageHelper.registerPackageStatusChangeObserver(download_listener);
		}
		super.checkDownloadBtnState();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loading_error_layout:
			if (DeviceUtil.isNetworkAvailable(getApplicationContext())) {
				loadContentData();
				iv_load_failed_game_guide_detail.setVisibility(View.INVISIBLE);
				network_loading_pb_game_guide_detail_act.setVisibility(View.VISIBLE);
			} else {
				CustomToast.showToast(getApplicationContext(), getString(R.string.alert_network_inavailble));
			}
			
			break;

		default:
			break;
		}
	}
	private void loadContentData() {
		NetUtil.getInstance().requestGameGuideDetail(MineProfile.getInstance().getUserID(), MineProfile.getInstance().getSessionID(), guideid, mRequestGuideListener);
	}
}
