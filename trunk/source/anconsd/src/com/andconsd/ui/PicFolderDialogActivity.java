package com.andconsd.ui;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.andconsd.R;
import com.andconsd.adapter.SimpleFolderListAdapter;
import com.andconsd.listener.DialogFragmentCallback;
import com.andconsd.utils.AndConstants;

public class PicFolderDialogActivity extends DialogFragment implements OnItemClickListener {

	DialogFragmentCallback callback;
	
	public static int THEME = R.style.Theme_Sherlock;

	private static String base_path = Environment.getExternalStorageDirectory().getAbsolutePath();
	private View content;
	private ListView lvfolder;
	ArrayList<FolderItem> files;
	SimpleFolderListAdapter sfla;

	AlertDialog folderDialog;

	static PicFolderDialogActivity newInstance(String basefolder) {
		base_path = basefolder;
		PicFolderDialogActivity pfda = new PicFolderDialogActivity();
		return pfda;
	}
	
	public void setDialogCallback(DialogFragmentCallback call){
		callback = call;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String title = base_path;
		content = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_folderpicker, null);

		lvfolder = (ListView) content.findViewById(R.id.lvFolders);

		initFolderList(base_path);

		lvfolder.setOnItemClickListener(this);

		folderDialog = new AlertDialog.Builder(getActivity()).setView(content).setTitle(title).setNegativeButton(R.string.button_negative, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(callback != null){
					callback.onDialogDismiss();
				}
			}
		}).setPositiveButton(R.string.button_folder_pick_positive, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if(callback != null)
					callback.onDilaogConfirm(base_path);
				
			}
		}).create();

		return folderDialog;
	}
	
	

	private void initFolderList(String base) {

		files = new ArrayList<FolderItem>();
		File basePath = new File(base);

		files.add(new FolderItem(".", "."));
		if (basePath.getParentFile() != null)
			files.add(new FolderItem("..", ".."));
		if (basePath != null && basePath.exists()) {
			try {
				for (File file : basePath.listFiles()) {
					if (file.isDirectory()) {
						FolderItem fi = new FolderItem();
						fi.name = file.getName();
						fi.path = file.getAbsolutePath();
						files.add(fi);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			return;
		}
//
//		sfla = new SimpleFolderListAdapter(files, getActivity().getApplicationContext());
//
//		lvfolder.setAdapter(sfla);
//
//		sfla.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg2 == 0) {
			folderDialog.setTitle("/");
			initFolderList("/");
		} else if (arg2 == 1) {
			File parent = new File(base_path).getParentFile();
			if (parent != null) {
				folderDialog.setTitle(parent.getAbsolutePath());
				base_path = parent.getAbsolutePath();
				initFolderList(parent.getAbsolutePath());
			}

		} else {
			base_path = files.get(arg2).path;
			folderDialog.setTitle(files.get(arg2).path);
			initFolderList(base_path);
		}

	}

	public class FolderItem {

		public FolderItem() {
		}

		public FolderItem(String name, String path) {
			this.name = name;
			this.path = path;
		}

		public String name;
		public String path;
	}
}
