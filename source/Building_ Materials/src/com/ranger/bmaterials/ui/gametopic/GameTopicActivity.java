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
public class GameTopicActivity extends FragmentActivity implements OnClickListener{

	private ImageView iv_btn_back;
	private AlwaysMarqueeTextView tv_topic_name;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.game_topic_activity_layout);
		
		initView();
	}

	//初始化界面
	public void initView() {
		iv_btn_back = (ImageView) findViewById(R.id.img_back);
		tv_topic_name = (AlwaysMarqueeTextView) findViewById(R.id.header_title);
		
		iv_btn_back.setOnClickListener(this);
		tv_topic_name.setOnClickListener(this);
		
		tv_topic_name.setText(R.string.home_section_title4);
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		}
		
	}

	
}
