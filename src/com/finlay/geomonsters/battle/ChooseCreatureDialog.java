package com.finlay.geomonsters.battle;

import java.util.ArrayList;

import com.finlay.geomonsters.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ChooseCreatureDialog extends DialogFragment {

	private static final String TAG = "ChooseCreatureDialog"; 
	
	String _currentCreature;

	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks.
	 */
	public interface ChooseCreatureDialogListener {
		public void onCreatureChosen(DialogFragment dialog);
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
		
		// Add a button for every creature
		Button button;
		for (Creature creature : creatures) {
			button = new Button(contentView.getContext());
			button.setText(creature.getNickName());
			
			// Can't reselect same creature
			if (creature.getNickName().equals(_currentCreature))
				button.setClickable(false);
			
			//TODO: Use better button styles
			button.setBackgroundColor(ResourceManager.getColorOfType(getResources(), creature.getType()));

			view.addView(button);
		}
		
		// Set layout for dialog
		builder.setView(contentView)
		// Add action buttons
		.setPositiveButton("Choose", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onCreatureChosen(ChooseCreatureDialog.this);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		final Dialog res = builder.create();
		
		return res;
	}
	
	public void init(String currentCreatureNickName) {
		_currentCreature = currentCreatureNickName;
	}
}
