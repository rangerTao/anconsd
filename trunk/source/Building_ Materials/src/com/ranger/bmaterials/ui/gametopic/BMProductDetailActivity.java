package com.ranger.bmaterials.ui.gametopic;

import com.ranger.bmaterials.R;
import com.ranger.bmaterials.view.AlwaysMarqueeTextView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
* @Description: 专题界面
* 
* @author taoliang(taoliang@baidu-mgame.com)
* @date 2014年6月5日 上午11:04:27 
* @version V
*
 */
public class BMProductDetailActivity extends FragmentActivity implements OnClickListener{

	private ImageView iv_btn_back;
    public static final String SUPPLY_ID = "supplyId";

    private String supplyId;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
        supplyId = getIntent().getStringExtra(SUPPLY_ID);

        setContentView(R.layout.game_topic_activity_layout);

		initView();
	}

	//初始化界面
	public void initView() {
		iv_btn_back = (ImageView) findViewById(R.id.btn_back);

		iv_btn_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		}
		
	}

	
}
