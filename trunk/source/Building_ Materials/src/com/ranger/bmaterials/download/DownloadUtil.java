package com.ranger.bmaterials.download;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.download.DownloadConfiguration.DefaultDownloadComprator;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadInputItem;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemListener;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadListener;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadReason;
import com.ranger.bmaterials.download.DownloadConfiguration.DownloadItemOutput.DownloadStatus;
import com.ranger.bmaterials.download.DownloadManager.Query;
import com.ranger.bmaterials.tools.MyLogger;

/*import com.duoku.gamesearch.statistics.DownloadStatistics;
 import com.duoku.gamesearch.tools.ProxyHttpClient;
 import com.ranger.bmaterials.R;
 import com.duoku.gamesearch.app.AppInfo;
 import com.duoku.gamesearch.download.Downloads;
 import com.duoku.gamesearch.myapp.AppItem;
 import com.duoku.gamesearch.myapp.AppItem.AppState;
 import com.duoku.gamesearch.myapp.AppManager;
 import com.duoku.gamesearch.myapp.MyAppConstants;
 import com.duoku.gamesearch.myapp.db.AppItemDao;
 import com.duoku.gamesearch.myapp.helper.IconDownloadTask;
 import com.duoku.gamesearch.tools.AppUtils;
 import com.duoku.gamesearch.tools.Constants;*/

/**
 * 下载工具类。
 */
public final class DownloadUtil {
	private static final String TAG = "DownloadUtil";

	private DownloadUtil() {
	}

	public static String encodeUrl(String url) {
		if (true) {
			return url;
		}
		StringBuffer sb = new StringBuffer();
		String split[] = url.split("/");
		sb.append(split[0]);
		for (int i = 1; i < split.length; i++) {
			try {
				split[i] = URLEncoder.encode(split[i], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sb.append("/").append(split[i]);
		}
		return sb.toString();
	}

	private static String getMimetype(String url) {
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
		return mimeString;
	}

	public static File composeDestination(Context context, String url, String destFolder, String saveName) {
		if (TextUtils.isEmpty(destFolder)) {
			destFolder = DownloadConfiguration.getInstance(context).getDefaultDestinationFolder();
		}
		if (TextUtils.isEmpty(saveName)) {
			int index = url.lastIndexOf('/') + 1;
			saveName = url.substring(index);
		}
		String ret = null;
		File file = new File(destFolder);
		if (!file.exists()) {
			file.mkdirs();
		}
		/*
		 * if(destFolder.charAt(destFolder.length()-1) == '/'){ ret =
		 * destFolder+saveName ; }else{ ret = destFolder+"/"+saveName ; }
		 */
		return new File(file, saveName);
	}

	private static int getAllowedNetworkType(Context context) {
		DownloadConfiguration config = DownloadConfiguration.getInstance(context);
		boolean mobileNetworkAllowed = config.isMobileNetworkAllowed();
		int ret = 0;
		if (mobileNetworkAllowed) {
			ret = DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI;
		} else {
			ret = DownloadManager.Request.NETWORK_WIFI;
		}
		return ret;
	}

	private static boolean isRoamingAllowede(Context context) {
		DownloadConfiguration config = DownloadConfiguration.getInstance(context);
		return config.isRoamingAllowed();

	}

	private static ContentValues formContentValues(Context context, String url, String mimetype, String destFolder, String saveName, String description, String extra) {
		ContentValues values = new ContentValues();
		values.put(Downloads.Impl.COLUMN_URI, encodeUrl(url));
		values.put(Downloads.Impl.COLUMN_NOTIFICATION_PACKAGE, context.getPackageName());
		values.put(Downloads.Impl.COLUMN_NOTIFICATION_CLASS, OpenDownloadReceiver.class.getCanonicalName());
		// values.put(Downloads.Impl.COLUMN_VISIBILITY,Downloads.Impl.VISIBILITY_VISIBLE);
		// // 下载仅通过notification提示进度，不提示下载完成或失败。
		values.put(Downloads.Impl.COLUMN_VISIBILITY, Downloads.Impl.VISIBILITY_HIDDEN); // 下载仅通过notification提示进度，不提示下载完成或失败。
		mimetype = TextUtils.isEmpty(mimetype) ? getMimetype(url) : mimetype;
		// values.put(Downloads.Impl.COLUMN_MIME_TYPE,
		// "application/vnd.android.package-archive");
		values.put(Downloads.Impl.COLUMN_MIME_TYPE, mimetype);
		values.put(Downloads.COLUMN_DESTINATION, Downloads.Impl.DESTINATION_FILE_URI);
		File destinationFile = composeDestination(context, url, destFolder, saveName);
		values.put(Downloads.COLUMN_FILE_NAME_HINT, Uri.fromFile(destinationFile).toString());
		//
		values.put(Downloads.Impl.COLUMN_DESCRIPTION, TextUtils.isEmpty(description) ? destinationFile.getName() : description); // description
		values.put(Downloads.Impl.COLUMN_NOTIFICATION_EXTRAS, context.getPackageName());
		/*
		 * values.put(Downloads.Impl.RequestHeaders.INSERT_KEY_PREFIX + "refer",
		 * "referer:http://m.baidu.com");
		 */
		values.put(Downloads.Impl.COLUMN_TITLE, TextUtils.isEmpty(description) ? destinationFile.getName() : description); // 下载前指定文件名，不带.apk
		// 这样数据库就不会自动生成文件名，显示notification的时候直接使用文件名即可

		values.put(Downloads.Impl.COLUMN_ALLOWED_NETWORK_TYPES, getAllowedNetworkType(context));
		values.put(Downloads.Impl.COLUMN_ALLOW_ROAMING, isRoamingAllowede(context));
		values.put(Downloads.Impl.COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI, true);
		values.put(Downloads.Impl.COLUMN_APP_DATA, extra);

		return values;
	}

	/**
	 * 获取下载信息
	 * 
	 * @param context
	 * @param url
	 *            文件下载url
	 * @return
	 */
	public static DownloadItemOutput getDownloadInfo(Context context, String url/*
																				 * ,
																				 * DownloadComprator
																				 * comprator
																				 */, String extra) {
		DownloadManager manager = DownloadManager.getInstance(context);
		Cursor cursor = manager.query(new Query());
		try {
			if (cursor == null || cursor.getCount() == 0) {
				return null;
			}
			DefaultDownloadComprator comprator = null;
			if (comprator == null) {
				comprator = new DefaultDownloadComprator();
			}
			DownloadItemOutput extractData = null;

			int urlColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_URI);
			int mExtraId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_EXTRA);
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

				String dburl = cursor.getString(urlColumnId);
				String dbExtra = cursor.getString(mExtraId);
				if (Constants.DEBUG) {
					Log.d("PackageHelper", "getDownloadInfo dburl:" + dburl);
				}

				if (comprator != null && comprator.isTheSame(dburl, url)) {
					if (extractData != null) {
						// throw new
						// RuntimeException("Dublicate entry for the same url:"+url);
					}
					extractData = extractData(cursor);
				} else if (dbExtra != null && extra != null && dbExtra.equals(extra)) {
					extractData = extractData(cursor);
				}
			}
			return extractData;
		} catch (Exception e) {
			return null;
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	public static DownloadItemOutput getDownloadInfo(Context context, String url/*
																				 * ,
																				 * DownloadComprator
																				 * comprator
																				 */) {
		return getDownloadInfo(context, url, null);
	}

	/**
	 * 获取下载信息
	 * 
	 * @param context
	 * @param downloadId
	 * @return
	 */
	public static DownloadItemOutput getDownloadInfo(Context context, long downloadId) {
		DownloadManager manager = DownloadManager.getInstance(context);
		Cursor cursor = manager.query(new Query().setFilterById(downloadId));

		try {
			if (cursor.getCount() == 0) {
				return null;
			}
			DownloadItemOutput extractData = null;

			cursor.moveToFirst();
			extractData = extractData(cursor);
			return extractData;
		} finally {
			 if(cursor != null) cursor.close();
		}

	}

	/*	*//**
	 * 
	 * @param context
	 * @param action
	 *            packagename
	 * @return
	 */
	/*
	 * public static DownloadItemOutput getDownloadInfo2(Context context, String
	 * extra) { DownloadManager manager = DownloadManager.getInstance(context);
	 * Cursor cursor = manager.query(new Query().setFilterByExtra(extra)); try {
	 * if (cursor.getCount() == 0) { return null; } DownloadItemOutput
	 * extractData = null; cursor.moveToFirst(); extractData =
	 * extractData(cursor); return extractData; } finally { if(cursor != null)
	 * cursor.close(); }
	 * 
	 * }
	 */

	static boolean checkPackage = false;

	/**
	 * 
	 * @param context
	 * @param url
	 * @param mimetype
	 * @param destFolder
	 * @param saveName
	 * @return
	 */
	public static synchronized boolean checkDownload(Context context, String url, String mimetype, String destFolder, String saveName, String extra) {
		// 还是要检查
		/*
		 * if(true){ File destinationFile = composeDestination(context, url,
		 * destFolder, saveName); boolean exists = destinationFile.exists();
		 * if((exists)){ destinationFile.delete(); } return true ; }
		 */
		File destinationFile = null;
		try {
			destinationFile = composeDestination(context, url, destFolder, saveName);
		} catch (Exception e) {
			return false;
		}
		if (!checkPackage) {
			extra = null;
		}
		DownloadItemOutput dbInfo = getDownloadInfo(context, url, extra);
		if (dbInfo == null) {
			boolean exists = destinationFile.exists();
			if ((exists && destinationFile.delete()) || !exists) {
				return true;
			} else {
				MyLogger logger = MyLogger.getLogger(DownloadUtil.class.getSimpleName());
				logger.e("checkDownload,File cannot delete.");
				return false;
			}
		} else {
			// ///////////////////////////////////////
			String dest = dbInfo.getDest();
			boolean delete = false;
			if (dest == null) {
				delete = true;
			} else {
				String path = Uri.parse(dest).getPath();
				File file = new File(path);
				delete = !file.exists();
			}
			if (delete) {
				if (dbInfo.getDownloadId() > 0) {
					removeDownload(context, true, dbInfo.getDownloadId());
				}
				return true;
			}
			// ///////////////////////////////////////
			return false;

		}

	}

	/**
	 * 获取所有下载信息(数据库中信息)
	 * 
	 * @param context
	 * @return
	 */
	public static List<DownloadItemOutput> getAllDownloads(Context context) {
		DownloadManager manager = DownloadManager.getInstance(context);
		Cursor cursor = manager.query(new Query());
		try {
			if (cursor == null || cursor.getCount() == 0) {
				return null;
			}
			List<DownloadItemOutput> list = new ArrayList<DownloadConfiguration.DownloadItemOutput>();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				try {
					DownloadItemOutput extractData = extractData(cursor);
					if (extractData != null) {
						list.add(extractData);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return null;
	}

	/**
	 * 开始下载,注意，已经下载的条目再次下载会报异常，所以需要捕获
	 * 
	 * @param context
	 * @param url
	 *            下载url（未经过url编码）
	 * @param mimetype
	 *            mimetype,如果为null，则根据url的得到的文件扩展名得到相应的mimetype
	 * @param destFolder
	 *            文件保存的目录（必须可访问）
	 * @param saveName
	 *            保存的文件名，如果为null，则则根据url的得到文件名
	 * @param description
	 *            文件描述（可选）
	 * @param extra
	 *            可选的额外信息
	 * 
	 * @return
	 */
	public static synchronized long download(Context context, String url, String mimetype, String destFolder, String saveName, String description, String extra) {
		if (!checkDownload(context, url, mimetype, destFolder, saveName, extra)) {
			throw new RuntimeException("Target already in database!");
		}
		ContentValues formContentValues = formContentValues(context, url, mimetype, destFolder, saveName, description, extra);
		// 启动 download provider 发起下载
		final Uri contentUri = context.getContentResolver().insert(Downloads.Impl.CONTENT_URI, formContentValues);
		long id = Long.parseLong(contentUri.getLastPathSegment());
		return id;
	}

	public static synchronized long download(Context context, String url, String mimetype, String destFolder, String saveName, String description, String extra, boolean checkDownload) {
		if (checkDownload && !checkDownload(context, url, mimetype, destFolder, saveName, extra)) {
			throw new RuntimeException("Target already in database!");
		}
		ContentValues formContentValues = formContentValues(context, url, mimetype, destFolder, saveName, description, extra);
		// 启动 download provider 发起下载
		final Uri contentUri = context.getContentResolver().insert(Downloads.Impl.CONTENT_URI, formContentValues);

		long id = 0;
		if (null != contentUri && null != contentUri.getLastPathSegment())
			id = Long.parseLong(contentUri.getLastPathSegment());

		return id;
	}

	public static synchronized void download(Context context, DownloadInputItem... items) {
		if (items.length == 0)
			return;
		ContentValues formContentValues[] = new ContentValues[items.length];
		int i = 0;
		for (DownloadInputItem item : items) {
			if (!checkDownload(context, item.getUrl(), item.getMimetype(), item.getDestFolder(), item.getSaveName(), item.getExtra())) {
				throw new RuntimeException("Target alreay in database!");
			}
			formContentValues[i] = formContentValues(context, item.getUrl(), item.getMimetype(), item.getDestFolder(), item.getSaveName(), item.getDescription(), item.getExtra());
			i++;
		}
		// 启动 download provider 发起下载
		context.getContentResolver().bulkInsert(Downloads.Impl.CONTENT_URI, formContentValues);
	}

	/**
	 * 暂停下载<br/>
	 * 1.只有pending或者running状态的downloadid才合法。<br/>
	 * 2.当ids含有非pending或者running状态的downloadid时，这些将忽略。如果所有的ids全部非法，则什么也不做。<br/>
	 * 3.当ids为null，则暂停全部pending或者running状态的任务。
	 * 
	 * @param context
	 * @param ids
	 *            需要暂停的任务id集合
	 */
	public static void pauseDownload(Context context, long... ids) {
		DownloadManager downloadManager = DownloadManager.getInstance(context);
		downloadManager.pauseDownload(context, ids);
	}

	public static int updateDownload(Context context, long id, String appData) {
		DownloadManager downloadManager = DownloadManager.getInstance(context);
		return downloadManager.updateDownload(context, id, appData);
	}

	/**
	 * 重新设置下载的网络类型(2/3G或者wifi)
	 * 
	 * @param context
	 */
	public static void resetAllowedNetworkType(Context context) {
		DownloadManager downloadManager = DownloadManager.getInstance(context);
		int allowedNetworkType = getAllowedNetworkType(context);
		downloadManager.resetAllowedNetworkType(context, allowedNetworkType);

	}

	/**
	 * 删除或者取消下载，同时删除文件
	 * 
	 * @param context
	 * @param ids
	 *            当ids为null，则删除全部
	 */
	public static int removeDownload(Context context, boolean deleteFile, long... ids) {
		DownloadManager downloadManager = DownloadManager.getInstance(context);
		// downloadManager.remove(ids);
		//
		Log.i(TAG, "RemoveDownload(id) deleteFile " + deleteFile);
		if (deleteFile) {
			return downloadManager.markRowDeleted(ids);
		} else {
			return downloadManager.remove(ids);
		}

	}

	/**
	 * 
	 * @param context
	 * @param extra
	 * @param deleteFile
	 */
	public static void removeDownload(Context context, String extra, boolean deleteFile) {
		DownloadManager downloadManager = DownloadManager.getInstance(context);
		if (deleteFile) {
			downloadManager.markRowDeleted(extra);
		} else {
			downloadManager.remove(extra);
		}
		Log.i(TAG, "RemoveDownload(extra) deleteFile " + deleteFile);
		//
		//
	}

	/**
	 * 删除下载记录（不删除文件）
	 * 
	 * @param context
	 * @param ids
	 */
	public static void removeDownloadRecord(Context context, long... ids) {
		DownloadManager downloadManager = DownloadManager.getInstance(context);
		downloadManager.remove(ids);
		Log.i(TAG, "removeDownloadRecord ");
	}

	/**
	 * /** 重新开始下载，同时删除原有文件
	 * 
	 * 1.只非pending和running状态的downloadid才合法。<br/>
	 * 2.当ids含有pending或者running状态的downloadid时，这些将忽略。如果所有的ids全部非法，则什么也不做。<br/>
	 * 3.当ids。
	 * 
	 * @param context
	 * @param ids
	 */
	public static void restartDownload2(Context context, long... ids) {
		DownloadManager downloadManager = DownloadManager.getInstance(context);
		downloadManager.restartDownload(context, ids);
	}

	public static void restartDownload(Context context, long... ids) {
		resumeDownload(context, ids);
	}

	public static int updateDownload(Context context, String oldUrl, String newUrl, String saveDest, String newAppData) {
		context.startService(new Intent(context, DownloadService.class));
		DownloadManager downloadManager = DownloadManager.getInstance(context);
		return downloadManager.updateDownload(oldUrl, newUrl, saveDest, newAppData);
	}

	public static int updateDownload(Context context, long oldId, String newUrl, String saveDest, String newAppData) {
		context.startService(new Intent(context, DownloadService.class));
		DownloadManager downloadManager = DownloadManager.getInstance(context);
		return downloadManager.updateDownload(oldId, newUrl, saveDest, newAppData);
	}

	/**
	 * 从暂停（包括手动或者由于网络原因）或者失败状态恢复下载，从原有位置继续
	 * 
	 * 1.只有非pending和running状态的downloadid才合法。<br/>
	 * 2.当ids含有pending或者running状态的downloadid时，这些将忽略。如果所有的ids全部非法，则什么也不做。<br/>
	 * 3.当ids为null，则恢复全部非pending和running状态的任务。
	 * 
	 * @param context
	 * @param ids
	 */
	public static void resumeDownload(Context context, long... ids) {
		if (context == null) {
			return;
		}
		context.startService(new Intent(context, DownloadService.class));
		DownloadManager downloadManager = DownloadManager.getInstance(context);
		downloadManager.resumeDownload(ids);
	}

	public static void resumeLastPausedDownload(Context context) {
		DownloadManager downloadManager = DownloadManager.getInstance(context);
		downloadManager.resumeLastPausedDownload();
	}

	private static boolean useMain = false;

	static class AllDownloadObserver extends ContentObserver {
		private static final String TAG = "AllDownloadObserver";
		private List<DownloadConfiguration.DownloadListener> listeners;
		private Context context;
		private Query baseQuery;
		private Handler handler;

		public AllDownloadObserver(Context context, Handler handler) {
			super(handler);
			this.context = context;
			this.baseQuery = new DownloadManager.Query().setOnlyIncludeVisibleInDownloadsUi(true).orderBy(DownloadManager.COLUMN_ID, DownloadManager.Query.ORDER_DESCENDING);
			// sAsyncHandler.getLooper().quit();
			this.handler = handler;
		}

		synchronized int listenerCount() {
			if (listeners != null) {
				return listeners.size();
			}
			return 0;
		}

		synchronized void addListener(DownloadListener listener) {
			if (listener == null) {
				return;
			}
			if (listeners == null) {
				listeners = new ArrayList<DownloadListener>();
			}
			for (DownloadListener item : listeners) {
				if (item == listener) {
					return;
				}
			}
			listeners.add(listener);
			if (com.ranger.bmaterials.app.Constants.DEBUG)
				Log.i("DownloadLog", "addListener :" + listener + " result:" + listeners);
		}

		synchronized void removeListener(DownloadListener listener) {
			if (listeners == null) {
				return;
			}
			listeners.remove(listener);
			if (com.ranger.bmaterials.app.Constants.DEBUG)
				Log.i("DownloadLog", "removeListener :" + listener + " result:" + listeners);
		}

		synchronized void removeAllListener() {
			if (listeners == null) {
				return;
			}
			listeners.clear();
		}

		synchronized void notifyChanged(final List<DownloadItemOutput> items) {
			if (listeners == null || listeners.size() == 0) {
				if (com.ranger.bmaterials.app.Constants.DEBUG)
					Log.i("DownloadLog", "notifyChanged listeners is empty");
				return;
			}
			if (Looper.getMainLooper() == null) {
				// 退出
				return;
			}
			if (useMain) {
				Handler h = new Handler(Looper.getMainLooper());
				h.post(new Runnable() {

					@Override
					public void run() {
						Iterator<DownloadListener> iterator = listeners.iterator();
						while (iterator.hasNext()) {
							DownloadListener DownloadListener = iterator.next();
							DownloadListener.onDownloadProcessing(items);
							if (com.ranger.bmaterials.app.Constants.DEBUG)
								Log.i("DownloadLog", "notifyChanged listener:" + DownloadListener);
						}
					}
				});
			} else {
				Iterator<DownloadListener> iterator = listeners.iterator();
				while (iterator.hasNext()) {
					DownloadListener DownloadListener = iterator.next();
					DownloadListener.onDownloadProcessing(items);
					if (com.ranger.bmaterials.app.Constants.DEBUG)
						Log.i("DownloadLog", "notifyChanged listener:" + DownloadListener);
				}
			}

		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			MyLogger logger = MyLogger.getLogger(DownloadUtil.class.getSimpleName());
			logger.i("AllDownloadObserver receive database changed!>>" + Thread.currentThread().getName());
			if (com.ranger.bmaterials.app.Constants.DEBUG)
				Log.i("DownloadLog", "onChange ");
			if (listenerCount() <= 0) {
				logger.v("AllDownloadObserver receive database changed,but no listener.");
				return;
			}

			/*
			 * Cursor mCursor =
			 * context.getContentResolver().query(ContentUris.withAppendedId(
			 * Downloads.Impl.CONTENT_URI, downloadId),null,null,null,null);
			 */

			Cursor mCursor = DownloadManager.getInstance(context).query(baseQuery);
			int count = 0;

			try {
				if (mCursor == null /* || (count = mCursor.getCount()) <= 0 */) {
					logger.v("DownloadItemObserver receive database changed,but cursor is null or size of dataset is empty.");
					return;
				}
				List<DownloadItemOutput> items = new ArrayList<DownloadConfiguration.DownloadItemOutput>();
				for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
					try {
						DownloadItemOutput item = extractData(mCursor);
						if (item != null)
							items.add(item);
						logger.e("DownloadItemObserver receive database changed! status:" + item.getDownloadId() + " " + item.getStatus() + " reason:" + item.getReason());
					} catch (Exception e) {
					}
				}
				// android.webkit.DownloadListener
				// java.util.ConcurrentModificationException
				// at
				// java.util.ArrayList$ArrayListIterator.next(ArrayList.java:569)
				notifyChanged(items);
				/*
				 * Iterator<DownloadListener> iterator = listeners.iterator();
				 * while (iterator.hasNext()) { DownloadListener
				 * DownloadListener = iterator.next();
				 * DownloadListener.onDownloadProcessing(items); }
				 */

			} finally {
				if (/* mCursor != null && */!mCursor.isClosed()) {
					mCursor.close();
				}
			}
		}

		void quitLooper() {
			Looper looper = handler.getLooper();
			Looper myLooper = Looper.myLooper();
			// myLooper's hashCode:1079064024,
			// handler looper's hashCode:1079323840,
			// main looper'hashCode:1079064024
			// (DownloadUtil$AllDownloadObserver:463)
			try {
				MyLogger logger = MyLogger.getLogger(this.getClass().getSimpleName());
				// logger.v( "myLooper's hashCode:"+myLooper.hashCode()
				// +",handler looper's hashCode:"+looper.hashCode()+",main looper'hashCode:"+Looper.getMainLooper().hashCode());
				if (looper != null && looper != Looper.getMainLooper()) {
					looper.quit();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static DownloadItemOutput extractData(Cursor mCursor) {

		try {
			int mIdColumnUrl = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_URI);
			int mIdColumnDest = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI);
			int mIdColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
			int mTitleColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE);
			int mStatusColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
			int mReasonColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);
			int mTotalBytesColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
			int mCurrentBytesColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
			int mMediaTypeColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
			int mDateColumnId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP);

			int mOringinalId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_ORIGINAL_STATUS);

			int mExtraId = mCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_EXTRA);

			String url = mCursor.getString(mIdColumnUrl);
			String dest = mCursor.getString(mIdColumnDest);
			long downloadId = mCursor.getLong(mIdColumnId);
			String title = mCursor.getString(mTitleColumnId);
			long totalBytes = mCursor.getLong(mTotalBytesColumnId);
			long currentBytes = mCursor.getLong(mCurrentBytesColumnId);
			int status = mCursor.getInt(mStatusColumnId);
			String mediaType = mCursor.getString(mMediaTypeColumnId);
			int reason = mCursor.getInt(mReasonColumnId);
			long date = mCursor.getLong(mDateColumnId);
			int originalStatus = mCursor.getInt(mOringinalId);
			String extra = mCursor.getString(mExtraId);

			// String statusString = getStatusStringId(status, reason);
			// String dateString = getDateString(date);

			DownloadItemOutput downloadItemOutput = new DownloadItemOutput();
			downloadItemOutput.setUrl(url);
			downloadItemOutput.setDest(Uri.parse(dest).toString());
			downloadItemOutput.setDownloadId(downloadId);
			downloadItemOutput.setTitle(title);
			downloadItemOutput.setMimeType(mediaType);
			downloadItemOutput.setTotalBytes(totalBytes);
			downloadItemOutput.setCurrentBytes(currentBytes);

			DownloadStatus finalStatus = getFinalStatus(status);
			MyLogger logger = MyLogger.getLogger(DownloadUtil.class.getSimpleName());
			logger.i(downloadId + " status:" + status + " finalStatus " + finalStatus);
			downloadItemOutput.setStatus(finalStatus);
			if (status == DownloadStatus.STATUS_FAILED.getStatusCode() || status == DownloadStatus.STATUS_PAUSED.getStatusCode()) {
				downloadItemOutput.setReason(getFinalReason(finalStatus, reason, originalStatus));
			}
			downloadItemOutput.setDate(date);
			downloadItemOutput.setOriginalStatusCode(originalStatus);

			downloadItemOutput.setAppData(extra);

			return downloadItemOutput;
		} catch (Exception e) {
			Log.e(TAG, "extractData error", e);
			return null;
		}

	}

	static class DownloadItemObserver extends ContentObserver {

		private List<DownloadConfiguration.DownloadItemListener> listeners;
		private Context context;
		private long downloadId;
		private Handler handler;
		private static String TAG = "DownloadItemObserver";
		private DownloadManager downloadManager;
		DownloadManager.Query baseQuery;

		public DownloadItemObserver(Context context, long downloadid, Handler handler) {
			super(handler);
			this.handler = handler;
			init(context, downloadid);

		}

		/*
		 * @Override public void onChange(boolean selfChange, Uri uri) {
		 * super.onChange(selfChange, uri); }
		 */

		void quitLooper() {

			Looper looper = handler.getLooper();
			Looper myLooper = Looper.myLooper();
			if (looper != null && looper != Looper.getMainLooper()) {
				looper.quit();
			}
		}

		synchronized void init(Context context, long downloadId) {
			this.context = context;
			this.downloadId = downloadId;

			this.downloadManager = DownloadManager.getInstance(context);
			this.baseQuery = new DownloadManager.Query().setOnlyIncludeVisibleInDownloadsUi(true).setFilterById(downloadId);
		}

		synchronized void addListener(DownloadItemListener listener) {
			if (listener == null) {
				return;
			}
			if (listeners == null) {
				listeners = new ArrayList<DownloadConfiguration.DownloadItemListener>();
			}
			listeners.add(listener);
		}

		synchronized void removeListener(DownloadItemListener listener) {
			if (listeners != null) {
				listeners.remove(listener);
			}
		}

		synchronized void removeAllListener() {
			if (listeners != null) {
				listeners.clear();
				listeners = null;
			}
		}

		synchronized int listenerCount() {
			if (listeners != null) {
				return listeners.size();
			}
			return 0;
		}

		synchronized void notifyChanged(final DownloadItemOutput downloadItemOutput) {
			if (listeners == null || listeners.size() == 0) {
				return;
			}
			if (Looper.getMainLooper() == null) {
				// 退出
				return;
			}
			if (useMain) {
				Handler h = new Handler(Looper.getMainLooper());
				h.post(new Runnable() {

					@Override
					public void run() {
						Iterator<DownloadItemListener> iterator = listeners.iterator();
						while (iterator.hasNext()) {
							DownloadItemListener listener = iterator.next();
							listener.onDownloadProcessing(downloadItemOutput);
						}

					}
				});
			} else {
				Iterator<DownloadItemListener> iterator = listeners.iterator();
				while (iterator.hasNext()) {
					DownloadItemListener listener = iterator.next();
					listener.onDownloadProcessing(downloadItemOutput);
				}
			}

			try {
				if (downloadItemOutput != null && downloadItemOutput.getStatus() == DownloadStatus.STATUS_SUCCESSFUL) {
					removeAllListener();
					removeAllDownloadItemListener(context, downloadId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private String getStatusStringId(int status, int reason) {
			switch (status) {
			case DownloadManager.STATUS_FAILED:
				// return R.string.download_error;
				return "download_error";

			case DownloadManager.STATUS_SUCCESSFUL:
				// return R.string.download_success;
				return "download_error";

			case DownloadManager.STATUS_PENDING:
			case DownloadManager.STATUS_RUNNING:
				// return R.string.download_running;
				return "download_running";

			case DownloadManager.STATUS_PAUSED:
				if (reason == DownloadManager.PAUSED_QUEUED_FOR_WIFI) {
					// return R.string.download_queued;
					return "download_queued";
				} else {
					// return R.string.download_paused;
					return "download_paused";
				}
			}
			throw new IllegalStateException("Unknown status: " + status);
		}

		private String getDateString(long dateLong) {
			Date date = new Date(dateLong);

			if (date.before(getStartOfToday())) {
				DateFormat mDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
				return mDateFormat.format(date);
			} else {
				DateFormat mTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
				return mTimeFormat.format(date);
			}
		}

		private Date getStartOfToday() {
			Calendar today = new GregorianCalendar();
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			return today.getTime();
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			MyLogger logger = MyLogger.getLogger(DownloadUtil.class.getSimpleName());
			logger.i("DownloadItemObserver receive database changed!>>" + Thread.currentThread().getName());
			if (listenerCount() <= 0) {
				logger.v("DownloadItemObserver receive database changed,but no listener.");
				return;
			}

			Cursor mCursor = downloadManager.query(baseQuery);

			try {
				int count = 0;
				if (mCursor == null /* || (count = mCursor.getCount()) <= 0 */) {
					logger.v("DownloadItemObserver receive database changed,but cursor is null or size of dataset is empty.");
					return;
				}

				logger.i("DownloadItemObserver receive database changed! cursor row count:" + count);
				// 删除的时候会触发，但是没有数据
				if (!mCursor.moveToNext()) {
					return;
				}
				DownloadItemOutput downloadItemOutput = null;
				try {
					downloadItemOutput = extractData(mCursor);
				} catch (Exception e) {
					return;
				}

				int status = downloadItemOutput.getStatus().getStatusCode();
				int reason = (downloadItemOutput.getReason() != null) ? downloadItemOutput.getReason().getReasonCode() : 0;
				if (com.ranger.bmaterials.app.Constants.DEBUG)
					Log.i("DownloadItemObserver",
							"DownloadItemObserver receive database changed! status:" + downloadItemOutput.getStatus() + " reason:" + reason + " " + downloadItemOutput.getDownloadId());
				notifyChanged(downloadItemOutput);
				/*
				 * Iterator<DownloadItemListener> iterator =
				 * listeners.iterator(); while (iterator.hasNext()) {
				 * DownloadItemListener listener = iterator.next();
				 * listener.onDownloadProcessing(downloadItemOutput); if (status
				 * == DownloadManager.STATUS_PENDING) {
				 * listener.onDownloadPending(downloadItemOutput); } else if
				 * (status == DownloadManager.STATUS_FAILED || status ==
				 * DownloadManager.STATUS_SUCCESSFUL) {
				 * listener.onDownloadCompleted(downloadItemOutput); } else
				 * if(status == DownloadManager.STATUS_RUNNING){
				 * listener.onDownloadRunning(downloadItemOutput); } else
				 * if(status == DownloadManager.STATUS_PAUSED){
				 * listener.onDownloadPaused(downloadItemOutput); }else { throw
				 * new IllegalStateException("status error:"+status
				 * +" reason:"+reason); }
				 * 
				 * }
				 */
			} finally {
				if (!mCursor.isClosed()) {
					mCursor.close();
				}
			}

		}

	}

	private static Map<Long, DownloadItemObserver> itemObservers = new ConcurrentHashMap<Long, DownloadItemObserver>();

	@Deprecated
	public static void addDownloadItemListener(Context context, long downloadId, DownloadItemListener listener) {
		ContentResolver contentResolver = context.getContentResolver();
		// new ContentObserver(new Handler(Looper.getMainLooper())) {
		// };
		if (com.ranger.bmaterials.app.Constants.DEBUG)
			Log.i("wangliang", "addDownloadItemListener before synchronized," + Thread.currentThread().getName() + ":" + System.currentTimeMillis() / 1000);
		synchronized (DownloadUtil.class) {
			DownloadItemObserver contentObserver = itemObservers.get(downloadId);
			if (contentObserver == null) {
				final HandlerThread thread = new HandlerThread("registerDownloadListener");
				thread.setPriority(4);
				thread.start();

				Looper looper = thread.getLooper(); // Looper{40b90550}
				Looper mainLooper = Looper.getMainLooper();// Looper{40511dd8}

				if (com.ranger.bmaterials.app.Constants.DEBUG)
					Log.i("wangliang", "addDownloadItemListener after synchronized " + Thread.currentThread().getName() + ":" + System.currentTimeMillis() / 1000);
				Handler sAsyncHandler = new Handler(looper);
				DownloadItemObserver observer = new DownloadItemObserver(context, downloadId, sAsyncHandler);
				itemObservers.put(downloadId, observer);
				observer.addListener(listener);
				if (com.ranger.bmaterials.app.Constants.DEBUG)
					Log.i("wangliang", "addDownloadItemListener before registerContentObserver " + Thread.currentThread().getName() + ":" + System.currentTimeMillis() / 1000);
				contentResolver.registerContentObserver(ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI, downloadId), false, observer);
				if (com.ranger.bmaterials.app.Constants.DEBUG)
					Log.i("wangliang", "addDownloadItemListener after registerContentObserver " + Thread.currentThread().getName() + ":" + System.currentTimeMillis() / 1000);
			} else {
				contentObserver.addListener(listener);
			}
		}
	}

	volatile static AllDownloadObserver allDownloadObserver;
	static Object lock = new Object();

	/**
	 * 添加listener，监听所有下载条目的变化
	 * 
	 * @param context
	 * @param listener
	 */
	@Deprecated
	public static void addAllDownloadsListener(Context context, DownloadListener listener) {
		synchronized (lock) {
			if (allDownloadObserver == null) {
				final HandlerThread thread = new HandlerThread("addAllDownloadsListener");
				thread.setPriority(4);
				thread.start();
				Handler sAsyncHandler = new Handler(thread.getLooper());
				AllDownloadObserver observer = new AllDownloadObserver(context, sAsyncHandler);
				observer.addListener(listener);
				context.getContentResolver().registerContentObserver(Downloads.Impl.CONTENT_URI, true, observer);
				allDownloadObserver = observer;
			} else {
				allDownloadObserver.addListener(listener);
			}
		}

	}

	/**
	 * 移除所有的监听全部下载条目的监听器
	 * 
	 * @param context
	 */
	@Deprecated
	public static void removeAllDownloadsListener(Context context) {
		synchronized (lock) {
			if (allDownloadObserver != null) {
				allDownloadObserver.removeAllListener();
				context.getContentResolver().unregisterContentObserver(allDownloadObserver);
				allDownloadObserver.quitLooper();
				allDownloadObserver = null;
			}
		}
	}

	/**
	 * 移除一个监听全部下载条目的监听器
	 * 
	 * @param context
	 * @param listener
	 */
	@Deprecated
	public static void removeDownloadsListener(Context context, DownloadListener listener) {
		synchronized (lock) {
			if (allDownloadObserver != null) {
				allDownloadObserver.removeListener(listener);
				int listenerCount = allDownloadObserver.listenerCount();
				if (listenerCount <= 0) {
					allDownloadObserver.quitLooper();
					context.getContentResolver().unregisterContentObserver(allDownloadObserver);
					allDownloadObserver = null;
				}

			}
		}
	}

	/**
	 * 移除一个下载条目的某个监听器
	 * 
	 * @param context
	 * @param downloadId
	 *            被监听的下载条目的id
	 * @param listener
	 *            将被移除的监听器
	 */
	@Deprecated
	public static void removeDownloadItemListener(Context context, long downloadId, DownloadItemListener listener) {

		ContentResolver contentResolver = context.getContentResolver();
		synchronized (DownloadUtil.class) {
			DownloadItemObserver contentObserver = itemObservers.get(downloadId);
			if (contentObserver != null) {
				contentObserver.removeListener(listener);
				int listenerCount = contentObserver.listenerCount();
				if (listenerCount == 0) {
					contentResolver.unregisterContentObserver(contentObserver);
					itemObservers.remove(downloadId);
					contentObserver.quitLooper();
				}
			}
		}

	}

	/**
	 * 移除一个下载条目的所有监听器
	 * 
	 * @param context
	 * @param downloadId
	 *            被监听的下载条目的id
	 */
	@Deprecated
	public static void removeAllDownloadItemListener(Context context, long downloadId) {

		ContentResolver contentResolver = context.getContentResolver();
		synchronized (DownloadUtil.class) {
			DownloadItemObserver contentObserver = itemObservers.get(downloadId);
			if (contentObserver != null) {
				contentObserver.removeAllListener();
				contentResolver.unregisterContentObserver(contentObserver);
				itemObservers.remove(downloadId);
				contentObserver.quitLooper();

			}
		}

	}

	/**
	 * 
	 * @param status
	 *            查看DownloadManager的STAUTS_*的状态码
	 * @return
	 */
	private static DownloadStatus getFinalStatus(int status) {
		return DownloadConfiguration.DownloadItemOutput.DownloadStatus.getStatus(status);
	}

	/**
	 * getFinalReason
	 * 
	 * @param status
	 *            查看DownloadManager的STAUTS_*的状态码,
	 *            并且只能为STATUS_PAUSED或者STATUS_FAILED
	 * @param reason
	 *            查看DownloadManager的PAUSED_*和ERROR_*的状态码
	 * @return
	 */
	private static DownloadReason getFinalReason(DownloadStatus status1, int reason, int originalStatus) {

		if (status1 == DownloadStatus.STATUS_FAILED) {
			int status = status1.getStatusCode();
			if (originalStatus == 404) {
				return DownloadReason.ERROR_HTTP_CANNOT_RUSUME;
			}
			if ((400 <= status && status < Downloads.Impl.MIN_ARTIFICIAL_ERROR_STATUS) // SUPPRESS
																						// CHECKSTYLE
					|| (500 <= status && status < 600)) { // SUPPRESS CHECKSTYLE
				// HTTP status code
				return DownloadReason.ERROR_HTTP_UNKNOWN;
			}

			switch (reason) {
			case DownloadManager.ERROR_FILE_ERROR:
				return DownloadReason.ERROR_FILE_ERROR;

			case DownloadManager.ERROR_CANNOT_RESUME:
				return DownloadReason.ERROR_HTTP_CANNOT_RUSUME;

			case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
			case DownloadManager.ERROR_HTTP_DATA_ERROR:
			case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
				return DownloadReason.ERROR_HTTP_ERROR;

			case DownloadManager.ERROR_INSUFFICIENT_SPACE:
				return DownloadReason.ERROR_INSUFFICIENT_SPACE;

			case DownloadManager.ERROR_DEVICE_NOT_FOUND:
				return DownloadReason.ERROR_DEVICE_NOT_FOUND;

			case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
				return DownloadReason.ERROR_FILE_ALREADY_EXISTS;

			default:
				return DownloadReason.ERROR_UNKNOWN;
			}
		} else if (status1 == DownloadStatus.STATUS_PAUSED) {
			return DownloadReason.getReason(reason);
		}
		return null;

	}

}
