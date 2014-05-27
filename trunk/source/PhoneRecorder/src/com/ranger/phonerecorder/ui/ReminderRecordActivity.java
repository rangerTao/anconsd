package com.ranger.phonerecorder.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ranger.phonerecorder.R;
import com.ranger.phonerecorder.adapter.ReminderRecordAdapter;
import com.ranger.phonerecorder.app.Constants;
import com.ranger.phonerecorder.pojos.PhoneRecord;
import com.ranger.phonerecorder.service.recorder.ReminderRecrodService;
import com.ranger.phonerecorder.service.recorder.ReminderRecrodService.IRecordBinder;
import com.ranger.phonerecorder.service.recorder.ReminderRecrodService.PlayerStatus;
import com.ranger.phonerecorder.utils.StringUtil;

public class ReminderRecordActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {

	private IRecordBinder recordBinder;

	private ImageView btnRecordStart;
	private ImageView btnReplay;
	private TextView tvTimeEclipsed;
	
	private AtomicInteger selectItemIndex = new AtomicInteger();
	
	private static final int REFRESH_TIME_ECLIPSE = 998;

	private ArrayList<PhoneRecord> reminders;

	private ListView lvReminders;
	private ReminderRecordAdapter adapterReminders;

	private PlayerStatus playerStatus = PlayerStatus.END;

	FileObserver recordFileObserver = new FileObserver(Constants.PHONE_REMIND_DIR) {

		@Override
		public void onEvent(int event, String path) {
			switch (event) {
			case FileObserver.CLOSE_WRITE:
				File newFile = new File(Constants.PHONE_REMIND_DIR + "/" + path);
				if (newFile.exists()) {
					PhoneRecord pr = new PhoneRecord();
					pr.name = newFile.getName();
					pr.path = newFile.getAbsolutePath();
					pr.create_time = Constants.date_formater.format(newFile.lastModified());
					reminders.add(0, pr);
				}

				adapterReminders.notifyDataSetChanged();
				break;

			default:
				break;
			}

		}
	};
	
	private AtomicInteger recordingTime = new AtomicInteger(60 * 1000);	
	
	class recordTimeThread extends Thread{

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			
			while (isRecording.get()) {
				
				if(recordingTime.get() < 1){

					Message msgend = new Message();
					msgend.what = ReminderRecrodService.RECORD_END;
					mHandler.sendMessage(msgend);
					return;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
				
				recordingTime.set(recordingTime.get() - 1000);
				
				Message msg = new Message();
				msg.what = REFRESH_TIME_ECLIPSE;
				mHandler.sendMessage(msg);
				
			}
			
			super.run();
		}
		
	};

	private Handler mHandler = new Handler() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case ReminderRecrodService.PLAYER_START:
				btnReplay.setVisibility(View.VISIBLE);
				playerStatus = PlayerStatus.PLAYING;
				btnReplay.setImageResource(R.drawable.icon_button_media_pause);
				break;
			case ReminderRecrodService.PLAYER_END:
				playerStatus = PlayerStatus.END;
				btnReplay.setImageResource(R.drawable.icon_button_media_play);
				break;
			case ReminderRecrodService.RECORD_END:
				stopRecording();
				break;
			case REFRESH_TIME_ECLIPSE:
				String time = StringUtil.getFormattedTimeByMillseconds(recordingTime.get());
				tvTimeEclipsed.setText(time);
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}

	};

	ServiceConnection scRecorder = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			recordBinder = (IRecordBinder) service;
			recordBinder.setHandler(mHandler);
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_remind_recorder_activity);

		setTitle("设置应答音");
		
		recordFileObserver.startWatching();

		reminders = new ArrayList<PhoneRecord>();

		Intent serviceIntent = new Intent(this, ReminderRecrodService.class);
		bindService(serviceIntent, scRecorder, BIND_AUTO_CREATE);

		btnRecordStart = (ImageView) findViewById(R.id.btn_record_start);
		btnReplay = (ImageView) findViewById(R.id.btn_replay);
		lvReminders = (ListView) findViewById(R.id.lv_record_list);
		tvTimeEclipsed = (TextView) findViewById(R.id.tvTimeEclipsed);

		initData();

		adapterReminders = new ReminderRecordAdapter(this, reminders);
		
		lvReminders.setAdapter(adapterReminders);
		lvReminders.setOnItemClickListener(this);
		lvReminders.setOnItemLongClickListener(this);
		lvReminders.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("请选择");
				menu.add(0, 1, 0, "播放");
				menu.add(0, 2, 0, "删除");
				menu.add(0, 3, 0, "选中");
			}
		});
	}

	private void initData() {
		File baseFolder = new File(Constants.PHONE_REMIND_DIR);
		if (!baseFolder.exists()) {
			baseFolder.mkdirs();
		}

		for (File reminder : baseFolder.listFiles()) {
			PhoneRecord pr = new PhoneRecord();
			pr.name = reminder.getName();
			pr.path = reminder.getAbsolutePath();
			pr.create_time = Constants.date_formater.format(reminder.lastModified());
			reminders.add(pr);
		}
	}

	private AtomicBoolean isRecording = new AtomicBoolean(false);

	public void button_onclick(View view) {

		switch (view.getId()) {
		case R.id.btn_record_start:

			if (!isRecording.get()) {
				
				tvTimeEclipsed.setText(StringUtil.getFormattedTimeByMillseconds(recordingTime.get()));
				tvTimeEclipsed.setVisibility(View.VISIBLE);
				btnRecordStart.setImageResource(R.drawable.icon_button_media_stop);
				recordBinder.startRecord();
				btnReplay.setVisibility(View.INVISIBLE);
				isRecording.set(true);
				new recordTimeThread().start();
			} else {
				stopRecording();
			}
			break;
		case R.id.btn_replay:
			try {
				switch (playerStatus) {
				case PLAYING:
					recordBinder.stopReplay();
					break;
				case END:
					recordBinder.playRecord();
					break;
				default:
					break;
				}
			} catch (Exception e) {
			}

			break;
		// case R.id.btn_finish:
		// recordBinder.setFileSelected();
		// finish();
		// break;
		default:
			break;
		}
	}

	private void stopRecording() {
		btnRecordStart.setImageResource(R.drawable.icon_button_media_record);
		recordBinder.stopRecord();
		btnReplay.setVisibility(View.VISIBLE);
		isRecording.set(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		unbindService(scRecorder);
		
		recordFileObserver.stopWatching();
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		PhoneRecord pr = reminders.get(position);
		if(pr == null)
			return;
		
		recordBinder.playRecord(pr.path);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		PhoneRecord pr = reminders.get(selectItemIndex.get());
		
		switch (item.getItemId()) {
		case 1:
			
			if(pr == null)
				break;
			
			if(!pr.path.equals(""))
				recordBinder.playRecord(pr.path);
			break;
		case 2:
			
			File prFile = new File(pr.path);
			if(prFile.exists()){
				prFile.delete();
				reminders.remove(selectItemIndex.get());
				adapterReminders.notifyDataSetChanged();
			}
			break;
		case 3:
			recordBinder.setFileSelected(pr.path);
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		selectItemIndex.set(position);
		lvReminders.showContextMenu();
		return true;
	}
	
	

}
