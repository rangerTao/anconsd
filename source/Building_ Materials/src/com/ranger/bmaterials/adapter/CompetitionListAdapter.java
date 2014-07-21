package com.ranger.bmaterials.adapter;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.app.GameDetailConstants;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.app.StartGame;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;
import com.ranger.bmaterials.mode.CompetitionResult.CompetitionInfo;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.ui.CustomToast;
import com.ranger.bmaterials.ui.GameDetailsActivity;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.view.RoundProgressBar;

public class CompetitionListAdapter extends PagerAdapter/* BaseAdapter */{
	private Context cx;
	public ArrayList<CompetitionInfo> infos = new ArrayList<CompetitionInfo>();
	private ArrayList<View> views = new ArrayList<View>();
	private DisplayImageOptions options;
	private OnScrollLastItemListener onScrollLastItemListener;
	private OnPageChangeListener listener;

	public CompetitionListAdapter(Context cx, OnPageChangeListener listener) {
		this.cx = cx;
		this.listener = listener;
		options = ImageLoaderHelper.getCustomOption(false, R.drawable.ad_default);
	}

	@Override
	public int getCount() {

		return infos.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {

		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {

		((ViewPager) container).removeView(views.get(position));
		views.set(position, null);//释放内存
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		views.set(position,getView(position));
		View child = views.get(position);
		((ViewPager) container).addView(child);
		// 给每个item的view 就是刚才views存放着的view
		if (position == views.size() - 1 && onScrollLastItemListener != null) {
			onScrollLastItemListener.OnScrollLastItem();
		}
		return child;
	}

	public View getCurrentItem(int position) {
		return views.get(position);
	}

	public int getCurrentItemResCode(int position) {
		return infos.get(position).rescode;
	}

	public CompetitionInfo getCurrentCompetitionInfo(int position) {
		return infos.get(position);
	}

	public void notifyDataSetChanged(boolean isfirstRequest) {
		for (int i = views.size(); i < infos.size(); i++) {
			views.add(i, null);
		}
		super.notifyDataSetChanged();
		if (isfirstRequest && infos.size() > 0) {
			listener.onPageSelected(0);
		}
	}

	public View getView(int position) {
		CompetitionInfo info = infos.get(position);
		View contentView = View.inflate(cx, R.layout.competition_list_item, null);
		TextView game_name_tv = (TextView) contentView.findViewById(R.id.competition_game_name);
		RoundCornerImageView picIv = (RoundCornerImageView) contentView.findViewById(R.id.competition_pic);
		TextView rewards_tv = (TextView) contentView.findViewById(R.id.competition_detail_rewards);
		TextView rule_tv = (TextView) contentView.findViewById(R.id.competition_detail_rule);
		Button jionin = (Button) contentView.findViewById(R.id.competition_jionin);
		RelativeLayout container = (RelativeLayout) contentView.findViewById(R.id.competition_imitate_layout_container);
		boolean[] enabled = { true, true };
		picIv.setCornersEnabled(enabled);
		picIv.setRadius(UIUtil.dip2px(cx, 5f));
		picIv.setDisplayImageOptions(options);
		picIv.setImageUrl(info.picUrl);
		game_name_tv.setText(info.gameName);
		rewards_tv.setText(info.rewards);
		rule_tv.setText(info.rule);
		switch (info.rescode) {
		case CompetitionInfo.competitionsoon: {
			View inflate = View.inflate(cx, R.layout.competition_timer_imitate_layout, null);
			container.addView(inflate);
			TextView start_tv = (TextView) inflate.findViewById(R.id.start_time);
			TextView end_tv = (TextView) inflate.findViewById(R.id.end_time);
			start_tv.setText(info.date.substring(5));
			end_tv.setText(info.end_date.substring(5));
			jionin.setBackgroundResource(R.drawable.bt_competition_timer_selector);
			jionin.setText(R.string.competition_timer);
		}
			break;
		case CompetitionInfo.competitioning: {
			View inflate = View.inflate(cx, R.layout.competition_joinin_imitate_layout, null);
			container.addView(inflate);
			TextView start_tv = (TextView) inflate.findViewById(R.id.start_time);
			TextView end_tv = (TextView) inflate.findViewById(R.id.end_time);
			start_tv.setText(info.date.substring(5));
			end_tv.setText(info.end_date.substring(5));
			jionin.setText(R.string.competition_joinin);
		}
			break;

		case CompetitionInfo.competitioned: {
			container.addView(View.inflate(cx, R.layout.competition_over_imitate_layout, null));
			jionin.setBackgroundResource(R.drawable.bt_competition_over_selector);
			jionin.setText(R.string.competition_over);
		}
			break;
		}
		jionin.setOnClickListener(new JoinInListener(position));
		return contentView;
	}

	private class JoinInListener implements OnClickListener {
		private int position;

		public JoinInListener(int position) {

			this.position = position;
		}

		@Override
		public void onClick(View v) {

			final CompetitionInfo info = infos.get(position);
			if (info.rescode == 0)
				new AsyncTask<Void, Void, PackageMode>() {

					@Override
					protected PackageMode doInBackground(Void... params) {

						int versionInt = 0;
						if (info.versionInt != null && !info.versionInt.equals(""))// maybe
																					// null
							versionInt = Integer.valueOf(info.versionInt);
						QueryInput qi = new QueryInput(info.pkgName, info.version, versionInt, info.downloadUrl, info.gameId);

						Map<QueryInput, PackageMode> status = PackageHelper.queryPackageStatus(qi);
						return status.get(qi);
					};

					protected void onPostExecute(PackageMode mode) {
						joinBtnOnClick(info, mode);
					}
				}.execute();
			else if(info.rescode == 2){
				//即将开赛，不进入游戏详情
			}else 
				handlerNetException(new INetException() {

					@Override
					public void onNetAvail() {

						enterGameDetail(false, info);
					}
				});
		}

	}

	// public void detailLayoutChange(ImageView btn, View layout, int position)
	// {
	// boolean detail_btn_visible = btn_visible_map.get(position);
	//
	// if (detail_btn_visible) {
	// btn.setImageResource(R.drawable.bt_competition_up_selector);
	// } else
	// btn.setImageResource(R.drawable.bt_competition_down_selector);
	// layout.setVisibility(detail_btn_visible ? View.VISIBLE : View.GONE);
	//
	// }

	private void joinBtnOnClick(final CompetitionInfo info, PackageMode mode) {
		switch (mode.status) {
		case PackageMode.UPDATABLE:
		case PackageMode.UPDATABLE_DIFF:
		case PackageMode.INSTALLED:
			StartGame isg = new StartGame(cx, info.pkgName, info.startAction, info.gameId, info.isNeedLogin);
			isg.startGame();
			ClickNumStatistics.addCompetitionGameStartDlStatistis(cx, info.gameName);
			break;
		case PackageMode.DOWNLOADED:
			if (mode.isDiffDownload) {
				PackageHelper.sendMergeRequest(mode, true);
			}
		case PackageMode.CHECKING_FINISHED:
			PackageHelper.installApp((Activity) cx, info.pkgName, info.gameId, mode.downloadDest);
			break;
		case PackageMode.UNDOWNLOAD:
			ClickNumStatistics.addCompetitionGameDlStatistis(cx);
			// 跳到游戏详情页并自动下载
			handlerNetException(new INetException() {

				@Override
				public void onNetAvail() {

					enterGameDetail(true, info);
				}
			});
			break;
		default:
			handlerNetException(new INetException() {

				@Override
				public void onNetAvail() {

					enterGameDetail(false, info);
				}
			});
			break;
		}
	}

	private void enterGameDetail(boolean auto_down, CompetitionInfo info) {
		Intent i = new Intent(cx, GameDetailsActivity.class);
		i.putExtra(GameDetailConstants.KEY_GAME_AUTO_DOWNLOAD, auto_down);
		i.putExtra(GameDetailConstants.KEY_GAME_ID, info.gameId);
		i.putExtra(GameDetailConstants.KEY_GAME_NAME, info.gameName);
		((Activity) cx).startActivity(i);
		ClickNumStatistics.addHomeGameCoverStatistics(cx, info.gameName);
	}

	private void handlerNetException(INetException e) {
		if (ConnectManager.isNetworkConnected(cx))
			e.onNetAvail();
		else
			CustomToast.showToast(cx, cx.getString(R.string.network_error_hint));
	}

	private interface INetException {
		void onNetAvail();
	}

	// class HolderView {
	// RoundCornerImageView picIv;
	// RoundProgressBar roundprogressbar;
	// TextView game_name_tv/*, time_tv*//*, memebers_tv*/, jionin_tv,
	// rewards_tv, rule_tv/*, coins_tv, toprewards_tv*/;
	// TextView start_tv,end_tv;
	// }

	public void setOnScrollLastItemListener(OnScrollLastItemListener onScrollLastItemListener) {
		this.onScrollLastItemListener = onScrollLastItemListener;
	}

	public static interface OnScrollLastItemListener {
		void OnScrollLastItem();
	}
}
