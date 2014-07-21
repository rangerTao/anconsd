package com.ranger.bmaterials.animation;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public final class HeaderCoinTvAnimation extends AnimationSet {

	public HeaderCoinTvAnimation(boolean shareInterpolator) {
		super(shareInterpolator);
		// TODO Auto-generated constructor stub
		setFillAfter(true);
	}

	public void init(final View v) {
		// 第一阶段
		TranslateAnimation ta = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
				-0.35f);
		ta.setFillAfter(true);
		ta.setDuration(1000);
		ta.setAnimationListener(new SimpleAnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				// 第二阶段 停两秒 然后淡出
				TranslateAnimation ta = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, -0.35f,
						Animation.RELATIVE_TO_SELF, -1);
				ta.setDuration(1000);
				ta.setStartOffset(1000);

				AlphaAnimation aa = new AlphaAnimation(1, 0);
				aa.setDuration(1000);
				aa.setStartOffset(1000);

				addAnimation(ta);
				addAnimation(aa);
				v.startAnimation(HeaderCoinTvAnimation.this);
			}
		});
		v.startAnimation(ta);
	}

}
