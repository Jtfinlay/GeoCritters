package com.finlay.geomonsters;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Handles conversations with the server.
 * @author James
 *
 */
public class MyIOCallback implements IOCallback {
	
	private static final String TAG = "MyIOCallback";
	private MainActivity _mainActivity;
	
	
	public MyIOCallback(MainActivity activity) {
		_mainActivity = activity;
	}
	
	@Override
	public void on(String event, IOAcknowledge arg1, Object... arg2) {
		try {

			Log.v(TAG, "Server triggered event '" + event + "'");
			JSONObject obj = (JSONObject) arg2[0];

			if (event.equals("message"))
				Log.v(TAG, "Message from server: " + obj.getString("message"));
			if (event.equals("result")) {
				Log.v(TAG, "Return from server: " + obj.getString("value"));
				_mainActivity.appendMessage("Return from server: " + obj.getString("value"));
				_mainActivity.launchBattle(obj.getString("value"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnect() {
		Log.v(TAG, "Connection established");
		_mainActivity.appendMessage("Connection established.");
	}

	@Override
	public void onDisconnect() {
		Log.v(TAG, "Connected terminated");
		_mainActivity.appendMessage("Disconnected.");
	}

	@Override
	public void onError(SocketIOException arg0) {
		Log.v(TAG, "onError " + arg0.getMessage());

	}

	@Override
	public void onMessage(String arg0, IOAcknowledge arg1) {
		Log.v(TAG, "onMessage from server: " + arg0);
	}

	@Override
	public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
		Log.v(TAG, "Server JSON Message: " + arg0.toString());

	}

}