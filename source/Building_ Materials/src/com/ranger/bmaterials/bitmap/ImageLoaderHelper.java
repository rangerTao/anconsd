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

	public static DisplayImageOptions getCustomOption(boolean cacheInMemory,
			int default_icon) {

		return new DisplayImageOptions.Builder().cacheInMemory(cacheInMemory)
				.cacheOnDisc(STORE_ON_SDCARD).showImageOnLoading(default_icon)
				.showImageForEmptyUri(default_icon)
				.showImageOnFail(default_icon)
				.imageScaleType(ImageScaleType.EXACTLY)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
				.build();

	}

	public static DisplayImageOptions getOptionForDownloadManage(
			int default_icon) {

		return new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(STORE_ON_SDCARD).showImageOnLoading(default_icon)
				.showImageForEmptyUri(default_icon)
				.showImageOnFail(default_icon)
				.imageScaleType(ImageScaleType.EXACTLY)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
				.build();
	}

	public static DisplayImageOptions getMemoryCustomOption(int default_icon) {

		return new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(STORE_ON_SDCARD).showImageOnLoading(default_icon)
				.showImageForEmptyUri(default_icon)
				.imageScaleType(ImageScaleType.EXACTLY)
				.showImageOnFail(default_icon).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

	}

	public static DisplayImageOptions getNoneStubOption() {

		return new DisplayImageOptions.Builder().cacheInMemory(false)
				.cacheOnDisc(STORE_ON_SDCARD).bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
				.imageScaleType(ImageScaleType.EXACTLY).build();

	}

	private static DisplayImageOptions options = getCustomOption(R.drawable.game_icon_list_default);

	public static DisplayImageOptions getDefaultImageOptions(boolean cacheOnDisc) {
		return new DisplayImageOptions.Builder().cacheInMemory(false)
				.considerExifParams(true).cacheOnDisc(cacheOnDisc)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY).build();
	}

	// 圆角图片
//	public static DisplayImageOptions getCustomRoundeOption(
//			boolean cacheInMemory, int default_icon) {
//		return new DisplayImageOptions.Builder()
//				.cacheInMemory(cacheInMemory)
//				.cacheOnDisc(STORE_ON_SDCARD)
//				.showImageOnLoading(default_icon)
//				.showImageForEmptyUri(default_icon)
//				.showImageOnFail(default_icon)
//				.bitmapConfig(Bitmap.Config.RGB_565)
//				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//				.considerExifParams(true)
//				// RoundedBitmapDisplayer（int roundPixels）设置圆角图片
//				// FakeBitmapDisplayer（）这个类什么都没做
//				// FadeInBitmapDisplayer（int durationMillis）设置图片渐显的时间　　　　　　　
//				// SimpleBitmapDisplayer()正常显示一张图片　　
//				.displayer(
//						new RoundedBitmapDisplayer(UIUtil.dip2px(
//								GameTingApplication.getAppInstance(), 8.0f), 0))
//				.build();
//
//	}

	public static DisplayImageOptions getCustomRoundeOption(
			boolean cacheInMemory, int default_icon, int[] margin) {
		return new DisplayImageOptions.Builder()
				.cacheInMemory(cacheInMemory)
				.cacheOnDisc(STORE_ON_SDCARD)
				.showImageOnLoading(default_icon)
				.showImageForEmptyUri(default_icon)
				.showImageOnFail(default_icon)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.considerExifParams(true)
				.displayer(
                        new RoundedBitmapDisplayer(UIUtil.dip2px(
                                GameTingApplication.getAppInstance(), 4.0f),
                                margin[0], margin[1], margin[2], margin[3]))
				.build();

	}

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

	public static Bitmap decodeBitmap(String filepath) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		File file = new File(filepath);
		if (file.exists()) {
			if (file.length() < 30720) { // 0-30
				options.inSampleSize = 2;
			} else if (file.length() < 122880) { // 1-2m
				options.inSampleSize = 3;
			} else if (file.length() < 245760) { // 2-4m
				options.inSampleSize = 6;
			} else if (file.length() < 491520) { // 4-8m
				options.inSampleSize = 8;
			} else {
				options.inSampleSize = 16;
			}
		}
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;

		// 通过这个bitmap获取图片的宽和高
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(
					filepath)), null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static Drawable decodeResource(int resid) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;

		BitmapDrawable bitmap = new BitmapDrawable(GameTingApplication
				.getAppInstance().getResources(), BitmapFactory.decodeResource(
				GameTingApplication.getAppInstance().getResources(), resid,
				options));

		return bitmap;
	}
	
	/**
	 * 获得原图的圆形备份
	 * @param src
	 * @return
	 */
	public static Bitmap getRoundedBitmap(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        int size = (width <= height) ? width : height;
		Bitmap output = Bitmap.createBitmap(size, size,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, src.getWidth(), src.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
//		canvas.drawCircle(src.getWidth() / 2, src.getHeight() / 2,
//				src.getWidth() / 2, paint);
        canvas.drawCircle(size / 2, size / 2,
                size / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(src, rect, rect, paint);

		return output;
	}
	
	/**
	 * 保存头像到本地cache.该方法会在每次登陆成功后被调用。
	 * 
	 * @param urlStr
	 * @param localdir
	 * @param filename
	 */
	public static void saveImg2Local(final String urlStr, final String localdir, final String filename, final Message sucessMsg) {

		HttpGet httpRequest = new HttpGet(urlStr);
		HttpClient httpclient = new DefaultHttpClient();
		FileOutputStream fileUoupts = null;
		try {
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream inputs = httpEntity.getContent();

				byte[] bytes = new byte[1024];
				int len;
				File destFile = new File(localdir);
				if (!destFile.exists()) {
					destFile.mkdirs();
				}
				fileUoupts = new FileOutputStream(destFile.getPath() + "//" + filename);
				while ((len = inputs.read(bytes)) != -1) {
					fileUoupts.write(bytes, 0, len);
				}
				
				inputs.close();
				
				if (sucessMsg != null) {
					sucessMsg.sendToTarget();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileUoupts != null) {
					fileUoupts.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

}
