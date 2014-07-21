package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.tools.StringUtil;

public class SingleClassGamesResult extends BaseResult {
	private ArrayList<GameInfo> game_list = new ArrayList<GameInfo>();
	private HashMap<String,GameInfo> map_game = new HashMap<String,GameInfo>();
    private int mTotalCount = 0;
    
    public int getTotalCount()
    {
        return mTotalCount;
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
            
            if (jsonObj.has("totalcount"))
            {
                mTotalCount = Integer.valueOf(jsonObj.getString("totalcount")).intValue();
            }
            
            if(errorcode != 0)
            	return;
            
            JSONArray games = jsonObj.getJSONArray("gamelist");
            for(int i=0; i<games.length();i++){
            	JSONObject obj = games.getJSONObject(i);
            	GameInfo gameInfo = new GameInfo();
            	gameInfo.setGameId(obj.getString("gameid"));
            	gameInfo.setGameName(obj.getString("gamename"));
            	gameInfo.setPkgname(obj.getString("pkgname"));
            	gameInfo.setIconUrl(obj.getString("gameicon"));
            	
            	try {
            		gameInfo.setStar(Float.parseFloat(obj.getString("star")));
				} catch (NumberFormatException e) {
					//e.printStackTrace();
				}
            	gameInfo.setSize(obj.getString("pkgsize"));
            	gameInfo.setDownloadurl(obj.getString("downloadurl"));
            	gameInfo.setDownloadedtimes(obj.getString("downloaded"));
            	
            	gameInfo.setStartaction(obj.getString("startaction"));
            	String needLogin = obj.getString("needlogin");
            	gameInfo.setNeedlogin("1".equals(needLogin)?true:false);
            	
            	gameInfo.setGameversion(obj.getString("versionname"));
            	gameInfo.setGameversioncode(StringUtil.parseInt(obj.getString("versioncode")));
            	
            	gameInfo.setComingsoon(obj.getString("comingsoon"));
            	
            	game_list.add(gameInfo);
            	map_game.put(gameInfo.getPkgname(), gameInfo);
            }
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
	}


	public ArrayList<GameInfo> getGame_list() {
		return game_list;
	}


	public HashMap<String, GameInfo> getMap_game() {
		return map_game;
	}
	
	
}
