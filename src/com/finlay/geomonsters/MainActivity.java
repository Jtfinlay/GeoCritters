package com.finlay.geomonsters;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.SocketIO;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.battle.BattleActivity;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Main Activity that app begins at
 * @author James
 *
 */
public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	private static final String URL = "http://204.191.142.13:8000/";

	private Button theButton = null;
	private TextView theTextView = null;
	
	private SocketIO socket;
	private LocationManager locationManager = null;
	private MyLocationListener locationListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreated");
		super.onCreate(savedInstanceState);

		// set app to fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// layout
		setContentView(R.layout.activity_main);

		// Socket and Location Manager
		socket = new SocketIO();
		connectSocket();
		locationListener = new MyLocationListener(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// layout items
		theTextView = (TextView) findViewById(R.id.txtMessage);
		theButton = (Button) findViewById(R.id.btnGetLocation);
		theButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				theButton.setEnabled(false);
				theButton.setText("...");

				// Best provider
				Criteria criteria = new Criteria();
				String bestProvider = locationManager.getBestProvider(criteria, false);

				locationManager.requestLocationUpdates(bestProvider, 60, 5, locationListener);
			}
		});
		
		// TODO: Check storage files to see if any locations were buffered while app was closed.
		// If some locations exist, send the location to the server (when connected) and load the encounters
		// for the user.
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.v(TAG, "onStop");
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		socket.disconnect();
		super.onDestroy();
	}

	public void launchBattle(String s) {
		locationManager.removeUpdates(locationListener);
		try {
			Intent intent = new Intent(this, BattleActivity.class);	

			// TODO: Look up creature in area & percent change of hitting
			if (s.equals("forest"))
				intent.putExtra("ENEMYNAME", "Bulbasaur");
			else if (s.equals("water"))
				intent.putExtra("ENEMYNAME", "Squirtle");
			else if (s.equals("cemetery"))
				intent.putExtra("ENEMYNAME", "Haunter");
			else
				intent.putExtra("ENEMYNAME", "FireType");
			startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "launchBattle: " + e.getMessage());
		}
	}

	public void appendMessage(final String s) {
		// append string as new line to the TextView.
		// Since TextView is part of UI, we need to have another thread queue the action
		runOnUiThread(new Runnable() {
			public void run() {
				theTextView.append(s + "\n");
			}
		});
	}

	public void LocationChanged(Location loc) {
		// LocationManager has received GPS location
		
		String longitude ="" + loc.getLongitude();
		String latitude = "" + loc.getLatitude();
		
		appendMessage("Location changed: " + longitude + ", " + latitude);

		if (socket.isConnected()) {
			// if we're connected, send the server the location
			sendLocation(longitude, latitude);
		} else {
			// TODO: if not connected, save location to file. We can load this
			// location later and request encounter from the server.
		}
	}

	public void connectSocket() {
		// Connect to the socket
		if (!socket.isConnected()) {
			try {
				socket.connect(URL, new MyIOCallback(this));
			} catch (MalformedURLException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
	public void sendLocation(String longitude, String latitude) {
		// Creates JSON object containing given location and sends
		// to the connected server.
		
		if (!socket.isConnected()) {
			Log.w(TAG, "Socket is not connected. Could not send location.");
			return;
		}
			
		try {
			
			JSONObject json = new JSONObject();
			json.putOpt("longitude", longitude);
			json.putOpt("latitude", latitude);
			
			socket.emit("user position", json);
			
		} catch (JSONException e) {
			Log.e(TAG, "sendLocation: " + e.getMessage());
		}
	}
}

