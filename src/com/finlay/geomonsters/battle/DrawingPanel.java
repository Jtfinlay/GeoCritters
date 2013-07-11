package com.finlay.geomonsters.battle;

import com.finlay.geomonsters.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*----- The drawing surface -----*/
class DrawingPanel extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "DrawingPanel";

	private GameThread _Thread;										// Game Thread
	private Paint _paint;											// Paint object
	private Creature _creatureUser, _creatureOther;					// Both creatures
	private InfoBar _userInfo, _otherInfo;							// Info bars for creatures
	private RectF leftDraw, rightDraw;								// Creatures are drawn in left and right rects

	private DrawingPanelListener customListener;					// allows canvas to send messages to the Activity

	private static final int GAME_STATE_SETUP				= 0;		// Setup phase
	private static final int GAME_STATE_START				= 1;		// "You have encountered an enemy w/e"
	private static final int GAME_STATE_IDLE				= 2;		// Between attacks
	private static final int GAME_STATE_INPUT				= 3;		// Wait for Player input
	private static final int GAME_STATE_ATTACK				= 4;		// Creature uses attack
	private static final int GAME_STATE_PLAYERDEAD			= 5;		// Player is dead.
	private static final int GAME_STATE_ENEMYDEAD			= 6;		// Other is dead.
	private static final int GAME_STATE_CHANGECREATURE		= 7;		// Change Creature

	private int 		GAME_STATE 			= GAME_STATE_SETUP;		// Current state of game
	private StateInfo 	STATE_INFO;									// Information object for AttackInfo state 
	private int 		GAME_STEP 			= 0;					// Current step in state
	private long 		TIMER				= -1;					// Time for current step
	private boolean		GAME_STEP_ONTOUCH 	= false;				// Wait for touch before next GAME_STEP

	private int canvas_width, canvas_height;						// Canvas dimensions

	public DrawingPanel(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		getHolder().addCallback(this);


		_Thread = new GameThread(getHolder(), this);
		_paint = new Paint();	

		setFocusable(true);
	}
	public void init(String enemyName) {
		Log.v(TAG, "init");
		
		// create important objects
		_creatureUser = ResourceManager.getUserCreatures(getResources(), 0).get(0);
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

	/** SURFACE / DRAWING SETUP **/
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v(TAG, "surfaceChanged");
		
		Rect surface = holder.getSurfaceFrame();
		canvas_width = surface.width();
		canvas_height = surface.height();

		updateDrawingPreferences();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(TAG, "surfaceCreated");

		Rect surface = holder.getSurfaceFrame();
		canvas_width = surface.width();
		canvas_height = surface.height();

		updateDrawingPreferences();

		_Thread.setRunning(true);
		_Thread.start();
	}

	public void updateDrawingPreferences() {

		// LEFT & RIGHT DRAWING AREAS
		float xcentre = canvas_width / 4;
		float ycentre = canvas_height / 2;

		if (xcentre > ycentre) {
			leftDraw = new RectF(xcentre-ycentre, 0, xcentre+ycentre, canvas_height);
			rightDraw = new RectF(3*xcentre-ycentre, 0, 3*xcentre+ycentre, canvas_height);
		} else {
			leftDraw = new RectF(0, ycentre-xcentre, canvas_width/2, ycentre+xcentre);
			rightDraw = new RectF(canvas_width/2, ycentre-3*xcentre, canvas_width, ycentre+3*xcentre);
		}
		
		RectF drawDestination;
		
		/** LEFT SIDE **/
		// creature
		drawDestination = new RectF(.25f*leftDraw.width(), .28f*leftDraw.height(), .75f*leftDraw.width(), .8f*leftDraw.height());
		_creatureUser.setDrawRect(drawDestination);
		
		// info bar 
		drawDestination = new RectF(.05f*leftDraw.width(), .02f*leftDraw.height(), .85f*leftDraw.width(), .25f*leftDraw.height());
		_userInfo.setDrawRect(drawDestination);
		
		/** RIGHT SIDE **/
		//creature
		drawDestination = new RectF(.25f*rightDraw.width(), .28f*rightDraw.height(), .75f*rightDraw.width(), .8f*rightDraw.height());
		_creatureOther.setDrawRect(drawDestination);
		
		// info bar
		drawDestination = new RectF(.15f*rightDraw.width(), .02f*rightDraw.height(), .95f*rightDraw.width(), .25f*rightDraw.height());
		_otherInfo.setDrawRect(drawDestination);
		
		// TODO: This should probably not end up going here. Could lead to bugs when changing screen size
		// without closing Activity
		setGameState(GAME_STATE_START);
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

	/** TOUCH EVENTS **/
	
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

	/** RENDER & UPDATE **/
	
	public void render(Canvas canvas) {

		// ensure data loaded
		if (GAME_STATE == GAME_STATE_SETUP)
			return;

		
		Bitmap ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);

		// background
		_paint.setStyle(Paint.Style.FILL);
		_paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, canvas_width, canvas_height, _paint);
		

		/** LEFT SIDE **/
		canvas.save();

		// Move canvas to the topleft of the position
		canvas.translate(leftDraw.left, leftDraw.top);

		// draw ground
		RectF drawDestination = new RectF(.16f*leftDraw.width(), .55f*leftDraw.height(), .83f*leftDraw.width(), .9f*leftDraw.height());
		canvas.drawBitmap(ground, null, drawDestination, _paint);

		// draw creature
		canvas.save();
		canvas.scale(-1f,  1f, .5f*leftDraw.width(), 0);			// flip creature horizontally
		_creatureUser.render(canvas, _paint);
		canvas.restore();

		// info bar
		_userInfo.render(canvas, _paint);
		canvas.restore();


		/** RIGHT SIDE **/
		canvas.save();

		// Move canvas to the topleft of the position
		canvas.translate(rightDraw.left, rightDraw.top);

		// draw ground
		drawDestination = new RectF(.16f*rightDraw.width(), .55f*rightDraw.height(), .83f*rightDraw.width(), .9f*rightDraw.height());
		canvas.drawBitmap(ground, null, drawDestination, _paint);

		// draw creature
		_creatureOther.render(canvas, _paint);

		// info bar		
		_otherInfo.render(canvas, _paint);

		canvas.restore();

	}

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

	/** MANAGING GAME STATES **/
	
	public void nextGameStep() {

		Log.v(TAG, "nextGameStep. STATE: " + GAME_STATE + ", STEP: " + GAME_STEP);

		switch (GAME_STATE) {

		case GAME_STATE_START:
			switch (GAME_STEP) {
			case 0:
				_creatureOther.performAnimation(Animation.ENTER_FRAME);
				nextStepIn(400);
				break;
			case 1:
				showMessage("You have encountered an enemy " + _creatureOther.getName());
				nextStepOnTouch();
				break;
			case 2:
				showMessage("Go, " + _creatureUser.getNickName() + "! Kick his ass!");
				nextStepOnTouch();
				break;
			case 3:
				_creatureUser.performAnimation(Animation.ENTER_FRAME);
				nextStepIn(400);	
				break;
			case 4:
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
				showMessage(STATE_INFO.getAttacker().getNickName() + " uses " + STATE_INFO.getName());
				nextStepIn(200);
				break;
			case 1:
				// Attack animation
				STATE_INFO.getAttacker().performAnimation(Animation.STRIKE);
				nextStepOnTouch();	
				break;
			case 2:
				// Super effective?
				if (STATE_INFO.getEffectiveMessage().equals(""))
					nextStep();
				else {
					showMessage(STATE_INFO.getEffectiveMessage());
					nextStepIn(200);
				}
				break;
			case 3:
				// Hurt & Animation
				if (STATE_INFO.getDamageDealt() == 0)
					nextStep();
				else {
					STATE_INFO.getDefender().performAnimation(Animation.HURT);
					nextStepIn(200);
				}
				break;
			case 4:
				if (STATE_INFO.getDamageDealt() == 0)
					nextStep();
				else {
					STATE_INFO.getDefender().Hurt(STATE_INFO.getDamageDealt());
					nextStepOnTouch();
				}
				break;
			case 5:
				// Back to idle
				STATE_INFO.getAttacker().resetNextAttackCounter();
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
			
			
		case GAME_STATE_CHANGECREATURE:
			switch(GAME_STEP) {
			case 0:
				showMessage(STATE_INFO.getOutgoing().getNickName() + ", that's enough!");
				nextStepOnTouch();
				break;
			case 1:
				STATE_INFO.getOutgoing().performAnimation(Animation.EXIT_FRAME);
				nextStepIn(400);
				break;
			case 2:
				showMessage("Go, " + STATE_INFO.getIncoming().getNickName() + "! Kick his ass!");
				STATE_INFO.setIncomingAsOutgoing();
				STATE_INFO.getIncoming().resetNextAttackCounter();
				nextStepOnTouch();
				break;
			case 3:
				STATE_INFO.getOutgoing().performAnimation(Animation.ENTER_FRAME);
				nextStepIn(400);
				break;
			case 4:				
				setGameState(GAME_STATE_IDLE);
				break;
			}
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
			if (STATE_INFO.getDefender().getHP() == 0) {
				if (STATE_INFO.getDefender().equals(_creatureUser)) 
					GAME_STATE = GAME_STATE_PLAYERDEAD;
				else 
					GAME_STATE = GAME_STATE_ENEMYDEAD;					

				nextGameStep();
				return;
			} else if (STATE_INFO.getAttacker().getHP() == 0){
				if (STATE_INFO.getAttacker().equals(_creatureUser)) 
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
		STATE_INFO = ResourceManager.getAttack(getResources(), attackName, _creatureUser, _creatureOther);
		setGameState(GAME_STATE_ATTACK);

	}

	/**
	 * Other player uses an attack. 
	 */
	public void performAttack_Other() {

		// Get random attack
		int index = (int) Math.floor(Math.random() * _creatureOther.getAttackList().size());
		String attackName = _creatureOther.getAttackList().get(index);
		STATE_INFO = ResourceManager.getAttack(getResources(), attackName, _creatureOther, _creatureUser);
		setGameState(GAME_STATE_ATTACK);

	}
	public void ChangeUserCreature(String nickname) {
		STATE_INFO = new StateInfo();
		STATE_INFO.setAsCreatureSwitch(_creatureUser, ResourceManager.getUserCreatureByNickName(getResources(), nickname));
		setGameState(GAME_STATE_CHANGECREATURE);
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