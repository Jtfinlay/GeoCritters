/*
 * GeoCritters. Real-world creature encounter game.
 * Copyright (C) 2013 James Finlay
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
