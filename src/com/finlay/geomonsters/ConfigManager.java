package com.finlay.geomonsters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class ConfigManager {

	private static final String TAG = "ConfigManager";
	
	private static final int MAX_ENCOUNTERS = 5;
	private static final String FILE_ENCOUNTERQUEUE = "encounters.xml";
	
	/**
	 * Adds encounter info to queue if not passed limit. For use when player is offline || app closed.
	 * @param context
	 * @param loc
	 * @param time
	 */
	public static void QueueEncounter(Context context, Location loc, double time) {
		
		try {

			// Read the file and count the number of encounters
			FileInputStream fIn = context.openFileInput(FILE_ENCOUNTERQUEUE);
			InputStreamReader isr = new InputStreamReader(fIn);
			BufferedReader buffreader = new BufferedReader(isr);
			
			String readString = buffreader.readLine();
			int counter = 0;
			while (readString != null) {
				counter++;
				readString = buffreader.readLine();
			}
			
			isr.close();		

			Log.v(TAG, "There are " + counter + " encounters queued.");
			
			// If total encounters does not exceed the max encounters allowed, append new encounter.
			if (counter >= MAX_ENCOUNTERS)
				return;
			
			// Write the new encounter to the file
			FileOutputStream fos = context.openFileOutput(FILE_ENCOUNTERQUEUE, Context.MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			
			osw.write(loc.getLatitude() + "," + loc.getLongitude() + "," + time);
			
			osw.flush();
			osw.close();
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

}
