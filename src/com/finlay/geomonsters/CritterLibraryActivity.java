package com.finlay.geomonsters;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class CritterLibraryActivity extends Activity {
	
	private static final String TAG = "CritterLibraryActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreated");
		super.onCreate(savedInstanceState);

		// get rid of title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// layout
		setContentView(R.layout.activity_main);
	}

}
