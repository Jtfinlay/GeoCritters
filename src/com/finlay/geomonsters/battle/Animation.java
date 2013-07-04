package com.finlay.geomonsters.battle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Animation {
	
	public static final int NONE = 0;
	public static final int HURT = 1;
	public static final int KILL = 2;
	public static final int STRIKE = 3;
	
	private int _currentAnimation = NONE;
	private double _startTime = 0;
	private Bitmap _image;
	
	public Animation(Bitmap image) {
		_image = image;
	}
	
	public void start(int animation) {		
		_currentAnimation = animation;
		_startTime = System.currentTimeMillis();
	}
	
	public void renderFrame(Canvas c, Paint p) {
		
		switch (_currentAnimation) {
		case NONE:
			c.drawBitmap(_image, 0, 0, p);
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
		
		Matrix m = new Matrix();
		
		if (time < 300)
			m.postTranslate(-40, 0);
		else if (time < 700)
			m.postTranslate(-40 + 40*(time-300)/(700-300), 0);
		else
			_currentAnimation = 0;
		
		c.drawBitmap(_image, m, p);
		
	}
	
	private void renderFrame_Hurt(Canvas c, Paint p) {
		
		double time = System.currentTimeMillis() - _startTime;
		
		Matrix m = new Matrix();
		
		if (time > 400) {
			_currentAnimation = 0;
			c.drawBitmap(_image, m, p);
		} else if (time > 300) 
			; // draw nothing
		else if (time > 200)
			c.drawBitmap(_image, m, p);
		else if (time > 100)
			; // draw nothing
		else
			c.drawBitmap(_image, m, p);
			
	}
	
	private void renderFrame_Kill(Canvas c, Paint p) {
		
		double time = System.currentTimeMillis() - _startTime;
		
		Matrix m = new Matrix();
		
		float y = (float) ((time/400f)*c.getHeight());
		y = (y > c.getHeight()) ? c.getHeight() : y; // don't move it too far..
		
		m.postTranslate(0f, y); // hide below screen
		
		c.drawBitmap(_image, m, p);
		
	}
	
	

}
