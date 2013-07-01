package com.finlay.geomonsters;

import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	
	private Button theButton = null;
	
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// lock screen for portrait
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
	
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Log.v(TAG, "onCreated");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class ButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {

			Log.v(TAG, "The button has been clicked");
			
			theButton.setText("Retrieving position...");
			locationListener = new MyLocationListener();
			
			// Best provider
			Criteria criteria = new Criteria();
			String bestProvider = locationManager.getBestProvider(criteria, false);
			
			locationManager.requestLocationUpdates(bestProvider, 60, 5, locationListener);
		}
		
	}

}


