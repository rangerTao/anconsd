package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.mode.GameInfo;

public class GameHotDataResult extends BaseResult {

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
            
            JSONArray games = jsonObj.getJSONArray("gamelist");
            for(int i=0; i<games.length();i++){
            	JSONObject obj = games.getJSONObject(i);
            	GameInfo gameInfo = new GameInfo();
            	gameInfo.setGameId(obj.getString("gameid"));
            	gameInfo.setGameName(obj.getString("gamename"));
            	gameInfo.setIconUrl(obj.getString("gameicon"));
            	
            	list_game.add(gameInfo);
            }
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
	}

	public ArrayList<GameInfo> getList_game() {
		return list_game;
	}

	
}
