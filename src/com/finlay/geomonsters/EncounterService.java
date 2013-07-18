package com.finlay.geomonsters;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class EncounterService extends Service implements LocationListenerParent {

	public static final String TAG = "EncounterService";

	public boolean isRunning = false;
	private LocationManager locationManager;
	private MyLocationListener locationListener;
	private Timer timer;
	private long TIME_DELAY = 120 * 1000; // easier to see with multiplication

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		// service being created
		locationListener = new MyLocationListener(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		timer = new Timer();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onStartCommand");

		// service is starting, due to a call to startService()
		if (!isRunning) {
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					handler.sendEmptyMessage(0);
				};
			}, TIME_DELAY, TIME_DELAY);
			
		}	

		isRunning = true;
		return Service.START_NOT_STICKY;
	}
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Criteria criteria = new Criteria();
			String bestProvider = locationManager.getBestProvider(criteria, false);
			locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);
		}
	};
	
	
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
		locationManager.removeUpdates(locationListener);		


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
