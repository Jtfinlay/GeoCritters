package com.finlay.geomonsters;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Uses GPS to get client location
 * @author James
 *
 */
public class MyLocationListener implements LocationListener {

	private static final String TAG = "MyLocationListener";

	private MainActivity _mainActivity;


	public MyLocationListener(MainActivity activity) {

		_mainActivity = activity;

	}

	@Override
	public void onLocationChanged(Location loc) {
		Log.v(TAG, "Location Changed");
		
		Toast.makeText(_mainActivity.getBaseContext(), "Location changed: Lat: " + loc.getLatitude() + " Lng: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();

		_mainActivity.LocationChanged(loc);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}
