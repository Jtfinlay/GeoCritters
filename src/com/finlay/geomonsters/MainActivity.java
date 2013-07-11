package com.finlay.geomonsters;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.battle.BattleActivity;

import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private Button theButton = null;
	private TextView theTextView = null;

	private LocationManager locationManager = null;
	private MyLocationListener locationListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreated");
		super.onCreate(savedInstanceState);

		// set app to fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// layout
		setContentView(R.layout.activity_main);

		// layout items
		theTextView = (TextView) findViewById(R.id.txtMessage);
		theButton = (Button) findViewById(R.id.btnGetLocation);
		theButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				theButton.setEnabled(false);
				theButton.setText("...");

				// Best provider
				Criteria criteria = new Criteria();
				String bestProvider = locationManager.getBestProvider(criteria, false);

				locationManager.requestLocationUpdates(bestProvider, 60, 5, locationListener);
			}
		});

		locationListener = new MyLocationListener(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.v(TAG, "onStop");
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		if (locationListener != null)
			locationListener.closeConnection();		// close SocketIO connection

		super.onDestroy();
	}

	public void launchBattle(String s) {
		locationManager.removeUpdates(locationListener);
		try {
			Intent intent = new Intent(this, BattleActivity.class);	

			// TODO: Look up creature in area & percent change of hitting
			if (s.equals("forest"))
				intent.putExtra("ENEMYNAME", "Bulbasaur");
			else if (s.equals("water"))
				intent.putExtra("ENEMYNAME", "Squirtle");
			else if (s.equals("cemetery"))
				intent.putExtra("ENEMYNAME", "Haunter");
			else
				intent.putExtra("ENEMYNAME", "FireType");
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void appendMessage(final String s) {
		runOnUiThread(new Runnable() {
			public void run() {
				theTextView.append(s + "\n");
			}
		});
	}
}

