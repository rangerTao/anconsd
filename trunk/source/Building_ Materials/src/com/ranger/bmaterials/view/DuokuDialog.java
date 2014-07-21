package com.ranger.bmaterials.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.ui.DownloadDialogActivity;

public class DuokuDialog {

	public interface OnAlertSelectId {
		void onClick(int whichButton);
	}

	/**
	 * 
	 * @param context
	 * @param reqCode
	 * @param type
	 * @param arg1
	 *            包名
	 * @param arg2
	 *            下载地址
	 * @param extra
	 *            额外的信息，可选
	 */
	public static void showNetworkAlertDialog(Activity context, int reqCode,
			String arg1, String arg2, Serializable extraArg) {
		Intent intent = new Intent(context, DownloadDialogActivity.class);
		intent.putExtra(DownloadDialogActivity.ARG_ALERT_TYPE,
				DownloadDialogActivity.ALERT_FOR_NETWORK);
		intent.putExtra(DownloadDialogActivity.ARG1, arg1);
		intent.putExtra(DownloadDialogActivity.ARG2, arg2);
		intent.putExtra(DownloadDialogActivity.ARG_EXTRA, extraArg);
		context.startActivityForResult(intent, reqCode);
	}


	/**
	 * DownloadDialogActivity.ALERT_FOR_NETWORK
	 * 
	 * @param context
	 * @param reqCode
	 * @param type
	 */
	public static void showNetworkAlertDialog(Fragment context, int reqCode,
			String arg1, String arg2, Serializable extraArg) {
		Intent intent = new Intent(context.getActivity(),
				DownloadDialogActivity.class);
		intent.putExtra(DownloadDialogActivity.ARG_ALERT_TYPE,
				DownloadDialogActivity.ALERT_FOR_NETWORK);
		intent.putExtra(DownloadDialogActivity.ARG1, arg1);
		intent.putExtra(DownloadDialogActivity.ARG2, arg2);
		intent.putExtra(DownloadDialogActivity.ARG_EXTRA, extraArg);
		context.startActivityForResult(intent, reqCode);
	}

	public static void showRemoveOldDialog(Activity context, int reqCode,
			String packageName, String name, Serializable extraArg) {
		Intent intent = new Intent(context, DownloadDialogActivity.class);
		intent.putExtra(DownloadDialogActivity.ARG_ALERT_TYPE,
				DownloadDialogActivity.ALERT_FOR_REMOVE_OLD);
		intent.putExtra(DownloadDialogActivity.ARG1, packageName);
		intent.putExtra(DownloadDialogActivity.ARG2, name);
		intent.putExtra(DownloadDialogActivity.ARG_EXTRA, extraArg);
		context.startActivityForResult(intent, reqCode);
	}

	public static Dialog showProgressDialog(Context context,
			boolean cancelable, OnCancelListener cancelListener) {
		Dialog dialog = new Dialog(context, R.style.dialog_style_zoom);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		View contentView = View.inflate(context,
				R.layout.custom_progress_dialog_layout, null);
		// ProgressBar progressBar = new
		// ProgressBar(context,null,android.R.attr.progressBarStyle);
		// progressBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progressbar_color));
		dialog.addContentView(contentView, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		dialog.show();

		return dialog;
	}

	public static Dialog showAlerDialog(Context context, boolean cancelable,
			String title, String message, String p, String n,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = null;
		// if(Build.VERSION.SDK_INT >= 11){
		// builder = new AlertDialog.Builder(context,
		// R.style.dialog_style_zoom);
		// }else {
		builder = new AlertDialog.Builder(context);
		// }
		builder.setCancelable(cancelable);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(p, listener);
		builder.setPositiveButton(n, listener);
		AlertDialog dialog = builder.create();
		dialog.show();

		return dialog;
	}

	/*
	 * public static Dialog showPushUpDialog(final Context context, final String
	 * title, final String[] items, String exit, final OnAlertSelectId alertDo,
	 * OnCancelListener cancelListener) {
	 * 
	 * String cancel = "cancle";//context.getString(R.string.app_cancel); final
	 * Dialog dlg = new Dialog(context, R.style.dialog_style_push_up);
	 * LayoutInflater inflater = (LayoutInflater)
	 * context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); LinearLayout
	 * layout = (LinearLayout)
	 * inflater.inflate(R.layout.alert_dialog_menu_layout, null); final int
	 * cFullFillWidth = 10000; layout.setMinimumWidth(cFullFillWidth); final
	 * ListView list = (ListView) layout.findViewById(R.id.content_list);
	 * AlertAdapter adapter = new AlertAdapter(context, title, items, exit,
	 * cancel); list.setAdapter(adapter); list.setDividerHeight(0);
	 * 
	 * list.setOnItemClickListener(new OnItemClickListener() {
	 * 
	 * @Override public void onItemClick(AdapterView<?> parent, View view, int
	 * position, long id) { if (!(title == null || title.equals("")) && position
	 * - 1 >= 0) { alertDo.onClick(position - 1); dlg.dismiss();
	 * list.requestFocus(); } else { alertDo.onClick(position); dlg.dismiss();
	 * list.requestFocus(); } } }); // set a large value put it in bottom Window
	 * w = dlg.getWindow(); WindowManager.LayoutParams lp = w.getAttributes();
	 * lp.x = 0; final int cMakeBottom = -1000; lp.y = cMakeBottom; lp.gravity =
	 * Gravity.BOTTOM; dlg.onWindowAttributesChanged(lp);
	 * dlg.setCanceledOnTouchOutside(true); if (cancelListener != null) {
	 * dlg.setOnCancelListener(cancelListener); } dlg.setContentView(layout);
	 * dlg.show(); return dlg; }
	 */
	/*
	 * public static Dialog showZoomDialog(final Context context, final String
	 * title, final String[] items, String exit, final OnAlertSelectId alertDo,
	 * OnCancelListener cancelListener) { }
	 */

}

class AlertAdapter extends BaseAdapter {
	public static final int TYPE_BUTTON = 0;
	public static final int TYPE_TITLE = 1;
	public static final int TYPE_EXIT = 2;
	public static final int TYPE_CANCEL = 3;
	private List<String> items;
	private int[] types;
	private boolean isTitle = false;
	private Context context;

	public AlertAdapter(Context context, String title, String[] items,
			String exit, String cancel) {
		if (items == null || items.length == 0) {
			this.items = new ArrayList<String>();
		} else {
			this.items = Arrays.asList(items);
		}
		this.types = new int[this.items.size() + 3];
		this.context = context;
		if (!TextUtils.isEmpty(title)) {
			types[0] = TYPE_TITLE;
			this.isTitle = true;
			this.items.add(0, title);
		}

		if (!TextUtils.isEmpty(exit)) {
			types[this.items.size()] = TYPE_EXIT;
			this.items.add(exit);
		}

		if (!TextUtils.isEmpty(cancel)) {
			types[this.items.size()] = TYPE_CANCEL;
			this.items.add(cancel);
		}
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		if (position == 0 && isTitle) {
			return false;
		} else {
			return super.isEnabled(position);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/*
		 * final String textString = (String) getItem(position); ViewHolder
		 * holder; int type = types[position]; if (convertView == null ||
		 * ((ViewHolder) convertView.getTag()).type != type) { holder = new
		 * ViewHolder(); if (type == TYPE_CANCEL) { convertView =
		 * View.inflate(context, R.layout.alert_dialog_menu_list_layout_cancel,
		 * null); } else if (type == TYPE_BUTTON) { convertView =
		 * View.inflate(context, R.layout.alert_dialog_menu_list_layout, null);
		 * } else if (type == TYPE_TITLE) { convertView = View.inflate(context,
		 * R.layout.alert_dialog_menu_list_layout_title, null); } else if (type
		 * == TYPE_EXIT) { convertView = View.inflate(context,
		 * R.layout.alert_dialog_menu_list_layout_special, null); }
		 * 
		 * holder.text = (TextView) convertView.findViewById(R.id.popup_text);
		 * holder.type = type;
		 * 
		 * convertView.setTag(holder); } else { holder = (ViewHolder)
		 * convertView.getTag(); }
		 * 
		 * holder.text.setText(textString);
		 */
		return convertView;
	}

	static class ViewHolder {
		TextView text;
		int type;
	}
}
