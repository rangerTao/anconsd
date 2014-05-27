package com.ranger.phonerecorder.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.ranger.phonerecorder.app.Constants;
import com.ranger.phonerecorder.pojos.EmailServer;
import com.ranger.phonerecorder.task.UploadFileTask;
import com.ranger.phonerecorder.utils.EmailUtil;
import com.ranger.phonerecorder.utils.SharePerfenceUtil;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

/**
 * A service used to observer the status of phone call.
 * 
 * @author taoliang
 * 
 */
public class PhoneStatusService extends Service {

	private String reminder_path;
	private String message_path;

	private static final int MAX_INTERVAL = 60 * 1000;
	private AtomicBoolean isRecording = new AtomicBoolean(false);
	private AtomicInteger recordingInterval = new AtomicInteger();

	private FileObserver messageFileObserver;
	private FileObserver systemRecorderObserver;

	UploadFileTask uFileTask;

	private Handler mHandler = new Handler();

	class RecordingObserverThread extends Thread {

		@Override
		public void run() {

			recordingInterval.set(0);

			while (isRecording.get() && (recordingInterval.get() < MAX_INTERVAL)) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
				recordingInterval.addAndGet(1000);
			}

			if (recorder != null) {
				stopRecorder();
			}

			super.run();
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private PhoneStateListener phoneStateListener;

	private RecordingObserverThread recordingObserverThread;

	@Override
	public void onCreate() {
		super.onCreate();
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		phoneStateListener = new MyPhoneStatusListener();
		tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);// 监听手机的通话状态的变化

		uFileTask = new UploadFileTask(getApplicationContext(), mHandler);

		messageFileObserver = new FileObserver(Constants.PHONE_MESSAGE_DIR) {

			@Override
			public void onEvent(int event, String path) {
				switch (event) {
				case FileObserver.CLOSE_WRITE:

					EmailServer es = Constants.emailServerInfo;
					// es = new EmailServer();
					// es.username = "taoliang1985@126.com";
					// es.pass = "R@nger19850121";
					// es.server = "smtp.126.com";
					// es.port = "";
					String income = "";
					if (path.contains("_")) {
						income = path.substring(0, path.indexOf("_"));
					} else {
						income = path;
					}
					if (Constants.emailServerInfo != null) {
						EmailUtil.sendMailByJavaMail(es, Constants.PHONE_MESSAGE_DIR + path, recordingInterval.get(), income);
					}
					break;
				default:
					break;
				}
			}
		};

		final String systemRecorderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Recorder/message";
		systemRecorderObserver = new FileObserver(systemRecorderPath) {

			@Override
			public void onEvent(int event, String path) {
				switch (event) {
				case FileObserver.CLOSE_WRITE:
					File file = new File(systemRecorderPath + "/" + path);
					if (file.exists()) {
						file.delete();
					}
					break;

				default:
					break;
				}

			}
		};

		systemRecorderObserver.startWatching();
		messageFileObserver.startWatching();
	}

	public MediaRecorder recorder;
	public MediaPlayer mPlayer;

	public void stopRecorder() {
		try {
			isRecording.set(false);
			if (recorder != null) {
				recorder.stop();
				if (message_path != null && !message_path.equals("")) {
					SharePerfenceUtil.saveRecordFileInterval(getApplicationContext(), message_path, recordingInterval.get());
				}
				recorder.reset();
				recorder.release();
				recorder = null;
			}
		} catch (Exception e) {
		}
	}

	private class MyPhoneStatusListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			try {
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:

					// 空闲状态，没有通话没有响铃
					stopRecorder();

					if (mPlayer != null) {
						mPlayer.stop();
						mPlayer.release();
						mPlayer = null;
					}

					break;
				case TelephonyManager.CALL_STATE_RINGING: // 响铃状态

					recorder = new MediaRecorder();
					// 创建一个录音机
					recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);
//					 recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
					// 设置录制的音频源从话筒里面获取声音
					recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
					recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					message_path = Constants.PHONE_MESSAGE_DIR + "/" + incomingNumber + "_" + Constants.date_formater.format(new Date()) + ".mp4";
					File cacheFolder = new File(Constants.PHONE_MESSAGE_DIR);
					if (!cacheFolder.exists()) {
						cacheFolder.mkdirs();
					}
					recorder.setOutputFile(message_path);
					recorder.prepare();

					autoAnswerPhone();

					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:

					// 通话状态
					if (recorder != null) {
						recorder.start();
						isRecording.set(true);
						recordingObserverThread = new RecordingObserverThread();
						recordingObserverThread.start();
					}

					File remindFile = new File(Constants.REMINDER_PATH);
					if (remindFile.exists()) {
						preparePlayerAndPlay();
					}

					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void preparePlayerAndPlay() throws IOException {
			if (mPlayer != null) {
				mPlayer.start();
			} else {
				mPlayer = new MediaPlayer();

				File remindFile = new File(Constants.REMINDER_PATH);
				// File remindFile = new
				// File(Environment.getExternalStorageDirectory().getAbsolutePath()
				// + "/1234.mp3");
				if (remindFile.exists()) {
					mPlayer.setDataSource(Constants.REMINDER_PATH);
				} else {
					AssetFileDescriptor default_reminder = getAssets().openFd("default_reminder.mp4");
					mPlayer.setDataSource(default_reminder.getFileDescriptor());
				}
				mPlayer.prepare();
				mPlayer.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						mPlayer.start();
					}
				});
			}
		}
	}

	public void autoAnswerPhone() {
		try {
			Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
			intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
			sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
			intent = new Intent("android.intent.action.MEDIA_BUTTON");
			keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
			intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
			sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
			Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
			localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			localIntent1.putExtra("state", 1);
			localIntent1.putExtra("microphone", 1);
			localIntent1.putExtra("name", "Headset");
			sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");
			Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
			KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
			localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
			sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
			Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
			KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
			localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
			sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");
			Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
			localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			localIntent4.putExtra("state", 0);
			localIntent4.putExtra("microphone", 1);
			localIntent4.putExtra("name", "Headset");
			sendOrderedBroadcast(localIntent4, "android.permission.CALL_PRIVILEGED");
		} catch (Exception e2) {
			try {
				Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
				meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
				sendOrderedBroadcast(meidaButtonIntent, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {

		Intent serviceIntent = new Intent(this, PhoneStatusService.class);
		startService(serviceIntent);
		super.onDestroy();
	}
}
