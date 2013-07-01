package com.finlay.geomonsters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

/*----- The drawing surface -----*/
class DrawingPanel extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final String TAG = "DrawingPanel";
	
	private GameThread _Thread;
	private Paint _paint;
	
	public DrawingPanel(Context context) {
		super(context);
		// adding callback (this) to intercept events
		getHolder().addCallback(this);
		
		// Initialize variables and set values
		_paint = new Paint();
		

		// create game loop thread3
		_Thread = new GameThread(getHolder(), this);
		
		// make DrawingPanel focusable so that it can handle events
		setFocusable(true);
	}
	
	public DrawingPanel(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		
		getHolder().addCallback(this);
		
		_paint = new Paint();
		_Thread = new GameThread(getHolder(), this);
		
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v(TAG, "surfaceChanged");
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(TAG, "surfaceCreated");
		
		_Thread.setRunning(true);
		_Thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(TAG, "surfaceDestroyed");
		
		boolean retry = true;
		_Thread.setRunning(false);
		while (retry) {
			try {
				_Thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will keep trying
			}
		}
	}
	
	public void render(Canvas canvas) {
		
		// background
		_paint.setColor(Color.RED);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), _paint);
		
		Bitmap kangoo = BitmapFactory.decodeResource(getResources(), R.drawable.kangaroo);
		canvas.drawBitmap(kangoo, 130, 10, null);
	}
	
	public void update() {
		
	}
	
}