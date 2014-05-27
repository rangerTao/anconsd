package com.andconsd.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.andconsd.AndApplication;
import com.andconsd.MineProfile;
import com.andconsd.R;
import com.andconsd.utils.ConnectManager;
import com.andconsd.utils.Constants;
import com.andconsd.utils.PhoneHelper;

public final class JSONBuilder {
	
	@SuppressWarnings("unused")
	public JSONObject createJsonObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		Context context = AndApplication.getAppInstance().getApplicationContext();

		try {
			jsonObject.put(Constants.JSON_VERSION, PhoneHelper.getAppVersionName());
			jsonObject.put(Constants.JSON_UA, Build.MODEL);
			jsonObject.put(Constants.JSON_OS, Build.VERSION.RELEASE);

			DisplayMetrics dm = AndApplication.getAppInstance().getResources().getDisplayMetrics();
			jsonObject.put(Constants.JSON_SCREENWH, String.valueOf(dm.widthPixels) + "_" + String.valueOf(dm.heightPixels));

			jsonObject.put(Constants.JSON_IMEI, PhoneHelper.getIMEI());
			jsonObject.put(Constants.JSON_UDID, PhoneHelper.getUdid());
			MineProfile mineProfile = MineProfile.getInstance();
			String channel = PhoneHelper.getChannelData(context.getString(R.string.channel_name));
			if(TextUtils.isEmpty(channel)){
				jsonObject.put(Constants.JSON_CHANNEL, mineProfile.getChannelId());
			}else{
				jsonObject.put(Constants.JSON_CHANNEL, channel);
			}
			jsonObject.put(Constants.JSON_PUSH_CHANNELID, mineProfile.getPush_channelid());
			jsonObject.put(Constants.JSON_PUSH_USERID, mineProfile.getPush_userid());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ConnectManager connectManager = new ConnectManager(context);
		String connectString = connectManager.getConnectionString(context);
		jsonObject.put(Constants.JSON_CONNECT_TYPE, connectString);
		return jsonObject;
	}
	
	public JSONObject createFeedBackJson(String feedback){
		
		try{
			JSONObject req = createJsonObject();
			req.put("feedback", feedback);
			
			return req;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

}
