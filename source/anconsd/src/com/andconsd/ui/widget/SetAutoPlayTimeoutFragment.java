package com.andconsd.ui.widget;

import java.util.ArrayList;

import com.andconsd.R;
import com.andconsd.adapter.SimpleFolderListAdapter;
import com.andconsd.listener.DialogFragmentCallback;
import com.andconsd.ui.activity.PicFolderDialogActivity.FolderItem;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.view.View;
import android.widget.ListView;

public class SetAutoPlayTimeoutFragment extends DialogFragment{

	DialogFragmentCallback callback;
	
	public static int THEME = R.style.Theme_Sherlock;

	private static String base_path = "/sdcard";
	private View content;
	private ListView lvfolder;
	ArrayList<FolderItem> files;
	SimpleFolderListAdapter sfla;

	AlertDialog folderDialog;

	static SetAutoPlayTimeoutFragment newInstance(String basefolder) {
		base_path = basefolder;
		SetAutoPlayTimeoutFragment pfda = new SetAutoPlayTimeoutFragment();
		return pfda;
	}
	
	public void setDialogCallback(DialogFragmentCallback call){
		callback = call;
	}
}
