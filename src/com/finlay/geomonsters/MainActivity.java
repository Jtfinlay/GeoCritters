package com.finlay.geomonsters;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.SocketIO;

import com.finlay.geomonsters.R;
import com.finlay.geomonsters.battle.BattleActivity;
import com.finlay.geomonsters.battle.ResourceManager;

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

	private Button forceButton = null;
	private Button waitButton = null;
	private TextView theTextView = null;
	
	private SocketIO socket;
	private LocationManager locationManager = null;
	private MyLocationListener locationListener = null;
	private WeatherManager weatherManager = null;
	private Weather weatherData = null;
	
	private MainActivity _activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreated");
		super.onCreate(savedInstanceState);
		_activity = this;

		// set app to fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// layout
		setContentView(R.layout.activity_main);
		
		// Socket, Location Manager, Weather Manager
		socket = new SocketIO();
		connectSocket();
		locationListener = new MyLocationListener(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		weatherManager = new WeatherManager(this);
		
		// layout items
		theTextView = (TextView) findViewById(R.id.txtMessage);
		forceButton = (Button) findViewById(R.id.btnGetLocation);
		waitButton = (Button) findViewById(R.id.btnWaitLocation);

		// Disable buttons until socket is connected
		forceButton.setEnabled(false);
		waitButton.setEnabled(false);
		
		forceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.v(TAG, "Force Click");
				forceButton.setText("...");

				// Best provider
				Criteria criteria = new Criteria();
				String bestProvider = locationManager.getBestProvider(criteria, false);

				locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);
				
				forceButton.setEnabled(false);
				waitButton.setEnabled(false);
			}
		});
		
		waitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.v(TAG, "Wait click");
				waitButton.setText("...");

				// Best provider
				//Criteria criteria = new Criteria();
				//String bestProvider = locationManager.getBestProvider(criteria, false);

				//locationManager.requestLocationUpdates(bestProvider, 10000, 0, locationListener);
				
				// Start Encounter Service
				Intent service = new Intent(_activity, EncounterService.class);
				_activity.startService(service);
				
				forceButton.setEnabled(false);
				waitButton.setEnabled(false);
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
		// Stop location updates
		locationManager.removeUpdates(locationListener);
		
		// Wait for weather data..
		Log.v(TAG, "Wait for weatherData..");
		while (weatherData == null) ;
		Log.v(TAG, "Weather ID: " + weatherData.weatherID);
		
		try {
			Intent intent = new Intent(this, BattleActivity.class);
			// TODO: Send more than just enemy name through. Ex: Lvl, Attributes, etc.
			intent.putExtra("ENEMYNAME", ResourceManager.getCreatureEncounter(getResources(), s, weatherData));
			
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
			// if we're connected, query weather data & send the server the location
			weatherManager.execute(loc, true);
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
	public void socketConnected() {
		forceButton.setEnabled(true);
		waitButton.setEnabled(true);
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
	public void setWeatherData(Weather weather) {
		Log.v(TAG, "Weather data received.");
		weatherData = weather;
		appendMessage("Weather data received.");
	}
}

