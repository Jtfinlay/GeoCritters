package com.finlay.geomonsters.battle;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import com.finlay.geomonsters.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BattleActivity extends Activity {

	private static final String TAG = "BattleActivity";

	private Button btn1, btn2, btn3, btn4;
	private DrawingPanel drawingPanel;
	private RelativeLayout	bottomPanel;
	private LinearLayout btnPanel;
	private TextWriter msgPanel;

	private long clickLast = 0;
	private long clickDelay = 150;

	Callable<Integer> onBackPress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		// set app to fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// get the Battle layout
		setContentView(R.layout.battle);
		
		// Get information sent by prior activity
		Intent intent = getIntent();
		String enemyName = intent.getStringExtra("ENEMYNAME");
		
		// Layout items
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		btn4 = (Button) findViewById(R.id.button4);

		btnPanel = (LinearLayout) findViewById(R.id.Buttons);				// Contains buttons on bottom panel
		msgPanel = (TextWriter) findViewById(R.id.MessageView);				// Scrolls text to bottom panel
		drawingPanel = (DrawingPanel) findViewById(R.id.BattleView);		// Main drawing canvas
		bottomPanel = (RelativeLayout) findViewById(R.id.Bottom_Panel);		// Bottom panel containing buttons and text

		msgPanel.setCharacterDelay(50);										// set Text scroll speed
		drawingPanel.setCustomListener(new MyDrawingPanelListener());		// allows Canvas to return events to this object
		drawingPanel.init(enemyName);										// send initialization info to canvas
		drawingPanel.setOnTouchListener(new MyViewTouchListener());			// listen for touches on canvas
		bottomPanel.setOnTouchListener(new MyViewTouchListener());			// listen for touches on bottom panel

		BtnSetup_Default();													// set buttons

		btn1.setOnClickListener(new MyButtonClickListener());
		btn2.setOnClickListener(new MyButtonClickListener());
		btn3.setOnClickListener(new MyButtonClickListener());
		btn4.setOnClickListener(new MyButtonClickListener());

	}

	private void BtnSetup_Default() {
		// Set text for each button
		btn1.setText("Fight");
		btn2.setText("Inventory");
		btn3.setText("GeoMonsters");
		btn4.setText("Flee");

		// button themes
		btn1.setBackgroundColor(Color.WHITE);
		btn2.setBackgroundColor(Color.WHITE);
		btn3.setBackgroundColor(Color.WHITE);
		btn4.setBackgroundColor(Color.WHITE);

		// Event when back button pressed.
		// TODO: Confirm message before exiting Activity
		onBackPress = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				finish();
				return null;
			}
		};

	}
	private void BtnSetup_Fight() {

		// Change button values to the attacker's attacks.
		Creature user_creature = drawingPanel.getCreature_User();
		ArrayList<String> attacks = user_creature.getAttackList();

		// Set button values to the attacks
		btn1.setVisibility(View.INVISIBLE);
		btn2.setVisibility(View.INVISIBLE);
		btn3.setVisibility(View.INVISIBLE);
		btn4.setVisibility(View.INVISIBLE);


		if (attacks.size() == 0) 
			Log.e(TAG, "No Attacks found for creature " + user_creature.getName());

		if (attacks.size() >= 1) {
			btn1.setText(attacks.get(0));
			btn1.setVisibility(View.VISIBLE);
			btn1.setBackgroundColor(ResourceManager.getColorOfAttack(getResources(), attacks.get(0)));
		}
		if (attacks.size() >= 2) {
			btn2.setText(attacks.get(1));
			btn2.setVisibility(View.VISIBLE);
			btn2.setBackgroundColor(ResourceManager.getColorOfAttack(getResources(), attacks.get(1)));
		}
		if (attacks.size() >= 3) {
			btn3.setText(attacks.get(2));
			btn3.setVisibility(View.VISIBLE);
			btn3.setBackgroundColor(ResourceManager.getColorOfAttack(getResources(), attacks.get(2)));
		}
		if (attacks.size() >= 4) {
			btn4.setText(attacks.get(3));
			btn4.setVisibility(View.VISIBLE);
			btn4.setBackgroundColor(ResourceManager.getColorOfAttack(getResources(), attacks.get(3)));
		}

		onBackPress = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				BtnSetup_Default();
				return null;
			}
		};

	}

	@Override
	public void onBackPressed() {
		// when back button is pressed
		Log.v(TAG, "Back pressed");

		try {
			onBackPress.call();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

	}

	class MyButtonClickListener implements OnClickListener {
		// events for bottomPanel buttons
		@Override
		public void onClick(View arg0) {
			Button thisButton = (Button) arg0;
			Log.v(TAG, "onClick: " + thisButton.getText());
			if (thisButton.getText().equals("Fight")) {
				BtnSetup_Fight();
			} else if (thisButton.getText().equals("Inventory")) {
				// TODO: Inventory
			} else if (thisButton.getText().equals("GeoMonsters")) {
				// TODO: GeoMonsters
			} else if (thisButton.getText().equals("Flee")) {
				finish();
			} else {
				// must be an attack
				drawingPanel.performAttack_Player(thisButton.getText().toString());
			}
		}

	}
	class MyViewTouchListener implements OnTouchListener {
		// For when user touches bottomPanel or canvas
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (System.currentTimeMillis() - clickLast < clickDelay)
				return false;
			clickLast = System.currentTimeMillis();
			
			if (msgPanel.isTyping()) {
				msgPanel.forceEnd();
				return false;
			}

			return drawingPanel.sendTouchEvent(event);
		}


	}
	class MyDrawingPanelListener implements DrawingPanelListener {
		// listens for messages from the canvas
		
		@Override
		public void showButtonView() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					BtnSetup_Default();
					msgPanel.setVisibility(View.INVISIBLE);
					btnPanel.setVisibility(View.VISIBLE);
				}
			});
		}
		@Override
		public void showMessage(final String s) {
			runOnUiThread(new Runnable() {
				public void run() {
					msgPanel.setVisibility(View.VISIBLE);
					btnPanel.setVisibility(View.INVISIBLE);
					//msgPanel.setText(s);
					msgPanel.animateText(s);
				}
			});
		}
	}
}
