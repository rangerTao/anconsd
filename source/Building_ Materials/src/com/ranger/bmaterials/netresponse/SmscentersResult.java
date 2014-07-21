package com.ranger.bmaterials.netresponse;

public class SmscentersResult extends BaseResult {

	private String cnMobileNum;
	private String cnUnicomNum;
	private String cnTelecomNum;
	private String commonNum;
	
	public SmscentersResult() {
		super();
	}
	public String getCnMobileNum() {
		return cnMobileNum;
	}
	public void setCnMobileNum(String cnMobileNum) {
		this.cnMobileNum = cnMobileNum;
	}
	public String getCnUnicomNum() {
		return cnUnicomNum;
	}
	public void setCnUnicomNum(String cnUnicomNum) {
		this.cnUnicomNum = cnUnicomNum;
	}
	public String getCnTelecomNum() {
		return cnTelecomNum;
	}
	public void setCnTelecomNum(String cnTelecomNum) {
		this.cnTelecomNum = cnTelecomNum;
	}
	public String getCommonNum() {
		return commonNum;
	}
	public void setCommonNum(String commonNum) {
		this.commonNum = commonNum;
	}
	
}
