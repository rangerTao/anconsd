package com.ranger.bmaterials.adapter;

import java.util.Date;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.OpenServer;
import com.ranger.bmaterials.tools.DateUtil;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.view.ImageViewForList;

/**
 * 开服列表
 * 
 * @author wangliang
 * 
 */
public class GameReleaseAdapter extends AbstractListAdapter<OpenServer> {
	private DisplayImageOptions options;

	public GameReleaseAdapter(Context context) {
		super(context);
		options = ImageLoaderHelper.getCustomOption(true,R.drawable.release_default_launcher);
	}

	static class Holder {
		RoundCornerImageView icon;
		TextView title;
		TextView date;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		Holder holder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.square_activity_list_item,
					parent, false);
			holder = new Holder();
			holder.title = (TextView) view.findViewById(R.id.list_title);
			holder.date = (TextView) view.findViewById(R.id.list_date);
			holder.icon = (RoundCornerImageView) view.findViewById(R.id.list_icon);
			boolean[] enabled = { true, true ,true,true};
			holder.icon.setCornersEnabled(enabled);
			holder.icon.setRadius(UIUtil.dip2px(context, 8f));
			holder.icon.setDisplayImageOptions(options);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (Holder) view.getTag();
		}
		bindView(position, holder);

		return view;
	}

	private void bindView(int position, Holder holder) {
		OpenServer item = getItem(position);
		holder.title
				.setText("【" + item.getGameName() + "】  " + item.getTitle());
		long time = item.getTime();
		holder.date.setText("时间:" + DateUtil.formatDate(new Date(time)));

		holder.icon.setImageUrl(item.getGameIcon());
	}

}
