package com.ranger.bmaterials.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.StartGame;
import com.ranger.bmaterials.app.InternalGames.InternalInstalledGames;
import com.ranger.bmaterials.app.InternalGames.InternalStartGames;
import com.ranger.bmaterials.listener.ItemOnTouchAnimationListener;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.MyGamesInfo;
import com.ranger.bmaterials.statistics.ClickNumStatistics;

public class MyGamesLocalAdapter extends BaseAdapter implements
		Comparator<MyGamesInfo> {

	private Activity context;
	private ArrayList<MyGamesInfo> showApplist;
	private List<InstalledAppInfo> downloadedAppInfoList;
	private ArrayList<String> newCornAppList = new ArrayList<String>();// new角标的游戏

	public MyGamesLocalAdapter(Activity context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return showApplist.size();
	}

	@Override
	public MyGamesInfo getItem(int position) {
		return showApplist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyHolder holder;
//		if (convertView == null) {
//			holder = new MyHolder();
//			convertView = View.inflate(context,R.layout.item_mine_local_game, null);
//			holder.iv = (ImageView) convertView.findViewById(R.id.mine_game_icon);
//			holder.tv = (TextView) convertView.findViewById(R.id.game_name_des);
//			holder.toggleBtn = convertView.findViewById(R.id.expandable_toggle_button);
//			holder.startAction = convertView.findViewById(R.id.search_item_action_layout);
////			holder.corner_iv = (ImageView) convertView
////					.findViewById(R.id.item_gv_game_recommend_corner_iv);
//			convertView.setTag(holder);
//		} else {
//			holder = (MyHolder) convertView.getTag();
//		}
		holder = new MyHolder();
		convertView = View.inflate(context,R.layout.item_mine_local_game, null);
		holder.iv = (ImageView) convertView.findViewById(R.id.mine_game_icon);
		holder.tv = (TextView) convertView.findViewById(R.id.game_name_des);
		holder.toggleBtn = convertView.findViewById(R.id.expandable_toggle_button);
		holder.startAction = convertView.findViewById(R.id.search_item_action_layout);
//		holder.corner_iv = (ImageView) convertView
//				.findViewById(R.id.item_gv_game_recommend_corner_iv);
		convertView.setTag(holder);

		MyGamesInfo gameInfo = showApplist.get(position);
		holder.iv.setImageDrawable(gameInfo.getIcon());
		holder.tv.setText(gameInfo.getName());
		holder.toggleBtn.setTag(gameInfo.getPkgName());
//		if (newCornAppList.contains(gameInfo.getPkgName())) {
//			holder.corner_iv.setVisibility(View.VISIBLE);
//		} else
//			holder.corner_iv.setVisibility(View.INVISIBLE);

		holder.startAction.setOnTouchListener(new ItemOnTouchAnimationListener(context,
						new SmallerAnimationListener(position)));

		return convertView;
	}

	class MyHolder {
		ImageView iv;
		TextView tv;
//		ImageView corner_iv;
		View toggleBtn;
		View startAction;
	}

	private class SmallerAnimationListener implements AnimationListener {
		private int position;

		public SmallerAnimationListener(int position) {
			this.position = position;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			MyGamesInfo info = showApplist.get(position);
			String pkgName = info.getPkgName();
			StartGame isg = new StartGame(context, pkgName,
					info.getAction(), info.getGameid(), info.isNeedLogin());
			isg.startGame(true);
			ClickNumStatistics.addMyGamesStartStatistics(context,info.getName());
		}
	};

	// 最近启动的游戏排在第一个 返回是否存在我的游戏
	public synchronized boolean lastStartGameChange() {
		ArrayList<MyGamesInfo> showApplist = new ArrayList<MyGamesInfo>();
		if (downloadedAppInfoList == null) {
			// 下载列表
			AppManager am = AppManager.getInstance(context);
			downloadedAppInfoList = am.getInstalledGames();
		}

		SharedPreferences started_app_sp = InternalStartGames
				.getSharedPreferences(context);

		ArrayList<String> internalInstalledAppList = InternalInstalledGames
				.getInternalInstalledGames(context);// 应用内安装的游戏
		newCornAppList.clear();
		for (String pkgName : internalInstalledAppList) {
			if (started_app_sp.getLong(pkgName, 0) == 0) {
				// 未启动过的游戏
				newCornAppList.add(pkgName);
			}
		}

		for (InstalledAppInfo info : downloadedAppInfoList) {

			MyGamesInfo myGameInfo = new MyGamesInfo();
			myGameInfo.setName(info.getName());
			myGameInfo.setIcon(info.getDrawable());
			myGameInfo.setPkgName(info.getPackageName());
			myGameInfo.setAction(info.getExtra());
			myGameInfo.setGameid(info.getGameId());
			myGameInfo.setNeedLogin(info.isNeedLogin());
			myGameInfo.setLastStartTime(started_app_sp.getLong(
					info.getPackageName(), 0));

			showApplist.add(myGameInfo);

		}

		Collections.sort(showApplist, this);

		this.showApplist = showApplist;
		return showApplist.isEmpty();
	}

	@Override
	public int compare(MyGamesInfo lhs, MyGamesInfo rhs) {
		// TODO Auto-generated method stub
		long lLastStartTime = lhs.getLastStartTime();
		long rLastStartTime = rhs.getLastStartTime();
		if (lLastStartTime > rLastStartTime)
			return -1;
		else if (lLastStartTime < rLastStartTime)
			return 1;
		else
			return 0;

	}

}
