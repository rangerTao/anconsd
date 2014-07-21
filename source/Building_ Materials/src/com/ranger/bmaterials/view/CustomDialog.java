package com.ranger.bmaterials.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.ranger.bmaterials.R;

public class CustomDialog extends Dialog {

	protected Context cx;

	private String title;

	private String message;

	private View.OnClickListener clickListner;

	public TextView buttonLeft, buttonRight;

	protected ICustomDialog impl;

	public View contentView;
	protected ViewGroup bodyView;

	public TextView titleTv;

	public TextView msgTv;

	public CustomDialog setImpl(ICustomDialog impl) {
		this.impl = impl;
		return this;
	}

	public interface ICustomDialog {
		View initOtherView(CustomDialog dialog);

		void preAddView(CustomDialog dialog);
	}

	public CustomDialog(Context context) {
		super(context, R.style.dialog_style_zoom);
		// TODO Auto-generated constructor stub
		this.cx = context;
		setCancelable(true);
	}

	public CustomDialog setClickListner(View.OnClickListener clickListner) {
		this.clickListner = clickListner;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	protected void initView() {
		contentView = View.inflate(cx, R.layout.custom_dialog_layout, null);
		titleTv = (TextView) contentView.findViewById(R.id.custom_dialog_title);
		msgTv = (TextView) contentView
				.findViewById(R.id.custom_dialog_body_msg);
		bodyView = (ViewGroup) contentView
				.findViewById(R.id.custom_dialog_body_layout);

		buttonLeft = (TextView) contentView
				.findViewById(R.id.dialog_button_left);
		buttonRight = (TextView) contentView
				.findViewById(R.id.dialog_button_right);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		if (cx != null && cx instanceof Activity
				&& !((Activity) cx).isFinishing())
			super.show();
	}

	public CustomDialog createView() {
		initView();

		if (impl != null) {
			View otherView = impl.initOtherView(this);
			if (otherView != null) {
				bodyView.addView(otherView);
			}else{
				bodyView.setVisibility(View.GONE);
			}
			impl.preAddView(this);
		}

		if (title != null) {
			titleTv.setText(title);
		}

		if (message != null) {
			msgTv.setVisibility(View.VISIBLE);
			msgTv.setText(message);
		}

		if (clickListner != null) {
			buttonLeft.setOnClickListener(clickListner);
			buttonRight.setOnClickListener(clickListner);
		}

		addContentView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		return this;
	}

	public View.OnClickListener getClickListner() {
		return clickListner;
	}
}
