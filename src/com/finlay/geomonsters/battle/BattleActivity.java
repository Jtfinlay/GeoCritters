package com.finlay.geomonsters.battle;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.creatures.Creature;
import com.finlay.geomonsters.creatures.Animation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BattleActivity extends Activity {

	private static final String TAG = "BattleActivity";

	private Button btn1, btn2, btn3, btn4;
	private DrawingPanel drawingPanel;
	private LinearLayout btnPanel;
	private TextView msgPanel;

	Callable<Integer> onBackPress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		// set app to fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//setContentView(new DrawingPanel(this));
		setContentView(R.layout.battle);
		
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		btn3 = (Button) findViewById(R.id.button3);
		btn4 = (Button) findViewById(R.id.button4);
		
		btnPanel = (LinearLayout) findViewById(R.id.Buttons);
		msgPanel = (TextView) findViewById(R.id.MessageView);
		drawingPanel = (DrawingPanel) findViewById(R.id.BattleView);
		drawingPanel.giveLayout(btnPanel, msgPanel);
		
		BtnSetup_Default();

		btn1.setOnClickListener(new MyClickListener());
		btn4.setOnClickListener(new MyClickListener());

	}

	private void BtnSetup_Default() {
		btn1.setText("Fight");
		btn2.setText("Inventory");
		btn3.setText("GeoMonsters");
		btn4.setText("Flee");
		onBackPress = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				finish();
				return null;
			}
		};

	}
	private void BtnSetup_Fight() {
		
		Creature user_creature = drawingPanel.getCreature_User();
		ArrayList<String> attacks = user_creature.getAttackList();
		
		switch(attacks.size()) {
		case 4:
			btn4.setText(attacks.get(3));
		case 3:
			btn3.setText(attacks.get(2));
		case 2: 
			btn2.setText(attacks.get(1));
		case 1:
			btn1.setText(attacks.get(0));
		case 0:
			Log.e(TAG, "No Attacks found for creature " + "Kangoo");
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

		Log.v(TAG, "Back pressed");
		try {
			onBackPress.call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Button thisButton = (Button) arg0;

			if (thisButton.getText().equals("Fight")) {
				BtnSetup_Fight();
			} else if (thisButton.getText().equals("Inventory")) {
				// TODO: Inventory
			} else if (thisButton.getText().equals("GeoMonsters")) {
				
			} else if (thisButton.getText().equals("Flee")) {
				finish();
			} else {
				// must be an attack
				drawingPanel.performUserAttack(thisButton.getText().toString());
				BtnSetup_Default();
			}
		}

	}
}
