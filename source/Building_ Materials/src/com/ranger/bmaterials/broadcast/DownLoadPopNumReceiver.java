package com.ranger.bmaterials.broadcast;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DownLoadPopNumReceiver extends BroadcastReceiver {
	public ArrayList<IPopNumChanged> listeners = new ArrayList<IPopNumChanged>();

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(BroadcaseSender.ACTION_MANAGER_APPS_CHANGED) || action.equals(BroadcaseSender.ACTION_UPDATABLE_LIST_INITIALIZED)) {
			for (IPopNumChanged impl : listeners) {
                impl.onPopNumChanged(intent);
			}
		}
	}

	public interface IPopNumChanged {
		void onPopNumChanged(Intent intent);
	}
}
