package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.ui.MineMsgItemInfo;
import com.ranger.bmaterials.view.NewSegmentedLayout;

public class MineMsgResult extends BaseResult {
	public int totalcount = 0;
	public List<MineMsgItemInfo> msgListInfo = new ArrayList<MineMsgItemInfo>();
}
