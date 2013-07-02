package com.finlay.geomonsters.battle;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.R.drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
		_paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), _paint);
		
		
		// draw ground
		Bitmap ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
		canvas.drawBitmap(ground, 0, canvas.getHeight()/2+10, _paint);

		// load kangaroo bitmap
		Bitmap kangoo = BitmapFactory.decodeResource(getResources(), R.drawable.kangaroo);
		
		
		
		
		// matrix to flip horizontally
		Matrix flipHorizontalMatrix = new Matrix();
		flipHorizontalMatrix.setScale(-1,1);
		flipHorizontalMatrix.postTranslate(kangoo.getWidth()+20, (canvas.getHeight() - kangoo.getHeight())/2);
		
		// draw kangoo
		canvas.drawBitmap(kangoo, flipHorizontalMatrix, _paint);
		
		// kangoo name
		_paint.setColor(Color.BLACK);
		_paint.setTextSize(30);
		canvas.drawText("Kangoo", 40, 40, _paint);
	}

	public void update() {

	}

}