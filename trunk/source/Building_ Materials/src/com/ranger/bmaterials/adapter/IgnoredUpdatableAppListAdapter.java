package com.ranger.bmaterials.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.adapter.InstalledAppListAdapter.AppInfoViewHolder;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.tools.AppUtil;

public class IgnoredUpdatableAppListAdapter extends UpdatableAppListAdapter2 {

	private static final String TAG = "IgnoredUpdatableAppListAdapter";

	public IgnoredUpdatableAppListAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		Button button = (Button) v.findViewById(R.id.manager_activity_updatable_list_item_update_button);
		button.setText("取消");
		return v ;
	}
	
	
}
