package com.andconsd.framework.json;

import org.json.JSONException;

import com.andconsd.framework.net.response.BaseResult;
import com.andconsd.framework.utils.Constants;

import android.util.Log;

public final class JSONHelper {

	public static BaseResult parserWithTag(int requestTag, String resData)
			throws JSONException {
		BaseResult res = null;
		if (Constants.DEBUG)
			Log.i("JsonHelper", "[parserWithTag] requestTag: " + requestTag
					+ " resData: " + resData);

		switch (requestTag) {
		
		case Constants.TAG_BEAUTY:
		case Constants.TAG_RELAX:
		case Constants.TAG_GAME:
		case Constants.TAG_CAR:
		case Constants.TAG_FAMOUS:
			res = JSONParser.parseBeautyList(resData);
			break;
		case Constants.TAG_FEEDBACK:
			res = JSONParser.parseJson(resData, BaseResult.class);
			break;
		default:
			res = JSONParser.parseJson(resData, BaseResult.class);
			break;
		}

		return res;
	}
}
