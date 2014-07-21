package com.ranger.bmaterials.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.MineProfile;

public class NetWorkTipDialog extends CustomDialog {

	public NetWorkTipDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		contentView = View.inflate(cx,
				R.layout.custom_delete_confirm_dialog_layout, null);
		titleTv = (TextView) contentView
				.findViewById(R.id.progress_message_title);
		msgTv = (TextView) contentView.findViewById(R.id.progress_message_body);

		buttonLeft = (TextView) contentView
				.findViewById(R.id.dialog_button_left);
		buttonRight = (TextView) contentView
				.findViewById(R.id.dialog_button_right);

		msgTv.setText(R.string.no_network_dialog_hint);
		buttonLeft.setText(R.string.no_network_dialog_confirm);
		titleTv.setText(R.string.no_network_dialog_title);
		buttonRight.setText(R.string.no_network_dialog_cancel);
	}

	public void changeConfig() {
		Thread t = new Thread() {
			public void run() {
				MineProfile profile = MineProfile.getInstance();
				profile.setDownloadOnlyWithWiFi(false);
				profile.Save(cx);
			};
		};
		t.setDaemon(true);
		t.start();
	}
}
