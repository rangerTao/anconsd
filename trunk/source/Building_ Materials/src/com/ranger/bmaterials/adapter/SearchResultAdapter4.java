package com.ranger.bmaterials.adapter;

import java.util.concurrent.CopyOnWriteArrayList;

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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager.GameStatus;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadReason;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;

public class SearchResultAdapter4 extends AbstractListAdapter<SearchItem> {
    
	private static final String TAG = null;

	public SearchResultAdapter4(Context context) {
		super(context);
		this.data = new CopyOnWriteArrayList<SearchItem>();
	}
	static class Holder {
		ImageView icon;
		TextView title;
		RatingBar rating;
		TextView downloadTimes;
		TextView gameSize;
		Button button ;
		ImageView comingIv ;
		TextView downloadProgress;
		
		ProgressBar progressBar ;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		Holder holder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.search_result_list_item2,
					parent, false);
			holder = new Holder();
			holder.title = (TextView) view
					.findViewById(R.id.game_name);
			holder.icon = (ImageView) view.findViewById(R.id.game_icon);
			holder.rating = (RatingBar) view.findViewById(R.id.game_rating);
			holder.downloadTimes = (TextView) view.findViewById(R.id.game_download_times);
			holder.gameSize  = (TextView) view.findViewById(R.id.game_size);
			holder.button  = (Button) view.findViewById(R.id.search_item_button);
			holder.comingIv  = (ImageView) view.findViewById(R.id.search_item_comingsoon_iv);
			holder.downloadProgress  = (TextView) view.findViewById(R.id.game_download_progress);
			holder.progressBar = (ProgressBar) view.findViewById(R.id.game_download_progressbar);
			
			holder.button.setOnClickListener(this);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (Holder) view.getTag();
			
		}
		holder.button.setTag(position);
		bindView(view,position, holder);
		
		return view;
	}
	
	
	public void updateItemView(ListView listView) {
		int firstVisiblePosition = listView.getFirstVisiblePosition();
		int lastVisiblePosition = listView.getLastVisiblePosition();
		int childCount = listView.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = listView.getChildAt(i);
			SearchItem item = getItem(i+firstVisiblePosition);
			updateItemView(childView, item);
		}
	}
	
	public void updateItemView(ListView listView,String gameId) {
				
		int targetPos = -1 ;
		SearchItem targetItem = null ;
		int count = getCount();
		for (int i = 0; i < count; i++) {
			SearchItem item = getItem(i);
			if(item.getGameId().equals(gameId)){
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
		//因为有header的存在
		if((targetPos - firstVisiblePosition ) < 0){
			//Log.v("SearchResultAdapter","updateItemView targetPos("+targetPos+") is invisiable for "+gameId+" firstVisiblePosition:"+firstVisiblePosition);
			return ;
		}
		int childCount = listView.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childAt = listView.getChildAt(i);
		}
		View childView = listView.getChildAt(targetPos - firstVisiblePosition);
		
		//Log.v("SearchResultAdapter","updateItemView header:"+listView.getHeaderViewsCount() +" footer:"+listView.getFooterViewsCount());
		
		//childView = listView.getChildAt(targetPos - firstVisiblePosition);
		if(childView != null){
			updateItemView(childView, targetItem);
		}
		
		
		/*if(childView instanceof FrameLayout){
			childView = listView.getChildAt(targetPos - firstVisiblePosition+1);
			updateItemView(childView, targetItem);
		}else{
			updateItemView(childView, targetItem);
		}*/
		
	}
	
	
	
	public void updateItemView(View view, SearchItem item) {
		ProgressBar progressBar = (ProgressBar) view
				.findViewById(R.id.game_download_progressbar);
		RatingBar rating = (RatingBar) view
				.findViewById(R.id.game_rating);
		
		TextView sizeText = (TextView) view.findViewById(R.id.game_size);
		TextView progressText = (TextView) view.findViewById(R.id.game_download_progress);
		
		TextView percentText = (TextView) view.findViewById(R.id.game_download_times);
		Button button = (Button) view.findViewById(R.id.search_item_button);
		
		/*if(progressBar.getVisibility() != View.VISIBLE){
			progressBar.setVisibility(View.VISIBLE);
		}*/
		
		
		updateItemView(item, progressText, percentText, sizeText, progressBar, rating, button);
	}
	
	
	private void updateItemView(SearchItem item, TextView progressText,
			TextView percentText, TextView sizeText,ProgressBar progressBar,RatingBar rating, Button button) {
		Integer position = (Integer) button.getTag();
		String downloadUrl = getItem(position).getDownloadUrl();
		long itemId = getItemId(position);
		if (!downloadUrl.equals(item.getDownloadUrl())) {
			// throw new RuntimeException("Error!");出错
			// Log.e(tag, msg)
		} else {
			bindProgress(item, progressText, percentText, sizeText, progressBar, rating, button);
		}

	}
	
	
	
	private void setDefaultForDownload(ProgressBar progressBar,RatingBar rating,TextView percentText,TextView sizeText,
			TextView progressText){
		if(progressBar.getVisibility() != View.VISIBLE){
			progressBar.setVisibility(View.VISIBLE);
		}
		if(rating.getVisibility() != View.GONE){
			rating.setVisibility(View.GONE);
		}
		if(percentText.getVisibility() != View.VISIBLE){
			percentText.setVisibility(View.VISIBLE);
		}
		if(sizeText.getVisibility() != View.GONE){
			sizeText.setVisibility(View.GONE);
		}
		if(progressText.getVisibility() != View.VISIBLE){
			progressText.setVisibility(View.VISIBLE);
		}
		
	}
	String formatString = "%s %d%%";
	String formatString2 = "%d%%";
	
	private void bindProgress(SearchItem item, TextView progressText,
			TextView percentText,TextView sizeText, ProgressBar progressBar, RatingBar rating,Button button) {
		button.setEnabled(true);
		boolean diffUpdate = item.isDiffDownload();
		int progressValue =  0 ;
		int apkStatus = item.getApkStatus();
		switch (apkStatus) {
			case PackageMode.UNDOWNLOAD:
				//可以不用设置(因为这个页面不是一直存在的)
				/*progressBar.setVisibility(View.GONE);
				progressText.setVisibility(View.GONE);
				button.setText(R.string.download);
				percentText.setText(R.string.installed);
				rating.setRating(item.getStar());
				//timesText.setText(StringUtil.formatTimes(item.getDownloadTimes()));
				String sizeTextString = Formatter.formatFileSize(context, item.getPackageSize());
				sizeText.setText(sizeTextString);*/
				
				break ;
			case PackageMode.INSTALLED:
				progressBar.setVisibility(View.GONE);
				progressText.setVisibility(View.GONE);
				button.setText(R.string.open);
				percentText.setText(R.string.installed);
				
				break;
			case PackageMode.UPDATABLE:
				//TODO
				
				break;
			case PackageMode.UPDATABLE_DIFF:
				//TODO
				break ;
			case PackageMode.DOWNLOAD_PENDING:
				setDefaultForDownload(progressBar, rating, percentText, sizeText, progressText);
				
				button.setText(R.string.label_pause);
				progressValue = getProgressValue(item.getTotalBytes(), item.getCurrentBytes());
				//左边
				percentText.setText(String.format(formatString, context.getString(R.string.waitting),progressValue));
				//右边
				setProgressText(progressText,item);
				//ProgressBar
				progressBar.setProgress(progressValue);
				
				break ;
			case PackageMode.DOWNLOAD_RUNNING:
				setDefaultForDownload(progressBar, rating, percentText, sizeText, progressText);
				
				button.setText(R.string.label_pause);
				progressValue = getProgressValue(item.getTotalBytes(), item.getCurrentBytes());
				percentText.setText(String.format(formatString2, progressValue));
				setProgressText(progressText,item);
				progressBar.setProgress(progressValue);
				break;
			case PackageMode.DOWNLOAD_FAILED:
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				//rating.setVisibility(View.VISIBLE);
				
				button.setText(R.string.try_again);
				percentText.setText(R.string.download_failed_and_try);
				break;
			case PackageMode.DOWNLOAD_PAUSED:
				setDefaultForDownload(progressBar, rating, percentText, sizeText, progressText);
				
				button.setText(R.string.resume);
				progressValue = getProgressValue(item.getTotalBytes(), item.getCurrentBytes());
				setProgressText(progressText,item);
				percentText.setText(String.format(formatString, context.getString(R.string.paused),progressValue));
				progressBar.setProgress(progressValue);
				break;
			case PackageMode.DOWNLOADED:
				//普通下载成功，并且签名一致（后台自动安装）
				if(diffUpdate){
					Log.e(TAG, "error");
				}
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				
				button.setText(R.string.install);
				percentText.setText(R.string.download_successful_and_install);
				
				break;
			case PackageMode.DOWNLOADED_DIFFERENT_SIGN:
				if(diffUpdate){
					Log.e(TAG, "error");
				}
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				
				button.setText(R.string.install);
				percentText.setText(R.string.download_successful_and_install);
				
				break;
			case PackageMode.MERGING:
				//合并中
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				
				button.setEnabled(false);
				button.setText(R.string.label_pause);
				percentText.setText(R.string.label_checking_diff_update);
				break;
			case PackageMode.MERGE_FAILED:
				//合并失败，重新普通更新下载(要给出提示,不能删除原来的，更新即可)
				//TODO
				/*button.setEnabled(false);
				button.setText(R.string.label_pause);
				percentText.setText(R.string.download_successful_and_merging);
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);*/
				break;
			case PackageMode.MERGED:
				//合并成功，并且签名一致（后台自动安装）
				if(!diffUpdate){
					Log.e(TAG, "error");
				}
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				
				button.setText(R.string.install);
				percentText.setText(R.string.download_successful_and_install);
				
				break;
			case PackageMode.MERGED_DIFFERENT_SIGN:
				//合并成功，但是签名不一致（只会在下载完成并且合成后提示，以后不会提示；前台提示卸载）
				//TODO
				if(!diffUpdate){
					Log.e(TAG, "error");
				}
				
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				
				button.setText(R.string.install);
				percentText.setText(R.string.download_successful_and_install);
				
				//TODO
				//PackageHelper.removeOldAuto((Activity)context, item.getPackageName());
				break;
			case PackageMode.INSTALLING:
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				
				button.setEnabled(false);
				button.setText(context.getString(R.string.installing));
				percentText.setText(R.string.hint_installing);
				
				
				break;
			case PackageMode.INSTALL_FAILED:
				progressText.setVisibility(View.GONE);
				progressBar.setVisibility(View.GONE);
				
				button.setText(R.string.install);
				percentText.setText(R.string.install_failed);
				
				break ;
			default:
				break;
		}
		
			
		
	}

	ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.title_color_yellow));
	
	private void setProgressText(TextView textView ,SearchItem item){
		
		String c = Formatter.formatShortFileSize(context, item.getCurrentBytes());
		String t = Formatter.formatShortFileSize(context, item.getTotalBytes() > 0?item.getTotalBytes():item.getPackageSize());
		
		int start = 0;
		int end = c.length() ;
		SpannableString spannableString = new SpannableString(c+"/"+t);
		spannableString.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);  
		textView.setText(spannableString);
	}

	
	private void setDefaultViewStatus(TextView titleText,RatingBar ratingBar,ProgressBar progressBar,TextView timesText,TextView sizeText
			,TextView progressText,Button button ,SearchItem item){
		
		if(ratingBar.getVisibility() != View.VISIBLE){
			ratingBar.setVisibility(View.VISIBLE);
		}
		if(progressBar.getVisibility() != View.GONE){
			progressBar.setVisibility(View.GONE);
		}
		if(timesText.getVisibility() != View.VISIBLE){
			timesText.setVisibility(View.VISIBLE);
		}
		if(sizeText.getVisibility() != View.VISIBLE){
			sizeText.setVisibility(View.VISIBLE);
		}
		if(progressText.getVisibility() != View.GONE){
			progressText.setVisibility(View.GONE);
		}
		if(!button.isEnabled()){
			button.setEnabled(true);
		}
		button.setText(R.string.download);
		
		ratingBar.setRating(item.getStar());
		
		titleText.setText(item.getGameName());
		timesText.setText(StringUtil.formatTimes(item.getDownloadTimes()));
		String sizeTextString = Formatter.formatFileSize(context, item.getPackageSize());
		sizeText.setText(sizeTextString);
		
		
	}
	private void bindView(View view,int position, Holder holder) {
		SearchItem item = getItem(position);
		ImageLoaderHelper.displayImage(item.getIconUrl(), holder.icon);
		/**
		 * 默认的情形
		 */
		setDefaultViewStatus(holder.title, holder.rating, holder.progressBar, holder.downloadTimes, 
				holder.gameSize, holder.downloadProgress, holder.button, item);
		
		
		boolean pendingOnLine = item.isPendingOnLine();
		/**
		 * 即将上线的情形
		 */
		if(pendingOnLine){
			holder.button.setVisibility(View.GONE);
			holder.comingIv.setVisibility(View.VISIBLE);
			return ;
		}
		
		RatingBar rating = holder.rating ;
		ProgressBar progressBar = holder.progressBar ;
		TextView percentText = holder.downloadTimes ;
		TextView sizeText = holder.gameSize ;
		TextView progressText = holder.downloadProgress ;
		Button button = holder.button ;
		
		bindProgress(item, progressText, percentText, sizeText, progressBar, rating, button);
	}
	
	
	public int getProgressValue(long total, long current) {
		if (total <= 0)
			return 0;
		return (int) (100L * current / total);
	}
	
	@Override
	public void onClick(View v) {
		if(onListItemClickListener == null){
			return ;
		}
		onListItemClickListener.onItemButtonClick(v, (Integer) v.getTag());
	}
}
