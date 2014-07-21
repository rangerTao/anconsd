package com.ranger.bmaterials.broadcast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.baidu.android.pushservice.PushConstants;
import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.tools.MyLogger;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.ui.MainHallActivity;

public class PushServiceReceiver extends BroadcastReceiver implements ImageLoadingListener {

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
	public static final int PUSH_TYPE_GAMEDETAIL = 1;
	public static final int PUSH_TYPE_GAMELIST = 2;
	public static final int PUSH_TYPE_MSGNUM = 3;
	public static final int PUSH_TYPE_WEB = 4;
	public static final int PUSH_TYPE_COMPETE = 5;
    public static final int PUSH_TYPE_ACTIVITY = 6;
	private static int notifyIndex = 0;

	private Intent in;
	private String ticker;
	private String title;
	private String content;
	private String iconUrl;
	private int notifyid;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			String message = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);

			logger.d("push message: " + message + "*******");
			JSONObject object = null;
			try {
				object = new JSONObject(message);

				int pushType = object.getInt("pushtype");

				switch (pushType) {
				case PUSH_TYPE_STARTPAGE:// 游戏推荐--跳转至产品首页
				case PUSH_TYPE_GAMEDETAIL:// 游戏推荐--跳转至详情页
				case PUSH_TYPE_GAMELIST:// 游戏推荐--跳转至列表页
				case PUSH_TYPE_WEB:
				case PUSH_TYPE_COMPETE:
                case PUSH_TYPE_ACTIVITY:

					in = new Intent(GameTingApplication.getAppInstance(), MainHallActivity.class);

					in.putExtra(PushServiceReceiver.PUSH_NOTIFICATION, true);
					in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					in.putExtra(PushServiceReceiver.PUSH_NOTIFY_ID, NOTIFICATION_ID);
					in.putExtra(PushServiceReceiver.PUSH_MESSAGE, message);

					notifyid = NOTIFICATION_ID + notifyIndex++;
					if (notifyIndex > 50000) {
						notifyIndex = 0;
					}

					in.putExtra(PushServiceReceiver.NOTIFY_ID, notifyid);
					in.putExtra(PushServiceReceiver.PUSH_RECEIVE_TIME, System.currentTimeMillis() +"");
					
					try {
						ticker = "";
						title = "";
						content = "";
						iconUrl = "";
						ticker = object.getString("ticker");
						title = object.getString("title");
						content = object.getString("content");
						iconUrl = object.getString("iconurl");
					} catch (Exception e) {
					}

					ClickNumStatistics.addPushReceivedStatistis(GameTingApplication.getAppInstance(), notifyid + ":" + content);

					if (ticker == null || ticker.length() <= 0) {
						ticker = title;
					}

					if (StringUtil.isEmpty(iconUrl)) {
						PendingIntent pt = PendingIntent.getActivity(GameTingApplication.getAppInstance(), notifyid, in, 0);
						showNotification(R.drawable.ic_notifier, ticker, title, content, pt, notifyid);
					} else {
							PendingIntent pt = PendingIntent.getActivity(GameTingApplication.getAppInstance(), notifyid, in, 0);
							
							showTypedNotification(pt, ticker, notifyid, pushType, title, iconUrl,content);
					}
					break;

				case PUSH_TYPE_MSGNUM:// 个人未读消息数推送
					if (MineProfile.getInstance().getIsLogin() && object.getString("userid").equals(MineProfile.getInstance().getUserID())) {
					    MineProfile.getInstance().setMessagenum(object.getString("unreadmsgnum"));
					    MineProfile.getInstance().Save();
					    
					    MineProfile.getInstance().broadcastRefreshMsgEvent();
					}
					break;

				default:
					logger.d("unkown push type");
					break;
				}
			} catch (Exception e) {
				logger.d(e.toString());
			}

			object = null;

		} else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
			final String method = intent.getStringExtra(PushConstants.EXTRA_METHOD);
			final int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE, PushConstants.ERROR_SUCCESS);
			final String content = new String(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));

			if (errorCode == 0 && method.equals(PushConstants.METHOD_BIND)) {
				try {
					JSONObject object = new JSONObject(content);
					JSONObject params = object.getJSONObject("response_params");
					String user_id = params.getString("user_id");
					String channel_id = params.getString("channel_id");

					MineProfile.getInstance().setPush_userid(user_id);
					MineProfile.getInstance().setPush_channelid(channel_id);
					MineProfile.getInstance().Save();
				} catch (Exception e) {
				}
			}
		}
	}

	private void showNotification(int icon, String tickertext, String title, String content, PendingIntent intent, int notifyID) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(GameTingApplication.getAppInstance());
		builder.setSmallIcon(R.drawable.ic_notifier);
		builder.setTicker(tickertext);
		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setContentIntent(intent);
		builder.setWhen(System.currentTimeMillis());
		// builder.setContent(new
		// RemoteViews(GameTingApplication.getAppInstance().getPackageName(),
		// R.layout.update_notification_layout));

		if (AppUtil.isGameSearchForeground()) {
			builder.setDefaults(Notification.DEFAULT_SOUND);
		} else {
			builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		}

		NotificationManager nm = (NotificationManager) GameTingApplication.getAppInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notifyID, builder.build());
	}

	private void showNotification(Bitmap icon, String tickertext, String title, String content, PendingIntent intent, int notifyID) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(GameTingApplication.getAppInstance());

		builder.setLargeIcon(icon);
		builder.setSmallIcon(R.drawable.ic_notifier);
		builder.setTicker(tickertext);
		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setContentIntent(intent);
		builder.setWhen(System.currentTimeMillis());
		// builder.setContent(new
		// RemoteViews(GameTingApplication.getAppInstance().getPackageName(),
		// R.layout.update_notification_layout));

		if (AppUtil.isGameSearchForeground()) {
			builder.setDefaults(Notification.DEFAULT_SOUND);
		} else {
			builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		}

		NotificationManager nm = (NotificationManager) GameTingApplication.getAppInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notifyID, builder.build());
	}

	private void showNotificationWithIcon(Bitmap icon, String tickertext, String title, String content, PendingIntent intent, int notifyID) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(GameTingApplication.getAppInstance());

		RemoteViews rv = new RemoteViews(GameTingApplication.getAppInstance().getPackageName(), R.layout.notification_with_custom_icon);
		rv.setTextViewText(R.id.notification_title, title);
		rv.setTextViewText(R.id.notification_text, content);
		rv.setImageViewBitmap(R.id.notification_image, icon);

		builder.setLargeIcon(icon);
		builder.setSmallIcon(R.drawable.ic_notifier);
		builder.setTicker(tickertext);
		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setContentIntent(intent);
		builder.setWhen(System.currentTimeMillis());
		builder.setContent(rv);

		if (AppUtil.isGameSearchForeground()) {
			builder.setDefaults(Notification.DEFAULT_SOUND);
		} else {
			builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		}

		NotificationManager nm = (NotificationManager) GameTingApplication.getAppInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(notifyID, builder.build());

	}

	private void showNotification(String iconUrl) {

		ImageLoaderHelper.config();

		ImageLoader.getInstance().loadImage(iconUrl, this);
	}

	private void showTypedNotification(final PendingIntent intent, final String msg, final int notify_id, int tag, final String title, final String imageUrl,final String content) {

		try {
			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... params) {
					return loadImageFromUrl(imageUrl);
				}

				@Override
				protected void onPostExecute(String result) {
					try {
						int icon1 = R.drawable.ic_notifier;

						// 创建Notifcation
						Notification notification1 = new Notification(icon1, msg, notify_id);
						
						// 设定是否振动
						notification1.flags |= Notification.FLAG_AUTO_CANCEL;
						// 创建RemoteViews用在Notification中
						RemoteViews contentView = new RemoteViews(GameTingApplication.getAppInstance().getPackageName(), R.layout.notification_with_custom_icon);
						contentView.setTextViewText(R.id.notification_title, title);
						contentView.setTextViewText(R.id.notification_text, content);
						if (result != null) {
							Bitmap bit = BitmapFactory.decodeFile(result);
							if (bit != null) {
								contentView.setImageViewBitmap(R.id.notification_image, bit);
							} else {
								contentView.setImageViewResource(R.id.notification_image, R.drawable.ic_notifier);
							}

						} else {
							contentView.setImageViewResource(R.id.notification_image, R.drawable.ic_notifier);
						}
						notification1.contentView = contentView;
						notification1.contentIntent = intent;

						// 显示这个notification
						NotificationManager mNotificationManager = (NotificationManager) GameTingApplication.getAppInstance().getSystemService(Context.NOTIFICATION_SERVICE);
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
		// PendingIntent pt =
		// PendingIntent.getActivity(GameTingApplication.getAppInstance(),
		// notifyid, in, 0);
		// showNotification(R.drawable.ic_notifier, ticker, title, content, pt,
		// notifyid);
	}

	@Override
	public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
		PendingIntent pt = PendingIntent.getActivity(GameTingApplication.getAppInstance(), notifyid, in, 0);
		showNotificationWithIcon(arg2, ticker, title, content, pt, notifyid);
	}

	@Override
	public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		PendingIntent pt = PendingIntent.getActivity(GameTingApplication.getAppInstance(), notifyid, in, 0);
		showNotification(R.drawable.ic_notifier, ticker, title, content, pt, notifyid);
	}

	@Override
	public void onLoadingStarted(String arg0, View arg1) {

	}
}
