package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.json.JSONUtil;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.tools.StringUtil;

public class GameMoreDataResult extends BaseResult {

	private ArrayList<GameInfo> list_game = new ArrayList<GameInfo>();
	private HashMap<String, GameInfo> map_game = new HashMap<String, GameInfo>();

	private int mTotal = 0;

	public int getTotalCount() {
		return mTotal;
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

			if (errorcode != 0)
				return;

			if (null != jsonObj && jsonObj.has("totalcount")) {
				mTotal = jsonObj.getInt("totalcount");
			}

			JSONArray games = jsonObj.getJSONArray("gamelist");
			for (int i = 0; i < games.length(); i++) {
				JSONObject obj = games.getJSONObject(i);
				GameInfo gameInfo = new GameInfo();
				gameInfo.setGameId(obj.getString("gameid"));
				gameInfo.setGameName(obj.getString("gamename"));
				gameInfo.setIconUrl(obj.getString("gameicon"));
				gameInfo.setLabelColor(JSONUtil.instance().getString(obj, Constants.JSON_GAME_LABLE_COLOR));
				gameInfo.setLabelName(JSONUtil.instance().getString(obj, Constants.JSON_GAME_LABLE_NAME));

				try {
					gameInfo.setStar(Float.parseFloat(obj.getString("star")));
				} catch (NumberFormatException e) {
					// e.printStackTrace();
				}
				gameInfo.setPkgname(obj.getString("pkgname"));
				gameInfo.setSize(obj.getString("pkgsize"));
				gameInfo.setDownloadurl(obj.getString("downloadurl"));
				gameInfo.setDownloadedtimes(obj.getString("downloaded"));
				gameInfo.setUpdatetime(obj.getString("updatetime"));
				gameInfo.setStartaction(obj.getString("startaction"));
				String needLogin = obj.getString("needlogin");
				gameInfo.setNeedlogin("1".equals(needLogin) ? true : false);
				gameInfo.setGameversion(obj.getString("versionname"));
				gameInfo.setGameversioncode(StringUtil.parseInt(obj.getString("versioncode")));
				gameInfo.setComingsoon(obj.getString("comingsoon"));

				list_game.add(gameInfo);
				map_game.put(gameInfo.getPkgname(), gameInfo);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
	}

	public ArrayList<GameInfo> getList_game() {
		return list_game;
	}

	public HashMap<String, GameInfo> getMap_game() {
		return map_game;
	}

}
