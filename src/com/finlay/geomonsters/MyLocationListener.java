package com.finlay.geomonsters;

import io.socket.SocketIO;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/*-----Listener class to get coordinates-----*/
public class MyLocationListener implements LocationListener {

	private static final String TAG = "MyLocationListener";

	private static final String URL = "http://204.191.142.13:8000/";
	private SocketIO _socket;
	private MainActivity _mainActivity;


	public MyLocationListener(MainActivity activity) {

		_mainActivity = activity;

		if (_socket == null)
			_socket = new SocketIO();
	}

	@Override
	public void onLocationChanged(Location loc) {

		if (!_socket.isConnected()) {
			try {
				_mainActivity.appendMessage("Connecting to server...");
				_socket.connect(URL, new MyIOCallback(_mainActivity));
			} catch (MalformedURLException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		Log.v(TAG, "Location Changed");
		Toast.makeText(_mainActivity.getBaseContext(), "Location changed: Lat: " + loc.getLatitude() + " Lng: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();

		String longitude ="" + loc.getLongitude();
		String latitude = "" + loc.getLatitude();

		_mainActivity.appendMessage("Query database...");

		// Send call to node server
		sendLocation(longitude, latitude);


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
			_socket.emit("user message", json);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendLocation(String longitude, String latitude) {
		try {

			JSONObject json = new JSONObject();
			json.putOpt("longitude", longitude);
			json.putOpt("latitude", latitude);
			_socket.emit("user position", json);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		if (_socket != null)
			_socket.disconnect();
	}
}
