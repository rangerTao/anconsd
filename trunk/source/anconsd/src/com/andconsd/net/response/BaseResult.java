package com.andconsd.net.response;

import org.json.JSONException;
import org.json.JSONObject;

import com.andconsd.DcError;
import com.andconsd.json.JSONParser;
import com.andconsd.json.JSONUtil;
import com.andconsd.utils.Constants;
import com.andconsd.utils.MyLogger;

public class BaseResult {

	protected static MyLogger mLogger = MyLogger.getLogger(JSONParser.class
			.getName());
	protected int errorcode = DcError.DC_Error;
	protected String errormsg;
	protected String tag;
	protected int mRequestID;

	public int getRequestID() {
		return mRequestID;
	}

	public void setRequestID(int requestID) {
		this.mRequestID = requestID;
	}

	public final int getErrorcode() {
		return errorcode;
	}

	public final void setErrorcode(int errorcode) {
		this.errorcode = errorcode;
	}

	public final String getErrormsg() {
		return errormsg;
	}

	public final void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public final String getTag() {
		return tag;
	}

	public final void setTag(String tag) {
		this.tag = tag;
	}

	public BaseResult(JSONObject json) throws JSONException {
		errorcode = JSONUtil.instance()
				.getInt(json, Constants.JSON_ERROR_CODE);
		errormsg = JSONUtil.instance().getString(json,
				Constants.JSON_ERROR_MSG);
		tag = JSONUtil.instance().getString(json, Constants.JSON_TAG);
	}

	public BaseResult() {
	}
}
