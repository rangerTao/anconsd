package com.andconsd.adapter;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.andconsd.R;
import com.andconsd.ui.Androsd;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageThumbAdapter extends BaseAdapter {

	ArrayList<String> files;
	GridView gv;
	DisplayImageOptions options;

	public ImageThumbAdapter(ArrayList<String> als, GridView gridView, DisplayImageOptions op) {
		this.files = als;
		gv = gridView;
		options = op;

		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(0)).build();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return files.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return files.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		String file = files.get(position).toLowerCase();

		ThumbItem ti;
		if (convertView == null) {
			View view = LayoutInflater.from(Androsd.appref).inflate(R.layout.griditem, null);
			ti = new ThumbItem();

			ti.iv = (ImageView) view.findViewById(R.id.gridImageView);
			ti.ivPlay = (ImageView) view.findViewById(R.id.ivPlayButton);

			view.setTag(ti);
			convertView = view;
		} else {
			ti = (ThumbItem) convertView.getTag();
		}

		if (file.endsWith(".mp4") || file.endsWith(".3gp") || file.endsWith(".rmvb") || file.endsWith(".rm") || file.endsWith(".avi") || file.endsWith(".flv")
				|| file.endsWith(".mkv")) {
			ti.ivPlay.setImageResource(R.drawable.videoplay);
			ti.ivPlay.setVisibility(View.VISIBLE);
			ti.iv.setImageResource(R.drawable.video);
		} else {
			ti.ivPlay.setVisibility(View.INVISIBLE);

			ImageLoader.getInstance().displayImage("file:///" + file, ti.iv, options);
		}

		return convertView;
	}

	class ThumbItem {
		ImageView iv;
		ImageView ivPlay;
	}

}