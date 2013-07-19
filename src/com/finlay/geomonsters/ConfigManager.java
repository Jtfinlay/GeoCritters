package com.finlay.geomonsters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class ConfigManager {

	private static final String TAG = "ConfigManager";

	private static final int MAX_ENCOUNTERS = 5;
	private static final String FILE_ENCOUNTERQUEUE = "encounters.dat";

	/**
	 * Adds encounter info to queue if not passed limit. For use when player is offline || app closed.
	 * @param context
	 * @param loc
	 * @param time
	 */
	public static boolean PushEncounter(Context context, Location loc, double time) {

		try {

			int counter = 0;
			if (context.getFileStreamPath(FILE_ENCOUNTERQUEUE).exists()) {

				// Read the file and count the number of encounters
				FileInputStream fIn = context.openFileInput(FILE_ENCOUNTERQUEUE);
				InputStreamReader isr = new InputStreamReader(fIn);
				BufferedReader buffreader = new BufferedReader(isr);

				String readString = buffreader.readLine();
				while (readString != null) {
					counter++;
					Log.v(TAG, "Encounter line: " + readString);
					readString = buffreader.readLine();
				}

				isr.close();	
			}

			Log.v(TAG, "There are " + counter + " encounters queued.");

			// If total encounters does not exceed the max encounters allowed, append new encounter.
			if (counter >= MAX_ENCOUNTERS)
				return false;

			// Write the new encounter to the file
			FileOutputStream fos = context.openFileOutput(FILE_ENCOUNTERQUEUE, Context.MODE_APPEND);
			OutputStreamWriter osw = new OutputStreamWriter(fos);

			osw.write(loc.getLatitude() + "," + loc.getLongitude() + "," + time + "\n");
			Log.v(TAG, "Written: " + loc.getLatitude() + "," + loc.getLongitude() + "," + time + "\n");
			osw.flush();
			osw.close();

			return true;
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return false;		
	}

	public static String PullEncounter(Context context) {

		if (!context.getFileStreamPath(FILE_ENCOUNTERQUEUE).exists())
			return "";

		try {
			// Read the file and count the number of encounters
			FileInputStream fIn = context.openFileInput(FILE_ENCOUNTERQUEUE);
			InputStreamReader isr = new InputStreamReader(fIn);
			BufferedReader buffreader = new BufferedReader(isr);

			String readString = buffreader.readLine();

			isr.close();	
			return (readString == null) ? "" : readString;

		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return "";
	}

	public static String PopEncounter(Context context) {

		if (!context.getFileStreamPath(FILE_ENCOUNTERQUEUE).exists())
			return "";


		// Pull all encounters into an array
		ArrayList<String> encounters = new ArrayList<String>();
		try {
			FileInputStream fIn = context.openFileInput(FILE_ENCOUNTERQUEUE);
			InputStreamReader isr = new InputStreamReader(fIn);
			BufferedReader buffreader = new BufferedReader(isr);

			String readString = buffreader.readLine();
			while (readString != null) {
				encounters.add(readString);
				readString = buffreader.readLine();
			}

			isr.close();


			// Rewrite the encounters, but ignore the first one
			if (encounters.size() > 0) {
				FileOutputStream fos = context.openFileOutput(FILE_ENCOUNTERQUEUE, Context.MODE_PRIVATE);
				OutputStreamWriter osw = new OutputStreamWriter(fos);

				for (int i=1; i<encounters.size(); i++) {
					osw.write(encounters.get(i));
				}
				osw.flush();
				osw.close();
			}

		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		
		return (encounters.size()>0) ? encounters.get(0) : "";


	}

	public static void ResetConfigFiles(Context context) {
		Log.v(TAG, "Reset config files.");
		context.deleteFile(FILE_ENCOUNTERQUEUE);
	}
}
