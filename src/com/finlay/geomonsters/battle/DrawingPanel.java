package com.finlay.geomonsters.battle;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.R.drawable;
import com.finlay.geomonsters.creatures.Creature;

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
	private Creature _creatureUser, _creatureOther;


	public DrawingPanel(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		getHolder().addCallback(this);
		_creatureUser = new Creature(getResources(), "Kangoo");
		_creatureOther = new Creature(getResources(), "Squirtle");

		_paint = new Paint();
		_Thread = new GameThread(getHolder(), this);

		setFocusable(true);
	}

	public Creature getCreature_User() {
		return _creatureUser;
	}
	public Creature getCreature_Other() {
		return _creatureOther;
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
		canvas.drawBitmap(ground, (canvas.getWidth() - ground.getWidth()), canvas.getHeight()/2+10, _paint);

		// matrix to flip horizontally
		Matrix userMatrix = new Matrix();
		userMatrix.setScale(-1,1);
		userMatrix.postTranslate(_creatureUser.getWidth()+20, (canvas.getHeight() - _creatureUser.getHeight())/2);
		
		// matrix to put on left side
		Matrix enemyMatrix = new Matrix();
		enemyMatrix.postTranslate((canvas.getWidth() - _creatureOther.getWidth()-20), (canvas.getHeight() - _creatureOther.getHeight())/2);

		// draw creatures
		_creatureUser.render(canvas, _paint, userMatrix);
		_creatureOther.render(canvas, _paint, enemyMatrix);
		

	}

	public void update() {

	}

}