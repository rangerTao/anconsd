package com.ranger.bmaterials.ui.topicdetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.json.JSONUtil;
import com.ranger.bmaterials.netresponse.BaseResult;

public class TopicDetailData extends BaseResult implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String  mDetailIcon;
    private String  mDetailName;
    private String  mDetailDescription;
    private volatile ArrayList<RecommendGameItem> mRecommendList;
    
    public String getDetailIcon()
    {
        return mDetailIcon;
    }
    
    public String getDetailName()
    {
        return mDetailName;
    }
    
    public String getDetailDescription()
    {
        return mDetailDescription;
    }
    
    private void initList()
    {
        if (null == mRecommendList)
        {
            mRecommendList = new ArrayList<RecommendGameItem>();
        }
    }
    
    public ArrayList<RecommendGameItem> getRecommendList()
    {
        return mRecommendList;
    }
    
    public void appendRecommendList(List<RecommendGameItem> list)
    {
        initList();
        mRecommendList.addAll(list);
    }

    public TopicDetailData(JSONObject json) throws JSONException
    {
        super(json);
        
        JSONUtil    util    = JSONUtil.instance();
        JSONArray   array   = util.getArray(json, Constants.BM_JSON_DATA_LIST);
        
        mDetailIcon         = util.getString(json, Constants.JSON_BANNER_ICON);
        mDetailName         = util.getString(json, Constants.JSON_TOPIC_NAME);
        mDetailDescription  = util.getString(json, Constants.JSON_TOPIC_DESCRIPTION);
        
        if (null != array && array.length() > 0)
        {
            initList();
            mRecommendList.clear();
            
            for (int index = 0; index < array.length(); ++index)
            {
                JSONObject tmp = array.getJSONObject(index);
                RecommendGameItem item = new RecommendGameItem(tmp);
                
                mRecommendList.add(item);
            }
        }
    }

    @SuppressWarnings("unused")
    private TopicDetailData()
    {
    }
    
}
