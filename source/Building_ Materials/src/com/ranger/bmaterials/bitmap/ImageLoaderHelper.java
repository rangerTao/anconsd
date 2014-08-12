package com.ranger.bmaterials.bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Message;
import android.widget.ImageView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.tools.DeviceUtil;
import com.ranger.bmaterials.tools.PhoneHelper;
import com.ranger.bmaterials.tools.UIUtil;

public class ImageLoaderHelper {
	private ImageLoaderHelper() {
	}

    private static boolean STORE_ON_SDCARD = true;

    static{
    }

	/**
	 * Get a displayimageoption with a default icon setted.
	 * 
	 * @param default_icon
	 * @return
	 */
	public static DisplayImageOptions getCustomOption(int default_icon) {

		return new DisplayImageOptions.Builder()
				.cacheInMemory(false)
				.cacheOnDisc(STORE_ON_SDCARD)
				.showImageOnLoading(default_icon)
				.showImageForEmptyUri(default_icon)
				.showImageOnFail(default_icon)
				// EXACTLY :图像将完全按比例缩小的目标大小
				// EXACTLY_STRETCHED:图片会缩放到目标完全大小
				// IN_SAMPLE_INT:图像将被二次采样的整数倍
				// IN_SAMPLE_POWER_OF_2:图片将降低2倍，直到下一减少步骤，使图像更小的目标大小
				// NONE:图片不会调整
				.imageScaleType(ImageScaleType.EXACTLY)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)// 减少内存占用
																				// 每像素站2byte
				.build();

	}

	private static DisplayImageOptions options = getCustomOption(R.drawable.loading);

    private static DisplayImageOptions optionUserHead = getCustomOption(R.drawable.bm_user_header_unlogin);

	private static boolean isNoPicture() {
		MineProfile profile = MineProfile.getInstance();
		return profile.isNoPicture();
	}

	public static void displayImage(String imageUrl, ImageView imageView,
			DisplayImageOptions options) {
		displayImage(imageUrl, imageView, options, null);
	}

	public static void displayImage(String imageUrl, ImageView imageView) {
		displayImage(imageUrl, imageView, options);

	}

	public static void displayImage(String imageUrl, ImageView imageView,
			DisplayImageOptions options, ImageLoadingListener listener) {
		if (imageUrl == null || imageView == null)
			return;
		config();
		ImageLoader imageLoader = ImageLoader.getInstance();
		if (options == null) {
			options = ImageLoaderHelper.options;
		}
		if (isNoPicture()) {
			imageLoader.denyNetworkDownloads(true);
		} else {
			imageLoader.denyNetworkDownloads(false);
		}
        try{
            imageLoader.displayImage(imageUrl, imageView, options, listener);
        }catch (OutOfMemoryError e){
            e.printStackTrace();
        }

	}

	public static void config() {
		ImageLoader loader = ImageLoader.getInstance();
		boolean inited = loader.isInited();
		if (!inited) {
			Builder builder = new ImageLoaderConfiguration.Builder(
					GameTingApplication.getAppInstance())
					.threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(2)
					.denyCacheImageMultipleSizesInMemory()
					.memoryCache(new LargestLimitedMemoryCache(256 * 1024))// 删除最大的bitmap
					.tasksProcessingOrder(QueueProcessingType.FIFO);

			File cacheDir = createImageCacheDir();
			if (cacheDir != null) {
				builder.discCache(new UnlimitedDiscCache(cacheDir));// UnlimitedDiscCache
																	// 比其他方式快30%以上.
			}
			ImageLoaderConfiguration config = builder.build();
			loader.init(config);
		}

	}

	public static void onDestroy() {
		try {
			ImageLoader.getInstance().clearMemoryCache();
			ImageLoader.getInstance().destroy();
		} catch (Exception e) {
		}

	}

	public static void clearCache() {
		try {
			ImageLoader.getInstance().clearMemoryCache();
		} catch (Exception e) {
		}
	}

	private static File createImageCacheDir() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdacrd = Environment.getExternalStorageDirectory();
			File cacheDir = new File(sdacrd.getAbsolutePath() + "/"
					+ Constants.IMAGE_CACHE);
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			return cacheDir;
		}
		return null;
	}
}
