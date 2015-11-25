package com.andconsd.adapter;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.andconsd.R;
import com.andconsd.ui.activity.PicViewer;
import com.andconsd.framework.utils.AndconsdUtils;

public class PicViewAdapter extends BaseAdapter implements OnClickListener{

	String[] files;
	int screenwidth = 0;
	int screenheight = 0;
	int position = 0;
	
	public PicViewAdapter(ArrayList<String> filess,int width ,int height){
		files = new String[filess.size()];
		for (int i = 0; i < filess.size(); i++) {
			files[i] = filess.get(i);
		}
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return files.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		this.position = position;
		PicViewerItem pvi;
		String filepath = files[position].toLowerCase();
		
		View view = LayoutInflater.from(PicViewer.appref).inflate(
				R.layout.picviewitem, null);

		pvi = new PicViewerItem();

		pvi.ivThumb = (ImageView) view.findViewById(R.id.ivThumb);
		pvi.ivPlay = (ImageView) view.findViewById(R.id.ivPlay);
		pvi.picUrl = filepath;

		view.setTag(pvi);
		convertView = view;

		if (filepath.endsWith(".mp4") || filepath.endsWith(".3gp")
				|| filepath.endsWith(".rmvb") || filepath.endsWith(".rm")
				|| filepath.endsWith(".avi") || filepath.endsWith(".flv") || filepath.endsWith(".mkv")) {
			
			pvi.isVideo = true;
			
			if(filepath.endsWith(".mp4") || filepath.endsWith(".3gp")){
				Bitmap bm = ThumbnailUtils.createVideoThumbnail(filepath,
						MediaStore.Video.Thumbnails.MINI_KIND);
				pvi.ivThumb.setImageBitmap(bm);
			}else{
				pvi.ivThumb.setImageResource(R.drawable.video);
			}
			
			pvi.ivPlay.setVisibility(View.VISIBLE);
			pvi.ivPlay.setImageResource(R.drawable.videoplay);

		} else {
			pvi.ivThumb.setImageBitmap(AndconsdUtils.getDrawable(filepath , 2));
			pvi.ivPlay.setVisibility(View.INVISIBLE);
		}
		
		if(pvi.ivPlay.getVisibility() == View.VISIBLE){
			pvi.ivPlay.setOnClickListener(this);
		}
		
		return convertView;
	}
	
	public class PicViewerItem{
		public String picUrl;
		public ImageView ivThumb;
		public ImageView ivPlay;
		public boolean isVideo;
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
	
}
