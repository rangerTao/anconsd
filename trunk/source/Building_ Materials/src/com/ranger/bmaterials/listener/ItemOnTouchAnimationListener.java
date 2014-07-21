package com.ranger.bmaterials.listener;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.ranger.bmaterials.R;

public class ItemOnTouchAnimationListener implements OnTouchListener {

	private AnimationListener l;
	private Animation sa_smaller, sa_bigger;
	private View animationView;

	public ItemOnTouchAnimationListener(Context cx, AnimationListener l) {
		sa_smaller = AnimationUtils.loadAnimation(cx,
				R.anim.scale_selector_smaller);
		sa_bigger = AnimationUtils.loadAnimation(cx,
				R.anim.scale_selector_bigger);
		this.l = l;
	}

	public void setAnimationView(View animationView) {
		this.animationView = animationView;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (animationView == null)
			animationView = v;
		ViewGroup p = (ViewGroup) animationView.getParent();
		p.setClipChildren(false);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
//			animationView.startAnimation(sa_bigger);
			break;
		case MotionEvent.ACTION_UP:
//			animationView.startAnimation(sa_smaller);
			sa_smaller.setAnimationListener(l);
			break;
		case MotionEvent.ACTION_CANCEL:
//			animationView.startAnimation(sa_smaller);
			sa_smaller.setAnimationListener(null);
			break;
		}
		return true;
	}

}
