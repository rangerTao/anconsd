package com.ranger.bmaterials.ui;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.tools.StringUtil;

public class MineGamesAdapter extends BaseAdapter {

	public static final int LISTTYPE_DOWNLOADED_GAMES = 0;
	public static final int LISTTYPE_COLLECTED_GAMES = 1;

	private List<MineGameItemInfo> mListGameInfo = null;
	private LayoutInflater inflater = null;
	private int mListType = LISTTYPE_DOWNLOADED_GAMES;
	private Context context;

	public MineGamesAdapter(Context context, List<MineGameItemInfo> gameList,
			int listType) {
		this.context = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mListGameInfo = gameList;
		this.mListType = listType;
	}

	@Override
	public int getCount() {
		return mListGameInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return mListGameInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (mListType == LISTTYPE_DOWNLOADED_GAMES) {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.mine_downloaded_game_listview_item, parent,
						false);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.img_mine_listview_icon);
				holder.title = (TextView) convertView
						.findViewById(R.id.label_listview_item_title);
				holder.subtitle = (TextView) convertView
						.findViewById(R.id.label_listview_item_subtitle);
				holder.mark = (ImageView) convertView
						.findViewById(R.id.img_mine_listview_mark);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			MineGameItemInfo itemInfo = mListGameInfo.get(position);
			ImageLoaderHelper.displayImage(itemInfo.gameurl, holder.icon);
			holder.title.setText(itemInfo.gameName);
			// holder.subtitle.setText(itemInfo.pkgsize);

			int pkgSize = StringUtil.parseInt(itemInfo.pkgsize);
			DecimalFormat df = new DecimalFormat("#.##");
			holder.subtitle.setText(df.format(pkgSize / 1024.0 / 1024.0) + "MB");

		} else if (mListType == LISTTYPE_COLLECTED_GAMES) {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.mine_collected_game_listview_item, parent,
						false);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.img_mine_listview_icon);
				holder.title = (TextView) convertView
						.findViewById(R.id.label_listview_item_title);
				holder.subtitle = (TextView) convertView
						.findViewById(R.id.label_listview_item_subtitle);
				holder.mark = (ImageView) convertView
						.findViewById(R.id.img_mine_listview_mark);
				holder.ratingBar = (RatingBar) convertView
						.findViewById(R.id.label_listview_item_progressbar);
				holder.downloadtimes = (TextView) convertView
						.findViewById(R.id.label_listview_item_download_times);
				holder.installed = (TextView) convertView
						.findViewById(R.id.label_listview_item_installed);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			MineGameItemInfo itemInfo = mListGameInfo.get(position);
			ImageLoaderHelper.displayImage(itemInfo.gameurl, holder.icon);
			holder.title.setText(itemInfo.gameName);
			String downloadString = itemInfo.downloadTimes;
			int download = StringUtil.parseInt(downloadString);

			int d = download / 10000;
			if (d > 0) {
				holder.downloadtimes.setText("" + d + "万+次下载");
			} else {
				holder.downloadtimes.setText(downloadString + "次下载");
			}

			int pkgSize = StringUtil.parseInt(itemInfo.pkgsize);

			String sizeTextString = StringUtil.getDisplaySize(String.valueOf(pkgSize));
			holder.subtitle.setText(sizeTextString);

			float rating = 0;
			try {
				rating = Float.valueOf(itemInfo.star);
			} catch (Exception e) {
				e.printStackTrace();
			}
			holder.ratingBar.setRating(rating);

			if (isInstalled(itemInfo.pkgName)) {
				holder.installed.setVisibility(View.VISIBLE);
			} else {
				holder.installed.setVisibility(View.INVISIBLE);
			}
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView icon;
		TextView title;
		TextView subtitle;
		RatingBar ratingBar;
		ImageView mark;
		TextView downloadtimes;
		TextView installed;
	}

	private boolean isInstalled(String packagename) {
		PackageInfo packageInfo;
		try {
			packageInfo = this.context.getPackageManager().getPackageInfo(
					packagename, 0);

		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}

		if (packageInfo == null) {
			return false;
		}

		return true;
	}
}
