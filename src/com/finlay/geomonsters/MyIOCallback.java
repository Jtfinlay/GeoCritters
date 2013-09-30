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
			Log.e(TAG, e.getMessage());
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
		arg0.printStackTrace();
		
		if (arg0.getMessage().equals("Error while handshaking"))
			_mainActivity.appendMessage("Could not connect to server.");
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