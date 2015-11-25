package com.andconsd.framework.control;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.andconsd.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public enum BitmapManager {
	INSTANCE;

	private final Map<String, Bitmap> cache;
	private final ExecutorService pool;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	private Bitmap placeholder;

	BitmapManager() {
		cache = new HashMap<String, Bitmap>();
		pool = Executors.newFixedThreadPool(15);
	}

	public void setPlaceholder(Bitmap bmp) {
		placeholder = bmp;
	}

	public Bitmap getBitmapFromCache(String url) {
		if (cache.containsKey(url)) {
			return cache.get(url);
		}

		return null;
	}

	public void queueJob(final String url, final ImageView imageView,
			final int width, final int height) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						imageView.setImageBitmap((Bitmap) msg.obj);
					} else {
						imageView.setImageBitmap(placeholder);
					}
				}
			}
		};

		pool.submit(new Runnable() {
			@Override
			public void run() {
				final Bitmap bmp = downloadBitmap(url, width, height);
				Message message = Message.obtain();
				message.obj = bmp;

				handler.sendMessage(message);
			}
		});
	}

	public void loadBitmap(final String url, final ImageView imageView,
			final int width, final int height) {
		imageViews.put(imageView, url);
		Bitmap bitmap = getBitmapFromCache(url);

		// check in UI thread, so no concurrency issues
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.loading);
			queueJob(url, imageView, width, height);
		}
	}
	
	public void loadBitmapForThumb(final String url, final ImageView imageView,
			final int width, final int height) {
		imageViews.put(imageView, url);
		Bitmap bitmap = getBitmapFromCache(url);

		// check in UI thread, so no concurrency issues
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.loading);
			queueJobForThumb(url, imageView, width, height);
		}
	}
	
	public void queueJobForThumb(final String url, final ImageView imageView,
			final int width, final int height) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						imageView.setImageBitmap((Bitmap) msg.obj);
					} else {
						imageView.setImageBitmap(placeholder);
					}
				}
			}
		};

		pool.submit(new Runnable() {
			@Override
			public void run() {
				final Bitmap bmp = downloadBitmapForThumb(url, width, height);
				Message message = Message.obtain();
				message.obj = bmp;

				handler.sendMessage(message);
			}
		});
	}

	private Bitmap downloadBitmap(String url, int width, int height) {
		try{
			Bitmap bitmap = ThumbnailUtils.extractThumbnail(getDrawable(url),width,height);
			if (url.endsWith(".mp4") || url.endsWith(".3gp")) {
				bitmap = ThumbnailUtils.extractThumbnail(ThumbnailUtils
						.createVideoThumbnail(url,
								MediaStore.Video.Thumbnails.MINI_KIND), width,
						height);
			}
			
			cache.put(url, bitmap);
			return bitmap;
		}catch (Exception e) {
			return null;
		}

	}
	
	private Bitmap downloadBitmapForThumb(String url, int width, int height) {
		try{
			Bitmap bitmap = ThumbnailUtils.extractThumbnail(getDrawableForThumb(url),width,height);
			if (url.endsWith(".mp4") || url.endsWith(".3gp")) {
				bitmap = ThumbnailUtils.extractThumbnail(ThumbnailUtils
						.createVideoThumbnail(url,
								MediaStore.Video.Thumbnails.MINI_KIND), width,
						height);
			}
			
			cache.put(url, bitmap);
			return bitmap;
		}catch (Exception e) {
			return null;
		}

	}
	
	private Bitmap getDrawableForThumb(String path) {
		if (path == null || path.length() < 1)
			return null;
		File file = new File(path);
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();

		if (file.length() < 131072) { // 0-1m
			opts.inSampleSize = 1;
		} else if (file.length() < 262144) { // 1-2m
			opts.inSampleSize = 4;
		} else if (file.length() < 524288) { // 2-4m
			opts.inSampleSize = 8;
		} else if (file.length() < 1048576) { // 4-8m
			opts.inSampleSize = 16;
		} else {
			opts.inSampleSize = 32;
		}
		
		try {
			InputStream is = new DataInputStream(new FileInputStream(file));
			
			resizeBmp = BitmapFactory.decodeStream(is,null, opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		
		return resizeBmp;
	}
	
	
	private Bitmap getDrawable(String path) {
		if (path == null || path.length() < 1)
			return null;
		File file = new File(path);
		Log.v("TAG", "file length " + file.length());
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();

		if (file.length() < 131072) { // 0-1m
			opts.inSampleSize = 1;
		} else if (file.length() < 262144) { // 1-2m
			opts.inSampleSize = 1;
		} else if (file.length() < 524288) { // 2-4m
			opts.inSampleSize = 2;
		} else if (file.length() < 1048576) { // 4-8m
			opts.inSampleSize = 4;
		} else {
			opts.inSampleSize = 8;
		}
		
		try {
			InputStream is = new DataInputStream(new FileInputStream(file));
			
			resizeBmp = BitmapFactory.decodeStream(is,null, opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		
		return resizeBmp;
	}
}