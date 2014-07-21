package com.ranger.bmaterials.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;

public class ImageViewForList extends ImageView {

	public ImageViewForList(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ImageViewForList(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void displayImage(String url) {
		displayImage(url,ImageLoaderHelper.getCustomOption(R.drawable.game_icon_list_default));
	}

	public void displayImage(boolean cache, String url) {
		displayImage(cache, url,ImageLoaderHelper.getCustomOption(cache, R.drawable.game_icon_list_default));
	}
	public void displayImage(String url, DisplayImageOptions options) {
		if (getTag() == null || !getTag().equals(url)) {
			ImageLoaderHelper.displayImage(url, this, options);
		}
		setTag(url);
	}
	
	public void displayImage(boolean cache, String url, DisplayImageOptions options) {
		if (getTag() == null || !getTag().equals(url)) {
			ImageLoaderHelper.displayImage(url, this, options);
		}
		setTag(url);
	}

	public void displayBannerImage(String url, DisplayImageOptions options) {
		if (getTag() == null || !getTag().equals(url)) {
			ImageLoaderHelper.displayImage(url, this, options);
		}
		setTag(url);
	}
}
