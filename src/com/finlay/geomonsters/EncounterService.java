package com.finlay.geomonsters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class EncounterService extends Service implements LocationListenerParent {
	
	public static final String TAG = "EncounterService";
	
	public boolean isRunning = false;
	private LocationManager locationManager;
	private MyLocationListener locationListener;

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		// service being created
		locationListener = new MyLocationListener(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onStartCommand");
		// service is starting, due to a call to startService()
		if (!isRunning) {
			
			// Best provider
			Criteria criteria = new Criteria();
			String bestProvider = locationManager.getBestProvider(criteria, false);
			
			// Request location updates -- set every two minutes
			locationManager.requestLocationUpdates(bestProvider, 120000, 0, locationListener);
		}	
		
		isRunning = true;
		return Service.START_NOT_STICKY;
	}
	@Override
	public boolean stopService(Intent service) {
		Log.v(TAG, "stopService");
		isRunning = false;
		return false;
	}
	
	@Override
	public void onDestroy() {
		// service is no longer used and is being destroyed
		Log.v(TAG, "onDestroy");
	}
	
	/**
	 * Location returned from MyLocationListener
	 * @param loc
	 */
	public void locationFound(Location loc) {
		ToastMessage("S: " + loc.getLatitude() + ", " + loc.getLongitude());
		
		// TODO: Save encounter location & time to file
		
		// TODO: Stop service if encounter queue is full
	}
	
	/**
	 * Shows toast message to user
	 * @param s
	 */
	private void ToastMessage(String s) {
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// A client is binding to the service with bindService()
		return null;
	}
}
