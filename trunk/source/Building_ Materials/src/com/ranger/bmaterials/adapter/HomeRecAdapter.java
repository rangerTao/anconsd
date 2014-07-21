package com.ranger.bmaterials.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.bitmap.ImageLoaderHelper;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.SearchResult.SearchItem;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.ui.GameDetailsActivity;
import com.ranger.bmaterials.ui.RoundCornerImageView;

public class HomeRecAdapter extends BaseAdapter implements OnClickListener {
	private LayoutInflater mInflater;
	private Activity context;
	private ArrayList<SearchItem> gameInfos;

	public interface OnGameClickListener {
		void onGameClick(View view, int position);
	}

	private OnGameClickListener onClickListener;

	public void setOnClickListener(OnGameClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	public HomeRecAdapter(Activity context, ArrayList<SearchItem> gameInfos) {
		this.gameInfos = gameInfos;
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		if (gameInfos.size() >= 8) {
			return 8;
		} else {
			return gameInfos.size();
		}
	}

	@Override
	public SearchItem getItem(int position) {
		return gameInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();

			convertView = mInflater.inflate(R.layout.item_home_rec_games, null);

			holder.iv_rec_games_icon = (RoundCornerImageView) convertView.findViewById(R.id.iv_rec_games_icon);
			holder.tv_rec_games_name = (TextView) convertView.findViewById(R.id.tv_rec_games_name);
			holder.tv_rec_games_status = (TextView) convertView.findViewById(R.id.tv_rec_games_status);
			holder.tv_rec_games_status.setOnClickListener(this);
			holder.tv_rec_games_status.setTag(position);
			holder.iv_rec_games_icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 跳转到详情页
					SearchItem gameInfo = gameInfos.get(position);
					Intent intent = new Intent(context, GameDetailsActivity.class);
					intent.putExtra("gameid", gameInfo.getGameId());
					context.startActivity(intent);
					context.finish();
					ClickNumStatistics.addRecDiaDetailButtonClickStatis(context, gameInfo.getGameName());
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		SearchItem gameInfo = gameInfos.get(position);
		ImageLoaderHelper.displayImage(gameInfo.getIconUrl(), holder.iv_rec_games_icon, ImageLoaderHelper.getCustomOption(R.drawable.game_icon_games_default));
		holder.tv_rec_games_name.setText(gameInfo.getGameName());

		updateDownloadStatus(gameInfo, holder.tv_rec_games_status);
		return convertView;
	}

	class Holder {
		RoundCornerImageView iv_rec_games_icon;
		TextView tv_rec_games_name;
		TextView tv_rec_games_status;
	}

	public void refreshAllItemsView(GridView listView) {
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

	public void updateItemView(GridView listView, String gameId) {
		if (listView == null || gameId == null) {
			return;
		}
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

		TextView actionTv = (TextView) view.findViewById(R.id.tv_rec_games_status);

		Integer position = (Integer) actionTv.getTag();
		String downloadUrl = getItem(position).getDownloadUrl();

		if (downloadUrl.equals(item.getDownloadUrl())) {
			updateDownloadStatus(item, actionTv);
		}
	}

	// 用于更新下载状态
	private void updateDownloadStatus(SearchItem item, TextView actionTv) {

		boolean diffUpdate = item.isDiffDownload();
		int progressValue = 0;
		int apkStatus = item.getApkStatus();
		String formatString = "%d%%";

		switch (apkStatus) {
		case PackageMode.UNDOWNLOAD:
			actionTv.setText("下载");
			break;
		case PackageMode.INSTALLED:
			actionTv.setText(R.string.open);

			break;
		case PackageMode.UPDATABLE:
			actionTv.setText(R.string.update);

			break;
		case PackageMode.UPDATABLE_DIFF:
			actionTv.setText(R.string.update_diff);

			break;
		case PackageMode.DOWNLOAD_PENDING:
			actionTv.setText(R.string.label_waiting);

			break;
		case PackageMode.DOWNLOAD_RUNNING:
			progressValue = getProgressValue(item.getTotalBytes(), item.getCurrentBytes());
			actionTv.setText(String.format(formatString, progressValue));

			break;
		case PackageMode.DOWNLOAD_FAILED:
			actionTv.setText(R.string.try_again);
			break;
		case PackageMode.DOWNLOAD_PAUSED:
			actionTv.setText(R.string.resume);

			break;
		case PackageMode.DOWNLOADED:
			actionTv.setText(R.string.install);
			break;
		case PackageMode.MERGING:
			// 合并中
			actionTv.setText("安全检查中");
			break;
		case PackageMode.MERGE_FAILED:
			// 合并失败，重新普通更新下载(要给出提示,不能删除原来的，更新即可)
			actionTv.setText(R.string.try_again);

			break;
		case PackageMode.MERGED:
			// 合并成功，并且签名一致（后台自动安装）
			actionTv.setText(R.string.install);

			break;
		case PackageMode.CHECKING:
			actionTv.setText("安全检查中");
			break;

		case PackageMode.CHECKING_FINISHED:
			actionTv.setText(R.string.install);

			break;

		case PackageMode.INSTALLING:
			actionTv.setText(R.string.installing);
			break;
		case PackageMode.INSTALL_FAILED:
			actionTv.setText(R.string.install);
			break;
		default:
			break;
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
		onClickListener.onGameClick(v, (Integer) v.getTag());
	}
}
