package com.ranger.bmaterials.ui.topicdetail;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.json.JSONUtil;
import com.ranger.bmaterials.netresponse.BaseResult;

public class TopicDetailMoreGamesData extends BaseResult
{
    private ArrayList<RecommendGameItem> mDataList;
    private int mTotalCount = 0;
    
    public List<RecommendGameItem> getDataList()
    {
        return mDataList;
    }
    
    public int getTotalCount()
    {
        return mTotalCount;
    }

    public TopicDetailMoreGamesData(JSONObject json) throws JSONException
    {
        super(json);
        
        JSONUtil util = JSONUtil.instance();
        JSONArray array = util.getArray(json, Constants.BM_JSON_DATA_LIST);
        
        mTotalCount = util.getInt(json, Constants.JSON_GAMECOUNT);
        
        if (null != array && array.length() > 0)
        {
            mDataList = new ArrayList<RecommendGameItem>();
            
            for (int index = 0; index < array.length(); ++index)
            {
                JSONObject tmp = array.getJSONObject(index);
                RecommendGameItem item = new RecommendGameItem(tmp);
                
                mDataList.add(item);
            }
        }
    }

    public TopicDetailMoreGamesData()
    {
    }

}
