package com.andconsd.adapter;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.andconsd.R;
import com.andconsd.ui.PicViewer;
import com.andconsd.utils.Constants;
import com.andconsd.widget.photoview.PhotoView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PicPagerAdapter extends PagerAdapter implements OnClickListener {

	String[] files;
	int screenwidth = 0;
	int screenheight = 0;
	int position = 0;
	DisplayImageOptions option;
	ProgressBar pbLoading;

	public PicPagerAdapter(ArrayList<String> filess, int width, int height, DisplayImageOptions op, ProgressBar pb) {
		files = new String[filess.size()];
		for (int i = 0; i < filess.size(); i++) {
			files[i] = filess.get(i);
		}
		option = op;
		pbLoading = pb;
	}

	@Override
	public int getCount() {
		return Constants.files.size();
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}
	
	

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {

		this.position = position;
		PicViewerItem pvi = new PicViewerItem();
		String filepath = Constants.files.get(position).toLowerCase();

		View contentView = LayoutInflater.from(PicViewer.appref).inflate(R.layout.picviewitem, view, false);

		pvi.ivThumb = (PhotoView) contentView.findViewById(R.id.ivThumb);
		pvi.ivPlay = (ImageView) contentView.findViewById(R.id.ivPlay);
		pvi.picUrl = filepath;

		contentView.setTag(pvi);

		if (filepath.endsWith(".mp4") || filepath.endsWith(".3gp") || filepath.endsWith(".rmvb") || filepath.endsWith(".rm") || filepath.endsWith(".avi")
				|| filepath.endsWith(".flv") || filepath.endsWith(".mkv")) {

			pvi.isVideo = true;

			if (filepath.endsWith(".mp4") || filepath.endsWith(".3gp")) {
				Bitmap bm = ThumbnailUtils.createVideoThumbnail(filepath, MediaStore.Video.Thumbnails.MINI_KIND);
				pvi.ivThumb.setImageBitmap(bm);
			} else {
				pvi.ivThumb.setImageResource(R.drawable.video);
			}

			pvi.ivPlay.setVisibility(View.VISIBLE);
			pvi.ivPlay.setImageResource(R.drawable.videoplay);

		} else {
			
			ImageLoader.getInstance().displayImage("file:///" + filepath,pvi.ivThumb, option, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					pbLoading.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					pbLoading.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					pbLoading.setVisibility(View.GONE);
				}
			});
			pvi.ivPlay.setVisibility(View.INVISIBLE);
		}

		if (pvi.ivPlay.getVisibility() == View.VISIBLE) {
			pvi.ivPlay.setOnClickListener(this);
		}else{
			pvi.ivThumb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					PicViewer.appref.showHideController();
				}
			});
		}

		((ViewPager) view).addView(contentView, 0);

		return contentView;
	}

	@Override
	public void onClick(View v) {
		PicViewer.appref.handler.post(new Runnable() {

			@Override
			public void run() {
				PicViewer.appref.playVideo(position);
			}
		});
	}

	public class PicViewerItem {
		public String picUrl;
		public PhotoView ivThumb;
		public ImageView ivPlay;
		public boolean isVideo;
	}

}
