package com.ranger.bmaterials.netresponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;

public class PublishCommentStarResult extends BaseResult {

	private int coinnum = 0;
	
	public int getCoinnum() {
		return coinnum;
	}

	public void setCoinnum(int coinnum) {
		this.coinnum = coinnum;
	}

	public void parse(String resData) {
		
		try {
			JSONObject jsonObj = new JSONObject(resData);
			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
            String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
            String tag = jsonObj.getString(Constants.JSON_TAG);
            
            this.setTag(tag);
            this.setErrorCode(errorcode);
            this.setErrorString(errorStr);
            this.setCoinnum(jsonObj.getInt(Constants.JSON_COINNUM));
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
	}

}
