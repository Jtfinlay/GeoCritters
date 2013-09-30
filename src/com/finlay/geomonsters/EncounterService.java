/*
 * GeoCritters. Real-world creature encounter game.
 * Copyright (C) 2013 James Finlay
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class EncounterService extends Service implements LocationListenerParent {

	public static final String TAG = "EncounterService";

	public boolean isRunning = false;
	private LocationManager locationManager;
	private MyLocationListener locationListener;
	private Timer timer;
	private long TIME_DELAY = 12 * 1000; // easier to see with multiplication

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

		// have timer that gets position at every set interval
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
	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			//TODO: Make probability of having new encounter
			
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

		// Save encounter location & time to file
		boolean hasRoom = ConfigManager.PushEncounter(getApplicationContext(), loc, System.currentTimeMillis());
		
		// Stop service if encounter queue is full
		if (!hasRoom)
			stopSelf();
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
