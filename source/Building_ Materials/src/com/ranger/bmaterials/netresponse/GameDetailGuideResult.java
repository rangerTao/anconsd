package com.ranger.bmaterials.netresponse;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.mode.GameGuideInfo;
import com.ranger.bmaterials.mode.GameInfo;

public class GameDetailGuideResult extends BaseResult implements Serializable {

    private static final long serialVersionUID = 1L;
    ArrayList<GameGuideInfo> list_game_guide = new ArrayList<GameGuideInfo>();;
	
	public void parse(String resData) {
		
		try {
			JSONObject jsonObj = new JSONObject(resData);
			int errorcode = jsonObj.getInt(Constants.JSON_ERROR_CODE);
            String errorStr = jsonObj.getString(Constants.JSON_ERROR_MSG);
            String tag = jsonObj.getString(Constants.JSON_TAG);
            
            this.setTag(tag);
            this.setErrorCode(errorcode);
            this.setErrorString(errorStr);
            
            if(errorcode != 0)
            	return;
            
            JSONArray guides = jsonObj.getJSONArray("gameguides");
           
            for(int i=0; i<guides.length();i++){
            	JSONObject obj = guides.getJSONObject(i);
            	GameGuideInfo guideInfo = new GameGuideInfo();
//            	guideInfo.setGameId(obj.getString("gameid"));
            	guideInfo.setGuideid(obj.getString("guideid"));
            	guideInfo.setGuidetitle(obj.getString("guidetitle"));
            	guideInfo.setGuidetime(obj.getString("guidetime"));
            	String c = obj.getString("collected");
//            	guideInfo.setIscollected("0".equals(c)? false:true);
            	GameInfo info=new GameInfo();
            	info.setGameId(obj.getString("gameid"));
            	info.setIscollected("0".equals(c)? false:true);
            	guideInfo.setInfo(info);
            	
            	list_game_guide.add(guideInfo);
            }
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
	}

	public ArrayList<GameGuideInfo> getList_game_guide() {
		return list_game_guide;
	}
	
	

}
