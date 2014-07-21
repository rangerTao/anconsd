package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.ui.MineGameItemInfo;
import com.ranger.bmaterials.ui.MineGuideItemInfo;
import com.ranger.bmaterials.ui.MineMsgItemInfo;

public class MineGuidesResult extends BaseResult {

	public int totalcount = 0;
	public List<MineGuideItemInfo> guideListInfo = new ArrayList<MineGuideItemInfo>();
}
