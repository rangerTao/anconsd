package com.ranger.bmaterials.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.format.Formatter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.app.GameDetailConstants;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.listener.HomeAppListDownloadListener;
import com.ranger.bmaterials.mode.HomeAppListInfoArray;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.HomeAppListInfoArray.HomeAppListBannerInfo;
import com.ranger.bmaterials.mode.HomeAppListInfoArray.HomeAppListBaseInfo;
import com.ranger.bmaterials.mode.HomeAppListInfoArray.HomeAppListItemInfo;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.MyTouchDelegate;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.ui.GameDetailsActivity;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.view.CircleProgressBar;
import com.ranger.bmaterials.view.ExpandablePullUpListView;
import com.ranger.bmaterials.view.GameLabelView;

public class HomeAppListAdapter extends BaseAdapter {
	private Activity context;
	private ArrayList<HomeAppListInfoArray> arraylist = new ArrayList<HomeAppListInfoArray>();
	private ArrayList<HomeAppListBaseInfo> infoList = new ArrayList<HomeAppListBaseInfo>();
	private final int VIEWTYP = 2;

	public final ArrayList<HomeAppListInfoArray> getList() {
		return arraylist;
	}

	public final void setList(ArrayList<HomeAppListInfoArray> list) {
		this.arraylist.clear();
		infoList.clear();

		addList(list);
	}

	public final void addList(ArrayList<HomeAppListInfoArray> list) {
		this.arraylist.addAll(list);

		for (HomeAppListInfoArray homeAppListInfoArray : list) {
			infoList.add(homeAppListInfoArray.bannerInfo);
			for (HomeAppListItemInfo cardInfo : homeAppListInfoArray.homeAppListInfos) {
				infoList.add(cardInfo);
			}
		}
	}

	private DisplayImageOptions bannerOpt, listOpt;
	private ExpandablePullUpListView pullUpLv;

	private int displayWidth;

	public interface CheckPackagesCallBack {
		void onFinish();
	}

	private void changeDownloadViewStatus(HolderView holder, HomeAppListBaseInfo homeInfo) {
		if (homeInfo == null)
			return;
		PackageMode packageMode = homeInfo.packageMode;
		if (packageMode == null)
			return;

		switch (packageMode.status) {

		case PackageMode.CHECKING_FINISHED:
		case PackageMode.DOWNLOADED:
			showInstall(holder, homeInfo);
			break;
		case PackageMode.UPDATABLE:
		case PackageMode.UPDATABLE_DIFF:
			showUpdate(holder, homeInfo);
			break;
		case PackageMode.UNDOWNLOAD:
			showDown(holder, homeInfo);
			break;

		case PackageMode.DOWNLOAD_PENDING:
		case PackageMode.DOWNLOAD_RUNNING:
			showDownloading(holder, homeInfo);
			break;

		case PackageMode.DOWNLOAD_PAUSED:
		case PackageMode.DOWNLOAD_FAILED:
			showUnCompeleted(holder, homeInfo);
			break;

		case PackageMode.INSTALLED:
			showStart(holder, homeInfo);
			break;

		case PackageMode.CHECKING:
		case PackageMode.MERGING:
		case PackageMode.INSTALLING:
			showInstalling(holder, homeInfo);
			break;

		case PackageMode.MERGE_FAILED:
			showUnCompeleted(holder, homeInfo);
			break;
		}
	}

	private void showDownloading(HolderView holder, HomeAppListBaseInfo homeInfo) {
		if(homeInfo.packageMode==null){
			return;
		}
		int percent = calPercent(homeInfo.packageMode.currentSize, homeInfo.packageMode.totalSize);
		if (homeInfo instanceof HomeAppListItemInfo) {
			holder.card_pb_tv.setVisibility(View.VISIBLE);
			holder.card_pb_tv.setText(percent + "%");
			holder.card_download_iv.setCurrentPercent(percent);
			holder.card_download_iv.setCustomMode(true);
			holder.card_download_iv.setEnabled(false);
			holder.card_download_iv.invalidate();
			holder.card_download_tv.setText(R.string.downloading);
			holder.card_download_tv.setVisibility(View.VISIBLE);

		} else {
			holder.banner_pb_tv.setVisibility(View.VISIBLE);
			holder.banner_pb_tv.setText(percent + "%");
			holder.banner_btn.setImageResource(R.drawable.icon_downloading_white_bg);
			holder.banner_btn.setEnabled(false);
		}
	}

	private void showInstalling(HolderView holder, HomeAppListBaseInfo homeInfo) {
		if (homeInfo instanceof HomeAppListItemInfo) {
			holder.card_pb_tv.setVisibility(View.GONE);
			holder.card_download_iv.setCustomMode(false);
			holder.card_download_iv.setImageResource(R.drawable.icon_install_list);
			holder.card_download_iv.setEnabled(false);
			holder.card_download_iv.invalidate();
			holder.card_download_tv.setText(R.string.installing);
			holder.card_download_tv.setVisibility(View.VISIBLE);

		} else {
			holder.banner_pb_tv.setVisibility(View.GONE);
			holder.banner_btn.setEnabled(false);
			holder.banner_btn.setImageResource(R.drawable.btn_download_install_selector);

		}
	}

	private void showDown(HolderView holder, HomeAppListBaseInfo homeInfo) {

		if (homeInfo instanceof HomeAppListItemInfo) {

			holder.card_pb_tv.setVisibility(View.GONE);
			holder.card_download_iv.setCustomMode(false);
			holder.card_download_iv.setImageResource(R.drawable.btn_download_selector);
			holder.card_download_iv.setEnabled(true);
			holder.card_download_iv.invalidate();
			holder.card_download_tv.setText(R.string.download);
			holder.card_download_tv.setVisibility(View.VISIBLE);
			// holder.card_download_tv.setVisibility(View.GONE);

		} else {
			holder.banner_pb_tv.setVisibility(View.GONE);
			holder.banner_btn.setImageResource(R.drawable.btn_download_selector);
			holder.banner_btn.setEnabled(true);

		}
	}

	private void showUpdate(HolderView holder, HomeAppListBaseInfo homeInfo) {

		if (homeInfo instanceof HomeAppListItemInfo) {
			holder.card_pb_tv.setVisibility(View.GONE);
			holder.card_download_iv.setCustomMode(false);
			holder.card_download_iv.setImageResource(R.drawable.btn_download_update_selector);
			holder.card_download_iv.setEnabled(true);
			holder.card_download_iv.invalidate();
			holder.card_download_tv.setText(R.string.update);
			holder.card_download_tv.setVisibility(View.VISIBLE);

		} else {
			holder.banner_pb_tv.setVisibility(View.GONE);
			holder.banner_btn.setImageResource(R.drawable.btn_download_selector);
			holder.banner_btn.setEnabled(true);

		}
	}

	private void showInstall(HolderView holder, HomeAppListBaseInfo homeInfo) {
		if (homeInfo instanceof HomeAppListItemInfo) {
			holder.card_pb_tv.setVisibility(View.GONE);
			holder.card_download_iv.setCustomMode(false);
			holder.card_download_iv.setImageResource(R.drawable.btn_download_install_selector);
			holder.card_download_iv.setEnabled(true);
			holder.card_download_iv.invalidate();
			holder.card_download_tv.setText(R.string.install);
			holder.card_download_tv.setVisibility(View.VISIBLE);

		} else {

			holder.banner_pb_tv.setVisibility(View.GONE);
			holder.banner_btn.setImageResource(R.drawable.btn_download_install_selector);
			holder.banner_btn.setEnabled(true);
		}
	}

	private void showUnCompeleted(HolderView holder, HomeAppListBaseInfo homeInfo) {
		int percent = calPercent(homeInfo.packageMode.currentSize, homeInfo.packageMode.totalSize);
		if (homeInfo instanceof HomeAppListItemInfo) {
			holder.card_pb_tv.setVisibility(View.VISIBLE);
			holder.card_pb_tv.setText(percent + "%");
			holder.card_download_iv.setCustomMode(true);
			holder.card_download_iv.setEnabled(false);
			holder.card_download_iv.invalidate();
			holder.card_download_tv.setText(R.string.uncompeleted);
			holder.card_download_tv.setVisibility(View.VISIBLE);
		} else {

			holder.banner_pb_tv.setVisibility(View.VISIBLE);
			holder.banner_pb_tv.setText(percent + "%");
			holder.banner_btn.setEnabled(false);
			holder.banner_btn.setImageResource(R.drawable.icon_downloading_white_bg);
		}
	}

	private void showStart(HolderView holder, HomeAppListBaseInfo homeInfo) {
		if (homeInfo instanceof HomeAppListItemInfo) {
			holder.card_pb_tv.setVisibility(View.GONE);
			holder.card_download_iv.setCustomMode(false);
			holder.card_download_iv.setImageResource(R.drawable.btn_download_launch_selector);
			holder.card_download_iv.setEnabled(true);
			holder.card_download_iv.invalidate();
			holder.card_download_tv.setText(R.string.open);
			holder.card_download_tv.setVisibility(View.VISIBLE);

		} else {

			holder.banner_pb_tv.setVisibility(View.GONE);
			holder.banner_btn.setImageResource(R.drawable.btn_download_launch_selector);
			holder.banner_btn.setEnabled(true);
		}
	}

	private int calPercent(long currentBytes, long totalBytes) {
		return (int) ((currentBytes / (1.0f * totalBytes)) * 100);
	}

	public void partRefresh(PackageMode packageMode) {
		int len = infoList.size();
		final int firstVisible = pullUpLv.getFirstVisiblePosition();
		int lastVisible = pullUpLv.getLastVisiblePosition() + 1;

		for (int i = 0; i < len; i++) {
			final HomeAppListBaseInfo info = infoList.get(i);
			if (compareHomeAppInfo(info, packageMode.packageName, packageMode.version, packageMode.versionCode)) {

				if (i >= firstVisible && i <= lastVisible) {
					final int position = i;
					info.packageMode = packageMode;

					context.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							View convertView = pullUpLv.getChildAt(position - firstVisible);
							if (convertView != null) {
								HolderView hv = (HolderView) convertView.getTag();
								if (hv != null) {
									changeDownloadViewStatus(hv, info);
									if (info instanceof HomeAppListBannerInfo)
										setBannerDownloadIvOnClick(hv, info);
									else
										setCardDownloadIvOnClick(hv, info);
								}
							}
						}
					});
				}

				break;
			}
		}

	}

	private boolean compareHomeAppInfo(HomeAppListBaseInfo oInfo, String pkgName, String version, int versionCode) {
		int vc = (oInfo.versioncode == null || oInfo.versioncode.equals("")) ? -1 : Integer.valueOf(oInfo.versioncode);

		if (oInfo == null || oInfo.pkgname == null || oInfo.versionname == null) {
			return false;
		}

		return oInfo.pkgname.equalsIgnoreCase(pkgName) && vc == versionCode && oInfo.versionname.equalsIgnoreCase(version);
	}

	public static boolean checkWifiConfig(Context context) {
		if (MineProfile.getInstance().isDownloadOnlyWithWiFi()) {
			if (!ConnectManager.isWifi(context))
				return true;
		}
		return false;
	}

	public HomeAppListAdapter(Activity context) {
		this.context = context;
		int[] wh = UIUtil.getScreenPx(context);
		displayWidth = wh[0];
		bannerOpt = ImageLoaderHelper.getCustomOption(R.drawable.ad_default);
		listOpt = ImageLoaderHelper.getCustomOption(R.drawable.game_icon_list_default);
	}

	public void setListView(ExpandablePullUpListView listview) {
		this.pullUpLv = listview;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return infoList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HolderView hv = null;

		HomeAppListBaseInfo info = infoList.get(position);

		if (contentView == null) {
			hv = new HolderView();
			if (getItemViewType(position) == 0) {
				contentView = View.inflate(context, R.layout.home_app_list_banner_item_layout, null);
				inflateBannerViewStub(hv, contentView, (HomeAppListBannerInfo) info);
			} else {
				contentView = View.inflate(context, R.layout.home_app_list_card_item_layout, null);
				inflateCardViewStub(hv, contentView, (HomeAppListItemInfo) info);
			}
			contentView.setTag(hv);
		} else {
			hv = (HolderView) contentView.getTag();
		}

		if (getItemViewType(position) == 0) {
			setupBannerView(hv, (HomeAppListBannerInfo) info);
		} else {
			setupCardView(hv, (HomeAppListItemInfo) info);
		}

		return contentView;
	}

	private void inflateCardViewStub(HolderView hv, View contentView, HomeAppListItemInfo info) {
		hv.card_layout = (LinearLayout) contentView.findViewById(R.id.home_app_list_item_card_viewstub);

		hv.card_name = (TextView) contentView.findViewById(R.id.home_app_item_card_name);
		hv.card_download_times = (TextView) contentView.findViewById(R.id.home_app_item_card_download_times);
		hv.card_size = (TextView) contentView.findViewById(R.id.home_app_item_card_size);
		hv.card_download_tv = (TextView) contentView.findViewById(R.id.home_app_item_card_download_tv);
		hv.card_recommend_tv = (TextView) contentView.findViewById(R.id.home_app_item_card_recommend_tv);

		hv.card_icon = (RoundCornerImageView) contentView.findViewById(R.id.home_app_item_card_iv);
		hv.card_download_iv = (CircleProgressBar) contentView.findViewById(R.id.home_app_item_card_download_iv);

		hv.card_rating = (RatingBar) contentView.findViewById(R.id.home_app_item_card_rating);
		hv.card_pb_tv = (TextView) contentView.findViewById(R.id.home_app_item_card_download_pb_tv);
		hv.card_game_label = (GameLabelView) contentView.findViewById(R.id.home_app_item_card_label_name);

		mTouchDelegate.setTouchDelegate((ViewGroup) hv.card_download_iv.getParent());
	}

	private void inflateBannerViewStub(HolderView hv, View contentView, HomeAppListBannerInfo bannerInfo) {
		hv.banner_layout = (RelativeLayout) contentView.findViewById(R.id.home_app_list_item_banner_viewstub);

		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) hv.banner_layout.getLayoutParams();
		lp.height = displayWidth / 3;
		hv.banner_layout.setLayoutParams(lp);

		hv.banner_bg = (RoundCornerImageView) contentView.findViewById(R.id.home_app_list_banner_bg);

		hv.banner_name = (TextView) contentView.findViewById(R.id.home_app_list_banner_name);
		hv.banner_btn = (ImageView) contentView.findViewById(R.id.home_app_list_banner_btn);
		hv.banner_pb_tv = (TextView) contentView.findViewById(R.id.home_app_list_banner_pb_tv);
	}

	private void setupCardView(HolderView hv, HomeAppListItemInfo info) {
		hv.card_name.setText(info.gamename);
		if (info.gamedownloadcount != null && !info.gamedownloadcount.equals(""))
			hv.card_download_times.setText(StringUtil.formatTimes(Long.valueOf(info.gamedownloadcount)));
		if (info.pkgsize != null && !info.pkgsize.equals(""))
			hv.card_size.setText(Formatter.formatFileSize(context, Long.valueOf(info.pkgsize)));
		hv.card_recommend_tv.setText(info.gamerecommenddesc);
		if (info.gamestar != null && !info.gamestar.equals(""))
			hv.card_rating.setRating(Float.valueOf(info.gamestar));
		if (info.labelName != null && !info.labelName.equals("")) {
			hv.card_game_label.setText(info.labelName);
			hv.card_game_label.setLabelColor(info.labelColor);
			hv.card_game_label.setVisibility(View.VISIBLE);
		} else
			hv.card_game_label.setVisibility(View.GONE);
		ImageLoaderHelper.displayImage(info.gameicon, hv.card_icon, listOpt);
		// hv.card_icon.displayImage(info.gameicon, listOpt);

		changeDownloadViewStatus(hv, info);

		hv.card_layout.setOnTouchListener(new CardOnTouchListener(info));

		setCardDownloadIvOnClick(hv, info);
	}

	private void setupBannerView(HolderView hv, HomeAppListBannerInfo bannerInfo) {
		// if (hv.card_layout != null) {
		// hv.card_icon.setImageBitmap(null);
		// hv.card_icon.setTag(null);
		// hv.card_layout.setVisibility(View.GONE);
		// }

		// if (bannerInfo == null || bannerInfo.bannericon == null ||
		// bannerInfo.bannericon.equals("")) {
		// hv.banner_bg.setTag(null);
		// hv.banner_bg.setImageBitmap(null);
		// hv.banner_layout.setVisibility(View.GONE);
		// } else {
		// hv.banner_layout.setVisibility(View.VISIBLE);
		ImageLoaderHelper.displayImage(bannerInfo.bannericon, hv.banner_bg, bannerOpt);
		// hv.banner_bg.displayBannerImage(bannerInfo.bannericon,
		// bannerOpt);
		// hv.banner_bg.setOnClickListener(new BannerOnClick(bannerInfo));
		hv.banner_name.setText(bannerInfo.gamename);

		// setBannerDownloadIvOnClick(hv, bannerInfo);
		// changeDownloadViewStatus(hv, bannerInfo);
		// }
		// hv.banner_bg.displayBannerImage(bannerInfo.bannericon, bannerOpt);
		// hv.banner_name.setText(bannerInfo.gamename);

		hv.banner_bg.setOnClickListener(new BannerOnClick(bannerInfo));

		setBannerDownloadIvOnClick(hv, bannerInfo);

		changeDownloadViewStatus(hv, bannerInfo);
	}

	private void setBannerDownloadIvOnClick(HolderView hv, HomeAppListBaseInfo homeInfo) {
		hv.banner_btn.setOnClickListener(new HomeAppListDownloadListener(homeInfo, true));
	}

	private void setCardDownloadIvOnClick(HolderView hv, HomeAppListBaseInfo homeInfo) {
		((ViewGroup) hv.card_download_iv.getParent()).setOnClickListener(new HomeAppListDownloadListener(homeInfo, false));
	}

	private class CardOnTouchListener implements OnTouchListener {

		private HomeAppListItemInfo info;

		public CardOnTouchListener(HomeAppListItemInfo info) {
			this.info = info;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				v.setBackgroundResource(R.drawable.list_card_item_bg_pressed);
				break;
			case MotionEvent.ACTION_UP:
				enterDetail(info);
				ClickNumStatistics.addHomeRecommendListGame(context, info.gamename);
				v.setBackgroundResource(R.drawable.list_card_item_bg);
				break;
			case MotionEvent.ACTION_CANCEL:
				v.setBackgroundResource(R.drawable.list_card_item_bg);
				break;
			}
			return true;
		}

	}

	private void enterDetail(HomeAppListBaseInfo info) {
		Intent intent = new Intent(context, GameDetailsActivity.class);
		intent.putExtra(GameDetailConstants.KEY_GAME_ID, info.gameid);
		intent.putExtra(GameDetailConstants.KEY_GAME_NAME, info.gamename);
		context.startActivity(intent);
	}

	private class BannerOnClick implements OnClickListener {
		private HomeAppListBannerInfo info;

		public BannerOnClick(HomeAppListBannerInfo info) {
			this.info = info;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			enterDetail(info);
			ClickNumStatistics.addHomeRecommendBannerGame(context, info.gamename);
		}

	}

	class HolderView {
		RelativeLayout banner_layout;
		LinearLayout card_layout;
		TextView banner_name, banner_pb_tv;
		ImageView banner_btn;
		RoundCornerImageView banner_bg;

		TextView card_name, card_download_times, card_size, card_download_tv, card_recommend_tv, card_pb_tv;
		GameLabelView card_game_label;
		RoundCornerImageView card_icon;
		CircleProgressBar card_download_iv;
		RatingBar card_rating;
	}

	// fixbug
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		pullUpLv.setMeasure(true);
		super.notifyDataSetChanged();
	}

	private MyTouchDelegate mTouchDelegate = new MyTouchDelegate() {

		@Override
		public void postDelegateArea(Rect delegateArea, View v) {
			// TODO Auto-generated method stub
			int newW = 72;// dp=108px in 480*800
			int newh = 72;// dp=108px in 480*800
			int wDp = UIUtil.px2dip(context, v.getWidth());
			int hDp = UIUtil.px2dip(context, v.getHeight());

			int offsetW = UIUtil.dip2px(context, newW - wDp);
			int offsetH = UIUtil.dip2px(context, newh - hDp);
			delegateArea.top -= offsetH / 2;
			delegateArea.bottom += offsetH / 2;
			delegateArea.left -= offsetW / 2;
			delegateArea.right += offsetW / 2;
		}
	};

	public int getItemViewType(int position) {
		return infoList.get(position) instanceof HomeAppListBannerInfo ? 0 : 1;
	};

	public int getViewTypeCount() {
		return VIEWTYP;
	};
}
