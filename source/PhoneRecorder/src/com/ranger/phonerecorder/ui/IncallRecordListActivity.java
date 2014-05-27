package com.ranger.phonerecorder.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.ranger.phonerecorder.R;
import com.ranger.phonerecorder.adapter.IncallRecordAdapter;
import com.ranger.phonerecorder.app.Constants;
import com.ranger.phonerecorder.pojos.PhoneRecord;
import com.ranger.phonerecorder.service.RecordPlayerService;
import com.ranger.phonerecorder.service.RecordPlayerService.RecordPlayerBinder;
import com.ranger.phonerecorder.task.UploadFileTask;
import com.ranger.phonerecorder.utils.EmailUtil;
import com.ranger.phonerecorder.utils.UploadStatusUtil;

import android.R.integer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class IncallRecordListActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {

	private ListView lv_record_list;
	private IncallRecordAdapter incall_adapter;

	private ArrayList<PhoneRecord> records;
	RecordPlayerBinder rpbinder;

	private LinearLayout ll_media_controller;
	private ImageView iv_button_control;
	
	private AtomicInteger selectedItemIndex = new AtomicInteger(-1);
	
	private Handler mHandler = new Handler() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case RecordPlayerService.MEDIA_PLAYING:
				if (ll_media_controller != null) {
					ll_media_controller.setVisibility(View.VISIBLE);
				}

				if (iv_button_control != null) {
					iv_button_control.setImageResource(R.drawable.icon_button_media_pause);

					iv_button_control.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							rpbinder.pause();
						}
					});
				}

				break;
			case RecordPlayerService.MEDIA_PAUSE:
				if (iv_button_control != null) {
					iv_button_control.setImageResource(R.drawable.icon_button_media_play);

					iv_button_control.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							rpbinder.resumeReplay();
						}
					});
				}
				break;
			case RecordPlayerService.MEDIA_STOP:
				if (ll_media_controller != null) {
					ll_media_controller.setVisibility(View.GONE);

					iv_button_control.setOnClickListener(null);
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private ServiceConnection playerConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			rpbinder = (RecordPlayerBinder) service;
			rpbinder.setHandler(mHandler);
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_incall_record_list_activity);
		setTitle("来电录音记录");

		lv_record_list = (ListView) findViewById(R.id.lv_record_list);
		lv_record_list.setOnItemClickListener(this);
		lv_record_list.setOnItemLongClickListener(this);
		lv_record_list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				PhoneRecord pr = null;
				if(selectedItemIndex.get() != -1){
					pr = records.get(selectedItemIndex.get());
				}
				menu.setHeaderTitle("请选择");
				menu.add(0, 1, 0, "播放");
				menu.add(0, 2, 0, "删除");
				if(pr!= null && !UploadStatusUtil.checkFileUpload(getApplicationContext(), pr.path)){
					menu.add(0, 3, 0, "上传");
				}
			}
		});
		
		ll_media_controller = (LinearLayout) findViewById(R.id.ll_media_controller);
		iv_button_control = (ImageView) findViewById(R.id.iv_button_play);

		initRecordDataAndListView();

		bindService(new Intent(this, RecordPlayerService.class), playerConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void initRecordDataAndListView() {

		records = new ArrayList<PhoneRecord>();
		File record_folder = new File(Constants.PHONE_MESSAGE_DIR);
		if (!record_folder.exists()) {
			record_folder.mkdirs();
		}
		if (record_folder.exists() && record_folder.isDirectory()) {
			for (File record : record_folder.listFiles()) {
				PhoneRecord ir = new PhoneRecord();
				ir.name = record.getName();
				ir.path = record.getAbsolutePath();
				ir.create_time = Constants.date_formater.format(record.lastModified());
				records.add(ir);
			}
		}

		incall_adapter = new IncallRecordAdapter(this, records);

		lv_record_list.setAdapter(incall_adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		String filePath = records.get(position).path;
		rpbinder.startPlay(filePath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		unbindService(playerConnection);
		super.onDestroy();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		selectedItemIndex.set(position);
		lv_record_list.showContextMenu();
		return true;
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case 1:
			if(selectedItemIndex.get() != -1 && records.get(selectedItemIndex.get()) != null)
				rpbinder.startPlay(records.get(selectedItemIndex.get()).path);
			break;
		case 2:
			if(selectedItemIndex.get() != -1 && records.get(selectedItemIndex.get()) != null){
				String path = records.get(selectedItemIndex.get()).path;
				File delFile = new File(path);
				if(delFile.exists()){
					delFile.delete();
					records.remove(selectedItemIndex.get());
					incall_adapter.notifyDataSetChanged();
				}
			}
			break;
		case 3:
			UploadFileTask uFileTask = new UploadFileTask(getApplicationContext(), mHandler);
			uFileTask.execute(records.get(selectedItemIndex.get()).path);
//			EmailUtil.sendMailByJavaMail(Constants.emailServerInfo, records.get(selectedItemIndex.get()).path, length, inComeCall)
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

}
