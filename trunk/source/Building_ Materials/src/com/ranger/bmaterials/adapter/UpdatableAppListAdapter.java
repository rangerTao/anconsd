package com.ranger.bmaterials.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.R.id;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.view.ImageViewForList;

public class UpdatableAppListAdapter extends AbstractListAdapter<UpdatableAppInfo> implements OnClickListener {

	private static final String TAG = "UpdatableAppListAdapter";
	private PopupWindow pw;

	public UpdatableAppListAdapter(Context context) {
		super(context);
	}

	static class AppInfoViewHolder {
		TextView title;
        RoundCornerImageView icon;

		TextView version;
		TextView newVersion;

		TextView size;
		TextView newSize;

		ProgressBar progressBar;
		TextView savedSize;
		TextView progressText;

		// Button button;
		ImageView actionIv;
		TextView actionTv;

		ImageView ivFace;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (Constants.DEBUG)
			Log.i("updatetest", "getView,position:" + position);
		View view;
		AppInfoViewHolder appInfoView;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.manager_activity_updatable_list_item, parent, false);
			appInfoView = new AppInfoViewHolder();
			appInfoView.title = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_name);
			appInfoView.icon = (RoundCornerImageView) view.findViewById(R.id.manager_activity_updatable_list_item_icon);

			appInfoView.version = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_version);
			appInfoView.newVersion = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_new_version);

			appInfoView.size = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_size);
			appInfoView.newSize = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_new_size);

			appInfoView.ivFace = (ImageView) view.findViewById(R.id.ivSavedSizeFace);
			appInfoView.savedSize = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_saved_size);
			appInfoView.progressText = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_progress);

			appInfoView.actionIv = (ImageView) view.findViewById(R.id.manager_activity_updatable_list_item_action_iv);
			appInfoView.actionTv = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_action_tv);
			appInfoView.progressBar = (ProgressBar) view.findViewById(R.id.game_download_progressbar);

			View p = (View) appInfoView.actionIv.getParent();
			// p.setText("更新");
			p.setOnClickListener(this);

			view.setTag(appInfoView);
		} else {
			view = convertView;
			appInfoView = (AppInfoViewHolder) view.getTag();
		}
		View p = (View) appInfoView.actionIv.getParent();
		// p.setText("更新");
		p.setTag(position);
		// app icon
		appInfoView.icon.setTag(position);
		bindView(position, appInfoView);
		return view;
	}

	private void setDefaultViewStatus(UpdatableAppInfo item, AppInfoViewHolder appInfoView) {
		if (Constants.DEBUG)
			Log.i("updatetest", "setDefaultViewStatus diff:" + item.isDiffUpdate());
		appInfoView.title.setText(item.getName());
		String version = item.getVersion();
		String newVersion = item.getNewVersion();

		if (version != null && version.equals(newVersion)) {
			appInfoView.version.setText(version + ("(" + item.getVersionInt() + ")"));
			appInfoView.newVersion.setText(newVersion + "(" + item.getNewVersionInt() + ")");
		} else {
			appInfoView.version.setText(version);
			appInfoView.newVersion.setText(newVersion);
		}
		appInfoView.version.setVisibility(View.VISIBLE);
		appInfoView.newVersion.setVisibility(View.VISIBLE);
		View versionP = (View) appInfoView.newVersion.getParent();
		versionP.setVisibility(View.VISIBLE);

		if (item.isDiffUpdate()) {
			appInfoView.size.setText(Formatter.formatFileSize(context, item.getNewSize()));
			appInfoView.newSize.setText("只需:" + Formatter.formatFileSize(context, item.getPatchSize()));
			View parent = (View) appInfoView.size.getParent();
			parent.findViewById(R.id.manager_activity_updatable_list_item_size_strike).setVisibility(View.VISIBLE);

			appInfoView.actionIv.setImageResource(R.drawable.btn_download_diff_update_selector);
			appInfoView.actionTv.setText(R.string.update_diff);

			appInfoView.size.setVisibility(View.VISIBLE);
			appInfoView.newSize.setVisibility(View.VISIBLE);
			appInfoView.ivFace.setVisibility(View.VISIBLE);

			View sizeP = (View) appInfoView.newSize.getParent();
			sizeP.setVisibility(View.VISIBLE);

			if (Constants.DEBUG)
				Log.i("updatetest", "setDefaultViewStatus size and newSize visiable");

		} else {

			appInfoView.size.setText(Formatter.formatFileSize(context, item.getNewSize()));
			View parent = (View) appInfoView.size.getParent();
			parent.findViewById(R.id.manager_activity_updatable_list_item_size_strike).setVisibility(View.GONE);

			appInfoView.actionIv.setImageResource(R.drawable.btn_download_update_selector);
			appInfoView.actionTv.setText(R.string.update);

			appInfoView.size.setVisibility(View.VISIBLE);
			appInfoView.newSize.setVisibility(View.GONE);
			appInfoView.ivFace.setVisibility(View.GONE);

			View sizeP = (View) appInfoView.size.getParent();
			sizeP.setVisibility(View.VISIBLE);
		}

		appInfoView.progressBar.setVisibility(View.GONE);
		appInfoView.progressText.setVisibility(View.GONE);
		appInfoView.savedSize.setVisibility(View.GONE);

//		appInfoView.icon.displayImage(item.getIconUrl(), ImageLoaderHelper.optionForDownload);
        ImageLoaderHelper.displayImage(item.getIconUrl(),appInfoView.icon,ImageLoaderHelper.optionForDownload);
	}

	private void bindView(int position, AppInfoViewHolder appInfoView) {
		UpdatableAppInfo item = getItem(position);
		setDefaultViewStatus(item, appInfoView);
		bindProgress(item, appInfoView);
	}

	private void setDefaultStatusForDownload(ProgressBar progressBar, TextView savedText, ImageView ivFace, TextView progressText, ImageView actionIv, TextView actionTv) {

		if (progressBar.getVisibility() != View.VISIBLE) {
			progressBar.setVisibility(View.VISIBLE);
		}
		ViewGroup progressBarP = (ViewGroup) progressBar.getParent();
		View versionView = progressBarP.findViewById(R.id.game_version_layout);

		if (versionView.getVisibility() != View.GONE) {
			versionView.setVisibility(View.GONE);
		}

		View progressP = (View) savedText.getParent();
		if (savedText.getVisibility() != View.VISIBLE) {
			savedText.setVisibility(View.VISIBLE);
		}
		if (progressP.getVisibility() != View.VISIBLE) {
			progressP.setVisibility(View.VISIBLE);
		}

		View progressG = (View) progressP.getParent();
		View sizeP = progressG.findViewById(R.id.manager_activity_updatable_list_item_size_layout_parent);
		if (sizeP.getVisibility() != View.GONE) {
			sizeP.setVisibility(View.GONE);
		}

		if (progressText.getVisibility() != View.VISIBLE) {
			progressText.setVisibility(View.VISIBLE);
		}
		View actionP = (View) actionIv.getParent();
		if (!actionP.isEnabled()) {
			actionP.setEnabled(true);
		}
		actionIv.setImageResource(R.drawable.icon_update_list);
		actionTv.setText(R.string.update);
	}

	private void bindProgress(UpdatableAppInfo item, AppInfoViewHolder holder) {
		bindProgress(item, holder.progressBar, holder.savedSize, holder.size, holder.ivFace, holder.progressText, holder.actionIv, holder.actionTv, null);
	}

	public int getProgressValue(long total, long current) {
		if (total <= 0)
			return 0;
		return (int) (100L * current / total);
	}

	ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.color_diff_update_saved_size_hint));

	private void setProgressText(TextView textView, UpdatableAppInfo item) {

		String c = Formatter.formatFileSize(context, item.getCurrtentSize());
		
		String t = "";
		if(item.isDiffUpdate()){
			t = Formatter.formatFileSize(context, item.getTotalSize() <= 0 ? item.getPatchSize() : item.getTotalSize());
		}else{
			t = Formatter.formatFileSize(context, item.getTotalSize() <= 0 ? item.getNewSize() : item.getTotalSize());
		}

		int start = 0;
		int end = c.length();
		SpannableString spannableString = new SpannableString(c + "/" + t);
		spannableString.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		textView.setText(spannableString);
	}

	private void setProgressTextFinish(TextView textView, UpdatableAppInfo item) {

		String c = Formatter.formatShortFileSize(context, item.getCurrtentSize());
		String t = Formatter.formatShortFileSize(context, item.getTotalSize() <= 0 ? item.getNewSize() : item.getTotalSize());

		int start = 0;
		int end = c.length();
		SpannableString spannableString = new SpannableString(t + "/" + t);
		spannableString.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		textView.setText(spannableString);
	}

	private void setProgress(UpdatableAppInfo item, ProgressBar progressBar) {
		long currtentSize = item.getCurrtentSize();
		// From download server
		long totalSize = item.getTotalSize();
		
		// From http server
		long patchSize = item.getPatchSize();
		// From http server
		long size = item.getNewSize();

		if (item.isDiffUpdate()) {
			long uiDownloadedSize = size - patchSize;
			long uiTotalSize = 0;
			if (totalSize > 0) {
				uiTotalSize = totalSize + uiDownloadedSize;
			} else if (patchSize > 0) {
				uiTotalSize = patchSize + uiDownloadedSize;
			}

			long uiDownloadProgress = uiDownloadedSize + currtentSize;

			int uiDownloadedProgressValue = getProgressValue(uiTotalSize, uiDownloadedSize);
			int uiProgressValue = getProgressValue(uiTotalSize, uiDownloadProgress);

			progressBar.setProgress(uiDownloadedProgressValue);
			progressBar.setSecondaryProgress(uiProgressValue);

			if (Constants.DEBUG)
				Log.i("updatetest", String.format("setProgress isDiffUpdate " + "uiDownloadedProgressValue:%s(%s)," + "uiProgressValue:%s(%s)," + "uiTotalSize:%s",
						uiDownloadedSize, uiDownloadedProgressValue, uiDownloadProgress, uiProgressValue, uiTotalSize));

		} else {
			int uiProgressValue = getProgressValue(totalSize, currtentSize);
			progressBar.setProgress(0);
			progressBar.setSecondaryProgress(uiProgressValue);

			if (Constants.DEBUG)
				Log.i("updatetest", String.format("setProgress is not DiffUpdate uiDownloadedProgressValue:%s,uiProgressValue:%s ", 0, uiProgressValue));
		}

	}

	private void bindProgress(UpdatableAppInfo item, ProgressBar progressBar, TextView savedText, TextView newSize, ImageView ivFace, TextView progressText, ImageView actionIv,
			TextView actionTv, FrameLayout sizeParent) {

		
		
		View p = (View) actionIv.getParent();

		int apkStatus = item.getApkStatus();
		
		switch (apkStatus) {
		case PackageMode.UNDOWNLOAD:
		case PackageMode.INSTALLED:
			return;
		case PackageMode.UPDATABLE:
		case PackageMode.UPDATABLE_DIFF:

			p.setEnabled(true);
			if (Constants.DEBUG)
				Log.i("updatetest", "bindProgress apkStatus " + PackageMode.getStatusString(apkStatus));
			newSize.setText(Formatter.formatFileSize(context, item.getNewSize()));

			newSize.setVisibility(View.VISIBLE);

			View progressG = (View) progressBar.getParent();
			View sizeP = progressG.findViewById(R.id.manager_activity_updatable_list_item_size_layout_parent);
			if (sizeP.getVisibility() == View.GONE) {
				sizeP.setVisibility(View.VISIBLE);
			}

			return;
		default:
			break;
		}

		setDefaultStatusForDownload(progressBar, savedText, ivFace, progressText, actionIv, actionTv);
		int progressValue = 0;
		boolean diffUpdate = item.isDiffUpdate();
		if (!diffUpdate) {
			// savedText.setBackgroundResource(0);
		} else {
			savedText.setBackgroundResource(R.drawable.bg_traffic_saved_list);
		}
		switch (apkStatus) {
		case PackageMode.DOWNLOAD_PENDING:
			p.setEnabled(true);
			
			if (item.isDiffUpdate()) {
				long size = item.getNewSize();
				long patchSize = item.getPatchSize();
				savedText.setText("节省" + Formatter.formatFileSize(context, (size - patchSize)));
			} else {
				savedText.setVisibility(View.GONE);
				progressValue = getProgressValue(item.getTotalSize(), item.getCurrtentSize());
			}
			// 右边
			setProgressText(progressText, item);
			setProgress(item, progressBar);

			actionIv.setImageResource(R.drawable.btn_download_pending_selector);
			actionTv.setText(R.string.label_waiting);

			break;
		case PackageMode.DOWNLOAD_RUNNING:
			p.setEnabled(true);
			// progressValue = getProgressValue(item.getTotalSize(),
			// item.getCurrtentSize());
			// 左边
			if (item.isDiffUpdate()) {
				long size = item.getNewSize();
				long patchSize = item.getPatchSize();

				savedText.setText("节省" + Formatter.formatFileSize(context, (size - patchSize)));
			} else {
				progressValue = getProgressValue(item.getTotalSize(), item.getCurrtentSize());
				savedText.setVisibility(View.INVISIBLE);
			}
			// 右边
			setProgressText(progressText, item);
			// ProgressBar
			// progressBar.setProgress(progressValue);
			setProgress(item, progressBar);
			actionIv.setImageResource(R.drawable.btn_download_pause_selector);
			actionTv.setText(R.string.label_pause);
			break;
		case PackageMode.DOWNLOAD_FAILED:
			p.setEnabled(true);
			if (Constants.DEBUG)
				Log.i("updatetest", "bindProgress apkStatus " + PackageMode.getStatusString(apkStatus));
			newSize.setText(Formatter.formatFileSize(context, item.getNewSize()));

			newSize.setVisibility(View.VISIBLE);

			View progressG = (View) progressBar.getParent();
			View sizeP = progressG.findViewById(R.id.manager_activity_updatable_list_item_size_layout_parent);
			if (sizeP.getVisibility() == View.GONE) {
				sizeP.setVisibility(View.VISIBLE);
			}

			ViewGroup progressBarP = (ViewGroup) progressBar.getParent();
			View versionView = progressBarP.findViewById(R.id.game_version_layout);
			versionView.setVisibility(View.VISIBLE);

			progressText.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			savedText.setVisibility(View.GONE);

			actionIv.setImageResource(R.drawable.btn_download_retry_selector);
			actionTv.setText(R.string.try_again);
			break;
		case PackageMode.UPDATABLE:
		case PackageMode.UPDATABLE_DIFF:
			break;
		case PackageMode.DOWNLOAD_PAUSED:
			// progressValue = getProgressValue(item.getTotalSize(),
			// item.getCurrtentSize());
			// 左边
			p.setEnabled(true);
			if (item.isDiffUpdate()) {
				long size = item.getNewSize();
				long patchSize = item.getPatchSize();
				savedText.setText("节省" + Formatter.formatFileSize(context, (size - patchSize)));
			} else {
				progressValue = getProgressValue(item.getTotalSize(), item.getCurrtentSize());
				savedText.setVisibility(View.INVISIBLE);
			}
			setProgressText(progressText, item);
			setProgress(item, progressBar);
			actionIv.setImageResource(R.drawable.btn_download_resume_selector);
			actionTv.setText(R.string.resume);

			break;
		case PackageMode.DOWNLOADED:
			p.setEnabled(true);
			setProgressText(progressText, item);
			setProgress(item, progressBar);
			
			setViewStateToDefault(item, progressBar, savedText, newSize, progressText);
			// 普通下载成功（后台自动安装）
			if (!diffUpdate) {
				actionIv.setImageResource(R.drawable.btn_download_install_selector);
				actionTv.setText(R.string.install);
				savedText.setVisibility(View.INVISIBLE);
			} else {
				// 增量更新下载成功
				actionIv.setImageResource(R.drawable.btn_download_install_selector);
				actionTv.setText(R.string.install);
			}

			break;
		case PackageMode.DOWNLOADED_DIFFERENT_SIGN:
			// 普通下载成功，但是签名不一致（只会在下载完成后提示，以后不会提示；前台提示卸载）
			break;
		case PackageMode.MERGED_DIFFERENT_SIGN:
			break;
		case PackageMode.MERGING:
			// 合并中
			p.setEnabled(false);

			long size = item.getNewSize();
			long patchSize = item.getPatchSize();

			setViewStateToDefault(item, progressBar, savedText, newSize, progressText);
			setProgressTextFinish(progressText, item);
			setProgress(item, progressBar);

			actionIv.setImageResource(R.drawable.icon_checking_list);
			actionTv.setText("安全检查中");
			break;
		case PackageMode.MERGE_FAILED:
			// 合并失败，重新普通更新下载(要给出提示,不能删除原来的，更新即可)
			// TODO
			p.setEnabled(true);
			setViewStateToDefault(item, progressBar, savedText, newSize, progressText);

			actionIv.setImageResource(R.drawable.btn_download_retry_selector);
			actionTv.setText(R.string.try_again);
			break;
		case PackageMode.MERGED:
			// 合并成功（后台自动安装）
			p.setEnabled(true);

			setViewStateToDefault(item, progressBar, savedText, newSize, progressText);
			// 普通下载成功（后台自动安装）
			actionIv.setImageResource(R.drawable.btn_download_install_selector);
			actionTv.setText(R.string.install);
			
			setProgressTextFinish(progressText, item);
			setProgress(item, progressBar);
			break;
		case PackageMode.CHECKING:
			p.setEnabled(false);

			size = item.getNewSize();
			patchSize = item.getPatchSize();

			setViewStateToDefault(item, progressBar, savedText, newSize, progressText);
			actionIv.setImageResource(R.drawable.icon_checking_list);
			actionTv.setText("安全检查中");
			break;
		case PackageMode.CHECKING_FINISHED:
			// 合并成功（后台自动安装）
			p.setEnabled(true);
			setViewStateToDefault(item, progressBar, savedText, newSize, progressText);

			actionIv.setImageResource(R.drawable.btn_download_install_selector);
			actionTv.setText(R.string.install);
			break;
		case PackageMode.INSTALLING:
			p.setEnabled(false);
			actionIv.setImageResource(R.drawable.installing);
			actionTv.setText(R.string.installing);
			break;
		case PackageMode.INSTALL_FAILED:
			p.setEnabled(true);
			actionIv.setImageResource(R.drawable.btn_download_install_selector);
			actionTv.setText(R.string.install);
			break;
		default:
			break;
		}

	}

	private void setViewStateToDefault(UpdatableAppInfo item, ProgressBar progressBar, TextView savedText, TextView newSize, TextView progressText) {
		View progressG;
		View sizeP;
		ViewGroup progressBarP;
		View versionView;
		newSize.setText(Formatter.formatFileSize(context, item.getNewSize()));

		newSize.setVisibility(View.VISIBLE);

		progressG = (View) progressBar.getParent();
		sizeP = progressG.findViewById(R.id.manager_activity_updatable_list_item_size_layout_parent);
		if (sizeP.getVisibility() == View.GONE) {
			sizeP.setVisibility(View.VISIBLE);
		}

		progressBarP = (ViewGroup) progressBar.getParent();
		versionView = progressBarP.findViewById(R.id.game_version_layout);
		versionView.setVisibility(View.VISIBLE);

		progressText.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		savedText.setVisibility(View.GONE);
	}

	public void updateItemView(ListView listView, String gameId) {
		int targetPos = -1;
		UpdatableAppInfo targetItem = null;
		int count = getCount();
		for (int i = 0; i < count; i++) {
			UpdatableAppInfo item = getItem(i);
			if (item.getGameId().equals(gameId)) {
				targetPos = i;
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
		int childCount = listView.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childAt = listView.getChildAt(i);
		}
		View childView = listView.getChildAt(targetPos - firstVisiblePosition);

		// childView = listView.getChildAt(targetPos - firstVisiblePosition);
		if (childView != null) {
			if (Constants.DEBUG && targetItem.getApkStatus() != PackageMode.DOWNLOAD_RUNNING) {
				Log.i("updatetest", "updateItemView status:" + PackageMode.getStatusString(targetItem.getApkStatus()));
			}

			updateItemView(childView, targetItem);
		}
	}

	public void updateItemView(View view, UpdatableAppInfo item) {
		ImageView actionIv = (ImageView) view.findViewById(R.id.manager_activity_updatable_list_item_action_iv);
		TextView actionTv = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_action_tv);

		ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.game_download_progressbar);
		TextView savedText = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_saved_size);
		TextView progressText = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_progress);
		ImageView ivFace = (ImageView) view.findViewById(R.id.ivSavedSizeFace);
		TextView newSize = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_size);
		FrameLayout sizeParent = (FrameLayout) view.findViewById(R.id.manager_activity_updatable_list_item_size_layout);
		updateItemView(item, progressBar, savedText, newSize, ivFace, progressText, actionIv, actionTv, sizeParent);

	}

	private void updateItemView(UpdatableAppInfo item, ProgressBar progressBar, TextView savedText, TextView newSize, ImageView ivFace, TextView progressText, ImageView actionIv,
			TextView actionTv, FrameLayout sizeParent) {
		View p = (View) actionIv.getParent();
		Integer position = (Integer) p.getTag();

		String downloadUrl = getItem(position).getDownloadUrl();
		long itemId = getItemId(position);
		if (!downloadUrl.equals(item.getDownloadUrl())) {
		} else {
			bindProgress(item, progressBar, savedText, newSize, ivFace, progressText, actionIv, actionTv, sizeParent);
		}

	}

	// icon and button's click callback.
	@Override
	public void onClick(View v) {
		if (onListItemClickListener == null) {
			return;
		}
		if (v instanceof ViewGroup) {
			onListItemClickListener.onItemButtonClick(v, (Integer) v.getTag());
		} else if (v instanceof ImageView) {
			onListItemClickListener.onItemIconClick(v, (Integer) v.getTag());
		}
	}

	class MyFilter extends Filter {

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {

		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			return null;
		}
	}
}
