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


package com.finlay.geomonsters.battle;

import java.util.ArrayList;

import com.finlay.geomonsters.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


public class ChooseCreatureDialog extends DialogFragment {

	private static final String TAG = "ChooseCreatureDialog"; 
	
	String _currentCreature;

	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks.
	 */
	public interface ChooseCreatureDialogListener {
		public void onCreatureChosen(String name);
	}

	ChooseCreatureDialogListener mListener;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// verify host implements callback interface
		try {
			mListener = (ChooseCreatureDialogListener) activity;
		} catch (ClassCastException e) {
			Log.e(TAG, activity.toString() + " must implement NoticeDialogListener");
		}
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Get the first six creatures of user
		ArrayList<Creature> creatures = ResourceManager.getUserCreatures(getResources(), 6);

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		// View
		View contentView = inflater.inflate(R.layout.change_geomonster, null);
		ViewGroup view = (ViewGroup) contentView.findViewById(R.id.ChangeGeomonsterLayout);
		
		// Add a button for every creature (max of 6)
		Button button;
		for (Creature creature : creatures) {
			button = new Button(contentView.getContext());
			button.setText(creature.getNickName());
			
			// Can't reselect same creature
			if (creature.getNickName().equals(_currentCreature)) {
				//TODO: Make current more visible
				button.setTextColor(Color.WHITE);
			}
			
			//TODO: Use better button styles. Change bg colour depending on hp
			button.setBackgroundColor(Color.GREEN);
			button.setOnClickListener(new MyButtonClickListener());
			view.addView(button);
		}
		
		// Set layout for dialog
		builder.setView(contentView)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		return builder.create();
	}
	
	public void init(String currentCreatureNickName) {
		_currentCreature = currentCreatureNickName;
	}
	

	class MyButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			Button button = (Button) arg0;
			
			if (button.getText().equals(_currentCreature))
				dismiss();
			else {
				mListener.onCreatureChosen((String) button.getText());
				dismiss();
			}
		}
	}

}


