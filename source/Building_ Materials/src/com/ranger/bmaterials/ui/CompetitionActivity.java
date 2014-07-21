package com.ranger.bmaterials.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.CompetitionListAdapter;
import com.ranger.bmaterials.adapter.CompetitionListAdapter.OnScrollLastItemListener;
import com.ranger.bmaterials.mode.CompetitionResult;
import com.ranger.bmaterials.mode.CompetitionResult.CompetitionInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.view.RoundProgressBar;
import com.ranger.bmaterials.work.LoadingTask;
import com.ranger.bmaterials.work.LoadingTask.ILoading;

public class CompetitionActivity extends HeaderCoinBackBaseActivity implements OnScrollLastItemListener, OnPageChangeListener
/* ScrollBottomListener */{
	private ViewPager plv;
	private CompetitionListAdapter adapter;
	private AtomicInteger current_page_index = new AtomicInteger(1);
	private final int PAGER_NUMBER = 10;
	SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		plv = (ViewPager) findViewById(R.id.competition_pull_refresh_list);
		// plv.setFooter(createLoadingFooter());

		// headerView = View.inflate(this, R.layout.competion_list_header_view,
		// null);
		//
		// plv.addHeaderView(headerView);
		adapter = new CompetitionListAdapter(this, this);

		// plv.setScrollBottomListener(this);
		adapter.setOnScrollLastItemListener(this);
		plv.setAdapter(adapter);
		plv.setOnPageChangeListener(this);
		loadData(new InitRequestHandler(), true);
	}

	// private View createLoadingFooter() {
	// View footer = View.inflate(this, R.layout.loading_layout, null);
	// TextView subView = (TextView) footer.findViewById(R.id.loading_text);
	// subView.setText(R.string.pull_to_refresh_refreshing_label);
	// footer.setFocusable(false);
	// footer.setFocusableInTouchMode(false);
	// return footer;
	// }

	private boolean isDataEnd;

	private void refreshList(ArrayList<CompetitionInfo> info, int gamesCount, boolean isfirstRequest) {
		if (info.isEmpty())
			return;
		adapter.infos.addAll(info);
		isDataEnd = adapter.infos.size() >= gamesCount;
		adapter.notifyDataSetChanged(isfirstRequest);
	}

	private class InitRequestHandler implements IRequestListener {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			// TODO Auto-generated method stub
			CompetitionResult result = (CompetitionResult) responseData;
			if (result.competitions_list != null && !result.competitions_list.isEmpty()) {
				current_page_index.incrementAndGet();

				// if (result.bannerIconUrl != null &&
				// !result.bannerIconUrl.equals("")) {
				// 有图片则默认布局都隐藏
				// showListHeaderImageView(result.bannerIconUrl);
				// }
				refreshList(result.competitions_list, result.gamesCount, true);
				// if (result.bannerIconUrl == null ||
				// result.bannerIconUrl.equals(""))
				// plv.post(new Runnable() {
				//
				// @Override
				// public void run() {
				// // TODO Auto-generated method stub
				// // showListHeaderAnimationView();
				// }
				// });
			}

		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			// TODO Auto-generated method stub

		}

	}

	private void loadData(final IRequestListener l, final boolean isShowNoNetWorkView) {
		LoadingTask requestDataTask = new LoadingTask(this, new ILoading() {

			@Override
			public void loading(IRequestListener listener) {
				// TODO Auto-generated method stub
				NetUtil.getInstance().requestCompetition(current_page_index.get(), PAGER_NUMBER, listener);
			}

			@Override
			public void preLoading(View network_loading_layout, View network_loading_pb, View network_error_loading_tv) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isShowNoNetWorkView() {
				// TODO Auto-generated method stub
				return isShowNoNetWorkView;
			}

			@Override
			public IRequestListener getRequestListener() {
				// TODO Auto-generated method stub
				return l;
			}

			@Override
			public boolean isAsync() {
				// TODO Auto-generated method stub
				return false;
			}
		});
		requestDataTask.loading();
	}

	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.competition_activity;
	}

	@Override
	public String getHeaderTitle() {
		// TODO Auto-generated method stub
		return getString(R.string.str_competition_title);
	}

	// private View headerView;

	// private void showListHeaderAnimationView() {
	// ImageView bg = (ImageView) headerView
	// .findViewById(R.id.competition_cup_bg);
	//
	// AnimationSet set = new AnimationSet(false);
	// set.setFillAfter(true);
	// ScaleAnimation sa = new ScaleAnimation(1f, 2f, 1f, 2f,
	// Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
	// 0.5f);
	//
	// sa.setDuration(0);
	//
	// RotateAnimation ra = new RotateAnimation(0, 360,
	// Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
	// 0.5f);
	// ra.setRepeatMode(Animation.RESTART);
	// ra.setInterpolator(new LinearInterpolator());
	// ra.setDuration(5000);
	// ra.setRepeatCount(Animation.INFINITE);
	//
	// set.addAnimation(sa);
	// set.addAnimation(ra);
	//
	// ViewGroup parent = (ViewGroup) bg.getParent();
	// parent.setClipChildren(false);
	// bg.startAnimation(set);
	// }

	// private void showListHeaderImageView(String imgUrl) {
	// ImageView bannerIconIv = (ImageView) headerView
	// .findViewById(R.id.competition_banner_bg);
	// bannerIconIv.setBackgroundResource(0);
	// headerView.findViewById(R.id.competition_desc_bg).setVisibility(
	// View.GONE);
	// headerView.findViewById(R.id.competition_desc_tv).setVisibility(
	// View.GONE);
	// headerView.findViewById(R.id.competition_cup).setVisibility(View.GONE);
	// ImageView cup_bg = (ImageView) headerView
	// .findViewById(R.id.competition_cup_bg);
	// cup_bg.clearAnimation();
	// cup_bg.setVisibility(View.GONE);
	// ImageLoaderHelper.displayImage(imgUrl, bannerIconIv,
	// ImageLoaderHelper.getDefaultImageOptions(true));
	// }

	@Override
	public void OnScrollLastItem() {
		// TODO Auto-generated method stub
		if (DeviceUtil.isNetworkAvailable(this)) {
			// if (!isDataEnd) {
			// if (!plv.isFooterVisible()) {
			// plv.showFooter();
			//
			// }
			// } else
			// plv.removeFooter();
			loadData(new LoadMoreRequestHandler(), false);
		}
	}

	private class LoadMoreRequestHandler implements IRequestListener {

		@Override
		public void onRequestSuccess(BaseResult responseData) {
			// TODO Auto-generated method stub
			CompetitionResult result = (CompetitionResult) responseData;
			if (result.competitions_list != null && !result.competitions_list.isEmpty()) {
				refreshList(result.competitions_list, result.gamesCount, false);
				current_page_index.incrementAndGet();
			} else {
				isDataEnd = true;
			}
			// plv.hideFooter();
		}

		@Override
		public void onRequestError(int requestTag, int requestId, int errorCode, String msg) {
			// TODO Auto-generated method stub
			// plv.hideFooter();
		}
	}

	@Override
	protected void onDestroy() {
		plv.setAdapter(null);
		plv = null;
		super.onDestroy();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// private int convertDateToProgress(String startDate) {
	// try {
	// long l = df.parse(startDate).getTime() - System.currentTimeMillis();
	// if (l < 0)
	// return -1;
	// long day = l / (24 * 60 * 60 * 1000);
	// long hour = (l / (60 * 60 * 1000) - day * 24);
	// long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
	// long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
	// if (day > 0)
	// return 0;
	// if (hour > 0) {
	// return (int) (hour * 1.0 / 24 * 60);
	// }
	// if (min > 0) {
	// return (int) min;
	// }
	// if (s > 0)
	// return (int) s;
	// return 0;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return -1;
	// }

	private String convertDateToString(String startDate) {
		try {
			long l = df.parse(startDate).getTime() - System.currentTimeMillis();
			if (l < 0)
				return "";
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
//			String dd = day < 10 ? "0" + day : "" + day;
			String hh = hour < 10 ? "0" + hour : "" + hour;
			String mm = min < 10 ? "0" + min : "" + min;
			String ss = s < 10 ? "0" + s : "" + s;
			if(day>0)
				return "";
			return hh + ":" + mm + ":" + ss;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void onPageSelected(int arg0) {
		final View currentView = adapter.getCurrentItem(arg0);
		if (currentView == null)
			return;
		final int rescode = adapter.getCurrentItemResCode(arg0);
		switch (rescode) {
		case CompetitionInfo.competitionsoon: {
			final RoundProgressBar roundprogressbar_timer = (RoundProgressBar) currentView.findViewById(R.id.roundprogressbar_timer);
			final Button competition_jionin = (Button) currentView.findViewById(R.id.competition_jionin);
			final CompetitionInfo info = adapter.getCurrentCompetitionInfo(arg0);
			new AsyncTask<Void, Integer, Integer>() {
				@Override
				protected Integer doInBackground(Void... params) {
					for (int i = 0; i < 20; i++) {
						publishProgress((int) (50 * 1.0 / 20 * i));
						SystemClock.sleep(100);
					}
					return null;
				}

				@Override
				protected void onProgressUpdate(Integer... values) {
					roundprogressbar_timer.setProgress(values[0]);
				}

				@Override
				protected void onPostExecute(Integer result) {
					roundprogressbar_timer.setProgress(40);
				}
			}.execute();

			if (currentView.getTag() == null) {
				final Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								String convertDateToString = convertDateToString(info.date);
								competition_jionin.setText(getString(R.string.competition_timer).concat(convertDateToString));
								if (TextUtils.isEmpty(convertDateToString))
									timer.cancel();
							}
						});
					}
				}, 0, 1000);
				currentView.setTag(timer);
			}
		}
			break;
		case CompetitionInfo.competitioning: {
			View competition_joinin_inner_icon = currentView.findViewById(R.id.competition_joinin_inner_icon);
			AnimationSet animationSet = new AnimationSet(true);
			ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			Animation rotateAnimation = new RotateAnimation(-180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			animationSet.setDuration(2000);
			animationSet.setInterpolator(new BounceInterpolator());
			animationSet.addAnimation(rotateAnimation);
			animationSet.addAnimation(scaleAnimation);
			competition_joinin_inner_icon.startAnimation(animationSet);
		}
			break;
		case CompetitionInfo.competitioned: {
			View competition_over_inner_bg_icon = currentView.findViewById(R.id.competition_over_inner_bg_icon);
			final View competition_over_inner_icon = currentView.findViewById(R.id.competition_over_inner_icon);
			Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 100.0f, 0.0f);
			translateAnimation.setDuration(1000);
			translateAnimation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					competition_over_inner_icon.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {

					AnimationSet animationSet = new AnimationSet(true);
					ScaleAnimation scaleAnimation = new ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					Animation rotateAnimation = new RotateAnimation(30f, 360f, Animation.RELATIVE_TO_SELF, -0.2f, Animation.RELATIVE_TO_SELF, 0.0f);// new
																																					// RotateAnimation(0f,
					animationSet.setInterpolator(new DecelerateInterpolator());
					animationSet.setDuration(1000);
					animationSet.addAnimation(scaleAnimation);
					animationSet.addAnimation(rotateAnimation);
					competition_over_inner_icon.startAnimation(animationSet);
					competition_over_inner_icon.setVisibility(View.VISIBLE);
				}
			});
			competition_over_inner_bg_icon.startAnimation(translateAnimation);
		}
			break;
		default:
			break;
		}

	}

}
