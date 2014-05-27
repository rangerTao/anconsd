package com.ranger.phonerecorder.service.recorder;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.ranger.phonerecorder.app.Constants;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class ReminderRecrodService extends Service {

	private MediaRecorder mRecorder;
	private MediaPlayer mPlayer;
	private String filename;

	private RecordStatus status;

	private Handler mHandler;

	public static final int PLAYER_START = 1;
	public static final int PLAYER_END = PLAYER_START + 1;
	public static final int RECORD_END = PLAYER_END + 1;

	public static enum RecordStatus {
		RECORDING, RECORD_END
	}

	public static enum PlayerStatus {
		PLAYING, END
	}

	@Override
	public IBinder onBind(Intent intent) {

		File cache_dir = new File(Constants.PHONE_REMIND_DIR);
		if (!cache_dir.exists() || !cache_dir.isDirectory()) {
			cache_dir.mkdirs();
		}

		filename = Constants.REMINDER_PATH;

		initMediaPlayer();

		return new IRecordBinder();
	}

	private void initRecorder() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		filename = Constants.PHONE_REMIND_DIR + "/" + Constants.date_formater.format(new Date()) + ".3gp";
		mRecorder.setOutputFile(filename);
		try {
			mRecorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initMediaPlayer() {
		mPlayer = new MediaPlayer();
	}

	public class IRecordBinder extends Binder {

		/**
		 * 开始录音
		 */
		public void startRecord() {
			if (mRecorder == null) {
				initRecorder();
			} else {
				releaseRecorder();
				initRecorder();
			}

			mRecorder.start();
		}

		/**
		 * 停止录音
		 */
		public void stopRecord() {
			releaseRecorder();

			status = RecordStatus.RECORD_END;
		}

		public RecordStatus getStatus() {
			return status;
		}

		public void setHandler(Handler handler) {
			mHandler = handler;
		}

		public MediaRecorder getRecorder() {
			return mRecorder;
		}

		public MediaPlayer getPlayer() {
			return mPlayer;
		}

		public void stopReplay() {
			if (mPlayer != null && mPlayer.isPlaying()) {
				releasePlayer();
			}
		}

		private void releasePlayer() {

			if (mPlayer == null) {
				return;
			}
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;

			Message msg = new Message();
			msg.what = PLAYER_END;
			mHandler.sendMessage(msg);
		}

		public void setFileSelected(String path) {
			SharedPreferences sp = getSharedPreferences("record", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("filepath", path);
			editor.commit();
			Constants.REMINDER_PATH = path;
		}
		
		public void playRecord(String path){
			filename = path;
			playRecord();
		}

		/**
		 * 播放录音
		 */
		public void playRecord() {

			if (mPlayer != null && mPlayer.isPlaying()) {
				mPlayer.stop();
				mPlayer.release();
				mPlayer = null;
			} else {
				File file = new File(filename);
				if (file.exists()) {
					try {
						mPlayer = new MediaPlayer();
						mPlayer.setDataSource(filename);
						mPlayer.prepare();
						mPlayer.setOnPreparedListener(new OnPreparedListener() {

							@Override
							public void onPrepared(MediaPlayer mp) {
								mPlayer.start();
								Message msg = new Message();
								msg.what = PLAYER_START;
								mHandler.sendMessage(msg);
							}
						});
						mPlayer.setOnCompletionListener(new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								releasePlayer();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}

		public ReminderRecrodService getService() {
			return ReminderRecrodService.this;
		}
	}

	private void releaseRecorder() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.reset();
			mRecorder.release();
			mRecorder = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		releaseRecorder();
	}

}
