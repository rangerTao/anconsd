package com.ranger.bmaterials.mode;

public  class QueryInput {
	public  String packageName ;
	public  String version;
	public  int versionCode ;
	public  String downloadUrl ;
	public String gameId ;
	
	
	public QueryInput() {
		super();
		// TODO Auto-generated constructor stub
	}


	public QueryInput(String packageName, String version, int versionCode,
			String downloadUrl, String gameId) {
		super();
		this.packageName = packageName;
		this.version = version;
		this.versionCode = versionCode;
		this.downloadUrl = downloadUrl;
		this.gameId = gameId;
	}


	@Override
	public String toString() {
		return "QueryInput [packageName=" + packageName + ", version="
				+ version + ", versionCode=" + versionCode
				+ ", downloadUrl=" + downloadUrl + ", gameId=" + gameId
				+ "]";
	}
	
	
}