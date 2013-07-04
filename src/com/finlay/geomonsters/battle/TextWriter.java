package com.finlay.geomonsters.battle;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class TextWriter extends TextView {

	private static final String TAG = "TextView";
	
	private CharSequence mText;
	private int mIndex;
	private long mDelay = 500; // Default 500ms delay

	
	public TextWriter(Context context) {
		super(context);
	}
	public TextWriter(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private Handler mHandler = new Handler();
	private Runnable characterAdder = new Runnable() {
		@Override
		public void run() {
			setText(mText.subSequence(0, mIndex++));
			if (mIndex <= mText.length())
				mHandler.postDelayed(characterAdder, mDelay);
		}
	};
	public boolean isTyping() {
		return mIndex < mText.length();
	}
	public void forceEnd() {
		Log.v(TAG, "Force End");
		mIndex = mText.length();
	}
	public void animateText(CharSequence text) {
		Log.v(TAG, "animateText: " + text);
		mText = text;
		mIndex = 0;
		
		setText("");
		mHandler.removeCallbacks(characterAdder);
		mHandler.postDelayed(characterAdder, mDelay);
	}
	
	public void setCharacterDelay(long millis) {
		mDelay = millis;
	}
}
