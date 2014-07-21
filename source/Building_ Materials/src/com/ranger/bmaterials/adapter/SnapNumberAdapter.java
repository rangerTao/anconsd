package com.ranger.bmaterials.adapter;

import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.SnapNumber;
import com.ranger.bmaterials.mode.SnapNumber.SnapNumberStatus;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.ui.RoundCornerImageView;

public class SnapNumberAdapter extends AbstractListAdapter<SnapNumber> {
	private DisplayImageOptions options;

	public SnapNumberAdapter(Context context) {
		super(context);
		this.data = new CopyOnWriteArrayList<SnapNumber>();
		options = ImageLoaderHelper.getCustomOption(true,R.drawable.snapnumber_default_launcher);
	}

	static class Holder {
		RoundCornerImageView icon;
		TextView title;
		ProgressBar progress;
		TextView percent;
		ImageView button;
		ImageView overImage;
		ImageView nullImage;
		ImageView alreadyImage;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		Holder holder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.square_activity_snapnumber_list_item2, parent, false);
			holder = new Holder();
			holder.title = (TextView) view.findViewById(R.id.square_activity_snapnumber_title);
			holder.icon = (RoundCornerImageView) view.findViewById(R.id.square_activity_snapnumber_icon);
			holder.progress = (ProgressBar) view.findViewById(R.id.square_activity_snapnumber_left_progress);
			holder.percent = (TextView) view.findViewById(R.id.square_activity_snapnumber_left_text);
			holder.button = (ImageView) view.findViewById(R.id.square_activity_snapnumber_button);
			holder.overImage = (ImageView) view.findViewById(R.id.square_activity_snapnumber_image_over);
			holder.nullImage = (ImageView) view.findViewById(R.id.square_activity_snapnumber_image_null);
			holder.alreadyImage=(ImageView) view.findViewById(R.id.square_activity_snapnumber_image_already);
			holder.button.setOnClickListener(this);
			boolean[] enabled = { true, true ,true,true};
			holder.icon.setCornersEnabled(enabled);
			holder.icon.setRadius(UIUtil.dip2px(context, 8f));
			holder.icon.setDisplayImageOptions(options);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (Holder) view.getTag();
		}
		holder.button.setTag(position);
		bindView(position, holder);

		return view;
	}

	private void showNormalButton(Holder appInfoView) {
		appInfoView.overImage.setVisibility(View.INVISIBLE);
		appInfoView.nullImage.setVisibility(View.INVISIBLE);
		appInfoView.alreadyImage.setVisibility(View.INVISIBLE);
		appInfoView.button.setVisibility(View.VISIBLE);
	}

	private void showOverButton(Holder appInfoView) {
		appInfoView.button.setVisibility(View.INVISIBLE);
		appInfoView.nullImage.setVisibility(View.INVISIBLE);
		appInfoView.alreadyImage.setVisibility(View.INVISIBLE);
		appInfoView.overImage.setVisibility(View.VISIBLE);
	}

	private void showAlreadyButton(Holder appInfoView) {
		appInfoView.button.setVisibility(View.INVISIBLE);
		appInfoView.nullImage.setVisibility(View.INVISIBLE);
		appInfoView.overImage.setVisibility(View.INVISIBLE);
		appInfoView.alreadyImage.setVisibility(View.VISIBLE);
	}

	private void showNullButton(Holder appInfoView) {
		appInfoView.button.setVisibility(View.INVISIBLE);
		appInfoView.overImage.setVisibility(View.INVISIBLE);
		appInfoView.alreadyImage.setVisibility(View.INVISIBLE);
		appInfoView.nullImage.setVisibility(View.VISIBLE);
	}

	private void bindView(int position, Holder holder) {
		SnapNumber item = getItem(position);
		holder.title.setText(item.getTitle());
		int progressValue = getProgressValue(item.getTotalCount(), item.getLeftCount());
		holder.progress.setProgress(progressValue);
		if (progressValue > 0) {
			holder.percent.setText("号码剩余量:" + progressValue + "%");
		} else {
			holder.percent.setText("号码剩余量:无");
		}
		// appInfoView.title.setTypeface(Typeface.DEFAULT_BOLD);
		SnapNumberStatus status = item.getStatus();
		if (status == null || status == SnapNumberStatus.NOT_SNAPPED || status == SnapNumberStatus.NOT_LOGIN) {
			// holder.button.setText("抢号");
			// holder.button.setCompoundDrawablesWithIntrinsicBounds(0,
			// R.drawable.square_snap_num, 0, 0);
			// appInfoView.button.setBackgroundResource(R.drawable.list_item_button_selector);
			// appInfoView.button.setBackgroundDrawable(getBtnBgNormal());
			showNormalButton(holder);
			holder.button.setEnabled(true);

		} else if (status == SnapNumberStatus.SNAPPED) {
			// holder.button.setText("已抢");
			// holder.button.setCompoundDrawablesWithIntrinsicBounds(0,
			// R.drawable.square_snaped_num, 0, 0);
			// appInfoView.button.setBackgroundDrawable(getBtnBgNormal());
			showAlreadyButton(holder);
			holder.button.setEnabled(false);
		} else if (status == SnapNumberStatus.OVER) {
			holder.button.setEnabled(false);
			showOverButton(holder);
		}else{
			showNullButton(holder);
			holder.button.setEnabled(false);
		}
		if (item.getLeftCount() <= 0 /* || status == SnapNumberStatus.SNAPPED */) {
			if (status == SnapNumberStatus.SNAPPED) {
				// holder.button.setText("已抢");
				// holder.button.setCompoundDrawablesWithIntrinsicBounds(0,
				// R.drawable.square_snaped_num, 0, 0);
				showAlreadyButton(holder);
				holder.button.setEnabled(false);

			} else if (status == SnapNumberStatus.OVER) {
				showOverButton(holder);
				holder.button.setEnabled(false);
			} else {
				showNullButton(holder);
				holder.button.setEnabled(false);
			}
		}
		holder.icon.setImageUrl(item.getIconUrl());
	}

	public static int getProgressValue(long total, long current) {
		if (total <= 0)
			return 0;
		int ret = (int) (100L * current / total);
		if (ret <= 0 && current > 0) { // 只要有号码就不显示为0
			ret = 1;
		}
		return ret;
	}

	@Override
	public void onClick(View v) {
		if (onListItemClickListener == null) {
			return;
		}
		onListItemClickListener.onItemButtonClick(v, (Integer) v.getTag());
	}
}
