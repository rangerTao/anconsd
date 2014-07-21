package com.ranger.bmaterials.listener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.mode.ADInfo;
import com.ranger.bmaterials.mode.ActivityInfo;
import com.ranger.bmaterials.ui.ActivityDetailActivity;
import com.ranger.bmaterials.ui.CompetitionActivity;
import com.ranger.bmaterials.ui.GameDetailsActivity;
import com.ranger.bmaterials.ui.GameGuideDetailActivity2;
import com.ranger.bmaterials.ui.MainHallActivity;
import com.ranger.bmaterials.ui.MoreClassGameActivity;
import com.ranger.bmaterials.ui.OpenServerDetailActivity;
import com.ranger.bmaterials.ui.SnapNumberDetailActivity;
import com.ranger.bmaterials.ui.SquareDetailBaseActivity;
import com.ranger.bmaterials.ui.WebviewActivity;
import com.ranger.bmaterials.ui.topicdetail.TopicDetailActivity;
import com.ranger.bmaterials.ui.topicdetail.Utils;

public class AdOnClickListener implements OnClickListener {
	protected Activity act;

	protected static final int SINGLE_GAME = 0;// 游戏
	protected static final int SINGLE_STRATEGY = 1;// 攻略
	protected static final int SINGLE_SNAP_NUMER = 2;// 抢号
	protected static final int SINGLE_ACTIVITY = 3;// 活动
	protected static final int SINGLE_OPEN_SERVICE = 4;// 开服
	protected static final int SINGLE_CLASSIFY = 5;// 分类
	protected static final int SINGLE_SUBJECT_DETAIL = 6;// 专题详情
	protected static final int SINGLE_COMPETITION = 7;// 争霸赛
	protected static final int SINGLE_SUBJECT = 8;// 专题

	protected static final int SINGLE_CHOUJIANG = 9;// 抽奖

	public AdOnClickListener(Activity act) {
		this.act = act;
	}

	public final Activity getAct() {
		return act;
	}

	public final void setAct(Activity act) {
		this.act = act;
	}

	private int adType = -1;
	private String itemid;

	public final int getAdType() {
		return adType;
	}

	public final void setAdType(int adType) {
		this.adType = adType;
	}

	public final String getItemid() {
		return itemid;
	}

	public final void setItemid(String itemid) {
		this.itemid = itemid;
	}

	@Override
	public void onClick(View v) {
		ADInfo adInfo = (ADInfo) v.getTag();
		if (adInfo != null) {
			adType = adInfo.getAdtype();
			itemid = adInfo.getItemid();
		}
		if (adType == -1 || itemid == null || act == null)
			return;

		switch (adType) {
		case SINGLE_GAME:
			Intent intent = new Intent(act, GameDetailsActivity.class);
			intent.putExtra("gameid", adInfo == null ? itemid : adInfo.getAdgameid());
			intent.putExtra("gamename", adInfo == null ? "" : adInfo.getAdgamename());
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			act.startActivity(intent);
			break;
		case SINGLE_STRATEGY:
			Intent in = new Intent(act, GameGuideDetailActivity2.class);
			in.putExtra("guideid", itemid + "");
			in.putExtra("gamename", adInfo == null ? "" : adInfo.getAdgamename());
			in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			act.startActivity(in);
			break;
		case SINGLE_SNAP_NUMER:
			intent = new Intent(act, SnapNumberDetailActivity.class);
			MineProfile profile = MineProfile.getInstance();

			String userID = profile.getUserID();
			String sessionID = profile.getSessionID();

			intent.putExtra(SnapNumberDetailActivity.ARG_GAMEID, adInfo == null ? null : adInfo.getAdgameid());
			intent.putExtra(SnapNumberDetailActivity.ARG_GRABID, itemid + "");
			intent.putExtra(SnapNumberDetailActivity.ARG_USERID, userID);
			intent.putExtra(SnapNumberDetailActivity.ARG_SESSIONID, sessionID);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			act.startActivity(intent);
			break;
		case SINGLE_ACTIVITY:
			ActivityInfo info = new ActivityInfo();
			info.setId(itemid);
			intent = new Intent(act, ActivityDetailActivity.class);
			intent.putExtra(SquareDetailBaseActivity.ARG_DETAIL, info);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			act.startActivity(intent);
			break;
		case SINGLE_OPEN_SERVICE:
			intent = new Intent(act, OpenServerDetailActivity.class);
			intent.putExtra(OpenServerDetailActivity.ARG_GAME_ID, adInfo == null ? null : adInfo.getAdgameid());
			intent.putExtra(OpenServerDetailActivity.ARG_OPENSERVER_ID, itemid + "");
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			act.startActivity(intent);
			break;
		case SINGLE_CLASSIFY:
			if (adInfo != null) {
				intent = new Intent(act, MoreClassGameActivity.class);
				intent.putExtra("game_type_number", adInfo.getGametypenumber());
				intent.putExtra("game_type", adInfo.getGametype());
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				act.startActivity(intent);
			}
			break;
		case SINGLE_SUBJECT_DETAIL:
			Bundle b = new Bundle();
			b.putString(TopicDetailActivity.KEY_EXTRA_TOPIC_ID, itemid + "");
			Utils.instance().tripToTopicDetail(act, b);
			break;
		case SINGLE_COMPETITION:
			intent = new Intent(act, CompetitionActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			act.startActivity(intent);
			break;
		case SINGLE_SUBJECT:
			MainHallActivity.jumpToTabByChildActivity(act, 0);
			break;
		case SINGLE_CHOUJIANG:
			if (adInfo != null) {
				intent = new Intent(act, WebviewActivity.class);
				intent.putExtra("title", act.getResources().getString(R.string.lottery_act_default_title));
				intent.putExtra("url", adInfo.getPrizeurl());
				intent.putExtra("arg1_param", "sweepstakes");
				act.startActivity(intent);
			}else if(itemid != null  && !itemid.equals("")){
                intent = new Intent(act, WebviewActivity.class);
                intent.putExtra("title", act.getResources().getString(R.string.lottery_act_default_title));
                intent.putExtra("url", itemid);
                intent.putExtra("arg1_param", "sweepstakes");
                act.startActivity(intent);
            }
			break;
		default:
			break;
		}
	}

}