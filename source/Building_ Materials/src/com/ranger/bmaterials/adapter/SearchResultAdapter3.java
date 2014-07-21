package com.ranger.bmaterials.adapter;

import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
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
import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.install.AppSilentInstaller.InstallStatus;

public class SearchResultAdapter3 extends AbstractListAdapter<SearchItem> {
    
	public SearchResultAdapter3(Context context) {
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
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.i("SnapNumberAdapter", "SnapNumberAdapter "+position);
		View view;
		Holder holder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.search_result_list_item2,
					parent, false);
			holder = new Holder();
			holder.title = (TextView) view
					.findViewById(R.id.game_name);
			holder.icon = (ImageView) view
					.findViewById(R.id.game_icon);
			holder.rating = (RatingBar) view
					.findViewById(R.id.game_rating);
			holder.downloadTimes = (TextView) view
					.findViewById(R.id.game_download_times);
			holder.gameSize  = (TextView) view
					.findViewById(R.id.game_size);
			holder.button  = (Button) view.findViewById(R.id.search_item_button);
			holder.comingIv  = (ImageView) view.findViewById(R.id.search_item_comingsoon_iv);
			holder.downloadProgress  = (TextView) view.findViewById(R.id.game_download_progress);
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
		ProgressBar bar = (ProgressBar) view
				.findViewById(R.id.game_download_progressbar);
		//TextView progress = (TextView) view.findViewById(R.id.game_size);
		TextView progress = (TextView) view.findViewById(R.id.game_download_progress);
		
		TextView percent = (TextView) view.findViewById(R.id.game_download_times);
		Button button = (Button) view.findViewById(R.id.search_item_button);
		
		if(bar.getVisibility() != View.VISIBLE){
			bar.setVisibility(View.VISIBLE);
		}
		
		updateItemView(item, progress, percent, bar, button);
	}
	
	
	private void updateItemView(SearchItem item, TextView progressText,
			TextView percentText, ProgressBar progressBar, Button button) {
		Integer position = (Integer) button.getTag();
		String downloadUrl = getItem(position).getDownloadUrl();
		long itemId = getItemId(position);
		if (!downloadUrl.equals(item.getDownloadUrl())) {
			// throw new RuntimeException("Error!");出错
			// Log.e(tag, msg)
		} else {
			bindProgress(item, progressText, percentText, progressBar, button);
		}

	}
	
	
	private void bindInstallProgress(SearchItem item, TextView progressText,
			TextView percentText, ProgressBar progressBar, Button button){
		
		DownloadStatus status = item.getDownloadStatus();
		DownloadReason reason = item.getDownloadReason();
		
		
		button.setEnabled(true);
		if(status == DownloadStatus.STATUS_SUCCESSFUL){
			InstallStatus installeStatus = item.getInstalleStatus();
			
			switch (installeStatus) {
				case INSTALLING:
					button.setText(R.string.installing);
					button.setEnabled(false);
					
					percentText.setText(R.string.hint_installing);

					item.setStatus(GameStatus.DONWLOADED);
					break;
	
				case INSTALLED:
					button.setText(R.string.open);
					button.setEnabled(true);
					
					percentText.setText(R.string.installed);
					item.setStatus(GameStatus.INSTALLED);
					break;
				case INSTALL_ERROR:
					button.setText(R.string.install);
					button.setEnabled(true);
					
					percentText.setText(R.string.install_failed);
					item.setStatus(GameStatus.DONWLOADED);
					break;
				case UNINSTALLED:
				default:
					button.setText(R.string.install);
					button.setEnabled(true);
					
					percentText.setText(R.string.download_successful_and_install);
					item.setStatus(GameStatus.DONWLOADED);
					break;
			}
		}
	}
	private void bindProgress(SearchItem item, TextView progressText,
			TextView percentText, ProgressBar progressBar, Button button) {
		DownloadStatus status = item.getDownloadStatus();
		DownloadReason reason = item.getDownloadReason();
		
		if(progressBar.getVisibility() != View.VISIBLE)progressBar.setVisibility(View.VISIBLE);
		if(percentText.getVisibility() != View.VISIBLE)percentText.setVisibility(View.VISIBLE);
		if(progressText.getVisibility() != View.VISIBLE)progressText.setVisibility(View.VISIBLE);
		
		/*if(item.getTotalSize() <= 0){
			percentText.setText("点击继续");
			progressText.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.GONE);
			button.setText("继续");
		}*/
		if(status == DownloadStatus.STATUS_PENDING) {
			button.setText(R.string.label_pause);
			
			setProgressText(progressText,item);
			percentText.setText(R.string.waitting);
			int progressValue = getProgressValue(item.getTotalBytes(), item.getCurrentBytes());
			progressBar.setProgress(progressValue);
			
		}else if( status == DownloadStatus.STATUS_RUNNING){
			button.setText(R.string.label_pause);
			
			setProgressText(progressText,item);
			
			int progressValue = getProgressValue(item.getTotalBytes(), item.getCurrentBytes());
			percentText.setText(progressValue+"%");
			progressBar.setProgress(progressValue);
			
		}else if(status == DownloadStatus.STATUS_SUCCESSFUL){
			button.setText(R.string.install);
			
			percentText.setText(R.string.download_successful_and_install);
			progressText.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.GONE);
			
			item.setStatus(GameStatus.DONWLOADED);
			
		}else if(status == DownloadStatus.STATUS_FAILED){
			button.setText(R.string.try_again);
			
			percentText.setText(R.string.download_failed_and_try);
			progressText.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.GONE);
			
			
		}else if(status == DownloadStatus.STATUS_PAUSED){
				if(reason == DownloadReason.PAUSED_BY_APP){ //手动暂停
					button.setText(R.string.resume);
					
					setProgressText(progressText,item);
					int progressValue = getProgressValue(item.getTotalBytes(), item.getCurrentBytes());
					percentText.setText("已暂停 "+progressValue+"%");
					progressBar.setProgress(progressValue);
					
				}else{
					//由于网络原因等程序暂停
					if(!DeviceUtil.isNetworkAvailable(context)){
						button.setText(R.string.resume);
						
						setProgressText(progressText,item);
						
						int progressValue = getProgressValue(item.getTotalBytes(), item.getCurrentBytes());
						//percentText.setText("请检查网络  "+progressValue+"%");
						percentText.setText("已暂停   "+progressValue+"%");
						progressBar.setProgress(progressValue);
					}else{
						button.setText(R.string.resume);
						
						setProgressText(progressText,item);
						
						int progressValue = getProgressValue(item.getTotalBytes(), item.getCurrentBytes());
						percentText.setText("已暂停 "+progressValue+"%");
						progressBar.setProgress(progressValue);
						
					}
					
					/*
					else{
						percentText.setText("正在等待  "+getProgressValue(item.getTotalSize(), item.getCurrtentSize())+"%");
						
						setProgressText(progressText,item);
						String c = Formatter.formatFileSize(context, item.getCurrtentSize());
						String t = Formatter.formatFileSize(context, item.getTotalSize());
						progressText.setText(c+"/"+t);
						
						
						progressBar.setProgress(getProgressValue(item.getTotalSize(), item.getCurrtentSize()));
						item.setStatus(DownloadStatus.STATUS_PENDING);
						button.setText("继续");
					}*/
					
				}
			
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

	private void bindView(View view,int position, Holder holder ) {
		SearchItem item = getItem(position);
		ImageLoaderHelper.displayImage(item.getIconUrl(), holder.icon);
		
		/**
		 * 没有下载的情形
		 */
		
		if(holder.rating.getVisibility() != View.VISIBLE){
			holder.rating.setVisibility(View.VISIBLE);
		}
		holder.rating.setRating(item.getStar());
		
		ProgressBar bar = (ProgressBar) view.findViewById(R.id.game_download_progressbar);
		if(bar.getVisibility() != View.GONE){
			bar.setVisibility(View.GONE);
		}
		holder.downloadProgress.setVisibility(View.GONE);
		holder.gameSize.setVisibility(View.VISIBLE);
		
		boolean enabled = holder.button.isEnabled();
		if(!enabled)holder.button.setEnabled(true);
		holder.button.setText(R.string.download);
		
		holder.title.setText(item.getGameName());
		holder.downloadTimes.setText(StringUtil.formatTimes(item.getDownloadTimes()));
		String sizeText = Formatter.formatFileSize(context, item.getPackageSize());
		holder.gameSize.setText(sizeText);
		
		boolean pendingOnLine = item.isPendingOnLine();
		/**
		 * 即将上线的情形
		 */
		if(pendingOnLine){
			holder.button.setVisibility(View.GONE);
			holder.comingIv.setVisibility(View.VISIBLE);
			holder.rating.setVisibility(View.VISIBLE);
			return ;
		}
		
		
		
		GameStatus status = item.getStatus();
		if(status == GameStatus.UNDOWNLOAD ){
			holder.button.setText(R.string.download);
		}else if(status == GameStatus.INSTALLED){
			holder.button.setText(context.getString(R.string.open));
		}else if(status == GameStatus.DOWNLOADING ){
			//holder.button.setText("查看");
		}else if(status == GameStatus.DONWLOADED){
			holder.button.setText(context.getString(R.string.install));
		}else if(status == null){
			item.setStatus( GameStatus.UNDOWNLOAD);
			holder.button.setText(R.string.download);
		}
//		MineProfile profile = MineProfile.getInstance();
		/*boolean noPicture = profile.isNoPicture();
		if(noPicture){
			holder.icon.setImageResource(R.drawable.ic_launcher);
		}else{
			
		}*/
		/**
		 * 新加的
		 */
		if(status == GameStatus.DOWNLOADING){
			
			//TextView progress = (TextView) view.findViewById(R.id.game_size);
			TextView progress = holder.downloadProgress;
			progress.setVisibility(View.VISIBLE);
			
			holder.gameSize.setVisibility(View.GONE);
			
			TextView percent = holder.downloadTimes ;//(TextView) view.findViewById(R.id.game_download_times);
			Button button = holder.button;//(Button) view.findViewById(R.id.search_item_button);
			bar.setVisibility(View.VISIBLE);
			holder.rating.setVisibility(View.GONE);
			bindProgress(item, progress, percent, bar, button);
			if(item.getInstalleStatus() != null && item.getInstalleStatus() != InstallStatus.UNINSTALLED){
				//holder.button.setText(R.string.install);
				bar.setVisibility(View.GONE);
				holder.rating.setVisibility(View.VISIBLE);
				bindInstallProgress(item, progress, percent, bar, button);
			}
				
				
		}else if(status == GameStatus.DONWLOADED){
			holder.button.setText(R.string.install);
			
			bar.setVisibility(View.GONE);
			holder.rating.setVisibility(View.VISIBLE);
			
			TextView progress = (TextView) view.findViewById(R.id.game_size);
			TextView percent = holder.downloadTimes ;//(TextView) view.findViewById(R.id.game_download_times);
			Button button = holder.button;//(Button) view.findViewById(R.id.search_item_button);
			if(item.getInstalleStatus() != null){
				bindInstallProgress(item, progress, percent, bar, button);
			}
		}
		
		//Log.i("SearchResultAdapter", "[bindView]GameStatus:"+status+",for "+item.getGameName()+" position:"+position);
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
