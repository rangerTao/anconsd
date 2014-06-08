package com.ranger.lpa.connectity.wifi;

import android.content.Context;

public class LPAWifiManager {

	private LPAWifiManager _instance;
	
	private LPAWifiManager(Context context){
	
	}
	
	public LPAWifiManager getInstance(Context context){
		if(_instance == null){
			_instance = new LPAWifiManager(context);
		}
		
		return _instance;
	}
	
}
