package com.ranger.bmaterials.view;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class NetImageView extends ImageView {
	private DisplayImageOptions options = null;
	private String mUrl;

	public NetImageView(Context context) {
		super(context);
	}

	public NetImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NetImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setDisplayImageOptions(DisplayImageOptions option) {
		options = option;
	}

	public void setImageUrl(String url) {

			if (null != url && !url.equals(mUrl)) {
				mUrl = url;
				ImageLoaderHelper.displayImage(url, this, options);
			} else if (null == url) {
				mUrl = null;
				setImageResource(null != options ? options.getDelayBeforeLoading() : 0);
			}

	}

}
