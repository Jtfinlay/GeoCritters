package com.finlay.geomonsters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class WeatherManager extends AsyncTask{
	
	// www.survivingwithandroid.com/2013/05/build-weather-app-json-http-android.html
	
	private static final String TAG = "WeatherManager";
	
	private static final String URL = "http://api.openweathermap.org/data/2.5/weather?";
	private MainActivity _parent;
	private HttpClient httpclient;
	public WeatherManager(MainActivity parent) {
		_parent = parent;
		httpclient = new DefaultHttpClient();
	}

	// Cannot be called from main thread.
	private String getWeatherData(Location loc) {
		
		// Prepare request object
		String address = URL + "lat=" + loc.getLatitude() + "&lon=" + loc.getLongitude();		
		
		HttpURLConnection con = null;
		InputStream is = null;
		
		try {
			con = (HttpURLConnection) ( new java.net.URL(address)).openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			
			Log.v(TAG, "Open Weather connected.");
			_parent.appendMessage("Open Weather connected.");
			
			// Let's read the response
			StringBuffer buffer = new StringBuffer();
			is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ( (line = br.readLine()) != null )
				buffer.append(line + "\r\n");
			
			is.close();
			con.disconnect();
			return buffer.toString();
			
		} catch (Throwable t) {
			Log.e(TAG, t.getMessage());
		} finally {
			try { is.close(); } catch (Throwable t) {}
			try { con.disconnect(); } catch (Throwable t) {}
		}
		
		return null;
	}

	@Override
	protected Object doInBackground(Object... params) {
		// getWeatherData cannot be called from main thread, so we have this function running 
		// on a separate thread.
		
		Location loc = (Location) params[0];
		
		// query weather data from openweather page
		String data = getWeatherData(loc);
		Log.v(TAG, "DATA: " + data);
		
		try {
		
			// Create JSON object from the returned data
			JSONObject jObj = new JSONObject(data);
			
			// Extract the info
			JSONObject coordObj = jObj.getJSONObject("coord");
			
			JSONObject sysObj = jObj.getJSONObject("sys");
			Log.v(TAG, "Country: " + sysObj.getString("country"));
			Log.v(TAG, "Sunrise: " + sysObj.getString("sunrise"));
			Log.v(TAG, "Sunset: " + sysObj.getString("sunset"));
			Log.v(TAG, "City: " + sysObj.getString("name"));
			
			// Weather info
			JSONArray jArr = jObj.getJSONArray("weather");
			
			
			
			
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}

		
		return null;
	}

}
