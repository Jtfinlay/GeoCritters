package com.finlay.geomonsters.battle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

public class Animation {

	public static final int NONE = 0;				// No animation. Just draw.
	public static final int HURT = 1;				// Creature hurt (flashing)
	public static final int KILL = 2;				// Creature killed (fast fall)
	public static final int STRIKE = 3;				// Creature attacks (quick jab)

	public static final int SCREEN_BUBBLE = 100;

	private int _currentAnimation = NONE;
	private double _startTime = 0;
	private Bitmap _image;
	private RectF _destRect;

	public Animation(Bitmap image, RectF destRect) {
		_image = image;
		_destRect = destRect;
	}

	public void start(int animation) {		
		_currentAnimation = animation;
		_startTime = System.currentTimeMillis();
	}

	public void renderCreatureFrame(Canvas c, Paint p) {

		switch (_currentAnimation) {
		case NONE:
			c.drawBitmap(_image, null, _destRect, p);
			break;
		case HURT:
			renderFrame_Hurt(c, p);
			break;
		case KILL:
			renderFrame_Kill(c, p);
			break;
		case STRIKE:
			renderFrame_Strike(c, p);
			break;
		}
	}

	private void renderFrame_Strike(Canvas c, Paint p) {

		float time = (float) (System.currentTimeMillis() - _startTime);
		RectF drawRect = new RectF(_destRect);
		
		float dx = .22f*drawRect.width();

		if (time < 300)
			drawRect.offset(-dx, 0);
		else if (time < 700)
			drawRect.offset(-dx + dx*(time-300)/(700-300), 0);
		else
			_currentAnimation = 0;

		c.drawBitmap(_image, null, drawRect, p);

	}

	private void renderFrame_Hurt(Canvas c, Paint p) {

		double time = System.currentTimeMillis() - _startTime;
		
		if (time > 400) {
			_currentAnimation = 0;
			c.drawBitmap(_image, null, _destRect, p);
		} else if (time > 300) 
			; // draw nothing
		else if (time > 200)
			c.drawBitmap(_image, null, _destRect, p);
		else if (time > 100)
			; // draw nothing
		else
			c.drawBitmap(_image, null, _destRect, p);

	}

	private void renderFrame_Kill(Canvas c, Paint p) {

		double time = System.currentTimeMillis() - _startTime;
		RectF drawRect = new RectF(_destRect);

		float y = (float) ((time/400f)*c.getHeight());
		y = (y > c.getHeight()) ? c.getHeight() : y; // don't move it too far..

		drawRect.offset(0, y); // hide below screen
		
		c.drawBitmap(_image, null, drawRect, p);

	}


}
