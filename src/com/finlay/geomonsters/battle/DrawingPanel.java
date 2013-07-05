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
	private InfoBar _userInfo, _otherInfo;				// Info bars for creatures

	private DrawingPanelListener customListener;

	private static final int GAME_STATE_SETUP			= 0;	// Setup phase
	private static final int GAME_STATE_IDLE			= 1;	// Between attacks
	private static final int GAME_STATE_INPUT			= 2;	// Wait for Player input
	private static final int GAME_STATE_ATTACK			= 3;	// Creature uses attack
	private static final int GAME_STATE_PLAYERDEAD		= 4;	// Player is dead.
	private static final int GAME_STATE_ENEMYDEAD		= 5;	// Other is dead.

	private int 	GAME_STATE 			= GAME_STATE_SETUP;		// Current state of game
	private Attack 	ATTACK;										// Info about any Actions 
	private int 	GAME_STEP 			= 0;					// Current step in state
	private long 	TIMER				= 0;					// Time for current step
	private boolean	GAME_STEP_ONTOUCH 	= false;				// Wait for touch before next GAME_STEP

	private int canvas_width, canvas_height;			// Canvas dimensions

	public DrawingPanel(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		getHolder().addCallback(this);


		_Thread = new GameThread(getHolder(), this);
		_paint = new Paint();	

		setFocusable(true);
	}
	public void init(String enemyName) {
		_creatureUser = ResourceManager.newCreature(getResources(), "Kangoo");
		_creatureOther = ResourceManager.newCreature(getResources(), enemyName);
		
		_userInfo = new InfoBar(_creatureUser);
		_otherInfo = new InfoBar(_creatureOther);
		_otherInfo.alignRight();
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
		return false;
	}
	
	public boolean sendTouchEvent(MotionEvent e) {
		Log.v(TAG, "onTouch");
		
		if (GAME_STEP_ONTOUCH) {
			GAME_STEP_ONTOUCH = false;
			nextGameStep();
		}

		return true;
	}

	/**
	 * Draw
	 * @param canvas
	 */
	public void render(Canvas canvas) {
		
		// ensure creatures are set
		if (_creatureUser == null || _creatureOther == null)
			return;

		float dx, dy;

		// background
		_paint.setStyle(Paint.Style.FILL);
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

		// left creature info bar
		canvas.save();
		canvas.translate(.04f*canvas_width, .05f*canvas_height);
		_userInfo.render(canvas, _paint);
		canvas.restore();

		// right creature
		canvas.save();
		dx = (canvas_width - _creatureOther.getWidth() - 20);
		dy = (canvas_height - _creatureOther.getHeight())/2+20;
		canvas.translate(dx, dy);									// translate position
		_creatureOther.render(canvas, _paint);						// position is top-left of image
		canvas.restore();

		// right creature info bar
		canvas.save();
		//TODO: x-coord should be set from InfoBar.getWidth();
		canvas.translate(.64f*canvas_width, .05f*canvas_height);
		_otherInfo.render(canvas, _paint);
		canvas.restore();




	}

	/**
	 * Cycle through actions to perform
	 */
	public void update() {

		// Check Step timer
		if (TIMER >= 0 && System.currentTimeMillis() > TIMER) {
			TIMER = -1;
			nextGameStep();
		}

		// If idle, send updates to creatures
		if (GAME_STATE == GAME_STATE_IDLE)  {

			if (_creatureUser.getNextAttackPercent() >= 1) {
				// player attack -- get input
				setGameState(GAME_STATE_INPUT);
			} else if (_creatureOther.getNextAttackPercent() >= 1) {
				// enemy attack 
				performAttack_Other();
			} else {
				// no attack ready yet
				_creatureUser.idleUpdate();
				_creatureOther.idleUpdate();
			}
		}

	}

	public void nextGameStep() {

		Log.v(TAG, "nextGameStep. STATE: " + GAME_STATE + ", STEP: " + GAME_STEP);

		switch (GAME_STATE) {

		case GAME_STATE_SETUP:
			switch (GAME_STEP) {
			case 0:
				showMessage("You have encountered an enemy " + _creatureOther.getName());
				nextStepOnTouch();
				break;
			case 1:
				_creatureUser.ResumeAttackCounter();
				_creatureOther.ResumeAttackCounter();
				setGameState(GAME_STATE_IDLE);
				break;
			}

			break;

		case GAME_STATE_IDLE:
			showMessage("");
			break;

		case GAME_STATE_INPUT:
			// don't really need to do anything here..
			break;

		case GAME_STATE_ATTACK:
			
			switch(GAME_STEP) {
			case 0:
				// State attack
				showMessage(ATTACK.getAttacker().getName() + " uses " + ATTACK.getName());
				nextStepIn(200);
				break;
			case 1:
				// Attack animation
				ATTACK.getAttacker().performAnimation(Animation.STRIKE);
				nextStepOnTouch();	
				break;
			case 2:
				// Super effective?
				if (ATTACK.getEffectiveMessage().equals(""))
					nextStep();
				else {
					showMessage(ATTACK.getEffectiveMessage());
					nextStepIn(200);
				}
				break;
			case 3:
				// Hurt & Animation
				if (ATTACK.getDamageDealt() == 0)
					nextStep();
				else {
					ATTACK.getDefender().performAnimation(Animation.HURT);
					nextStepIn(200);
				}
				break;
			case 4:
				if (ATTACK.getDamageDealt() == 0)
					nextStep();
				else {
					ATTACK.getDefender().Hurt(ATTACK.getDamageDealt());
					nextStepOnTouch();
				}
				break;
			case 5:
				// Back to idle
				ATTACK.getAttacker().resetNextAttackCounter();
				setGameState(GAME_STATE_IDLE);
				break;
					
			}

			break;
			
		case GAME_STATE_PLAYERDEAD:
			showMessage("Aww shucks. You died. GTFO.");
			_creatureUser.performAnimation(Animation.KILL);
			break;
			
		case GAME_STATE_ENEMYDEAD:
			showMessage("Yay! You killed him. Now, GTFO.");
			_creatureOther.performAnimation(Animation.KILL);
			break;
		}

	}

	private void nextStep() {
		GAME_STEP++;
		TIMER = 0;
	}
	private void nextStepIn(long time) {
		GAME_STEP++;
		TIMER = System.currentTimeMillis() + time;
	}
	private void nextStepOnTouch() {
		GAME_STEP++;
		GAME_STEP_ONTOUCH = true;
	}
	public void setGameState(int state) {		
		GAME_STEP = 0;

		if (GAME_STATE == GAME_STATE_ATTACK) {
			// check for death -- TODO: Make this less shitty
			if (ATTACK.getDefender().getHP() == 0) {
				if (ATTACK.getDefender().equals(_creatureUser)) 
					GAME_STATE = GAME_STATE_PLAYERDEAD;
				 else 
					GAME_STATE = GAME_STATE_ENEMYDEAD;					
				
				nextGameStep();
				return;
			} else if (ATTACK.getAttacker().getHP() == 0){
				if (ATTACK.getAttacker().equals(_creatureUser)) 
					GAME_STATE = GAME_STATE_PLAYERDEAD;
				 else 
					GAME_STATE = GAME_STATE_ENEMYDEAD;					
				
				nextGameStep();
				return;
			}
		}
		
		if (state == GAME_STATE_INPUT)
			showButtons();
		else if (state == GAME_STATE_IDLE) {
			_creatureUser.ResumeAttackCounter();
			_creatureOther.ResumeAttackCounter();
		}
		
		GAME_STATE = state;
		nextGameStep();
	}

	/**
	 * Local player uses an attack. Called by BattleActivity
	 */
	public void performAttack_Player(String attackName) {
		ATTACK = ResourceManager.getAttack(getResources(), attackName, _creatureUser, _creatureOther);
		setGameState(GAME_STATE_ATTACK);

	}

	/**
	 * Other player uses an attack. 
	 */
	public void performAttack_Other() {
		//TODO: Maybe not have it random? I dunno.

		// Get random attack
		int index = (int) Math.floor(Math.random() * _creatureOther.getAttackList().size());
		String attackName = _creatureOther.getAttackList().get(index);
		ATTACK = ResourceManager.getAttack(getResources(), attackName, _creatureOther, _creatureUser);
		setGameState(GAME_STATE_ATTACK);

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