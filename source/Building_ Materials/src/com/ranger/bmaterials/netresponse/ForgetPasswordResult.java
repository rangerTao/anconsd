package com.ranger.bmaterials.netresponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.ranger.bmaterials.app.Constants;

public class ForgetPasswordResult extends BaseResult {
	private int flag = 2; // 1:已绑定 2:未绑定
	private String phonenum = "";
	private String servicenum = "";

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String getServicenum() {
		return servicenum;
	}

	public void setServicenum(String servicenum) {
		this.servicenum = servicenum;
	}

}
