package com.ranger.bmaterials.netresponse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.mode.GameGuideDetailInfo;
import com.ranger.bmaterials.mode.GameInfo;
import com.ranger.bmaterials.mode.GameGuideDetailInfo.GameGuideContent;
import com.ranger.bmaterials.tools.StringUtil;

public class GameGuideDetailResult extends BaseResult {

	private GameGuideDetailInfo mGameGuideDetailInfo = new GameGuideDetailInfo();
	
	private boolean mIsUseHTML = false;
    private String mHTMLContent = null;

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
            GameInfo info=new GameInfo();
            mGameGuideDetailInfo.setInfo(info);
            info.setGameId(jsonObj.getString("gameid"));
            info.setGameName(jsonObj.getString("gamename"));
            info.setIconUrl(jsonObj.getString("gameicon"));
            info.setPkgname(jsonObj.getString("pkgname"));
            info.setSize(jsonObj.getString("pkgsize"));
            info.setDownloadurl(jsonObj.getString("downloadurl"));
            info.setStartaction(jsonObj.getString("startaction"));
            String needlogin = jsonObj.getString("startaction");
            info.setNeedlogin("1".equals(needlogin)?true:false);
            mGameGuideDetailInfo.setGuideid(jsonObj.getString("guideid"));
            mGameGuideDetailInfo.setGuidetitle(jsonObj.getString("guidetitle"));
            mGameGuideDetailInfo.setGuidetime(jsonObj.getString("guidetime"));
        	String c = jsonObj.getString("collected");
        	info.setIscollected("0".equals(c)? false:true);
        	info.setComingsoon(jsonObj.getString("comingsoon"));
        	try {
        		info.setGameversion(jsonObj.getString("versionname"));
        		info.setGameversioncode(StringUtil.parseInt(jsonObj.getString("versioncode")));
			} catch (Exception e) {
			}
            
        	if (jsonObj.has("guidecontents"))
            {
                JSONArray contents = jsonObj.getJSONArray("guidecontents");
                
                ArrayList<GameGuideContent> list_guide_content = new ArrayList<GameGuideContent>();
                if (null != contents)
                {
                    for(int i=0; i<contents.length();i++)
                    {
                        JSONObject obj = contents.getJSONObject(i);
                        
                        GameGuideDetailInfo.GameGuideContent ggc = mGameGuideDetailInfo.new GameGuideContent();
                        ggc.content = obj.getString("guidecontent");
                        ggc.picurl = obj.getString("guidepic");
                        
                        list_guide_content.add(ggc);
                        
                    }
                }
                
                mGameGuideDetailInfo.setList_guide_content(list_guide_content);
            }
            
            if (jsonObj.has("htmlstring"))
            {
                mIsUseHTML = true;
                mHTMLContent = jsonObj.getString("htmlstring");
            }
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mLogger.e(e.toString());
		}
	}

	public GameGuideDetailInfo getmGameGuideDetailInfo() {
		return mGameGuideDetailInfo;
	}
	
    public boolean getIsUseHTML()
    {
        return mIsUseHTML;
    }
    
    public String getHTMLContent()
    {
        return mHTMLContent;
    }	
}
