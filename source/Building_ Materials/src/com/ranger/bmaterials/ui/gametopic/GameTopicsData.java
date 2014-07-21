package com.ranger.bmaterials.ui.gametopic;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.json.JSONUtil;
import com.ranger.bmaterials.netresponse.BaseResult;

public class GameTopicsData extends BaseResult implements Serializable
{

    private static final long serialVersionUID = 1L;
    

    /** @download -- @游戏专题 -- 专题列表 **/
    public static final String JSON_TOPIC_LIST             = "subjectlist";

    /** @download -- @游戏专题 -- 专题banner图**/
    public static final String JSON_BANNER_ICON            = "subjecticon";
    /** @download -- @游戏专题 -- 专题名称 **/
    public static final String JSON_TOPIC_NAME             = "subjectname";
    /** @download -- @游戏专题 -- 专题描述 **/
    public static final String JSON_TOPIC_DESCRIPTION      = "subjectdesc";
    /** @download -- @游戏专题 -- 专题id **/
    public static final String JSON_TOPIC_ID               = "subjectid";
    /** @download -- @游戏专题 -- 专题总数 **/
    public static final String JSON_TOPIC_COUNT            = "subjectcount";
    
    private int                             mTotalCount;
    private ArrayList<GameTopicsDataItem>   mDataList;
    
    public void setDataList(ArrayList<GameTopicsDataItem> array)
    {
        mDataList = array;
    }
    
    public ArrayList<GameTopicsDataItem> getDataList()
    {
        return mDataList;
    }
    
    public void setTotalCount(int count)
    {
        mTotalCount = count;
    }
    
    public final int getTotalCount()
    {
        return mTotalCount;
    }

    public GameTopicsData(JSONObject json) throws JSONException
    {
        super(json);
        
        if (null != json)
        {
            JSONUtil util = JSONUtil.instance();
            JSONArray list = util.getArray(json, Constants.JSON_TOPIC_LIST);
            
            mTotalCount = util.getInt(json, Constants.JSON_TOPIC_COUNT);
            
            if (null == mDataList)
            {
                mDataList = new ArrayList<GameTopicsDataItem>();
            }
            else
            {
                mDataList.clear();
            }
            
            if (null != list && list.length() > 0)
            {
                for (int index = 0; index < list.length(); ++index)
                {
                    JSONObject tmp = list.getJSONObject(index);
                    GameTopicsDataItem item = new GameTopicsDataItem();
                    
                    item.setBannerIcon(util.getString(tmp, Constants.JSON_BANNER_ICON));
                    item.setDescription(util.getString(tmp, Constants.JSON_TOPIC_DESCRIPTION));
                    item.setId(util.getString(tmp, Constants.JSON_TOPIC_ID));
                    item.setName(util.getString(tmp, Constants.JSON_TOPIC_NAME));
                    
                    mDataList.add(item);
                }
            }
        }
    }

}
