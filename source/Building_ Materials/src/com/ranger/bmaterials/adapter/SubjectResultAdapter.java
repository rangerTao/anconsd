package com.ranger.bmaterials.adapter;

import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.tools.StringUtil;

public class SubjectResultAdapter extends AbstractListAdapter<SearchItem> {

	private static final String TAG = null;
	public String subjectTitle;
	public String subjectContent;

	public SubjectResultAdapter(Context context) {
		super(context);
		this.data = new CopyOnWriteArrayList<SearchItem>();
	}

	static class Holder {
		ImageView icon;
		TextView title;
		RatingBar rating;
		TextView downloadTimes;
		TextView gameSize;
		ImageView comingIv;
		ImageView actionIv;
		TextView actionTv;

		// TextView subjectTitle;
		TextView subjectContent;
	}

	@Override
	public int getCount() {
		return data.size() + 1;// 加1
	}

	@Override
	public boolean isEnabled(int position) {
		if (position == 0) {
			return false;
		}

		return true;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0)
			return 0;

		return 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			if (position == 0) {
				view = mInflater.inflate(R.layout.subject_games_listview_first_item, parent, false);
				holder.subjectContent = (TextView) view.findViewById(R.id.label_subject_content);
			} else {
				view = mInflater.inflate(R.layout.search_result_list_item, parent, false);
				holder.title = (TextView) view.findViewById(R.id.game_name);
				holder.icon = (ImageView) view.findViewById(R.id.game_icon);
				holder.rating = (RatingBar) view.findViewById(R.id.game_rating);
				holder.downloadTimes = (TextView) view.findViewById(R.id.game_download_times);
				holder.gameSize = (TextView) view.findViewById(R.id.game_size);
				holder.actionIv = (ImageView) view.findViewById(R.id.search_item_action_iv);
				holder.actionTv = (TextView) view.findViewById(R.id.search_item_action_tv);
				holder.comingIv = (ImageView) view.findViewById(R.id.search_item_comingsoon_iv);

				View p = (View) holder.actionIv.getParent();
				p.setOnClickListener(this);
			}
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (Holder) view.getTag();

		}

		if (position == 0) {
			// holder.subjectTitle.setText(subjectTitle);
			holder.subjectContent.setText(subjectContent);
		} else {
			View p = (View) holder.actionIv.getParent();
			p.setTag(position);
			updateView(view, position, holder);
		}

		return view;
	}

	public void refreshAllItemsView(ListView listView) {
		int firstVisiblePosition = listView.getFirstVisiblePosition();

		int childCount = listView.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = listView.getChildAt(i);
			int index = i + firstVisiblePosition;

			if (index > 0) {
				SearchItem item = getItem(index - 1);
				this.updateItemView(childView, item);
			}
		}
	}

	public void updateItemView(ListView listView, String gameId) {

		int targetPos = -1;
		SearchItem targetItem = null;
		int count = getCount();

		for (int i = 0; i < count - 1; i++) {
			SearchItem item = getItem(i);
			if (item.getGameId().equals(gameId)) {
				targetPos = i + 1;
				targetItem = item;
			}
		}

		if (targetPos == -1) {
			return;
		}

		int firstVisiblePosition = listView.getFirstVisiblePosition();
		if ((targetPos - firstVisiblePosition) < 0) {
			return;
		}
		View childView = listView.getChildAt(targetPos - firstVisiblePosition);

		if (childView != null) {
			updateItemView(childView, targetItem);
		}
	}

	private void updateItemView(View view, SearchItem item) {
		ImageView actionIv = (ImageView) view.findViewById(R.id.search_item_action_iv);
		TextView actionTv = (TextView) view.findViewById(R.id.search_item_action_tv);

		View p = (View) actionIv.getParent();
		Integer position = (Integer) p.getTag();
		position -= 1;
		String downloadUrl = getItem(position).getDownloadUrl();

		if (downloadUrl.equals(item.getDownloadUrl())) {
			updateDownloadStatus(item, actionIv, actionTv);
		}
	}

	private void setViewForUpdate(View view, SearchItem item) {
		view.findViewById(R.id.game_rating).setVisibility(View.GONE);
		ViewGroup versionViewGroup = (ViewGroup) view.findViewById(R.id.game_version_layout);
		versionViewGroup.setVisibility(View.VISIBLE);
		TextView versionText = (TextView) versionViewGroup.findViewById(R.id.item_version);
		TextView newVersionText = (TextView) versionViewGroup.findViewById(R.id.item_new_version);

		String localVersion = item.getLocalVersion();
		String version = item.getVersion();
		versionText.setText(localVersion);
		newVersionText.setText(version);

		view.findViewById(R.id.game_download_times_layout).setVisibility(View.GONE);
		ViewGroup sizeViewGroup = (ViewGroup) view.findViewById(R.id.game_size_layout_parent);
		TextView sizeText = (TextView) sizeViewGroup.findViewById(R.id.item_size);
		ImageView sizeTextStrike = (ImageView) sizeViewGroup.findViewById(R.id.item_size_strike);
		TextView newSizeText = (TextView) sizeViewGroup.findViewById(R.id.item_new_size);

		if (item.isDiffDownload()) {
			sizeText.setText(Formatter.formatFileSize(context, item.getPackageSize()));
			sizeTextStrike.setVisibility(View.VISIBLE);
			newSizeText.setText(Formatter.formatFileSize(context, item.getPatchSize()));
		} else {
			sizeText.setText(Formatter.formatFileSize(context, item.getPackageSize()));
			sizeTextStrike.setVisibility(View.GONE);
			newSizeText.setVisibility(View.GONE);
		}

	}

	

	private void updateDownloadStatus(SearchItem item, ImageView actionIv, TextView actionTv) {
		View p = (View) actionIv.getParent();
		p.setEnabled(true);
		boolean diffUpdate = item.isDiffDownload();
		int progressValue = 0;
		int apkStatus = item.getApkStatus();
		String formatString = "%d%%";
		
		switch (apkStatus) {
		case PackageMode.UNDOWNLOAD:
			break;
		case PackageMode.INSTALLED:
			actionTv.setText(R.string.open);
			actionIv.setImageResource(R.drawable.icon_start_list);

			break;
		case PackageMode.UPDATABLE:
			actionTv.setText(R.string.update);
			actionIv.setImageResource(R.drawable.icon_update_list);

			break;
		case PackageMode.UPDATABLE_DIFF:
			actionTv.setText(R.string.update_diff);
			actionIv.setImageResource(R.drawable.btn_download_diff_update_selector);

			break;
		case PackageMode.DOWNLOAD_PENDING:
			actionIv.setImageResource(R.drawable.icon_waiting_list);
			actionTv.setText(R.string.label_waiting);

			break;
		case PackageMode.DOWNLOAD_RUNNING:
			actionIv.setImageResource(R.drawable.btn_download_pause_selector);
			progressValue = getProgressValue(item.getTotalBytes(), item.getCurrentBytes());
			actionTv.setText(String.format(formatString, progressValue));

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
			actionIv.setImageResource(R.drawable.icon_install_list);
			actionTv.setText(R.string.install);
			break;
		case PackageMode.MERGING:
			// 合并中
			p.setEnabled(false);
			actionIv.setImageResource(R.drawable.icon_checking_list);
			actionTv.setText("安全检查中");
			break;
		case PackageMode.MERGE_FAILED:
			// 合并失败，重新普通更新下载(要给出提示,不能删除原来的，更新即可)
			actionIv.setImageResource(R.drawable.btn_download_retry_selector);
			actionTv.setText(R.string.try_again);

			break;
		case PackageMode.MERGED:
			// 合并成功，并且签名一致（后台自动安装）
			if (!diffUpdate) {
				Log.e(TAG, "error");
			}
			actionIv.setImageResource(R.drawable.btn_download_retry_selector);
			actionTv.setText(R.string.install);

			break;
		case PackageMode.CHECKING:
			p.setEnabled(false);
			actionIv.setImageResource(R.drawable.icon_checking_list);
			actionTv.setText("安全检查中");
			break;

		case PackageMode.CHECKING_FINISHED:
			if (!diffUpdate) {
				Log.e(TAG, "error");
			}
			actionIv.setImageResource(R.drawable.icon_install_list);
			actionTv.setText(R.string.install);

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

	private void setDefaultViewStatus(TextView titleText, RatingBar ratingBar, TextView timesText,
			TextView sizeText, ImageView actionIv, TextView actionTv, SearchItem item) {

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
		String sizeTextString = Formatter.formatFileSize(context, item.getPackageSize());
		sizeText.setText(sizeTextString);

	}

	private void updateView(View view, int position, Holder holder) {

		if (position == 0) {
			return;
		}

		SearchItem item = getItem(position - 1);
		ImageLoaderHelper.displayImage(item.getIconUrl(), holder.icon);
		/**
		 * 默认的情形
		 */
		setDefaultViewStatus(holder.title, holder.rating, holder.downloadTimes, holder.gameSize,
				holder.actionIv, holder.actionTv, item);

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
		if (apkStatus == PackageMode.UPDATABLE || apkStatus == PackageMode.UPDATABLE_DIFF) {
			setViewForUpdate(view, item);
		}
		updateDownloadStatus(item, holder.actionIv, holder.actionTv);

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
