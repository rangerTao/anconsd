package com.ranger.bmaterials.json;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.GameTingApplication;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.encrypt.AES;
import com.ranger.bmaterials.tools.ConnectManager;
import com.ranger.bmaterials.tools.PhoneHelper;
/**
 * 
 * @author wenzutong
 * 
 */
public final class JSONBuilder {

	private static JSONObject createJsonObject() throws JSONException {

		JSONObject jsonObject = new JSONObject();
		Context context = GameTingApplication.getAppInstance().getApplicationContext();

		try {
			jsonObject.put(Constants.JSON_VERSION, PhoneHelper.getAppVersionName());
			jsonObject.put(Constants.JSON_UA, Build.MODEL);
			jsonObject.put(Constants.JSON_OS, Build.VERSION.RELEASE);
			jsonObject.put(Constants.JSON_IMEI, PhoneHelper.getIMEI());
			jsonObject.put(Constants.JSON_UDID, PhoneHelper.getUdid());

			jsonObject.put(Constants.JSON_CHANNEL, PhoneHelper.getChannelData(context.getString(R.string.channel_name)));

			DisplayMetrics dm = GameTingApplication.getAppInstance().getResources().getDisplayMetrics();
			jsonObject.put(Constants.JSON_SCREENH, String.valueOf(dm.heightPixels));
			jsonObject.put(Constants.JSON_SCREENW, String.valueOf(dm.widthPixels));

			jsonObject.put(Constants.JSON_PUSH_CHANNELID, MineProfile.getInstance().getPush_channelid());
			jsonObject.put(Constants.JSON_PUSH_USERID, MineProfile.getInstance().getPush_userid());

		} catch (Exception e) {
			e.printStackTrace();
		}

		ConnectManager connectManager = new ConnectManager(context);
		String connectString = connectManager.getConnectionString(context);
		jsonObject.put(Constants.JSON_CONNECT_TYPE, connectString);

		return jsonObject;
	}

	public String buildCheckUpdateString() {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_CHECK_UPDATE));
			// jsonObj.put(Constants.JSON_APP_VERSION, appversion);
			// jsonObj.put(Constants.JSON_APP_VERSION_CODE, appversioncode);
			res = jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res;
	}

	public String buildChangePwdString(String oldpwd, String newpwd, String userid, String sessionid) {
		String res = "";
		try {
			JSONObject jsonObj = createJsonObject();
			jsonObj.put(Constants.JSON_TAG, String.valueOf(Constants.NET_TAG_CHANGE_PWD));
			jsonObj.put(Constants.JSON_USERID, userid);
			jsonObj.put(Constants.JSON_SESSIONID, sessionid);
			jsonObj.put(Constants.JSON_NEWPASSWORD, newpwd);
			jsonObj.put(Constants.JSON_OLDPASSWORD, oldpwd);
			res = jsonObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

    //"{\"username\":\"" + username + "\",password:\"" + password + "\"}"

    public static String buildLoginString(String name,String pwd){
        String res = "";
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("username",name);
            jsonObj.put("password",pwd);
            res = jsonObj.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return res;
    }

    public static String buildUpdateUserinfoString(){
        String res = "";
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("nickname",MineProfile.getInstance().getNickName());
            jsonObj.put("realname",MineProfile.getInstance().getUserName());
            jsonObj.put("sex",MineProfile.getInstance().getUserType()+"");
            jsonObj.put("provinces","");
            jsonObj.put("city",MineProfile.getInstance().getArea());
            jsonObj.put("signature",MineProfile.getInstance().getSignture());
            jsonObj.put("qq","");
            res = jsonObj.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return res;
    }

}
