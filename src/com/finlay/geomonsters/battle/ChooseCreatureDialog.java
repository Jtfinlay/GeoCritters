package com.finlay.geomonsters.battle;

import com.finlay.geomonsters.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;


public class ChooseCreatureDialog extends DialogFragment {
	
	private static final String TAG = "ChooseCreatureDialog";
	
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
		
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		// Inflate and set the layout for this dialog
		builder.setView(inflater.inflate(R.layout.change_geomonster, null))
		// Add action buttons
			.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.v(TAG, "Choose");
					mListener.onCreatureChosen(ChooseCreatureDialog.this);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.v(TAG, "Cancel");
				}
			});
		return builder.create();
	}
}
