package com.finlay.geomonsters.battle;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.R.drawable;
import com.finlay.geomonsters.creatures.Animation;
import com.finlay.geomonsters.creatures.Creature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/*----- The drawing surface -----*/
class DrawingPanel extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "DrawingPanel";

	private GameThread _Thread;							// Game Thread
	private Paint _paint;								// Paint object
	private Creature _creatureUser, _creatureOther;		// Both creatures
	
	private LinearLayout 	_btnPanel;					// Menu button wrapper
	private Button			_btn1, _btn2, _btn3, _btn4;	// Menu buttons
	private TextView		_msgPanel;					// Menu text holder

	private int canvas_width, canvas_height;			// Canvas dimensions

	public DrawingPanel(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		getHolder().addCallback(this);
		_creatureUser = new Creature(getResources(), "Kangoo");
		_creatureOther = new Creature(getResources(), "Squirtle");
		
		_paint = new Paint();
		_Thread = new GameThread(getHolder(), this);

		setFocusable(true);
	}
	public void giveLayout(LinearLayout btnPanel, TextView msgPanel) {
		_btnPanel = btnPanel;
		_msgPanel = msgPanel;
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
		Rect surface = holder.getSurfaceFrame();
		canvas_width = surface.width();
		canvas_height = surface.height();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(TAG, "surfaceCreated");
		
		Rect surface = holder.getSurfaceFrame();
		canvas_width = surface.width();
		canvas_height = surface.height();

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
		canvas.drawRect(0, 0, canvas_width, canvas_height, _paint);

		// draw ground
		Bitmap ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
		canvas.drawBitmap(ground, 0, canvas_height/2+10, _paint);
		canvas.drawBitmap(ground, (canvas_width - ground.getWidth()), canvas_height/2+10, _paint);

		// matrix to flip horizontally
		Matrix userMatrix = new Matrix();
		userMatrix.setScale(-1,1);
		userMatrix.postTranslate(_creatureUser.getWidth()+20, (canvas_height - _creatureUser.getHeight())/2);
		
		// matrix to put on left side
		Matrix enemyMatrix = new Matrix();
		enemyMatrix.postTranslate((canvas_width - _creatureOther.getWidth()-20), (canvas_height - _creatureOther.getHeight())/2);

		// draw creatures
		_creatureUser.render(canvas, _paint, userMatrix);
		_creatureOther.render(canvas, _paint, enemyMatrix);
		
		// draw names
		_paint.setTextSize(25);
		_paint.setColor(Color.BLACK);
		canvas.drawText(_creatureUser.getName(), .05f*canvas_width, .14f*canvas_height, _paint);
		canvas.drawText(_creatureOther.getName(), .58f*canvas_width, .14f*canvas_height, _paint);
		
		// draw health bars
		float xi, xf, y, xm;
		_paint.setStrokeWidth(.01f*canvas_height);
		
		xi = .05f*canvas_width;
		xf = .33f*canvas_width;
		y = .22f*canvas_height;
		xm = xi + (xf - xi)*_creatureUser.getHealthPercent();
		
		_paint.setColor(Color.GREEN);
		canvas.drawLine(xi, y, xm, y, _paint);
		_paint.setColor(Color.RED);
		canvas.drawLine(xm, y, xf, y, _paint);
		
		xi = .58f*canvas_width;
		xf = .86f*canvas_width;
		y = .22f*canvas_height;
		xm = xi + (xf - xi)*_creatureOther.getHealthPercent();
		
		_paint.setColor(Color.GREEN);
		canvas.drawLine(xi, y, xm, y, _paint);
		_paint.setColor(Color.RED);
		canvas.drawLine(xm, y, xf, y, _paint);
		

	}

	public void update() {

	}
	
	public void performUserAttack(String attackName) {
		
		Attack attack = new Attack(getResources(), attackName);
		
		// TODO: Calculate damage and stuff from Creature class
		_creatureUser.performAnimation(Animation.STRIKE);
		_creatureOther.performAnimation(Animation.HURT);
		_creatureOther.Hurt(10);
		
	}
	
	public void showMessage(String message) {
		_btnPanel.setVisibility(View.INVISIBLE);
		_msgPanel.setVisibility(View.VISIBLE);
		_msgPanel.setText(message);
	}
	
	public void showButtons() {
		_btnPanel.setVisibility(View.VISIBLE);
		_msgPanel.setVisibility(View.INVISIBLE);
	}
	
	

}