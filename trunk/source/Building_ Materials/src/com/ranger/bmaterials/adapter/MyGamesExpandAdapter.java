package com.ranger.bmaterials.adapter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageStats;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.AppManager;
import com.ranger.bmaterials.app.GameDetailConstants;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.app.StartGame;
import com.ranger.bmaterials.app.InternalGames.InternalInstalledGames;
import com.ranger.bmaterials.app.InternalGames.InternalStartGames;
import com.ranger.bmaterials.mode.ActivityInfo;
import com.ranger.bmaterials.mode.GameRelatedInfo;
import com.ranger.bmaterials.mode.InstalledAppInfo;
import com.ranger.bmaterials.mode.MyGamesInfo;
import com.ranger.bmaterials.netresponse.BaseResult;
import com.ranger.bmaterials.netresponse.GameRelatedResult;
import com.ranger.bmaterials.statistics.ClickNumStatistics;
import com.ranger.bmaterials.tools.AppUtil;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.Logger;
import com.ranger.bmaterials.utils.NetUtil;
import com.ranger.bmaterials.tools.StringUtil;
import com.ranger.bmaterials.utils.NetUtil.IRequestListener;
import com.ranger.bmaterials.tools.install.PackageUtils;
import com.ranger.bmaterials.ui.ActivityDetailActivity;
import com.ranger.bmaterials.ui.AppraisalDetailActivity;
import com.ranger.bmaterials.ui.GameDetailsActivity;
import com.ranger.bmaterials.ui.GameGuideDetailActivity2;
import com.ranger.bmaterials.ui.OpenServerDetailActivity;
import com.ranger.bmaterials.ui.SnapNumberDetailActivity;
import com.ranger.bmaterials.ui.SquareDetailBaseActivity;
import com.ranger.bmaterials.view.slideexpand.ExpandCollapseAnimation;

public class MyGamesExpandAdapter extends BaseAdapter implements
		Comparator<MyGamesInfo> {

	private final static String TAG = "MyGamesExpandAdapter";
	
	private Activity context;
	private ArrayList<MyGamesInfo> showApplist;
	private List<InstalledAppInfo> downloadedAppInfoList;
	private ArrayList<String> newCornAppList = new ArrayList<String>();// new角标的游戏

	private final SparseIntArray viewHeights = new SparseIntArray(10);
	private BitSet openItems = new BitSet();

	/**存放message和view的kv关系，用来异步得到包大小后刷新view*/
	HashMap<Message, TextView> kvMap = new HashMap<Message, TextView>();
//	private MyExpandListView pullUpLv;
	/**
	 * 最后一次展开的item
	 */
	private View lastOpen = null;
	private View lastOpenTogBtn;
	private View lastOpenBanner;
	/**
	 * 最后一次打开的item的position
	 * -1表示当前没有打开的item，否则就是指向当前打开的item
	 */
	private int lastOpenPosition = -1;
	
	/**
	 * 动画默认的执行时间
	 */
	private int animationDuration = 330;
	private boolean lastColorWhite = true;

	public MyGamesExpandAdapter(Activity context) {
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

	OnClickListener itemClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View itemView) {
			if (itemView.getId() == R.id.mine_item5) {
				String pkgname = (String)itemView.getTag();
				Intent i = new Intent(context, GameDetailsActivity.class);
				i.putExtra(GameDetailConstants.KEY_GAME_PACKAGE_NAME, pkgname);
				context.startActivity(i);
				return;
			}
			GameRelatedInfo info = (GameRelatedInfo)itemView.getTag();
			
			int type = Integer.parseInt(info.getInfoType());
			
			final String id = info.getInfoId();

			ClickNumStatistics.addJump2RelatedInfoClickStatis(context,id);
//			final String id = "266829";
			// 跳转至对应的页面
			Intent intent = new Intent();
			switch (Integer.valueOf(type)) {
			// 0表示攻略，1表示测评，2表示资讯，3表示活动，4表示抢号，5表示开服
			case 0:
				// 跳转到攻略详情
				intent.setClass(context, GameGuideDetailActivity2.class);
				intent.putExtra("guideid", id);
				break;
			case 1:
				// 跳转到测评详情
				intent.setClass(context, AppraisalDetailActivity.class);
				intent.putExtra(SquareDetailBaseActivity.ARG_DETAIL, new ActivityInfo("", id, "", "", 0));
				intent.putExtra("arg_page", 0);
				break;
			case 2:
				// 跳转到资讯详情
				intent.setClass(context, AppraisalDetailActivity.class);
				intent.putExtra(SquareDetailBaseActivity.ARG_DETAIL, new ActivityInfo("", id, "", "", 0));
				intent.putExtra("arg_page", 1);
				break;
			case 3:
				// 跳转到活动详情
				intent.setClass(context, ActivityDetailActivity.class);
				ActivityInfo actinfo=new ActivityInfo();
				actinfo.setId(id);
//				actinfo.setGameId(gameid);
				intent.putExtra(SquareDetailBaseActivity.ARG_DETAIL, actinfo);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
				break;
			case 4:
				// 跳转到抢号详情
				intent.setClass(context, SnapNumberDetailActivity.class);
				MineProfile profile = MineProfile.getInstance();
				String userID = profile.getUserID();
				String sessionID = profile.getSessionID();
				boolean isLogin = profile.getIsLogin();
				if (isLogin) {
//					intent.putExtra(SnapNumberDetailActivity.ARG_GAMEID, gameid);
					intent.putExtra(SnapNumberDetailActivity.ARG_GRABID, id);
					intent.putExtra(SnapNumberDetailActivity.ARG_USERID, userID);
					intent.putExtra(SnapNumberDetailActivity.ARG_SESSIONID, sessionID);
				} else {
//					intent.putExtra(SnapNumberDetailActivity.ARG_GAMEID, gameid);
					intent.putExtra(SnapNumberDetailActivity.ARG_GRABID, id);
				}
				break;
			case 5:
				// 跳转到开服详情
				intent.setClass(context, OpenServerDetailActivity.class);
//				intent.putExtra(OpenServerDetailActivity.ARG_GAME_ID, gameid);
				intent.putExtra(OpenServerDetailActivity.ARG_OPENSERVER_ID, id);
				break;
			default:
				intent = null;
				break;
			}
			if (intent != null) {
				context.startActivity(intent);				
			} else {
//				Toast.makeText(context, String.valueOf(type), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
//	@Override
//	public void notifyDataSetChanged() {
//		pullUpLv.setMeasure(true);
//		super.notifyDataSetChanged();
//	}
//
//	public void setListView(MyExpandListView listview) {
//		this.pullUpLv = listview;
//	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			if (kvMap.containsKey(msg)) {
				TextView ttview = kvMap.get(msg);
				String infoString = "";
				PackageStats newPs = msg.getData().getParcelable(PackageUtils.ATTR_PACKAGE_STATS);
				if (newPs != null) {
					infoString = StringUtil.getDisplaySize(newPs.codeSize+newPs.dataSize+newPs.cacheSize);
				}
				ttview.setText("大小:"+infoString);
			}
		};
	};

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final MyHolder holder;
		final View item_toolbar;
		if (convertView == null) {
			holder = new MyHolder();
			convertView = View.inflate(context,R.layout.item_mine_local_game, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.mine_game_icon);
			holder.tv = (TextView) convertView.findViewById(R.id.game_name_des);
			holder.size = (TextView)convertView.findViewById(R.id.game_size);
			holder.gamename = (TextView) convertView.findViewById(R.id.mine_game_name);
			holder.toggleBtn = convertView.findViewById(R.id.expandable_toggle_button);
			holder.toggle_img = (ImageView) convertView.findViewById(R.id.toggle_img);
			holder.startAction = convertView.findViewById(R.id.search_item_action_layout);
			holder.item_toolbar = convertView.findViewById(R.id.expandable);
			holder.item_head = convertView.findViewById(R.id.item_head);
			convertView.setTag(holder);
		} else {
			holder = (MyHolder) convertView.getTag();
		}
		item_toolbar = holder.item_toolbar;

		if (position >= showApplist.size()) {
			return convertView;
		}
		MyGamesInfo gameInfo = showApplist.get(position);
		holder.iv.setImageDrawable(gameInfo.getIcon());
		holder.gamename.setText(gameInfo.getName());
		holder.toggleBtn.setTag(gameInfo.getPkgName());
		
		PackageUtils pkgUtils = new PackageUtils();
		try {
//			PackageSizeInfo sizeInfo = pkgUtils.queryInstalledPacakgeSize(context,gameInfo.getPkgName());
//			String infoString = "0";
//			if (sizeInfo != null) {
//				infoString = StringUtil.getDisplaySize(sizeInfo.getCacheSize()+sizeInfo.getDataSize()+sizeInfo.getCacheSize());
//			}
//			holder.size.setText("大小:"+infoString);
			
			Message msg = mHandler.obtainMessage();
			pkgUtils.getpkginfo(context, gameInfo.getPkgName(),msg);
			kvMap.put(msg, holder.size);
			
			PackageInfo pkgInfo = AppUtil.getPacakgeInfo(context, gameInfo.getPkgName());
			if (pkgInfo != null) {
				holder.tv.setText("版本号:"+pkgInfo.versionName);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		item_toolbar.measure(parent.getWidth(), parent.getHeight());
		int height = viewHeights.get(position, -1);
		if(height == -1) {
			viewHeights.put(position, item_toolbar.getMeasuredHeight());
			updateExpandable(item_toolbar,position,item_toolbar.getMeasuredHeight());
		} else {
			updateExpandable(item_toolbar, position,height);
		}

		OnClickListener ontoggleListener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ClickNumStatistics.addGameRelatedInfoClickStatis(context);
				if (lastOpenPosition == position) {
					holder.item_head.setBackgroundResource(R.drawable.list_card_item_selector);
					collapseLastOpen();
					return;
				} else {
					collapseLastOpen();
				}
				
				int isExpand = item_toolbar.getVisibility() == View.VISIBLE ? ExpandCollapseAnimation.COLLAPSE : ExpandCollapseAnimation.EXPAND;
				if (isExpand == ExpandCollapseAnimation.EXPAND) {
					holder.toggle_img.setBackgroundResource(R.drawable.fold_collapse);
					holder.item_head.setBackgroundResource(R.drawable.mine_item_expand_head);
				} else {
					holder.toggle_img.setBackgroundResource(R.drawable.fold_normal);
				}
				item_toolbar.getParent().requestLayout();
				if (!ConnectManager.isNetworkConnected(context)) {
					item_toolbar.findViewById(R.id.error_hint).setVisibility(View.VISIBLE);
				} else {
					item_toolbar.findViewById(R.id.network_loading).setVisibility(View.VISIBLE);
					item_toolbar.findViewById(R.id.error_hint).setVisibility(View.GONE);
					final String pkgname = (String) holder.toggleBtn.getTag();
					
					NetUtil.getInstance().requestRelatedGameInfo(pkgname, new IRequestListener() {
						
						@Override
						public void onRequestSuccess(BaseResult resData) {
							item_toolbar.findViewById(R.id.network_loading).setVisibility(View.GONE);
							item_toolbar.findViewById(R.id.item_related_info).setVisibility(View.VISIBLE);
							GameRelatedResult gameInfoResult = (GameRelatedResult) resData;
							ArrayList<GameRelatedInfo> infoList = gameInfoResult.getGamesList();
							int infosize = infoList.size();

							View view1 = item_toolbar.findViewById(R.id.mine_item1);
							View view2 = item_toolbar.findViewById(R.id.mine_item2);
							View view3 = item_toolbar.findViewById(R.id.mine_item3);
							View view4 = item_toolbar.findViewById(R.id.mine_item4);
							view1.setOnClickListener(itemClickListener);
							view2.setOnClickListener(itemClickListener);
							view3.setOnClickListener(itemClickListener);
							view4.setOnClickListener(itemClickListener);
							
							int MODE = View.GONE;
							switch (infosize) {
							case 0:
								view1.setVisibility(MODE);
							case 1:
								view2.setVisibility(MODE);
							case 2:
								view3.setVisibility(MODE);
							case 3:
								view4.setVisibility(MODE);
							default:
								break;
							}

							int itemsHeight = 0;
							for (int i = 0; i < infosize; i++) {
								GameRelatedInfo info = infoList.get(i);
								int resId = getResIdByType(info.getInfoType());
								switch (i) {
								case 0:
									setBackground(view1,infosize,1);
									ImageView iView1 = (ImageView) item_toolbar.findViewById(R.id.mine_item1_icon);
									TextView tView1 = (TextView) item_toolbar.findViewById(R.id.mine_item1_desc);
									iView1.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), resId));
									tView1.setText(info.getInfocontent());
									view1.setTag(info);
									itemsHeight +=view1.getMeasuredHeight();
									break;
								case 1:								
									setBackground(view2,infosize,2);
									ImageView iView2 = (ImageView) item_toolbar.findViewById(R.id.mine_item2_icon);
									TextView tView2 = (TextView) item_toolbar.findViewById(R.id.mine_item2_desc);
									iView2.setImageResource(resId);
									tView2.setText(info.getInfocontent());
									view2.setTag(info);
									itemsHeight +=view2.getMeasuredHeight();
									break;
								case 2:
									setBackground(view3,infosize,3);
									ImageView iView3 = (ImageView) item_toolbar.findViewById(R.id.mine_item3_icon);
									TextView tView3 = (TextView) item_toolbar.findViewById(R.id.mine_item3_desc);								
									iView3.setImageResource(resId);
									tView3.setText(info.getInfocontent());
									itemsHeight +=view3.getMeasuredHeight();
									view3.setTag(info);
									break;
								case 3:
									setBackground(view4,infosize,4);
									ImageView iView4 = (ImageView) item_toolbar.findViewById(R.id.mine_item4_icon);
									TextView tView4 = (TextView) item_toolbar.findViewById(R.id.mine_item4_desc);								
									iView4.setImageResource(resId);
									tView4.setText(info.getInfocontent());
									view4.setTag(info);
									itemsHeight +=view4.getMeasuredHeight();
									break;
								default:
									break;
								}
							}						
							item_toolbar.measure(parent.getWidth(), itemsHeight);
							viewHeights.put(position, itemsHeight);
							updateExpandable(item_toolbar, position,itemsHeight);
							if (infosize <= 0) {
								Toast.makeText(context, "没有该游戏相关的信息.", Toast.LENGTH_SHORT).show();
								holder.toggle_img.setBackgroundResource(R.drawable.fold_normal);
								holder.item_head.setBackgroundResource(R.drawable.list_card_item_selector);
								item_toolbar.setVisibility(View.GONE);
								lastOpen = null;
								lastOpenPosition = -1;
							}							
							notifyDataSetChanged();
						}
						
						@Override
						public void onRequestError(int requestTag, int requestId, int errorCode,
								String msg) {
							Logger.d(TAG, "Request item relatedInfo.errorCode="+errorCode+",msg="+msg);
						}
					});
				}
				
				item_toolbar.setAnimation(null);

				int type = item_toolbar.getVisibility() == View.VISIBLE ? ExpandCollapseAnimation.COLLAPSE : ExpandCollapseAnimation.EXPAND;
				// remember the state
				if (type == ExpandCollapseAnimation.EXPAND) {
					openItems.set(position, true);
				} else {
					openItems.set(position, false);
				}
				// check if we need to collapse a different view
				if (type == ExpandCollapseAnimation.EXPAND) {
					lastOpen = item_toolbar;
					lastOpenTogBtn = holder.toggle_img;
					lastOpenBanner = holder.item_head;
					lastOpenPosition = position;
				} else if (lastOpenPosition == position) {
					lastOpenPosition = -1;
				}
				animateView(item_toolbar, type);
			}
		};
		holder.toggleBtn.setOnClickListener(ontoggleListener);
		holder.item_head.setOnClickListener(ontoggleListener);
//		holder.startAction.setOnTouchListener(new ItemOnTouchAnimationListener(context,
//						new SmallerAnimationListener(position)));
		holder.startAction.setOnTouchListener(new StartGameOnTouchListener(position));
		return convertView;
	}

	private class StartGameOnTouchListener implements OnTouchListener {
		
		private int gamePos;
		
		public StartGameOnTouchListener(int gameIndex) {
			gamePos = gameIndex;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:				
				MyGamesInfo info = showApplist.get(gamePos);
				String pkgName = info.getPkgName();
				StartGame isg = new StartGame(context, pkgName,
						info.getAction(), info.getGameid(), info.isNeedLogin());
				isg.startGame(true);
				ClickNumStatistics.addMyGamesStartStatistics(context,info.getName());
				break;
			case MotionEvent.ACTION_CANCEL:
				break;
			}
			return true;
		}
	}
	
	/**
	 * Closes the current open item.
	 * If it is current visible it will be closed with an animation.
	 *
	 * @return true if an item was closed, false otherwise
	 */
	public boolean collapseLastOpen() {
		if(isAnyItemExpanded()) {
			// if visible animate it out
			if(lastOpen != null) {
				Logger.d(TAG, "lastOpen is not null.animateView of "+lastOpenPosition);
				animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE);
			} else {
				Logger.d(TAG, "lastOpen is null in collapseLastOpen.");
			}
			lastOpenTogBtn.setBackgroundResource(R.drawable.fold_normal);
			lastOpenBanner.setBackgroundResource(R.drawable.list_card_item_selector);
//			lastOpenBanner.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_card_item_selector));
			lastColorWhite = true;//reset
			lastOpen = null;
			openItems.set(lastOpenPosition, false);
			lastOpenPosition = -1;
			return true;
		}
		return false;
	}
	
	/**
	 * Check's if any position is currently Expanded
	 * To collapse the open item @see collapseLastOpen
	 * 
	 * @return boolean True if there is currently an item expanded, otherwise false
	 */
	public boolean isAnyItemExpanded() {
		boolean result = (lastOpenPosition != -1) ? true : false;
		Logger.d(TAG, "hasAnyItemExpanded="+(result)+",pos="+lastOpenPosition);
		return result;
	}
	
	/**
	 * Performs either COLLAPSE or EXPAND animation on the target view
	 * @param target the view to animate
	 * @param type the animation type, either ExpandCollapseAnimation.COLLAPSE
	 *			 or ExpandCollapseAnimation.EXPAND
	 */
	private void animateView(final View target, final int type) {
		Animation anim = new ExpandCollapseAnimation(target,type);
		anim.setDuration(animationDuration);
		target.startAnimation(anim);
	}
	
	private void updateExpandable(View target, int position,int measureHeight) {
		final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)target.getLayoutParams();
		if(openItems.get(position)) {
			target.setVisibility(View.VISIBLE);
			params.height = measureHeight;
			params.bottomMargin = 0;
		} else {
			target.setVisibility(View.GONE);
			params.height = measureHeight;
			params.bottomMargin = 0-viewHeights.get(position);
		}
	}
	private int getResIdByType(String infoType) {
		int type = Integer.parseInt(infoType);
		int resId = 0;
		switch (type) {
		case 0:
			resId = R.drawable.mine_item_icon_gonglve;
			break;
		case 1:
			resId = R.drawable.mine_item_icon_pingce;
			break;
		case 2:
			resId = R.drawable.mine_item_icon_zixun;
			break;
		case 3:
			resId = R.drawable.mine_item_icon_huodong;
			break;
		case 4:
			resId = R.drawable.mine_item_icon_qianghao;
			break;
		case 5:
			resId = R.drawable.mine_item_icon_kaifu;
			break;
		default:
			break;
		}
		return resId;
	}

	class MyHolder {
		ImageView iv;
		TextView tv;
		TextView size;
		TextView gamename;
//		ImageView corner_iv;
		View toggleBtn;
		ImageView toggle_img;
		View startAction;
		View item_toolbar;
		View item_head;
	}

	private class SmallerAnimationListener implements AnimationListener {
		private int position;

		public SmallerAnimationListener(int position) {
			this.position = position;
		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

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
//		if (downloadedAppInfoList == null) {
//			// 下载列表
//			AppManager am = AppManager.getInstance(context);
//			downloadedAppInfoList = am.getInstalledGames();
//		}
		// 下载列表
		AppManager am = AppManager.getInstance(context);
		downloadedAppInfoList = am.getInstalledGames();

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

	private void setBackground(View view,int total,int current) {
		boolean isTail = ((total == current) || current == 4)? true : false;
		if (isTail) {
			if (lastColorWhite) {
				view.setBackgroundResource(R.drawable.mine_item_expand_gray_tail);
//				view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mine_item_expand_gray_tail));
				lastColorWhite = false;;
			} else {
				view.setBackgroundResource(R.drawable.mine_item_expand_white_tail);
//				view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mine_item_expand_white_tail));
				lastColorWhite = true;
			}
		} else {
			if (lastColorWhite) {
				view.setBackgroundResource(R.drawable.mine_item_expand_gray_middle);
//				view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mine_item_expand_gray_middle));
				lastColorWhite = false;;
			} else {
				view.setBackgroundResource(R.drawable.mine_item_expand_white_middle);
//				view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.mine_item_expand_white_middle));
				lastColorWhite = true;
			}
		}
	}
}