package com.ranger.lpa;

import android.app.Application;

/**
 * 
* @Description: TODO
* 
* @author taoliang(taoliang@baidu-mgame.com)
* @date 2014年5月31日 下午8:33:54 
* @version V
*
 */
public class LPApplication extends Application{

	private static LPApplication _instants;
	
	public static LPApplication getLPApplication(){
		return _instants;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		_instants = this;
	}
}
