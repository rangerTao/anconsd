package com.ranger.bmaterials.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil
{
    private static JSONUtil util = null;
    
    public static JSONUtil instance()
    {
        if (null == util)
        {
            util = new JSONUtil();
        }
        
        return util;
    }

    private JSONUtil(){}
 
    public int getInt(JSONObject obj, String key) throws JSONException
    {
        int val = 0;
        
        if (null != obj && null != key && obj.has(key))
        {
            val = obj.getInt(key);
        }
        
        return val;
    }
    
    public boolean getBoolean(JSONObject obj, String key) throws JSONException 
    {
        boolean val = false;
        
        if (null != obj && null != key && obj.has(key))
        {
            val = obj.getBoolean(key);
        }
        
        return val;
    }
    
    public String getString(JSONObject obj, String key) throws JSONException 
    {
        String val = "";
        
        if (null != obj && null != key && obj.has(key))
        {
            val = obj.getString(key);
        }
        
        return val;
    }
    
    public JSONArray getArray(JSONObject obj, String key) throws JSONException 
    {
        if (null != obj && null != key && obj.has(key))
        {
            return obj.getJSONArray(key);
        }
        
        return null;
    }
    
    public JSONObject getJSONObject(JSONObject obj, String key) throws JSONException
    {
        if (null != obj && null != key && obj.has(key))
        {
            return obj.getJSONObject(key);
        }
        
        return null;
    }
    
}
