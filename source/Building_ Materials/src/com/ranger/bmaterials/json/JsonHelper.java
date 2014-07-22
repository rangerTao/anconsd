package com.ranger.bmaterials.json;

import org.json.JSONException;

import android.util.Log;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.netresponse.BaseResult;

public final class JsonHelper {

	public static BaseResult parserWithTag(int requestTag, String resData) throws JSONException {
		BaseResult res = null;
		if (Constants.DEBUG)
			Log.i("JsonHelper", "[parserWithTag] request Tag: " + requestTag + " resData: " + resData);
		switch (requestTag) {
		
		default:
			break;
		}

		return res;
	}
}
