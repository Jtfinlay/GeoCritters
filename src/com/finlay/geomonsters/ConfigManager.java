package com.finlay.geomonsters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.finlay.geomonsters.ranch.Grid;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class ConfigManager {

	private static final String TAG = "ConfigManager";

	private static final int MAX_ENCOUNTERS = 5;
	private static final String FILE_ENCOUNTERQUEUE = "encounters.dat";
	private static final String FILE_USERGRID = "ranchgrid.dat";

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

	public static int EncounterCount(Context context) {
		
		if (!context.getFileStreamPath(FILE_ENCOUNTERQUEUE).exists())
			return 0;

		try {
			// Read the file and count the number of encounters
			FileInputStream fIn = context.openFileInput(FILE_ENCOUNTERQUEUE);
			InputStreamReader isr = new InputStreamReader(fIn);
			BufferedReader buffreader = new BufferedReader(isr);

			int counter = 0;
			String readString = buffreader.readLine();
			while (readString != null) {
				counter++;
				readString = buffreader.readLine();
			}
			
			isr.close();	
			return counter;

		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		
		return 0;
	}
	
	public static void ResetConfigFiles(Context context) {
		Log.v(TAG, "Reset config files.");
		context.deleteFile(FILE_ENCOUNTERQUEUE);
		//context.deleteFile(FILE_USERGRID);
	}

	public static Grid GetRanchGrid(Context context) {
		Log.v(TAG, "getRanchGrid");
		Grid grid = null;
		
		try {
			
			if (!context.getFileStreamPath(FILE_USERGRID).exists()) {
				Log.v(TAG, "DNE");
				// create new grid and save it to file
				grid = new Grid(true);
				SaveRanchGrid(context, grid);
			} else {
				Log.v(TAG, "Exists");
				// grid exists in file, load it.
				ArrayList<String> contents = new ArrayList<String>();
				
				// Read the file and gather the data
				FileInputStream fIn = context.openFileInput(FILE_USERGRID);
				InputStreamReader isr = new InputStreamReader(fIn);
				BufferedReader buffreader = new BufferedReader(isr);

				String readString;
				while ((readString = buffreader.readLine()) != null) {
					contents.add(readString);
					Log.v(TAG, readString);
				}
				
				isr.close();	
				
				// Create grid matarix from the data
				char[][] matrix = new char[contents.get(0).length()][contents.size()];
				
				for (int line = 0; line<contents.size(); line++)
					for (int schar = 0; schar<contents.get(0).length(); schar++)
						matrix[line][schar] = contents.get(line).charAt(schar);
				
				// Load data into new grid
				grid = new Grid(false);
				grid.set(matrix);				
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		return grid;
	}
	
	public static void SaveRanchGrid(Context context, Grid grid) {
		Log.v(TAG, "SaveRanchGrid");
		try {
			
			// Write the new encounter to the file
			FileOutputStream fos = context.openFileOutput(FILE_USERGRID, Context.MODE_APPEND);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			
			char[][] matrix = grid.getGrid();

			for (char[] row : matrix) {
				String line = "";
				for (char item : row) {
					line += item;
				}
				osw.write(line + "\n");
			}
			
			osw.flush();
			osw.close();
			
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
