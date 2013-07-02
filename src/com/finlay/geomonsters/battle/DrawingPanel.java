package com.finlay.geomonsters.battle;

import com.finlay.geomonsters.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*----- The drawing surface -----*/
class DrawingPanel extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "DrawingPanel";

	private GameThread _Thread;							// Game Thread
	private Paint _paint;								// Paint object
	private Creature _creatureUser, _creatureOther;		// Both creatures

	private DrawingPanelListener customListener;

	private Action	COMMAND;							// For managing action cycle

	private int canvas_width, canvas_height;			// Canvas dimensions

	public DrawingPanel(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		getHolder().addCallback(this);
		_creatureUser = ResourceManager.newCreature(getResources(), "Kangoo");
		_creatureOther = ResourceManager.newCreature(getResources(), "Squirtle");

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

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();

		return true;
	}

	/**
	 * Draw
	 * @param canvas
	 */
	public void render(Canvas canvas) {

		float dx, dy;

		// background
		_paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, canvas_width, canvas_height, _paint);

		// draw ground
		Bitmap ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
		canvas.drawBitmap(ground, 0, canvas_height/2+10, _paint);
		canvas.drawBitmap(ground, (canvas_width - ground.getWidth()), canvas_height/2+10, _paint);

		// left creature
		canvas.save();
		canvas.scale(-1f, 1f, .5f*canvas_width, 0);					// flip horizontally
		dx = (canvas_width - _creatureUser.getWidth() - 20);
		dy = (canvas_height-_creatureUser.getHeight())/2+20;							
		canvas.translate(dx, dy);									// translate position
		_creatureUser.render(canvas, _paint);						// position is top-left of image
		canvas.restore();

		// right creature
		canvas.save();
		dx = (canvas_width - _creatureOther.getWidth() - 20);
		dy = (canvas_height - _creatureOther.getHeight())/2+20;
		canvas.translate(dx, dy);									// translate position
		_creatureOther.render(canvas, _paint);						// position is top-left of image
		canvas.restore();


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

	/**
	 * Cycle through actions to perform
	 */
	public void update() {


		if (COMMAND != null && System.currentTimeMillis() > COMMAND.time) {

			if (COMMAND.getAttacker() == Action.ATTACKER_PLAYR) {
				// PLAYER ATTACKS!
				switch (COMMAND.step) {
				case 0:
					// Shows attack message & animation
					showMessage(_creatureUser.getName() + " uses " + COMMAND.getName() + ".");
					_creatureUser.performAnimation(COMMAND.getAnimationType());

					COMMAND.time = System.currentTimeMillis() + 1000;
					COMMAND.step++;
					break;
				case 1:
					// Say it is super effective or w/e
					showMessage("It might have been super effective!");
					_creatureOther.performAnimation(Animation.HURT);
					COMMAND.time = System.currentTimeMillis() + 1000;
					COMMAND.step++;
					break;
				case 2:
					// Lower enemy hp
					_creatureOther.Hurt(10);
					COMMAND.step++;
					break;
				case 3:
					// Reset all
					COMMAND = null;
					performAttack_Other("");
					break;
				}
			} else {
				// OPPONENT ATTACKS!
				switch (COMMAND.step) {
				case 0:
					// Shows attack message & animation
					showMessage(_creatureOther.getName() + " uses " + COMMAND.getName() + ".");
					_creatureOther.performAnimation(COMMAND.getAnimationType());

					COMMAND.time = System.currentTimeMillis() + 1000;
					COMMAND.step++;
					break;
				case 1:
					// Say it is super effective or w/e
					showMessage("It might have been super effective!");
					_creatureUser.performAnimation(Animation.HURT);
					COMMAND.time = System.currentTimeMillis() + 1000;
					COMMAND.step++;
					break;
				case 2:
					// Lower enemy hp
					_creatureUser.Hurt(10);
					COMMAND.step++;
					break;
				case 3:
					// Reset all
					COMMAND = null;
					showButtons();
					break;
				}
				
			}
		}


	}

	/**
	 * Local player uses an attack. Called by BattleActivity
	 */
	public void performAttack_Player(String attackName) {

		COMMAND = ResourceManager.getAttack(getResources(), attackName, Action.ATTACKER_PLAYR);

	}

	/**
	 * Other player uses an attack. 
	 */
	public void performAttack_Other(String attackName) {
		//TODO: Maybe not have it random? I dunno.

		// Get random attack
		int index = (int) Math.floor(Math.random() * _creatureOther.getAttackList().size());
		COMMAND = ResourceManager.getAttack(getResources(), _creatureOther.getAttackList().get(index), Action.ATTACKER_OTHER);
	}

	/**
	 * Uses DrawingPanelListener interface to send information to the bottom panel.
	 * @param listen
	 */
	public void setCustomListener(DrawingPanelListener listen) {
		customListener = listen;
	}
	/**
	 * Write message on bottom panel
	 * @param message
	 */
	private void showMessage(String message) {
		customListener.showMessage(message);
	}
	/**
	 * Hide TextView, show action buttons
	 */
	private void showButtons() {
		customListener.showButtonView();
	}



}