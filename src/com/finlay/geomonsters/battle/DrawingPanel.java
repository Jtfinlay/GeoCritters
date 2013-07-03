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

	private static final int GAME_STATE_INPUT			= -1;
	private static final int GAME_STATE_SETUP			= 0;
	private static final int GAME_STATE_PLAYERATTACK	= 1;
	private static final int GAME_STATE_ENEMYATTACK		= 2;
	private static final int GAME_STATE_PLAYERDEATH		= 3;
	private static final int GAME_STATE_ENEMYDEATH		= 4;

	private int 	GAME_STATE 	= GAME_STATE_SETUP;		// Current state of game
	private Attack 	ATTACK;								// Info about any Actions 
	private int 	GAME_STEP 	= 0;					// Current step in state
	private long 	TIMER	= 0;						// Time for current step

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

		// only do something if the prior event is finished
		if (System.currentTimeMillis() > TIMER) {

			switch (GAME_STATE) {
			case GAME_STATE_SETUP:

				//TODO: Big opening thingy where we summon our creature and w/e

				// Fastest creature gets first attack
				if (_creatureOther.getSpeed() > _creatureUser.getSpeed()) {
					// enemy attacks
					performAttack_Other();
				} else {
					// user gets attack - wait for input
					setGameState(GAME_STATE_INPUT);
					showButtons();
				}

				break;


			case GAME_STATE_PLAYERATTACK:

				// Cycle through the steps:
				switch (GAME_STEP) {

				case 0:
					// Attack Message & Animation
					ATTACK.init(getResources(), _creatureUser, _creatureOther);

					showMessage(_creatureUser.getName() + " uses " + ATTACK.getName() + ".");
					_creatureUser.performAnimation(ATTACK.getAnimationType());

					TIMER = System.currentTimeMillis() + 1500;
					GAME_STEP++;
					break;

				case 1:		
					// Effectiveness Message
					String effectiveMsg = ATTACK.getEffectiveMessage();

					if (!effectiveMsg.equals("")) {
						showMessage(effectiveMsg);
						TIMER = System.currentTimeMillis() + 1500;
					}
					GAME_STEP++;
					break;

				case 2:
					// Perform HIT animation
					if (ATTACK.getDamageDealt() > 0 ) {
						_creatureOther.performAnimation(Animation.HURT);
						TIMER = System.currentTimeMillis() + 500;
					}
					GAME_STEP++;
					break;

				case 3:
					// Lower HP
					if (ATTACK.getDamageDealt() > 0 ) {
						_creatureOther.Hurt(ATTACK.getDamageDealt());
						TIMER = System.currentTimeMillis() + 500;
					}
					GAME_STEP++;
					break;

				case 4:
					if (_creatureOther.getHP() <= 0) {
						setGameState(GAME_STATE_ENEMYDEATH);
					} else {
						GAME_STEP++;
					}
					break;

				case 5:
					// Enemy gets to attack user now
					performAttack_Other();
					break;

				}
				break;



			case GAME_STATE_ENEMYATTACK:

				// Cycle through the steps:
				switch (GAME_STEP) {


				case 0:
					// Attack Message & Animation
					ATTACK.init(getResources(), _creatureOther, _creatureUser);

					showMessage(_creatureOther.getName() + " uses " + ATTACK.getName() + ".");
					_creatureOther.performAnimation(ATTACK.getAnimationType());

					TIMER = System.currentTimeMillis() + 1500;
					GAME_STEP++;
					break;

				case 1:		
					// Effectiveness Message
					String effectiveMsg = ATTACK.getEffectiveMessage();

					if (!effectiveMsg.equals("")) {
						showMessage(effectiveMsg);
						TIMER = System.currentTimeMillis() + 1500;
					}
					GAME_STEP++;
					break;

				case 2:
					// Perform HIT animation
					if (ATTACK.getDamageDealt() > 0 ) {
						_creatureUser.performAnimation(Animation.HURT);
						TIMER = System.currentTimeMillis() + 500;
					}
					GAME_STEP++;
					break;

				case 3:
					// Lower HP
					if (ATTACK.getDamageDealt() > 0 ) {
						_creatureUser.Hurt(ATTACK.getDamageDealt());
						TIMER = System.currentTimeMillis() + 500;
					}
					GAME_STEP++;
					break;

				case 4:
					// TODO: Check for deadness
					GAME_STEP++;
					break;

				case 5:
					// Wait for input so player can attack
					setGameState(GAME_STATE_INPUT);
					break;

				}
				break;

			case GAME_STATE_PLAYERDEATH:

				switch(GAME_STEP) {

				}
				break;

			case GAME_STATE_ENEMYDEATH:
				Log.v(TAG, "EnemyDeath. State: " + GAME_STEP);
				switch(GAME_STEP) {
				case 0:
					showMessage("Wowzer! You actually managed to kill the Squirtle. That's incredible!");

					TIMER = System.currentTimeMillis() + 5000;
					GAME_STEP++;
					break;

				case 1:
					showMessage("I mean, it took me like a whole 10 seconds to write it's AI!");

					TIMER = System.currentTimeMillis() + 5000;
					GAME_STEP++;
					break;

				case 2:
					showMessage("Well, I guess I'll resurrect Squirtle's dead corpse for you. G'Luck!");

					TIMER = System.currentTimeMillis() + 5000;
					GAME_STEP++;
					break;

				case 3:
					performAttack_Other();
					break;

				}
				break;

			default: break;

			}
		}
	}

	public void setGameState(int state) {
		GAME_STEP = 0;
		GAME_STATE = state;

		if (GAME_STATE == GAME_STATE_INPUT)
			showButtons();
	}

	/**
	 * Local player uses an attack. Called by BattleActivity
	 */
	public void performAttack_Player(String attackName) {

		ATTACK = ResourceManager.getAttack(getResources(), attackName);
		setGameState(GAME_STATE_PLAYERATTACK);

	}

	/**
	 * Other player uses an attack. 
	 */
	public void performAttack_Other() {
		//TODO: Maybe not have it random? I dunno.

		// Get random attack
		int index = (int) Math.floor(Math.random() * _creatureOther.getAttackList().size());
		ATTACK = ResourceManager.getAttack(getResources(), _creatureOther.getAttackList().get(index));
		setGameState(GAME_STATE_ENEMYATTACK);

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