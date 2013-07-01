package com.finlay.geomonsters;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.*;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

	private static final String TAG = "Location Listener";
	private static final String URL = "http://192.168.1.64:8000/";
	
	private SocketIO socket;
	
	
	@Override
	public void onLocationChanged(Location loc) {
		
		try {
			
		socket = new SocketIO();
		socket.connect(URL, new MyIOCallback());

		Log.v(TAG, "Location Changed");
	
		String longitude = "" + loc.getLongitude();
		String latitude = "" + loc.getLatitude();
		
		sendLocation(longitude, latitude);
		
		} catch (MalformedURLException e) {
			Log.v(TAG, "MalformedURLException");
			e.printStackTrace();
		}
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
	
	public void sendMessage(String message) {
		
		try {
			
			JSONObject json = new JSONObject();
			json.putOpt("message", message);
			
			socket.emit("user message", json);
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void sendLocation(String longitude, String latitude) {
		
		try {
			
			JSONObject json = new JSONObject();
			json.putOpt("longitude", longitude);
			json.putOpt("latitude", latitude);
			
			socket.emit("user position", json);
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	

}
