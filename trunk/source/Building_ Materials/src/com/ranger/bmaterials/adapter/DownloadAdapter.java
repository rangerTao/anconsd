package com.ranger.bmaterials.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.mode.DownloadAppInfo;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.ui.DownloadAppListFragment;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.view.AnimationDrawableView;
import com.ranger.bmaterials.view.ImageViewForList;
import com.ranger.bmaterials.view.StickyListHeadersAdapter;

public class DownloadAdapter extends AbstractListAdapter<DownloadAppInfo> implements StickyListHeadersAdapter {
	
	private static final String TAG = "DownloadAdapter";
	private static final boolean DEBUG = true;

	public void setIconSize(int iconSize){
	}
		
	public DownloadAdapter(Context context) {
		super(context);
	}
	
	static class AppInfoViewHolder {
        RoundCornerImageView icon;
		TextView title;
		ProgressBar progressBar;
		//80%之类
		TextView percentText;
		//3M/10M之类
		TextView progressText;

		AnimationDrawableView ivDownloadAction;
		TextView tvDownloadStatus;
		LinearLayout llDownloadActionLayout;
	}

	@Override
	public long getItemId(int position) {
		DownloadAppInfo item = getItem(position);
		return item.getDownloadId() ;
	}
	
	
	private void updateItemView(DownloadAppInfo item,TextView progressText,TextView percentText,ProgressBar progressBar,LinearLayout layout,AnimationDrawableView ivDownloadStatus,TextView tvDownloadStatus){
		long downloadId = item.getDownloadId();
		Integer position = (Integer) ivDownloadStatus.getTag();
		long itemId = getItemId(position);
		if(downloadId != itemId){
			throw new RuntimeException("Error!");
		}
		bindProgress(item, progressText, percentText, progressBar, layout,ivDownloadStatus,tvDownloadStatus);
	}

	public void updateItemView(ListView listView,long downloadId) {
		int targetPos = -1 ;
		DownloadAppInfo targetItem = null ;
		int count = getCount();
		for (int i = 0; i < count; i++) {
			DownloadAppInfo item = getItem(i);
			if(item.getDownloadId() == downloadId){
				targetPos = i ;
				targetItem = item ;
			}
		}
		//Log.v("SearchResultAdapter","updateItemView target position:"+targetPos);
		if(targetPos == -1){
			//Log.v("SearchResultAdapter","updateItemView targetPos is -1 for "+gameId);
			return ;
		}
		
		int firstVisiblePosition = listView.getFirstVisiblePosition();
		if((targetPos - firstVisiblePosition ) < 0){
			//Log.v("SearchResultAdapter","updateItemView targetPos("+targetPos+") is invisiable for "+gameId+" firstVisiblePosition:"+firstVisiblePosition);
			return ;
		}
		int childCount = listView.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childAt = listView.getChildAt(i);
		}
		View childView = listView.getChildAt(targetPos - firstVisiblePosition);
		
		if(childView != null){
			updateItemView(childView, targetItem);
		}
		
		
		
	}

	public void updateItemView(View view, DownloadAppInfo item) {
		ProgressBar progressBar = (ProgressBar) view
				.findViewById(R.id.manager_activity_download_list_item_progress);
		TextView progressText = (TextView) view
				.findViewById(R.id.manager_activity_download_list_item_text_progress);
		TextView percentText = (TextView) view
				.findViewById(R.id.manager_activity_download_list_item_text_percent);
		LinearLayout llAction = (LinearLayout) view.findViewById(R.id.download_item_action_layout);
		AnimationDrawableView ivDownlaod = (AnimationDrawableView) view.findViewById(R.id.download_item_action_iv);
		TextView tvDownload = (TextView) view.findViewById(R.id.download_item_action_tv);
		updateItemView(item, progressText, percentText, progressBar, llAction,ivDownlaod,tvDownload);
	}

	@Override
	public int getCount() {
		return super.getCount();
	}
	
	public int getPositionForId(long downloadId){
		int count = getCount();
		for (int i = 0; i < count; i++) {
			long itemId = getItemId(i);
			if(itemId == downloadId){
				return i;
			}
		}
		return -1 ;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		AppInfoViewHolder info;
		if (convertView == null) {
			//view = mInflater.inflate(R.layout.manager_activity_download_list_item,parent, false);
			view = mInflater.inflate(R.layout.manager_activity_download_list_item,parent, false);
			info = new AppInfoViewHolder();
			info.title = (TextView) view.findViewById(R.id.manager_activity_download_list_item_name);
			info.icon = (RoundCornerImageView) view.findViewById(R.id.manager_activity_download_list_item_icon);
			info.progressBar = (ProgressBar) view.findViewById(R.id.manager_activity_download_list_item_progress);
			info.percentText = (TextView) view.findViewById(R.id.manager_activity_download_list_item_text_percent);
			info.progressText = (TextView) view.findViewById(R.id.manager_activity_download_list_item_text_progress);
			info.ivDownloadAction = (AnimationDrawableView ) view.findViewById(R.id.download_item_action_iv);
			info.tvDownloadStatus = (TextView) view.findViewById(R.id.download_item_action_tv);
			info.llDownloadActionLayout = (LinearLayout) view.findViewById(R.id.download_item_action_layout);
			
			View p = (View) info.ivDownloadAction.getParent();
			p.setOnClickListener(this);
			view.setTag(info);
		} else {
			view = convertView;
			info = (AppInfoViewHolder) view.getTag();
		}

		View p = (View) info.ivDownloadAction.getParent();
		//p.setText("更新");
		p.setTag(position);
		//app icon
		info.icon.setTag(position);
		
		bindView(position, info);
		return view;
	}
	
	
	private void setDefaultViewState(TextView progressText,TextView percentText,ProgressBar progressBar,LinearLayout button,AnimationDrawableView ivDownloadStatus,TextView tvDownloadStatus){
		if(progressBar.getVisibility() != View.VISIBLE){
			progressBar.setVisibility(View.VISIBLE);
		}
		if(percentText.getVisibility() != View.VISIBLE){
			percentText.setVisibility(View.VISIBLE);
		}
		if(progressText.getVisibility() != View.VISIBLE){
			progressText.setVisibility(View.VISIBLE);
		}
		if(!ivDownloadStatus.isEnabled()){
			ivDownloadStatus.setEnabled(true);
		}
		
		if(ivDownloadStatus.getVisibility() != View.VISIBLE){
			ivDownloadStatus.setVisibility(View.VISIBLE);
		}
		
		if(tvDownloadStatus.getVisibility() != View.VISIBLE){
			tvDownloadStatus.setVisibility(View.VISIBLE);
		}
		//button.setText(R.string.label_pause);
	}
	
	String formatString = "%s ";
	String formatString2 = "%d%%";
	
	
	private void bindProgress(DownloadAppInfo item,TextView progressText,TextView percentText,ProgressBar progressBar,LinearLayout layout,AnimationDrawableView ivDownloadStatus,TextView tvDownloadStatus){
		layout.setEnabled(true);
		
		int apkStatus = item.getApkStatus();
		switch (apkStatus) {
			case PackageMode.UNDOWNLOAD:
			case PackageMode.INSTALLED:
			case PackageMode.UPDATABLE:
			case PackageMode.UPDATABLE_DIFF:
				return ;
			default:
				break;
		}
		setDefaultViewState(progressText, percentText, progressBar, layout,ivDownloadStatus,tvDownloadStatus);
		int progressValue = 0 ;
		boolean diffUpdate = item.isDiffUpdate();
		switch (apkStatus) {
			case PackageMode.DOWNLOAD_PENDING:
				//等待中
				progressValue = getProgressValue(item.getTotalSize(), item.getCurrtentSize());				
				//左边
				percentText.setText(String.format(formatString, context.getString(R.string.waitting),progressValue));
				//右边
				setProgressText(progressText,item);
				//ProgressBar
				progressBar.setProgress(progressValue);
				
				ivDownloadStatus.setImageResource(R.drawable.btn_download_pending_selector);
				tvDownloadStatus.setText(R.string.label_waiting);
				
				break;
			case PackageMode.DOWNLOAD_RUNNING:
				progressValue = getProgressValue(item.getTotalSize(), item.getCurrtentSize());
				percentText.setText(String.format(formatString2, progressValue));
				setProgressText(progressText,item);
				progressBar.setProgress(progressValue);
				
				ivDownloadStatus.setImageResource(R.drawable.btn_download_pause_selector);

				tvDownloadStatus.setText(R.string.label_pause);
				
				break;
			case PackageMode.DOWNLOAD_FAILED:
				percentText.setText(R.string.download_failed_and_try);
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				
				ivDownloadStatus.setImageResource(R.drawable.btn_download_retry_selector);
				tvDownloadStatus.setText(R.string.try_again);
				
				break ;
			case PackageMode.DOWNLOAD_PAUSED:
				progressValue = getProgressValue(item.getTotalSize(), item.getCurrtentSize());
				percentText.setText(String.format(formatString, context.getString(R.string.paused),progressValue));
				setProgressText(progressText,item);
				progressBar.setProgress(progressValue);
				
				ivDownloadStatus.setImageResource(R.drawable.btn_download_resume_selector);
				tvDownloadStatus.setText(R.string.resume);
				
				break;
			case PackageMode.DOWNLOADED:
				if(DEBUG){
					Log.d(TAG, String.format("%s downloaded,diffUpdate?%s", item.getName(),diffUpdate));
				}
				//普通下载成功（后台自动安装）
				if(!diffUpdate){
					percentText.setText(R.string.download_successful_and_install);
					progressText.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					
					ivDownloadStatus.setImageResource(R.drawable.btn_download_install_selector);
					tvDownloadStatus.setText(R.string.install);
					
				}else{
					//增量更新下载成功
					//button.setEnabled(true);
					progressText.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					percentText.setText(R.string.download_successful_and_install);
					
					ivDownloadStatus.setImageResource(R.drawable.icon_checking_list);
					tvDownloadStatus.setText(R.string.install);
				}
				
				break ;
			case PackageMode.DOWNLOADED_DIFFERENT_SIGN:
				//普通下载成功，但是签名不一致（只会在下载完成后提示，以后不会提示；前台提示卸载）
				//TODO
				//PackageHelper.removeOldAuto((Activity)context, item.getPackageName());
				break ;
			case PackageMode.MERGED_DIFFERENT_SIGN:
				//合并成功，但是签名不一致（只会在下载完成并且合成后提示，以后不会提示；前台提示卸载）
				//TODO
				//PackageHelper.removeOldAuto((Activity)context, item.getPackageName());
				break;
			case PackageMode.MERGING:
				//合并中
				layout.setEnabled(false);
				percentText.setText(R.string.download_successful_and_merging);
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				
				ivDownloadStatus.setImageResource(R.drawable.icon_checking_list);
				tvDownloadStatus.setText(R.string.label_checking_diff_update);
				
				break ;
			case PackageMode.MERGE_FAILED:
				//合并失败，重新普通更新下载(要给出提示,不能删除原来的，更新即可)
				//TODO
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				percentText.setText(R.string.checking_failed);
				
				ivDownloadStatus.setImageResource(R.drawable.btn_download_retry_selector);
				tvDownloadStatus.setText(R.string.try_again);
				
				break;
			case PackageMode.MERGED:
				//合并成功（后台自动安装）
				//button.setEnabled(false);
				percentText.setText(R.string.download_successful_and_merging);
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				
				ivDownloadStatus.setImageResource(R.drawable.btn_download_install_selector);
				tvDownloadStatus.setText(R.string.label_installing);
				break;
			case PackageMode.CHECKING:
				layout.setEnabled(false);
				//To do .Refix the UI>
				percentText.setText(R.string.download_successful_and_merging);
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				
				ivDownloadStatus.setImageResource(R.drawable.icon_checking_list);
				tvDownloadStatus.setText(R.string.label_checking_diff_update);
				
				break;
			case PackageMode.CHECKING_FINISHED:
				percentText.setText(R.string.download_successful_and_install);
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				ivDownloadStatus.setImageResource(R.drawable.btn_download_install_selector);
				tvDownloadStatus.setText(R.string.install);
				break;
				
			case PackageMode.INSTALLING:
				layout.setEnabled(false);
				percentText.setText(R.string.hint_installing);
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				ivDownloadStatus.setImageResource(R.drawable.installing);
				tvDownloadStatus.setText(R.string.installing);
				
				break;
			case PackageMode.INSTALL_FAILED:
				percentText.setText(R.string.install_failed);
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				ivDownloadStatus.setImageResource(R.drawable.btn_download_install_selector);
				tvDownloadStatus.setText(R.string.install);
				break ;
			
			default:
				break;
		}
	}
	ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.title_color_yellow));
	
	private void setProgressText(TextView textView ,DownloadAppInfo item){
		
		String c = Formatter.formatFileSize(context, item.getCurrtentSize());
		String t = Formatter.formatFileSize(context, item.getTotalSize() <= 0?item.getSize():item.getTotalSize());
		
		int start = 0;
		int end = c.length() ;
		SpannableString spannableString = new SpannableString(c+"/"+t);
		spannableString.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);  
		textView.setText(spannableString);
	}
	private void bindView(int position, AppInfoViewHolder info ) {
		DownloadAppInfo item = getItem(position);
		if (Constants.DEBUG)Log.i(DownloadAppListFragment.TAG, "bindView "+position+" "+item);
	
		info.title.setText(item.getName());
//		Drawable icon =  null;//AppUtil.loadApplicationIcon(info.icon.getContext().getPackageManager(), item.getPackageName());
//		if(icon == null){
//			ImageLoaderHelper.displayImage(item.getIconUrl(), info.icon);
//		}else {
//			info.icon.setImageDrawable(icon);
//		}

        ImageLoaderHelper.displayImage(item.getIconUrl(),info.icon,ImageLoaderHelper.optionForDownload);

		info.icon.setTag(position);
		info.ivDownloadAction.setTag(position);
		
		bindProgress(item, info.progressText, info.percentText, info.progressBar, info.llDownloadActionLayout,info.ivDownloadAction,info.tvDownloadStatus);
		
	}
	
	public int getProgressValue(long total, long current) {
		if (total <= 0)
			return 0;
		return (int) (100L * current / total);
	}
	
	/*private void setTextForView(View paramView, int paramInt,
			String paramString) {
		((TextView) paramView.findViewById(paramInt)).setText(paramString);
	}*/
	@Override
	public void onClick(View v) {
		if(onListItemClickListener == null){
			return ;
		}
		if(v instanceof ImageView){
			onListItemClickListener.onItemIconClick(v, (Integer) v.getTag());
		}else if(v instanceof Button){
			onListItemClickListener.onItemButtonClick(v, (Integer) v.getTag());
		}else if(v instanceof LinearLayout){
			onListItemClickListener.onItemButtonClick(v, (Integer) v.getTag());
		}
	}
	
	class HeaderViewHolder {
		TextView textPlain;
		TextView textRed;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.manager_activity_download_list_header, parent, false);
			holder.textPlain = (TextView) convertView.findViewById(R.id.red_notify_plain_text);
			holder.textRed = (TextView) convertView.findViewById(R.id.red_notify_red_text);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}
		
		View header = convertView.findViewById(R.id.download_header_layout);
		header.setBackgroundColor(context.getResources().getColor(R.color.listview_header_background));
		
		DownloadAppInfo item = getItem(position);
		DownloadStatus status = item.getStatus();
		int apkStatus = item.getApkStatus();
		holder.textRed.setVisibility(View.GONE);
		if(apkStatus == PackageMode.MERGED  || apkStatus == PackageMode.INSTALLING || apkStatus == PackageMode.MERGE_FAILED
				|| apkStatus == PackageMode.INSTALL_FAILED || apkStatus == PackageMode.DOWNLOADED 
				||apkStatus == PackageMode.CHECKING_FINISHED ){
//			if(apkStatus == PackageMode.MERGED || apkStatus == PackageMode.MERGE_FAILED || 
//					apkStatus == PackageMode.MERGING || apkStatus == PackageMode.INSTALLING
//					|| apkStatus == PackageMode.INSTALL_FAILED || apkStatus == PackageMode.DOWNLOADED 
//					|| apkStatus == PackageMode.CHECKING
//					||apkStatus == PackageMode.CHECKING_FINISHED ){
			int count = getSuccessfulCount();
			holder.textPlain.setText("下载完成"+"("+count+")");
			//holder.textRed.setText(String.valueOf(count));
		}else {
			int count = getSuccessfulCount();
			int count2 = getCount();
			holder.textPlain.setText("正在下载("+(count2 - count)+")");
			//int count = getCount(DownloadStatus.STATUS_RUNNING) + getCount( DownloadStatus.STATUS_PENDING);
			holder.textRed.setText(String.valueOf(count));
		}
		
		return convertView;
		
	}
	
	/**
	 * 	public static final int DOWNLOAD_PENDING = 1 << 2;
		public static final int DOWNLOAD_RUNNING = 1 << 3;
		public static final int DOWNLOAD_PAUSED = 1 << 4;
		public static final int DOWNLOAD_FAILED = 1 << 5;
	 * @return
	 */
	private int getSuccessfulCount() {
		int count = getCount();
		int ret = 0;
		for (int i = 0; i < count; i++) {
			int apkStatus = getItem(i).getApkStatus();
			if (apkStatus == PackageMode.DOWNLOAD_PENDING
					|| apkStatus == PackageMode.CHECKING
					|| apkStatus == PackageMode.DOWNLOAD_RUNNING
					|| apkStatus == PackageMode.MERGING
					|| apkStatus == PackageMode.DOWNLOAD_PAUSED
					|| apkStatus == PackageMode.DOWNLOAD_FAILED) {
				ret++;
			}
		}

		return count - ret;
	}

	/**
	 * New Version
	 */
	@Override
	public long getHeaderId(int position) {
		DownloadAppInfo item = getItem(position);
		int apkStatus = item.getApkStatus();
		long id = 0 ;
		if(apkStatus == PackageMode.MERGED  || apkStatus == PackageMode.INSTALLING || apkStatus == PackageMode.MERGE_FAILED
				|| apkStatus == PackageMode.INSTALL_FAILED || apkStatus == PackageMode.DOWNLOADED
				||apkStatus == PackageMode.CHECKING_FINISHED){
//			if(apkStatus == PackageMode.MERGED || apkStatus == PackageMode.MERGE_FAILED || 
//					apkStatus == PackageMode.MERGING || apkStatus == PackageMode.INSTALLING
//					|| apkStatus == PackageMode.INSTALL_FAILED || apkStatus == PackageMode.DOWNLOADED
//					||  apkStatus == PackageMode.CHECKING
//					||apkStatus == PackageMode.CHECKING_FINISHED){
			
			id = PackageMode.DOWNLOADED;
		}else{
			id = PackageMode.DOWNLOADED + 1 ;
		}
		return id;
	}

}
