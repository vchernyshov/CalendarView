package ua.insomnia;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeDetector extends GestureDetector.SimpleOnGestureListener {

	private float SWIPE_MIN_DISTANCE = 100;
	private OnSwipeListener swipeListener;

	public void setSwipeListener(OnSwipeListener swipeListener) {
		this.swipeListener = swipeListener;
	}
	
	public SwipeDetector (OnSwipeListener listener) {
		swipeListener = listener;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float dx = e2.getX() - e1.getX();
		if (Math.abs(dx) > SWIPE_MIN_DISTANCE) {
			if (dx < 0)
				if (swipeListener != null) {
					swipeListener.onSwipeLeft();
				}
			if (dx > 0)
				if (swipeListener != null) {
					swipeListener.onSwipeRight();
				}
		}

		return true;

	}

}
