package com.andconsd.receiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.andconsd.AndApplication;
import com.andconsd.MineProfile;
import com.andconsd.R;
import com.andconsd.framework.bitmap.ImageLoaderHelper;
import com.andconsd.framework.utils.AppUtil;
import com.andconsd.framework.utils.Constants;
import com.andconsd.framework.utils.MyLogger;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class PushServiceReceiver extends FrontiaPushMessageReceiver implements ImageLoadingListener {

	private static final String TAG = "PushServiceReceiver";
	private MyLogger logger = MyLogger.getLogger(TAG);

	public static final int NOTIFICATION_ID = 10101;

	public static final String PUSH_NOTIFICATION = "pushnotification";
	public static final String PUSH_NOTIFY_ID = "pushnotifyid";
	public static final String PUSH_TYPE = "pushtype";
	public static final String PUSH_MESSAGE = "pushmessage";
	public static final String NOTIFY_ID = "notifyid";
	public static final String PUSH_RECEIVE_TIME = "push_time";

	public static final int PUSH_TYPE_STARTPAGE = 0;
	// 跳转至活动详情
	public static final int PUSH_TYPE_DETAIL = 1;
	// 跳转至活动
	public static final int PUSH_TYPE_ACTIVITY = 2;
	// 系统广播
	public static final int PUSH_TYPE_BROADCAST = 3;
	// 商城
	public static final int PUSH_TYPE_SHOPPINGMALL = 4;

	public static final int VERSION_UPPER = 1;
	public static final int VERSION_LOWWER = 2;
	public static final int VERSION_EQUAL = 3;

	private static int notifyIndex = 0;

	private Intent in;
	private String ticker;
	private String title;
	private String content;
	private String iconUrl;
	private int notifyid;

	private int getLocalVersionCode() throws NameNotFoundException {
		PackageManager pm = AndApplication.getAppInstance().getPackageManager();
		PackageInfo packInfo = pm.getPackageInfo(AndApplication.getAppInstance().getPackageName(), 0);
		int version_cur = packInfo.versionCode;

		return version_cur;
	}

	private void showNotification(int icon, String tickertext, String title, String content, PendingIntent intent, int notifyID) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(AndApplication.getAppInstance());
		builder.setSmallIcon(R.drawable.icon);
		builder.setTicker(tickertext);
		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setContentIntent(intent);
		builder.setWhen(System.currentTimeMillis());

		if (AppUtil.iskkgameForeground()) {
			builder.setDefaults(Notification.DEFAULT_SOUND);
		} else {
			builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		}

		NotificationManager nm = (NotificationManager) AndApplication.getAppInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notifyID, builder.build());
	}

	private void showNotification(Bitmap icon, String tickertext, String title, String content, PendingIntent intent, int notifyID) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(AndApplication.getAppInstance());

		builder.setLargeIcon(icon);
		// builder.setSmallIcon(R.drawable.ic_notifier);
		builder.setTicker(tickertext);
		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setContentIntent(intent);
		builder.setWhen(System.currentTimeMillis());

		if (AppUtil.iskkgameForeground()) {
			builder.setDefaults(Notification.DEFAULT_SOUND);
		} else {
			builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		}

		NotificationManager nm = (NotificationManager) AndApplication.getAppInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notifyID, builder.build());
	}

	private void showNotificationWithIcon(Bitmap icon, String tickertext, String title, String content, PendingIntent intent, int notifyID) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(AndApplication.getAppInstance());

		RemoteViews rv = new RemoteViews(AndApplication.getAppInstance().getPackageName(), R.layout.notification_with_custom_icon);
		rv.setTextViewText(R.id.notification_title, title);
		rv.setTextViewText(R.id.notification_text, content);
		rv.setImageViewBitmap(R.id.notification_image, icon);

		builder.setLargeIcon(icon);
		builder.setSmallIcon(R.drawable.icon);
		builder.setTicker(tickertext);
		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setContentIntent(intent);
		builder.setWhen(System.currentTimeMillis());
		builder.setContent(rv);

		if (AppUtil.iskkgameForeground()) {
			builder.setDefaults(Notification.DEFAULT_SOUND);
		} else {
			builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		}

		NotificationManager nm = (NotificationManager) AndApplication.getAppInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notifyID, builder.build());

	}

	private void showNotification(String iconUrl) {

		ImageLoaderHelper.config();

		ImageLoader.getInstance().loadImage(iconUrl, this);
	}

	private void showTypedNotification(final PendingIntent intent, final String msg, final int notify_id, int tag, final String title,
			final String imageUrl, final String content) {

		try {
			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... params) {
					return loadImageFromUrl(imageUrl);
				}

				@Override
				protected void onPostExecute(String result) {
					try {
						int icon1 = R.drawable.icon;

						// 创建Notifcation
						Notification notification1 = new Notification(icon1, msg, notify_id);

						// 设定是否振动
						notification1.flags |= Notification.FLAG_AUTO_CANCEL;
						// 创建RemoteViews用在Notification中
						RemoteViews contentView = new RemoteViews(AndApplication.getAppInstance().getPackageName(),
								R.layout.notification_with_custom_icon);
						contentView.setTextViewText(R.id.notification_title, title);
						contentView.setTextViewText(R.id.notification_text, content);
						if (result != null) {
							Bitmap bit = BitmapFactory.decodeFile(result);
							if (bit != null) {
								contentView.setImageViewBitmap(R.id.notification_image, bit);
							} else {
								contentView.setImageViewResource(R.id.notification_image, R.drawable.icon);
							}

						} else {
							contentView.setImageViewResource(R.id.notification_image, R.drawable.icon);
						}
						notification1.contentView = contentView;
						notification1.contentIntent = intent;

						// 显示这个notification
						NotificationManager mNotificationManager = (NotificationManager) AndApplication.getAppInstance().getSystemService(
								Context.NOTIFICATION_SERVICE);
						mNotificationManager.notify(notify_id, notification1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				private String loadImageFromUrl(String imageUrl) {
					String path = Environment.getExternalStorageDirectory() + Constants.IMAGE_CACHE;
					try {
						File rootfile = new File(path);
						if (!rootfile.exists()) {
							rootfile.mkdirs();
						}
						String file = path + "pushtemp.jpg";
						URL url = new URL(imageUrl);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(5000);
						conn.setRequestMethod("GET");
						conn.setDoInput(true);
						if (conn.getResponseCode() == 200) {

							InputStream is = conn.getInputStream();
							FileOutputStream fos = new FileOutputStream(file);
							byte[] buffer = new byte[1024];
							int len = 0;
							while ((len = is.read(buffer)) != -1) {
								fos.write(buffer, 0, len);
							}
							is.close();
							fos.close();
							return file;
						} else {
							return null;
						}
						// 返回一个URI对象
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			}.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLoadingCancelled(String arg0, View arg1) {
		PendingIntent pt = PendingIntent.getActivity(AndApplication.getAppInstance(), notifyid, in, 0);
		showNotification(R.drawable.icon, ticker, title, content, pt, notifyid);
	}

	@Override
	public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		PendingIntent pt = PendingIntent.getActivity(AndApplication.getAppInstance(), notifyid, in, 0);
		showNotificationWithIcon(arg2, ticker, title, content, pt, notifyid);
	}

	@Override
	public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		PendingIntent pt = PendingIntent.getActivity(AndApplication.getAppInstance(), notifyid, in, 0);
		showNotification(R.drawable.icon, ticker, title, content, pt, notifyid);
	}

	@Override
	public void onLoadingStarted(String arg0, View arg1) {

	}

	@Override
	public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {

		if (errorCode == 0 && appid.equals(Constants.APP_ID)) {
			try {
				MineProfile.getInstance().setPush_userid(userId);
				MineProfile.getInstance().setPush_channelid(channelId);
				MineProfile.getInstance().Save();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {

	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2, String arg3) {

	}

	@Override
	public void onMessage(Context arg0, String message, String customContentString) {

		logger.d("push message: " + message + "*******");
		JSONObject object = null;
		try {
			object = new JSONObject(message);

			int version_flag = object.getInt("version_flag");
			String version = object.getString("version");
			int versionFromServer = 0;
			try {
				versionFromServer = Integer.parseInt(version);
			} catch (Exception e) {
				e.printStackTrace();
			}
			int pushType = object.getInt("pushtype");

			int version_cur = getLocalVersionCode();

			switch (version_flag) {
			case VERSION_EQUAL:
				if (versionFromServer != version_cur)
					return;
				break;
			case VERSION_UPPER:
				if (versionFromServer <= version_cur)
					return;
				break;
			case VERSION_LOWWER:
				if (versionFromServer >= version_cur)
					return;
				break;

			default:
				break;
			}

			switch (pushType) {
			case PUSH_TYPE_STARTPAGE:// 游戏推荐--跳转至产品首页
			case PUSH_TYPE_DETAIL:// 游戏推荐--跳转至详情页
			case PUSH_TYPE_ACTIVITY:// 游戏推荐--跳转至列表页
			case PUSH_TYPE_SHOPPINGMALL:

				break;
			case PUSH_TYPE_BROADCAST:
				Gson gson = new Gson();

				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.d(e.toString());
		}

		object = null;

	}

	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {

	}

	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {

	}
}
