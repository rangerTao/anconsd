package com.ranger.bmaterials.netresponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;
import com.ranger.bmaterials.app.DcError;
import com.ranger.bmaterials.app.MineProfile;
import com.ranger.bmaterials.json.JSONParser;
import com.ranger.bmaterials.json.JSONUtil;
import com.ranger.bmaterials.tools.MyLogger;

public class BaseResult {

    protected int success;
    protected String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    protected static MyLogger mLogger = MyLogger.getLogger(JSONParser.class.getName());
	protected int		mErrorCode = DcError.DC_Error;
	protected String	mErrorString;
	protected String    mTag;
	protected int    mRequestID;
	
	public int getRequestID() {
		return mRequestID;
	}
	public void setRequestID(int requestID) {
		this.mRequestID = requestID;
	}
	
	public int getErrorCode() {
		return mErrorCode;
	}
	public void setErrorCode(int errorCode) {
		this.mErrorCode = errorCode;
	}
	public String getErrorString() {
		return mErrorString;
	}
	public void setErrorString(String errorString) {
		this.mErrorString = errorString;
	}
	public String getTag(){
		return mTag;
	}
	public void setTag(String tag){
		this.mTag = tag;
	}
	
	public BaseResult(JSONObject json) throws JSONException
	{
	    mErrorCode = JSONUtil.instance().getInt(json, Constants.JSON_ERROR_CODE);
	    mErrorString = JSONUtil.instance().getString(json, Constants.JSON_ERROR_MSG);
        mTag = JSONUtil.instance().getString(json, Constants.JSON_TAG);
        success = JSONUtil.instance().getInt(json,"success");

        if(success == 3){
            MineProfile.getInstance().setIsLogin(false);
            MineProfile.getInstance().Reset();
        }
	}
	
	public BaseResult(){}
}

