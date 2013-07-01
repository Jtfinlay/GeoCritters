package com.finlay.geomonsters.battle;

import java.util.concurrent.Callable;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.R.id;
import com.finlay.geomonsters.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;

public class BattleActivity extends Activity {

	private static final String TAG = "BattleActivity";

	private Button btn1, btn2, btn3, btn4;

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

		BtnSetup_Default();

		btn1.setOnClickListener(new MyClickListener());

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
		//TODO: Set up depending on creature's attacks
		btn1.setText("Punch");
		btn2.setText("Rebel");
		btn3.setText("Bitch");
		btn4.setText("Complain");

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
			}
		}

	}
}
