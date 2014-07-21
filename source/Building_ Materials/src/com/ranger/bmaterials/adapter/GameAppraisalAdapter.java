package com.ranger.bmaterials.adapter;

import java.util.Date;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.ActivityInfo;
import com.ranger.bmaterials.tools.DateUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.tools.UIUtil;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.work.HtmlGetter;

public class GameAppraisalAdapter extends AbstractListAdapter<ActivityInfo>{

	private DisplayImageOptions options;

	public GameAppraisalAdapter(Context context,int item_layout_default_icon) {
		super(context);
		options = ImageLoaderHelper.getCustomOption(false, item_layout_default_icon);
	}

	static class Holder {
		RoundCornerImageView icon;
		TextView title;
		TextView date;
		TextView content;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		Holder holder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.square_appraisal_list_item, parent, false);
			holder = new Holder();
			holder.title = (TextView) view.findViewById(R.id.list_title);
			holder.date = (TextView) view.findViewById(R.id.list_date);
			holder.content = (TextView) view.findViewById(R.id.list_content);
			holder.icon = (RoundCornerImageView) view.findViewById(R.id.list_icon);
			boolean[] enabled = { true, false ,false,true};
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
		ActivityInfo item = getItem(position);
		holder.title.setText(item.getTitle());
		long time = item.getTime();
		holder.date.setText(DateUtil.formatDate(new Date(time)));
		holder.content.setText(Html.fromHtml(StringUtil.convertEscapeStringWithNoCharacter(item.getContent()), new HtmlGetter(context, holder.content), null));
		holder.icon.setImageUrl(item.getGameIcon());
	}

	public int getProgressValue(long total, long current) {
		if (total <= 0)
			return 0;
		return (int) (100L * current / total);
	}
}
