package com.ranger.lpa.ui.activity;

import com.ranger.lpa.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

/**
 * 
* @Description: TODO
* 
* @author taoliang(taoliang@baidu-mgame.com)
* @date 2014年5月31日 下午8:33:39 
* @version V
*
 */
public class SplashActivity extends BaseActivity{

	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		
	}

	public void login_enter(View view){
        no_login_enter(view);
    }

    public void no_login_enter(View view){

        finish();

        Intent intentMain = new Intent(getApplicationContext(),LPAMainActivity.class);
        startActivity(intentMain);
    }

    @Override
    public void initView() {

    }
}
