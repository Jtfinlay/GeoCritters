package com.finlay.geomonsters;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

public class EncounterService extends Service {
	
	public static final String TAG = "EncounterService";
	
	public boolean isRunning = false;
	private LocationManager locationManager;
	private MyLocationListener locationListener;

	@Override
	public void onCreate() {
		// service being created
		locationListener = new MyLocationListener(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// service is starting, due to a call to startService()
		if (!isRunning) {
			
		}	
		
		isRunning = true;
		return Service.START_NOT_STICKY;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// A client is binding to the service with bindService()
		return null;
	}
	@Override
	public boolean stopService(Intent service) {
		
		isRunning = false;
		return false;
	}
	
	@Override
	public void onDestroy() {
		// service is no longer used and is being destroyed
	}
}
