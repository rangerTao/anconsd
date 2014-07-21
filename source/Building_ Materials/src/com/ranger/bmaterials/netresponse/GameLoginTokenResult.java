package com.ranger.bmaterials.netresponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;

public final class GameLoginTokenResult extends BaseResult {

	private String token = "";

	public void parse(String resData) {
		try {
			JSONObject jsonObj = new JSONObject(resData);
			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
            String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
            String tag = jsonObj.getString(Constants.JSON_TAG);
            
            this.setTag(tag);
            this.setErrorCode(errorcode);
            this.setErrorString(errorStr);
			token = jsonObj.getString(Constants.JSON_TOKEN);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
	}

	public String getToken() {
		return token;
	}
}
