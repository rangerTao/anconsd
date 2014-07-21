package com.ranger.bmaterials.animation;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

public class HeaderCoinIconAnimation extends AnimationSet{

	public HeaderCoinIconAnimation(boolean shareInterpolator) {
		super(shareInterpolator);
		// TODO Auto-generated constructor stub
	}

	
	public void init(){
		ScaleAnimation sa = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);

		sa.setDuration(1000);
		sa.setRepeatMode(Animation.RESTART);
		sa.setRepeatCount(2);

		AlphaAnimation aa = new AlphaAnimation(1, 0);
		aa.setRepeatMode(Animation.RESTART);
		aa.setDuration(1000);
		aa.setRepeatCount(2);

		addAnimation(sa);
		addAnimation(aa);
	}
}
