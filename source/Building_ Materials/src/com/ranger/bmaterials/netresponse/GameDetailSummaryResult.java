package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.tools.StringUtil;

public class GameDetailSummaryResult extends BaseResult {
	
	private GameInfo gameInfo = new GameInfo();
	private ArrayList<String> list_gameSmallPics = new ArrayList<String>();
	private ArrayList<String> list_gameBigPics=new ArrayList<String>();
	private ArrayList<GameInfo> list_rd_games = new ArrayList<GameInfo>();
	
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
            
            
        	gameInfo.setGameId(jsonObj.getString("gameid"));
        	gameInfo.setGameName(jsonObj.getString("gamename"));
        	gameInfo.setIconUrl(jsonObj.getString("gameicon"));
        	try {
        		gameInfo.setStar(Float.parseFloat(jsonObj.getString("star")));
			} catch (NumberFormatException e) {
				//e.printStackTrace();
			}
        	gameInfo.setPkgname(jsonObj.getString("pkgname"));
        	gameInfo.setSize(jsonObj.getString("pkgsize"));
        	gameInfo.setDownloadurl(jsonObj.getString("downloadurl"));
        	gameInfo.setDownloadedtimes(jsonObj.getString("downloaded"));
        	gameInfo.setUpdatetime(jsonObj.getString("updatetime"));
        	gameInfo.setStartaction(jsonObj.getString("startaction"));
        	gameInfo.setGameversion(jsonObj.getString("version"));
        	gameInfo.setGameversioncode(StringUtil.parseInt(jsonObj.getString("versioncode")));
        	gameInfo.setDescription(jsonObj.getString("description"));
        	String c = jsonObj.getString("collected");
        	gameInfo.setIscollected("1".equals(c)? true : false);
        	String needLogin = jsonObj.getString("needlogin");
        	gameInfo.setNeedlogin("1".equals(needLogin)?true:false);
        	
        	gameInfo.setComingsoon(jsonObj.getString("comingsoon"));
        	try {
				gameInfo.setGametypename(jsonObj.getString("gametypename"));
			} catch (Exception e) {
				
			}
            
            JSONArray pics = jsonObj.getJSONArray("gamepics");
            
            for(int i = 0; i<pics.length();i++){
            	JSONObject obj = pics.getJSONObject(i);
            	
            	list_gameSmallPics.add(obj.getString("gamepic"));
            	list_gameBigPics.add(obj.getString("gamepicbig"));
            }
            
            JSONArray games = jsonObj.getJSONArray("recommendgames");
            
            for(int i=0; i<games.length();i++){
            	JSONObject obj = games.getJSONObject(i);
            	GameInfo _gameInfo = new GameInfo();
            	_gameInfo.setGameId(obj.getString("rdgameid"));
            	_gameInfo.setGameName(obj.getString("rdgamename"));
            	_gameInfo.setIconUrl(obj.getString("rdgameicon"));
            	
            	list_rd_games.add(_gameInfo);
            }
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
		
	}

	public GameInfo getGameInfo() {
		return gameInfo;
	}

	public ArrayList<String> getList_gameSmallPics() {
		return list_gameSmallPics;
	}

	public ArrayList<String> getList_gameBigPics() {
		return list_gameBigPics;
	}

	public ArrayList<GameInfo> getList_rd_games() {
		return list_rd_games;
	}
	
	

}
