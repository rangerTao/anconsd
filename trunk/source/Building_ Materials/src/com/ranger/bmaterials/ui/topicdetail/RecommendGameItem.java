package com.ranger.bmaterials.ui.topicdetail;

import java.io.Serializable;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.PackageHelper;
import com.ranger.bmaterials.json.JSONUtil;
import com.ranger.bmaterials.mode.PackageMode;
import com.ranger.bmaterials.mode.QueryInput;

public class RecommendGameItem implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String mGameId;
    private String mPkgName;
    private String mGameName;
    private long   mPkgSize;
    private String mGameIcon;
    private String mGameStartAction;
    private String mGameDownloadUrl;
    private String mGameVersionName;
    private int    mGameVersionCode;
    private float  mGameStar;
    private long   mGameDownloadTimes;
    private String mRecommendation;
    private String mGameLableName;
    private String mGameLableColor;
    private PackageMode mPkgMode;
    
    @SuppressWarnings("unused")
    private RecommendGameItem(){}
    
    public RecommendGameItem(JSONObject json) throws JSONException
    {
        if (null != json)
        {
            JSONUtil util = JSONUtil.instance();
            
            mGameId             = util.getString(json, Constants.JSON_SPEED_DOWNLOAD_GAME_ID);
            mPkgName            = util.getString(json, Constants.JSON_GAME_PACKAGE);
            mGameName           = util.getString(json, Constants.JSON_GAMENAME);
            mPkgSize            = transferToLong(util.getString(json, Constants.JSON_PKGSIZE));
            mGameIcon           = util.getString(json, Constants.JSON_GAME_ICON);
            mGameStartAction    = util.getString(json, Constants.JSON_GAME_KEY);
            mGameDownloadUrl    = util.getString(json, Constants.JSON_SPEED_DOWNLOAD_URL);
            mGameVersionName    = util.getString(json, Constants.JSON_SPEED_DOWNLOAD_VERSIONNAME);
            mGameVersionCode    = transferToInt(util.getString(json, Constants.JSON_SPEED_DOWNLOAD_VERSIONCODE));
            mGameStar           = transferToFloat(util.getString(json, Constants.JSON_GAME_STARS));
            mGameDownloadTimes  = transferToLong(util.getString(json, Constants.JSON_GAME_DOWNLOADED_COUNT));
            mRecommendation     = util.getString(json, Constants.JSON_GAME_RECOMMENDATION);
            mGameLableName      = util.getString(json, Constants.JSON_GAME_LABLE_NAME);
            mGameLableColor     = util.getString(json, Constants.JSON_GAME_LABLE_COLOR);
            
            refreshPackageMode();
        }
    }
    
    public void refreshPackageMode()
    {
        QueryInput qi = new QueryInput(mPkgName, mGameVersionName, mGameVersionCode, mGameDownloadUrl, mGameId);
        Map<QueryInput, PackageMode> map = PackageHelper.queryPackageStatus(qi);
        
        if (null != map && map.size() > 0)
        {
            mPkgMode = map.get(qi);
        }
    }
    
    public String   getGameId()             {return mGameId;}
    public String   getPackageName()        {return mPkgName;}
    public String   getGameName()           {return mGameName;}
    public long     getPackageSize()        {return mPkgSize;}
    public String   getGameIcon()           {return mGameIcon;}
    public String   getGameStartAction()    {return mGameStartAction;}
    public String   getGameDownloadUrl()    {return mGameDownloadUrl;}
    public String   getGameVersionName()    {return mGameVersionName;}
    public int      getGameVersionCode()    {return mGameVersionCode;}
    public float    getGameStar()           {return mGameStar;}
    public long     getGameDownloadTimes()  {return mGameDownloadTimes;}
    public String   getGameRecommendation() {return mRecommendation;}
    public String   getGameLabelName()      {return mGameLableName;}
    public String   getGameLabelColor()     {return mGameLableColor;}
    public PackageMode  getPackageMode()    {return mPkgMode;}
    public void         setPackageMode(PackageMode mode)    {mPkgMode = mode;}
    
    private int transferToInt(String val)
    {
    	try{
    		if (null != val)
    		{
    			return Integer.valueOf(val).intValue();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return 0;
    	}
        
        return 0;
    }
    
    private long transferToLong(String val)
    {
    	try{
    		if (null != val)
    		{
    			return Long.valueOf(val).longValue();
    		}
    	}catch(Exception e){
    		return 0L;
    	}

    	return 0L;
    }
    
    private float transferToFloat(String val)
    {
    	try{
    		if (null != val)
    		{
    			return Float.valueOf(val).floatValue();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return 0f;
    	}
        
        return 0f;
    }

}
