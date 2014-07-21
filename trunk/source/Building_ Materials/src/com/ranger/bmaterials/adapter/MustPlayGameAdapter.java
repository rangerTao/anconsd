package com.ranger.bmaterials.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.ui.RoundCornerImageView;
import com.ranger.bmaterials.view.GameLabelView;
import com.ranger.bmaterials.view.PinnedSectionListView.PinnedSectionListAdapter;

public class MustPlayGameAdapter extends BaseAdapter implements OnClickListener, PinnedSectionListAdapter {
	public static final int LISTTYPE_ITEM_SECTION = 0;
	public static final int LISTTYPE_ITEM_GAME = 1;

	private List<SearchItem> mListGameInfo = null;
	private Context context = null;
	private LayoutInflater inflater = null;
	private int sectionColors[] = { R.drawable.mustplay_section_color1, R.drawable.mustplay_section_color2, R.drawable.mustplay_section_color3 };
	public List<String> sectionString = null;

	public interface OnGameItemClickListener {
		void onGameItemClick(View view, int position);
	}

	private OnGameItemClickListener onClickListener;

	public void setOnClickListener(OnGameItemClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public MustPlayGameAdapter(Context context, List<SearchItem> gameList) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mListGameInfo = gameList;
	}

	static class Holder {
		RoundCornerImageView icon;
		TextView title;
		TextView subTitle;
		ImageView comingIv;
		ImageView actionIv;
		TextView actionTv;
		GameLabelView labeview;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		Holder holder;

		boolean isSectionView = false;
		if (StringUtil.isEmpty(mListGameInfo.get(position).getGameId())) {
			isSectionView = true;
		}

		if (convertView == null) {
			holder = new Holder();
			if (isSectionView) {
				view = inflater.inflate(R.layout.must_play_list_item_section, parent, false);
				holder.title = (TextView) view.findViewById(R.id.label_section_title);
			} else {
				view = inflater.inflate(R.layout.must_play_list_item_game, parent, false);
				holder.title = (TextView) view.findViewById(R.id.game_name);
				holder.subTitle = (TextView) view.findViewById(R.id.game_name_des);
				holder.icon = (RoundCornerImageView) view.findViewById(R.id.game_icon);
				holder.actionIv = (ImageView) view.findViewById(R.id.search_item_action_iv);
				holder.actionTv = (TextView) view.findViewById(R.id.search_item_action_tv);
				holder.comingIv = (ImageView) view.findViewById(R.id.search_item_comingsoon_iv);
				holder.labeview = (GameLabelView) view.findViewById(R.id.app_item_card_label_name);

				View p = (View) holder.actionIv.getParent();
				p.setOnClickListener(this);
			}
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (Holder) view.getTag();
		}

		if (isSectionView) {
			holder.title.setText(mListGameInfo.get(position).getGameName());

			for (int i = 0; i < sectionString.size(); i++) {
				if (sectionString.get(i).equals(mListGameInfo.get(position).getGameName())) {
					// holder.title.setBackgroundColor(sectionColors[i % 3]);
					holder.title.setBackgroundResource(sectionColors[i % 3]);
					break;
				}
			}
		} else {
			View p = (View) holder.actionIv.getParent();
			p.setTag(position);
			updateView(view, position, holder);
		}

		return view;
	}

	@Override
	public boolean isEnabled(int position) {
		return super.isEnabled(position);
	}

	@Override
	public int getItemViewType(int position) {
		if (StringUtil.isEmpty(mListGameInfo.get(position).getGameId())) {
			return LISTTYPE_ITEM_SECTION;
		}

		return LISTTYPE_ITEM_GAME;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return mListGameInfo.size();
	}

	@Override
	public SearchItem getItem(int position) {
		return mListGameInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void refreshAllItemsView(ListView listView) {
		int firstVisiblePosition = listView.getFirstVisiblePosition();

		int childCount = listView.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = listView.getChildAt(i);
			int index = i + firstVisiblePosition;

			if (index > 0) {
				SearchItem item = getItem(index);

				if (!StringUtil.isEmpty(item.getGameId())) {
					this.updateItemView(childView, item);
				}
			}
		}
	}

	public void updateItemView(ListView listView, String gameId) {

		int targetPos = -1;
		SearchItem targetItem = null;
		int count = getCount();

		for (int i = 0; i < count; i++) {
			SearchItem item = getItem(i);
			if (item.getGameId() != null && item.getGameId().equals(gameId)) {
				targetPos = i;
				targetItem = item;
				break;
			}
		}

		if (targetPos == -1) {
			return;
		}

		int firstVisiblePosition = listView.getFirstVisiblePosition();
		if ((targetPos - firstVisiblePosition) < 0) {
			return;
		}
		View childView = listView.getChildAt(targetPos - firstVisiblePosition);

		if (childView != null) {
			updateItemView(childView, targetItem);
		}
	}

	private void updateItemView(View view, SearchItem item) {
		if (StringUtil.isEmpty(item.getGameId())) {
			return;
		}

		ImageView actionIv = (ImageView) view.findViewById(R.id.search_item_action_iv);
		TextView actionTv = (TextView) view.findViewById(R.id.search_item_action_tv);

		View p = (View) actionIv.getParent();
		Integer position = (Integer) p.getTag();
		String downloadUrl = getItem(position).getDownloadUrl();

		if (downloadUrl.equals(item.getDownloadUrl())) {
			updateDownloadStatus(item, actionIv, actionTv);
		}
	}

	private void updateDownloadStatus(SearchItem item, ImageView actionIv, TextView actionTv) {
		View p = (View) actionIv.getParent();
		p.setEnabled(true);
		boolean diffUpdate = item.isDiffDownload();
		int progressValue = 0;
		int apkStatus = item.getApkStatus();
		String formatString = "%d%%";

		switch (apkStatus) {
		case PackageMode.UNDOWNLOAD:
			// actionTv.setVisibility(View.GONE);
			actionTv.setText(R.string.download);
			actionIv.setImageResource(R.drawable.btn_download_selector);
			break;
		case PackageMode.INSTALLED:
			actionTv.setText(R.string.open);
			actionIv.setImageResource(R.drawable.btn_download_launch_selector);
			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.UPDATABLE:
			actionTv.setText(R.string.update);
			actionIv.setImageResource(R.drawable.btn_download_update_selector);
			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.UPDATABLE_DIFF:
			actionTv.setText(R.string.update_diff);
			actionIv.setImageResource(R.drawable.btn_download_diff_update_selector);
			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.DOWNLOAD_PENDING:
			actionIv.setImageResource(R.drawable.btn_download_pending_selector);
			actionTv.setText(R.string.label_waiting);
			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.DOWNLOAD_RUNNING:
			actionIv.setImageResource(R.drawable.btn_download_pause_selector);
			progressValue = getProgressValue(item.getTotalBytes(), item.getCurrentBytes());
			actionTv.setText(String.format(formatString, progressValue));
			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.DOWNLOAD_FAILED:
			actionIv.setImageResource(R.drawable.btn_download_retry_selector);
			actionTv.setText(R.string.try_again);
			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.DOWNLOAD_PAUSED:
			actionIv.setImageResource(R.drawable.btn_download_resume_selector);
			actionTv.setText(R.string.resume);

			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.DOWNLOADED:
			actionIv.setImageResource(R.drawable.btn_download_resume_selector);
			actionTv.setText(R.string.install);
			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.MERGING:
			// 合并中
			p.setEnabled(false);
			actionIv.setImageResource(R.drawable.icon_checking_list);
			actionTv.setText("安全检查中");
			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.MERGE_FAILED:
			// 合并失败，重新普通更新下载(要给出提示,不能删除原来的，更新即可)
			actionIv.setImageResource(R.drawable.btn_download_retry_selector);
			actionTv.setText(R.string.try_again);
			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.MERGED:
			// 合并成功，并且签名一致（后台自动安装）
			actionIv.setImageResource(R.drawable.btn_download_retry_selector);
			actionTv.setText(R.string.install);
			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.CHECKING:
			p.setEnabled(false);
			actionIv.setImageResource(R.drawable.icon_checking_list);
			actionTv.setText("安全检查中");
			actionTv.setVisibility(View.VISIBLE);
			break;

		case PackageMode.CHECKING_FINISHED:
			actionIv.setImageResource(R.drawable.btn_download_install_selector);
			actionTv.setText(R.string.install);
			actionTv.setVisibility(View.VISIBLE);
			break;

		case PackageMode.INSTALLING:
			p.setEnabled(false);
			actionIv.setImageResource(R.drawable.installing);
			actionTv.setText(R.string.installing);
			actionTv.setVisibility(View.VISIBLE);
			break;
		case PackageMode.INSTALL_FAILED:
			actionIv.setImageResource(R.drawable.btn_download_install_selector);
			actionTv.setText(R.string.install);
			actionTv.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

	}

	private void setDefaultViewStatus(TextView titleText, TextView subtitleText, ImageView actionIv, TextView actionTv, SearchItem item) {

		View p = (View) actionIv.getParent();
		if (!p.isEnabled()) {
			p.setEnabled(true);
		}
		actionIv.setImageResource(R.drawable.btn_download_selector);
		actionTv.setText(R.string.download);
		titleText.setText(item.getGameName());
		subtitleText.setText(item.getGameNameDes());
	}

	private void updateView(View view, int position, Holder holder) {

		SearchItem item = (SearchItem) getItem(position);
		ImageLoaderHelper.displayImage(item.getIconUrl(), holder.icon);
		/**
		 * 默认的情形
		 */
		setDefaultViewStatus(holder.title, holder.subTitle, holder.actionIv, holder.actionTv, item);

		boolean pendingOnLine = item.isPendingOnLine();
		/**
		 * 即将上线的情形
		 */
		if (pendingOnLine) {
			View p = (View) holder.actionIv.getParent();
			p.setVisibility(View.GONE);
			holder.comingIv.setVisibility(View.VISIBLE);
			return;
		}

		updateDownloadStatus(item, holder.actionIv, holder.actionTv);

		// holder.labeview.setLabelColor("#F86D22");
		// holder.labeview.setText("首发");
		// holder.labeview.setVisibility(View.VISIBLE);
		if (item.labelName != null && !item.labelName.equals("")) {
			holder.labeview.setText(item.labelName);
			holder.labeview.setLabelColor(item.labelColor);
			holder.labeview.setVisibility(View.VISIBLE);
		} else {
			holder.labeview.setVisibility(View.GONE);
		}
	}

	public int getProgressValue(long total, long current) {
		if (total <= 0)
			return 0;
		return (int) (100L * current / total);
	}

	@Override
	public void onClick(View v) {
		if (onClickListener == null) {
			return;
		}

		onClickListener.onGameItemClick(v, (Integer) v.getTag());
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == LISTTYPE_ITEM_SECTION;
	}
}
