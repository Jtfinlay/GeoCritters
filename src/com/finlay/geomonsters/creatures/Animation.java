package com.finlay.geomonsters.creatures;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Animation {
	
	public static final int NONE = 0;
	public static final int HURT = 1;
	public static final int STRIKE = 2;
	
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
	
	public void renderFrame(Canvas c, Matrix m, Paint p) {
		
		switch (_currentAnimation) {
		case NONE:
			c.drawBitmap(_image, m, p);
			break;
		case HURT:
			renderFrame_Hurt(c, m, p);
			break;
		case STRIKE:
			renderFrame_Strike(c, m, p);
			break;
		}
		
	}
	
	private void renderFrame_Strike(Canvas c, Matrix m, Paint p) {
		
		double time = System.currentTimeMillis() - _startTime;
		
		if (time >= 500)
			m.postTranslate(-20, 0);
		else if (time > 700)
			_currentAnimation = 0;
		
		c.drawBitmap(_image, m, p);
		
	}
	
	private void renderFrame_Hurt(Canvas c, Matrix m, Paint p) {
		
		double time = System.currentTimeMillis() - _startTime;
		
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
	
	

}
