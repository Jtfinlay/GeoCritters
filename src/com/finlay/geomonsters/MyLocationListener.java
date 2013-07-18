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

	private LocationListenerParent _parent;


	public MyLocationListener(LocationListenerParent parent) {
		_parent = parent;
	}

	@Override
	public void onLocationChanged(Location loc) {
		Log.v(TAG, "Location Changed");
		_parent.locationFound(loc);
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
