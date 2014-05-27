package com.andconsd.bitmap;

import java.io.ByteArrayOutputStream;
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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Message;
import android.widget.ImageView;

import com.andconsd.AndApplication;
import com.andconsd.R;
import com.andconsd.utils.Constants;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageLoaderHelper {

	private static DisplayImageOptions options = getCustomOption(R.drawable.picture_loading);
	public static DisplayImageOptions optionsForLargImage = getCustomOption(R.drawable.picture_loading);
	public static DisplayImageOptions optionsWithoutMemoryCache = getCustomOption(false, R.drawable.picture_loading);

	private ImageLoaderHelper() {
	}

	/**
	 * Get a displayimageoption with a default icon setted.
	 * 
	 * @param default_icon
	 * @return
	 */
	public static DisplayImageOptions getCustomOption(int default_icon) {

		return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).showImageOnLoading(default_icon)
				.showImageForEmptyUri(default_icon).showImageOnFail(default_icon).imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)// 减少内存占用
																				// 每像素站2byte
				.build();

	}

	public static DisplayImageOptions getCustomOption(boolean cacheInMemory, int default_icon) {

		return new DisplayImageOptions.Builder().cacheInMemory(cacheInMemory).cacheOnDisc(true).showImageOnLoading(default_icon)
				.showImageForEmptyUri(default_icon).showImageOnFail(default_icon).imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)// 减少内存占用
				.build();

	}

	public static DisplayImageOptions getOptionForDownloadManage(int default_icon) {

		return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).showImageOnLoading(default_icon)
				.showImageForEmptyUri(default_icon).showImageOnFail(default_icon).imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)// 减少内存占用
																				// 每像素站2byte
				.build();
	}

	public static DisplayImageOptions getMemoryCustomOption(int default_icon) {

		return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).showImageOnLoading(default_icon)
				.showImageForEmptyUri(default_icon).showImageOnFail(default_icon).imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)// 减少内存占用
																				// 每像素站2byte
				.build();

	}

	public static DisplayImageOptions getNoneStubOption(boolean cacheMemory) {

		return new DisplayImageOptions.Builder().cacheInMemory(cacheMemory).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).considerExifParams(true)// 减少内存占用
				.build();

	}

	public static DisplayImageOptions getDefaultImageOptions(boolean cacheOnDisc) {
		return new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisc(cacheOnDisc).bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).considerExifParams(true).build();
	}

	// 圆角图片
	public static DisplayImageOptions getCustomRoundeOption(int default_icon, boolean cacheInMemory, int roundepx) {
		// 默认不要内存缓存
		return new DisplayImageOptions.Builder().cacheInMemory(cacheInMemory).cacheOnDisc(true).showImageOnLoading(default_icon)
				.showImageForEmptyUri(default_icon).showImageOnFail(default_icon).imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
				// 减少内存占用 每像素站2byte
				.displayer(new RoundedBitmapDisplayer(roundepx)).build();

	}

	private static boolean isNoPicture() {
		return false;
	}

	public static void config() {
		ImageLoader loader = ImageLoader.getInstance();
		boolean inited = loader.isInited();
		if (!inited) {
			Builder builder = new ImageLoaderConfiguration.Builder(AndApplication.getAppInstance()).threadPriority(Thread.NORM_PRIORITY - 2)
					.denyCacheImageMultipleSizesInMemory().memoryCache(AndApplication.getAppInstance().getImageMemoryCache())
					.tasksProcessingOrder(QueueProcessingType.LIFO).threadPoolSize(5);

			File cacheDir = createImageCacheDir();
			if (cacheDir != null) {
				builder.discCache(new TotalSizeLimitedDiscCache(cacheDir, Constants.DISK_CACHE_SIZE));
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
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File sdacrd = Environment.getExternalStorageDirectory();
			File cacheDir = new File(sdacrd.getAbsolutePath() + "/" + Constants.IMAGE_CACHE);
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
		FileInputStream fileInputSteam = null;
		try {
			File photoFile = new File(filepath);
			// Log.e("xxxx", "filePath.exists()="+photoFile.getAbsolutePath());
			// Log.e("xxxx", "fileForInputSteam.exists()="+photoFile.exists());
			if (photoFile.exists()) {
				fileInputSteam = new FileInputStream(photoFile);

				// bitmap = BitmapFactory.decodeStream(fileInputSteam, null,
				// options);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] b = new byte[1024];
				int len = 0;
				while ((len = fileInputSteam.read(b, 0, 1024)) != -1) {
					baos.write(b, 0, len);
					baos.flush();
				}
				byte[] bytes = baos.toByteArray();
				bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				baos.close();
				// if (bitmap == null) {
				// Log.e("xxxx", "decode bitmap result=null");
				// } else {
				// Log.e("xxxx", "decode bitmap result is not null");
				// }
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInputSteam != null) {
				try {
					fileInputSteam.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fileInputSteam = null;
			}
		}
		return bitmap;
	}

	public static Drawable decodeResource(int resid) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;

		BitmapDrawable bitmap = new BitmapDrawable(AndApplication.getAppInstance().getResources(), BitmapFactory.decodeResource(AndApplication
				.getAppInstance().getResources(), resid, options));

		return bitmap;
	}

	public static void displayImage(String imageUrl, ImageView imageView, DisplayImageOptions options) {
		displayImage(imageUrl, imageView, options, null);
	}

	public static void setImageViewResource(ImageView imageView, int resId) {
		try {
			imageView.setImageResource(resId);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
		}
	}

	public static void displayImage(String imageUrl, ImageView imageView) {
		displayImage(imageUrl, imageView, options);
	}

	public static void displayImageWithoutCache(String imageUrl, ImageView imageView) {
		displayImage(imageUrl, imageView, optionsWithoutMemoryCache);
	}

	public static void displayLargeImage(String imageUrl, ImageView imageView) {
		displayImage(imageUrl, imageView, optionsForLargImage);
	}

	public static void displayImage(String imageUrl, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
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
		try {
			imageLoader.displayImage(imageUrl, imageView, options, listener);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
		}

	}

	/**
	 * 保存头像到本地cache.该方法会在每次登陆成功后被调用。
	 * 
	 * @param urlStr
	 * @param localdir
	 * @param filename
	 */
	public static void saveImg2Local(final String urlStr, final String localdir, final String filename, final Message sucessMsg) {
		new Thread() {
			public void run() {
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
						if (sucessMsg != null) {
							sucessMsg.sendToTarget();
						}
						inputs.close();
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
		}.start();
	}
}
