package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.mode.GameTypeInfo;
import com.ranger.bmaterials.mode.GameTypeInfo.GameBriefInfo;

public class GameClassDataResult extends BaseResult {

	private ArrayList<GameTypeInfo> list_game = new ArrayList<GameTypeInfo>();
	
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
            
            JSONArray games = jsonObj.getJSONArray("gametypes");
            for(int i=0; i<games.length();i++){
            	JSONObject obj = games.getJSONObject(i);
            	GameTypeInfo gameTypeInfo = new GameTypeInfo();
            	gameTypeInfo.setLabel(obj.getString("label"));
            	gameTypeInfo.setGametypeicon(obj.getString("gametypeicon"));
            	gameTypeInfo.setGametype(obj.getString("gametype"));
            	gameTypeInfo.setGametypenumber(obj.getString("gametypenumber"));
            	gameTypeInfo.setGametypename(obj.getString("gametypename"));
            	gameTypeInfo.setTotalcount(obj.getString("totalcount"));
            	
            	if(obj.has("gamelist")){
                	JSONArray gameInfos = obj.getJSONArray("gamelist");
                	
                	for(int gi=0;gi<gameInfos.length();gi++){
                		JSONObject game = gameInfos.getJSONObject(gi);
                		
                		GameBriefInfo gbi = gameTypeInfo.new GameBriefInfo(game);
                		
                		gameTypeInfo.setGame(gbi);
                	}

            	}
            	
            	list_game.add(gameTypeInfo);
            }
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
	}

	public ArrayList<GameTypeInfo> getList_game() {
		return list_game;
	}
	
	

}
