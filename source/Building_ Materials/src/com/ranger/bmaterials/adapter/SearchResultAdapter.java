package com.ranger.bmaterials.adapter;

import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.view.GameLabelView;
import com.ranger.bmaterials.view.ImageViewForList;

public class SearchResultAdapter extends AbstractListAdapter<SearchItem> {

	private static final String TAG = null;
	private static final boolean DEBUG = true;
	
	DisplayImageOptions options = ImageLoaderHelper.optionForDownload;

	ForegroundColorSpan sizeSavedColorSpan = new ForegroundColorSpan(context
			.getResources().getColor(
					R.color.color_diff_update_total_size_to_download));
	ImageSpan imageSpanFace;

	public SearchResultAdapter(Context context) {
		super(context);
		this.data = new CopyOnWriteArrayList<SearchItem>();
		imageSpanFace = new ImageSpan(context, R.drawable.icon_face);
	}

	static class Holder {
        RoundCornerImageView icon;
		TextView title;
		RatingBar rating;
		TextView downloadTimes;
		TextView gameSize;

		ImageView comingIv;
		// TextView downloadProgress;
		// ProgressBar progressBar ;
		// View button ;
		ImageView actionIv;
		TextView actionTv;
		GameLabelView label_name;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		Holder holder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.search_result_list_item, parent,
					false);
			holder = new Holder();
			holder.title = (TextView) view.findViewById(R.id.game_name);
			holder.icon = (RoundCornerImageView) view.findViewById(R.id.game_icon);
			holder.rating = (RatingBar) view.findViewById(R.id.game_rating);
			holder.downloadTimes = (TextView) view
					.findViewById(R.id.game_download_times);
			holder.gameSize = (TextView) view.findViewById(R.id.game_size);

			// holder.downloadProgress = (TextView)
			// view.findViewById(R.id.game_download_progress);
			// holder.progressBar = (ProgressBar)
			// view.findViewById(R.id.game_download_progressbar);

			holder.actionIv = (ImageView) view
					.findViewById(R.id.search_item_action_iv);
			holder.actionTv = (TextView) view
					.findViewById(R.id.search_item_action_tv);
			holder.comingIv = (ImageView) view
					.findViewById(R.id.search_item_comingsoon_iv);
			holder.label_name = (GameLabelView) view
					.findViewById(R.id.game_label_name);

			View p = (View) holder.actionIv.getParent();
			p.setOnClickListener(this);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (Holder) view.getTag();

		}
		View p = (View) holder.actionIv.getParent();
		p.setTag(position);
		bindView(view, position, holder);

		return view;
	}

	public void updateItemView(ListView listView) {
		int firstVisiblePosition = listView.getFirstVisiblePosition();
		int lastVisiblePosition = listView.getLastVisiblePosition();
		int childCount = listView.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = listView.getChildAt(i);
			SearchItem item = getItem(i + firstVisiblePosition);
			updateItemView(childView, item);
		}
	}

	public void updateItemView(ListView listView, String gameId) {

		int targetPos = -1;
		SearchItem targetItem = null;
		int count = getCount();
		for (int i = 0; i < count; i++) {
			SearchItem item = getItem(i);
			if (item.getGameId().equals(gameId)) {
				targetPos = i;
				targetItem = item;
			}
		}
		// Log.v("SearchResultAdapter","updateItemView target position:"+targetPos);
		if (targetPos == -1) {
			// Log.v("SearchResultAdapter","updateItemView targetPos is -1 for "+gameId);
			return;
		}

		int firstVisiblePosition = listView.getFirstVisiblePosition();
		// 因为有header的存在
		if ((targetPos - firstVisiblePosition) < 0) {
			return;
		}
		View childView = listView.getChildAt(targetPos - firstVisiblePosition);

		if (childView != null) {
			updateItemView(childView, targetItem);
		}

	}

	public void updateItemView(View view, SearchItem item) {
		ImageView actionIv = (ImageView) view
				.findViewById(R.id.search_item_action_iv);
		TextView actionTv = (TextView) view
				.findViewById(R.id.search_item_action_tv);
		updateItemView(item, actionIv, actionTv);
	}

	private void updateItemView(SearchItem item, ImageView actionIv,
			TextView actionTv) {
		View p = (View) actionIv.getParent();
		Integer position = (Integer) p.getTag();

		String downloadUrl = getItem(position).getDownloadUrl();
		long itemId = getItemId(position);
		if (!downloadUrl.equals(item.getDownloadUrl())) {
		} else {
			bindProgress(item, actionIv, actionTv);
		}

	}

	private void setViewForUpdate(View view, SearchItem item) {
		view.findViewById(R.id.game_rating).setVisibility(View.GONE);
		ViewGroup versionViewGroup = (ViewGroup) view
				.findViewById(R.id.game_version_layout);
		versionViewGroup.setVisibility(View.VISIBLE);
		TextView versionText = (TextView) versionViewGroup
				.findViewById(R.id.item_version);
		TextView newVersionText = (TextView) versionViewGroup
				.findViewById(R.id.item_new_version);

		String localVersion = item.getLocalVersion();
		String version = item.getVersion();
		versionText.setText(localVersion);
		newVersionText.setText(version);

		view.findViewById(R.id.game_download_times_layout).setVisibility(
				View.GONE);
		ViewGroup sizeViewGroup = (ViewGroup) view
				.findViewById(R.id.game_size_layout_parent);
		sizeViewGroup.setVisibility(View.VISIBLE);
		TextView sizeText = (TextView) sizeViewGroup
				.findViewById(R.id.item_size);
		ImageView sizeTextStrike = (ImageView) sizeViewGroup
				.findViewById(R.id.item_size_strike);
		TextView newSizeText = (TextView) sizeViewGroup
				.findViewById(R.id.item_new_size);

		int apkStatus = item.getApkStatus();
		if (apkStatus == PackageMode.UPDATABLE_DIFF) {
			sizeText.setText(Formatter.formatFileSize(context,
					item.getPackageSize()));
			sizeTextStrike.setVisibility(View.VISIBLE);

			SpannableString sizeString = new SpannableString(
					"1"
							+ String.format(
									context.getString(R.string.update_managment_hint_patchsize),
									Formatter.formatFileSize(context,
											item.getPatchSize())));
			sizeString.setSpan(sizeSavedColorSpan, 0, sizeString.length(),
					Spannable.SPAN_INCLUSIVE_INCLUSIVE);
			sizeString.setSpan(imageSpanFace, 0, 1,
					Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			newSizeText.setText(sizeString);

			String format = String.format("Diff update size:%s for %s",
					item.getPackageSize(), item.getGameName());
			if (DEBUG) {
				Log.i(TAG, format);
			}
		} else {
			sizeText.setText(Formatter.formatFileSize(context,
					item.getPackageSize()));
			sizeTextStrike.setVisibility(View.GONE);
			newSizeText.setVisibility(View.GONE);
			if (DEBUG) {
				String format = String.format(
						"Normal update.size:%s,new size %s for %s",
						item.getPackageSize(), item.getPatchSize(),
						item.getGameName());
				Log.i(TAG, format);
			}
		}

	}

	// String formatString = "%s %d%%";
	String formatString2 = "%d%%";

	private void bindProgress(SearchItem item, ImageView actionIv,
			TextView actionTv) {
		View p = (View) actionIv.getParent();
		p.setEnabled(true);
		boolean diffUpdate = item.isDiffDownload();
		int progressValue = 0;
		int apkStatus = item.getApkStatus();
		switch (apkStatus) {
		case PackageMode.UNDOWNLOAD:
			break;
		case PackageMode.INSTALLED:
			actionTv.setText(R.string.open);
			actionIv.setImageResource(R.drawable.icon_start_list);
			break;
		case PackageMode.UPDATABLE:
			// TODO
			actionTv.setText(R.string.update);
			actionIv.setImageResource(R.drawable.icon_update_list);

			break;
		case PackageMode.UPDATABLE_DIFF:
			// TODO
			actionTv.setText(R.string.update_diff);
			actionIv.setImageResource(R.drawable.btn_download_diff_update_selector);

			break;
		case PackageMode.DOWNLOAD_PENDING:
			actionIv.setImageResource(R.drawable.icon_waiting_list);
			actionTv.setText(R.string.label_waiting);
			progressValue = getProgressValue(item.getTotalBytes(),
					item.getCurrentBytes());

			break;
		case PackageMode.DOWNLOAD_RUNNING:
			actionIv.setImageResource(R.drawable.btn_download_pause_selector);
			actionTv.setText(R.string.label_pause);
			progressValue = getProgressValue(item.getTotalBytes(),
					item.getCurrentBytes());
			actionTv.setText(String.format(formatString2, progressValue));

			break;
		case PackageMode.DOWNLOAD_FAILED:
			actionIv.setImageResource(R.drawable.btn_download_retry_selector);
			actionTv.setText(R.string.try_again);
			break;
		case PackageMode.DOWNLOAD_PAUSED:
			actionIv.setImageResource(R.drawable.icon_resume_list);
			actionTv.setText(R.string.resume);

			break;
		case PackageMode.DOWNLOADED:
			// 普通下载成功（后台自动安装）
			if (!diffUpdate) {
				actionIv.setImageResource(R.drawable.icon_install_list);
				actionTv.setText(R.string.install);
			} else {
				actionIv.setImageResource(R.drawable.icon_install_list);
				// p.setEnabled(false);
				actionTv.setText(R.string.install);
			}
			break;
		case PackageMode.DOWNLOADED_DIFFERENT_SIGN:
			break;
		case PackageMode.MERGING:
			// 合并中
			// TODO
			p.setEnabled(false);
			actionIv.setImageResource(R.drawable.icon_checking_list);
			actionTv.setText("安全检查中");
			break;
		case PackageMode.MERGE_FAILED:
			// 合并失败，重新普通更新下载(要给出提示,不能删除原来的，更新即可)
			// TODO
			actionIv.setImageResource(R.drawable.btn_download_retry_selector);
			// p.setEnabled(false);
			actionTv.setText(R.string.try_again);
			break;

		case PackageMode.MERGED:
			// 合并成功，并且签名一致（后台自动安装）
			actionIv.setImageResource(R.drawable.icon_checking_list);
			actionTv.setText(R.string.install);

			break;
		case PackageMode.CHECKING:
			p.setEnabled(false);
			actionIv.setImageResource(R.drawable.icon_checking_list);
			actionTv.setText("安全检查中");
			break;
		case PackageMode.CHECKING_FINISHED:
			actionIv.setImageResource(R.drawable.icon_install_list);
			actionTv.setText(R.string.install);

			break;
		case PackageMode.MERGED_DIFFERENT_SIGN:
			// TODO
			break;
		case PackageMode.INSTALLING:
			p.setEnabled(false);
			actionIv.setImageResource(R.drawable.installing);
			actionTv.setText(R.string.installing);
			break;
		case PackageMode.INSTALL_FAILED:
			actionIv.setImageResource(R.drawable.icon_install_list);
			actionTv.setText(R.string.install);
			break;
		default:
			break;
		}

	}

	ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context
			.getResources().getColor(R.color.title_color_yellow));

	private void setProgressText(TextView textView, SearchItem item) {

		String c = Formatter.formatFileSize(context, item.getCurrentBytes());
		String t = Formatter.formatFileSize(
				context,
				item.getTotalBytes() > 0 ? item.getTotalBytes() : item
						.getPackageSize());

		int start = 0;
		int end = c.length();
		SpannableString spannableString = new SpannableString(c + "/" + t);
		spannableString.setSpan(foregroundColorSpan, start, end,
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		textView.setText(spannableString);
	}

	private void setDefaultViewStatus(TextView titleText, RatingBar ratingBar,
			TextView timesText, TextView sizeText, ImageView actionIv,
			TextView actionTv, SearchItem item) {

		if (ratingBar.getVisibility() != View.VISIBLE) {
			ratingBar.setVisibility(View.VISIBLE);
		}
		if (timesText.getVisibility() != View.VISIBLE) {
			timesText.setVisibility(View.VISIBLE);
		}
		if (sizeText.getVisibility() != View.VISIBLE) {
			sizeText.setVisibility(View.VISIBLE);
		}
		View p = (View) actionIv.getParent();
		if (!p.isEnabled()) {
			p.setEnabled(true);
		}
		actionIv.setImageResource(R.drawable.btn_download_selector);
		actionTv.setText(R.string.download);

		ratingBar.setRating(item.getStar());

		titleText.setText(item.getGameName());
		timesText.setText(StringUtil.formatTimes(item.getDownloadTimes()));
		sizeText.setText(StringUtil.getDisplaySize(item.getPackageSize()));
	}

	private void bindView(View view, int position, Holder holder) {
		SearchItem item = getItem(position);
		ImageLoaderHelper.displayImage(item.getIconUrl(), holder.icon);
//		holder.icon.displayImage(item.getIconUrl(), options);
		/**
		 * 默认的情形
		 */
		setDefaultViewStatus(holder.title, holder.rating, holder.downloadTimes,
				holder.gameSize, holder.actionIv, holder.actionTv, item);

		boolean pendingOnLine = item.isPendingOnLine();
		/**
		 * 即将上线的情形
		 */
		if (pendingOnLine) {
			View p = (View) holder.actionIv.getParent();
			p.setVisibility(View.GONE);
			holder.comingIv.setVisibility(View.VISIBLE);
			return;
		}
		int apkStatus = item.getApkStatus();
		// 有更新显示不同的view
		if (apkStatus == PackageMode.UPDATABLE
				|| apkStatus == PackageMode.UPDATABLE_DIFF) {
			setViewForUpdate(view, item);
		}else{
			setViewForNonUpdate(view);
		}
		bindProgress(item, holder.actionIv, holder.actionTv);

		if (item.labelName != null && !item.labelName.equals("")) {
			holder.label_name.setText(item.labelName);
			holder.label_name.setLabelColor(item.labelColor);
			holder.label_name.setVisibility(View.VISIBLE);
		} else
			holder.label_name.setVisibility(View.GONE);
	}

	private void setViewForNonUpdate(View view) {
		ViewGroup versionViewGroup = (ViewGroup) view
				.findViewById(R.id.game_version_layout);
		versionViewGroup.setVisibility(View.GONE);
		ViewGroup sizeViewGroup = (ViewGroup) view
				.findViewById(R.id.game_size_layout_parent);
		sizeViewGroup.setVisibility(View.GONE);
		view.findViewById(R.id.game_download_times_layout).setVisibility(
				View.VISIBLE);
	}

	public int getProgressValue(long total, long current) {
		if (total <= 0)
			return 0;
		return (int) (100L * current / total);
	}

	@Override
	public void onClick(View v) {
		if (onListItemClickListener == null) {
			return;
		}
		onListItemClickListener.onItemButtonClick(v, (Integer) v.getTag());
	}
}
