package com.ranger.bmaterials.json;

public class JSONManager {
	private static JSONManager mManager = null;
	private JSONBuilder 		mJsonBuilder = null;
	private JSONParser	 		mJsonParser = null;
	
	public static JSONBuilder getJsonBuilder(){
		return _getInstance()._getJsonBuilder();
	}
	
	public static JSONParser getJsonParser(){
		return _getInstance()._getJsonParser();
	}
	
	private JSONManager(){
		 mJsonBuilder = new JSONBuilder();
		 mJsonParser = new JSONParser();
	}
	
	private synchronized static JSONManager _getInstance(){
		if(mManager == null){
			mManager = new JSONManager();
		}
		
		return mManager;
	}
	
	private JSONBuilder _getJsonBuilder(){
		return mJsonBuilder;
	}
	
	private JSONParser _getJsonParser(){
		return mJsonParser;
	}
	
}
