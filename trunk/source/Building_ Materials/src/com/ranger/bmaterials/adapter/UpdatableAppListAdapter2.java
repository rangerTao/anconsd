package com.ranger.bmaterials.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.UpdatableAppInfo;
import com.ranger.bmaterials.tools.AppUtil;

public class UpdatableAppListAdapter2 extends AbstractListAdapter<UpdatableAppInfo> implements OnClickListener{

	private static final String TAG = "UpdatableAppListAdapter";
	private PopupWindow pw;

	public UpdatableAppListAdapter2(Context context) {
		super(context);
	}

	static class AppInfoViewHolder {
		TextView title;
		TextView version;
		TextView size;
		ImageView icon;
		Button button;
		TextView newVersion;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		AppInfoViewHolder appInfoView;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.manager_activity_updatable_list_item2,
					parent, false);
			appInfoView = new AppInfoViewHolder();
			appInfoView.title = (TextView) view
					.findViewById(R.id.manager_activity_updatable_list_item_name);
			appInfoView.icon = (ImageView) view
					.findViewById(R.id.manager_activity_updatable_list_item_icon);
			appInfoView.version = (TextView) view
					.findViewById(R.id.manager_activity_updatable_list_item_version);
			appInfoView.size = (TextView) view
					.findViewById(R.id.manager_activity_updatable_list_item_size);
			
			appInfoView.newVersion = (TextView) view.findViewById(R.id.manager_activity_updatable_list_item_version_new);
			//appInfoView.icon.setOnClickListener(this);
			
			appInfoView.button = (Button) view.findViewById(R.id.manager_activity_updatable_list_item_update_button);
			appInfoView.button.setText("更新");
			appInfoView.button.setOnClickListener(this);
			
			view.setTag(appInfoView);
		} else {
			view = convertView;
			appInfoView = (AppInfoViewHolder) view.getTag();
		}
		View button = view.findViewById(R.id.manager_activity_updatable_list_item_update_button);
		//update buttton
		button.setTag(position);
		//app icon
		appInfoView.icon.setTag(position);
		
		UpdatableAppInfo item = getItem(position);
		appInfoView.title.setText(item.getName());
		String version = item.getVersion();
		String newVersion = item.getNewVersion();
		if(version != null && !version.equals(newVersion)){
			appInfoView.version.setText(version + " ");
			appInfoView.newVersion.setText(" " +newVersion);
		}else{
			appInfoView.version.setText(item.getVersionInt() + " ");
			appInfoView.newVersion.setText(" " +item.getNewVersionInt());
		}
		
		appInfoView.size.setText(Formatter.formatFileSize(context, item.getNewSize()));
		ImageLoaderHelper.displayImage(item.getIconUrl(), appInfoView.icon);
		return view;
	}
	
	
	//icon and button's click callback.
	@Override
	public void onClick(View v) {
		if(onListItemClickListener == null){
			return ;
		}
		if(v instanceof Button){
			onListItemClickListener.onItemButtonClick(v,(Integer) v.getTag());
		}else if(v instanceof ImageView){
			onListItemClickListener.onItemIconClick(v,(Integer) v.getTag());
		}
	}
	class MyFilter extends  Filter {
		
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			
		}
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			return null;
		}
	}
}
