package com.andconsd;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.andconsd.utils.Constants;
import com.baidu.frontia.FrontiaApplication;
import com.baidu.mobads.AdView;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class AndApplication extends Application {

	private static AndApplication instance = null; 
	public DisplayImageOptions options;
	
	private static final WeakMemoryCache gameTingImageMemoryCache = new WeakMemoryCache();
	
	public String cache_dir = Constants.ROOT_DIR;
	
	public static synchronized AndApplication getAppInstance(){
		return instance;
	}
	
	public WeakMemoryCache getImageMemoryCache() {
		return gameTingImageMemoryCache;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		instance = this;
		initImageLoader(getApplicationContext());
		
		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(100)).build();
		
//		AdView.setAppSid(getApplicationContext(), "debug");
//		AdView.setAppSec(getApplicationContext(), "debug");
		
		// init the push service
		FrontiaApplication.initFrontiaApplication(this);
	}

	public static void initImageLoader(Context context) {
		// method.
		Builder builder = new ImageLoaderConfiguration.Builder(AndApplication.getAppInstance()).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory().memoryCache(AndApplication.getAppInstance().getImageMemoryCache())
				.tasksProcessingOrder(QueueProcessingType.FIFO);

		File cacheDir = createImageCacheDir();
		if (cacheDir != null) {
			builder.discCache(new TotalSizeLimitedDiscCache(cacheDir, Constants.DISK_CACHE_SIZE));
		}
		ImageLoaderConfiguration config = builder.build();
		ImageLoader.getInstance().init(config);
	}
	

	private static File createImageCacheDir() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File cacheDir = new File(Constants.IMAGE_CACHE);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			return cacheDir;
		}
		return null;
	}
}
