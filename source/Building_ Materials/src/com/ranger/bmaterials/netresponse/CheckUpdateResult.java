package com.ranger.bmaterials.netresponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;

public class CheckUpdateResult extends BaseResult {
	public int updatetype;
	public String apkurl;
	public String apkversion;
	public String apksize;
	public String description;
}
