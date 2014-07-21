package com.ranger.bmaterials.test;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;

import com.baidu.android.pushservice.PushConstants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.broadcast.PushServiceReceiver;

public class BaiduPushTest {
	
	public static void pushGame() {
		Intent intent = new Intent(PushConstants.ACTION_MESSAGE);
		JSONObject object = new JSONObject();
		
		try {
			object.put("ticker", "新游戏来了！");
			object.put("title", "游戏推荐");
			object.put("content", "推荐个新游戏gameid:50818");
			object.put("gameid", "50818");
			object.put("gamename", "");
			object.put("pushtype", PushServiceReceiver.PUSH_TYPE_GAMEDETAIL);
		} catch (Exception e) {
		}
		
		String message = object.toString();
		intent.putExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING, message);
		
		GameTingApplication.getAppInstance().sendBroadcast(intent);
	}
	
	public static void pushStartPage() {

		Intent intent = new Intent(PushConstants.ACTION_MESSAGE);
		JSONObject object = new JSONObject();
		
		try {
			object.put("ticker", "你好久没来了！");
			object.put("title", "多酷游戏首页");
			object.put("content", "多酷游戏喊你回家吃饭~");
			object.put("pushtype", PushServiceReceiver.PUSH_TYPE_STARTPAGE);
		} catch (Exception e) {
		}
		
		String message = object.toString();
		intent.putExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING, message);
		
		GameTingApplication.getAppInstance().sendBroadcast(intent);
	}

	public static void pushMsgNum() {
		Intent intent = new Intent(PushConstants.ACTION_MESSAGE);
		JSONObject object = new JSONObject();
		
		try {
			object.put("userid", MineProfile.getInstance().getUserID());
			object.put("unreadmsgnum", "9");
			object.put("pushtype", PushServiceReceiver.PUSH_TYPE_MSGNUM);
		} catch (Exception e) {
		}
		
		String message = object.toString();
		intent.putExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING, message);
		
		GameTingApplication.getAppInstance().sendBroadcast(intent);
	}
	
	public static void pushGameList() {
		Intent intent = new Intent(PushConstants.ACTION_MESSAGE);
		JSONObject object = new JSONObject();
		
		try {
			object.put("ticker", "好玩的游戏");
			object.put("title", "推荐列表页");
			object.put("content", "多酷游戏有新游戏了");
			object.put("pushtype", PushServiceReceiver.PUSH_TYPE_GAMELIST);
			object.put("subjecttitle", "新游戏来了");
			object.put("subjectcontent", "多酷游戏有新游戏了,欢迎你来玩,JJ斗地主冲100送100！");
			
			JSONArray array = new JSONArray();
			
			JSONObject obj1 = new JSONObject();
			obj1.put("gameid", "54424");
			obj1.put("gamename", "帝国联盟");
			obj1.put("gameicon", "http://img.m.duoku.com/preview/wap/mm/54/54424/logo4.jpg");
			obj1.put("star", "5");
			obj1.put("pkgname", "com.empire2.activity.lakooMM");
			obj1.put("pkgsize", "45854720");
			obj1.put("downloadurl", "http://dl.m.duoku.com/game/mm/54/54424/54424.apk");
			obj1.put("downloaded", "16");
			obj1.put("versionname", "2.0.0");
			obj1.put("versioncode", "5");
			
			array.put(obj1);
			
			JSONObject obj2 = new JSONObject();
			obj2.put("gameid", "53923");
			obj2.put("gamename", "JJ斗主");
			obj2.put("gameicon", "http://img.m.duoku.com/preview/wap/53000/53923/wap_icon.png");
			obj2.put("star", "4");
			obj2.put("pkgname", "cn.jj");
			obj2.put("pkgsize", "9434755");
			obj2.put("downloadurl", "http://dl.m.duoku.com/game/53000/53923/20130726152149_DuoKu.apk");
			obj2.put("downloaded", "39303");
			obj2.put("versionname", "2.0.5");
			obj2.put("versioncode", "5");
			
			array.put(obj2);
			
			obj2 = new JSONObject();
			obj2.put("gameid", "53923");
			obj2.put("gamename", "JJ斗主");
			obj2.put("gameicon", "http://img.m.duoku.com/preview/wap/53000/53923/wap_icon.png");
			obj2.put("star", "4");
			obj2.put("pkgname", "cn.jj");
			obj2.put("pkgsize", "9434755");
			obj2.put("downloadurl", "http://dl.m.duoku.com/game/53000/53923/20130726152149_DuoKu.apk");
			obj2.put("downloaded", "39303");
			obj2.put("versionname", "2.0.5");
			obj2.put("versioncode", "5");
			
			array.put(obj2);
			obj2 = new JSONObject();
			obj2.put("gameid", "53923");
			obj2.put("gamename", "JJ斗主");
			obj2.put("gameicon", "http://img.m.duoku.com/preview/wap/53000/53923/wap_icon.png");
			obj2.put("star", "4");
			obj2.put("pkgname", "cn.jj");
			obj2.put("pkgsize", "9434755");
			obj2.put("downloadurl", "http://dl.m.duoku.com/game/53000/53923/20130726152149_DuoKu.apk");
			obj2.put("downloaded", "39303");
			obj2.put("versionname", "2.0.5");
			obj2.put("versioncode", "5");
			
			array.put(obj2);
			obj2 = new JSONObject();
			obj2.put("gameid", "53923");
			obj2.put("gamename", "JJ斗主");
			obj2.put("gameicon", "http://img.m.duoku.com/preview/wap/53000/53923/wap_icon.png");
			obj2.put("star", "4");
			obj2.put("pkgname", "cn.jj");
			obj2.put("pkgsize", "9434755");
			obj2.put("downloadurl", "http://dl.m.duoku.com/game/53000/53923/20130726152149_DuoKu.apk");
			obj2.put("downloaded", "39303");
			obj2.put("versionname", "2.0.5");
			obj2.put("versioncode", "5");
			
			array.put(obj2);
			obj2 = new JSONObject();
			obj2.put("gameid", "53923");
			obj2.put("gamename", "JJ斗主");
			obj2.put("gameicon", "http://img.m.duoku.com/preview/wap/53000/53923/wap_icon.png");
			obj2.put("star", "4");
			obj2.put("pkgname", "cn.jj");
			obj2.put("pkgsize", "9434755");
			obj2.put("downloadurl", "http://dl.m.duoku.com/game/53000/53923/20130726152149_DuoKu.apk");
			obj2.put("downloaded", "39303");
			obj2.put("versionname", "2.0.5");
			obj2.put("versioncode", "5");
			
			array.put(obj2);
			obj2 = new JSONObject();
			obj2.put("gameid", "53923");
			obj2.put("gamename", "JJ斗主");
			obj2.put("gameicon", "http://img.m.duoku.com/preview/wap/53000/53923/wap_icon.png");
			obj2.put("star", "4");
			obj2.put("pkgname", "cn.jj");
			obj2.put("pkgsize", "9434755");
			obj2.put("downloadurl", "http://dl.m.duoku.com/game/53000/53923/20130726152149_DuoKu.apk");
			obj2.put("downloaded", "39303");
			obj2.put("versionname", "2.0.5");
			obj2.put("versioncode", "5");
			
			array.put(obj2);
			obj2 = new JSONObject();
			obj2.put("gameid", "53923");
			obj2.put("gamename", "JJ斗主");
			obj2.put("gameicon", "http://img.m.duoku.com/preview/wap/53000/53923/wap_icon.png");
			obj2.put("star", "4");
			obj2.put("pkgname", "cn.jj");
			obj2.put("pkgsize", "9434755");
			obj2.put("downloadurl", "http://dl.m.duoku.com/game/53000/53923/20130726152149_DuoKu.apk");
			obj2.put("downloaded", "39303");
			obj2.put("versionname", "2.0.5");
			obj2.put("versioncode", "5");
			
			array.put(obj2);
			object.put("gamelist", array);
			
		} catch (Exception e) {
		}
		
		String message = object.toString();
		intent.putExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING, message);
		
		GameTingApplication.getAppInstance().sendBroadcast(intent);
	}
}
