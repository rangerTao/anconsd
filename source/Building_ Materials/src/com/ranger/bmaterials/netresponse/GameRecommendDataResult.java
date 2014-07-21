package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.mode.ADInfo;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.tools.StringUtil;

public class GameRecommendDataResult extends BaseResult {

	private ArrayList<ADInfo> list_ad = new ArrayList<ADInfo>();
	private ArrayList<GameInfo> list_game = new ArrayList<GameInfo>();
	
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
            
            JSONArray ads = jsonObj.getJSONArray("gameads");
            for(int i=0; i<ads.length();i++){
            	JSONObject obj = ads.getJSONObject(i);
            	ADInfo adInfo = new ADInfo();
            	adInfo.setAdgameid(obj.getString("adgameid"));
            	
            	adInfo.setAdpicurl(obj.getString("adurl"));
            	//adInfo.setAdpkgname(obj.getString(Constants.JSON_ADPKGNAME_HOME_PAGE));
            	
            	adInfo.setAdtype(StringUtil.parseInt(obj.getString("adtype")));
            	
            	adInfo.setItemid(obj.getString("itemid"));
            	adInfo.setGametype(obj.getString("gametype"));
            	adInfo.setGametypenumber(obj.getString("gametypenumber"));
            	
            	list_ad.add(adInfo);
            }
            
            JSONArray games = jsonObj.getJSONArray("gamelist");
            for(int i=0; i<games.length();i++){
            	JSONObject obj = games.getJSONObject(i);
            	GameInfo gameInfo = new GameInfo();
            	gameInfo.setGameId(obj.getString("gameid"));
            	gameInfo.setGameName(obj.getString("gamename"));
            	gameInfo.setIconUrl(obj.getString("gameicon"));
            	
            	list_game.add(gameInfo);
            }
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
	}

	public ArrayList<ADInfo> getList_ad() {
		return list_ad;
	}


	public ArrayList<GameInfo> getList_game() {
		return list_game;
	}



}
