package com.ranger.lpa.connectity.bluetooth;

import android.content.Context;

public class LPABlueToothManager {

	private LPABlueToothManager _instance;
	
	private LPABlueToothManager(Context context) {
	
	};
	
	public LPABlueToothManager getInstance(Context context) {
		
		if(_instance == null){
			_instance = new LPABlueToothManager(context);
		}
		
		return _instance;
	}
}
