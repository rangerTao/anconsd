package com.ranger.phonerecorder.service;

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class RecordPlayerService extends Service {

	private MediaPlayer mPlayer;
	private Handler mHandler;

	public static final int MEDIA_PLAYING = 1;
	public static final int MEDIA_PAUSE = MEDIA_PLAYING + 1;
	public static final int MEDIA_STOP = MEDIA_PAUSE + 1;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new RecordPlayerBinder();
	}

	public class RecordPlayerBinder extends Binder {

		public void setHandler(Handler handler) {
			mHandler = handler;
		}

		public void resumeReplay() {
			if (mPlayer != null) {
				mPlayer.start();

				sendMediaPlayingEvent();
			}
		}

		public void pause() {
			if (mPlayer != null && mPlayer.isPlaying()) {
				mPlayer.pause();

				Message msg = new Message();
				msg.what = MEDIA_PAUSE;
				mHandler.sendMessage(msg);
			}
		}

		private void sendMediaPlayingEvent() {
			Message msg = new Message();
			msg.what = MEDIA_PLAYING;
			mHandler.sendMessage(msg);
		}

		public void startPlay(String path) {
			if (mPlayer == null) {
				File mediaFile = new File(path);
				if (!mediaFile.exists()) {
					return;
				}
				mPlayer = new MediaPlayer();
			}else if(mPlayer.isPlaying()){
				mPlayer.stop();
				mPlayer.reset();
			}
			try {
				mPlayer.setDataSource(path);
				mPlayer.prepare();
				mPlayer.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						mPlayer.start();

						sendMediaPlayingEvent();
					}

				});
				mPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						Message msg = new Message();
						msg.what = MEDIA_STOP;
						mHandler.sendMessage(msg);
						
						releasePlayer();
					}
				});
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void stopPlay() {
			releasePlayer();

			Message msg = new Message();
			msg.what = MEDIA_STOP;
			mHandler.sendMessage(msg);
		}

	}

	private void releasePlayer() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.reset();
			mPlayer.release();
			mPlayer = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		releasePlayer();
		super.onDestroy();
	}

}
