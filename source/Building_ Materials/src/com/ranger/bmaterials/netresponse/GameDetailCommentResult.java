package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.mode.GameCommentInfo;

public class GameDetailCommentResult extends BaseResult {

	private volatile ArrayList<GameCommentInfo> list_comment = new ArrayList<GameCommentInfo>();
	
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
            
            JSONArray guides = jsonObj.getJSONArray("comments");
           
            for(int i=0; i<guides.length();i++){
            	JSONObject obj = guides.getJSONObject(i);
            	GameCommentInfo gcInfo = new GameCommentInfo();
            	gcInfo.setUserid(obj.getString("userid"));
            	gcInfo.setCommentid(obj.getString("cmtid"));
            	gcInfo.setCmtusername(obj.getString("cmtusername"));
            	gcInfo.setCommenttime(obj.getString("cmttime"));
            	gcInfo.setCmtcotent(obj.getString("cmtcontent"));

                list_comment.add(gcInfo);
            }
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
	}

	public ArrayList<GameCommentInfo> getList_comment() {
        return list_comment;
	}
	
	

}
